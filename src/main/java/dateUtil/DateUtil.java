/*
 * 文 件 名:  DateUtil.java
 * 版    权:  Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 创 建 人:  james
 * 创建时间:  2016年8月30日
 */
package dateUtil;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <一句话功能简述>
 *  
 * @author  james
 * @version  [V1.00, 2016年8月30日]
 * @see  [相关类/方法]
 * @since V1.00
 */
public class DateUtil {

	private static final SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat sdfWeek = new SimpleDateFormat("yyyy-ww");
	private static final SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
	
	private static transient int gregorianCutoverYear = 1582;

	/** 闰年中每月天数 */
	private static final int[] DAYS_P_MONTH_LY = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	/** 非闰年中每月天数 */
	private static final int[] DAYS_P_MONTH_CY = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	/** 代表数组里的年、月、日 */
	private static final int Y = 0, M = 1, D = 2;
	
	
	/**
	 * 
	 * 得到昨天的日期（yyyy-MM-dd）
	 * <功能详细描述>
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String getLastDay(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date lastDay = calendar.getTime();
		return sdfDay.format(lastDay);
	}
	
	/**
	 * 
	 * 传入一个日期，得到这个日期的前一天的日期
	 * 如果传入类似2016-08-32的日期，sdfDay.parse(dateTime)会转换成2016-09-01
	 * @param dateTime
	 * @return
	 * @throws ParseException
	 * @see [类、类#方法、类#成员]
	 */
	public static String getLastDay(String dateTime) throws ParseException{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdfDay.parse(dateTime));
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date lastDay = calendar.getTime();
		return sdfDay.format(lastDay);
	}
	

	/**
	 * 将代表日期的字符串分割为代表年月日的整形数组
	 * 
	 * @param date
	 * @return
	 */
	public static int[] splitYMD(String date) {
		date = date.replace("-", "");
		int[] ymd = { 0, 0, 0 };
		ymd[Y] = Integer.parseInt(date.substring(0, 4));
		ymd[M] = Integer.parseInt(date.substring(4, 6));
		ymd[D] = Integer.parseInt(date.substring(6, 8));
		return ymd;
	}

	/**
	 * 检查传入的参数代表的年份是否为闰年
	 * 
	 * @param year
	 * @return
	 */
	public static boolean isLeapYear(int year) {
		return year >= gregorianCutoverYear ? ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)))
				: (year % 4 == 0);
	}

	/**
	 * 日期加1天
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	private static int[] addOneDay(int year, int month, int day) {
		if (isLeapYear(year)) {
			day++;
			if (day > DAYS_P_MONTH_LY[month - 1]) {
				month++;
				if (month > 12) {
					year++;
					month = 1;
				}
				day = 1;
			}
		} else {
			day++;
			if (day > DAYS_P_MONTH_CY[month - 1]) {
				month++;
				if (month > 12) {
					year++;
					month = 1;
				}
				day = 1;
			}
		}
		int[] ymd = { year, month, day };
		return ymd;
	}

	/**
	 * 将不足两位的月份或日期补足为两位
	 * 
	 * @param decimal
	 * @return
	 */
	public static String formatMonthDay(int decimal) {
		DecimalFormat df = new DecimalFormat("00");
		return df.format(decimal);
	}

	/**
	 * 将不足四位的年份补足为四位
	 * 
	 * @param decimal
	 * @return
	 */
	public static String formatYear(int decimal) {
		DecimalFormat df = new DecimalFormat("0000");
		return df.format(decimal);
	}

	/**
	 * 计算两个日期之间相隔的天数
	 * 
	 * @param begin
	 * @param end
	 * @return
	 * @throws ParseException
	 */
	public static long countDay(String begin, String end) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date beginDate, endDate;
		long day = 0;
		try {
			beginDate = format.parse(begin);
			endDate = format.parse(end);
			//加上-1,排除今天
			day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000) - 1;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return day;
	}

	/**
	 * 以循环的方式计算日期
	 * 
	 * @param beginDate
	 *            endDate
	 * @param days
	 * @return
	 */
	public static List<String> getEveryday(String beginDate) {
		long days = countDay(beginDate, sdfDay.format(new Date()));
		int[] ymd = splitYMD(beginDate);
		List<String> everyDays = new ArrayList<String>();
		everyDays.add(beginDate);
		for (int i = 0; i < days; i++) {
			ymd = addOneDay(ymd[Y], ymd[M], ymd[D]);
			everyDays.add(formatYear(ymd[Y]) + "-" + formatMonthDay(ymd[M]) + "-" + formatMonthDay(ymd[D]));
		}
		return everyDays;
	}

	/**
	 * 
	 * 获取从beginDate开始到现在的每个星期（周六为起始）
	 * <功能详细描述>
	 * @param beginDate
	 * @return
	 * @throws ParseException
	 * @see [类、类#方法、类#成员]
	 */
	public static List<String> getEverySaturday(String beginDate) throws ParseException {
		List<String> weekList = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.SATURDAY);
		calendar.setTime(sdfDay.parse(beginDate));
		int day = calendar.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		String saturdayTime = "";
		if (7 == day) {
			saturdayTime = sdfDay.format(calendar.getTime());
		} else {
			calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - day - 7);
			saturdayTime = sdfDay.format(calendar.getTime());
		}
		calendar.add(Calendar.DATE, 6);
		String fridayTime = sdfDay.format(calendar.getTime());
		weekList.add(saturdayTime + "~" + fridayTime);
		String lastSaturdayTime = getLastSaturday();
		while (saturdayTime.compareTo(lastSaturdayTime) < 0) {
			calendar.add(Calendar.DATE, 1);
			saturdayTime = sdfDay.format(calendar.getTime());
			calendar.add(Calendar.DATE, 6);
			fridayTime = sdfDay.format(calendar.getTime());
			weekList.add(saturdayTime + "~" + fridayTime);
		}
		return weekList;
	}
	
	/**
	 * 
	 * 根据今天的日期，得到上周周六（此方法是以周六为一周的开始）
	 * <功能详细描述>
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String getLastSaturday(){
		Calendar calendar = Calendar.getInstance();
		//设置一周的开始是周六
		calendar.setFirstDayOfWeek(Calendar.SATURDAY);
		calendar.setTime(new Date());
		// 获得当前日期是一个星期的第几天,此方法获取到的第几天与设置周开始日期无关，周日就是1，周一就是2，周二就是3...周六就是7
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DATE, -7);
		String saturdayTime = "";
		if (7 == day) {
			saturdayTime = sdfDay.format(calendar.getTime());
		} else {
			calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - day - 7);
			saturdayTime = sdfDay.format(calendar.getTime());
		}
		return saturdayTime;
	}
	
	/**
	 * 
	 * 设置一周的开始，可以获取上周的开始日期
	 * <功能详细描述>
	 * @param firstDay
	 * @return
	 * @throws ParseException
	 * @see [类、类#方法、类#成员]
	 */
	public static String getLastWeekFirstDay(int firstDay) throws ParseException{
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(firstDay);
		calendar.setTime(new Date());
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		System.out.println(day);
		calendar.add(Calendar.DATE, -7);
		String mondayTime = "";
		if(firstDay == day){
			mondayTime = sdfDay.format(calendar.getTime());
		}else if(day < firstDay){
			calendar.add(Calendar.DATE, firstDay - day - 7);
			mondayTime = sdfDay.format(calendar.getTime());
		}else{
			calendar.add(Calendar.DATE, firstDay - day);
			mondayTime = sdfDay.format(calendar.getTime());
		}
		return mondayTime;
	}
	
	/**
	 * 
	 * 获取从开始日期到上个月的每个月的起始和结束日期
	 * eg. beginDate:2016-01-02
	 * return [2016-01-01~2016-01-31, 2016-02-01~2016-02-29, 2016-03-01~2016-03-31, 
	 * 2016-04-01~2016-04-30, 2016-05-01~2016-05-31, 2016-06-01~2016-06-30, 2016-07-01~2016-07-31]
	 * @param beginDate
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static List<String> getEveryMonth(String beginDate){
		List<String> monthList = new ArrayList<String>();
		int[] ymd = splitYMD(beginDate);
		int[] ymdNow = splitYMD(sdfDay.format(new Date()));
		for(int i = 1; i < ymdNow[M]; i++){
			int monthLastDay = 0;
			if(isLeapYear(ymd[Y])){
				monthLastDay = DAYS_P_MONTH_LY[i - 1];
			}else{
				monthLastDay = DAYS_P_MONTH_CY[i - 1];
			}
			monthList.add(formatYear(ymd[Y]) + "-" + formatMonthDay(i) + "-" + formatMonthDay(1) 
			+ "~" + formatYear(ymd[Y]) + "-" + formatMonthDay(i) + "-" + formatMonthDay(monthLastDay));
		}
		return monthList;
	}
	
	
	public static void main(String[] args) throws ParseException {
		System.out.println(getLastWeekFirstDay(Calendar.SUNDAY));
		System.out.println("====================================");
		
	}
	
	
}
