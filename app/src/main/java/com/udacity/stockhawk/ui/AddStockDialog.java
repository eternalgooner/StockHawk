package com.udacity.stockhawk.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.udacity.stockhawk.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import yahoofinance.YahooFinance;


public class AddStockDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Boolean>{

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.dialog_stock)
    EditText stock;
    private static final int YAHOO_FINANCE_LOADER = 191;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") View custom = inflater.inflate(R.layout.add_stock_dialog, null);

        ButterKnife.bind(this, custom);

        stock.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addStock();
                return true;
            }
        });
        builder.setView(custom);

        builder.setMessage(getString(R.string.dialog_title));
        builder.setPositiveButton(getString(R.string.dialog_add),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addStock();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);

        Dialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return dialog;
    }

    private void addStock() {
        //TODO perform check here to see if user stock is real/available
        Bundle queryStockBundle = new Bundle();
        queryStockBundle.putString("stockToCheck", stock.getText().toString());

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(YAHOO_FINANCE_LOADER, queryStockBundle, this).forceLoad();

        Timber.d("in addStock - beginning to add stock");
        Activity parent = getActivity();
        if (parent instanceof MainActivity) {
            Timber.d("calling addStock in MainActivity, passing as param: " + stock.getText().toString());
            ((MainActivity) parent).addStock(stock.getText().toString());
        }
        dismissAllowingStateLoss();
    }


    @Override
    public Loader<Boolean> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<Boolean>(this.getActivity().getApplicationContext()) {
            @Override
            public Boolean loadInBackground() {
                String stockToCheck = args.getString("stockToCheck");
                if(id == YAHOO_FINANCE_LOADER){
                    return isStockValid(stockToCheck);
                }
                return null;
            }

            //TODO left off here - checking if stock is valid - using ATL - might need to go back to main activity & put method calls in onPostExecute()
            //TODO so they only happen if it's valid or not
            private Boolean isStockValid(String stockToCheck) {
                boolean isValidStock = false;
                try {
                    isValidStock = YahooFinance.get(stock.getText().toString()).isValid();
                    if(isValidStock){
                        Timber.d("trying to add new stock, calling YahooFinance.get(" + stock.getText().toString() + ") returned VALID. Exiting addStock()");
                        return true;
                    }
                }catch (NullPointerException npe){
                    Timber.e("NullPointerException thrown - not valid stock");
                    npe.printStackTrace();
                } catch (IOException e) {
                    Timber.e("error in addStock try block, checking if stock request is INVALID");
                    e.printStackTrace();
                }
                Timber.d("returning false from ATL - invalid stock");
                return false;
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {

    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {

    }


}
