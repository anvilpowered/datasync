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

package rocks.milspecsg.msdatasync.service.sponge.member;

import com.google.inject.Inject;
import org.bson.types.ObjectId;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.service.common.member.repository.CommonMongoMemberRepository;
import rocks.milspecsg.msrepository.datastore.mongodb.MongoContext;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpongeMongoMemberRepository<
    TMember extends Member<ObjectId>,
    TSnapshot extends Snapshot<ObjectId>> extends CommonMongoMemberRepository<TMember, TSnapshot, User, Key<?>> {

    @Inject
    public SpongeMongoMemberRepository(MongoContext mongoContext) {
        super(mongoContext);
    }

    @Override
    public Optional<User> getUser(UUID userUUID) {
        return Sponge.getServiceManager().provide(UserStorageService.class).flatMap(u -> u.get(userUUID));
    }

    @Override
    public Optional<User> getUser(String lastKnownName) {
        return Sponge.getServiceManager().provide(UserStorageService.class).flatMap(u -> u.get(lastKnownName));
    }

    @Override
    public CompletableFuture<Optional<User>> getUser(ObjectId id) {
        return CompletableFuture.supplyAsync(() -> getUUID(id).join().flatMap(this::getUser));
    }

}
