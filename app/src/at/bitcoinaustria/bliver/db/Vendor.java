package at.bitcoinaustria.bliver.db;

import at.bitcoinaustria.bliver.R;

public enum Vendor {
    EBAY("ebay", R.drawable.ic_vendor_ebay),
    AMAZON("Amazon", R.drawable.ic_vendor_amazon),
    ALIBABA("Ali Baba", R.drawable.ic_vendor_ali),
    DEALEXTREME("Deal Extreme", R.drawable.ic_vendor_dx),
    GENERIC("Unknown vendor", R.drawable.ic_vendor_generic);

    private final String caption;
    private final int iconId;

    Vendor(String caption, int iconId) {
        this.caption = caption;
        this.iconId = iconId;
    }

    public String getCaption() {
        return caption;
    }

    public int getIconId() {
        return iconId;
    }

}
