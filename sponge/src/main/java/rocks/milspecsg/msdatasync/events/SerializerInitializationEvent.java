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

package rocks.milspecsg.msdatasync.events;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import rocks.milspecsg.msdatasync.api.serializer.SnapshotSerializer;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotManager;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;

public class SerializerInitializationEvent<S extends Snapshot<?>> extends AbstractEvent {

    private final Cause cause;
    private final SnapshotSerializer<S, User> snapshotSerializer;
    private final SnapshotManager<S, Key<?>> snapshotRepository;

    public SerializerInitializationEvent(
        SnapshotSerializer<S, User> snapshotSerializer,
        SnapshotManager<S, Key<?>> snapshotRepository,
        Cause cause) {
        this.cause = cause;
        this.snapshotSerializer = snapshotSerializer;
        this.snapshotRepository = snapshotRepository;
    }

    @Override
    public Cause getCause() {
        return cause;
    }

    public SnapshotSerializer<S, User> getSnapshotSerializer() {
        return snapshotSerializer;
    }

    public SnapshotManager<S, Key<?>> getSnapshotRepository() {
        return snapshotRepository;
    }
}
