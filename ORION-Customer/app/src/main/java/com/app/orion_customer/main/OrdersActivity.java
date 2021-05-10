package com.app.orion_customer.main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_customer.R;
import com.app.orion_customer.adapters.OrderItemListAdapter;
import com.app.orion_customer.adapters.OrderListAdapter;
import com.app.orion_customer.base.BaseActivity;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.commons.ReqConst;
import com.app.orion_customer.models.CartItem;
import com.app.orion_customer.models.Destination;
import com.app.orion_customer.models.Order;
import com.app.orion_customer.models.OrderItem;
import com.app.orion_customer.models.Payment;
import com.app.orion_customer.models.User;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Callable;

public class OrdersActivity extends BaseActivity {

    ImageView searchButton, cancelButton;
    LinearLayout searchBar;
    FrameLayout trackFrame;
    EditText ui_edtsearch;
    TextView title;
    ListView list;
    FrameLayout progressBar;
    ImageView[] nodes = {};
    View[] lines = {};

    public ArrayList<Order> orders = new ArrayList<>();
    OrderListAdapter orderListAdapter = new OrderListAdapter(this);

    public ArrayList<OrderItem> orderItems = new ArrayList<>();
    OrderItemListAdapter orderItemListAdapter = new OrderItemListAdapter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        Commons.ordersActivity = this;

        progressBar = (FrameLayout) findViewById(R.id.loading_bar);
        title = (TextView)findViewById(R.id.title);

        trackFrame = (FrameLayout)findViewById(R.id.trackFrame);

        searchBar = (LinearLayout)findViewById(R.id.search_bar);
        searchButton = (ImageView)findViewById(R.id.searchButton);
        cancelButton = (ImageView)findViewById(R.id.cancelButton);

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
        ui_edtsearch.setFocusable(true);
        ui_edtsearch.requestFocus();

        title.setTypeface(bold);
        ui_edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ui_edtsearch.getText().toString().trim().toLowerCase(Locale.getDefault());
                if(((View)findViewById(R.id.indicator_all)).getVisibility() == View.VISIBLE) orderListAdapter.filter(text);
                else orderItemListAdapter.filter(text);
            }
        });

        list = (ListView) findViewById(R.id.list);
        ((TextView)findViewById(R.id.caption1)).setTypeface(bold);
        ((TextView)findViewById(R.id.caption2)).setTypeface(bold);

        setupUI(findViewById(R.id.activity), this);

        initTrackFrame();
        all();
    }

    public void search(View view){
        cancelButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.GONE);
        searchBar.setVisibility(View.VISIBLE);
        title.setVisibility(View.GONE);
    }

    public void cancelSearch(View view){
        cancelButton.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        searchBar.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void clearIndicators(){
        if(progressBar.getVisibility() == View.VISIBLE)return;
        ((View)findViewById(R.id.indicator_all)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_placed)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_confirmed)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_prepared)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_ready)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_delivered)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_all)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_placed)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_confirmed)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_prepared)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_ready)).setTypeface(normal);
        ((TextView)findViewById(R.id.txt_delivered)).setTypeface(normal);

        ((TextView)findViewById(R.id.txt_all)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_placed)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_confirmed)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_prepared)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_ready)).setTextColor(getColor(R.color.lightPrimary));
        ((TextView)findViewById(R.id.txt_delivered)).setTextColor(getColor(R.color.lightPrimary));
    }

    private void initTrackFrame(){
        nodes = new ImageView[]{
                ((ImageView) findViewById(R.id.img_placed)),
                ((ImageView)findViewById(R.id.img_confirmed)),
                ((ImageView)findViewById(R.id.img_prepared)),
                ((ImageView)findViewById(R.id.img_ready)),
                ((ImageView)findViewById(R.id.img_delivered))
        };

        lines = new View[]{
                ((View)findViewById(R.id.view_confirm)),
                ((View)findViewById(R.id.view_prepare)),
                ((View)findViewById(R.id.view_ready)),
                ((View)findViewById(R.id.view_delivery))
        };
    }

    private void appearTrackFrame(int sel){
        for(ImageView node:nodes)node.setImageResource(R.drawable.gray_circle);
        for(View line:lines)line.setBackgroundColor(getColor(R.color.gray));
        for(int i=0;i<nodes.length; i++){
            if(i <= sel){
                nodes[i].setImageResource(R.drawable.marroon_circle);
            }
            if(i == 0)continue;
            else if(i <= sel)lines[i - 1].setBackgroundColor(getColor(R.color.colorPrimary));
        }
    }

    private void tabFocus(LinearLayout tabLayout){
        HorizontalScrollView horizontalScrollView = (HorizontalScrollView)findViewById(R.id.hScrollView);
        int x = tabLayout.getLeft();
        int y = tabLayout.getTop();
        horizontalScrollView.scrollTo(x, y);
    }

    private void tabFocus2(LinearLayout tabLayout){
        HorizontalScrollView horizontalScrollView = (HorizontalScrollView)findViewById(R.id.hScrollView);
        int x = tabLayout.getLeft() - 200;
        int y = tabLayout.getTop();
        horizontalScrollView.scrollTo(x, y);
    }

    public void selAll(View view){
        all();
    }

    private void all(){
        clearIndicators();
        trackFrame.setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_all)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_all)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_all)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus(((LinearLayout)findViewById(R.id.lyt_all)));
        getOrders();
        getOrderItems();
    }

    public void selPlaced(View view){
        placeds();
    }

    private void placeds(){
        clearIndicators();
        trackFrame.setVisibility(View.VISIBLE);
        appearTrackFrame(Commons.orderStatus.statusIndex.get("placed"));
        ((View)findViewById(R.id.indicator_placed)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_placed)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_placed)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus2(((LinearLayout)findViewById(R.id.lyt_placed)));

        getItemsByStatus("placed");
    }

    public void selConfirmed(View view){
        confirmeds();
    }

    private void confirmeds(){
        clearIndicators();
        trackFrame.setVisibility(View.VISIBLE);
        appearTrackFrame(Commons.orderStatus.statusIndex.get("confirmed"));
        ((View)findViewById(R.id.indicator_confirmed)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_confirmed)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_confirmed)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus2(((LinearLayout)findViewById(R.id.lyt_confirmed)));

        getItemsByStatus("confirmed");
    }

    public void selPrepared(View view){
        clearIndicators();
        trackFrame.setVisibility(View.VISIBLE);
        appearTrackFrame(Commons.orderStatus.statusIndex.get("prepared"));
        ((View)findViewById(R.id.indicator_prepared)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_prepared)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_prepared)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus(((LinearLayout)findViewById(R.id.lyt_prepared)));

        getItemsByStatus("prepared");
    }

    public void selReady(View view){
        clearIndicators();
        trackFrame.setVisibility(View.VISIBLE);
        appearTrackFrame(Commons.orderStatus.statusIndex.get("ready"));
        ((View)findViewById(R.id.indicator_ready)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_ready)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_ready)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus(((LinearLayout)findViewById(R.id.lyt_ready)));

        getItemsByStatus("ready");
    }

    public void selDelivered(View view){
        clearIndicators();
        trackFrame.setVisibility(View.VISIBLE);
        appearTrackFrame(Commons.orderStatus.statusIndex.get("delivered"));
        ((View)findViewById(R.id.indicator_delivered)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_delivered)).setTypeface(bold);
        ((TextView)findViewById(R.id.txt_delivered)).setTextColor(getColor(R.color.colorPrimary));
        tabFocus(((LinearLayout)findViewById(R.id.lyt_delivered)));

        getItemsByStatus("delivered");
    }

    private void getItemsByStatus(String status){
        ArrayList<OrderItem> items = new ArrayList<>();
        for(OrderItem item: orderItems){
            if(item.getStatus().equals(status))items.add(item);
        }
        orderItemListAdapter.setDatas(items);
        if(orderItemListAdapter.getCount() == 0){
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
            trackFrame.setVisibility(View.GONE);
        }else {
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
        }
        orderItemListAdapter.notifyDataSetChanged();
        list.setAdapter(orderItemListAdapter);

        if(items.size() > 0){
            if(status.equals("placed")){
                ((FrameLayout)findViewById(R.id.countFrame_placed)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_placed)).setText(String.valueOf(items.size()));
            }else if(status.equals("confirmed")){
                ((FrameLayout)findViewById(R.id.countFrame_confirmed)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_confirmed)).setText(String.valueOf(items.size()));
            }else if(status.equals("prepared")){
                ((FrameLayout)findViewById(R.id.countFrame_prepared)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_prepared)).setText(String.valueOf(items.size()));
            }else if(status.equals("ready")){
                ((FrameLayout)findViewById(R.id.countFrame_ready)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_ready)).setText(String.valueOf(items.size()));
            }else if(status.equals("delivered")){
                ((FrameLayout)findViewById(R.id.countFrame_delivered)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_delivered)).setText(String.valueOf(items.size()));
            }
        }
    }

    public void getOrders() {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getUserOrders")
                .addBodyParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
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
                                orders.clear();
                                JSONArray feedsArr = response.getJSONArray("data");
                                for(int i=0; i<feedsArr.length(); i++) {
                                    JSONObject object = (JSONObject) feedsArr.get(i);
                                    Order order = new Order();
                                    order.setId(object.getInt("id"));
                                    order.setUser_id(object.getInt("member_id"));
                                    order.setOrderID(object.getString("orderID"));
                                    order.setPrice(Double.parseDouble(object.getString("price")));
                                    order.setUnit(object.getString("unit"));
                                    order.setShipping(Double.parseDouble(object.getString("shipping")));
                                    order.setDate(object.getString("date_time"));
                                    order.setEmail(object.getString("email"));
                                    order.setAddress(object.getString("address"));
                                    order.setAddress_line(object.getString("address_line"));
                                    order.setPhone_number(object.getString("phone_number"));
                                    order.setStatus(object.getString("status"));
                                    order.setDiscount(object.getInt("discount"));
                                    double lat = 0.0d, lng = 0.0d;
                                    if(object.getString("latitude").length() > 0){
                                        lat = Double.parseDouble(object.getString("latitude"));
                                        lng = Double.parseDouble(object.getString("longitude"));
                                    }
                                    order.setLatLng(new LatLng(lat, lng));

                                    ArrayList<OrderItem> orderItems = new ArrayList<>();
                                    JSONArray objArr = object.getJSONArray("items");
                                    for(int j=0; j<objArr.length(); j++) {
                                        JSONObject obj = (JSONObject) objArr.get(j);
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
                                        lat = 0.0d; lng = 0.0d;
                                        if(object.getString("latitude").length() > 0){
                                            lat = Double.parseDouble(object.getString("latitude"));
                                            lng = Double.parseDouble(object.getString("longitude"));
                                        }
                                        item.setLatLng(new LatLng(lat, lng));

                                        Log.d("STATUS!!!", item.getStatus());

                                        orderItems.add(item);
                                    }

                                    order.setItems(orderItems);
                                    orders.add(order);
                                }

                                orderListAdapter.setDatas(orders);
                                if(orderListAdapter.getCount() == 0){
                                    ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                                }else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
                                orderListAdapter.notifyDataSetChanged();
                                list.setAdapter(orderListAdapter);

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

    public void deleteOrder(Order order){
        showAlertDialogForQuestion("Warning", "Are you sure you want to delete this order?", this, null, new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                progressBar.setVisibility(View.VISIBLE);
                AndroidNetworking.post(ReqConst.SERVER_URL + "delOrder")
                        .addBodyParameter("order_id", String.valueOf(order.getId()))
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
                                    if (result.equals("0")) {
                                        int index = orders.indexOf(order);
                                        orders.remove(index);

                                        orderListAdapter.setDatas(orders);
                                        if(orderListAdapter.getCount() == 0){
                                            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                                        }else ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
                                        orderListAdapter.notifyDataSetChanged();
                                        list.setAdapter(orderListAdapter);

                                        getOrderItems();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError error) {
                                // handle error
                                Log.d("ERROR!!!", error.getErrorBody());
                                progressBar.setVisibility(View.GONE);
                                showToast(error.getErrorDetail());
                            }
                        });

                return null;
            }
        });
    }

    public void getOrderItems() {

        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "userOrderItems")
                .addBodyParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
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
                                orderItems.clear();
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
                                    double lat = 0.0d; double lng = 0.0d;
                                    if(obj.getString("latitude").length() > 0){
                                        lat = Double.parseDouble(obj.getString("latitude"));
                                        lng = Double.parseDouble(obj.getString("longitude"));
                                    }
                                    item.setLatLng(new LatLng(lat, lng));

                                    Log.d("STATUS!!!", item.getStatus());

                                    orderItems.add(item);
                                }

                                getItemStatus("placed");
                                getItemStatus("confirmed");
                                getItemStatus("prepared");
                                getItemStatus("ready");
                                getItemStatus("delivered");

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

    private void getItemStatus(String status){

        ArrayList<OrderItem> items = new ArrayList<>();
        for(OrderItem item: orderItems){
            if(item.getStatus().equals(status))items.add(item);
        }

        if(items.size() > 0){
            if(status.equals("placed")){
                ((FrameLayout)findViewById(R.id.countFrame_placed)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_placed)).setText(String.valueOf(items.size()));
            }else if(status.equals("confirmed")){
                ((FrameLayout)findViewById(R.id.countFrame_confirmed)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_confirmed)).setText(String.valueOf(items.size()));
            }else if(status.equals("prepared")){
                ((FrameLayout)findViewById(R.id.countFrame_prepared)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_prepared)).setText(String.valueOf(items.size()));
            }else if(status.equals("ready")){
                ((FrameLayout)findViewById(R.id.countFrame_ready)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_ready)).setText(String.valueOf(items.size()));
            }else if(status.equals("delivered")){
                ((FrameLayout)findViewById(R.id.countFrame_delivered)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.count_delivered)).setText(String.valueOf(items.size()));
            }
        }else {
            if(status.equals("placed")){
                ((FrameLayout)findViewById(R.id.countFrame_placed)).setVisibility(View.GONE);
            }else if(status.equals("confirmed")){
                ((FrameLayout)findViewById(R.id.countFrame_confirmed)).setVisibility(View.GONE);
            }else if(status.equals("prepared")){
                ((FrameLayout)findViewById(R.id.countFrame_prepared)).setVisibility(View.GONE);
            }else if(status.equals("ready")){
                ((FrameLayout)findViewById(R.id.countFrame_ready)).setVisibility(View.GONE);
            }else if(status.equals("delivered")){
                ((FrameLayout)findViewById(R.id.countFrame_delivered)).setVisibility(View.GONE);
            }
        }
    }

    public void toCheckOut(Order order){
        Commons.cartItems.clear();
        for(OrderItem item: order.getItems()){
            CartItem cartItem = new CartItem();
            cartItem.setId(item.getId());
            cartItem.setVendor_id(item.getVendor_id());
            cartItem.setStore_id(item.getStore_id());
            cartItem.setStore_name(item.getStore_name());
            cartItem.setPicture_url(item.getPicture_url());
            cartItem.setCategory(item.getCategory());
            cartItem.setSubcategory(item.getSubcategory());
            cartItem.setGender(item.getGender());
            cartItem.setGender_key(item.getGender_key());
            cartItem.setProduct_id(item.getProduct_id());
            cartItem.setProduct_name(item.getProduct_name());
            cartItem.setDelivery_days(item.getDelivery_days());
            cartItem.setDelivery_price(item.getDelivery_price());
            cartItem.setPrice(item.getPrice());
            cartItem.setUnit(item.getUnit());
            cartItem.setQuantity(item.getQuantity());

            Commons.cartItems.add(cartItem);
        }

        Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
        startActivity(intent);
    }

    public void toHome(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Commons.ordersActivity = null;
    }

    public void showAlertDialogForPayment(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OrdersActivity.this);
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
                    double deliveryPrice = Commons.orderItem.getDelivery_price();
                    double subTotalPrice = (Commons.orderItem.getPrice()*(1- Commons.orderItem.getDiscount()/100))*Commons.orderItem.getQuantity();
                    double totalPrice = subTotalPrice + deliveryPrice;
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
















































