package com.webcab.elit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.webcab.elit.Log;
import com.webcab.elit.R;
import com.webcab.elit.data.house;
import com.webcab.elit.net.ServiceConnection;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends ArrayAdapter<house> implements Filterable {
    private ArrayList<String> mData;
    private List<house> st;
    private int viewResourceId;
    private String streetId;
    private Context ctx;
    
    public HomeAdapter(Context context, int textViewResourceId, String strid) {
        super(context, textViewResourceId);
        mData = new ArrayList<String>();
        st = new ArrayList<house>();
        viewResourceId = textViewResourceId;
        streetId = strid;
        ctx = context;
        Log.d("strid", strid);
    }

    public HomeAdapter(Context context, int textViewResourceId, int textview1, String strid) {
        super(context, textViewResourceId, textview1);
        mData = new ArrayList<String>();
        st = new ArrayList<house>();
        viewResourceId = textViewResourceId;
        streetId = strid;
        ctx = context;
        Log.d("strid", strid);
    }

    @Override
    public int getCount() {
        return st.size();
    }

    @Override
    public house getItem(int index) {
        if(index < st.size())
            return st.get(index);
        else
            return null;
    }
    public house getItem(String house){
        for(house h : st){
            if (h.getValue().equals(house))
                return h;
        }
        return null;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
            v.setBackgroundResource(R.drawable.spinner_item);
        }

        house customer = null;
        if (st.size() > position) {
            customer = st.get(position);
        }
        if (customer != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.txt_year);
            if (customerNameLabel != null) {
                customerNameLabel.setText(st.get(position).getValue());
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null && constraint.length()>0) {
                	ServiceConnection sc2 = new ServiceConnection(ctx);
                	st = sc2.getStreetHouse(streetId, constraint.toString());
                	
    				mData.clear();
    				for (int i=0; i<st.size(); i++) {
    					mData.add(st.get(i).getValue());
    					Log.d("street", i + " - " + st.get(i).getValue() + " - id - " + st.get(i).getId() +
    							" x - " + st.get(i).getX() + " - y - " + st.get(i).getY());
    				}
    				
    				if (st.size()==1) {
    					if (st.get(0).getId().equals("0")) {
    						mData.clear();
    					}
    				}
                }
                
                filterResults.values = mData;
                filterResults.count = mData.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if(contraint != null && contraint.length()>0 && results != null && results.count > 0) {
                notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }
}