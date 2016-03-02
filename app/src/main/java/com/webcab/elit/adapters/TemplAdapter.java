package com.webcab.elit.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webcab.elit.DBPoints;
import com.webcab.elit.DBRoutes;
import com.webcab.elit.HomeScreen;
import com.webcab.elit.Log;
import com.webcab.elit.R;
import com.webcab.elit.TemplAddAddr;
import com.webcab.elit.TemplAddRoute;
import com.webcab.elit.data.TemplatesSyncHelper;
import com.webcab.elit.data.templ;

import java.util.List;

public class TemplAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	List<templ> tm;
	Context ctx;
	View view;
	ContextThemeWrapper themedContext;

	public TemplAdapter(Context context, List<templ> rt) {
		// inflater = LayoutInflater.from(context);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ctx = context;
		tm = rt;
	}

	public void upDateList(List<templ> newAddrList) {
		tm = newAddrList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return tm.size();
	}

	@Override
	public Object getItem(int position) {
		return tm.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			themedContext = new ContextThemeWrapper(ctx, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		} else {
			themedContext = new ContextThemeWrapper(ctx, android.R.style.Theme_Light_NoTitleBar);
		}

		view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.item_addr_templ, parent, false);
		}



		Typeface font1 = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");

		TextView title = (TextView) view.findViewById(R.id.txt_title);
		TextView desc = (TextView) view.findViewById(R.id.txt_desc);

		title.setTypeface(font1);
		desc.setTypeface(font1);
		
		title.setText(tm.get(position).getTitle());
		desc.setText(tm.get(position).getDesc());

		
		ImageButton ed = (ImageButton) view.findViewById(R.id.templ_edit);
		ed.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (tm.get(position).isAddr()) {
					Intent intent = new Intent(ctx, TemplAddAddr.class);
					intent.putExtra("id", Integer.toString(tm.get(position).getId()));
					ctx.startActivity(intent);
					((Activity)ctx).finish();
				} else {
					Intent intent = new Intent(ctx, TemplAddRoute.class);
					intent.putExtra("id", Integer.toString(tm.get(position).getId()));
					ctx.startActivity(intent);
					((Activity)ctx).finish();
				}
			}
		});
		
		ImageButton del = (ImageButton) view.findViewById(R.id.templ_del);
		del.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				AlertDialog.Builder alt_bld = new AlertDialog.Builder(
						themedContext);
				alt_bld.setMessage(ctx.getResources().getString(R.string.temple_delete_message));
				alt_bld.setPositiveButton("Да", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {


						TemplatesSyncHelper templatesSyncHelper = new TemplatesSyncHelper(ctx, TemplatesSyncHelper.DELETE,
								Integer.parseInt(tm.get(position).getServerTemplID()), TemplAdapter.this);
						templatesSyncHelper.execute();

						if (tm.get(position).isAddr()) {
							DBPoints dbHelper = new DBPoints(ctx);
							// подключаемся к БД
							SQLiteDatabase db = dbHelper.getWritableDatabase();

							int row = db.delete("addr", "id=" + tm.get(position).getId(), null);
							Log.d("Templ Adapter", "id - " + tm.get(position).getId() + " - row deleted - " + row);

							dbHelper.close();

							tm.remove(position);
							notifyDataSetChanged();
						} else {
							DBRoutes dbHelper = new DBRoutes(ctx);
							// подключаемся к БД
							SQLiteDatabase db = dbHelper.getWritableDatabase();

							int row = db.delete("route", "id=" + tm.get(position).getId(), null);
							Log.d("Templ Adapter rpoute", "id - " + tm.get(position).getId() + " - row deleted - " + row);

							dbHelper.close();

							tm.remove(position);
							notifyDataSetChanged();
						}
					}
				});
				alt_bld.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				alt_bld.setCancelable(false);
				alt_bld.show();
				

			}
		});
		
		LinearLayout lnl = (LinearLayout) view.findViewById(R.id.lnl);
		lnl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (tm.get(position).isAddr()) {
					Intent intent = new Intent(ctx, HomeScreen.class);
					intent.putExtra("templ_id", tm.get(position).getId());
					ctx.startActivity(intent);
					((Activity)ctx).finish();
					//Log.d("lnl", ""+addrL.get(position).getId());
				} else {

					Intent intent = new Intent(ctx, HomeScreen.class);
					intent.putExtra("routeId", tm.get(position).getId());
					ctx.startActivity(intent);
					((Activity)ctx).finish();
				}
			}
		});

		return view;
	}
}
