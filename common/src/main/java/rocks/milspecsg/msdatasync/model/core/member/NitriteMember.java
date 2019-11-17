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

import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.mapper.NitriteMapper;
import rocks.milspecsg.msrepository.datastore.nitrite.annotation.NitriteEntity;
import rocks.milspecsg.msrepository.model.data.dbo.NitriteDbo;

import java.util.*;

@NitriteEntity
public class NitriteMember extends NitriteDbo implements Member<NitriteId> {

    private UUID userUUID;

    private List<NitriteId> snapshotIds;

    @Override
    public UUID getUserUUID() {
        return userUUID;
    }

    @Override
    public void setUserUUID(UUID userUUID) {
        this.userUUID = userUUID;
    }

    @Override
    public List<NitriteId> getSnapshotIds() {
        if (snapshotIds == null) {
            snapshotIds = new ArrayList<>();
        }
        return snapshotIds;
    }

    @Override
    public void setSnapshotIds(List<NitriteId> snapshotIds) {
        this.snapshotIds = Objects.requireNonNull(snapshotIds, "snapshotIds cannot be null");
    }

    @Override
    public Document write(NitriteMapper mapper) {
        Document document = super.write(mapper);
        document.put("userUUID", userUUID);
        document.put("snapshotIds", getSnapshotIds());
        return document;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(NitriteMapper mapper, Document document) {
        super.read(mapper, document);
        userUUID = (UUID) document.get("userUUID");
        snapshotIds = (List<NitriteId>) document.get("snapshotIds");
    }
}
