package gms.helper;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by bvolovyk on 01.12.2016.
 */
public class TimeOperations {

	public static final String BEGIN_OF_DAY_SUFFIX = " 00:00:00";
	public static final String END_OF_DAY_SUFFIX = " 23:59:59";
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static final String DESIRED_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static String getLocalTimeZone() {
        TimeZone timeZone = TimeZone.getDefault();
        return timeZone.getID();
    }

    public static int getLocalTimeZoneOffset() {
        TimeZone timeZone = TimeZone.getDefault();
        return timeZone.getOffset(new Date().getTime())/1000/60;
    }

    /**
     * Method returns timestamp string in ISO 8601 format "yyyy-MM-ddTHH:mm:ss.SSSZ" such as "2013-05-28T15:30:00.000Z"
     *
     * @param secondsFromNow
     * @return
     */
    public static String getUTCTimeInFuture(int secondsFromNow) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(timeZone);
        calendar.add(Calendar.SECOND, +secondsFromNow);
        String formatted = simpleDateFormat.format(calendar.getTime());
        return formatted + ".000Z";
    }

    /**
     * Method returns timestamp string in ISO 8601 format "yyyy-MM-ddTHH:mm:ss.SSSZ" such as "2013-05-28T15:30:00.000Z"
     *
     * @param secondsFromNow
     * @return
     */
    public static String getUTCTimeInPast(int secondsFromNow) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(timeZone);
        calendar.add(Calendar.SECOND, -secondsFromNow);
        String formatted = simpleDateFormat.format(calendar.getTime());
        return formatted + ".000Z";
    }

    public static void getMethodExecutionTime(long startTime, long endTime, String methodName) {
        long executionNanoTime = endTime - startTime;
        long minutes = TimeUnit.NANOSECONDS.toMinutes(executionNanoTime);
        long seconds = TimeUnit.NANOSECONDS.toSeconds(executionNanoTime) - TimeUnit.MINUTES.toSeconds(minutes);
        long milliseconds =
                TimeUnit.NANOSECONDS.toMillis(executionNanoTime) -
                        TimeUnit.MINUTES.toMillis(minutes) -
                        TimeUnit.SECONDS.toMillis(seconds);
        String executionTime = String.format("%d min, %d sec, %d ms", minutes, seconds, milliseconds);
        System.out.printf("Execution time for %s method is: %s%n", methodName, executionTime);
    }

    public static String getDateDaysAhead(int timeZoneOffset, int daysFromNow, String pattern) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(tz);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.US);
        simpleDateFormat.setTimeZone(tz);
        calendar.add(Calendar.MINUTE, timeZoneOffset);
        calendar.add(Calendar.DATE, +daysFromNow);
        String formatted = simpleDateFormat.format(calendar.getTime());
        return formatted;
//        Calendar calendar = Calendar.getInstance();
////        calendar.add(Calendar.HOUR, Integer.parseInt(timeZoneOffset));
//        calendar.add(Calendar.DATE, +daysFromNow);
//        Date date = calendar.getTime();
//        return new SimpleDateFormat("MM-dd-yyyy").format(date);
    }

    public static String getDateDaysBack(int timeZoneOffset, int daysFromNow, String pattern) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(tz);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.US);
        simpleDateFormat.setTimeZone(tz);
        calendar.add(Calendar.MINUTE, timeZoneOffset);
        calendar.add(Calendar.DATE, -daysFromNow);
        String formatted = simpleDateFormat.format(calendar.getTime());
        return formatted;
    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return formatted;
    }

    public static String getCurrentDayOfWeek() {
        String day;
        String[] weekDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        Calendar.getInstance();
        //Calendar calendar = Calendar.getInstance();
        day = weekDays[Calendar.DAY_OF_WEEK];
        return day;
    }
    
    public static String getDesiredTimeInPeriod(int daysAgo, int daysAfter){
    	Timestamp desiredTime = null;   
    	    		
    	String dS=String.valueOf(getDateDaysBack(0, daysAgo, DATE_PATTERN))+BEGIN_OF_DAY_SUFFIX;
    	String dE=String.valueOf(getDateDaysAhead(0, daysAfter, DATE_PATTERN))+END_OF_DAY_SUFFIX;
    	    	
    	long startDateTime = Timestamp.valueOf(dS).getTime();
      	long seconds = startDateTime / 1000;
       	long endDateTime = Timestamp.valueOf(dE).getTime();
       	long diff = endDateTime - startDateTime + 1;
    	desiredTime = new Timestamp(startDateTime + (long)(Math.random() * diff));
    	
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DESIRED_TIME_PATTERN);
       	String formatted = simpleDateFormat.format(desiredTime.getTime())+"Z";
    	return formatted;
    	
    }
    
    public static String getDesiredDateInPeriod(int daysAgo, int daysAfter){
    	String desiredTime = null;
    	
    	int startDay = Integer.valueOf(getDateDaysBack(0, daysAgo, "d"));
    	int startMonth= Integer.valueOf(getDateDaysBack(0, daysAgo, "MM"));
    	int startYear = Integer.valueOf(getDateDaysBack(0, daysAgo, "yyyy"));
    	int endDay = Integer.valueOf(getDateDaysAhead(0, daysAfter, "d"));
    	int endMonth= Integer.valueOf(getDateDaysAhead(0, daysAfter, "MM"));
    	int endYear = Integer.valueOf(getDateDaysAhead(0, daysAfter, "yyyy"));
    	
    	DateFormat formatter; 
    	Date startDate, endDate; 
    	formatter = new SimpleDateFormat(DATE_PATTERN);
    	   	    	
    	Random random = new Random();
    	int minDay = (int) LocalDate.of(startYear, startMonth, startDay).toEpochDay();
    	int maxDay = (int) LocalDate.of(endYear, endMonth, endDay).toEpochDay();
    	
    	long randomDay = minDay + random.nextInt(maxDay - minDay);
    	LocalDate randomCbDate = LocalDate.ofEpochDay(randomDay);
    	    	
    	desiredTime=randomCbDate.toString();
    	System.out.println(desiredTime);
    	
    	return desiredTime;
    }
}
