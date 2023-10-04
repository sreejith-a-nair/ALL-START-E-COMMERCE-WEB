package com.mydemo.demoproject.Entity.enumlist;

public enum PriceRange {

    RANGE_0_500("0-500", "below  Rs.500"),
    RANGE_500_1500("500-1500", "Rs.500 to Rs.1500"),
    RANGE_1500_2500("1500-2500", "Rs.1500 to Rs.2500"),
    RANGE_2500_5000("2500-5000", "Rs.2500 to Rs.5000"),
    RANGE_5000_10000("5000-10000", "Rs.5000 to Rs.10000"),
    RANGE_10000_25000("10000-25000", "Rs.10000 to Rs.25000"),
    RANGE_25000_50000("25000-50000", "Rs.25000 to Rs.50000"),
    RANGE_ABOVE_50000("50000-100000", "Rs.50000 and above");

    private final String value;
    private final String label;

    PriceRange(String value, String label) {
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
