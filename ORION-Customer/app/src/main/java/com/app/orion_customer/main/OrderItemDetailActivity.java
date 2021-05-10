package com.app.orion_customer.main;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_customer.R;
import com.app.orion_customer.base.BaseActivity;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.commons.Constants;
import com.app.orion_customer.commons.ReqConst;
import com.app.orion_customer.fragments.PDetailFragment;
import com.app.orion_customer.models.Destination;
import com.app.orion_customer.models.OrderItem;
import com.app.orion_customer.models.Payment;
import com.app.orion_customer.models.Product;
import com.app.orion_customer.models.Store;
import com.app.orion_customer.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderItemDetailActivity extends BaseActivity {

    TextView orderIDBox, orderDateBox, orderStatusBox, phoneBox, addressBox, addressLineBox, subTotalBox, shippingBox, totalBox, bonusBox, deliveryBox, quantityBox;
    RoundedImageView pictureBox;
    TextView paymentStatusBox;
    ImageView payButton, driverButton;
    LinearLayout bonusFrame;
    public static DecimalFormat df = new DecimalFormat("0.00");
    double deliveryPrice = 0.0d;
    FrameLayout progressBar;
    double totalPrice = 0.0d;
    double subTotalPrice = 0.0d;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_item_detail);

        progressBar = (FrameLayout)findViewById(R.id.loading_bar);

        paymentStatusBox = (TextView)findViewById(R.id.payment_status);
        paymentStatusBox.setTypeface(bold);

        payButton = (ImageView)findViewById(R.id.btn_pay);
        driverButton = (ImageView)findViewById(R.id.btn_driver);

        if(Commons.orderItem.getPayment_status().equals("pay")){
            payButton.setVisibility(View.GONE);
            driverButton.setVisibility(View.VISIBLE);
            paymentStatusBox.setVisibility(View.VISIBLE);
            paymentStatusBox.setText("(PAID)");
            paymentStatusBox.setTextColor(Color.GREEN);
        }
        else {
            if(Commons.orderItem.getStatus().equals("ready") || Commons.orderItem.getStatus().equals("delivered")) {
                payButton.setVisibility(View.VISIBLE);
                driverButton.setVisibility(View.VISIBLE);
                paymentStatusBox.setVisibility(View.GONE);
            }else {
                payButton.setVisibility(View.GONE);
                driverButton.setVisibility(View.GONE);
                paymentStatusBox.setVisibility(View.VISIBLE);
                paymentStatusBox.setText("(UNPAID)");
                paymentStatusBox.setTextColor(Color.GRAY);
            }
        }

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption1)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption3)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption4)).setTypeface(normal);

        ((TextView)findViewById(R.id.caption5)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption6)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption7)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption8)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption9)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption10)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption11)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption12)).setTypeface(normal);

        orderIDBox = (TextView)findViewById(R.id.order_number);
        orderDateBox = (TextView)findViewById(R.id.order_date);
        phoneBox = (TextView)findViewById(R.id.phone);
        addressBox = (TextView)findViewById(R.id.address);
        addressLineBox = (TextView)findViewById(R.id.address2);
        subTotalBox = (TextView)findViewById(R.id.subtotal_price);
        shippingBox = (TextView)findViewById(R.id.shipping_price);
        totalBox = (TextView)findViewById(R.id.total_price);
        bonusFrame = (LinearLayout)findViewById(R.id.bonusFrame);
        bonusBox = (TextView)findViewById(R.id.bonus);
        deliveryBox = (TextView)findViewById(R.id.deliveryBox);

        orderStatusBox = (TextView)findViewById(R.id.order_status);
        orderStatusBox.setTypeface(bold);
        orderStatusBox.setText(Commons.orderStatus.statusStr.get(Commons.orderItem.getStatus()));

        pictureBox = (RoundedImageView)findViewById(R.id.pictureBox);
        quantityBox = (TextView)findViewById(R.id.quantityBox);

        Glide.with(getApplicationContext()).load(Commons.orderItem.getPicture_url()).into(pictureBox);
        quantityBox.setText("X " + String.valueOf(Commons.orderItem.getQuantity()));
        pictureBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productInfo(String.valueOf(Commons.orderItem.getProduct_id()));
            }
        });

        if(Commons.orderItem.getDiscount() > 0)bonusFrame.setVisibility(View.VISIBLE);

        orderIDBox.setText(Commons.orderItem.getOrderID());
        orderDateBox.setTypeface(normal);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        String date = dateFormat.format(new Date(Long.parseLong(Commons.orderItem.getDate_time())));
        orderDateBox.setText(date);
        phoneBox.setText(Commons.orderItem.getContact());
        addressBox.setText(Commons.order.getAddress());
        addressLineBox.setText(Commons.order.getAddress_line());

        deliveryBox.setText(String.valueOf(Commons.orderItem.getDelivery_days()) + " Days, " + String.valueOf(Commons.orderItem.getDelivery_price()) + " SGD");
        deliveryPrice = Commons.orderItem.getDelivery_price();

        subTotalPrice = (Commons.orderItem.getPrice()*(1- Commons.orderItem.getDiscount()/100))*Commons.orderItem.getQuantity();
        subTotalBox.setText(df.format(subTotalPrice) + " " + Constants.currency);
        bonusBox.setText("-" + df.format(Commons.orderItem.getDiscount()) + "%");
        shippingBox.setText(String.valueOf(deliveryPrice) + " " + Constants.currency);
        totalBox.setText(df.format(subTotalPrice + deliveryPrice) + " " + Constants.currency);

        totalPrice = subTotalPrice + deliveryPrice;

        phoneBox.setTypeface(bold);
        addressBox.setTypeface(bold);
        addressLineBox.setTypeface(normal);
        ((TextView)findViewById(R.id.method)).setTypeface(bold);
        ((TextView)findViewById(R.id.subtotal_price)).setTypeface(bold);
        ((TextView)findViewById(R.id.shipping_price)).setTypeface(bold);
        ((TextView)findViewById(R.id.total_price)).setTypeface(bold);
        deliveryBox.setTypeface(bold);

    }

    public void back(View view){
        onBackPressed();
    }

    public void productInfo(String productId) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "productInfo")
                .addBodyParameter("product_id", productId)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        progressBar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                JSONObject object = response.getJSONObject("product");
                                Product product = new Product();
                                product.setIdx(object.getInt("id"));
                                product.setStoreId(object.getInt("store_id"));
                                product.setUserId(object.getInt("member_id"));
                                product.setBrandId(object.getInt("brand_id"));
                                product.setName(object.getString("name"));
                                product.setPicture_url(object.getString("picture_url"));
                                product.setCategory(object.getString("category"));
                                product.setSubcategory(object.getString("subcategory"));
                                product.setGender(object.getString("gender"));
                                product.setGenderKey(object.getString("gender_key"));
                                product.setPrice(Double.parseDouble(object.getString("price")));
                                product.setNew_price(Double.parseDouble(object.getString("new_price")));
                                product.setUnit(object.getString("unit"));
                                product.setDescription(object.getString("description"));
                                product.setRegistered_time(object.getString("registered_time"));
                                product.setStatus(object.getString("status"));
                                product.setBrand_name(object.getString("brand_name"));
                                product.setBrand_logo(object.getString("brand_logo"));
                                product.setDelivery_price(Double.parseDouble(object.getString("delivery_price")));
                                product.setDelivery_days(Integer.parseInt(object.getString("delivery_days")));

                                product.setLikes(object.getInt("likes"));
                                product.setRatings(Float.parseFloat(object.getString("ratings")));


                                object = response.getJSONObject("store");
                                Store store = new Store();
                                store.setId(object.getInt("id"));
                                store.setUserId(object.getInt("member_id"));
                                store.setName(object.getString("name"));
                                store.setPhoneNumber(object.getString("phone_number"));
                                store.setAddress(object.getString("address"));
                                store.setRatings(Float.parseFloat(object.getString("ratings")));
                                store.setReviews(object.getInt("reviews"));
                                store.setLogoUrl(object.getString("logo_url"));
                                store.set_registered_time(object.getString("registered_time"));
                                store.set_status(object.getString("status"));
                                double lat = 0.0d, lng = 0.0d;
                                if(object.getString("latitude").length() > 0){
                                    lat = Double.parseDouble(object.getString("latitude"));
                                    lng = Double.parseDouble(object.getString("longitude"));
                                }
                                store.setLatLng(new LatLng(lat, lng));

                                Commons.product1 = product;
                                Commons.store1 = store;

                                Fragment fragment = new PDetailFragment();
                                FragmentManager manager = getSupportFragmentManager();
                                FragmentTransaction transaction = manager.beginTransaction();
                                transaction.replace(R.id.activity, fragment);
                                transaction.addToBackStack(null).commit();

                            }else {
                                showToast("Server issue.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void contactVendor(View view){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Commons.orderItem.getContact()));
        startActivity(intent);
    }

    public void toDriver(View view){
        getDriver(String.valueOf(Commons.orderItem.getId()));
    }

    public void toPayment(View view){
        showAlertDialogForPayment();
    }

    public void showAlertDialogForPayment(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderItemDetailActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_alert_pay1, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        TextView titleBox = (TextView)view.findViewById(R.id.title);
        titleBox.setTypeface(bold);
        RadioButton radioButton1 = (RadioButton)view.findViewById(R.id.option1);
        radioButton1.setTypeface(bold);
        RadioButton radioButton2 = (RadioButton)view.findViewById(R.id.option2);
        radioButton2.setTypeface(bold);
        TextView okButton = (TextView)view.findViewById(R.id.btn_ok);
        okButton.setTypeface(bold);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioButton1.isChecked()){
                    ArrayList<OrderItem> items = new ArrayList<>();
                    items.add(Commons.orderItem);
                    Payment payment = new Payment();
                    payment.setTotalPrice(totalPrice);
                    payment.setSubTotalPrice(subTotalPrice);
                    payment.setBonus(Commons.order.getDiscount());
                    payment.setDeliveryFee(deliveryPrice);
                    payment.setItems(items);
                    Commons.payment = payment;
                    Intent intent = new Intent(getApplicationContext(), StripePaymentActivity.class);
                    startActivity(intent);
                }else if(radioButton2.isChecked()){
                    Intent intent = new Intent(getApplicationContext(), OrderedStoreItemsToPayActivity.class);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });

        ImageView cancelButton = (ImageView)view.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        // Set alert dialog width equal to screen width 80%
        int dialogWindowWidth = (int) (displayWidth * 0.9f);
        // Set alert dialog height equal to screen height 80%
        //    int dialogWindowHeight = (int) (displayHeight * 0.8f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        //      layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        dialog.getWindow().setAttributes(layoutParams);
        dialog.setCancelable(false);

    }


    public void getDriver(String itemId) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getDriver")
                .addBodyParameter("item_id", itemId)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        progressBar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                JSONObject object = response.getJSONObject("data");
                                User user = new User();
                                user.set_idx(object.getInt("id"));
                                user.set_name(object.getString("name"));
                                user.set_email(object.getString("email"));
                                user.set_password(object.getString("password"));
                                user.set_photoUrl(object.getString("picture_url"));
                                user.set_registered_time(object.getString("registered_time"));
                                user.setRole(object.getString("role"));
                                user.set_phone_number(object.getString("phone_number"));
                                user.set_address(object.getString("address"));
                                user.set_country(object.getString("country"));
                                user.set_area(object.getString("area"));
                                user.set_street(object.getString("street"));
                                user.set_house(object.getString("house"));
                                double lat = 0.0d, lng = 0.0d;
                                if(object.getString("latitude").length() > 0){
                                    lat = Double.parseDouble(object.getString("latitude"));
                                    lng = Double.parseDouble(object.getString("longitude"));
                                }
                                user.setLatLng(new LatLng(lat, lng));
                                user.set_stores(object.getInt("stores"));
                                user.set_status(object.getString("status"));

                                Destination destination = new Destination();
                                destination.setUserId(user.get_idx());
                                destination.setTitle(user.get_name());
                                destination.setPicture_url(user.get_photoUrl());
                                destination.setAddress(user.get_address());
                                destination.setLatLng(user.getLatLng());
                                Commons.destination = destination;
                                Intent intent = new Intent(getApplicationContext(), LocationTrackingActivity.class);
                                startActivity(intent);

                            }else {
                                showToast("No driver...");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        progressBar.setVisibility(View.GONE);
                        showToast(error.getErrorDetail());
                    }
                });

    }

}














































