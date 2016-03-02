package com.webcab.elit;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.webcab.elit.adapters.AutoCompleteLoadding;
import com.webcab.elit.adapters.HomeAdapter;
import com.webcab.elit.adapters.StreetAdapter;
import com.webcab.elit.data.house;
import com.webcab.elit.data.street;

import com.webcab.elit.Utils.OnSwipeTouchListener;

/**
 * Created by Sergey on 18.08.2015.
 */
public class AddressFragment extends Fragment {

    Context mContext;


    public String currentStreetName;
    public String currentStreetID;
    public String currentHouseID;
    public String currentHouseNumber;
    public String currentGeoX;
    public String currentGeoY;

    HomeAdapter hAdapter;
    StreetAdapter stAdapter;
    house selectHouse;
    boolean aut2_selected;

    TextView strIDTxt, geoxTxt, geoyTxt, houseID;
    ImageView deleteAddressImage;

    AutoCompleteLoadding aut1, aut2;

    public interface onAddressActionListener {
        public void deleteAddress(int id);

        public void moveUp(int id);

        public void moveDown(int id);
    }

    onAddressActionListener addressAction;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            addressAction = (onAddressActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    public AddressFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.address_item_editable, container,
                false);

        mContext = getActivity();

        strIDTxt = (TextView) rootView.findViewById(R.id.street_id);
        geoxTxt = (TextView) rootView.findViewById(R.id.geox);
        geoyTxt = (TextView) rootView.findViewById(R.id.geoy);
        houseID = (TextView) rootView.findViewById(R.id.houseID);
        deleteAddressImage = (ImageView) rootView.findViewById(R.id.imageView9);

        ProgressBar pr_str = (ProgressBar) rootView.findViewById(R.id.pr_str);
        ProgressBar pr_dom = (ProgressBar) rootView.findViewById(R.id.pr_dom);

        aut1 = (AutoCompleteLoadding) rootView.findViewById(R.id.aut1);
        aut1.setLoadingIndicator(pr_str);
        stAdapter = new StreetAdapter(mContext, R.layout.street_item, R.id.txt_year);
        aut1.setAdapter(stAdapter);

        aut2 = (AutoCompleteLoadding) rootView.findViewById(R.id.aut2);
        aut2.setLoadingIndicator(pr_dom);
        aut2.setThreshold(1);
        aut2.setEnabled(true);
        //aut2.addTextChangedListener(this);

        aut1.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                android.util.Log.d("setOnItemClickListener", "test");
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    street st = null;
                    st = (street) aut1.getAdapter().getItem(0);
                    if (st != null) {
                        //aut1.setText("" + st.getName());
                        aut1.setText(st.getName());
                        currentStreetName = st.getName();
                        currentStreetID = st.getId();
                        currentGeoX = st.getGeox();
                        currentGeoY = st.getGeoy();

                        aut1.dismissDropDown();

                        hAdapter = new HomeAdapter(mContext, R.layout.street_item, R.id.txt_year, currentStreetID);
                        hAdapter.registerDataSetObserver(new DataSetObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                if (aut2.getDropDownAnchor() != 0)
                                    selectHouse = hAdapter.getItem(0);
                            }
                        });
                        aut2.setAdapter(hAdapter);
                        aut2.setEnabled(true);
                        aut2.setText("");
                        aut2.requestFocus();
                        aut2_selected = false;
                    }
                    return false;
                }
                return false;
            }
        });
        aut1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Object obj = arg0.getItemAtPosition(arg2); //должно быть "com.webcab.elit.data.street"
                Adapter mAdap = arg0.getAdapter();
                street st1 = (street) mAdap.getItem(arg2);
                street st = (street) arg0.getItemAtPosition(arg2);
                if (st != null) {
                    currentStreetName = st.getName();
                    aut1.setText(currentStreetName);
                    currentStreetID = st.getId();
                    currentGeoX = st.getGeox();
                    currentGeoY = st.getGeoy();

                    hAdapter = new HomeAdapter(mContext, R.layout.street_item, R.id.txt_year, currentStreetID);
                    hAdapter.registerDataSetObserver(new DataSetObserver() {
                        @Override
                        public void onChanged() {
                            super.onChanged();
                            if (aut2.getDropDownAnchor() != 0)
                                selectHouse = hAdapter.getItem(0);
                        }
                    });
                    aut2.setAdapter(hAdapter);
                    aut2.setEnabled(true);
                    aut2.setText("");
                    aut2.requestFocus();
                    aut2_selected = false;
                }

            }
        });

        aut2.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // ""
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    aut2_selected = true;
                    house st = null;
                    st = (house) aut2.getAdapter().getItem(0);
                    if (st != null) {
                        aut2.setText(st.getValue());
                        currentHouseNumber = st.getValue();
                        currentHouseID = st.getId();
                        currentGeoX = st.getX();
                        currentGeoY = st.getY();
                        strIDTxt.setText(currentStreetID);
                        geoxTxt.setText(currentGeoX);
                        geoyTxt.setText(currentGeoY);
                        houseID.setText(currentHouseID);
                    }
                    return false;
                }
                return false;
            }
        });

        aut2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //Log.d("setOnItemClickListener" , "pos - " + arg2);
                Object obj = arg0.getItemAtPosition(arg2); //должно быть "com.webcab.elit.data.house"
                house st = (house) arg0.getItemAtPosition(arg2);
                if (st != null) {
                    aut2_selected = true;
                    aut2.setText(st.getValue());
                    currentHouseNumber = st.getValue();
                    currentHouseID = st.getId();
                    currentGeoX = st.getX();
                    currentGeoY = st.getY();
                    strIDTxt.setText(currentStreetID);
                    geoxTxt.setText(currentGeoX);
                    geoyTxt.setText(currentGeoY);
                }

            }
        });

        if (getArguments() != null) {
            aut1.setText(!getArguments().getString("streetName").equals("") ? getArguments().getString("streetName") : "");
            aut2.setText(!getArguments().getString("houseNumber").equals("") ? getArguments().getString("houseNumber") : "");
            strIDTxt.requestFocus();
        }

        rootView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeLeft() {

                deleteAddressImage.setVisibility(View.VISIBLE);

                Log.d("SWIPE", "swipe");
                super.onSwipeBottom();
            }

            @Override
            public void onSwipeTop() {
                addressAction.moveUp(Integer.parseInt(getTag()));
                super.onSwipeTop();
            }

            @Override
            public void onSwipeBottom() {
                addressAction.moveDown(Integer.parseInt(getTag()));
                super.onSwipeBottom();
            }
        });

        deleteAddressImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressAction.deleteAddress(Integer.parseInt(getTag()));
            }
        });

        return rootView;
    }

}
