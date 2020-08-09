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

package org.anvilpowered.datasync.sponge.command.snapshot;

import com.google.inject.Inject;
import org.anvilpowered.datasync.api.member.MemberManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class SpongeSnapshotListCommand implements CommandExecutor {

    @Inject
    private MemberManager<Text> memberManager;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {
        Optional<User> optionalUser = context.getOne(Text.of("user"));
        if (!optionalUser.isPresent()) {
            throw new CommandException(Text.of("User is required"));
        }
        memberManager.list(optionalUser.get().getUniqueId()).thenAcceptAsync(list -> {
            Optional<PaginationService> paginationService = Sponge.getServiceManager().provide(PaginationService.class);
            if (!paginationService.isPresent()) return;
            PaginationList.Builder paginationBuilder =
                paginationService.get()
                    .builder()
                    .title(Text.of(TextColors.GOLD, "Snapshots - ", optionalUser.get().getName()))
                    .padding(Text.of(TextColors.DARK_GREEN, "-"))
                    .contents(list).linesPerPage(20);
            paginationBuilder.build().sendTo(source);
        });
        return CommandResult.success();
    }
}
