package de.shoppinglist.android.helper;

import java.util.HashMap;
import java.util.Map;

public class TranslateUmlauts {

	static Map<String, String> umlautsWithTranslations;

	private static void fillMapWithUmlauts() {
		TranslateUmlauts.umlautsWithTranslations = new HashMap<String, String>();
//		TranslateUmlauts.umlautsWithTranslations.put("�, "&auml;");
//		TranslateUmlauts.umlautsWithTranslations.put("�, "&Auml;");
//		TranslateUmlauts.umlautsWithTranslations.put("�, "&ouml;");
//		TranslateUmlauts.umlautsWithTranslations.put("�, "&Ouml;");
//		TranslateUmlauts.umlautsWithTranslations.put("�, "&uuml;");
//		TranslateUmlauts.umlautsWithTranslations.put("�, "&Uuml;");
//		TranslateUmlauts.umlautsWithTranslations.put("�, "&szlig;");
	}

	/**
	 * 
	 * 
	 * <p>
	 * Translates the german umlauts in a string in html-umlauts.
	 * </p>
	 * 
	 * @param stringToReplaceUmlauts
	 * 
	 * @return stringWithHtmlUmlauts
	 */
	public static String translateFromGermanUmlauts(String stringToReplaceUmlauts) {
		TranslateUmlauts.fillMapWithUmlauts();

		for (final String germanUmlaut : TranslateUmlauts.umlautsWithTranslations.keySet()) {
			stringToReplaceUmlauts = stringToReplaceUmlauts.replace(germanUmlaut,
					TranslateUmlauts.umlautsWithTranslations.get(germanUmlaut));
		}
		return stringToReplaceUmlauts;
	}

	/**
	 * 
	 * <p>
	 * Translates the html umlauts in a string in german umlauts.
	 * </p>
	 * 
	 * @param stringToReplaceUmlauts
	 * 
	 * @return stringWithGermanUmlauts
	 */
	public static String translateIntoGermanUmlauts(String stringToReplaceUmlauts) {
		TranslateUmlauts.fillMapWithUmlauts();

		for (final String germanUmlaut : TranslateUmlauts.umlautsWithTranslations.keySet()) {
			stringToReplaceUmlauts = stringToReplaceUmlauts.replace(TranslateUmlauts.umlautsWithTranslations.get(germanUmlaut),
					germanUmlaut);
		}
		return stringToReplaceUmlauts;
	}
}
