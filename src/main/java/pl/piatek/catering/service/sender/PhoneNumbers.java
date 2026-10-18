package pl.piatek.catering.service.sender;

public final class PhoneNumbers {

    private PhoneNumbers() {
    }

    /** "600 100 200" -> "+48600100200", "0048600100200" -> "+48600100200". */
    public static String normalize(String raw) {
        String s = raw.replaceAll("[^0-9+]", "");
        if (s.startsWith("00")) {
            s = "+" + s.substring(2);
        }
        if (!s.startsWith("+") && s.length() == 9) {
            s = "+48" + s;
        }
        return s;
    }
}
