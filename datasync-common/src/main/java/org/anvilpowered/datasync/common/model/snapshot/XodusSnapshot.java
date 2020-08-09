/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.datasync.common.model.snapshot;

import com.google.common.base.Preconditions;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.util.ByteArraySizedInputStream;
import jetbrains.exodus.util.LightByteArrayOutputStream;
import org.anvilpowered.anvil.api.datastore.XodusEntity;
import org.anvilpowered.anvil.api.model.Mappable;
import org.anvilpowered.anvil.base.model.XodusDbo;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XodusEntity
public class XodusSnapshot extends XodusDbo implements Snapshot<EntityId>, Mappable<Entity> {

    private String name;

    private String server;

    private List<String> modulesUsed;

    private List<String> modulesFailed;

    private Map<String, Object> keys;

    private byte[] inventory;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getServer() {
        return server;
    }

    @Override
    public void setServer(String server) {
        this.server = server;
    }

    @Override
    public List<String> getModulesUsed() {
        if (modulesUsed == null) {
            modulesUsed = new ArrayList<>();
        }
        return modulesUsed;
    }

    @Override
    public void setModulesUsed(List<String> modulesUsed) {
        this.modulesUsed = Preconditions.checkNotNull(modulesUsed, "modulesUsed");
    }

    @Override
    public List<String> getModulesFailed() {
        if (modulesFailed == null) {
            modulesFailed = new ArrayList<>();
        }
        return modulesFailed;
    }

    @Override
    public void setModulesFailed(List<String> modulesFailed) {
        this.modulesFailed = Preconditions.checkNotNull(modulesFailed, "modulesFailed");
    }

    @Override
    public Map<String, Object> getKeys() {
        if (keys == null) {
            keys = new HashMap<>();
        }
        return keys;
    }

    @Override
    public void setKeys(Map<String, Object> keys) {
        this.keys = Preconditions.checkNotNull(keys, "keys");
    }

    @Override
    public byte[] getInventory() {
        return inventory;
    }

    @Override
    public void setInventory(byte[] inventory) {
        this.inventory = inventory;
    }

    @Override
    public Entity writeTo(Entity object) {
        super.writeTo(object);
        if (name != null) {
            object.setProperty("name", name);
        }
        if (server != null) {
            object.setProperty("server", server);
        }
        try {
            object.setBlob("modulesUsed",
                new ByteArraySizedInputStream(Mappable.serializeUnsafe(getModulesUsed())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            object.setBlob("modulesFailed",
                new ByteArraySizedInputStream(Mappable.serializeUnsafe(getModulesFailed())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            object.setBlob("keys",
                new ByteArraySizedInputStream(Mappable.serializeUnsafe(getKeys())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        object.setBlob("inventory",
            new ByteArraySizedInputStream(inventory));
        return object;
    }

    @Override
    public void readFrom(Entity object) {
        super.readFrom(object);
        Comparable<?> name = object.getProperty("name");
        if (name instanceof String) {
            this.name = (String) name;
        }
        Comparable<?> server = object.getProperty("server");
        if (name instanceof String) {
            this.server = (String) server;
        }
        Mappable.<List<String>>deserialize(object.getBlob("modulesUsed"))
            .ifPresent(t -> modulesUsed = t);
        Mappable.<List<String>>deserialize(object.getBlob("modulesFailed"))
            .ifPresent(t -> modulesFailed = t);
        Mappable.<Map<String, Object>>deserialize(object.getBlob("keys"))
            .ifPresent(t -> keys = t);
        try {
            InputStream inputStream = object.getBlob("inventory");
            if (inputStream != null) {
                LightByteArrayOutputStream outputStream =
                    new LightByteArrayOutputStream(inputStream.available());
                int next;
                while ((next = inputStream.read()) != -1) {
                    outputStream.write(next);
                }
                setInventory(outputStream.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
