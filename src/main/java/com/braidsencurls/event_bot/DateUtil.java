package com.braidsencurls.event_bot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private DateUtil() {}

    public static final String DATE_TIME_24_HOUR = "yyyy-MM-dd HH:mm";

    public static String formatDateTime(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }

    public static LocalDateTime parseDateTime(String localDateTTimeStr, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(localDateTTimeStr, formatter);
    }
}
