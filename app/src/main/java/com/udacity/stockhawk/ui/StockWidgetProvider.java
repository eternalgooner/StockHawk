package com.udacity.stockhawk.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.StockWidgetRemoteViewsService;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class StockWidgetProvider extends AppWidgetProvider {
    public static final String OPEN_APP_FROM_WIDGET_CLICK = "openAppFromWidget";
    public static final String ACTION_CLICK = "actionClick";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.d("in onUpdate(), no. of widgets is: " + appWidgetIds.length);
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Timber.d("in for loop updating widget");

            Intent intent = new Intent(context, StockWidgetRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_widget_grid);
            views.setRemoteAdapter(R.id.stock_widget_grid, intent);

            //pending intent to start app on click widget click
            Intent launchMainActivityintent = new Intent(context, StockWidgetProvider.class);
            launchMainActivityintent.setAction(ACTION_CLICK);
            launchMainActivityintent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, launchMainActivityintent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.stock_widget_grid, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Timber.d("in onEnabled() setting onClick behaviour");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void sendRefreshBroadcast(Context context){
        Timber.d("in sendRefreshBroadcast()");
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, StockWidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("in onReceive()");

        final String action = intent.getAction();
        if(action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
            Timber.d("action == app widget update - update widget manager");
            //refresh all widgets
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, StockWidgetProvider.class);
            manager.notifyAppWidgetViewDataChanged(manager.getAppWidgetIds(componentName), R.id.stock_widget_grid);
        }

        if(action.equals(ACTION_CLICK)){
            Timber.d("match with click on widget!!!!");
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
        super.onReceive(context, intent);
    }
}

