package com.nl.pm.server.common;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {
    public static Long convertDateToLong(Date date){
        if(date == null){
            return null;
        }
        return date.getTime();
    }

    public static String convertDateToStr(Date date){
        if(date!=null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String format = df.format(date);
            return format;
        }else{
            return null;
        }
    }

    public static Date convertLongToDate(Long datetime){
        if(datetime != null){
            Date date = new Date(datetime);
            return date;
        }else{
            return null;
        }
    }

    public static Date convertStrToDate(String dateStr) throws ParseException {
        if(dateStr!=null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date date = df.parse(dateStr);
            return date;
        }else{
            return null;
        }
    }

    public static List<String> getBetweenDays(String starttime, String endtime) throws ParseException {
        List<String> betweenTime = new ArrayList<String>();
        try
        {
            Date sdate= new SimpleDateFormat("yyyy-MM-dd").parse(starttime);
            Date edate= new SimpleDateFormat("yyyy-MM-dd").parse(endtime);

            SimpleDateFormat outformat = new SimpleDateFormat("yyyy-MM-dd");

            Calendar sCalendar = Calendar.getInstance();
            sCalendar.setTime(sdate);
            int year = sCalendar.get(Calendar.YEAR);
            int month = sCalendar.get(Calendar.MONTH);
            int day = sCalendar.get(Calendar.DATE);
            sCalendar.set(year, month, day, 0, 0, 0);

            Calendar eCalendar = Calendar.getInstance();
            eCalendar.setTime(edate);
            year = eCalendar.get(Calendar.YEAR);
            month = eCalendar.get(Calendar.MONTH);
            day = eCalendar.get(Calendar.DATE);
            eCalendar.set(year, month, day, 0, 0, 0);

            while (sCalendar.before(eCalendar))
            {
                betweenTime.add(outformat.format(sCalendar.getTime()));
                sCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            betweenTime.add(outformat.format(eCalendar.getTime()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return betweenTime;
    }
}
