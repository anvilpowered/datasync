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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jsondb.annotation.Document;
import rocks.milspecsg.msdatasync.model.core.serializeditemstack.JsonSerializedItemStack;
import rocks.milspecsg.msdatasync.model.core.serializeditemstack.SerializedItemStack;
import rocks.milspecsg.msrepository.model.data.dbo.JsonDbo;

import java.util.*;

@Document(collection = "snapshots", schemaVersion = "1.0")
public class JsonSnapshot extends JsonDbo implements Snapshot<UUID> {

    private String name;

    private String server;

    private List<String> modulesUsed;

    private List<String> modulesFailed;

    private Map<String, Object> keys;

    private List<JsonSerializedItemStack> jsonItemStacks;

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

    public List<JsonSerializedItemStack> getJsonItemStacks() {
        if (jsonItemStacks == null) {
            jsonItemStacks = new ArrayList<>();
        }
        return jsonItemStacks;
    }

    @Override
    @JsonIgnore
    @SuppressWarnings("unchecked")
    public List<SerializedItemStack> getItemStacks() {
        return (List<SerializedItemStack>)(List<?>) getJsonItemStacks();
    }

    public void setJsonItemStacks(List<JsonSerializedItemStack> jsonItemStacks) {
        this.jsonItemStacks = Objects.requireNonNull(jsonItemStacks, "itemStacks cannot be null");
    }

    @Override
    @JsonIgnore
    @SuppressWarnings("unchecked")
    public void setItemStacks(List<SerializedItemStack> itemStacks) {
        setJsonItemStacks((List<JsonSerializedItemStack>)(List<?>) itemStacks);
    }
}
