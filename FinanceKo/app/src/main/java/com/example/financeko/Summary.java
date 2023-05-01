package com.example.financeko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class Summary extends AppCompatActivity {

    private Button backBtn;
    private ArrayList barArrayList, barArrayListFrequency;

    private List<Entry> entries;

    private String[] months = {"January", "February", "March", "April", "May", "June", "July","August","September","October","November","December"};

    private TextView mostSpentCategoryValue, leastSpentCategoryValue, totalOneTimeExpenseValue, totalDailyExpenseValue, totalMonthlyExpenseValue, totalYearlyExpenseValue, estimatedMonthlyExpense, estimatedYearlyExpense;

    private List<TextView> textViewList = new ArrayList<>();
    private TextView JanuaryValue, FebruaryValue, MarchValue, AprilValue, MayValue, JuneValue, JulyValue, AugustValue,SeptemberValue, OctoberValue, NovemberValue, DecemberValue;

    private String selectedFrequency, selectedCategory;

    private String[] frequencyOptions = {"Once", "Daily", "Monthly", "Yearly", "All"}; //Added "All"
    private String[] cateogryOptions = {"Transportation", "Bills", "Necessity", "Food", "Others", "All"};//Added all
    DBHelper DB;

    private LineChart lineChart;

    AutoCompleteTextView categoryDropDown, frequencyDropDown;
    ArrayAdapter<String> adapterItemCategory, adapterItemFrequency;

    int currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        mostSpentCategoryValue = findViewById(R.id.mostSpentCategoryValue);
        leastSpentCategoryValue = findViewById(R.id.leastSpentCategoryValue);
        totalOneTimeExpenseValue = findViewById(R.id.totalOneTimeExpenseValue);
        totalDailyExpenseValue = findViewById(R.id.totalDailyExpenseValue);
        totalMonthlyExpenseValue = findViewById(R.id.totalMonthlyExpenseValue);
        totalYearlyExpenseValue = findViewById(R.id.totalYearlyExpenseValue);
        frequencyDropDown = findViewById(R.id.frequency_dropdown);
        JanuaryValue = findViewById(R.id.JanuaryValue);
        FebruaryValue = findViewById(R.id.FebruaryValue);
        MarchValue = findViewById(R.id.MarchValue);
        AprilValue = findViewById(R.id.AprilValue);
        MayValue = findViewById(R.id.MayValue);
        JuneValue = findViewById(R.id.JuneValue);
        JulyValue = findViewById(R.id.JulyValue);
        AugustValue = findViewById(R.id.AugustValue);
        SeptemberValue = findViewById(R.id.SeptemberValue);
        OctoberValue = findViewById(R.id.OctoberValue);
        NovemberValue = findViewById(R.id.NovemberValue);
        DecemberValue = findViewById(R.id.DecemberValue);
        estimatedMonthlyExpense = findViewById(R.id.estimatedMonthlyExpenseValue);
        estimatedYearlyExpense = findViewById(R.id.estimatedYearlyExpenseValue);

        textViewList.add(JanuaryValue);
        textViewList.add(FebruaryValue);
        textViewList.add(MarchValue);
        textViewList.add(AprilValue);
        textViewList.add(MayValue);
        textViewList.add(JuneValue);
        textViewList.add(JulyValue);
        textViewList.add(AugustValue);
        textViewList.add(SeptemberValue);
        textViewList.add(OctoberValue);
        textViewList.add(NovemberValue);
        textViewList.add(DecemberValue);

        backBtn = findViewById(R.id.backBtn);
        DB = new DBHelper(this);

        selectedFrequency = "All";
        selectedCategory = "All";

        //Dropdown Menu FREQUENCY
        frequencyDropDown = findViewById(R.id.frequency_dropdown);
        adapterItemFrequency = new ArrayAdapter<String >(this, android.R.layout.simple_list_item_1, frequencyOptions);
        frequencyDropDown.setAdapter(adapterItemFrequency);

        //Get current user
        Log.d("MainActivity.loginUser","UserID: " + DB.getUserId(MainActivity.loginUser));
        currentUserID = DB.getUserId(MainActivity.loginUser);

        //Initialize Barchart
        BarChart barChart = findViewById(R.id.barchart);
        getData("All");
        BarChart barchartFrequency = findViewById((R.id.barchart_frequency));
        getDataFrequency();

        //Made into their own functions to reduce clutter
        modifyBarChart(barChart, barArrayList);
        modifyBarChart(barchartFrequency, barArrayListFrequency);
        modfiyLegend(barChart, AddTransaction.Categories);
        modfiyLegend(barchartFrequency, AddTransaction.Frequency);

        //Initialize line chart
        lineChart = findViewById(R.id.lineChart);
        getDataLineChart(lineChart);
        modifyLineChart(lineChart);

        //Set values of text views
        setMostAndLeastSpentCategoryValues();//Least and Most Spent category
        setFrequencyValues(); //One time, Daily, Monthly, Yearly

        frequencyDropDown.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFrequency = adapterView.getItemAtPosition(i).toString();
                getData(selectedFrequency);
                modifyBarChart(barChart, barArrayList);
                barChart.notifyDataSetChanged();
                barChart.invalidate();
                Log.e("frequencyDropDown", "frequencyDropDown has been set: " + selectedFrequency);
            }
        }));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Summary.this,HomePageActivity.class);
                startActivity(intent);
                Summary.this.finish();
            }
        });
    }

    private void modifyLineChart(LineChart lineChart) {
        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(12f);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawBorders(false);
        lineChart.getLegend().setEnabled(false);
        XAxis xAxis = lineChart.getXAxis();
        //xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setLabelCount(months.length);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

    }

    private void getDataLineChart(LineChart lineChart){
        entries = new ArrayList<>();

        int[] monthSumList = new int[12];
        int xAxis = 0;
        //Make x values of line chart to be the sum of the transactions of each month
        //Get transaction sum of each month
        for (int x=0; x < monthSumList.length; x++){
            monthSumList[x] = DB.getSumMonth(currentUserID,x+1);
            //Set data of months
            TextView currentTextView = textViewList.get(x);
            currentTextView.setText(Integer.toString(monthSumList[x]));
            Log.e("monthSumList", months[x] + " : " + monthSumList[x]);
        }

        for(int x=0; x<monthSumList.length; x++){
            xAxis = xAxis +2;
            entries.add(new Entry(xAxis,monthSumList[x]));
        }
    }

    private void setFrequencyValues(){
        //Frequency
        String[] frequency = AddTransaction.Frequency;
        int[] frequencySumList = new int[4];
        frequencySumList[0] = DB.getSumFrequency(currentUserID,frequency[0]);//Once
        frequencySumList[1] = DB.getSumFrequency(currentUserID,frequency[1]);//Daily
        frequencySumList[2] = DB.getSumFrequency(currentUserID,frequency[2]);//Monthly
        frequencySumList[3] = DB.getSumFrequency(currentUserID,frequency[3]);//Yearly

        totalOneTimeExpenseValue.setText(String.valueOf(frequencySumList[0]));
        totalDailyExpenseValue.setText(String.valueOf(frequencySumList[1]));
        totalMonthlyExpenseValue.setText(String.valueOf(frequencySumList[2]));
        totalYearlyExpenseValue.setText(String.valueOf(frequencySumList[3]));
    }
    private void setMostAndLeastSpentCategoryValues(){
        //Get most spent category
        String[] category = AddTransaction.Categories;
        int[] categoriesSumList = new int[5];
        categoriesSumList[0] = DB.getSumCategory(currentUserID,category[0]);//transportation
        categoriesSumList[1] = DB.getSumCategory(currentUserID,category[1]);//bills
        categoriesSumList[2] = DB.getSumCategory(currentUserID,category[2]);//necessity
        categoriesSumList[3] = DB.getSumCategory(currentUserID,category[3]);//food
        categoriesSumList[4] = DB.getSumCategory(currentUserID,category[4]);//others

        int largestCategoryValue = 0;
        int smallestCategoryValue = 0;
        String largestCategory = "None";
        String smallestCategory = "None";
        for(int count = 0; count <categoriesSumList.length;count++) {
            if (largestCategoryValue < categoriesSumList[count]) {
                largestCategoryValue = categoriesSumList[count];
                largestCategory = category[count];
                Log.e("largestCategoryValue", largestCategoryValue + "=" + largestCategory);
            }
            //initalize smallestCateogry
            if (count == 0) {
                smallestCategoryValue = categoriesSumList[count];
                smallestCategory = category[count];
                Log.e("leastSpentCategoryValue", "Initial value " + smallestCategoryValue + "=" + smallestCategory);
            }

            Log.e("leastSpentCategoryValue", smallestCategoryValue + ">" + categoriesSumList[count]);
            if (smallestCategoryValue > categoriesSumList[count]) {
                smallestCategoryValue = categoriesSumList[count];
                smallestCategory = category[count];
                Log.e("leastSpentCategoryValue", smallestCategoryValue + "=" + smallestCategory);
            }
        }

        mostSpentCategoryValue.setText(largestCategory);
        leastSpentCategoryValue.setText(smallestCategory);
    }

    private void getData(String selectedFrequency){
        barArrayList = new ArrayList();

        float xAxis = 0;
        float yAxis = 0;
        String[] category = AddTransaction.Categories;

        //Make x axis be the sum of each category

        //Get total amount of each category
        int[] categoriesSumList = new int[5];
        categoriesSumList[0] = DB.getSumCategoryWithFrequencyFilter(currentUserID,category[0],selectedFrequency);//transportation
        categoriesSumList[1] = DB.getSumCategoryWithFrequencyFilter(currentUserID,category[1],selectedFrequency);//bills
        categoriesSumList[2] = DB.getSumCategoryWithFrequencyFilter(currentUserID,category[2],selectedFrequency);//necessity
        categoriesSumList[3] = DB.getSumCategoryWithFrequencyFilter(currentUserID,category[3],selectedFrequency);//food
        categoriesSumList[4] = DB.getSumCategoryWithFrequencyFilter(currentUserID,category[4],selectedFrequency);//others

        for(int count=0; count<AddTransaction.Categories.length; count++){
            xAxis = xAxis +2;
            yAxis = yAxis +20;
            barArrayList.add(new BarEntry(xAxis,categoriesSumList[count]));
        }

        Log.e("barArrayList", "Final array count: " + barArrayList.size());
        Log.e("getData", "Data has been gathered with the filter: " + selectedFrequency);
    }

    private void getDataFrequency(){
        barArrayListFrequency = new ArrayList();

        float xAxis = 0;
        float yAxis = 0;
        String[] category = AddTransaction.Frequency;

        //Make y axis be the sum of each Frequency

        //Get total amount of each category
        int[] frequencySumList = new int[5];
        frequencySumList[0] = DB.getSumFrequency(currentUserID,category[0]);//Once
        frequencySumList[1] = DB.getSumFrequency(currentUserID,category[1]);//Daily
        frequencySumList[2] = DB.getSumFrequency(currentUserID,category[2]);//Monthly
        frequencySumList[3] = DB.getSumFrequency(currentUserID,category[3]);//Yearly

        for(int count=0; count<AddTransaction.Frequency.length; count++){
            xAxis = xAxis +2;
            yAxis = yAxis +20;
            barArrayListFrequency.add(new BarEntry(xAxis,frequencySumList[count]));
        }
        String monthExpense = Integer.toString((frequencySumList[1] * 30) + frequencySumList[2]);
        String yearExpense = Integer.toString((frequencySumList[1] * 30) + (frequencySumList[2] * 12) + frequencySumList[3]);
        estimatedMonthlyExpense.setText(monthExpense);
        estimatedYearlyExpense.setText(yearExpense);
    }
    private void modifyBarChart(BarChart barChart, ArrayList barArrayList){
        BarDataSet barDataSet = new BarDataSet(barArrayList, "");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);
        //barChart.setDrawGridBackground(false);

        //Modify X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);

        //Modify Y-axis
        YAxis leftYAxis = barChart.getAxisLeft();
        YAxis rightYAxis = barChart.getAxisRight();

        //leftYAxis.setEnabled(false);
        rightYAxis.setEnabled(false);
        leftYAxis.setTextSize(16f);
        leftYAxis.setGranularity(20f);

        Log.e("modifyBarChart", barChart + " has been updated");
    }
    private void modfiyLegend(BarChart barChart, String[] arraylist){
        // Get reference to the chart's legend
        Legend legend = barChart.getLegend();

        // Create an array to hold the legend entries
        LegendEntry[] legendEntries = new LegendEntry[arraylist.length];

        // Loop through the Categories array and create a new legend entry for each category
        for (int i = 0; i < arraylist.length; i++) {
            LegendEntry entry = new LegendEntry();
            entry.label = arraylist[i];
            entry.formColor = ColorTemplate.COLORFUL_COLORS[i];
            legendEntries[i] = entry;
        }

        // Set the modified legend entries
        legend.setFormSize(16f);
        legend.setTextSize(21f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setYOffset(10f);
        legend.setXOffset(0f);
        legend.setYEntrySpace(0f);
        legend.setXEntrySpace(16f);
        legend.setWordWrapEnabled(true);
        legend.setCustom(legendEntries);
    }
}