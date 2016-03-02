package com.webcab.elit.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Sergey on 13.01.2016.
 */
public class WidgetFactoryService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TaxiWidgetListFactory(getApplicationContext(), intent);
    }
}
