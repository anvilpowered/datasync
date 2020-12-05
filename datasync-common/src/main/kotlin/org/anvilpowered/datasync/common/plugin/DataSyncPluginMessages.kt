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

package org.anvilpowered.datasync.common.plugin

import com.google.inject.Inject
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.datasync.api.plugin.PluginMessages

class DataSyncPluginMessages<TString, TCommandSource> : PluginMessages<TString> {

    @Inject
    private lateinit var textService: TextService<TString, TCommandSource>

    private val noPermissionText: TString by lazy {
        textService.builder()
            .appendPrefix()
            .red().append("You do not have permission for this command!")
            .build()
    }

    private val userRequiredText: TString by lazy {
        textService.builder()
            .appendPrefix()
            .yellow().append("User is required!")
            .build()
    }

    private val invalidUserText: TString by lazy {
        textService.builder()
            .appendPrefix()
            .yellow().append("Invalid User!")
            .build()
    }

    override fun getNoPermissions(): TString = noPermissionText
    override fun getUserRequired(): TString = userRequiredText
    override fun getInvalidUser(): TString = invalidUserText
}
