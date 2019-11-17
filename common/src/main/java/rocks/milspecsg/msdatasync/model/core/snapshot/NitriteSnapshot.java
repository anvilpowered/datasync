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

package rocks.milspecsg.msdatasync.model.core.snapshot;

import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.mapper.NitriteMapper;
import rocks.milspecsg.msdatasync.model.core.serializeditemstack.SerializedItemStack;
import rocks.milspecsg.msrepository.datastore.nitrite.annotation.NitriteEntity;
import rocks.milspecsg.msrepository.model.data.dbo.NitriteDbo;

import java.util.*;

@NitriteEntity
public class NitriteSnapshot extends NitriteDbo implements Snapshot<NitriteId> {

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
    public Document write(NitriteMapper mapper) {
        Document document = super.write(mapper);
        document.put("name", name);
        document.put("server", server);
        document.put("modulesUsed", getModulesUsed());
        document.put("modulesFailed", getModulesFailed());
        document.put("keys", getKeys());
        document.put("itemStacks", getItemStacks());
        return document;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(NitriteMapper mapper, Document document) {
        super.read(mapper, document);
        name = (String) document.get("name");
        server = (String) document.get("server");
        modulesUsed = (List<String>) document.get("modulesUsed");
        modulesFailed = (List<String>) document.get("modulesFailed");
        keys = (Map<String, Object>) document.get("keys");
        itemStacks = (List<SerializedItemStack>) document.get("itemStacks");
    }
}
