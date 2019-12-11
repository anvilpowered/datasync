/*
 *     MSDataSync - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.msdatasync.service.common.member.repository;

import com.google.inject.Inject;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.RecordIterable;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.filters.ObjectFilters;
import rocks.milspecsg.msdatasync.api.member.repository.NitriteMemberRepository;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.datastore.nitrite.NitriteConfig;
import rocks.milspecsg.msrepository.service.common.repository.CommonNitriteRepository;

import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Singleton // for synchronization purposes
public class CommonNitriteMemberRepository<
    TMember extends Member<NitriteId>,
    TSnapshot extends Snapshot<NitriteId>,
    TUser,
    TDataKey>
    extends CommonMemberRepository<NitriteId, TMember, TSnapshot, TUser, TDataKey, Nitrite, NitriteConfig>
    implements CommonNitriteRepository<TMember, CacheService<NitriteId, TMember, Nitrite, NitriteConfig>>,
    NitriteMemberRepository<TMember, TSnapshot, TUser> {

    @Inject
    public CommonNitriteMemberRepository(DataStoreContext<NitriteId, Nitrite, NitriteConfig> dataStoreContext) {
        super(dataStoreContext);
    }

    private synchronized boolean removeSnapshotId(ObjectFilter filter, NitriteId snapshotId) {
        List<NitriteId> ids = getSnapshotIds(filter).join();
        ids.remove(snapshotId);
        TMember member = generateEmpty();
        member.setSnapshotIds(ids);
        return getDataStoreContext().getDataStore().filter(n -> n.getRepository(getTClass()).update(filter, member).getAffectedCount() > 0).isPresent();
    }

    private synchronized boolean addSnapshotId(ObjectFilter filter, NitriteId snapshotId) {
        List<NitriteId> ids = getSnapshotIds(filter).join();
        ids.add(snapshotId);
        TMember member = generateEmpty();
        member.setSnapshotIds(ids);
        return getDataStoreContext().getDataStore().filter(n -> n.getRepository(getTClass()).update(filter, member).getAffectedCount() > 0).isPresent();
    }

    private Predicate<NitriteId> filterMatchingDate(Date date) {
        return id -> date != null && date.equals(snapshotRepository.getCreatedUtcDate(id).join().orElse(null));
    }

    @Override
    public CompletableFuture<Optional<TMember>> getOneForUser(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> asCursor(userUUID).map(RecordIterable::firstOrDefault));
    }

    @Override
    public ObjectFilter asFilter(UUID userUUID) {
        return ObjectFilters.eq("userUUID", userUUID);
    }

    @Override
    public Optional<Cursor<TMember>> asCursor(UUID userUUID) {
        return asCursor(asFilter(userUUID));
    }

    @Override
    public CompletableFuture<List<NitriteId>> getSnapshotIds(ObjectFilter filter) {
        return CompletableFuture.supplyAsync(() -> asCursor(filter).map(c -> {
            TMember member = c.firstOrDefault();
            if (member == null) {
                return Collections.<NitriteId>emptyList();
            }
            return member.getSnapshotIds();
        }).orElse(Collections.emptyList()));
    }

    @Override
    public CompletableFuture<List<NitriteId>> getSnapshotIds(NitriteId id) {
        return getSnapshotIds(asFilter(id));
    }

    @Override
    public CompletableFuture<List<NitriteId>> getSnapshotIdsForUser(UUID userUUID) {
        return getSnapshotIds(asFilter(userUUID));
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDates(ObjectFilter filter) {
        return getSnapshotIds(filter).thenApplyAsync(ids -> ids.stream()
            .map(id -> snapshotRepository.getCreatedUtcDate(id).join().orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDates(NitriteId id) {
        return getSnapshotDates(asFilter(id));
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDatesForUser(UUID userUUID) {
        return getSnapshotDates(asFilter(userUUID));
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(ObjectFilter filter, NitriteId snapshotId) {
        return CompletableFuture.supplyAsync(() -> removeSnapshotId(filter, snapshotId) && snapshotRepository.deleteOne(snapshotId).join());
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(ObjectFilter filter, Date date) {
        return getSnapshotIds(filter)
            .thenApplyAsync(ids -> ids.stream()
                .filter(filterMatchingDate(date))
                .findFirst()
                .map(id -> removeSnapshotId(filter, id) && snapshotRepository.deleteOne(id).join())
                .orElse(false)
            );
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(NitriteId id, NitriteId snapshotId) {
        return deleteSnapshot(asFilter(id), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(NitriteId id, Date date) {
        return deleteSnapshot(asFilter(id), date);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, NitriteId snapshotId) {
        return deleteSnapshot(asFilter(userUUID), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, Date date) {
        return deleteSnapshot(asFilter(userUUID), date);
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(ObjectFilter filter, NitriteId snapshotId) {
        return CompletableFuture.supplyAsync(() -> addSnapshotId(filter, snapshotId));
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(NitriteId id, NitriteId snapshotId) {
        return CompletableFuture.supplyAsync(() -> addSnapshotId(asFilter(id), snapshotId));
    }

    @Override
    public CompletableFuture<Boolean> addSnapshotForUser(UUID userUUID, NitriteId snapshotId) {
        return getOneOrGenerateForUser(userUUID).thenApplyAsync(optionalMember -> optionalMember
            .filter(tMember -> addSnapshotId(asFilter(tMember.getId()), snapshotId)).isPresent());
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getSnapshot(ObjectFilter filter, Date date) {
        return getSnapshotIds(filter)
            .thenApplyAsync(ids -> ids.stream()
                .filter(filterMatchingDate(date))
                .findFirst()
                .flatMap(id -> snapshotRepository.getOne(id).join())
            );
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getSnapshot(NitriteId id, Date date) {
        return getSnapshot(asFilter(id), date);
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getSnapshotForUser(UUID userUUID, Date date) {
        return getSnapshot(asFilter(userUUID), date);
    }

    @Override
    public CompletableFuture<List<NitriteId>> getClosestSnapshots(ObjectFilter filter, Date date) {
        return getSnapshotIds(filter).thenApplyAsync(ids -> {
            Optional<NitriteId> closestBefore = Optional.empty();
            Optional<Date> closestBeforeDate = Optional.empty();
            Optional<NitriteId> closestAfter = Optional.empty();
            Optional<Date> closestAfterDate = Optional.empty();
            Optional<NitriteId> same = Optional.empty();

            for (NitriteId nitriteId : ids) {
                Optional<Date> optionalDate = snapshotRepository.getCreatedUtcDate(nitriteId).join();
                if (!optionalDate.isPresent()) {
                    continue;
                }
                Date toTest = optionalDate.get();

                if (toTest.equals(date)) {
                    same = Optional.of(nitriteId);
                } else if (toTest.before(date) && (!closestBeforeDate.isPresent() || toTest.after(closestBeforeDate.get()))) {
                    closestBefore = Optional.of(nitriteId);
                    closestBeforeDate = Optional.of(toTest);
                } else if (toTest.after(date) && (!closestAfterDate.isPresent() || toTest.before(closestAfterDate.get()))) {
                    closestAfter = Optional.of(nitriteId);
                    closestAfterDate = Optional.of(toTest);
                }
            }

            List<NitriteId> toReturn = new ArrayList<>();
            closestBefore.ifPresent(toReturn::add);
            same.ifPresent(toReturn::add);
            closestAfter.ifPresent(toReturn::add);
            return toReturn;
        });
    }

    @Override
    public CompletableFuture<List<NitriteId>> getClosestSnapshots(NitriteId id, Date date) {
        return getClosestSnapshots(asFilter(id), date);
    }

    @Override
    public CompletableFuture<List<NitriteId>> getClosestSnapshotsForUser(UUID userUUID, Date date) {
        return getClosestSnapshots(asFilter(userUUID), date);
    }
}
