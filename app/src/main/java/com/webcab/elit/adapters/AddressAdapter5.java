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
 * Created by Sergey on 03.09.2015.
 */
public class AddressAdapter5 extends BaseAdapter {

    private List<addr2> addresses;
    private Context mContext;
    private LayoutInflater inflater;
    private ViewHolder holder;

    public AddressAdapter5(List<addr2> addresses, Context mContext) {
        this.addresses = addresses;
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return addresses.size();
    }

    @Override
    public addr2 getItem(int position) {
        return addresses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void upDateAddress (List<addr2> newList) {
        addresses = newList;
        this.notifyDataSetChanged();
    }

    private class ViewHolder {

        private TextView streetTxt;
        private TextView streetNameTxt;
        private TextView houseTxt;
        private TextView houseNameTxt;

        private ViewHolder(View v) {

            Typeface font = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/RobotoCondensed-Regular.ttf");

            streetTxt = (TextView) v.findViewById(R.id.txt_year);
            streetNameTxt = (TextView) v.findViewById(R.id.street_txt);
            houseTxt = (TextView) v.findViewById(R.id.TextView01);
            houseNameTxt = (TextView) v.findViewById(R.id.house_txt);

            streetTxt.setTypeface(font);
            streetNameTxt.setTypeface(font);
            houseTxt.setTypeface(font);
            houseNameTxt.setTypeface(font);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.address_where_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.streetNameTxt.setText(addresses.get(position).getSreetname());
        holder.houseNameTxt.setText(addresses.get(position).getHousenumber());

        holder.streetNameTxt.setBackgroundResource(addresses.get(position).getSreetname().equals("") ?
                R.drawable.wrong_field : R.drawable.correct_address);
        holder.houseNameTxt.setBackgroundResource(addresses.get(position).getHousenumber().equals("") ?
                R.drawable.wrong_field : R.drawable.correct_address);

        return convertView;
    }
}
