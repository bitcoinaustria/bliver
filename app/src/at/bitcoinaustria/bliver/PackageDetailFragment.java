package at.bitcoinaustria.bliver;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import at.bitcoinaustria.bliver.db.Delivery;
import at.bitcoinaustria.bliver.db.DeliveryDao;

public class PackageDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "delivery_id";

    Delivery mItem;
    DeliveryDao deliveryDao;

    public PackageDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.deliveryDao = new DeliveryDao(getActivity());

        if (getActivity().getIntent().hasExtra(ARG_ITEM_ID)) {
            mItem = deliveryDao.getById(getActivity().getIntent().getLongExtra(ARG_ITEM_ID, -1));
        } else if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = deliveryDao.getById(getArguments().getLong(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_package_detail, container, false);
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.package_detail)).setText(mItem.getOrderDescription());
        }
        return rootView;
    }
}
