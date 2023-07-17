package com.sandeep.smsspamfilter.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;
        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonObjectRequest;
        import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sandeep.smsspamfilter.R;
import com.sandeep.smsspamfilter.adapter.SMSAdapter;
import com.sandeep.smsspamfilter.model.Message;
import com.sandeep.smsspamfilter.util.SMSFetcher;

import org.json.JSONException;
        import org.json.JSONObject;
        import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_READ_SMS = 1;
    private RecyclerView recyclerView;
    private SMSAdapter smsAdapter;
    private List<Message> smsList;

    private TextView hider;


    private static final String FLASK_SERVER_URL = "http://192.168.0.180:5000/predict";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        hider = findViewById(R.id.hider);

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0) {
//                    // Scrolling down
//                    hider.setVisibility(View.GONE);
//                } else if (dy < 0) {
//                    // Scrolling up
//                    hider.setVisibility(View.VISIBLE);
//                }
//            }
//        });


        smsAdapter = new SMSAdapter(smsList);
        recyclerView.setAdapter(smsAdapter);


        if (hasReadSmsPermission()) {
            fetchSMS();
        } else {
            requestReadSmsPermission();
        }

        smsAdapter.setOnItemClickListener(new SMSAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ImageView imageView) {
                Message sms = smsList.get(position);
                loadGif(MainActivity.this,imageView);
                sendApiRequest(sms,position);
            }
        });
    }

    private boolean hasReadSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS},
                PERMISSION_REQUEST_READ_SMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchSMS();
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchSMS() {
        smsList = SMSFetcher.fetchSMS(this);
        smsAdapter.setSmsList(smsList);
        smsAdapter.notifyDataSetChanged();
    }

    private void sendApiRequest(Message sms,int position) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("message", sms.getBody());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, FLASK_SERVER_URL, requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String decisionTreeResult = response.getString("decision_tree");
//                                String logisticRegressionResult = response.getString("logistic_regression");
                                String naiveBayesResult = response.getString("naive_bayes");
                                String svcResult = response.getString("svc");

                                String spamResult;
                                if ((decisionTreeResult.equals("spam") && (naiveBayesResult.equals("spam") || svcResult.equals("spam"))) ||
                                        (naiveBayesResult.equals("spam") && svcResult.equals("spam"))) {
                                    spamResult = "spam";
                                } else {
                                    spamResult = "ham";
                                }
                                Toast.makeText(MainActivity.this, "SMS is: " + spamResult, Toast.LENGTH_SHORT).show();
                                int result = (spamResult.equals("spam"))? 1:0;
                                smsList.set(position,new Message(sms.getBody(),sms.getAddress(),result));
                                smsAdapter.setSmsList(smsList);
                                smsAdapter.notifyDataSetChanged();
                                // You can update the SMS item view here based on the spamResult value
                            } catch (JSONException e) {
                                Log.e("net",e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, "Server error, Please try again later", Toast.LENGTH_SHORT).show();
                        }
                    });

            Volley.newRequestQueue(this).add(request);
        } catch (JSONException e) {
            Log.e("net",e.toString());
        }
    }
    private void loadGif(Context context, ImageView imageView) {

        Glide.with(context)
                .asGif()
                .load(R.drawable.anim)
                .into(new ImageViewTarget<GifDrawable>(imageView) {
                    @Override
                    protected void setResource(GifDrawable resource) {
                        imageView.setImageDrawable(resource);
                    }

                    @Override
                    public void onResourceReady(GifDrawable resource, Transition<? super GifDrawable> transition) {
                        super.onResourceReady(resource, transition);
                        resource.start();
                    }
                });
    }


}
