package com.lf.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;

/**
 *     
 * @项目名称：INote    
 * @类名称：TimeUtil    
 * @类描述：时间工具类    
 * @创建人：lianfeng    
 * @创建时间：2015-2-11 下午2:25:06    
 * @修改人：lianfeng    
 * @修改时间：2015-2-11 下午2:25:06    
 * @修改备注：    
 * @version     
 *
 */
public class TimeUtil {
    
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 以秒的单位计算 个性时间显示 服务器端由于没有存储毫秒数
     * 
     * @param timestamp
     * @return
     */
    public static String converTime(long timestamp) {
        long currentSeconds = System.currentTimeMillis() / 1000;
        timestamp = (timestamp + "").length() == 13 ? timestamp / 1000 : timestamp;
        long timeGap = currentSeconds - timestamp;// 与现在时间相差秒数
        String timeStr = null;
        if (timeGap > 24 * 60 * 60 * 30 * 365) {// 1年以上
            timeStr = timeGap / (24 * 60 * 60 * 30 * 365) + "年前";
        }
        else if (timeGap > 24 * 60 * 60 * 30) {// 30天以上
            timeStr = timeGap / (24 * 60 * 60 * 30) + "月前";
        }
        else if (timeGap > 24 * 60 * 60) {// 1-30天
            timeStr = timeGap / (24 * 60 * 60) + "天前";
        }
        else if (timeGap > 60 * 60) {// 1小时-24小时
            timeStr = timeGap / (60 * 60) + "小时前";
        }
        else if (timeGap > 60) {// 1分钟-59分钟
            timeStr = timeGap / 60 + "分钟前";
        }
        else {// 1秒钟-59秒钟
            timeStr = "刚刚";
        }
        return timeStr;
    }
    
    /**
     * 根据日期取出规定格式的字符串
     * 
     * @param timestamp
     * @return
     */
    public static String getTimeFromDate(long timestamp) {
        String dateStr = "";
        long timegap = (System.currentTimeMillis() - timestamp) / 1000;
        SimpleDateFormat dateFormat = new SimpleDateFormat("M月dd日");
        SimpleDateFormat timeFormat = new SimpleDateFormat("H:mm");
        if (timegap < 60 * 60 * 24) {
            dateStr = timeFormat.format(new Date(timestamp));
        }
        else {
            dateStr = dateFormat.format(new Date(timestamp));
        }
        return dateStr;
    }
    
    /**
     * 根据日期取出规定格式的字符串
     * 
     * @param timestamp
     * @return
     */
    public static String getTimeFromDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH:mm");
        try {
            return format.format(dateFormat.parse(date));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    /**
     * 格式化只取到分钟数
     * 
     * @param timestamp
     *            秒为单位
     * @return
     */
    public static String getStandardTimes(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault());
        int timeLength = (timestamp + "").length();
        if (timeLength == 10) {
            timestamp = timestamp * 1000;
        }
        Date date = new Date(timestamp);// 服务器端没有存储毫秒数所以要乘1000 还原
        return sdf.format(date);
    }
    
    /**
     * 格式化 当前时间
     * 
     * @param form
     *            格式字符串 可为空串
     * @return
     */
    public static String getCurrentDateTime(String form) {
        String localform = form;
        if ("".equals(localform) || form == null) {
            localform = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat time = new SimpleDateFormat(localform, Locale.getDefault());
        return time.format(new Date());
        
    }
    
    public static String getDateTime(String time) {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = format1.parse(time);
            format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return format1.format(date);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        
        return "";
        
    }
    
    /**
     * 格式化 当前时间
     * 
     * @param form
     *            格式字符串 可为空串
     * @return
     */
    public static long getCurrentDateTimeToLong(String form) {
        long time = 0;
        try {
            String localform = form;
            if ("".equals(localform)) {
                localform = "yyyy-MM-dd HH:mm:ss";
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat(localform, Locale.getDefault());
            String timeStr = dateFormat.format(new Date());
            Date date = dateFormat.parse(timeStr);
            time = date.getTime();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
    
    /**
     * 格式化 当前时间
     * 
     * @param form
     *            格式字符串 可为空串
     * @return
     */
    public static String formatDateTimeToString(String date) {
        if (StringUtils.isEmpty(date)) {
            return "";
        }
        return formatDateTimeToString(null, null, date);
    }
    
    /**
     * 格式化 当前时间
     * 
     * @param form
     *            格式字符串 可为空串
     * @return
     */
    public static String formatDateTimeToString(String srcFomat, String dstFomat, String date) {
        if (StringUtils.isEmpty(srcFomat)) {
            srcFomat = FORMAT_DATETIME;
        }
        if (StringUtils.isEmpty(dstFomat)) {
            dstFomat = "yyyy-MM-dd HH:mm";
        }
        if (StringUtils.isEmpty(date)) {
            return "";
        }
        String string = date;
        Date date2 = null;
        SimpleDateFormat src = new SimpleDateFormat(srcFomat, Locale.getDefault());
        SimpleDateFormat dst = new SimpleDateFormat(dstFomat, Locale.getDefault());
        try {
            date2 = src.parse(date);
            string = dst.format(date2);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return string;
    }
    
    /**
     * @param dt
     *            构造Date
     * @param form
     *            格式化字符串 可为空串
     * @return
     */
    public static String dateTime2String(Date dt, String form) {
        String localform = form;
        if ("".equals(localform)) {
            localform = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat time = new SimpleDateFormat(localform, Locale.getDefault());
        return time.format(dt);
    }
    
    /**
     * 时间戳转换格式化的时间
     * 
     * @param ldt
     *            时间戳
     * @param form
     *            yyyy-MM-dd HH:mm:ss 格式化字符串 可为空
     * @return
     */
    public static String getTime2String(long ldt, String form) {
        String localform = form;
        if ("".equals(localform)) {
            localform = "yyyy-MM-dd HH:mm:ss";
        }
        int timeLength = (ldt + "").length();
        if (timeLength == 10) {
            ldt = ldt * 1000;
        }
        Date dt = new Date(ldt);
        SimpleDateFormat time = new SimpleDateFormat(localform, Locale.getDefault());
        return time.format(dt);
    }
    
    /**
     * 字符串解析成Date对象
     * 
     * @param dataString
     *            要解析字符串
     * @param pattern
     *            要解析的格式 如 yyyy年MM月dd
     * @return Date
     */
    public static Date parseStringtoDate(String dataString, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());// 设定格式
        try {
            return dateFormat.parse(dataString);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * @Description:获得两个时间戳的间隔分钟
     * @param src
     *            时间错1
     * @param dst
     *            时间戳2
     * @return long 间隔时间
     * @throws
     */
    public static long getTimeGapMin(long src, long dst) {
        long gap = src - dst;
        gap = Math.abs(gap);
        gap = gap / 1000 / 60;
        return gap;
    }
    
    /**
     * 格式化日期数据
     * 
     * @param calendar
     * @param pattern
     * @return
     */
    public static String getFormatDate(String srcFormat, String goalFormat, String date) {
    	Date date2 = null;
    	String sRst = "";
    	if (StringUtils.isEmpty(srcFormat)) {
    		srcFormat = FORMAT_DATETIME;
    	}
    	if (StringUtils.isEmpty(goalFormat)) {
    		goalFormat = "yyyy-MM-dd ";
    	}
    	SimpleDateFormat format = new SimpleDateFormat(srcFormat, Locale.getDefault());
    	SimpleDateFormat goalsdFormat = new SimpleDateFormat(goalFormat, Locale.getDefault());
    	try {
    		date2 = format.parse(date);
    		Calendar calendar = Calendar.getInstance(Locale.getDefault());
    		calendar.setTime(date2);
    		sRst = goalsdFormat.format(date2);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return sRst;
    }
    /**
     * 获取当前日期和星期
     * 
     * @param calendar
     * @param pattern
     * @return
     */
    public static String getWeekFromCalendar(String srcFormat, String goalFormat, String date) {
        String dayOfweek = "";
        Date date2 = null;
        String sRst = "";
        if (StringUtils.isEmpty(srcFormat)) {
            srcFormat = FORMAT_DATETIME;
        }
        if (StringUtils.isEmpty(goalFormat)) {
            goalFormat = "yyyy-MM-dd ";
        }
        SimpleDateFormat format = new SimpleDateFormat(srcFormat, Locale.getDefault());
        SimpleDateFormat goalsdFormat = new SimpleDateFormat(goalFormat, Locale.getDefault());
        try {
            date2 = format.parse(date);
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTime(date2);
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            switch (week) {
                case 1:
                    dayOfweek = "星期日";
                    break;
                case 2:
                    dayOfweek = "星期一";
                    break;
                case 3:
                    dayOfweek = "星期二";
                    break;
                case 4:
                    dayOfweek = "星期三";
                    break;
                case 5:
                    dayOfweek = "星期四";
                    break;
                case 6:
                    dayOfweek = "星期五";
                    break;
                case 7:
                    dayOfweek = "星期六";
                    break;
            }
            sRst = goalsdFormat.format(date2) + " " + dayOfweek;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return sRst;
    }
    
    /**
     * 根据日期获取星期
     * 
     * @param date
     * @return
     */
    public static String getWeekFromSting(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date dateString = null;
        try {
            dateString = format.parse(date.trim());
        }
        catch (ParseException e) {
            return "";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateString);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        String dayOfweek = "";
        switch (week) {
            case 1:
                dayOfweek = "星期天";
                break;
            case 2:
                dayOfweek = "星期一";
                break;
            case 3:
                dayOfweek = "星期二";
                break;
            case 4:
                dayOfweek = "星期三";
                break;
            case 5:
                dayOfweek = "星期四";
                break;
            case 6:
                dayOfweek = "星期五";
                break;
            case 7:
                dayOfweek = "星期六";
                break;
        }
        return dayOfweek;
    }
    
    /**
     * 获取当前日期和星期
     * 
     * @param calendar
     * @param pattern
     * @return
     */
    public static String getWeekFromCalendar(Calendar calendar, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        long currentTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
        String dateString = format.format(calendar.getTime());
        String dayOfweek = "";
        long timetamp = (calendar.getTimeInMillis() - currentTime) / (1000);
        if (timetamp < 0) {
            dayOfweek = "今天";
        }
        else if (timetamp < 60 * 60 * 24 * 1) {
            dayOfweek = "明天";
        }
        else if (timetamp < 60 * 60 * 24 * 2) {
            dayOfweek = "后天";
        }
        else {
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            switch (week) {
                case 1:
                    dayOfweek = "周日";
                    break;
                case 2:
                    dayOfweek = "周一";
                    break;
                case 3:
                    dayOfweek = "周二";
                    break;
                case 4:
                    dayOfweek = "周三";
                    break;
                case 5:
                    dayOfweek = "周四";
                    break;
                case 6:
                    dayOfweek = "周五";
                    break;
                case 7:
                    dayOfweek = "周六";
                    break;
            }
        }
        return dateString + " " + dayOfweek;
    }
    
    
    /**
     * @param mTempCalendar
     * @param mCurrentCalendar
     * @return 相减返回天数
     */
    public static double subDate(Calendar mTempCalendar) {
        Calendar mCurrentCalendar = Calendar.getInstance();
        long time = mTempCalendar.getTime().getTime() - mCurrentCalendar.getTime().getTime();
        double tempValue = (time * 1.0) / (24 * 60 * 60 * 1000);
        return tempValue;
    }
    
    @SuppressLint("SimpleDateFormat")
    public static String getUpdateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());
        // 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }
    
    /**
     * @Description:
     * @param @return
     * @return String yyyy-MM-dd HH:mm:ss
     * @throws
     */
    public static String getTime() {
        Date date = new Date();
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return localSimpleDateFormat.format(date);
    }
    
    public static String getTime(long time) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return localSimpleDateFormat.format(time);
    }
    
    public static boolean compareTime(String time, int startTime, int endTime) {
        boolean isvisible = false;
        int hour = Integer.valueOf(time.substring(0, time.indexOf(":")));
        if (hour >= startTime && hour < endTime) {
            isvisible = true;
        }
        return isvisible;
    }
    
    public static String getMAD(String timeType) {
        String mounth = timeType.substring(timeType.indexOf("-") + 1, timeType.lastIndexOf("-"));
        String day = timeType.substring(timeType.lastIndexOf("-") + 1, timeType.indexOf(" "));
        return mounth + "月" + day + "日";
    }
    
    /**
     * 获取月日、已经调试
     * 
     * @param timeType
     * @return
     */
    public static String getMAD1(String timeType) {
        String mounth = timeType.substring(timeType.indexOf("-") + 1, timeType.lastIndexOf("-"));
        String day = timeType.substring(timeType.lastIndexOf("-") + 1);
        return mounth + "月" + day + "日";
    }
    
    public static String getTimeFromMin(int x) {
        String result = "0分";
        if (x != 0) {
            int h = x / 60;
            int m = x - h * 60;
            result = h + "时" + m + "分";
        }
        return result;
    }
    
    public static String getDay(String startDate, int lastDay) {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        Date str_date = null;
        try {
            str_date = myFormat.parse(startDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(str_date);
        cal.add(Calendar.DATE, lastDay);
        
        String time = TimeUtil.dateTime2String(cal.getTime(), "yyyy-MM-dd");
        
        return time;
    }
    
    public static String getDateAfter(String startDate, int day) {
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date str_date;
        Calendar now = Calendar.getInstance();
        try {
            str_date = sdf.parse(startDate);
            now.setTime(str_date);
            now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        
        return sdf.format(now.getTime());
    }
    
    public static String getWeekday(int index) {
        String weekday;
        switch (index) {
            case 1:
                weekday = "周一";
                break;
            case 2:
                weekday = "周二";
                break;
            case 3:
                weekday = "周三";
                break;
            case 4:
                weekday = "周四";
                break;
            case 5:
                weekday = "周五";
                break;
            case 6:
                weekday = "周六";
                break;
            case 0:
                weekday = "周七";
                break;
            default:
                weekday = "无数据";
                break;
        }
        return weekday;
    }
    
    public boolean compare(String time1, String time2) throws ParseException {
        // 如果想比较日期则写成"yyyy-MM-dd"就可以了
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        // 将字符串形式的时间转化为Date类型的时间
        Date a = sdf.parse(time1);
        Date b = sdf.parse(time2);
        // Date类的一个方法，如果a早于b返回true，否则返回false
        if (a.before(b))
            return true;
        else
            return false;
    }
    
}
