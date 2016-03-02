package com.webcab.elit.adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

public class AutoCompleteLoadding extends AutoCompleteTextView {

	public AutoCompleteLoadding(Context context) {
		super(context);
		// ""
	}

	public AutoCompleteLoadding(Context context, AttributeSet attrs) {
		super(context, attrs);
		// ""
	} 

	public AutoCompleteLoadding(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// ""
	}

	private ProgressBar mLoadingIndicator;

	public void setLoadingIndicator(ProgressBar view) {
		mLoadingIndicator = view;
	}

	@Override
	protected void performFiltering(CharSequence text, int keyCode) {
		// the AutoCompleteTextview is about to start the filtering so show
		// the ProgressPager
		if (mLoadingIndicator != null)
			mLoadingIndicator.setVisibility(View.VISIBLE);
		super.performFiltering(text, keyCode);
	}

	@Override
	public void onFilterComplete(int count) {
		// the AutoCompleteTextView has done its job and it's about to show
		// the drop down so close/hide the ProgreeBar
		if (mLoadingIndicator != null)
			mLoadingIndicator.setVisibility(View.GONE);
		super.onFilterComplete(count);
	}

}