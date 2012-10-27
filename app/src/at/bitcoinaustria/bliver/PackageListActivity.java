package at.bitcoinaustria.bliver;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class PackageListActivity extends FragmentActivity implements PackageListFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_package_list);

        if (findViewById(R.id.package_detail_container) != null) {
            mTwoPane = true;
            ((PackageListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.package_list))
                    .setActivateOnItemClick(true);
        }

        final Intent intent = getIntent();
        final Uri data = intent.getData();
        if (data != null) {
            new AsyncTask<Void,Void,String>(){
                @Override
                protected String doInBackground(Void... params) {
                    final MultisigUri multisigUri = MultisigUri.from(data.toString());
                    return new MultisigUriHandler(Signer.DEMO_SIGNER).fromMultisigUri(multisigUri);
                }

                @Override
                protected void onPostExecute(String bitcoinURI) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(bitcoinURI));
                    startActivity(i);
                }
            }.execute();

            int debug = 0;
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
