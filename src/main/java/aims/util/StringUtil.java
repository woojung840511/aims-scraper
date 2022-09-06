package aims.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;

/**
 * @Author : Samsunglife_eDirect_TF
 * @Date : 2013. 6. 19. 오후 2:43:00
 * @Description : 문자열 관련 util
 * @Version : 1.0
 */
public class StringUtil {

//	private static final Logger logger = Logger.getLogger(StringUtil.class);

	private static String[] SIZE_NAME = {
		"B",
		"K",
		"M",
		"G"
	};
	private static long[] SIZE_UNIT = {
		1,
		1024,
		1024 * 1024,
		1024 * 1024 * 1024
	};
	private static String[] specialChar = {
		"|",
		"\\",
		":",
		"*",
		"\"",
		"<",
		">"
	};

	/**
	 * @title : 문자열 정수형으로 변환
	 * @location : com.samsunglife.edirect.common.util.getInt
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : getInt
	 * @param str : String 문자열
	 * @param defaultVal : int 문자열 값이 null 일 경우 값
	 * @return int
	 */
	public static int getInt(String str, int defaultVal) {
		if (str == null)
			return defaultVal;

		try {
			return Integer.parseInt(str.trim());
		} catch (NumberFormatException ne) {

		}

		return defaultVal;
	}

	/**
	 * @title : 문자열 정수형으로 변환
	 * @location : com.samsunglife.edirect.common.util.getInt
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : getInt
	 * @param str : String 문자열
	 * @return int
	 */
	public static int getInt(String str) {
		return getInt(str, 0);
	}

	/**
	 * @title : 문자열 long 형으로 변환
	 * @location : com.samsunglife.edirect.common.util.getLong
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : getLong
	 * @param str : String 문자열
	 * @param defaultVal : long 문자열 값이 null 일 경우 값
	 * @return long
	 */
	public static long getLong(String str, long defaultVal) {
		if (str == null)
			return defaultVal;

		try {
			return Long.parseLong(str.trim());
		} catch (NumberFormatException ne) {

		}

		return defaultVal;
	}

	/**
	 * @title : 문자열 long 형으로 변환
	 * @location : com.samsunglife.edirect.common.util.getLong
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : getLong
	 * @param str : String 문자열
	 * @return long
	 */
	public static long getLong(String str) {
		return getLong(str, 0);
	}

	/**
	 * @title : 대상값과 SIZE_NAME 배열의 값이 일치하는 배열 순번
	 * @location : com.samsunglife.edirect.common.util.getType
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : getType
	 * @param name : String
	 * @return int
	 */
	private static int getType(String name) {
		if ((name == null) || name.equals("")) {
			return 0;
		}

		for (int i = 0; i < 4; i++) {
			if (name.equalsIgnoreCase(SIZE_NAME[i])) {
				return i;
			}
		}

		return 0;
	}

	/**
	 * @title : 반올림 처리
	 * @location : com.samsunglife.edirect.common.util.banolim
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : banolim
	 * @param value : float value
	 * @param cut : float 반올림 자리수
	 * @return float
	 */
	private static float banolim(float value, float cut) {
		int temp = (int) ((value * 10) / cut);
		int bet = temp - ((temp / 10) * 10);

		if (bet < 5) {
			temp = temp - bet;
		} else {
			temp = temp - bet + 10;
		}

		temp = temp / 10;

		return temp * cut;
	}

	/**
	 * @title : 파일사이즈 단위 변환
	 * @location : com.samsunglife.edirect.common.util.sizeString
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : sizeString
	 * @param size : int source 단위에 해당되는 값
	 * @param size1 : String source 단위
	 * @param size2 : String target 단위
	 * @return String
	 */
	public static String sizeString(int size, String size1, String size2) {
		return sizeString((long) size, size1, size2);
	}

	/**
	 * @title : 파일사이즈 단위 변환
	 * @location : com.samsunglife.edirect.common.util.sizeString
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : sizeString
	 * @param size : long source 단위에 해당되는 값
	 * @param size1 : String source 단위
	 * @param size2 : String target 단위
	 * @return String
	 */
	public static String sizeString(long size, String size1, String size2) {
		return sizeString(size * SIZE_UNIT[getType(size1)], size2);
	}

	/**
	 * @title : 파일사이즈 단위 변환
	 * @location : com.samsunglife.edirect.common.util.sizeString
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : sizeString
	 * @param size : long source 단위에 해당되는 값
	 * @param name : String source 단위
	 * @return String
	 */
	private static String sizeString(long size, String name) {
		long div = SIZE_UNIT[getType(name)];
		float result = ((float) size) / div;

		if (result > 100) {
			result = banolim(result, 1);
		} else if (result > 10) {
			result = banolim(result, (float) 0.1);
		} else {
			result = banolim(result, (float) 0.01);
		}

		String res = new StringBuffer().append(result).toString();

		// inserted by gullbi 2001.10.18
		return setSosuFormat(res);

		// int idx = res.indexOf('.');
		// if(idx + 2 < res.length()) res = res.substring(0, idx + 3);
		// return res;
	}

	/**
	 * @title : print to second postion of decimal number format
	 * @location : com.samsunglife.edirect.common.util.setSosuFormat
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : setSosuFormat
	 * @param str : String
	 * @return String
	 */
	public static String setSosuFormat(String str) {
		int idx = str.indexOf(".");

		if (idx == -1) {
			str = str + ".00";
		} else if ((idx + 3) > str.length()) {
			str = str + "0";
		} else {
			str = str.substring(0, idx + 3);
		}

		return str;

		// logger.info(a[i]);
	}

	/**
	 * @title : 문자열 자르기
	 * @location : com.samsunglife.edirect.common.util.cutString
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : cutString
	 * @param srcString : String 문자열
	 * @param length : int 길이
	 * @return String
	 */
	public static String cutString(String srcString, int length) {
		String desString = "";

		if (srcString == null) {
			desString = "";
		} else {
			int srcLength = srcString.length();
			if (srcLength > length && length > 3) {
				desString = srcString.substring(0, length) + "...";
			} else {
				desString = srcString;
			}
		}

		return desString;
	}

	/**
	 * @title : Byte 단위로 문자열 자르기
	 * @location : com.samsunglife.edirect.common.util.cutStringByByte
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : cutStringByByte
	 * @param srcString : String 문자열
	 * @param length : int 길이
	 * @return String
	 */
	public static String cutStringByByte(String srcString, int length) {
		byte[] source;
		byte[] target;
		String desString = "";

		if (srcString == null) {
			return "";
		}

		try {
			source = srcString.getBytes("KSC5601");
		} catch (UnsupportedEncodingException e) {
			source = srcString.getBytes();
		}

		int srcLength = source.length;

		target = new byte[length];

		if (srcLength > length && length > 3) {
			System.arraycopy(source, 0, target, 0, length);
			try {
				desString = new String(target, "KSC5601");
			} catch (UnsupportedEncodingException e1) {
				desString = new String(target);
			}
			desString += "...";
		} else {
			desString = srcString;
		}

		return desString;
	}

	/**
	 * @title : 문자열 분리
	 * @location : com.samsunglife.edirect.common.util.split
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : split
	 * @param str : String 문자열
	 * @param delim : String 구분자
	 * @return String[]
	 */
	public static String[] split(String str, String delim) {
		return split(str, delim, true);
	}

	/**
	 * @title : 문자열 분리
	 * @location : com.samsunglife.edirect.common.util.split
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : split
	 * @param str : String 문자열
	 * @param delim : String 구분자
	 * @param ignoreEmpty : boolean 값이 없을때 처리
	 * @return String[]
	 */
	public static String[] split(String str, String delim, boolean ignoreEmpty) {

		if ((str == null) || str.equals("") || (delim == null) || delim.equals("")) {
			return null;
		}

		int start = 0;
		int end = 0;
		int delimLength = delim.length();
		ArrayList<String> list = new ArrayList<String>();
		String item = null;

		while ((end = str.indexOf(delim, start)) != -1) {
			item = str.substring(start, end);
			start = end + delimLength;
			if (ignoreEmpty && item.equals("")) {
				continue;
			}
			list.add(item);
		}

		if (start < str.length()) {
			item = str.substring(start);
			if (!ignoreEmpty || !item.equals("")) {
				list.add(item);
			}
		}

		String[] result = new String[list.size()];
		return ((String[]) list.toArray(result));
	}

	/**
	 * @title : 문자열 분리
	 * @location : com.samsunglife.edirect.common.util.split
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : split
	 * @param strData : String 문자열
	 * @param delim : String 구분자
	 * @param maxsize : 분리 최대 크기
	 * @return String[]
	 */
	public static String[] split(String strData, String delim, int maxsize) {
		boolean debug = false;
		int count = 0;
		String temp;
		String tmpValues[] = new String[maxsize];
		String pretoken = delim;

		for (int i = 0; i < tmpValues.length; i++) {
			tmpValues[i] = "";
		}

		StringTokenizer st = new StringTokenizer(strData, delim, true);
		while (st.hasMoreTokens()) {
			try {
				temp = st.nextToken();

				if (temp.equals(delim)) {
					if (pretoken.equals(delim)) {
						if (debug) {
//							logger.info("count=" + count + " token=" + temp + " pretoken=[" + pretoken + "]");
						}
						tmpValues[count] = "";
						pretoken = delim;
						count++;
					} else {
						pretoken = delim;
						continue;
					}
				} else {
					if (debug) {
//						logger.info("count=" + count + " token=" + temp + " pretoken=[" + pretoken + "]");
					}
					tmpValues[count] = temp;

					pretoken = tmpValues[count];
					count++;
				}
			} catch (NoSuchElementException nse) {
			}
		}

		return tmpValues;
	}

	/**
	 * @title : 문자열 치환
	 * @location : com.samsunglife.edirect.common.util.replaceold
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : replaceold
	 * @param mainString : String source 문자열
	 * @param oldString : String 대체할 문자열
	 * @param newString : String 대체 문자열
	 * @return String
	 */
	public static String replaceold(String mainString, String oldString, String newString) {
		if (mainString == null) {
			return null;
		}
		if (oldString == null || oldString.length() == 0) {
			return mainString;
		}
		if (newString == null) {
			newString = "";
		}

		int i = mainString.lastIndexOf(oldString);
		if (i < 0) {
			return mainString;
		}

		StringBuffer mainSb = new StringBuffer(mainString);

		while (i >= 0) {
			mainSb.replace(i, (i + oldString.length()), newString);
			i = mainString.lastIndexOf(oldString, i - 1);
		}
		return mainSb.toString();
	}

	/**
	 * @title : 문자열 치환
	 * @location : com.samsunglife.edirect.common.util.replace
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : replace
	 * @param sourceStr : String source 문자열
	 * @param source : String 대체할 문자열
	 * @param target : String 대체 문자열
	 * @return String
	 */
	public static String replace(String sourceStr, String source, String target) {
		if (null == sourceStr || "".equals(sourceStr)) {
			return "";
		}

		if (target == null) {
			target = "";
		}

		final StringBuffer result = new StringBuffer();
		int startIdx = 0;
		int idxOld = 0;
		while ((idxOld = sourceStr.indexOf(source, startIdx)) >= 0) {
			result.append(sourceStr.substring(startIdx, idxOld));
			result.append(target);
			startIdx = idxOld + source.length();
		}
		result.append(sourceStr.substring(startIdx));
		return result.toString();
	}

	/**
	 * @title : Number 포맷
	 * @location : com.samsunglife.edirect.common.util.numberFormat
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : numberFormat
	 * @param s : String 문자열
	 * @param i : int 소수점 이하 자리수
	 * @return String
	 */
	public static String numberFormat(String s, int i) {

		if (s == null || s.equals("") || s.equals("0"))
			return "-";
		if (i < 0)
			return s;

		StringBuffer stringbuffer = new StringBuffer();

		if (i > 0) {
			stringbuffer.append("###,###,##0");
			stringbuffer.append(".");
			for (int j = 0; j < i; j++)
				stringbuffer.append("0");

		} else {
			stringbuffer.append("###,###,###");
		}

		if (s.indexOf(".") == -1)
			s = s + ".0";

		DecimalFormat decimalformat = new DecimalFormat(stringbuffer.toString());
		return decimalformat.format(Double.parseDouble(s));
	}

	/**
	 * @title : Null 체크
	 * @location : com.samsunglife.edirect.common.util.isNull
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : isNull
	 * @param str : String 문자열
	 * @return String
	 */
	public static String isNull(String str) {
		return (str == null ? "" : str.trim());

	}

	/**
	 * @Title : Null 체크 후 값 변경
	 * @Method : isNull
	 * @Date : 2013. 6. 26. 오후 6:06:57
	 * @Location : com.samsunglife.edirect.common.util.StringUtil.isNull
	 * @param str
	 * @param replaceStr
	 * @return String
	 */
	public static String isNull(String str, String replaceStr) {
		return (str == null ? replaceStr : str.trim());

	}

	/**
	 * @title : 문자열의 왼쪽에 대체문자열 concat
	 * @location : com.samsunglife.edirect.common.util.lpad
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : lpad
	 * @param str : String 문자열
	 * @param len : int 전체길이
	 * @param addStr : String 추가문자열
	 * @return
	 */
	public static String lpad(String str, int len, String addStr) {
		String result = str;
		int templen = len - result.length();

		for (int i = 0; i < templen; i++) {
			result = addStr + result;
		}

		return result;
	}

	/**
	 * @title : Flag 설정
	 * @location : com.samsunglife.edirect.common.util.convertFlag
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : convertFlag
	 * @param flag : String 문자열
	 * @return
	 */
	public static String convertFlag(String flag) {
		if (("Y".equals(flag) || "".equals(flag.trim()))) {
			return "N";
		} else {
			return "Y";
		}
	}

	/**
	 * @title : 음양력 구분 플래그 변경
	 * @location : com.samsunglife.edirect.common.util.convertSLFlag
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : convertSLFlag
	 * @param value : String 문자열
	 * @return
	 */
	public static String convertSLFlag(String value) {

		String rtnVal = "";

		if ("01".equals(value)) {
			rtnVal = "S";
		} else if ("02".equals(value)) {
			rtnVal = "L";
		} else {
			rtnVal = value;
		}

		return rtnVal;
	}

	/**
	 * @title : 비정상적인 휴대폰번호 정형화
	 * @location : com.samsunglife.edirect.common.util.convertMobile
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : convertMobile
	 * @param mob
	 * @return
	 */
	public static String convertMobile(String mob) {

		if (!"".equals(mob) && mob != null) {
			mob = replace(mob, " ", "");
			if (mob.startsWith("082")) {
				mob = mob.substring(3);
			}
		}

		return mob;
	}

	/**
	 * @title : 휴대폰 형식 변환
	 * @location : com.samsunglife.edirect.common.util.formatMobile
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : formatMobile
	 * @param mob
	 * @return
	 */
	public static String formatMobile(String mob) {

		if (!"".equals(mob) && mob != null) {

			if (mob.length() == 10) {
				mob = mob.substring(0, 3) + "-" + mob.substring(3, 6) + "-" + mob.substring(6);
			} else if (mob.length() == 11) {
				mob = mob.substring(0, 3) + "-" + mob.substring(3, 7) + "-" + mob.substring(7);
			}
		} else {
			mob = "";
		}

		return mob;
	}

	/**
	 * @title : 특수문자거르기
	 * @location : com.samsunglife.edirect.common.util.removeSpecialChar
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : removeSpecialChar
	 * @param value
	 * @return
	 */
	public static String removeSpecialChar(String value) {

		if (!"".equals(value)) {

			for (int i = 0; i < specialChar.length; i++) {
				value = StringUtil.replace(value, specialChar[i], "");
			}
		}

		return value;
	}

	/**
	 * @title : 전각문자용
	 * @location : com.samsunglife.edirect.common.util.rtrim
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : rtrim
	 * @param s
	 * @return
	 */
	public static String rtrim(String s) {
		char[] val = s.toCharArray();
		int st = 0;
		int len = s.length();

		while (st < len && val[len - 1] <= '　') {
			len--;
		}

		return s.substring(0, len);
	}

	/**
	 * @title : 전각문자용
	 * @location : com.samsunglife.edirect.common.util.ltrim
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : ltrim
	 * @param s
	 * @return
	 */
	public static String ltrim(String s) {
		char[] val = s.toCharArray();
		int st = 0;
		int len = s.length();

		while (st < len && val[st] <= '　') {
			st++;
		}

		return s.substring(st, len);
	}

	/**
	 * @title : Stream 데이터를 String형태로 반환
	 * @location : com.samsunglife.edirect.common.util.ltrim
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : ltrim
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String streamToString(BufferedReader in) throws IOException {
		StringBuffer out = new StringBuffer();
		char[] b = new char[1024];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	/**
	 * @title : null Check
	 * @location : com.samsunglife.edirect.common.util.nvl
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : nvl
	 * @param str
	 * @return
	 */
	public static String nvl(String str) {
		if (str == null)
			return "";
		else
			return str;
	}

	/**
	 * @title : null Check
	 * @location : com.samsunglife.edirect.common.util.nvl
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : nvl
	 * @param strs
	 * @return
	 */
	public static String[] nvl(String[] strs) {
		String[] result = new String[strs.length];
		for (int i = 0; i < strs.length; i++) {
			if (strs[i] == null) {
				result[i] = "";
			} else {
				result[i] = strs[i];
			}
		}

		return result;
	}

	/**
	 * @title : null Check
	 * @location : com.samsunglife.edirect.common.util.nvl
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : nvl
	 * @param str
	 * @param rep
	 * @return
	 */
	public static String nvl(String str, String rep) {
		if (str == null)
			return rep;
		else
			return str;
	}

	/**
	 * @title : null Check
	 * @location : com.samsunglife.edirect.common.util.nvl
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : nvl
	 * @param obj
	 * @return
	 */
	public static String nvl(Object obj) {
		if (obj == null)
			return "";
		else
			return obj.toString();
	}

	/**
	 * @Title : null check
	 * @Method : convNullObj
	 * @Date : 2013. 9. 6. 오후 6:16:40
	 * @Location : com.samsunglife.edirect.common.util.StringUtil.convNullObj
	 * @param obj
	 * @param def
	 * @return
	 */
	public static String convNullObj(Object obj, String def) {
		if (def == null) {
			def = "";
		}
		if (obj == null || "".equals(obj)) {
			return def;
		}
		return obj.toString().trim();
	}

	/**
	 * @title : html tag 삭제
	 * @location : com.samsunglife.edirect.common.util.cleanXss
	 * @wireteDay : 2013. 6. 14. 오후 3:48:00
	 * @Method : cleanXss
	 * @param str
	 * @return
	 */
	public static String cleanXss(String str) {
		str = nvl(str);
//		logger.info(">>>>> cleanXss : " + str);
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("\"", "&quot;");
		str = str.replaceAll("\\<[^>]*>", "");
		return str;
	}

	/**
	 * 예정이율이 0.0375 의 형태로 오는데 이것을 3.75 의 형태로 변경하여 반환한다.
	 * @param str 입력값
	 * @return
	 */
	public static String getPercent(String str) {
		String val = "0";
		if (str == null || str.trim().equals("")) {
			return val;
		}

		double dbl = 0;
		dbl = Double.parseDouble(str);
		dbl = dbl * 100;

		val = "" + dbl;
		int len = val.substring(val.indexOf(".") + 1).length();
		if (len < 2) {
			for (int j = 0; (len + j) < 2; j++) {
				val += "0";
			}
		}
		return val;
	}

	/**
	 * @Title : 전체 문자중에 숫자만 파싱하여 반환
	 * @Method : strNumParse
	 * @Date : 2013. 8. 23. 오후 5:59:00
	 * @Location : com.samsunglife.edirect.common.util.StringUtil.strNumParse
	 * @param str
	 * @return
	 */
	public static String strNumParse(String str) {
		String numStr = "";
		for (int i = 0; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {
				numStr += str.charAt(i);
			}
		}
		return numStr;
	}

	/**
	 * 해시태그 클래스 리플레이스
	 * @param content
	 * @return
	 */
	public static String replaceHashTag(String content) {

		String patternStr = "\\#([0-9a-zA-Z가-힣%]*)";

		StringBuffer sb = new StringBuffer();
		try {
			Pattern compiledPattern = Pattern.compile(patternStr);
			Matcher matcher = compiledPattern.matcher(content);

			int count = 0;
			while (matcher.find()) {
				count++;
				String hashTag = matcher.group().trim();
				matcher.appendReplacement(sb, createTargetClass(hashTag));
			}
			if (count == 0) {
				return content;
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}

	/**
	 * @param hashTag
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String createTargetClass(String hashTag) throws UnsupportedEncodingException {
		String rtn = "";
		if (hashTag.equals("#")) {
			rtn = hashTag;
		} else {
			rtn = "<a class=\"hashTagLink\">" + hashTag + "</a>";
		}
		return rtn;
	}
	
	
	
	public static String replaceHashTagEx(String content) {

		String patternStr = "\\#([0-9a-zA-Z가-힣%]*)";

		StringBuffer sb = new StringBuffer();
		try {
			Pattern compiledPattern = Pattern.compile(patternStr);
			Matcher matcher = compiledPattern.matcher(content);

			int count = 0;
			while (matcher.find()) {
				count++;
				String hashTag = matcher.group().trim();
				matcher.appendReplacement(sb, createTargetClassEx(hashTag));
			}
			if (count == 0) {
				return content;
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}

	/**
	 * @param hashTag
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String createTargetClassEx(String hashTag) throws UnsupportedEncodingException {
		String rtn = "";
		if (hashTag.equals("#")) {
			rtn = hashTag;
		} else {
			rtn = "<a href=\"#\" class=\"hashTagLink\">" + hashTag + "</a>";
		}
		return rtn;
	}	
	
	
	/**
	 * 해시태그 List로 반환
	 * @param content
	 * @return
	 */
	public static List<String> listHashTag(String content) {

		String patternStr = "\\#([0-9a-zA-Z가-힣%]*)";

		List<String> list = new ArrayList<String>(); 
		
		StringBuffer sb = new StringBuffer();
		try {
			Pattern compiledPattern = Pattern.compile(patternStr);
			Matcher matcher = compiledPattern.matcher(content);

			int count = 0;
			while (matcher.find()) {
				count++;
				String hashTag = matcher.group().trim();
				matcher.appendReplacement(sb, hashTag);
				
				list.add(hashTag);
			}
			if (count == 0) {
				return list;
			}
		} catch (Exception e) {
		}
		return list;
	}
	

	/**
	 * 숫자인지 체크
	 * @param str
	 * @return
	 */
	public static boolean checkNumber(String str) {
		char check;

		if (str.equals("")) {
			// 문자열이 공백인지 확인
			return false;
		}

		for (int i = 0; i < str.length(); i++) {
			check = str.charAt(i);
			if (check < 48 || check > 58) {
				// 해당 char값이 숫자가 아닐 경우
				return false;
			}
		}
		return true;
	}

	/**
	 * ASC 문자를 UTF-8 로 변환
	 * @param str 대상 문자
	 * @return 대상 문자
	 */
	public static String a2k(final String str) {
		String rtn = null;
		try {
			rtn = (str == null) ? "" : new String(str.getBytes("8859_1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return rtn;
	}

	/**
	 * UTF-8 문자를 ASC 로 변환
	 * @param str 대상 문자
	 * @return 대상 문자
	 */
	public static String k2a(final String str) {
		String rtn = null;
		try {
			rtn = (str == null) ? "" : new String(str.getBytes("UTF-8"), "8859_1");
		} catch (UnsupportedEncodingException e) {
		}
		return rtn;
	}

	/**
	 * 문자열 암호화
	 * @param str
	 * @return
	 */
	public static String secretString(String str) {
		String passwordHash = null;
		try {
			byte[] raw = str.getBytes("UTF-8");
			byte[] hash = MessageDigest.getInstance("SHA").digest(raw);
			passwordHash = Base64.encodeBase64String(hash);
		} catch (Exception e) {
//			logger.error(Exception.class.getSimpleName(), e);
		}
		return passwordHash.toUpperCase().trim();
	}

	/**
	 * MD5 문자열 생성.
	 * @param str - 문자열
	 * @return
	 */
	public static String makeMD5(String str) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
//			logger.error(Exception.class.getSimpleName(), e);
		}
		md.update(str.getBytes());
		byte[] md5Code = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte element : md5Code) {
			String md5Char = String.format("%02x", 0xff & (char) element);
			sb.append(md5Char);
		}
		return sb.toString().toUpperCase().trim();
	}
	
	
	// 금액 자리수 -1 자리에서 올림처리 (차트용) 
	// ex) 7697000 > 7700000
	// ex) 12110 > 13000
	public static long numberRoundChart(long num) {
		long returnNum = 0 ;
		
		if (num > 99999999 ){
			returnNum = (num / 10000000) * 10000000 + 10000000;
		}else if (num > 9999999 ){
			returnNum = (num / 1000000) * 1000000 + 1000000;
		}else if (num > 999999 ){
			returnNum = (num / 100000) * 100000 + 100000;
		}else if (num > 99999 ){
			returnNum = (num / 10000) * 10000 + 10000;
		}else if (num > 9999 ){
			returnNum = (num / 1000) * 1000 + 1000;
		}else if (num > 999 ){
			returnNum = (num / 100) * 100 + 100;
		}else if (num > 99 ){
			returnNum = (num / 10) * 10 + 10;
		}else{
			returnNum = num;
		}
		
		return returnNum;
	}

	/**
	 * 문자열 empty 여부 확인
	 * null 또는 "" 이면, false
	 * 그 외에는 true.
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str==null || "".equals(str);
	}
	
	public static String arrayToString(String args[]) { 
		if (args == null){
			return "";
		}
	    StringBuilder builder = new StringBuilder(); 
	    for (int i = 0; i < args.length;) { 
	        builder.append(args[i]); 
	        if (++i < args.length) { 

	            builder.append(","); 
	        } 
	    } 
	    return builder.toString(); 
	} 	
}
