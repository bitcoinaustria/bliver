package at.bitcoinaustria.bliver.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import at.bitcoinaustria.bliver.Bitcoins;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class DeliveryDao {

    private final SqlHelper sqlHelper;

    public DeliveryDao(Context context) {
        this.sqlHelper = new SqlHelper(context);
    }

    public List<Delivery> getAll() {
        final SQLiteDatabase db = sqlHelper.getReadableDatabase();
        final Cursor activeCursor = db.rawQuery("SELECT * " +
                "FROM delivery " +
                "WHERE order_status NOT IN ('RECEIVED_TRANSACTION', 'CANCELED') " +
                "ORDER BY id DESC", new String[]{});
        final List<Delivery> activeDeliveries = createListFromCursor(activeCursor);
        activeCursor.close();

        final Cursor passiveCursor = db.rawQuery("SELECT * " +
                "FROM delivery " +
                "WHERE order_status IN ('RECEIVED_TRANSACTION', 'CANCELED') " +
                "ORDER BY id DESC", new String[]{});
        final List<Delivery> passiveDeliveries = createListFromCursor(passiveCursor);
        passiveCursor.close();

        db.close();

        final ArrayList<Delivery> all = Lists.newArrayListWithExpectedSize(activeDeliveries.size() + passiveDeliveries.size());
        all.addAll(activeDeliveries);
        all.addAll(passiveDeliveries);
        return all;
    }

    public Delivery getById(Long id) {
        final SQLiteDatabase db = sqlHelper.getReadableDatabase();
        final Cursor cursor = db.rawQuery("SELECT * " +
                "FROM delivery " +
                "WHERE id=" + id, new String[]{});

        cursor.moveToNext();
        final Delivery delivery = createFromCursor(cursor);
        cursor.close();

        db.close();

        return delivery;
    }

    public void save(Delivery delivery) {
        final SQLiteDatabase db = sqlHelper.getWritableDatabase();
        save(db, delivery);
        db.close();
    }

    public void save(SQLiteDatabase db, Delivery delivery) {
        if (delivery.getId() == null) {
            db.insert("delivery", null, serializeForCursor(delivery));
        } else {
            db.update("delivery", serializeForCursor(delivery), "id = " + delivery.getId(), null);
        }

        final Cursor cursor = db.rawQuery("SELECT LAST_INSERT_ROWID()", new String[]{});
        cursor.moveToNext();
        delivery.setId(cursor.getLong(0));
        cursor.close();
    }

    private List<Delivery> createListFromCursor(Cursor cursor) {
        final ArrayList<Delivery> deliveries = new ArrayList<Delivery>();

        while (cursor.moveToNext()) {
            deliveries.add(createFromCursor(cursor));
        }

        return deliveries;
    }

    private ContentValues serializeForCursor(Delivery delivery) {
        final ContentValues fields = new ContentValues();

        fields.put("server_url", delivery.getServerUrl());
        fields.put("order_id", delivery.getOrderId());
        fields.put("order_description", delivery.getOrderDescription());
        fields.put("order_status", delivery.getOrderStatus().name());
        fields.put("vendor", delivery.getVendor().name());
        fields.put("amount", delivery.getAmount().toBigInteger().longValue());
        fields.put("multisig_address", delivery.getMultisigAddress());
        fields.put("tx_input_hash", delivery.getTxInputHash());
        fields.put("tx_id", delivery.getTxId());

        return fields;
    }

    private Delivery createFromCursor(Cursor cursor) {
        final Delivery delivery = new Delivery();

        delivery.setId(cursor.getLong(cursor.getColumnIndex("id")));
        delivery.setServerUrl(cursor.getString(cursor.getColumnIndex("server_url")));
        delivery.setOrderId(cursor.getString(cursor.getColumnIndex("order_id")));
        delivery.setOrderDescription(cursor.getString(cursor.getColumnIndex("order_description")));
        delivery.setOrderStatus(OrderStatus.valueOf(cursor.getString(cursor.getColumnIndex("order_status"))));
        delivery.setVendor(Vendor.valueOf(cursor.getString(cursor.getColumnIndex("vendor"))));
        delivery.setAmount(Bitcoins.valueOf(cursor.getLong(cursor.getColumnIndex("amount"))));
        delivery.setMultisigAddress(cursor.getString(cursor.getColumnIndex("multisig_address")));
        delivery.setTxInputHash(cursor.getString(cursor.getColumnIndex("tx_input_hash")));
        delivery.setTxId(cursor.getString(cursor.getColumnIndex("tx_id")));

        return delivery;
    }

    public Delivery getByOrderId(String orderId) {
        final SQLiteDatabase db = sqlHelper.getReadableDatabase();
        final Cursor cursor = db.rawQuery("SELECT * " +
                "FROM delivery " +
                "WHERE order_id=?" , new String[]{orderId});

        cursor.moveToNext();
        final Delivery delivery = createFromCursor(cursor);
        cursor.close();
        db.close();
        return delivery;
    }

}
