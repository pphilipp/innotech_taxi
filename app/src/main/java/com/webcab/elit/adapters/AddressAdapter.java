package com.webcab.elit.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.webcab.elit.R;
import com.webcab.elit.data.addr2;
import com.webcab.elit.data.house;
import com.webcab.elit.data.street;

import java.util.List;

/**
 * Created by Sergey on 18.08.2015.
 */
public class AddressAdapter extends BaseAdapter{

    LayoutInflater inflater;
    Context mContext;
    View view;
    List<addr2> addresses;
    addr2 currentAddress;
    public String currentStreetName;
    public String currentStreetID;
    public String currentHouseID;
    public String currentHouseNumber;
    public String currentGeoX;
    public String currentGeoY;

    TextView strIDTxt, geoxTxt, geoyTxt, houseID;
    ImageView deleteAddressImage;

    HomeAdapter hAdapter;
    StreetAdapter stAdapter;
    house selectHouse;
    boolean aut2_selected;


    AutoCompleteLoadding aut1, aut2;

    public AddressAdapter(Context mContext, List<addr2> addresses) {
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.addresses = addresses;
    }

    public void upDateAddress (List<addr2> newList) {
        addresses = newList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return addresses.size();
    }

    @Override
    public Object getItem(int position) {
        return addresses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.address_item_editable, parent, false);
        }

        strIDTxt = (TextView) view.findViewById(R.id.street_id);
        geoxTxt = (TextView) view.findViewById(R.id.geox);
        geoyTxt = (TextView) view.findViewById(R.id.geoy);
        houseID = (TextView) view.findViewById(R.id.houseID);
        deleteAddressImage = (ImageView) view.findViewById(R.id.imageView9);

        ProgressBar pr_str = (ProgressBar) view.findViewById(R.id.pr_str);
        ProgressBar pr_dom = (ProgressBar) view.findViewById(R.id.pr_dom);

        aut1 = (AutoCompleteLoadding) view.findViewById(R.id.aut1);
        aut1.setLoadingIndicator(pr_str);
        stAdapter = new StreetAdapter(mContext, R.layout.street_item, R.id.txt_year);
        aut1.setAdapter(stAdapter);
        //aut1.setThreshold(1);

        aut2 = (AutoCompleteLoadding) view.findViewById(R.id.aut2);
        aut2.setLoadingIndicator(pr_dom);
        aut2.setThreshold(1);
        aut2.setEnabled(true);
        //aut2.addTextChangedListener(this);
        //aut1.addTextChangedListener(this);




        /*aut1.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                android.util.Log.d("setOnItemClickListener", "test");
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    street st = null;
                    st = (street) aut1.getAdapter().getItem(0);
                    if (st != null) {
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
        });*/
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

                    addr2 newAddr = new addr2(currentStreetName, currentStreetID, currentHouseNumber, currentGeoX, currentGeoY);
                    newAddr.setHouseID(currentHouseID);
                    onAddressReadyListener writeAddress = (onAddressReadyListener) mContext;
                    writeAddress.addAddressToList(newAddr, position);
                }

            }
        });

        aut1.setText(!addresses.get(position).getSreetname().equals("") ? addresses.get(position).getSreetname() : "");
        aut2.setText(!addresses.get(position).getHousenumber().equals("") ? addresses.get(position).getHousenumber() : "");

        /*if (!aut1.getText().toString().equals(""))
            aut1.setLoadingIndicator(null);
        if (!aut2.getText().toString().equals(""))
            aut2.setLoadingIndicator(null);*/


        return view;
    }

    public interface onAddressReadyListener{
        public void addAddressToList(addr2 newAddress, int position);
    }


}
