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

package rocks.milspecsg.msdatasync;

public abstract class PluginPermissions {
    public static final String LOCK_COMMAND = "msdatasync.lock";
    public static final String RELOAD_COMMAND = "msdatasync.reload";
    public static final String SNAPSHOT_BASE = "msdatasync.snapshot.base";
    public static final String SNAPSHOT_CREATE = "msdatasync.snapshot.create";
    public static final String SNAPSHOT_DELETE = "msdatasync.snapshot.delete";
    public static final String SNAPSHOT_RESTORE = "msdatasync.snapshot.restore";
    public static final String SNAPSHOT_VIEW_EDIT = "msdatasync.snapshot.view.edit";
    public static final String SNAPSHOT_VIEW_BASE = "msdatasync.snapshot.view.base";
    public static final String MANUAL_OPTIMIZATION_ALL = "msdatasync.optimize.all";
    public static final String MANUAL_OPTIMIZATION_BASE = "msdatasync.optimize.base";
}
