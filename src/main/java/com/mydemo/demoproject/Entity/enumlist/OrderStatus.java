package com.mydemo.demoproject.Entity.enumlist;

public enum OrderStatus {

    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    RETURN("Return"),
    CANCELED("Canceled");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
