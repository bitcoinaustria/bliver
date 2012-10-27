package at.bitcoinaustria.bliver;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import at.bitcoinaustria.bliver.db.Delivery;
import at.bitcoinaustria.bliver.db.DeliveryDao;
import com.google.bitcoin.core.Address;
import com.google.bitcoin.uri.BitcoinURI;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
            new AsyncTask<Void, Void, MultisigWrapper>() {
                @Override
                protected MultisigWrapper doInBackground(Void... params) {
                    final MultisigUri multisigUri = MultisigUri.from(incomingIntentUrl.toString());
                    String intentUri = new MultisigUriHandler(Signer.DEMO_SIGNER).fromMultisigUri(multisigUri);
                    Address address = new BitcoinURI(intentUri).getAddress();
                    Delivery delivery = new Delivery(multisigUri, address);
                    deliveryDao.save(delivery);
                    return new MultisigWrapper(delivery, intentUri);
                }

                @Override
                protected void onPostExecute(MultisigWrapper data) {
                    fragment.addDelivery(data.delivery);
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(data.outgoingIntentUrl));
                    startActivity(i);
                }
            }.execute();
        }
    }


    private static class MultisigWrapper {
        private final Delivery delivery;
        private final String outgoingIntentUrl;

        private MultisigWrapper(Delivery delivery, String outgoingIntentUrl) {
            this.delivery = delivery;
            this.outgoingIntentUrl = outgoingIntentUrl;
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
            //scanResult.getContents()
            // TODO
        }
    }

}
