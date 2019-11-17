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

package rocks.milspecsg.msdatasync.api.member.repository;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectFilter;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.datastore.nitrite.NitriteConfig;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface NitriteMemberRepository<
    TMember extends Member<NitriteId>,
    TSnapshot extends Snapshot<NitriteId>,
    TUser>
    extends MemberRepository<NitriteId, TMember, TSnapshot, TUser, Nitrite, NitriteConfig> {

    CompletableFuture<List<NitriteId>> getSnapshotIds(ObjectFilter filter);

    CompletableFuture<List<Date>> getSnapshotDates(ObjectFilter filter);

    CompletableFuture<Boolean> deleteSnapshot(ObjectFilter filter, NitriteId snapshotId);

    CompletableFuture<Boolean> deleteSnapshot(ObjectFilter filter, Date date);

    CompletableFuture<Boolean> addSnapshot(ObjectFilter filter, NitriteId snapshotId);

    CompletableFuture<Optional<TSnapshot>> getSnapshot(ObjectFilter filter, Date date);

    CompletableFuture<List<NitriteId>> getClosestSnapshots(ObjectFilter filter, Date date);

    ObjectFilter asFilter(UUID userUUID);

    Optional<Cursor<TMember>> asCursor(UUID userUUID);

}
