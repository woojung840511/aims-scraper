package aims.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 날짜 관련 유틸리티 클래스
 * @author www.inpion.com
 */
public class DateUtil {

	/**
	 * 날짜를 format 형식의 문자열로 변환한다.
	 * @param date 날짜
	 * @param format 형식
	 * @return 변환된 문자열
	 */
	public static String formatString(Date date, String format) {
		String result = "";
		
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		result = formatter.format(date);
		
		return result;
	}
	
	/**
	 * 문자열 형식의 날짜를 Date로 변환한다.
	 * @param date 문자열날짜
	 * @param format 형식
	 * @return 날짜
	 */
	public static Date parseDate(String date, String format) {
		Date result = null;
		
		SimpleDateFormat formatter = null;
		
		try {
			formatter = new SimpleDateFormat(format);
			result = formatter.parse(date);
		} catch (ParseException e) {
		}
		
		return result;
	}
	
	/**
	 * 날짜에 일수를 더한다.
	 * @param date 날짜
	 * @param amount 더할 일수
	 * @return 날짜
	 */
	public static Date addDay(Date date, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		calendar.add(Calendar.DAY_OF_YEAR, amount);
		
		return calendar.getTime();
	}
	
	/**
	 * 날짜에 개월을 더한다.
	 * @param date 날짜
	 * @param amount 더할 개월수
	 * @return 날짜
	 */
	public static Date addMonth(Date date, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		calendar.add(Calendar.MONTH, amount);
		
		return calendar.getTime();
	}
	
	/**
	 * 날짜에 년수를 더한다.
	 * @param date 날짜
	 * @param amount 더할 년수
	 * @return 날짜
	 */
	public static Date addYear(Date date, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		calendar.add(Calendar.YEAR, amount);
		
		return calendar.getTime();
	}
	
	/**
	 * 두 날짜간 차이를 구함
	 * @param begin 시작일자
	 * @param end 종료일자
	 * @return 날짜
	 * @throws Exception 
	 */
	public static long diffOfDate(String begin, String end) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	 
	    Date beginDate = formatter.parse(begin);
	    Date endDate = formatter.parse(end);
	 
	    long diff = endDate.getTime() - beginDate.getTime();
	    long diffDays = diff / (24 * 60 * 60 * 1000);
	 
	    return diffDays;
	}

}
