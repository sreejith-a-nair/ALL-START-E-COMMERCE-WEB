package com.mydemo.demoproject.Entity.enumlist;


public enum BannerType {
    Main_Banner("Main Banner"),
//    SMALL_BANNER("SMALL BANNER");
    OFFER_BANNER("Offer banner"),


    YONEX("Yonex"),
    LINING("Li-ning"),
    VICTOR("Victor");
    private final String displayName;

    BannerType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
