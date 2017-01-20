package com.popdq.app.util;

import android.content.Context;
import android.text.format.DateFormat;

import com.popdq.app.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 24/07/2015.
 */
public class DateUtil extends android.text.format.DateUtils {
    //    public static long DELAY_TIME = 12 * 60 * 60 * 1000;
    public static long DELAY_TIME = 0;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    private static boolean isWithin(final long millis, final long span, final TimeUnit unit) {
        return System.currentTimeMillis() - millis <= unit.toMillis(span);

    }

    private static int convertDelta(final long millis, TimeUnit to) {
        return (int) to.convert(System.currentTimeMillis() - millis, TimeUnit.MILLISECONDS);
    }

    private static String getFormattedDateTime(long time, String template, Locale locale) {
        String localizedPattern = new SimpleDateFormat(template, locale).toLocalizedPattern();
        return new SimpleDateFormat(localizedPattern, locale).format(new Date(time));
    }

    public static String getBriefRelativeTimeSpanString(final Context c, final Locale locale, final long timestamp) {
        if (isWithin(timestamp, 1, TimeUnit.MINUTES)) {
            return c.getString(R.string.just_now);
        } else if (isWithin(timestamp, 1, TimeUnit.HOURS)) {
            int mins = convertDelta(timestamp, TimeUnit.MINUTES);
            return mins + " " + c.getString(R.string.minute_ago);
        } else if (isWithin(timestamp, 1, TimeUnit.DAYS)) {
            int hours = convertDelta(timestamp, TimeUnit.HOURS);
            return c.getResources().getQuantityString(R.plurals.hours_ago, hours, hours);
        } else if (isWithin(timestamp, 6, TimeUnit.DAYS)) {
            return getFormattedDateTime(timestamp, "EEE", locale);
        } else if (isWithin(timestamp, 365, TimeUnit.DAYS)) {
            return getFormattedDateTime(timestamp, "MMM d", locale);
        } else {
            return getFormattedDateTime(timestamp, "MMM d, yyyy", locale);
        }
    }

    public static String getExtendedRelativeTimeSpanString(final Context c, final Locale locale, final long timestamp) {
        if (isWithin(timestamp, 1, TimeUnit.MINUTES)) {
            return c.getString(R.string.just_now);
        } else if (isWithin(timestamp, 1, TimeUnit.HOURS)) {
            int mins = (int) TimeUnit.MINUTES.convert(System.currentTimeMillis() - timestamp, TimeUnit.MILLISECONDS);
            return mins + " " + c.getString(R.string.minute_ago);
        } else {
            StringBuilder format = new StringBuilder();
            if (isWithin(timestamp, 6, TimeUnit.DAYS)) format.append("EEE ");
            else if (isWithin(timestamp, 365, TimeUnit.DAYS)) format.append("MMM d, ");
            else format.append("MMM d, yyyy, ");

            if (DateFormat.is24HourFormat(c)) format.append("HH:mm");
            else format.append("hh:mm a");

            return getFormattedDateTime(timestamp, format.toString(), locale);
        }
    }

    public static SimpleDateFormat getDetailedDateFormatter(Context context, Locale locale) {
        String dateFormatPattern;

        if (DateFormat.is24HourFormat(context)) {
            dateFormatPattern = "MMM d, yyyy HH:mm:ss zzz";
        } else {
            dateFormatPattern = "MMM d, yyyy hh:mm:ss a zzz";
        }

        return new SimpleDateFormat(dateFormatPattern, locale);
    }

    public static String parserDate(Context context, Locale locale, String dateString) {
        //"2016-07-31 22:03:09"
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        String newDate = "";
        try {
            date = format.parse(dateString);
            newDate = getBriefRelativeTimeSpanString(context, locale, date.getTime() + DELAY_TIME);

            String fg = format.format(date);
            int i = 0;
            i++;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;

    }

    public static long getDateLong(String dateApi) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        String newDate = "";
        try {
            date = format.parse(dateApi);
        } catch (Exception e) {

        }
        if (date != null) {
            return date.getTime();
        } else return -1;
    }

    public static String convertLeftToString(long leftTime) {
        if (leftTime <= 0) {
            return "00:00";
        }
        int minute = (int) leftTime / 1000 / 60;
        int day = minute / 60 / 24;
        if (day < 1) {
            int hours = minute / 60;
            int min = minute % 60;
            return hours + " hours " + min + " mins";
        } else {
            int hours = (minute / 60) % 24;
            return day + " day " + hours + " hours";
        }
    }

    public static String getLeftTime(Context context, Locale locale, long time) {
//        long left = getDateLong(dateCreate) + 48 * 60 * 60 * 1000 - (System.currentTimeMillis() - DELAY_TIME);
        long left = time + 48 * 60 * 60 * 1000 - (System.currentTimeMillis() - DELAY_TIME);


        if (left < 0) {
            left = 0;
        }
        return convertLeftToString(left);
    }
}
