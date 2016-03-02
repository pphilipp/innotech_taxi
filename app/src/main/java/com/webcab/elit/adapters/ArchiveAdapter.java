package com.webcab.elit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webcab.elit.CabinetArchItem;
import com.webcab.elit.R;
import com.webcab.elit.data.order_s;

import java.text.SimpleDateFormat;
import java.util.List;

public class ArchiveAdapter extends BaseAdapter {

	private static final String TAG = "ARCHIVE_TAG";
	private LayoutInflater inflater;
	List<order_s> ord_s;
	Context ctx;
	View view;
	private SharedPreferences mSettingsReg;

	public ArchiveAdapter(Context context, List<order_s> lst) {
		// inflater = LayoutInflater.from(context);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ctx = context;
		ord_s = lst;
		this.mSettingsReg = ctx.getSharedPreferences("order", Context.MODE_PRIVATE);
	}

	@Override
	public int getCount() {
		return ord_s.size();
	}

	@Override
	public Object getItem(int position) {
		return ord_s.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.item_addr_archive, parent, false);
		}

        Typeface font1 = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");

		TextView date = (TextView) view.findViewById(R.id.txt_date);
		TextView from = (TextView) view.findViewById(R.id.txt_from);
		TextView preorder_index = (TextView) view.findViewById(R.id.preorder_index);
        date.setTypeface(font1);
        from.setTypeface(font1);

		int currentOrderID = Integer.parseInt(mSettingsReg.getString("orderId", "-1"));
		Log.d(TAG, "orderID = " + ord_s.get(position).getOrderID() + " in settings id = " + currentOrderID);

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll);

		//date.setText(ord_s.get(position).getWhen().replace("-", "."));
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm  dd.MM.yyyy");
		//Log.d("ARCH_TEST", "time = " + sdf.format(ord_s.get(position).getWhenDate()));
        date.setText(sdf.format(ord_s.get(position).getWhenDate() * 1000));
        from.setText(ord_s.get(position).getFrom() + " - " + ord_s.get(position).getTo());
        if (ord_s.get(position).getPreorderStatus()) {
            ll.setBackgroundColor(Color.parseColor("#FFCFF0FF"));
            preorder_index.setVisibility(View.VISIBLE);
        } else {
			preorder_index.setVisibility(View.GONE);
			ll.setBackgroundColor(0x00000000);
		}

		LinearLayout lnl = (LinearLayout) view.findViewById(R.id.ll);
		lnl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent;
				//if (ord_s.get(position).getPreorderStatus()) {
                    /*GetFullOrderInfo mGetFullOrderInfo = new GetFullOrderInfo(ctx, ord_s.get(position));
                    mGetFullOrderInfo.execute();*/
				//} else {
					intent = new Intent(ctx, CabinetArchItem.class);
					intent.putExtra("rate", ord_s.get(position).getRate());
                    intent.putExtra("from", ord_s.get(position).getFrom());
                    intent.putExtra("to", ord_s.get(position).getTo());
                    intent.putExtra("price", ord_s.get(position).getPrice());
                    intent.putExtra("id", ord_s.get(position).getId());
                    intent.putExtra("date", ord_s.get(position).getWhen());
                    intent.putExtra("orderID", ord_s.get(position).getOrderID());
                    intent.putExtra("serverTime", ord_s.get(position).getWhenDate());
                    intent.putExtra("orderStatus", ord_s.get(position).getPreorderStatus());
                    intent.putIntegerArrayListExtra("additionalServices", ord_s.get(position).getAdditionalServices());
                    intent.putExtra("carClass", ord_s.get(position).getCarClass());
                    ctx.startActivity(intent);
                    ((Activity)ctx).finish();
				//}
			}
		});

		return view;
	}


}
