package com.webcab.elit.adapters;

import android.app.Activity;
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
import com.webcab.elit.data.street;
import com.webcab.elit.net.ServiceConnection;

import java.util.ArrayList;
import java.util.List;

public class StreetAdapter extends ArrayAdapter<street> implements Filterable {
    private ArrayList<String> mData;
    private List<street> st;
    private int viewResourceId;
    private int textview;
    private Context ctx;
    private AutoCompleteLoadding aut;

    public StreetAdapter(Context context, int textViewResourceId, int textview1) {
        super(context, textViewResourceId, textview1);
        mData = new ArrayList<String>();
        st = new ArrayList<street>();
        viewResourceId = textViewResourceId;
        textview = textview1;
        ctx = context;
    }

    public StreetAdapter(Context context, int textViewResourceId, int textview1, AutoCompleteLoadding aut) {
        super(context, textViewResourceId, textview1);
        mData = new ArrayList<String>();
        st = new ArrayList<street>();
        viewResourceId = textViewResourceId;
        textview = textview1;
        ctx = context;
        this.aut = aut;
    }

    @Override
    public int getCount() {
        return st.size();
    }

    @Override
    public street getItem(int index) {
        if(index < st.size() )
         return st.get(index);
        else return null;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
            v.setBackgroundResource(R.drawable.spinner_item);
        }
        street customer = null;
        if(position < st.size())
            customer = st.get(position);
        if (customer != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.txt_year);
            if (customerNameLabel != null) {
                if (st.get(position).getName().length()>=23) //random number for "\n" on listView
                {
                    customerNameLabel.setText(st.get(position).getName().replaceFirst(" ", "\n"));
                }
                else
                    customerNameLabel.setText(st.get(position).getName());
            }
        }
        return v;
    }

    /*@Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (aut != null) {
            Log.d("StreetAdapter", "notifyDataSetChanged");
            aut.clearFocus();
            aut.requestFocus();
            aut.showDropDown();
        }
    }*/

    public String getStreetId(int index) {
    	return st.get(index).getId();
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null && constraint.length()>1) {
                	
                	
                	ServiceConnection sc2 = new ServiceConnection(ctx);
                	st = sc2.getStreet(constraint.toString());
                	
    				mData.clear();
    				for (int i=0; i<st.size(); i++) {
    					mData.add(st.get(i).getName());
    					Log.d("street", i + " - " + st.get(i).getName() + " - id - " + st.get(i).getId());
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
			protected void publishResults(CharSequence contraint,
					FilterResults results) {
				if (contraint != null && contraint.length() > 1
						&& results != null && results.count > 0) {
                    Activity currentActivity = (Activity) ctx;
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });

				} else {
					notifyDataSetInvalidated();
				}
			}
		};
		return myFilter;
    }
}