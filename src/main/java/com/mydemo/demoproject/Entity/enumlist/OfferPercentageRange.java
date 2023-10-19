package com.mydemo.demoproject.Entity.enumlist;

public enum OfferPercentageRange {
    RANGE_1_TO_10("1-10", "1% to 10%"),
    RANGE_10_TO_20("10-20", "10% to 20%"),
    RANGE_20_TO_30("20-30", "20% to 30%"),
    RANGE_30_TO_40("30-40", "30% to 40%"),
    RANGE_40_TO_50("40-50", "40% to 50%"),
    RANGE_50_TO_60("50-60", "50% to 60%"),
    RANGE_60_TO_ABOVE("60-100", "60% and above");

    private final String value;
    private final String label;

    OfferPercentageRange(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
