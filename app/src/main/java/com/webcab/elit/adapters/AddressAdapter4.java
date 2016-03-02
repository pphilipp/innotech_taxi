package com.webcab.elit.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.webcab.elit.R;
import com.webcab.elit.data.addr2;
import com.webcab.elit.data.house;
import com.webcab.elit.data.street;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 03.09.2015.
 */
public class AddressAdapter4 extends BaseAdapter {

    public static final String EDITABLE_VIEW = "edit";
    public static final String NON_EDITABLE_VIEW = "noedit";
    public static final String DELETE_VIEW = "delete";

    LayoutInflater inflater;
    Context mContext;
    View view;
    List<addr2> addresses;
    List<View> myViews;
    int selectedPosition = -1;
    int viewTodelete = -1;
    boolean isEditable;

    public String currentStreetName;
    public String currentStreetID;
    public String currentHouseID;
    public String currentHouseNumber;
    public String currentGeoX;
    public String currentGeoY;

    house selectHouse;
    boolean aut2_selected;

    StreetAdapter stAdapter;
    //StreetAdapter1 stAdapter;

    ProgressBar pr_dom;


    public interface onAddressReadyListener{
        void addAddressToList(addr2 newAddress, int position);
    }

    public interface onAddressActionListener {
        void deleteAddress(int id);
    }

    onAddressActionListener addressAction;

    public AddressAdapter4 (Context mContext, List<addr2> addresses) {
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.addresses = addresses;
    }

    @Override
    public int getCount() {
        return addresses.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        myViews = updateViews(parent);
        View curView = myViews.get(position);

        return curView;
    }

    public void upDateAddress (List<addr2> newList) {
        addresses = newList;
        this.notifyDataSetChanged();
    }

    /**
     * Method sets view in which Autocomplete will be active
     * @param position - id of this view
     */
    public void setEditableViewID(int position) {
        /*if (position == this.selectedPosition)
            this.selectedPosition = -1;
        else
            this.selectedPosition = position;*/
        this.selectedPosition = position;
        this.viewTodelete = -1;
        this.notifyDataSetChanged();
    }

    /**
     * Method sets view in which delete layout will be active
     * @param position
     */
    public void setDeleteViewID(int position) {
        this.viewTodelete = position;
        this.selectedPosition = -1;
        this.notifyDataSetChanged();
    }

    public List<View> updateViews(final ViewGroup parent) {
        List<View> mViews = new ArrayList<>();
        View curView;


        final HomeAdapter[] hAdapter = new HomeAdapter[1];

        Typeface font1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");

        addressAction = (onAddressActionListener) mContext;

        for (int i = 0; i < this.getCount(); i++) {
            if (i == this.selectedPosition || getCount() == 1 && addresses.get(0).getSreetname().equals("")) {
                //editable address item
                curView = inflater.inflate(R.layout.address_item_editable, parent, false);
                isEditable = true;
                curView.setTag(EDITABLE_VIEW);
                stAdapter = null;

                final LinearLayout adLL = (LinearLayout) curView.findViewById(R.id.mainLayout);
                adLL.setBackgroundResource(R.drawable.address_item_background);

                final ProgressBar pr_str = (ProgressBar) curView.findViewById(R.id.pr_str);
                final AutoCompleteLoadding aut1 = (AutoCompleteLoadding) curView.findViewById(R.id.aut1);
                aut1.setText(addresses.get(i).getSreetname());
                aut1.setSelection(addresses.get(i).getSreetname().length());
                aut1.setLoadingIndicator(pr_str);
                stAdapter = new StreetAdapter(mContext, R.layout.street_item, R.id.txt_year, aut1);
                //stAdapter = new StreetAdapter1(mContext);
                aut1.setAdapter(stAdapter);
                //may be the cause of the problems
                aut1.requestFocus();
                //aut1.setThreshold(2);

                //final AutoCompleteLoadding aut2 = (AutoCompleteLoadding) curView.findViewById(R.id.aut2);
                final AutoCompleteTextView aut2 = (AutoCompleteTextView) curView.findViewById(R.id.aut2);
                pr_dom = (ProgressBar) curView.findViewById(R.id.pr_dom);
                //aut2.setLoadingIndicator(pr_dom);
                aut2.setEnabled(true);
                aut2.setText(addresses.get(i).getHousenumber());
                aut2.setSelection(addresses.get(i).getHousenumber().length());
                aut2.setThreshold(1);

                //set up fonts
                ((TextView) curView.findViewById(R.id.txt_year)).setTypeface(font1);
                ((TextView) curView.findViewById(R.id.TextView01)).setTypeface(font1);
                aut1.setTypeface(font1);
                aut2.setTypeface(font1);

                if (!aut1.getText().toString().equals("") && currentStreetName != null) {
                    if (!aut1.getText().toString().equals(currentStreetName)) {
                        aut1.setBackgroundResource(R.drawable.wrong_field);
                    }
                }

                if (!aut2.getText().toString().equals("") && currentHouseNumber != null) {
                    if (!aut2.getText().toString().equals(currentHouseNumber)) {
                        aut2.setBackgroundResource(R.drawable.wrong_field);
                    }
                }

                aut1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int arg2, long id) {

                        street st = (street) parent.getItemAtPosition(arg2);
                        if (st != null) {
                            currentStreetName = st.getName();
                            aut1.setText(currentStreetName);
                            currentStreetID = st.getId();
                            currentGeoX = st.getGeox();
                            currentGeoY = st.getGeoy();

                            aut1.dismissDropDown();

                            /*hAdapter[0] = new HomeAdapter(mContext, R.layout.street_item, R.id.txt_year, currentStreetID);
                            hAdapter[0].registerDataSetObserver(new DataSetObserver() {
                                @Override
                                public void onChanged() {
                                    super.onChanged();
                                    if (aut2.getDropDownAnchor() != 0)
                                        selectHouse = hAdapter[0].getItem(0);
                                }
                            });
                            aut2.setAdapter(hAdapter[0]);
                            hAdapter[0].notifyDataSetChanged();*/
                            aut2.setEnabled(true);
                            pr_dom.setVisibility(View.VISIBLE);
                            //aut2.showDropDown();
                            //aut2.setText("");
                            aut2_selected = false;
                            aut1.setBackgroundResource(R.drawable.correct_address);
                            aut2.requestFocus();


                        }
                    }
                });

                final int finalI = i;
                aut2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {

                        house st = (house) arg0.getItemAtPosition(arg2);
                        if (st != null) {
                            aut2_selected = true;
                            aut2.setText(st.getValue());
                            currentHouseNumber = st.getValue();
                            currentHouseID = st.getId();
                            currentGeoX = st.getX();
                            currentGeoY = st.getY();

                            aut2.dismissDropDown();
                            pr_dom.setVisibility(View.GONE);

                            addr2 newAddr = new addr2(currentStreetName, currentStreetID, currentHouseNumber, currentGeoX, currentGeoY);
                            newAddr.setHouseID(currentHouseID);
                            onAddressReadyListener writeAddress = (onAddressReadyListener) mContext;
                            writeAddress.addAddressToList(newAddr, finalI);
                            aut2.setBackgroundResource(R.drawable.correct_address);
                            selectedPosition = -1;
                            setEditableViewID(selectedPosition);
                            clearCurrents();
                            parent.clearFocus();
                        }

                    }


                });

                aut1.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    String fieldBeforeChange;

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        //change border depending on content
                        if (hasFocus) {
                            //blue border. Edit state
                            aut1.setBackgroundResource(R.drawable.border_with_gradient);
                            //save previous value if it was correct
                            if (currentStreetID != null && !currentStreetID.equals("")) {
                                fieldBeforeChange = aut1.getText().toString();
                            }
                        } else {
                            //if text was changed, reset current field ID
                            if (fieldBeforeChange != null && !fieldBeforeChange.equals(aut1.getText().toString())
                                    || aut1.getText().equals("")) {
                                currentStreetID = "";
                            }
                            if ((!aut1.getText().toString().equals("")
                                    && (currentStreetID == null || currentStreetID.equals(""))
                                    || aut1.getText().toString().equals(""))) {
                                //set RED border for this field. Error state
                                aut1.setBackgroundResource(R.drawable.wrong_field);
                            } else {
                                //set green border. Ok state
                                aut1.setBackgroundResource(R.drawable.correct_address);
                            }
                        }

                    }
                });



                aut2.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    String fieldBeforeChange;

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        //change border depending on content
                        if (hasFocus) {
                            //set up dropdown
                            if (!aut2_selected && currentStreetID != null && !currentStreetID.equals("")) {

                                aut2.setText("");
                                hAdapter[0] = new HomeAdapter(mContext, R.layout.street_item, R.id.txt_year, currentStreetID);
                                hAdapter[0].registerDataSetObserver(new DataSetObserver() {
                                    @Override
                                    public void onChanged() {
                                        super.onChanged();
                                        if (aut2.getDropDownAnchor() != 0)
                                            selectHouse = hAdapter[0].getItem(0);
                                    }
                                });
                                aut2.setAdapter(hAdapter[0]);
                                hAdapter[0].notifyDataSetChanged();

                                //pr_dom.setVisibility(View.VISIBLE);
                                aut2.showDropDown();
                                //aut2.setText("");
                                //aut2.requestFocus();

                            } else {
                                Toast.makeText(mContext,
                                        mContext.getResources().getString(R.string.choose_address_first), Toast.LENGTH_SHORT).show();
                                aut2.setEnabled(false);
                                aut2.setText("");
                                aut1.setBackgroundResource(R.drawable.wrong_field);
                            }
                            aut2_selected = true;
                            //blue border. Edit state
                            aut2.setBackgroundResource(R.drawable.border_with_gradient);
                            //save previous value if it was correct
                            if (currentHouseID != null && !currentHouseID.equals("")) {
                                fieldBeforeChange = aut2.getText().toString();
                            }

                        } else {
                            //if text was changed, reset current field ID
                            if (fieldBeforeChange != null && !fieldBeforeChange.equals(aut2.getText().toString())
                                    || aut2.getText().toString().equals("")) {
                                currentHouseID = "";
                            }
                            if ((!aut2.getText().toString().equals("")
                                    && (currentHouseID == null || currentHouseID.equals(""))
                                    || aut2.getText().toString().equals(""))) {
                                //set RED border for this field. Error state
                                aut2.setBackgroundResource(R.drawable.wrong_field);
                            } else {
                                //set green border. Ok state
                                aut2.setBackgroundResource(R.drawable.correct_address);
                            }
                            aut2_selected = false;
                        }

                    }
                });


            }
            else {
                //not editable addresses
                curView = inflater.inflate(R.layout.address_item_noneditable, parent, false);
                isEditable = false;
                curView.setTag(NON_EDITABLE_VIEW);

                TextView streetTV = (TextView) curView.findViewById(R.id.street_txt);
                TextView houseTV = (TextView) curView.findViewById(R.id.house_txt);
                LinearLayout container = (LinearLayout) curView.findViewById(R.id.ll_container);

                //set up values
                streetTV.setText(addresses.get(i).getSreetname());
                houseTV.setText(addresses.get(i).getHousenumber());

                //set up theme
                streetTV.setBackgroundResource((!addresses.get(i).getSreetname().equals("")) ?
                        R.drawable.correct_address : R.drawable.edittext_active_border);
                houseTV.setBackgroundResource((!addresses.get(i).getHousenumber().equals("")) ?
                        R.drawable.correct_address : R.drawable.edittext_active_border);
                if ((!addresses.get(i).getSreetname().equals("") &&
                        !addresses.get(i).getHousenumber().equals("")))
                    container.setBackgroundResource(R.drawable.address_item_background);


                //set up fonts
                streetTV.setTypeface(font1);
                houseTV.setTypeface(font1);
                ((TextView) curView.findViewById(R.id.txt_year)).setTypeface(font1);
                ((TextView) curView.findViewById(R.id.TextView01)).setTypeface(font1);
            }
            if (i == viewTodelete) {
                //currently is not used. May be used to create separate view to show on delete
                curView = inflater.inflate(R.layout.address_item_delete, parent, false);
                isEditable = false;
                curView.setTag(DELETE_VIEW);

                TextView streetTV = (TextView) curView.findViewById(R.id.street_txt);
                TextView houseTV = (TextView) curView.findViewById(R.id.house_txt);

                //set up values
                streetTV.setText(addresses.get(i).getSreetname());
                houseTV.setText(addresses.get(i).getHousenumber());

                //set up fonts
                ((TextView) curView.findViewById(R.id.txt_year)).setTypeface(font1);
                ((TextView) curView.findViewById(R.id.TextView01)).setTypeface(font1);
                streetTV.setTypeface(font1);
                houseTV.setTypeface(font1);

                LinearLayout delete = (LinearLayout) curView.findViewById(R.id.delete_order_ll);

                final int finalI1 = i;
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addressAction.deleteAddress(finalI1);
                    }
                });
            }

            mViews.add(curView);
        }
        return mViews;
    }

    private void clearCurrents() {
        currentStreetName = "";
        currentStreetID = "";
        currentHouseID = "";
        currentHouseNumber = "";
        currentGeoX = "";
        currentGeoY = "";
    }
}
