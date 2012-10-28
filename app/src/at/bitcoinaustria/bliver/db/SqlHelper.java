package at.bitcoinaustria.bliver.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import at.bitcoinaustria.bliver.Bitcoins;

public class SqlHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "bliver";
    private static final int DB_VERSION = 1;
    private static final String DB_CREATE = "CREATE TABLE delivery (\n" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
            "server_url TEXT, \n" +
            "order_id TEXT, \n" +
            "order_description TEXT, \n" +
            "order_status TEXT, \n" +
            "vendor TEXT, \n" +
            "amount TEXT, \n" +
            "multisig_address TEXT, \n" +
            "tx_input_hash TEXT, \n" +
            "tx_id TEXT\n" +
            ");";

    private final Context context;

    public SqlHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_CREATE);

        final DeliveryDao deliveryDao = new DeliveryDao(context);
        deliveryDao.save(sqLiteDatabase, new Delivery(
                "http://10.200.1.73/bliver/multisig.txt",
                "123",
                "testbestellung 123",
                OrderStatus.AWAITING_DELIVERY,
                Vendor.EBAY,
                Bitcoins.valueOf(300000000L),
                "",
                "",
                ""
        ));
        deliveryDao.save(sqLiteDatabase, new Delivery(
                "http://10.200.1.73/bliver/multisig.txt",
                "120",
                "Amazon Kindle Order",
                OrderStatus.AWAITING_DELIVERY,
                Vendor.AMAZON,
                Bitcoins.valueOf(1200000000L),
                "",
                "",
                ""
        ));
        deliveryDao.save(sqLiteDatabase, new Delivery(
                "http://10.200.1.73/bliver/multisig.txt",
                "99",
                "MyPad Order",
                OrderStatus.AWAITING_DELIVERY,
                Vendor.ALIBABA,
                Bitcoins.valueOf(600000000L),
                "",
                "",
                ""
        ));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException("not implemented");
    }

}
