package com.webcab.elit.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.webcab.elit.R;
import com.webcab.elit.data.addr2;

import java.util.List;

/**
 * Created by Sergey on 30.08.2015.
 */
public class AddressAdapter3 extends BaseAdapter {

    LayoutInflater inflater;
    Context mContext;
    View view;
    List<addr2> addresses;

    TextView streetTxt, houseTxt, streetNameTxt, houseNumberTxt;


    public AddressAdapter3(Context mContext, List<addr2> addresses) {
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

    public void upDateAddress (List<addr2> newList) {
        addresses = newList;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.address_item_editable, parent, false);

        streetTxt = (TextView) view.findViewById(R.id.street_txt);
        streetNameTxt = (TextView) view.findViewById(R.id.street_name_txt);
        houseTxt = (TextView) view.findViewById(R.id.house_txt);
        houseNumberTxt = (TextView) view.findViewById(R.id.house_number_txt);

        streetNameTxt.setText(addresses.get(position).getSreetname());
        houseNumberTxt.setText(addresses.get(position).getHousenumber());

        setUpFonts(view);

        return view;
    }

    private void setUpFonts(View view) {

        Typeface font = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");

        streetTxt.setTypeface(font);
        streetNameTxt.setTypeface(font);
        houseTxt.setTypeface(font);
        houseNumberTxt.setTypeface(font);

    }
}
