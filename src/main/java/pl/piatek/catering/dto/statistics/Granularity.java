package pl.piatek.catering.dto.statistics;

public enum Granularity {
    DAY("YYYY-MM-DD"), MONTH("YYYY-MM");

    private final String format;

    Granularity(String format) {
        this.format = format;
    }

    public String format() {
        return format;
    }
}