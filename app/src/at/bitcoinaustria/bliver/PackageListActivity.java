package at.bitcoinaustria.bliver;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import at.bitcoinaustria.bliver.db.Delivery;
import at.bitcoinaustria.bliver.db.DeliveryDao;
import at.bitcoinaustria.bliver.db.Vendor;
import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Address;
import com.google.bitcoin.uri.BitcoinURI;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Random;

public class PackageListActivity extends FragmentActivity implements PackageListFragment.Callbacks {

    private boolean mTwoPane;
    private DeliveryDao deliveryDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deliveryDao = new DeliveryDao(this);
        setContentView(R.layout.activity_package_list);

        final PackageListFragment fragment = (PackageListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.package_list);

        if (findViewById(R.id.package_detail_container) != null) {
            mTwoPane = true;
            fragment.setActivateOnItemClick(true);
        }

        final Intent intent = getIntent();
        final Uri incomingIntentUrl = intent.getData();

        if (incomingIntentUrl != null) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    final Vendor randomVendor = Vendor.values()[new Random().nextInt(Vendor.values().length)];
                    final MultisigUri multisigUri = MultisigUri.from(incomingIntentUrl.toString());
                    String intentUri = new MultisigUriHandler(Signer.DEMO_SIGNER).fromMultisigUri(multisigUri);
                    Address address = new BitcoinURI(intentUri).getAddress();
                    Delivery delivery = new Delivery(multisigUri, address, randomVendor);
                    deliveryDao.save(delivery);
                    return intentUri;
                }

                @Override
                protected void onPostExecute(String outgoingIntentUrl) {
                    fragment.refreshFromDb();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(outgoingIntentUrl));
                    startActivity(i);
                }
            }.execute();
        }
    }

    @Override
    public void onItemSelected(Long id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putLong(PackageDetailFragment.ARG_ITEM_ID, id);
            PackageDetailFragment fragment = new PackageDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.package_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, PackageDetailActivity.class);
            detailIntent.putExtra(PackageDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.scan_action) {
            final IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            BasicTask task = new BasicTask() {
                @Override
                void run() {
                    Signer demo_signer = Signer.DEMO_SIGNER;
/*                    String contents = scanResult.getContents();
                    byte[] base64Decode = Coder.base64Decode(contents);
                    Transaction signed;

                    try {
                        Transaction transaction = new Transaction(Net.NETWORK, base64Decode);
                        //  signed = new SingleKeySigner(demo_signer).signed(transaction);
                    } catch (ProtocolException e) {
                        throw new RuntimeException(e);
                    }*/
                /*    Delivery delivery = deliveryDao.getByOrderId("123");
                    MultisigUri multisigUri = new MultisigUri(delivery);*/
                    // new MultisigUriHandler(Signer.DEMO_SIGNER).broadcastTransaction(multisigUri,signed);
                    new MultisigUriHandler(demo_signer).broadcastTransaction();
                }
            };
            task.start();
        }
    }

}
