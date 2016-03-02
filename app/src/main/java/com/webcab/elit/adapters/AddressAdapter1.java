package com.webcab.elit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.webcab.elit.R;
import com.webcab.elit.data.addr2;
import com.webcab.elit.data.house;

import java.util.List;

/**
 * Created by Sergey on 30.08.2015.
 */
public class AddressAdapter1 extends BaseAdapter {

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

    static StreetAdapter stAdapter;


    house selectHouse;
    boolean aut2_selected;

    public interface onAddressActionListener {
        public void deleteAddress(int id);
    }

    onAddressActionListener addressAction;




    public AddressAdapter1 (Context mContext, List<addr2> addresses) {
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.addresses = addresses;
        this.stAdapter = new StreetAdapter(mContext, R.layout.street_item, R.id.txt_year);
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

    private static class ViewHolder {

        TextView strIDTxt, geoxTxt, geoyTxt, houseID;
        ImageView deleteAddressImage;
        AutoCompleteLoadding aut1, aut2;

        ProgressBar pr_str;
        ProgressBar pr_dom;

        HomeAdapter hAdapter;


        public ViewHolder(View view) {
            strIDTxt = (TextView) view.findViewById(R.id.street_id);
            geoxTxt = (TextView) view.findViewById(R.id.geox);
            geoyTxt = (TextView) view.findViewById(R.id.geoy);
            houseID = (TextView) view.findViewById(R.id.houseID);
            deleteAddressImage = (ImageView) view.findViewById(R.id.imageView9);

            pr_str = (ProgressBar) view.findViewById(R.id.pr_str);
            pr_dom = (ProgressBar) view.findViewById(R.id.pr_dom);

            aut1 = (AutoCompleteLoadding) view.findViewById(R.id.aut1);

            aut2 = (AutoCompleteLoadding) view.findViewById(R.id.aut2);


        }
    }

    public void createViews(List<addr2> addr) {

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        view = convertView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.address_item_editable, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.aut1.setLoadingIndicator(holder.pr_str);
        //holder.stAdapter = new StreetAdapter(mContext, R.layout.street_item, R.id.txt_year);
        holder.aut1.setAdapter(stAdapter);
        //holder.aut1.setThreshold(1);


        holder.aut2.setLoadingIndicator(holder.pr_dom);
        holder.aut2.setThreshold(1);
        holder.aut2.setEnabled(true);


        if (addresses.get(position).getSreetname() != null && addresses.get(position).getHousenumber() != null) {
            holder.aut1.setText(!addresses.get(position).getSreetname().equals("") ? addresses.get(position).getSreetname() : "", false);
            holder.aut2.setText(!addresses.get(position).getHousenumber().equals("") ? addresses.get(position).getHousenumber() : "", false);
            if (holder.pr_dom != null)
                holder.pr_dom.setVisibility(View.GONE);
            if (holder.pr_str != null)
                holder.pr_str.setVisibility(View.GONE);
        }

        addressAction = (onAddressActionListener) mContext;

        holder.aut1.requestFocus();

        /*holder.aut1.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                android.util.Log.d("setOnItemClickListener", "test");
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    Adapter curAdaper = holder.aut1.getAdapter();
                    street st = (street) curAdaper.getItem(position);
                    //street st = (street) parent.getItemAtPosition(position);
                    if (st != null) {
                        holder.aut1.setText(st.getName());

                        currentStreetID = st.getId();
                        currentGeoX = st.getGeox();
                        currentGeoY = st.getGeoy();

                        holder.aut1.dismissDropDown();


                        holder.hAdapter = new HomeAdapter(mContext, R.layout.street_item, R.id.txt_year, currentStreetID);
                        holder.hAdapter.registerDataSetObserver(new DataSetObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                if (holder.aut2.getDropDownAnchor() != 0)
                                    selectHouse = holder.hAdapter.getItem(0);
                            }
                        });
                        holder.aut2.setAdapter(holder.hAdapter);
                        holder.aut2.setEnabled(true);
                        holder.aut2.setText("");
                        holder.aut2.requestFocus();
                        aut2_selected = false;
                    }
                    return false;
                }
                return false;
            }
        });

        holder.aut1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Adapter curAdaper = parent.getAdapter();
                street st = (street) curAdaper.getItem(position);
                //street st = (street) parent.getItemAtPosition(position);
                if (st != null) {
                    holder.aut1.setText(st.getName());

                    currentStreetID = st.getId();
                    currentGeoX = st.getGeox();
                    currentGeoY = st.getGeoy();

                    holder.aut1.dismissDropDown();

                    holder.hAdapter = new HomeAdapter(mContext, R.layout.street_item, R.id.txt_year, currentStreetID);
                    holder.hAdapter.registerDataSetObserver(new DataSetObserver() {
                        @Override
                        public void onChanged() {
                            super.onChanged();
                            if (holder.aut2.getDropDownAnchor() != 0)
                                selectHouse = holder.hAdapter.getItem(0);
                        }
                    });
                    holder.aut2.setAdapter(holder.hAdapter);
                    holder.aut2.setEnabled(true);
                    holder.aut2.setText("");
                    holder.aut2.requestFocus();
                    aut2_selected = false;
                }
            }
        });

        holder.aut2.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // ""
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    aut2_selected = true;
                    house st = null;
                    st = (house) holder.aut2.getAdapter().getItem(0);
                    if (st != null) {
                        aut2_selected = true;
                        holder.aut2.setText(st.getValue());
                        currentHouseNumber = st.getValue();
                        currentHouseID = st.getId();
                        currentGeoX = st.getX();
                        currentGeoY = st.getY();
                        holder.strIDTxt.setText(currentStreetID);
                        holder.geoxTxt.setText(currentGeoX);
                        holder.geoyTxt.setText(currentGeoY);

                        addr2 newAddr = new addr2(currentStreetName, currentStreetID, currentHouseNumber, currentGeoX, currentGeoY);
                        newAddr.setHouseID(currentHouseID);
                        onAddressReadyListener writeAddress = (onAddressReadyListener) mContext;
                        writeAddress.addAddressToList(newAddr, position);
                    }
                    return false;
                }
                return false;
            }
        });

        holder.aut2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //Log.d("setOnItemClickListener" , "pos - " + arg2);
                Object obj = arg0.getItemAtPosition(arg2); //должно быть "com.webcab.elit.data.house"
                Adapter curHouseAd = arg0.getAdapter();
                house st = (house) arg0.getItemAtPosition(arg2);
                Log.d("myLogs", "hAdapterCount2 = " + holder.aut2.getAdapter().getCount());
                if (st != null) {
                    aut2_selected = true;
                    holder.aut2.setText(st.getValue());
                    currentHouseNumber = st.getValue();
                    currentHouseID = st.getId();
                    currentGeoX = st.getX();
                    currentGeoY = st.getY();
                    holder.strIDTxt.setText(currentStreetID);
                    holder.geoxTxt.setText(currentGeoX);
                    holder.geoyTxt.setText(currentGeoY);

                    addr2 newAddr = new addr2(currentStreetName, currentStreetID, currentHouseNumber, currentGeoX, currentGeoY);
                    newAddr.setHouseID(currentHouseID);
                    onAddressReadyListener writeAddress = (onAddressReadyListener) mContext;
                    writeAddress.addAddressToList(newAddr, position);
                }

            }
        });*/

        /*aut1.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (lastFocussedPosition == -1 || lastFocussedPosition == position) {
                                lastFocussedPosition = position;
                                aut1.requestFocus();
                            }
                        }
                    }, 200);

                } else {
                    lastFocussedPosition = -1;
                }
            }
        });*/




        return view;
    }

    public interface onAddressReadyListener{
       void addAddressToList(addr2 newAddress, int position);
    }


}
