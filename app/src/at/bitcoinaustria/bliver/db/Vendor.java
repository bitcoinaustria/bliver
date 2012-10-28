package at.bitcoinaustria.bliver.db;

import at.bitcoinaustria.bliver.R;

public enum Vendor {
    EBAY(R.drawable.ic_vendor_ebay),
    AMAZON(R.drawable.ic_vendor_amazon),
    ALIBABA(R.drawable.ic_vendor_ali),
    DEALEXTREME(R.drawable.ic_vendor_dx),
    GENERIC(R.drawable.ic_vendor_generic);
    private final int iconId;

    Vendor(int iconId) {
        this.iconId = iconId;
    }

    public int getIconId() {
        return iconId;
    }

}
