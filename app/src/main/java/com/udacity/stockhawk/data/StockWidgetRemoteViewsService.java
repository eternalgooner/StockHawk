package com.udacity.stockhawk.data;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.ui.GridRemoteViewsFactory;

import timber.log.Timber;

/**
 * Created by David on 08-Aug-17.
 */

public class StockWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Timber.d("in onGetViewFactory()");
        return new GridRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
