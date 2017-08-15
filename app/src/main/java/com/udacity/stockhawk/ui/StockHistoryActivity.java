package com.udacity.stockhawk.ui;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.udacity.stockhawk.R;

import java.util.ArrayList;

public class StockHistoryActivity extends AppCompatActivity {

    private BarChart barChart;
    private TextView txtStockName;
    private float[] stockHistory;
    private static final int ANIMATE_X_SPEED = 2000;
    private static final int ANIMATE_Y_SPEED = 2000;
    private static final float BAR_WIDTH = .6f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_history);

        stockHistory = getIntent().getExtras().getFloatArray(getString(R.string.stockHistory));

        barChart = (BarChart) findViewById(R.id.bar_chart);
        txtStockName = (TextView) findViewById(R.id.txt_stock_name);
        txtStockName.setText(getString(R.string.stock_hist_label) + getIntent().getStringExtra(getString(R.string.stockSymbol)));

        BarData barData = getData();
        barData.setBarWidth(BAR_WIDTH);

        showChart(barData);
    }

    private void showChart(BarData barData) {
        barChart.setData(barData);
        barChart.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.chartBackgroundColour));
        barChart.enableScroll();
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        YAxis yAxis = barChart.getAxisLeft();
        barChart.animateXY(ANIMATE_X_SPEED, ANIMATE_Y_SPEED);
        barChart.getXAxis().setDrawLabels(false);
    }

    private BarData getData() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for(int i = 0; i < stockHistory.length; ++i){
            barEntries.add(new BarEntry(i, stockHistory[i]));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, getString(R.string.stock_price_in_Dollars));
        BarData theData = new BarData(barDataSet);

        return theData;
    }
}
