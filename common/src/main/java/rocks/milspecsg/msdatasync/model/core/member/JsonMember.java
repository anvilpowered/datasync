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

package rocks.milspecsg.msdatasync.model.core.member;

import io.jsondb.annotation.Document;
import rocks.milspecsg.msrepository.model.data.dbo.JsonDbo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Document(collection = "members", schemaVersion = "1.0")
public class JsonMember extends JsonDbo implements Member<UUID> {

    private UUID userUUID;

    private List<UUID> snapshotIds;

    @Override
    public UUID getUserUUID() {
        return userUUID;
    }

    @Override
    public void setUserUUID(UUID userUUID) {
        this.userUUID = userUUID;
    }

    @Override
    public List<UUID> getSnapshotIds() {
        if (snapshotIds == null) {
            snapshotIds = new ArrayList<>();
        }
        return snapshotIds;    }

    @Override
    public void setSnapshotIds(List<UUID> snapshotIds) {
        this.snapshotIds = Objects.requireNonNull(snapshotIds, "snapshotIds cannot be null");
    }
}
