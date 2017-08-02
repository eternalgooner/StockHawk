package com.udacity.stockhawk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.udacity.stockhawk.R;

import java.util.ArrayList;

public class StockHistoryActivity extends AppCompatActivity {

    private BarChart barChart;
    private TextView txtStockName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        barChart = (BarChart) findViewById(R.id.bar_chart);
        txtStockName = (TextView) findViewById(R.id.txt_stock_name);
        txtStockName.setText("Stock history for: " + getIntent().getStringExtra("stockSymbol"));

        BarData barData = getData();
        //barData.setValueTextColor(R.color.colorAccent);
        showChart(barData);
    }

    private void showChart(BarData barData) {
        barChart.setData(barData);
        //barChart.setBackground(getDrawable(R.drawable.fab_plus));
        //barChart.setGridBackgroundColor(R.color.colorAccent);
        barChart.setBackgroundColor(getResources().getColor(R.color.chartBackgroundColour));
        //barChart.setTouchEnabled(true);

        barChart.animateXY(3000, 3000);
    }

    private BarData getData() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(44f, 0));
        barEntries.add(new BarEntry(54f, 1));
        barEntries.add(new BarEntry(54f, 2));
        barEntries.add(new BarEntry(64f, 3));
        barEntries.add(new BarEntry(14f, 4));
        barEntries.add(new BarEntry(24f, 5));
        barEntries.add(new BarEntry(84f, 5));

        BarDataSet barDataSet = new BarDataSet(barEntries, "stock price");

        ArrayList<String> theDates = new ArrayList<>();
        theDates.add("April");
        theDates.add("May");
        theDates.add("June");
        theDates.add("July");
        theDates.add("August");
        theDates.add("September");

        //barDataSet.setStackLabels(theDates.toArray(new String[theDates.size()]));
        barDataSet.setColor(R.color.material_red_700);
        BarData theData = new BarData(barDataSet);

        return theData;
    }
}
