package com.webcab.elit.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.webcab.elit.R;
import com.webcab.elit.data.street;
import com.webcab.elit.net.ServiceConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sergey on 13.10.2015.
 */
public class StreetAdapter1 extends BaseAdapter implements Filterable {

    private Context mContext;
    private List<street> streetList;

    public StreetAdapter1(Context mContext) {
        this.mContext = mContext;
        this.streetList = new ArrayList<street>();
    }


    @Override
    public int getCount() {
        return streetList.size();
    }

    @Override
    public street getItem(int position) {
        return streetList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.street_item, parent, false);
        }
        street mStreet = getItem(position);
        ((TextView) convertView.findViewById(R.id.txt_year)).setText(mStreet.getName());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                Log.d("DropDown", "FilterResults");
                if (constraint != null) {
                    List<street> streets = findStreets(mContext, constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = streets;
                    filterResults.count = streets.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Log.d("DropDown", "publishResults");
                if (results != null && results.count > 0) {
                    streetList = (List<street>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private List<street> findStreets(Context mContext, String s) {


        try {
            return (new GetStreetsFromServer(mContext)).execute(s).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("Async", "InterruptedException error = " + e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.d("Async", "ExecutionException error = " + e.getMessage());
        }
        return null;
    }

    private class GetStreetsFromServer extends AsyncTask<String, Void, List<street>> {

        Context ctx;

        public GetStreetsFromServer(Context ctx) {
            this.ctx = ctx;
        }


        @Override
        protected List<street> doInBackground(String... params) {

            String s = params[0];
            ServiceConnection sc = new ServiceConnection(mContext);
            List<street> streetsFromServer = sc.getStreet(s);

            return streetsFromServer;
        }
    }
}
