package ru.barabo.total.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

final public class StrUtils {

	final static transient private Logger logger = Logger.getLogger(StrUtils.class.getName());

	static public boolean isCyrillic(String value) {
		return Pattern.compile("[а-яА-ЯёЁ]").matcher(value).find();
	}

	private static boolean isCyrillicUppper(char ch) {
		return (ch >= 'А' && ch <= 'Я');
	}

	static public String getDigitsOnly(String value) {
		return value == null ? null : value.replaceAll("\\D+", "");
	}

	static public String getAlphaDigitOnly(String value) {
		return value == null ? null : value.replaceAll("[^a-zA-Z0-9а-яА-ЯёЁ]", "").toLowerCase();
	}

	private static final Map<Character, Character> LAT_TO_CYR = new HashMap<>();

	static {
		// first Lat second Cyr
		LAT_TO_CYR.put('A', 'А');
		LAT_TO_CYR.put('B', 'В');
		LAT_TO_CYR.put('E', 'Е');
		LAT_TO_CYR.put('K', 'К');
		LAT_TO_CYR.put('M', 'М');
		LAT_TO_CYR.put('H', 'Н');
		LAT_TO_CYR.put('O', 'О');
		LAT_TO_CYR.put('P', 'Р');
		LAT_TO_CYR.put('C', 'С');
		LAT_TO_CYR.put('T', 'Т');
		LAT_TO_CYR.put('X', 'Х');
		LAT_TO_CYR.put('Y', 'У');
	}

	static public String latinToCyrillicGosNumber(String latin) {
		if (latin == null) {
			return null;
		}

		latin = latin.trim().toUpperCase();

		StringBuilder cyrillicDigit = new StringBuilder();

		for (int i = 0; i < latin.length(); i++) {

			char ch = latin.charAt(i);

			if (Character.isDigit(ch) || isCyrillicUppper(ch)) {
				cyrillicDigit.append(ch);
				continue;
			}

			logger.info("ch=" + ch);

			logger.info("ch=" + ch);

			Character charFromMap = LAT_TO_CYR.get(ch);
			if (charFromMap != null) {
				cyrillicDigit.append(charFromMap);
			}
		}
		return cyrillicDigit.toString();
	}
}
