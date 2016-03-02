package com.webcab.elit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

/**
 * Created by Sergey on 13.10.2015.
 */
public class CustomAutoComplete extends AutoCompleteTextView {

    private ProgressBar mLoadingIndicator;

    public CustomAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setLoadingIndicator(ProgressBar progressBar) {
        mLoadingIndicator = progressBar;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
        super.performFiltering(text, keyCode);
    }

    @Override
    public void onFilterComplete(int count) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.GONE);
        }
        super.onFilterComplete(count);
    }

}
