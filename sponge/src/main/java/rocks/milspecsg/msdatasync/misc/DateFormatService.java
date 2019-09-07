package rocks.milspecsg.msdatasync.misc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatService {

    private DateFormat df = new SimpleDateFormat("yyyy-M-dd-HH:mm:ss");

    public String format(Date date) {
        return df.format(date);
    }

    public String formatDiff(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        StringBuilder s = new StringBuilder();
        int years = calendar.get(Calendar.YEAR) - 1970;
        int months = calendar.get(Calendar.MONTH);
        int days = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        int hours = calendar.get(Calendar.HOUR_OF_DAY) - 1;
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        if (years > 0) {
            s.append(years).append(years == 1 ? " year, " : " years, ");
        }
        if (months > 0) {
            s.append(months).append(months == 1 ? " month, " : " months, ");
        }
        if (days > 0) {
            s.append(days).append(days == 1 ? " day, " : " days, ");
        }
        if (hours > 0) {
            s.append(hours).append(hours == 1 ? " hour, " : " hours, ");
        }
        if (minutes > 0) {
            s.append(minutes).append(minutes == 1 ? " minute, " : " minutes, ");
        }
        if (seconds > 0) {
            s.append(seconds).append(seconds == 1 ? " second, " : " seconds");
        }
        if (s.substring(s.length() - 2, s.length()).equals(", ")) {
            s.deleteCharAt(s.length() - 1);
            s.deleteCharAt(s.length() - 1);
        }
        return s.toString();
    }

    public Date parse(String string) throws ParseException {
        return df.parse(string);
    }

}
