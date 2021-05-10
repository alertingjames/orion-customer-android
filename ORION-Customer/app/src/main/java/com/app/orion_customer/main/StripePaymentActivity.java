package com.app.orion_customer.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_customer.R;
import com.app.orion_customer.base.BaseActivity;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.commons.ReqConst;
import com.app.orion_customer.models.OrderItem;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

public class StripePaymentActivity extends BaseActivity {

    FrameLayout progressBar;
    public static DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);

        progressBar = (FrameLayout) findViewById(R.id.loading_bar);
        progressBar.setVisibility(View.VISIBLE);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        ((TextView)findViewById(R.id.title)).setText("Pay S$" + df.format(Commons.payment.getTotalPrice()));

        WebView webView=(WebView) findViewById(R.id.webview);

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 9_3 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13E233 Safari/601.1");
        webView.setWebChromeClient(new WebChromeClient());

        try{
            List<String> itemIds = new ArrayList<>();
            for(OrderItem item:Commons.payment.getItems()){
                itemIds.add(String.valueOf(item.getId()));
            }
            String itemIdsStr = Arrays.toString(itemIds.toArray());

            webView.loadUrl(ReqConst.SERVER_URL + "paybycard?price=" + String.valueOf(Commons.payment.getTotalPrice()*100)
                    + "&member_id=" + String.valueOf(Commons.thisUser.get_idx())
                    + "&vendor_id=" + String.valueOf(Commons.payment.getItems().get(0).getVendor_id())
                    + "&itemids=" + itemIdsStr.replace(" ","").replace("[","").replace("]","")
                    + "&type=" + "pay"
            );

        }catch (NullPointerException e){
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, 6000);

        new Timer().schedule(doAsynchronousTask, 0, 10000);

        setupUI(findViewById(R.id.activity_checkout_page), this);

    }

    Handler mHandler = new Handler();

    TimerTask doAsynchronousTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        getPaidStatus();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void getPaidStatus() {
        AndroidNetworking.post(ReqConst.SERVER_URL + "getPaid")
                .addBodyParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
                .addBodyParameter("vendor_id", String.valueOf(Commons.payment.getItems().get(0).getVendor_id()))
                .addBodyParameter("store_id", String.valueOf(Commons.payment.getItems().get(0).getStore_id()))
                .addBodyParameter("order_id", String.valueOf(Commons.payment.getItems().get(0).getOrder_id()))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        try {
                            String result = response.getString("result_code");
                            if (result.equals("0")) {
                                showAlertDialog("Info", "Payment is successful!", StripePaymentActivity.this, new Callable<Void>() {
                                    @Override
                                    public Void call() throws Exception {
                                        finish();
                                        return null;
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.setResult(2);
        doAsynchronousTask.cancel();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        this.setResult(2);
    }


}


































