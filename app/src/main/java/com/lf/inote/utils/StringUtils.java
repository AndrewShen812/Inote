package com.lf.inote.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 字符串处理
 * 
 * @author genie
 * 
 */
public class StringUtils {

	/**
	 * @Description: 判断String是否为空,true代表空，false代表非空
	 * @param @param string
	 * @return boolean
	 * @throws
	 */
	public static boolean isEmpty(String string) {
		if (string == null) {
			return true;
		}
		else if (string.trim().equalsIgnoreCase("")) {
			return true;
		}
		return false;
	}

	/**
	 * 返回两个字符串中间的内容
	 * 
	 * @param all
	 * @param start
	 * @param end
	 * @return
	 */
	public static String getMiddleString(String all, String start, String end) {
		int beginIdx = all.indexOf(start) + start.length();
		int endIdx = all.indexOf(end);
		return all.substring(beginIdx, endIdx);
	}

	/**
	 * 判定输入汉字
	 * 
	 * @param c
	 * @return
	 */
	public boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 判定输入数字
	 * 
	 * @param c
	 * @return
	 */
	public boolean isNumber(char c) {
		if (c >= '0' && c <= '9') {
			return true;
		}
		return false;
	}

	/**
	 * 判定输入字母
	 * 
	 * @param c
	 * @return
	 */
	public boolean isWorld(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			return true;
		}
		return false;
	}

	/**
	 * Capitalize the first letter
	 * 
	 * @param s
	 *            model,manufacturer
	 * @return Capitalize the first letter
	 */
	public static String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		}
		else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	/**
	 * @Description: 验证码
	 * @param @param passworld 密码
	 * @return boolean true密码验证通过 false密码验证不通过
	 * @throws
	 */
	public static boolean pwdVerification(String passworld) {
		if (StringUtils.isEmpty(passworld) || StringUtils.isEmpty(passworld.trim())) {
			return false;
		}
		passworld = passworld.trim();
		if (passworld.length() < 6 || passworld.length() > 20) {
			return false;
		}
		String regex = "[0-9a-zA-Z]+$";
		return passworld.matches(regex);
	}


	/**
	 * InputStream转String
	 */
	public static String convertStreamToString(InputStream is) {
		if (is == null) {
			return "";
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "/n");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				is.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
