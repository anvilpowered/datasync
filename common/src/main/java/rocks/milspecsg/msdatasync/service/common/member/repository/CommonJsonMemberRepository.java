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
import com.google.inject.Singleton;
import io.jsondb.JsonDBOperations;
import io.jsondb.query.Update;
import rocks.milspecsg.msdatasync.api.member.repository.JsonMemberRepository;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.datastore.json.JsonConfig;
import rocks.milspecsg.msrepository.service.common.repository.CommonJsonRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Singleton
public class CommonJsonMemberRepository<
    TMember extends Member<UUID>,
    TSnapshot extends Snapshot<UUID>,
    TUser,
    TDataKey>
    extends CommonMemberRepository<UUID, TMember, TSnapshot, TUser, TDataKey, JsonDBOperations, JsonConfig>
    implements CommonJsonRepository<TMember, CacheService<UUID, TMember, JsonDBOperations, JsonConfig>>,
    JsonMemberRepository<TMember, TSnapshot, TUser> {

    @Inject
    public CommonJsonMemberRepository(DataStoreContext<UUID, JsonDBOperations, JsonConfig> dataStoreContext) {
        super(dataStoreContext);
    }

    private final Object snapshotIdLock = new Object();

    private List<UUID> accessSnapshotIds(String query) {
//        synchronized (snapshotIdLock) {
        return getDataStoreContext().getDataStore().flatMap(j ->
            Optional.ofNullable(j.findOne(query, getTClass())).map(Member::getSnapshotIds))
            .orElse(new ArrayList<>());
//        }
    }

    private boolean modifySnapshotIds(String query, Function<List<UUID>, List<UUID>> transformer) {
        try {
            List<UUID> ids = accessSnapshotIds(query);
            Update update = Update.update("snapshotIds", transformer.apply(ids));
            return getDataStoreContext().getDataStore().filter(j -> {
                try {
                    TMember modified = j.findAndModify(query, update, getTClass());
                    if (modified == null) {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }).isPresent();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean removeSnapshotId(String query, UUID snapshotId) {
        return modifySnapshotIds(query, list -> {
            list.remove(snapshotId);
            return list;
        });
    }

    private boolean addSnapshotId(String query, UUID snapshotId) {
        return modifySnapshotIds(query, list -> {
            list.add(snapshotId);
            return list;
        });
    }

    private Predicate<UUID> filterMatchingDate(Date date) {
        return id -> date != null && date.equals(snapshotRepository.getCreatedUtcDate(id).join().orElse(null));
    }

    @Override
    public CompletableFuture<Optional<TMember>> getOneForUser(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> getDataStoreContext().getDataStore().map(j -> j.findOne(asQueryForUser(userUUID), getTClass())));
    }

    @Override
    public CompletableFuture<List<UUID>> getSnapshotIds(String query) {
        return CompletableFuture.supplyAsync(() -> accessSnapshotIds(query));
    }

    @Override
    public CompletableFuture<List<UUID>> getSnapshotIds(UUID id) {
        return getSnapshotIds(asQuery(id));
    }

    @Override
    public CompletableFuture<List<UUID>> getSnapshotIdsForUser(UUID userUUID) {
        return getSnapshotIds(asQueryForUser(userUUID));
    }

    private Function<List<UUID>, List<Date>> convertIdsToDate = ids -> ids.stream()
        .map(snapshotId -> snapshotRepository.getCreatedUtcDate(snapshotId).join().orElse(null))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    @Override
    public CompletableFuture<List<Date>> getSnapshotDates(UUID id) {
        return getSnapshotIds(id).thenApplyAsync(convertIdsToDate);
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDatesForUser(UUID userUUID) {
        return getSnapshotIdsForUser(userUUID).thenApplyAsync(convertIdsToDate);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(String query, UUID snapshotId) {
        return CompletableFuture.supplyAsync(() -> removeSnapshotId(query, snapshotId) && snapshotRepository.deleteOne(snapshotId).join());
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(String query, Date date) {
        return getSnapshotIds(query)
            .thenApplyAsync(ids -> ids.stream()
                .filter(filterMatchingDate(date))
                .findFirst()
                .map(id -> removeSnapshotId(query, id) && snapshotRepository.deleteOne(id).join())
                .orElse(false)
            );
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(UUID id, UUID snapshotId) {
        return deleteSnapshot(asQuery(id), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(UUID id, Date date) {
        return deleteSnapshot(asQuery(id), date);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, UUID snapshotId) {
        return deleteSnapshot(asQueryForUser(userUUID), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, Date date) {
        return deleteSnapshot(asQueryForUser(userUUID), date);
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(String query, UUID snapshotId) {
        return CompletableFuture.supplyAsync(() -> addSnapshotId(query, snapshotId));
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(UUID id, UUID snapshotId) {
        return addSnapshot(asQuery(id), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> addSnapshotForUser(UUID userUUID, UUID snapshotId) {
        return getOneOrGenerateForUser(userUUID).thenApplyAsync(optionalMember -> optionalMember
            .filter(tMember -> addSnapshotId(asQuery(tMember.getId()), snapshotId)).isPresent());
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getSnapshot(String query, Date date) {
        return getSnapshotIds(query)
            .thenApplyAsync(ids -> ids.stream()
                .filter(filterMatchingDate(date))
                .findFirst()
                .flatMap(id -> snapshotRepository.getOne(id).join())
            );
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getSnapshot(UUID id, Date date) {
        return getSnapshot(asQuery(id), date);
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getSnapshotForUser(UUID userUUID, Date date) {
        return getSnapshot(asQueryForUser(userUUID), date);
    }

    @Override
    public CompletableFuture<List<UUID>> getClosestSnapshots(String query, Date date) {
        return getSnapshotIds(query).thenApplyAsync(ids -> {
            Optional<UUID> closestBefore = Optional.empty();
            Optional<Date> closestBeforeDate = Optional.empty();
            Optional<UUID> closestAfter = Optional.empty();
            Optional<Date> closestAfterDate = Optional.empty();
            Optional<UUID> same = Optional.empty();

            for (UUID uuid : ids) {
                Optional<Date> optionalDate = snapshotRepository.getCreatedUtcDate(uuid).join();
                if (!optionalDate.isPresent()) {
                    continue;
                }
                Date toTest = optionalDate.get();

                if (toTest.equals(date)) {
                    same = Optional.of(uuid);
                } else if (toTest.before(date) && (!closestBeforeDate.isPresent() || toTest.after(closestBeforeDate.get()))) {
                    closestBefore = Optional.of(uuid);
                    closestBeforeDate = Optional.of(toTest);
                } else if (toTest.after(date) && (!closestAfterDate.isPresent() || toTest.before(closestAfterDate.get()))) {
                    closestAfter = Optional.of(uuid);
                    closestAfterDate = Optional.of(toTest);
                }
            }

            List<UUID> toReturn = new ArrayList<>();
            closestBefore.ifPresent(toReturn::add);
            same.ifPresent(toReturn::add);
            closestAfter.ifPresent(toReturn::add);
            return toReturn;
        });
    }

    @Override
    public CompletableFuture<List<UUID>> getClosestSnapshots(UUID id, Date date) {
        return getClosestSnapshots(asQuery(id), date);
    }

    @Override
    public CompletableFuture<List<UUID>> getClosestSnapshotsForUser(UUID userUUID, Date date) {
        return getClosestSnapshots(asQueryForUser(userUUID), date);
    }

    @Override
    public String asQueryForUser(UUID userUUID) {
        return String.format("/.[userUUID='%s']", userUUID);
    }
}
