package com.common.lib.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {

	public static String formatMsTime(long lt) {
		Date msgTime = new Date(lt);

		return msgTime.toString().substring(11, 19);
	}

	public static String formatMsShortTime(long lt) {
		Date msgTime = new Date(lt);

		return msgTime.toString().substring(11, 16);
	}

	public static String formatMsDate(long lt) {
		Date msgTime = new Date(lt);

		int year = msgTime.getYear() + 1900;
		int month = msgTime.getMonth();
		int day = msgTime.getDate();

		return year + "-" + (month + 1) + "-" + (day);
	}

	public static String formatMsDate(long lt, String separator) {
		Date msgTime = new Date(lt);

		int year = msgTime.getYear() + 1900;
		int month = msgTime.getMonth();
		int day = msgTime.getDate();

		return year + separator + (month + 1) + separator + (day);
	}

	/**
	 * 计算两个日期相隔的天数.
	 *
	 * @param d1
	 * @param d2
	 * @return 返回两个日期相隔的天数,如果是同一天返回0.
	 */
	public static int getDaysBetween(Calendar d1, Calendar d2) {
		if (d1.after(d2)) {
			Calendar swap = d1;
			d1 = d2;
			d2 = swap;
		}
		int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
		int y2 = d2.get(Calendar.YEAR);
		if (d1.get(Calendar.YEAR) != y2) {
			d1 = (Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
				d1.add(Calendar.YEAR, 1);
			}
			while (d1.get(Calendar.YEAR) != y2);
		}
		return days;
	}


	public static boolean isInTheSameYear(long src, long dest) {
		Calendar last = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		last.setTimeInMillis(src);
		Calendar current = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		current.setTimeInMillis(dest);
		int lastYear = last.get(Calendar.YEAR);
		int currentYear = current.get(Calendar.YEAR);
		return (lastYear == currentYear);
	}

	/**
	 * 判断两个时间是否在同一天,用于导航获取域名对应IP及端口时使用
	 *
	 * @param src
	 * @param dest
	 * @return
	 */
	public static boolean isInTheSameDay(long src, long dest) {
		Calendar last = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		last.setTimeInMillis(src);
		Calendar current = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		current.setTimeInMillis(dest);
		int lastDay = last.get(Calendar.DAY_OF_YEAR);
		int currentDay = current.get(Calendar.DAY_OF_YEAR);
		return (lastDay == currentDay);
	}

	public static boolean isBeforeYesterdayDate(long times) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return times < c.getTimeInMillis();
	}

	public static boolean isToday(long times) {
		Calendar msgCalendar = Calendar.getInstance();
		Calendar nowCalendar = Calendar.getInstance();
		msgCalendar.setTimeInMillis(times);
		nowCalendar.setTimeInMillis(System.currentTimeMillis());

		int msgYear = msgCalendar.get(Calendar.YEAR);
		int msgMnoth = msgCalendar.get(Calendar.MONTH);
		int msgDay = msgCalendar.get(Calendar.DATE);
		int nowYear = nowCalendar.get(Calendar.YEAR);
		int nowMnoth = nowCalendar.get(Calendar.MONTH);
		int nowDay = nowCalendar.get(Calendar.DATE);

		return msgYear == nowYear && msgMnoth == nowMnoth && msgDay == nowDay;
	}

	public static boolean isYesterdayByDate(long times) {
		Calendar msgCalendar = Calendar.getInstance();
		Calendar nowCalendar = Calendar.getInstance();
		msgCalendar.setTimeInMillis(times);
		nowCalendar.setTimeInMillis(System.currentTimeMillis());

		int msgYear = msgCalendar.get(Calendar.YEAR);
		int msgMnoth = msgCalendar.get(Calendar.MONTH);
		int msgDay = msgCalendar.get(Calendar.DATE);
		int nowYear = nowCalendar.get(Calendar.YEAR);
		int nowMnoth = nowCalendar.get(Calendar.MONTH);
		int nowDay = nowCalendar.get(Calendar.DATE);

		if (msgYear == nowYear && msgMnoth == nowMnoth) { // 同年同月
			if (nowDay - msgDay == 1) {
				return true;
			}
		}
		else if (msgYear == nowYear && msgMnoth != nowMnoth) { // 同年不同月
			if (nowMnoth - msgMnoth == 1) {
				int maximum = msgCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (nowDay == 1 && maximum == msgDay) {
					return true;
				}
			}
		}
		else if (msgYear != nowYear) { // 不同年
			if (nowYear - msgYear == 1 && nowMnoth + 12 - msgMnoth == 1) {
				int maximum = msgCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (nowDay == 1 && maximum == msgDay) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * HH:mm 上午/下午
	 *
	 * @return
	 */
	public static String hh_mm_Format(long times, Resources mResouces) {
		String formatStr = "HH:mm";
		Date date = new Date(times);
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr);
		String result = dateFormat.format(date);
		return result;
	}


	/**
	 * 08-07 12:36
	 *
	 * @return
	 */
	public static String MM_DD_HH_mm_Format(long times) {
		String formatString = "MM-dd HH:mm";
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
		return dateFormat.format(new Date(times));
	}

	/**
	 * 日期格式化
	 * @return xx月xx日 xx:xx
	 */
	public static String MMDD_HHmm_Format(long times) {
		String formatString = "MM月dd日 HH:mm";
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
		return dateFormat.format(new Date(times));
	}

	public static String yyyy_MM_DD_HH_mm_ss_format(long times) {
		String formatString = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
		return dateFormat.format(new Date(times));
	}

	public static String yyyy_MM_DD_HH_mm_format(long times) {
		String formatString = "yyyy-MM-dd HH:mm";
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
		return dateFormat.format(new Date(times));
	}

	public static String yyyy_MM_DD_format(long times) {
		String formatString = "yyyy-MM-dd";
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
		return dateFormat.format(new Date(times));
	}

	public static String MM_DD_format(long times) {
		String formatString = "MM-dd";
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
		return dateFormat.format(new Date(times));
	}

	public static String formatCloudDate(long lt) {
		String year = "";
		String month = "";
		String day = "";
		String h = "";
		String m = "";
		String s = "";
		try {
			String data = String.valueOf(lt);
			year = data.substring(0, 4);
			month = data.substring(4, 6);
			day = data.substring(6, 8);
			h = data.substring(8, 10);
			m = data.substring(10, 12);
			s = data.substring(12, 14);
		}
		catch (Exception e) {
			return "";
		}
		return year + "-" + (month) + "-" + (day) + " " + h + ":" + m + ":" + s;
	}

	public static String yyyy_MM_DD_Format(long times) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(times));
	}

	/**
	 * 获取date零晨时间
	 *
	 * @param date
	 * @return
	 */
	public static long getDateTimes(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	public static long getMiliSecondOfDay(long times) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(times);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return times - cal.getTimeInMillis();
	}

	/**
	 * 获得当天时，分，秒，毫秒的偏移量
	 */
	public static long getTimesOfToday() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Date().getTime() - cal.getTimeInMillis();
	}

	public static Date createDateofToadyByTimeOffset(long timeOffset) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date date = new Date();
		date.setTime(cal.getTimeInMillis() + timeOffset);
		return date;
	}

	/**
	 * 时间格式HH:mm
	 *
	 * @param dateStr
	 * @return
	 */
	public static long parsehh_mmString(String dateStr) {
		if (TextUtils.isEmpty(dateStr)) {
			return -1;
		}
		else {
			String[] timeArr = dateStr.split(":");
			if (timeArr == null || timeArr.length != 2) {
				return -1;
			}
			else {
				try {
					return Long.parseLong(timeArr[0]) * 1000 * 60 * 60 + Long.parseLong(timeArr[1]) * 1000 * 60;
				}
				catch (Exception e) {
					e.printStackTrace();
					return -1;
				}
			}
		}
	}

	public static long parseyyyy_MM_dd_date(String dateString) {
		SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = yyyy_MM_dd.parse(dateString);
			return date.getTime();
		}
		catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/***
	 * 不带星期和昨日今日的时间格式化, 用于通话记录详情页面
	 *
	 * @param context
	 * @param locale
	 * @param time
	 * @return
	 */
	public static String formatDateNoWeek(Context context, Locale locale, long time) {
		String displayDatetime = null;
		long currentTime = System.currentTimeMillis();
		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.setTimeInMillis(currentTime);
		Calendar timeCalendar = Calendar.getInstance();
		timeCalendar.setTimeInMillis(time);
		Date date = timeCalendar.getTime();

		boolean is24 = DateFormat.is24HourFormat(context);
		String format;
		if (is24) {
			format = "yyyy-MM-dd HH:mm";
		}
		else {
			format = "yyyy-MM-dd hh:mm";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, locale);
		displayDatetime = dateFormat.format(date);

		if (!is24) {
			String ampmValues;
			if (timeCalendar.get(Calendar.AM_PM) == 0) {
				ampmValues = "AM";
			}
			else {
				ampmValues = "PM";
			}
			return displayDatetime + " " + ampmValues;
		}

		return displayDatetime;
	}


	/**
	 * 返回当前时间的小时
	 *
	 * @return
	 */
	public static int getCurrentTimeHour() {
		int hour = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		return hour;
	}
    public static String getCurrentDate() {
        String datetime = "";
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        datetime = sdf.format(date);
        return datetime;
    }

    public static String getCurrentDateTime() {
        String datetime = "";
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        datetime = sdf.format(date);
        return datetime;
    }

    public static String getCurrentTime() {
        String datetime = "";
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        datetime = sdf.format(date);
        return datetime;
    }

	/**
	 * 格式化录音时间
	 *
	 * @param duration
	 * @return e.g. 00:00
	 */
	public static String formatRecordTime(int duration) {
		if (duration < 60) {
			if (duration < 10) {
				return "00" + ":0" + duration;
			}
			else {
				return "00" + ":" + duration;
			}
		}
		else if (duration >= 60 && duration < 600) {
			int minit = duration / 60;
			int sec = duration % 60;
			String minitStr = String.valueOf(minit);
			String secStr = String.valueOf(sec);
			if (minit < 10) {
				minitStr = "0" + minit;
			}
			if (sec < 10) {
				secStr = "0" + sec;
			}
			return minitStr + ":" + secStr;
		}
		else {
			return 99 + ":" + 99;
		}
	}

	/**
	 * 格式化时长
	 * @param seconds 秒数
	 * @return 格式00:00:00
	 */
	public static String formatdDuration(int seconds) {
		StringBuilder duration = new StringBuilder();
		int s = seconds % 60;
		int m = (seconds % 3600) / 60;
		int h = seconds / 3600;
		if (h > 0) {
			duration.append(h).append(":");
		}

		if (m == 0) {
			duration.append("00");
		}
		else if (m < 10) {
			duration.append("0").append(m);
		}
		else {
			duration.append(m);
		}
		duration.append(":");

		if (s == 0) {
			duration.append("00");
		}
		else if (s < 10) {
			duration.append("0").append(s);
		}
		else {
			duration.append(s);
		}
		return duration.toString();
	}

	public static String socialCommentNotify_time_format(long times) {
		String formatString = "yyyy/MM/dd HH:mm:ss";
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
		return dateFormat.format(new Date(times));
	}

	public static String formatRecordPosition(long lt) {
		long hours = (lt % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
	    long minutes = (lt % (1000 * 60 * 60)) / (1000 * 60);
	    long seconds = (lt % (1000 * 60)) / 1000;

	    StringBuilder stringBuilder = new StringBuilder();
	    if(hours != 0){
	    	if(hours < 10){
	    		stringBuilder.append("0");
	    	}
	    	stringBuilder.append(hours);
	    	stringBuilder.append(":");
	    }else{
	    	stringBuilder.append("00");
	    	stringBuilder.append(":");
	    }
	    if(minutes != 0){
	    	if(minutes < 10){
	    		stringBuilder.append("0");
	    	}
	    	stringBuilder.append(minutes);
	    	stringBuilder.append(":");
	    }
	    else{
	    	stringBuilder.append("00");
	    	stringBuilder.append(":");
	    }
	    if(seconds != 0){
	    	if(seconds < 10){
	    		stringBuilder.append("0");
	    	}
	    	stringBuilder.append(seconds);
	    }
	    else{
	    	stringBuilder.append("00");
	    }
	    return stringBuilder.toString();
	}

	/**
	 * 时间格式化
	 *
	 * @param millisecond 毫秒
	 * @return
	 */
	public static String formatDurationMillisecond(int millisecond) {
		return formatDuration(millisecond / 1000);
	}

	/**
	 * 时间格式化(HH:MM:SS)
	 *
	 * @param second 秒
	 * @return
	 */
	public static String formatDuration(int second) {
		int hh = 0;
		int mm = 0;
		if (second >= 3600)
			hh = second / 3600;
		if ((second - 3600 * hh) >= 60)
			mm = (second - 3600 * hh) / 60;
		if (hh == 0) {
			return String.format("%1$02d:%2$02d", mm, second - 3600 * hh - 60
					* mm);
		} else {
			return String.format("%1$02d:%2$02d:%3$02d", hh, mm, second - 3600
					* hh - 60 * mm);
		}
	}


	/**
	 * 时间比较，返回相差的小时数
	 *
	 * @param oldTime
	 * @param newTime
	 * @return int
	 */
	public static int diffHours(String oldTime, String newTime) {
		int diffHours = 0;
		java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d1 = df.parse(newTime);
			Date d2 = df.parse(oldTime);
			long diffTime = d1.getTime() - d2.getTime();
			diffHours = (int)(diffTime / (1000 * 60 * 60));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return diffHours;
	}

	/**
	 * 返回指定时间到当前时间所经过的时间，并格式化其显示
	 *
	 * @param since
	 * @return
	 */
	public static String getTimeFromNow(long since) {
		long offset = System.currentTimeMillis() - since;
		if (offset < 0) {
			return "1分钟内";
		}
		long second = offset / 1000;
		if (second < 60) {
			return "1分钟内";
		}
		long minutes = second / 60;
		if (minutes < 60) {
			return minutes + "分钟前";
		}
		long hours = minutes / 60;
		if (hours < 24) {
			return hours + "小时前";
		}
//		long days = hours / 24;
//		if (days < 15) {
//			return days + "天前";
//		}
//		long months = days / 30;
//		if (months < 12) {
//			return months + "个月之前";
//		}
		return yyyy_MM_DD_HH_mm_format(since);
	}

}
