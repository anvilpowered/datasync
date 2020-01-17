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

package rocks.milspecsg.msdatasync.common.model.snapshot;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.util.ByteArraySizedInputStream;
import rocks.milspecsg.msdatasync.api.model.snapshot.*;
import rocks.milspecsg.msdatasync.api.model.serializeditemstack.SerializedItemStack;
import rocks.milspecsg.msrepository.api.datastore.annotation.XodusEntity;
import rocks.milspecsg.msrepository.api.model.Mappable;
import rocks.milspecsg.msrepository.common.model.XodusDbo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@XodusEntity
public class XodusSnapshot extends XodusDbo implements MappableSnapshot<EntityId, Entity> {

    private String name;

    private String server;

    private List<String> modulesUsed;

    private List<String> modulesFailed;

    private Map<String, Object> keys;

    private List<SerializedItemStack> itemStacks;

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
        this.modulesUsed = Objects.requireNonNull(modulesUsed, "modulesUsed cannot be null");
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
        this.modulesFailed = Objects.requireNonNull(modulesFailed, "modulesFailed cannot be null");
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
        this.keys = Objects.requireNonNull(keys, "keys cannot be null");
    }

    @Override
    public List<SerializedItemStack> getItemStacks() {
        if (itemStacks == null) {
            itemStacks = new ArrayList<>();
        }
        return itemStacks;
    }

    @Override
    public void setItemStacks(List<SerializedItemStack> itemStacks) {
        this.itemStacks = Objects.requireNonNull(itemStacks, "itemStacks cannot be null");
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
            object.setBlob("modulesUsed", new ByteArraySizedInputStream(Mappable.serializeUnsafe(getModulesUsed())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            object.setBlob("modulesFailed", new ByteArraySizedInputStream(Mappable.serializeUnsafe(getModulesFailed())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            object.setBlob("keys", new ByteArraySizedInputStream(Mappable.serializeUnsafe(getKeys())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            object.setBlob("itemStacks", new ByteArraySizedInputStream(Mappable.serializeUnsafe(getItemStacks())));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        Mappable.<List<String>>deserialize(object.getBlob("modulesUsed")).ifPresent(t -> modulesUsed = t);
        Mappable.<List<String>>deserialize(object.getBlob("modulesFailed")).ifPresent(t -> modulesFailed = t);
        Mappable.<Map<String, Object>>deserialize(object.getBlob("keys")).ifPresent(t -> keys = t);
        Mappable.<List<SerializedItemStack>>deserialize(object.getBlob("itemStacks")).ifPresent(t -> itemStacks = t);
    }
}
