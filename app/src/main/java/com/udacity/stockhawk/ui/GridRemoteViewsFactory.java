package com.udacity.stockhawk.ui;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
    private static final int STARTING_INDEX = 0;
    private static final int ONE_POSITION = 1;
    private static final int TWO_POSITIONS = 2;
    private static final int THREE_POSITIONS = 4;
    private static final int FOUR_POSITIONS = 5;
    private static final char DECIMAL_POINT = '.';
    private static final int STOCK_NAME = 1;
    private static final int STOCK_PRICE = 2;
    private static final int ID = 0;
    private static final int ONE_VIEW_TYPE = 1;
    private static final int ZERO = 0;
    private static final String DESC = " DESC";
    private int appWidgetId;
    //private List<WidgetItem>

    public GridRemoteViewsFactory(Context applicationContext, Intent intent){
        Timber.d("in constructor()");
        context = applicationContext;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate(){
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
                Contract.Quote.COLUMN_PRICE + DESC
        );
    }

    @Override
    public void onDestroy() {
        cursor.close();
    }

    @Override
    public int getCount() {
        Timber.d("in getCount(), count is: " + cursor.getCount());
        return  cursor == null ? ZERO : cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Timber.d("in getViewAt() position: " + position);
        cursor.moveToPosition(position);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.stock_widget_item);
        StringBuilder stockPrice = formatPriceToTwoDecimals(cursor.getString(STOCK_PRICE));
        remoteViews.setTextViewText(R.id.appwidget_stock_name, cursor.getString(STOCK_NAME));
        remoteViews.setTextViewText(R.id.appwidget_stock_price, stockPrice.toString());

        Bundle extras = new Bundle();
        extras.putInt(StockWidgetProvider.ACTION_CLICK, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        remoteViews.setOnClickFillInIntent(R.id.stock_widget_item, fillInIntent);

        return remoteViews;
    }

    private StringBuilder formatPriceToTwoDecimals(String string) {
        Timber.d("in formatPriceToTwoDecimals() to check String: " + string);

        //if no decimal return original string
        if(!string.contains(".")){
            return new StringBuilder(string);
        }

        //check strings with 3 decimal places
        if(string.charAt(string.length() - THREE_POSITIONS) == DECIMAL_POINT){
            Timber.d("returning: " + new StringBuilder(string.substring(STARTING_INDEX, (string.length() - ONE_POSITION))));
            return new StringBuilder(string.substring(STARTING_INDEX, (string.length() - ONE_POSITION)));
        }

        //check strings with 4 decimal places
        if(string.length() >= 5 && string.charAt(string.length() - FOUR_POSITIONS) == DECIMAL_POINT){
            Timber.d("returning: " + new StringBuilder(string.substring(STARTING_INDEX, (string.length() - TWO_POSITIONS))));
            return new StringBuilder(string.substring(STARTING_INDEX, (string.length() - TWO_POSITIONS)));
        }

        Timber.d("returning: " + new StringBuilder(string));
        return new StringBuilder(string);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return ONE_VIEW_TYPE;
    }

    @Override
    public long getItemId(int position) {
        Timber.d("in getItemId(), position: " + position);
        return cursor.moveToPosition(position) ? cursor.getLong(ID) : position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
