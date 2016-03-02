package com.webcab.elit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webcab.elit.DBPoints;
import com.webcab.elit.HomeScreen;
import com.webcab.elit.Log;
import com.webcab.elit.R;
import com.webcab.elit.TemplAddAddr;
import com.webcab.elit.data.addr;

import java.util.List;

public class TemplAddrAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	List<addr> addrL;
	Context ctx;
	View view;

	public TemplAddrAdapter(Context context, List<addr> lst) {
		// inflater = LayoutInflater.from(context);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ctx = context;
		addrL = lst;
	}

	@Override
	public int getCount() {
		return addrL.size();
	}

	@Override
	public Object getItem(int position) {
		return addrL.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.item_addr_templ, parent, false);
		}

		TextView title = (TextView) view.findViewById(R.id.txt_title);
		TextView desc = (TextView) view.findViewById(R.id.txt_desc);

		Typeface font1 = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");

		title.setTypeface(font1);
		desc.setTypeface(font1);
		
		title.setText(addrL.get(position).getTitle());
		desc.setText(addrL.get(position).getDesc());
		
		ImageButton ed = (ImageButton) view.findViewById(R.id.templ_edit);
		ed.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(ctx, TemplAddAddr.class);
				intent.putExtra("id", Integer.toString(addrL.get(position).getId()));
				ctx.startActivity(intent);
				((Activity)ctx).finish();
			}
		});
		
		ImageButton del = (ImageButton) view.findViewById(R.id.templ_del);
		del.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DBPoints dbHelper = new DBPoints(ctx);
				// подключаемся к БД
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				
				int row = db.delete("addr", "id=" + addrL.get(position).getId(), null);
				Log.d("TemplAddrAdapter", "id - " + addrL.get(position).getId() + " - row deleted - " + row);

				dbHelper.close();
				
				addrL.remove(position);
	            notifyDataSetChanged();
			}
		});
		
		LinearLayout lnl = (LinearLayout) view.findViewById(R.id.lnl);
		lnl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ctx, HomeScreen.class);
				intent.putExtra("templ_id", addrL.get(position).getId());
				ctx.startActivity(intent);
				((Activity)ctx).finish();
				//Log.d("lnl", ""+addrL.get(position).getId());
			}
		});

		return view;
	}
}
