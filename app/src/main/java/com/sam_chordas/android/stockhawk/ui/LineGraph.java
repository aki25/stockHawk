package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.robinhood.spark.SparkView;
import com.sam_chordas.android.stockhawk.GraphAdapter;
import com.sam_chordas.android.stockhawk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LineGraph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        String symbol = getIntent().getStringExtra("symbol_name");
        String bid = getIntent().getStringExtra("bid_price");
        TextView symbolText = (TextView) findViewById(R.id.detailSymbol);
        TextView bidTextView = (TextView) findViewById(R.id.detailBid);
        TextView bidValueView = (TextView) findViewById(R.id.detailBidValue);
        symbolText.setText(symbol);
        bidTextView.setText(getString(R.string.bid_info));
        bidValueView.setText(bid);

        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        c.add(Calendar.DATE, -30);
        Date halfYearGap = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String endDate = sdf.format(today);
        final String startDate = sdf.format(halfYearGap);
        final TextView scrubInfo = (TextView) findViewById(R.id.scrubInfo);
        String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22"+symbol+"%22%20and%20startDate%20%3D%20%22"+startDate+"%22%20and%20endDate%20%3D%20%22"+endDate+"%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
        System.out.println("url: "+url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        SparkView sparkView = (SparkView) findViewById(R.id.sparkview);
                        final String DATE = "Date";
                        final String CLOSE = "Close";
                        String graphDates[];
                        float graphClose[];

                        try {
                            JSONObject temp = response.getJSONObject("query");
                            JSONObject temp2 = temp.getJSONObject("results");
                            JSONArray quotes = temp2.getJSONArray("quote");
                            int length = quotes.length();
                            graphClose = new float[length];
                            graphDates = new String[length];
                            for (int i=0;i<length;i++){
                                JSONObject values = quotes.getJSONObject(i);
                                graphDates[i] = values.getString(DATE);
                                graphClose[i] = Float.parseFloat(values.getString(CLOSE));
                            }
                            sparkView.setLineColor(Color.BLUE);
                            sparkView.setLineWidth(7f);
                            scrubInfo.setText(getString(R.string.scrub_help));
                            sparkView.setScrubEnabled(true);
                            sparkView.setScrubListener(new SparkView.OnScrubListener() {
                                @Override
                                public void onScrubbed(Object value) {
                                    if (value == null) {
                                        scrubInfo.setText(getString(R.string.scrub_help));
                                    } else {
                                        scrubInfo.setText(String.valueOf(value));
                                    }
                                }
                            });
                            sparkView.setAdapter(new GraphAdapter(graphClose));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        scrubInfo.setText(getString(R.string.detail_error));
                        //Toast.makeText(LineGraph.this, "Error retrieving data, please check the internet connection!", Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(this).add(jsObjRequest);
    }
}
