package com.popdq.app.values;

/**
 * Created by Dang Luu on 21/07/2016.
 */
public class LanguageCommon {

    public final static String[] LANGUAGE = {"Tiếng Việt", "English"};
    public final static String[] LANGUAGE_CODE = {"vi", "en"};

    public static String getLanguageFromCode(String code) {
        int size = LANGUAGE_CODE.length;
        for (int i = 0; i < size; i++) {
            if (LANGUAGE_CODE[i].equalsIgnoreCase(code)) {
                return LANGUAGE[i];
            }
        }
        return "";
    }
}
