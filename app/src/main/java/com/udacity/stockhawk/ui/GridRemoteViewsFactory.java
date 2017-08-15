package com.udacity.stockhawk.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import timber.log.Timber;

/**
 * Created by David on 05-Aug-17.
 */

public class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private Cursor cursor;

    public GridRemoteViewsFactory(Context applicationContext, Intent intent){
        Timber.d("in constructor()");
        context = applicationContext;
    }

    @Override
    public void onCreate() {
        Timber.d("in onCreate()");
    }

    @Override
    public void onDataSetChanged() {
        Timber.d("in onDatasetChanged(), sending query");
        cursor = context.getContentResolver().query(
                Contract.Quote.URI,
                null,
                null,
                null,
                Contract.Quote.COLUMN_PRICE + " DESC"
        );
    }

    @Override
    public void onDestroy() {
        cursor.close();
    }

    @Override
    public int getCount() {
        Timber.d("in getCount(), count is: " + cursor.getCount());
        return  cursor == null ? 0 : cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Timber.d("in getViewAt() position: " + position);
        cursor.moveToPosition(position);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.stock_widget_item);
        remoteViews.setTextViewText(R.id.appwidget_stock_name, cursor.getString(1));
        remoteViews.setTextViewText(R.id.appwidget_stock_price, cursor.getString(2));

//        Intent launchMainActivityintent = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchMainActivityintent, 0);
//
//        remoteViews.setOnClickPendingIntent(R.id.stock_widget_grid, pendingIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        Timber.d("in getItemId(), position: " + position);
        return cursor.moveToPosition(position) ? cursor.getLong(0) : position;
        //return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
