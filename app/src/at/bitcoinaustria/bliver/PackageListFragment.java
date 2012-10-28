package at.bitcoinaustria.bliver;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import at.bitcoinaustria.bliver.db.Delivery;
import at.bitcoinaustria.bliver.db.DeliveryDao;

import java.util.List;

public class PackageListFragment extends ListFragment {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private Callbacks mCallbacks = sDeliveryCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private DeliveryDao deliveryDao;
    private DeliveryArrayAdapter listAdapter;

    public interface Callbacks {
        public void onItemSelected(Long id);
    }

    private static Callbacks sDeliveryCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Long id) {
        }
    };

    public PackageListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.deliveryDao = new DeliveryDao(getActivity());

        final List<Delivery> items = deliveryDao.getAll();
        listAdapter = new DeliveryArrayAdapter(getActivity(),
                R.layout.simple_list_item_activated_1,
                R.id.text1,
                items);
        setListAdapter(listAdapter);
    }

    public void refreshFromDb() {
        final List<Delivery> items = deliveryDao.getAll();
        listAdapter.clear();
        listAdapter.addAll(items);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState
                .containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDeliveryCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        final List<Delivery> items = deliveryDao.getAll();
        mCallbacks.onItemSelected(items.get(position).getId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    public void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    private class DeliveryArrayAdapter extends ArrayAdapter<Delivery> {

        public DeliveryArrayAdapter(Context context, int resource, int textViewResourceId, List<Delivery> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TextView item = (TextView) super.getView(position, convertView, parent);
            final Delivery delivery = this.getItem(position);
            item.setCompoundDrawablesWithIntrinsicBounds(delivery.getVendor().getIconId(), 0, 0, 0);
            item.setCompoundDrawablePadding(10);
            return item;
        }
    }

}
