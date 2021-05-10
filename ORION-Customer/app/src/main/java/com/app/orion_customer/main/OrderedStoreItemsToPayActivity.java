package com.app.orion_customer.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.app.orion_customer.models.OrderItem;
import com.app.orion_customer.models.Payment;
import com.app.orion_customer.models.Product;
import com.app.orion_customer.models.Store;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderedStoreItemsToPayActivity extends BaseActivity {

    TextView orderIDBox, orderDateBox, subTotalBox, shippingBox, totalBox, bonusBox, storeNameBox;
    public static DecimalFormat df = new DecimalFormat("0.00");
    LinearLayout bonusFrame, layout;
    FrameLayout progressBar;
    double totalPrice = 0;
    double subTotalPrice = 0;
    double deliveryPrice = 0;

    public ArrayList<OrderItem> orderItems = new ArrayList<>();
    public ArrayList<OrderItem> orderItems1 = new ArrayList<>();
    ArrayList<OrderItem> orderItemsToPay = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered_store_items);

        progressBar = (FrameLayout)findViewById(R.id.loading_bar);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption1)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption2)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption3)).setTypeface(normal);
        ((TextView)findViewById(R.id.caption4)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption5)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption6)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption7)).setTypeface(normal);

        orderIDBox = (TextView)findViewById(R.id.order_number);
        orderDateBox = (TextView)findViewById(R.id.order_date);
        subTotalBox = (TextView)findViewById(R.id.subtotal_price);
        shippingBox = (TextView)findViewById(R.id.shipping_price);
        totalBox = (TextView)findViewById(R.id.total_price);
        bonusFrame = (LinearLayout)findViewById(R.id.bonusFrame);
        bonusBox = (TextView)findViewById(R.id.bonus);
        storeNameBox = (TextView)findViewById(R.id.storeNameBox);

        storeNameBox.setText(Commons.orderItem.getStore_name());
        storeNameBox.setTypeface(bold);

        orderIDBox.setText(Commons.orderItem.getOrderID());
        orderDateBox.setTypeface(normal);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        String date = dateFormat.format(new Date(Long.parseLong(Commons.orderItem.getDate_time())));
        orderDateBox.setText(date);
        orderDateBox.setTypeface(normal);

        if(Commons.order.getDiscount() > 0)bonusFrame.setVisibility(View.VISIBLE);

        deliveryPrice = Commons.orderItem.getDelivery_price();

        layout = (LinearLayout)findViewById(R.id.layout);

    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStoreOrderItemsToPay();
    }

    public void getStoreOrderItemsToPay() {

        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "storeOrderItemsToPay")
                .addBodyParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
                .addBodyParameter("order_id", String.valueOf(Commons.orderItem.getOrder_id()))
                .addBodyParameter("store_id", String.valueOf(Commons.orderItem.getStore_id()))
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
                                layout.removeAllViews();
                                orderItems.clear();
                                orderItems1.clear();
                                totalPrice = 0;
                                subTotalPrice = 0;
                                JSONArray dataArr = response.getJSONArray("data");
                                for(int j=0; j<dataArr.length(); j++) {
                                    JSONObject obj = (JSONObject) dataArr.get(j);
                                    OrderItem item = new OrderItem();
                                    item.setId(obj.getInt("id"));
                                    item.setOrder_id(obj.getInt("order_id"));
                                    item.setUser_id(obj.getInt("member_id"));
                                    item.setVendor_id(obj.getInt("vendor_id"));
                                    item.setStore_id(obj.getInt("store_id"));
                                    item.setStore_name(obj.getString("store_name"));
                                    item.setProduct_id(obj.getInt("product_id"));
                                    item.setProduct_name(obj.getString("product_name"));
                                    item.setCategory(obj.getString("category"));
                                    item.setSubcategory(obj.getString("subcategory"));
                                    item.setGender(obj.getString("gender"));
                                    item.setGender_key(obj.getString("gender_key"));
                                    item.setDelivery_days(obj.getInt("delivery_days"));
                                    item.setDelivery_price(Double.parseDouble(obj.getString("delivery_price")));
                                    item.setPrice(Double.parseDouble(obj.getString("price")));
                                    item.setUnit(obj.getString("unit"));
                                    item.setQuantity(obj.getInt("quantity"));
                                    item.setDate_time(obj.getString("date_time"));
                                    item.setPicture_url(obj.getString("picture_url"));
                                    item.setStatus(obj.getString("status"));
                                    item.setOrderID(obj.getString("orderID"));
                                    item.setContact(obj.getString("contact"));
                                    item.setDiscount(obj.getInt("discount"));
                                    item.setPaid_amount(Double.parseDouble(obj.getString("paid_amount")));
                                    item.setPaid_time(obj.getString("paid_time"));
                                    item.setPayment_status(obj.getString("payment_status"));
                                    item.setPaid_id(obj.getInt("paid_id"));
                                    item.setAddress(obj.getString("address"));
                                    item.setAddress_line(obj.getString("address_line"));
                                    double lat = 0.0d, lng = 0.0d;
                                    if(obj.getString("latitude").length() > 0){
                                        lat = Double.parseDouble(obj.getString("latitude"));
                                        lng = Double.parseDouble(obj.getString("longitude"));
                                    }
                                    item.setLatLng(new LatLng(lat, lng));

                                    Log.d("STATUS!!!", item.getStatus());

                                    orderItems.add(item);

                                    LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View view = layoutInflater.inflate(R.layout.item_order_storeitems, null);
                                    ImageView picture = (ImageView) view.findViewById(R.id.pictureBox);
                                    TextView productBox = (TextView)view.findViewById(R.id.productNameBox);
                                    TextView categoryBox = (TextView)view.findViewById(R.id.categoryBox);
                                    TextView statusBox = (TextView)view.findViewById(R.id.statusBox);
                                    TextView priceBox = (TextView)view.findViewById(R.id.priceBox);
                                    TextView quantityBox = (TextView)view.findViewById(R.id.quantityBox);
                                    CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkbox);

                                    productBox.setTypeface(bold);
                                    categoryBox.setTypeface(normal);
                                    statusBox.setTypeface(normal);
                                    priceBox.setTypeface(bold);
                                    quantityBox.setTypeface(bold);

                                    productBox.setText(item.getProduct_name());
                                    categoryBox.setText(item.getCategory() + "|" + item.getSubcategory() + "|" + item.getGender());
                                    statusBox.setText(item.getStatus());
                                    quantityBox.setText("X " + String.valueOf(item.getQuantity()));
                                    Glide.with(getApplicationContext()).load(item.getPicture_url()).into(picture);

                                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if(b){
                                                orderItems1.add(item);
                                            }else {
                                                if(orderItems1.contains(item))orderItems1.remove(item);
                                            }
                                            if(!orderItems1.isEmpty())refresh(orderItems1);
                                            else refresh(orderItems);
                                        }
                                    });

                                    view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            productInfo(String.valueOf(item.getProduct_id()));
                                        }
                                    });

                                    subTotalPrice = subTotalPrice + (item.getPrice()*(1 - item.getDiscount()/100)*item.getQuantity());

                                    layout.addView(view);
                                }

                                totalPrice = subTotalPrice + deliveryPrice;

                                subTotalBox.setText(df.format(subTotalPrice) + " " + Constants.currency);
                                bonusBox.setText("-" + df.format(Commons.order.getDiscount()) + "%");
                                shippingBox.setText(df.format(deliveryPrice) + " " + Constants.currency);
                                totalBox.setText(df.format(totalPrice) + " " + Constants.currency);

                                orderItemsToPay.addAll(orderItems);

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

    private void refresh(ArrayList<OrderItem> items){
        orderItemsToPay.clear();
        orderItemsToPay.addAll(items);
        subTotalPrice = 0;
        for(OrderItem item: items){
            subTotalPrice = subTotalPrice + (item.getPrice()*(1 - item.getDiscount()/100)*item.getQuantity());
        }

        totalPrice = subTotalPrice + deliveryPrice;

        subTotalBox.setText(df.format(subTotalPrice) + " " + Constants.currency);
        bonusBox.setText("-" + df.format(Commons.order.getDiscount()) + "%");
        shippingBox.setText(df.format(deliveryPrice) + " " + Constants.currency);
        totalBox.setText(df.format(totalPrice) + " " + Constants.currency);
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

    public void toPayment(View view){
        Payment payment = new Payment();
        payment.setTotalPrice(totalPrice);
        payment.setSubTotalPrice(subTotalPrice);
        payment.setBonus(Commons.order.getDiscount());
        payment.setDeliveryFee(deliveryPrice);
        payment.setItems(orderItemsToPay);
        Commons.payment = payment;
        Intent intent = new Intent(getApplicationContext(), StripePaymentActivity.class);
        startActivity(intent);
    }

}




















































