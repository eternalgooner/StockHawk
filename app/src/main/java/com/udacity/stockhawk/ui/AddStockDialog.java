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
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class AddStockDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Boolean>{

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.dialog_stock)
    EditText stock;
    private static final int YAHOO_FINANCE_LOADER = 191;
    private Activity parent;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        parent = getActivity();
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
        Timber.d("in addStock()..putting stock query into bundle to pass to Loader");
        Bundle queryStockBundle = new Bundle();
        queryStockBundle.putString(getString(R.string.stockToCheck), stock.getText().toString());

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(YAHOO_FINANCE_LOADER, queryStockBundle, this).forceLoad();

        Timber.d("in addStock - Loader has been executed...await results");
//        Activity parent = getActivity();
//        if (parent instanceof MainActivity) {
//            Timber.d("calling addStock in MainActivity, passing as param: " + stock.getText().toString());
//            ((MainActivity) parent).addStock(stock.getText().toString());
//        }
        dismissAllowingStateLoss();
    }


    @Override
    public Loader<Boolean> onCreateLoader(final int id, final Bundle args) {
        Timber.d("in onCreateLoader() in overridden loader method");
        return new AsyncTaskLoader<Boolean>(this.getActivity().getApplicationContext()) {
            @Override
            public Boolean loadInBackground() {
                Timber.d("in loadInBackground in ATL");
                String stockToCheck = args.getString(getString(R.string.stockToCheck));
                if(id == YAHOO_FINANCE_LOADER){
                    Timber.d("loader ID matched for yahoo finance loader");
                    return isStockValid(stockToCheck);
                }
                return null;
            }

            private Boolean isStockValid(String stockToCheck) {
                Timber.d("in isStockValid in ATL - stock to check is: " + stockToCheck);
                boolean isValidStock = false;
                try {
                    Stock stock = YahooFinance.get(stockToCheck);
                    isValidStock = stock.isValid();
                    if(isValidStock){
                        Timber.d("found VALID stock symbol - return true");
                        onLoadFinished(null, true);
                        return true;
                    }
                }catch (NullPointerException npe){
                    Timber.e("NullPointerException thrown - trying to get stock from YahooFinance");
                    npe.printStackTrace();
                }catch (IOException e) {
                    Timber.e("error in addStock try block, checking if stock request is INVALID");
                    e.printStackTrace();
                }
                Timber.d("returning false from ATL - invalid stock");
                onLoadFinished(null, false);
                return false;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        Timber.d("in onLoadFinished - passed boolean value from ATL: " + data);
        if (parent instanceof MainActivity && data) {
            Timber.d("parent is an instance of MainActivity");
            Timber.d("in onLoadFinished in ATL - stock is valid & can now add to stock list");
            ((MainActivity) parent).addStock(stock.getText().toString(), data);
        }else {
            Timber.d("in onLoadFinished in ATL - parent is not instance of MainActivity & won't be added to stock list");
            //Looper.prepare();
            //Toast.makeText(parent.getApplicationContext(), "The stock symbol you entered does not exist, please try another", Toast.LENGTH_SHORT).show();
            ((MainActivity) parent).addStock(stock.getText().toString(), data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {
        Timber.d("in onLoaderReset() in loader method");
    }
}
