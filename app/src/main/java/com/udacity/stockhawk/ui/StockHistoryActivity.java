package com.udacity.stockhawk.ui;

import android.graphics.DashPathEffect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.udacity.stockhawk.R;

import java.util.ArrayList;
import java.util.Calendar;

public class StockHistoryActivity extends AppCompatActivity {

    private BarChart barChart;
    private TextView txtStockName;
    private float[] stockHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        stockHistory = getIntent().getExtras().getFloatArray("stockHistory");

        barChart = (BarChart) findViewById(R.id.bar_chart);
        txtStockName = (TextView) findViewById(R.id.txt_stock_name);
        txtStockName.setText("Stock history for: " + getIntent().getStringExtra("stockSymbol"));

        BarData barData = getData();
        barData.setBarWidth(.6f);
        barData.setValueTextColor(R.color.colorAccent);

        showChart(barData);
    }

    private void showChart(BarData barData) {
        barChart.setData(barData);
        //barChart.setBackground(getDrawable(R.drawable.fab_plus));
        //barChart.setGridBackgroundColor(R.color.colorAccent);
        barChart.setBackgroundColor(getResources().getColor(R.color.chartBackgroundColour));
        barChart.enableScroll();
        barChart.setHorizontalScrollBarEnabled(true);
        barChart.isHorizontalScrollBarEnabled();
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);


        YAxis yAxis = barChart.getAxisLeft();


        //yAxis.setTextSize(12);

        //Legend legend = barChart.getLegend();
        //legend.setTextSize(15);


        //ArrayList<LegendEntry> theDates = new ArrayList<>();
        //theDates.add(new LegendEntry("Date", null, 15, 15, new DashPathEffect(new float[]{2f}, 3f), 5));
        //theDates.add(new LegendEntry());

       // legend.setEntries(theDates);
        //barChart.setAutoScaleMinMaxEnabled(true);
        //barChart.setTouchEnabled(true);

        barChart.animateXY(2000, 2000);
        barChart.getXAxis().setDrawLabels(false);

    }

    private BarData getData() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for(int i = 0; i < stockHistory.length; ++i){
            barEntries.add(new BarEntry(i, stockHistory[i]));
        }
//        barEntries.add(new BarEntry(44f, 0));
//        barEntries.add(new BarEntry(54f, 1));
//        barEntries.add(new BarEntry(54f, 2));
//        barEntries.add(new BarEntry(64f, 3));
//        barEntries.add(new BarEntry(14f, 4));
//        barEntries.add(new BarEntry(24f, 5));
//        barEntries.add(new BarEntry(84f, 5));

        BarDataSet barDataSet = new BarDataSet(barEntries, "stock price in Dollars");
        //barDataSet.setColor(R.color.material_red_700);
        //barDataSet.setStackLabels(new String[]{"may", "june", "july"});

//        ArrayList<String> theDates = new ArrayList<>();
//        theDates.add("April");
//        theDates.add("May");
//        theDates.add("June");
//        theDates.add("July");
//        theDates.add("August");
//        theDates.add("September");

        //barDataSet.setStackLabels(theDates.toArray(new String[theDates.size()]));
        barDataSet.setColor(R.color.material_red_700);
        BarData theData = new BarData(barDataSet);

        return theData;
    }
}
