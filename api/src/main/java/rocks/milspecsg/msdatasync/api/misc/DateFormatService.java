package rocks.milspecsg.msdatasync.api.misc;

import java.text.ParseException;
import java.util.Date;

public interface DateFormatService {

    String format(Date date);

    String formatDiff(Date date);

    Date parse(String string) throws ParseException;
}
