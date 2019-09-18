package rocks.milspecsg.msdatasync.api.misc;

import java.util.List;
import java.util.Optional;

public interface SyncUtils {

    Optional<List<int[]>> decodeOptimizationStrategy(List<?> list);
}
