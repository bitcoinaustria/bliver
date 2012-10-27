package at.bitcoinaustria.bliver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class PackageListActivity extends FragmentActivity
        implements PackageListFragment.Callbacks {

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
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(PackageDetailFragment.ARG_ITEM_ID, id);
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
}
