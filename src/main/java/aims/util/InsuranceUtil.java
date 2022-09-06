package aims.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InsuranceUtil {

	/**
	 * 보험나이를 기준으로 생년월일을 생성한다.
	 * (보험나이: 생일기준으로 6개월이 지났으면 +1살이다.)
	 * @param age 보험나이
	 * @return 생년월일
	 */
	public static String getBirthday(int age) {
		String result = "" ;

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -age+1);
		calendar.add(Calendar.MONTH, -7);

		Date date = calendar.getTime();
		
		result = DateUtil.formatString(date, "yyyy")+DateUtil.formatString(date, "MM")+DateUtil.formatString(date, "dd");

		return result;
	}

	/**
	 * 보험나이 구하기(삼성생명 Ver.)
	 * @param ymd
	 * @return
	 */
	public static int getInsuAgeByYmd(String ymd) {
		int age = 0;
		
		if(ymd != null && !ymd.equals("") && ymd.length() == 8) {
	    
			Date today  = new Date();
		    int tYear  = Integer.parseInt(DateUtil.formatString(today, "yyyy"));
		    int tMonth = Integer.parseInt(DateUtil.formatString(today, "MM"));
		    int tDay   = Integer.parseInt(DateUtil.formatString(today, "dd"));
		    
		    int year  = Integer.parseInt(ymd.substring(0,4));
		    int month = Integer.parseInt(ymd.substring(4,6));
		    int day   = Integer.parseInt(ymd.substring(6,8));
			
		    boolean dateChk = false;
			if (((tMonth == 1 || tMonth == 3 || tMonth == 5 || tMonth == 7 || tMonth == 8 || tMonth == 10 || tMonth == 12) && tDay == 31) || 
				((tMonth == 4 || tMonth == 6 || tMonth == 9 || tMonth == 11) && tDay == 30) || 
				(tMonth == 2 && (tDay == 28 || tDay == 29))) {
				dateChk = true;
			}
	
			int calDay = tDay - day;
			if (calDay < 0 && dateChk == false) {
				tMonth--;
			}
	
			int calMonth = tMonth - month;
			if (calMonth < 0) {
				tYear--;
				calMonth = calMonth + 12;
			}
	
			age = tYear - year;
			if (calMonth > 5) {
				age++;
			}

		}
		
		return age;	    
	}
	
	/**
	 * 상령일 구하기
	 * @param ymd
	 * @return
	 */
	
	public static String getInsuDay(String ymd) {
		String insuDay = "";

		if (ymd.length() == 8){
			try {
				SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
				SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

				String birthMMdd = ymd.substring(4,8);
				String toYear = yyyy.format(new Date());
				String todayBirth = toYear + birthMMdd; 

				Date birthday = yyyyMMdd.parse(todayBirth); 

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(birthday);
				calendar.add(Calendar.MONTH, +6);

				insuDay = yyyyMMdd.format(calendar.getTime());				
		    } catch (Exception e) {
				e.printStackTrace();
			}
		}

		return insuDay;	 
	}
	
	
	
	/**
	 * 상령일 까지 남은 날짜 구하기
	 * @param ymd
	 * @return
	 */
	public static long getInsuDayCount(String ymd) {

		long insuDayCount = 0;
		String insuDay = "";		    	
		
		if (ymd.length() == 8){
		    try {
				SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
				SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");		    	
		    	
				String birthMMdd = ymd.substring(4,8);
				String toYear = yyyy.format(new Date());
				String todayBirth = toYear + birthMMdd; 

				Date birthday = yyyyMMdd.parse(todayBirth); 
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(birthday);
				calendar.add(Calendar.MONTH, +6);

				insuDay = yyyyMMdd.format(calendar.getTime());

		    	// 상령일이 오늘보다 작으면 + 1년 처리한다.
				String today = yyyyMMdd.format(new Date());
		    	if (Integer.parseInt(insuDay) < Integer.parseInt(today)){
		    		Calendar calendarEx = Calendar.getInstance();
		    		calendarEx.setTime(calendar.getTime());
		    		calendarEx.add(Calendar.YEAR, +1); 
		    		insuDay = yyyyMMdd.format(calendarEx.getTime());
		    	}
		    	
		    	// 두 날짜간 차이를 구함
			    Date beginDate = yyyyMMdd.parse(today);
			    Date endDate = yyyyMMdd.parse(insuDay);
			    long diff = endDate.getTime() - beginDate.getTime();
			    insuDayCount = diff / (24 * 60 * 60 * 1000);		    	
		    	
			    
			    // 만약 해당 수가 365 보다 클경우는 -365 를 하는 로직 추가해야할듯
			    
			} catch (Exception e) {
				e.printStackTrace();
			}
		    
		}

		return insuDayCount;	 
	}	

}
