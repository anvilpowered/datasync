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

package rocks.milspecsg.msdatasync.api.member;

import rocks.milspecsg.msdatasync.api.member.repository.MemberRepository;
import rocks.milspecsg.msdatasync.api.model.member.*;
import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.manager.Manager;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MemberManager<
    TMember extends Member<?>,
    TSnapshot extends Snapshot<?>,
    TUser,
    TString>
    extends Manager<MemberRepository<?, TMember, TSnapshot, TUser, ?>> {

    @Override
    default String getDefaultIdentifierSingularUpper() {
        return "Member";
    }

    @Override
    default String getDefaultIdentifierPluralUpper() {
        return "Members";
    }

    @Override
    default String getDefaultIdentifierSingularLower() {
        return "member";
    }

    @Override
    default String getDefaultIdentifierPluralLower() {
        return "members";
    }

    CompletableFuture<TString> deleteSnapshot(UUID userUUID, Optional<String> optionalString);

    CompletableFuture<TString> info(UUID userUUID, TSnapshot snapshot);

    CompletableFuture<TString> info(UUID userUUID, Optional<String> optionalString);

    CompletableFuture<Iterable<TString>> list(UUID userUUID);
}
