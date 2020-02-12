/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.datasync.common.misc;

import org.anvilpowered.datasync.api.misc.SyncUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommonSyncUtils implements SyncUtils {

    @Override
    public Optional<List<int[]>> decodeOptimizationStrategy(List<?> list) {
        List<int[]> numsList = new ArrayList<>();

        // check format
        if (list.stream().anyMatch(s -> {
                if (!(s instanceof String)) return true;
                String[] snums = ((String) s).split("[:]");
                if (snums.length != 2) return true;
                int[] nums = new int[2];
                try {
                    nums[0] = Integer.parseInt(snums[0]);
                    nums[1] = Integer.parseInt(snums[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return true;
                }
                if (nums[0] < 0 || nums[1] < 0) return true;
                numsList.add(nums);
                return false;
            }
        )) {
            return Optional.empty();
        }

        for (int i = 0; i < numsList.size() - 1; i++) {
            if (numsList.get(i + 1)[0] != numsList.get(i)[0] * numsList.get(i)[1]) return Optional.empty();
        }
        return Optional.of(numsList);
    }
}
