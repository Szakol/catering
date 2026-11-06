package pl.piatek.catering.service;

import java.security.SecureRandom;
import java.util.Locale;

public final class OrderCodes {

    // bez znaków mylących się przy przepisywaniu: 0, O, 1, I, L
    private static final String ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    private static final int LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    private OrderCodes() {
    }

    /** Losowy kod, np. "7K4Q9XMR" (31^8, czyli ok. 850 miliardów kombinacji). */
    public static String generate() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    /** "7K4Q9XMR" -> "7K4Q-9XMR" (postać pokazywana ludziom). */
    public static String format(String code) {
        if (code == null || code.length() <= 4) {
            return code;
        }
        return code.substring(0, 4) + "-" + code.substring(4);
    }

    /** "7k4q-9xmr" albo " 7K4Q 9XMR " -> "7K4Q9XMR" (do wyszukiwania). */
    public static String normalize(String input) {
        return input == null ? "" : input.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
    }
}
