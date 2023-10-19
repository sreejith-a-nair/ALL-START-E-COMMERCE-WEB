package com.mydemo.demoproject.Entity.enumlist;

public enum ReportFrequency {

    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly");

    private final String label;

    ReportFrequency(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
