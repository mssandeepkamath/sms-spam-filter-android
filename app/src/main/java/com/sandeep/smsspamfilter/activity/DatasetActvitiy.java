package com.sandeep.smsspamfilter.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sandeep.smsspamfilter.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DatasetActvitiy extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String SERVER_URL = "http://192.168.0.180:5000/message-count";

    private FrameLayout chartContainer;
    private BarChart barChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataset_actvitiy);

        chartContainer = findViewById(R.id.chartContainer);

        // Create the BarChart instance
        barChart = new BarChart(this);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);

        // Add the BarChart to the chart container
        chartContainer.addView(barChart);

        // Make the HTTP request to the Flask server
        makeRequest();
    }

    private void makeRequest() {
        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create the request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, SERVER_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Retrieve the spam and ham counts from the response
                            int spamCount = response.getInt("spam_count");
                            int hamCount = response.getInt("ham_count");

                            // Populate the chart with the retrieved data
                            populateChart(spamCount, hamCount);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DatasetActvitiy.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(DatasetActvitiy.this, "Error retrieving data from server", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue
        queue.add(request);
    }

    private void populateChart(int spamCount, int hamCount) {
        // Create the chart data
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, spamCount));
        entries.add(new BarEntry(1f, hamCount));

        BarDataSet dataSet = new BarDataSet(entries, "Spam vs Ham");

        // Set colors for spam and ham bars
        int[] colors = new int[] { getResources().getColor(android.R.color.holo_red_light),
                getResources().getColor(android.R.color.holo_green_light) };
        dataSet.setColors(colors);

        BarData barData = new BarData(dataSet);

        // Set the data to the BarChart
        barChart.setData(barData);
        barChart.invalidate(); // Refresh the chart to display the updated data
    }
}