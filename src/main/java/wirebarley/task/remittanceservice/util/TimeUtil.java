package wirebarley.task.remittanceservice.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static String convertToZonedDateTime(LocalDateTime localDateTime) {
        if(localDateTime != null) {
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Seoul"));
            return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        return null;
    }
}
