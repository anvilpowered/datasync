/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.datasync.common.model.member;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.util.ByteArraySizedInputStream;
import org.anvilpowered.anvil.api.datastore.XodusEntity;
import org.anvilpowered.anvil.api.model.Mappable;
import org.anvilpowered.anvil.base.model.XodusDbo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@XodusEntity
public class XodusMember extends XodusDbo implements org.anvilpowered.datasync.api.model.member.Member<EntityId>, Mappable<Entity> {

    private String userUUID;

    private List<EntityId> snapshotIds;

    @Override
    public UUID getUserUUID() {
        return UUID.fromString(userUUID);
    }

    @Override
    public void setUserUUID(UUID userUUID) {
        this.userUUID = userUUID.toString();
        prePersist();
    }

    @Override
    public List<EntityId> getSnapshotIds() {
        if (snapshotIds == null) {
            snapshotIds = new ArrayList<>();
        }
        return snapshotIds;
    }

    @Override
    public void setSnapshotIds(List<EntityId> snapshotIds) {
        this.snapshotIds = Objects.requireNonNull(snapshotIds, "snapshotIds cannot be null");
        prePersist();
    }

    @Override
    public Entity writeTo(Entity object) {
        super.writeTo(object);
        if (userUUID != null) {
            object.setProperty("userUUID", userUUID);
        }
        try {
            object.setBlob("snapshotIds", new ByteArraySizedInputStream(Mappable.serializeUnsafe(getSnapshotIds())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public void readFrom(Entity object) {
        super.readFrom(object);
        Comparable<?> userUUID = object.getProperty("userUUID");
        if (userUUID instanceof String) {
            this.userUUID = (String) userUUID;
        }
        Mappable.<List<EntityId>>deserialize(object.getBlob("snapshotIds")).ifPresent(t -> snapshotIds = t);
    }
}
