package com.example.nomanahmed.portlr.Utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

    public static boolean timeCompare(Date time1, Date time2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time1);
        int sh = cal.get(Calendar.HOUR_OF_DAY);
        int sd = cal.get(Calendar.MINUTE);
        cal.setTime(time2);
        int eh = cal.get(Calendar.HOUR_OF_DAY);
        int ed = cal.get(Calendar.MINUTE);

        long interval = eh * 60 + ed - sh * 60 - sd;
        return interval < 0;
    }

    public static Date ServerToLocalDate(String date) {
        if(date.isEmpty()) {
            return new Date();
        }

        String newDate = date;
        while(newDate.contains(".")) {
            newDate = newDate.substring(0, newDate.length() - 1);
        }
        if(newDate.contains("Z")) {
            newDate = newDate.substring(0, newDate.length() - 1);
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return dateFormatter.parse(newDate);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String sumServerTime(String sum, Double time) {
        String timeStr = getLocalTimeFromServer(time);
        int[] op1 = separateTime(sum);
        int[] op2 = separateTime(timeStr);

        int hour = op1[0] + op2[0];
        int min = op1[1] + op2[1];
        String finalHStr = "";
        String finalMStr = "";
        if(min < 60) {
            finalHStr = String.valueOf(hour);
            finalMStr = String.valueOf(min);
        } else {
            finalHStr = String.valueOf(hour + 1);
            finalMStr = String.valueOf(min - 60);
        }

        if(finalHStr.length() == 1) {
            finalHStr = "0" + finalHStr;
        }

        if(finalMStr.length() == 1) {
            finalMStr = "0" + finalMStr;
        }

        return finalHStr + ":" + finalMStr;
    }

    private static int[] separateTime(String timeStr) {
        String[] list = timeStr.split(":");
        if(list.length != 2) {
            return new int[]{0,0};
        }
        return new int[]{Integer.parseInt(list[0]), Integer.parseInt(list[1])};
    }

    @SuppressLint("DefaultLocale")
    public static String getServerTimeForPost(String timeStr) {
        double time = convertLocalDotToServerDotTime(timeStr);
        return String.format("%.2f", time);
    }

    private static Double convertLocalDotToServerDotTime(String timeStr) {
        double time = 0.0;
        String[] list = timeStr.split("\\.");
        if(list.length != 2) {
            return 0.0;
        }

        double hours = Double.parseDouble(list[0]);
        double mins = Double.parseDouble(list[1]);

        time = hours + mins / 60;
        return time;
    }

    public static int getIntervalTime(String timeStr) {
        if(timeStr.contains(":")) {
            String[] list = timeStr.split(":");
            if(list.length != 2) {
                return -1;
            }

            int myNum1 = 0;
            int myNum2 = 0;

            try {
                myNum1 = Integer.parseInt(list[0]);
                myNum2 = Integer.parseInt(list[1]);
                return myNum1 * 3600 + myNum2 * 60;
            } catch(NumberFormatException nfe) {
                return 0;
            }
        } else {
            return -1;
        }
    }

    public static String changeDateFormat(Date strDate, String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(strDate);
    }

    public static String getCusTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(new Date());
    }

    public static String getNotificationTime(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    public static String changeTimeFormat(Date strDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(strDate);
    }

    public static String changeTimeFormatSpe(Date strDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(strDate) + ".0000000";
    }

    public static String getLocalDateString(Date selDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        return dateFormat.format(selDate);
    }

    public static Date getStringToTime(String time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            return format.parse(time);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String getStrimgToStringForReg(String time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date newDate;
        try {
            newDate = format.parse(time);
        } catch (ParseException e) {
            newDate = new Date();
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(newDate);
    }

//    public static String getCorrectTimeFromSec(String sec) {
//
//    }

    public static String getLocalTimeFromServer(double time) {
        String timeStr = String.valueOf(time);
        String[] list = timeStr.split("\\.");
        if (list.length != 2) {
            return "00:00";
        }

        String hourStr = list[0];
        if(hourStr.length() == 1) hourStr = "0" + hourStr;

        double min = time - (int)time;
        String minStr = String.valueOf((int)Math.round(min * 60));
        if(minStr.length() == 1) {
            minStr = "0" + minStr;
        }
        return hourStr + ":" + minStr;
    }

}
