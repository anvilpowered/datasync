package rocks.milspecsg.msdatasync.service.misc;

import rocks.milspecsg.msdatasync.api.misc.SyncUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApiSyncUtils implements SyncUtils {

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
