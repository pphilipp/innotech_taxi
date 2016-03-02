package com.webcab.elit.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webcab.elit.Log;
import com.webcab.elit.Order_to;
import com.webcab.elit.R;

public class addPoint {

	LinearLayout ll;
	Context ctx;

	public addPoint(LinearLayout ll, Context ctx) {
		super();
		this.ll = ll;
		this.ctx = ctx;
	}

	public void draw() {
		//addN();
		//addLast();
	}

	public void addN(int index) {

		FrameLayout f1 = new FrameLayout(ctx);
		f1.setLayoutParams(new LinearLayout.LayoutParams(
				new MarginLayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT)));

		Button bt = new Button(ctx);
		bt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
		bt.setBackgroundResource(R.drawable.item_home);

		FrameLayout.LayoutParams p_bt = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, 75);
		bt.setLayoutParams(p_bt);
		
		final int j = index;
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ctx, Order_to.class);
				intent.putExtra("id", j+1);
				ctx.startActivity(intent);
			}
		});

		LinearLayout l2 = new LinearLayout(ctx);
		l2.setOrientation(LinearLayout.HORIZONTAL);
		l2.setLayoutParams(new LinearLayout.LayoutParams(
				new MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT)));
		
		ImageView i1 = new ImageView(ctx);
		i1.setImageResource(R.drawable.from_active);

		LinearLayout.LayoutParams p_i1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		p_i1.setMargins(5, 0, 5, 0);
		p_i1.gravity = Gravity.CENTER_VERTICAL;
		i1.setLayoutParams(p_i1);
		
		
		ImageView i2 = new ImageView(ctx);
		i2.setBackgroundResource(R.drawable.sep);

		LinearLayout.LayoutParams p_i2 = new LinearLayout.LayoutParams(
				2, LinearLayout.LayoutParams.MATCH_PARENT);
		p_i2.gravity = Gravity.CENTER_VERTICAL;
		i2.setLayoutParams(p_i2);

		
		Typeface font1 = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/GOTHIC.TTF");
		
		TextView t = new TextView(ctx);
		t.setText("адрес прибытия автомобиля");
//		t.setTypeface(font1);
		t.setLines(1);
		t.setTextColor(Color.rgb(203, 203, 203));
		
		LinearLayout.LayoutParams p_t = new LinearLayout.LayoutParams(
				200, LinearLayout.LayoutParams.WRAP_CONTENT);
		p_t.gravity = Gravity.CENTER_VERTICAL;
		p_t.setMargins(10, 0, 0, 0);
		
		t.setLayoutParams(p_t);
		
		ImageView i3 = new ImageView(ctx);
		i3.setImageResource(R.drawable.file_inactive);
		
		FrameLayout.LayoutParams p_i3 = new FrameLayout.LayoutParams(40, 40);
		p_i3.setMargins(0, 0, 5, 0);
		p_i3.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		
		i3.setLayoutParams(p_i3);
		
		l2.addView(i1);
		l2.addView(i2);
		l2.addView(t);
		
		f1.addView(bt);
		f1.addView(l2);
		f1.addView(i3);
		
		ll.addView(f1);

	}
	
	public void addLast(int index) {
		FrameLayout f1 = new FrameLayout(ctx);
		f1.setLayoutParams(new LinearLayout.LayoutParams(
				new MarginLayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT)));

		Button bt = new Button(ctx);
		bt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
		bt.setBackgroundResource(R.drawable.item_home);

		FrameLayout.LayoutParams p_bt = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, 75);
		bt.setLayoutParams(p_bt);
		final int j = index;
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Intent intent = new Intent(ctx, OrderTo2.class);
				//intent.putExtra("id", j+1);
				//ctx.startActivity(intent);
			}
		});

		LinearLayout l2 = new LinearLayout(ctx);
		l2.setOrientation(LinearLayout.HORIZONTAL);
		l2.setLayoutParams(new LinearLayout.LayoutParams(
				new MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT)));
		
		LinearLayout l3 = new LinearLayout(ctx);
		l3.setOrientation(LinearLayout.VERTICAL);
		l3.setLayoutParams(new LinearLayout.LayoutParams(
				new MarginLayoutParams(80, 75)));
		
		ImageView i1 = new ImageView(ctx);
		i1.setImageResource(R.drawable.to_active);
		
		LinearLayout.LayoutParams p_i1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, 45);
		p_i1.setMargins(0, 0, 0, 0);
		p_i1.gravity = Gravity.CENTER_HORIZONTAL;
		i1.setLayoutParams(p_i1);
		
		ImageView add = new ImageView(ctx);
		add.setBackgroundResource(R.drawable.fg);
		add.setImageResource(R.drawable.pl);

		LinearLayout.LayoutParams p_add = new LinearLayout.LayoutParams(
				75, 30);
		p_add.setMargins(5, 0, 5, 0);
		p_add.gravity = Gravity.CENTER_HORIZONTAL;
		add.setLayoutParams(p_add);
		
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (j==5) {
					Log.v("test", "to more");
				} else if ((j<5)&&(j>1)) {
					addLast(j+1);
				} else {
					Log.v("test", "error adding");
				}
			}
		});
		
		
		ImageView i2 = new ImageView(ctx);
		i2.setBackgroundResource(R.drawable.sep);

		LinearLayout.LayoutParams p_i2 = new LinearLayout.LayoutParams(
				2, LinearLayout.LayoutParams.MATCH_PARENT);
		p_i2.gravity = Gravity.CENTER_VERTICAL;
		i2.setLayoutParams(p_i2);

		
		Typeface font1 = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/GOTHIC.TTF");
		
		TextView t = new TextView(ctx);
		t.setText("адрес прибытия автомобиля");
//		t.setTypeface(font1);
		t.setLines(1);
		t.setTextColor(Color.rgb(203, 203, 203));
		
		LinearLayout.LayoutParams p_t = new LinearLayout.LayoutParams(
				200, LinearLayout.LayoutParams.WRAP_CONTENT);
		p_t.gravity = Gravity.CENTER_VERTICAL;
		p_t.setMargins(10, 0, 0, 0);
		
		t.setLayoutParams(p_t);
		
		ImageView i3 = new ImageView(ctx);
		i3.setImageResource(R.drawable.file_inactive);
		
		FrameLayout.LayoutParams p_i3 = new FrameLayout.LayoutParams(40, 40);
		p_i3.setMargins(0, 0, 5, 13);
		p_i3.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		
		i3.setLayoutParams(p_i3);
		
		ImageView del = new ImageView(ctx);
		del.setBackgroundResource(R.drawable.fg);
		del.setImageResource(R.drawable.cross);
		
		FrameLayout.LayoutParams p_del = new FrameLayout.LayoutParams(75, 30);
		p_del.setMargins(0, 0, 0, 0);
		p_del.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		
		del.setLayoutParams(p_del);
		
		del.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ll.removeViewAt(0);
				
			}
		});
		
		l3.addView(i1);
		l3.addView(add);
		
		l2.addView(l3);
		l2.addView(i2);
		l2.addView(t);
		
		f1.addView(bt);
		f1.addView(l2);
		
		f1.addView(i3);
		f1.addView(del);
		
		ll.addView(f1);

	}

}
