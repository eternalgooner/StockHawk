package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener,
        StockAdapter.StockAdapterOnClickHandler {

    private static final int STOCK_LOADER = 0;
    private static final int HISTORY = 5;
    private static final int FIRST_ELEMENT = 1;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recycler_view)
    RecyclerView stockRecyclerView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.error)
    TextView error;
    private StockAdapter adapter;
    private float[] selectedStockHistoryFloatArray;

    @Override
    public void onClick(String symbol) {
        Timber.d("Symbol clicked: %s", symbol);
        getAllHistoryAndDisplay();
        Intent intent = new Intent(this, StockHistoryActivity.class);
        intent.putExtra(getString(R.string.stockSymbol), symbol);
        intent.putExtra(getString(R.string.stockHistory), selectedStockHistoryFloatArray);
        startActivity(intent);
    }

    
    // TODO REQUIREMENT  Data Persistence - spurious behaviour with the storing of Stocks
    // TODO REQUIREMENT  e.g. if not stocks selected, app removed from stack, no data connection, app started, data connection up - back to default stock quotes
    // TODO REQUIREMENT  e.g.2. during add/remove of stocks some persist and some don't - launching app from widget shows different stock quotes between widget and app
    // TODO-2 Good job!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("in onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new StockAdapter(this, this);
        stockRecyclerView.setAdapter(adapter);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout.setOnRefreshListener(this);
        //swipeRefreshLayout.setRefreshing(true);
        onRefresh();

        //QuoteSyncJob.initialize(this);
        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                String symbol = adapter.getSymbolAtPosition(viewHolder.getAdapterPosition());
                PrefUtils.removeStock(MainActivity.this, symbol);
                getContentResolver().delete(Contract.Quote.makeUriForStock(symbol), null, null);
            }
        }).attachToRecyclerView(stockRecyclerView);

        getAllHistoryAndDisplay();
        StockWidgetProvider.sendRefreshBroadcast(this);
    }

    private void getAllHistoryAndDisplay() {
        Timber.d("in getAllHistoryAndDisplay()");
        Cursor cursor = getAllhistory();
        displayCursor(cursor);
    }

    private void displayCursor(Cursor cursor) {
        Timber.d("in displayCursor()");
        Timber.d("cursor count is: " + cursor.getCount());

        while (cursor.moveToNext()){
            initAllStockHistory(cursor.getString(HISTORY));
        }
    }

    private void initAllStockHistory(String history) {
        Timber.d("in initAllStockHistory()");
        ArrayList<float[]> stockHistory = new ArrayList<>();
        stockHistory.add(convertStringHistoryToFloatArray(history));
    }

    private float[] convertStringHistoryToFloatArray(String history) {
        Timber.d("in convertStringHistoryToFloatArray()");
        String[] strArray = (history.split(getString(R.string.newLine)));

        ArrayList<String[]> stArrList = new ArrayList<>();

        for(String str: strArray){
            stArrList.add(str.split(getString(R.string.comma)));
        }
        Timber.d("size of split array is: " + stArrList.size());

        selectedStockHistoryFloatArray = new float[stArrList.size()];
        Timber.d("checking if selectedStockHistoryFloatArray is null");

        for(int i = 0; i < stArrList.size(); ++i){
            selectedStockHistoryFloatArray[i] = Float.parseFloat(stArrList.get(i)[FIRST_ELEMENT].trim());
        }
        return selectedStockHistoryFloatArray;
    }

    private Cursor getAllhistory() {
        try{
            return getContentResolver().query(Contract.Quote.URI,
                    null,
                    null,
                    null,
                    null);
        }catch (Exception e){
            Timber.d("failed to get all history from DB");
            e.printStackTrace();
            return null;
        }
    }

    private boolean networkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onRefresh() {

        QuoteSyncJob.syncImmediately(this);

        if (!networkUp() && adapter.getItemCount() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_network));
            error.setVisibility(View.VISIBLE);
        } else if (!networkUp()) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, R.string.toast_no_connectivity, Toast.LENGTH_LONG).show();
        } else if (PrefUtils.getStocks(this).size() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_stocks));
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
        }
    }

    public void button(@SuppressWarnings("UnusedParameters") View view) {
        new AddStockDialog().show(getFragmentManager(), getString(R.string.StockDialogFragment));
    }

    void addStock(String symbol, boolean isValid) {
        Timber.d("in addStock(), symbol received is: " + symbol.toString());
        if (isValid) {

            if (networkUp()) {
                Timber.d("in addStock(), network is up, will refresh layout");
                refreshUi();
                StockWidgetProvider.sendRefreshBroadcast(this);
            } else {
                String message = getString(R.string.toast_stock_added_no_connectivity, symbol);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }

            Timber.d("calling PrefUtils.addStock passing args: " + this + " and: " + symbol);
            PrefUtils.addStock(this, symbol);
            QuoteSyncJob.syncImmediately(this);
        }else {
            Timber.d("if invalid stock symbol, show toast message");
            showToast();
        }
        //TODO SUGGESTION Add an existing stock gives inaccurate error messages:
        //TODO SUGGESTION - when you add an already included stock it says "does not exist"
        //TODO SUGGESTION - when you add a stock with no data connection "The stock symbol you entered does not exist"

    }

    private void refreshUi() {
       this.runOnUiThread(new Runnable() {
           @Override
           public void run() {
               swipeRefreshLayout.setRefreshing(true);
           }
       });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swipeRefreshLayout.setRefreshing(false);

        if (data.getCount() != 0) {
            error.setVisibility(View.GONE);
        }
        adapter.setCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.setCursor(null);
    }


    private void setDisplayModeMenuItemIcon(MenuItem item) {
        if (PrefUtils.getDisplayMode(this)
                .equals(getString(R.string.pref_display_mode_absolute_key))) {
            item.setIcon(R.drawable.ic_percentage);
        } else {
            item.setIcon(R.drawable.ic_dollar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_settings, menu);
        MenuItem item = menu.findItem(R.id.action_change_units);
        setDisplayModeMenuItemIcon(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change_units) {
            PrefUtils.toggleDisplayMode(this);
            setDisplayModeMenuItemIcon(item);
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), R.string.unfound_stock_msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
