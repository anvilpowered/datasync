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

package rocks.milspecsg.msdatasync.api.config;


public interface ConfigKeys extends rocks.milspecsg.msrepository.api.config.ConfigKeys {

    int SERIALIZE_ENABLED_SERIALIZERS_LIST = 1;
    int SERIALIZE_ON_JOIN_LEAVE = 10;
    int SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN = 11;

    int SNAPSHOT_MIN_COUNT = 30;
    int SNAPSHOT_OPTIMIZATION_STRATEGY = 31;
    int SNAPSHOT_UPLOAD_INTERVAL = 32;
    int SERVER_NAME = 49;



    int MONGODB_HOSTNAME = 100;
    int MONGODB_PORT = 101;
    int MONGODB_DBNAME = 102;
    int MONGODB_USERNAME = 103;
    int MONGODB_PASSWORD = 104;
    int MONGODB_USE_AUTH = 105;

    int NITRITE_USERNAME = 110;
    int NITRITE_PASSWORD = 111;
    int NITRITE_USE_AUTH = 112;
    int NITRITE_USE_COMPRESSION = 113;

}
