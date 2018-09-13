package main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ServerTimer{
	
	public static String getTotalWithS()
	{
		Calendar now = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return s.format(now.getTime());
	}
	public static String getTotal()
	{
		Calendar now = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("yyyyMMddHHmmss");
		return s.format(now.getTime());
	}
	public static String getFullWithS()
	{
		Calendar now = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		return s.format(now.getTime());
	}
	public static String getFull(Calendar c)
	{
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return s.format(c.getTime());
	}
	public static String getFull()
	{
		Calendar now = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return s.format(now.getTime());
	}
	public static String getFull(int second) {
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c2000 = Calendar.getInstance();
		try {
			c2000.setTime(s.parse("2000-1-1 0:0:0"));
		} catch (Exception e) {		}
		long l = (long)second + c2000.getTimeInMillis()/1000;
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(l * 1000);		
		return s.format(now.getTime());
	}
	public static String getYMD()
	{
		Calendar now = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd");
		return s.format(now.getTime());
	}
	public static String getYMD(Calendar c)
	{
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd");
		return s.format(c.getTime());
	}
	public static Calendar getCalendarFromString(String str)
	{
		Calendar c = Calendar.getInstance();
		boolean mark=true;
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			c.setTime(s.parse(str));
		} catch (ParseException e) {
			mark=false;
		}
		if(mark==false){
			
	        SimpleDateFormat s1=new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
			try {
				c.setTime(s1.parse(str));
			} catch (ParseException e) {
				mark=false;
			}
		}
		
		if(mark==false){
			SimpleDateFormat s2=new SimpleDateFormat("yyyy:MM:dd");
			try {
				c.setTime(s2.parse(str));
			} catch (ParseException e) {
				mark=false;
			}
		}
		
		if(mark==false){
			SimpleDateFormat s2=new SimpleDateFormat("yyyy-MM-dd");
			try {
				c.setTime(s2.parse(str));
			} catch (ParseException e) {
				mark=false;
			}
		}
		return c;
	}
	
	public static int distOfDay()
	{
		Calendar c1970 = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			c1970.setTime(s.parse("1970-1-1 0:0:0"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int) ((Calendar.getInstance().getTimeInMillis() - c1970.getTimeInMillis())/(24*60*60*1000));
	}
	public static int distOfDay(Calendar c)
	{
		Calendar c1970 = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			c1970.setTime(s.parse("1970-1-1 0:0:0"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int) ((c.getTimeInMillis() - c1970.getTimeInMillis())/(24*60*60*1000));
	}
	public static int distOfHour()
	{
		
		Calendar c2000 = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			c2000.setTime(s.parse("2000-1-1 0:0:0"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int) ((Calendar.getInstance().getTimeInMillis() - c2000.getTimeInMillis())/(60*60*1000));
	}
	public static int distOfHour(Calendar c)
	{
		
		Calendar c2000 = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			c2000.setTime(s.parse("2000-1-1 0:0:0"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int) ((c.getTimeInMillis() - c2000.getTimeInMillis())/(60*60*1000));
	}
	
	public static int distOfMinute()
	{
		Calendar c2000 = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			c2000.setTime(s.parse("2000-1-1 0:0:0"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int) ((Calendar.getInstance().getTimeInMillis() - c2000.getTimeInMillis())/(60*1000));
	}
	public static int distOfMinute(Calendar c)
	{
		Calendar c2000 = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			c2000.setTime(s.parse("2000-1-1 0:0:0"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int) ((c.getTimeInMillis() - c2000.getTimeInMillis())/(60*1000));
	}
	public static int distOfSecond()
	{
		Calendar c2000 = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			c2000.setTime(s.parse("2000-1-1 0:0:0"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int) ((Calendar.getInstance().getTimeInMillis() - c2000.getTimeInMillis())/(1000));
	}
	public static int distOfSecond(Calendar c)
	{
		Calendar c2000 = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			c2000.setTime(s.parse("2000-1-1 0:0:0"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int) ((c.getTimeInMillis() - c2000.getTimeInMillis())/(1000));
	}

    public static String getYearMonth(){
		
    	Calendar now = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM");
		return s.format(now.getTime());		
	}
    public static String getDayString()
	{
		Calendar now = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("dd");
		return s.format(now.getTime());
	}
    public static String getHmString(){
	   
    	Calendar now = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("HHmm");
		return s.format(now.getTime());
     }
    public static String getHourString(){
 	   
    	Calendar now = Calendar.getInstance();
		SimpleDateFormat s=new SimpleDateFormat("HH");
		return s.format(now.getTime());
     }
    public static int getHour(){
    	return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }
}
