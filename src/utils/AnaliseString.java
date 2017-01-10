package utils;

import java.text.Normalizer;

public class AnaliseString {

    public static String normalizeLower(String value) {

        value = Normalizer.normalize(value, Normalizer.Form.NFD);
        value = value.toLowerCase().replaceAll("[^\\p{ASCII}]", "");

        return value;
    }

    public static String normalizeUpper(String value) {

        value = Normalizer.normalize(value, Normalizer.Form.NFD);
        value = value.toUpperCase().replaceAll("[^\\p{ASCII}]", "");

        return value;
    }

}
