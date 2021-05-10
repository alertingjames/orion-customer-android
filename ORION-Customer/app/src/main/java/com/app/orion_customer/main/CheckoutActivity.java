package com.app.orion_customer.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_customer.R;
import com.app.orion_customer.adapters.CouponListAdapter;
import com.app.orion_customer.adapters.StoreProductListAdapter;
import com.app.orion_customer.base.BaseActivity;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.commons.Constants;
import com.app.orion_customer.commons.ReqConst;
import com.app.orion_customer.fragments.PDetailFragment;
import com.app.orion_customer.models.Address;
import com.app.orion_customer.models.CartItem;
import com.app.orion_customer.models.Coupon;
import com.app.orion_customer.models.Phone;
import com.app.orion_customer.models.Product;
import com.app.orion_customer.models.Store;
import com.google.android.gms.maps.model.LatLng;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends BaseActivity {

    TextView phone, address, addressLine, coupon, method, subTotal, shipping, total, deliveryBox;
    TextView confirmButton;
    LinearLayout shippingFrame1, shippingFrame2;
    LinearLayout itemsBox;
    public static DecimalFormat df = new DecimalFormat("0.00");
    FrameLayout progressBar;
    ArrayList<Coupon> coupons = new ArrayList<>();
    CouponListAdapter couponListAdapter = new CouponListAdapter(this);
    int pDiscount = 0;
    public int couponId = 0;
    double subTotalPrice = 0.0d;
    String orderItemStr = "";
    double deliveryPrice = 0.0d;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Commons.phoneId = 0;
        Commons.addrId = 0;

        Commons.checkoutActivity = this;

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.lb1)).setTypeface(bold);
        ((TextView)findViewById(R.id.lb2)).setTypeface(normal);
        ((TextView)findViewById(R.id.lb3)).setTypeface(normal);
        ((TextView)findViewById(R.id.lb4)).setTypeface(normal);
        ((TextView)findViewById(R.id.lb5)).setTypeface(normal);

        ((TextView)findViewById(R.id.lb6)).setTypeface(normal);
        ((TextView)findViewById(R.id.lb7)).setTypeface(normal);
        ((TextView)findViewById(R.id.lb8)).setTypeface(normal);
        ((TextView)findViewById(R.id.lb9)).setTypeface(normal);
        ((TextView)findViewById(R.id.lb10)).setTypeface(normal);
        ((TextView)findViewById(R.id.lb11)).setTypeface(bold);
        ((TextView)findViewById(R.id.lb12)).setTypeface(bold);
        ((TextView)findViewById(R.id.lb13)).setTypeface(normal);

        progressBar = (FrameLayout)findViewById(R.id.loading_bar);

        phone = (TextView)findViewById(R.id.phone);
        address = (TextView)findViewById(R.id.address);
        addressLine = (TextView)findViewById(R.id.address2);
        coupon = (TextView)findViewById(R.id.coupon);
        method = (TextView)findViewById(R.id.method);
        deliveryBox = (TextView)findViewById(R.id.deliveryBox);
        subTotal = (TextView)findViewById(R.id.subtotal_price);
        shipping = (TextView)findViewById(R.id.shipping_price);
        total = (TextView)findViewById(R.id.total_price);
        total.setPaintFlags(total.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        phone.setTypeface(bold);
        address.setTypeface(bold);
        addressLine.setTypeface(normal);
        coupon.setTypeface(bold);
        method.setTypeface(bold);
        subTotal.setTypeface(bold);
        shipping.setTypeface(bold);
        total.setTypeface(bold);
        deliveryBox.setTypeface(bold);

        itemsBox = (LinearLayout)findViewById(R.id.items);

        confirmButton = (TextView)findViewById(R.id.btn_confirm);
        confirmButton.setTypeface(bold);

        shippingFrame1 = (LinearLayout)findViewById(R.id.shippingFrame1);
        shippingFrame2 = (LinearLayout)findViewById(R.id.shippingFrame2);

        itemsBox.removeAllViews();

        ArrayList<Integer> storeIds = new ArrayList<>();

        for(CartItem cartItem:Commons.cartItems){
            subTotalPrice += (cartItem.getNew_price() > 0? cartItem.getNew_price():cartItem.getPrice()) * cartItem.getQuantity();
            if(!storeIds.contains(cartItem.getStore_id())){
                storeIds.add(cartItem.getStore_id());
                deliveryBox.setText(deliveryBox.getText().toString().length() > 0? deliveryBox.getText().toString() + "|": "" + String.valueOf(cartItem.getDelivery_days()) + " Days, " + String.valueOf(cartItem.getDelivery_price()) + " SGD");
                deliveryPrice = deliveryPrice + cartItem.getDelivery_price();
            }
            LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_order_picture, null);
            RoundedImageView picture = (RoundedImageView) view.findViewById(R.id.item_picture);
            TextView mask = (TextView)view.findViewById(R.id.txt_mask);
            mask.setText("X " + String.valueOf(cartItem.getQuantity()));
            Picasso.with(getApplicationContext()).load(cartItem.getPicture_url()).into(picture);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), CheckoutItemsActivity.class);
                    startActivity(intent);
                }
            });
            itemsBox.addView(view);
        }

        subTotal.setText(df.format(subTotalPrice) + " " + Constants.currency);
        shipping.setText(df.format(deliveryPrice) + " " + Constants.currency);
        total.setText(df.format(subTotalPrice + deliveryPrice) + " " + Constants.currency);

        Commons.totalPrice = subTotalPrice + deliveryPrice;

        setupUI(findViewById(R.id.activity), this);

    }

    public void toDeliveries(View view){
        Intent intent = new Intent(getApplicationContext(), CheckoutItemsActivity.class);
        startActivity(intent);
    }

    public void toCoupons(View view){
        if(Commons.thisUser != null){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_in);
            ((LinearLayout)findViewById(R.id.bonusFrame)).setAnimation(animation);
            ((LinearLayout)findViewById(R.id.bonusFrame)).setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((LinearLayout)findViewById(R.id.bg_dark)).setVisibility(View.VISIBLE);
                }
            }, 200);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void dontUseCoupon(View view){
        couponId = 0;
        applyCoupon(pDiscount = 0);
    }

    public void applyCoupon(int discount){
        pDiscount = discount;
        dismissFrame();
        double subtp = 0.0d;
        subtp = subTotalPrice - (double) subTotalPrice * discount/100;
        subTotal.setText(df.format(subtp) + " " + Constants.currency);
        shipping.setText(df.format(deliveryPrice) + " " + Constants.currency);
        total.setText(df.format(subtp + deliveryPrice) + " " + Constants.currency);

        if(discount > 0) coupon.setText("Discount: -" + String.valueOf(discount) + "%");
        else coupon.setText("Use coupon");

        Commons.totalPrice = subtp + deliveryPrice;
    }

    public void dismissFrame(View view){
        dismissFrame();
    }

    public void dismissFrame(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_out);
        ((LinearLayout)findViewById(R.id.bonusFrame)).setAnimation(animation);
        ((LinearLayout)findViewById(R.id.bonusFrame)).setVisibility(View.GONE);
        ((LinearLayout)findViewById(R.id.bg_dark)).setVisibility(View.GONE);
    }

    public void toPhone(View view){
        if(Commons.thisUser == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), AddressPhoneListActivity.class);
            intent.putExtra("option", "phone");
            startActivity(intent);
        }
    }

    public void toAddress(View view){
        if(Commons.thisUser == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), AddressPhoneListActivity.class);
            startActivity(intent);
        }
    }

    public void back(View view){
        onBackPressed();
    }

    private void getPhones() {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getPhones")
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
                                Commons.phones.clear();
                                JSONArray dataArr = response.getJSONArray("data");
                                for(int i=0; i<dataArr.length(); i++) {
                                    JSONObject object = (JSONObject) dataArr.get(i);
                                    Phone phone = new Phone();
                                    phone.setId(object.getInt("id"));
                                    phone.setUserId(object.getInt("member_id"));
                                    phone.setPhone_number(object.getString("phone_number"));
                                    phone.setStatus(object.getString("status"));

                                    Commons.phones.add(phone);
                                }
                                if(Commons.phones.size() > 0)phone.setText(Commons.phones.get(Commons.phoneId).getPhone_number());
                                else {
                                    if(Commons.thisUser != null)phone.setText(Commons.thisUser.get_phone_number());
                                }

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
                        showToast(error.getErrorDetail());
                    }
                });
    }

    private void getAddresses() {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getAddresses")
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
                                Commons.addresses.clear();
                                JSONArray dataArr = response.getJSONArray("data");
                                for(int i=0; i<dataArr.length(); i++) {
                                    JSONObject object = (JSONObject) dataArr.get(i);
                                    Address address = new Address();
                                    address.setId(object.getInt("id"));
                                    address.setUserId(object.getInt("member_id"));
                                    address.setAddress(object.getString("address"));
                                    address.setArea(object.getString("area"));
                                    address.setStreet(object.getString("street"));
                                    address.setHouse(object.getString("house"));
                                    address.setStatus(object.getString("status"));
                                    double lat = 0.0d, lng = 0.0d;
                                    if(object.getString("latitude").length() > 0){
                                        lat = Double.parseDouble(object.getString("latitude"));
                                        lng = Double.parseDouble(object.getString("longitude"));
                                    }
                                    address.setLatLng(new LatLng(lat, lng));

                                    Commons.addresses.add(address);
                                }

                                if(Commons.thisUser == null){
                                    if(Commons.addresses.size() > 0){
                                        shippingFrame1.setVisibility(View.GONE);
                                        shippingFrame2.setVisibility(View.VISIBLE);
                                        address.setText(Commons.addresses.get(Commons.addrId).getAddress());
                                        addressLine.setText(Commons.addresses.get(Commons.addrId).getArea() + ", " + Commons.addresses.get(Commons.addrId).getStreet() + ", "
                                                + Commons.addresses.get(Commons.addrId).getHouse());
                                    }else {
                                        shippingFrame1.setVisibility(View.VISIBLE);
                                        shippingFrame2.setVisibility(View.GONE);
                                    }
                                }else if(Commons.thisUser != null && Commons.thisUser.get_address().length() == 0){
                                    if(Commons.addresses.size() > 0){
                                        shippingFrame1.setVisibility(View.GONE);
                                        shippingFrame2.setVisibility(View.VISIBLE);
                                        address.setText(Commons.addresses.get(Commons.addrId).getAddress());
                                        addressLine.setText(Commons.addresses.get(Commons.addrId).getArea() + ", " + Commons.addresses.get(Commons.addrId).getStreet() + ", "
                                                + Commons.addresses.get(Commons.addrId).getHouse());
                                    }else {
                                        shippingFrame1.setVisibility(View.VISIBLE);
                                        shippingFrame2.setVisibility(View.GONE);
                                    }
                                }
                                else {
                                    shippingFrame1.setVisibility(View.GONE);
                                    shippingFrame2.setVisibility(View.VISIBLE);

                                    if(Commons.addresses.size() > 0){
                                        shippingFrame1.setVisibility(View.GONE);
                                        shippingFrame2.setVisibility(View.VISIBLE);
                                        address.setText(Commons.addresses.get(Commons.addrId).getAddress());
                                        addressLine.setText(Commons.addresses.get(Commons.addrId).getArea() + ", " + Commons.addresses.get(Commons.addrId).getStreet()
                                                + ", " + Commons.addresses.get(Commons.addrId).getHouse());
                                    }else {
                                        address.setText(Commons.thisUser.get_address());
                                        addressLine.setText(Commons.thisUser.get_area() + ", " + Commons.thisUser.get_street() + ", " + Commons.thisUser.get_house());
                                    }
                                }

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
                        showToast(error.getErrorDetail());
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        if(Commons.thisUser != null){
            getCoupons();
            getPhones();
            getAddresses();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Commons.checkoutActivity = null;
    }

    public void confirmOrder(View view){

        if(phone.getText().length() == 0){
            showToast("Enter your phone number.");
            return;
        }

        if(shippingFrame1.getVisibility() == View.VISIBLE){
            showToast("Enter your address.");
            return;
        }

        if(Commons.thisUser == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            return;
        }

        try {
            uploadOrder(createOrderItemJsonString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String createOrderItemJsonString()throws JSONException{

        orderItemStr = "";
        JSONObject jsonObj = null;
        JSONArray jsonArr = new JSONArray();
        if (Commons.cartItems.size()>0){
            for(int i=0; i<Commons.cartItems.size(); i++){

                String userId = String.valueOf(Commons.thisUser.get_idx());
                String vendorId = String.valueOf(Commons.cartItems.get(i).getUser_id());
                String storeId = String.valueOf(Commons.cartItems.get(i).getStore_id());
                String storeName = Commons.cartItems.get(i).getStore_name();
                String productId = String.valueOf(Commons.cartItems.get(i).getProduct_id());
                String productName = Commons.cartItems.get(i).getProduct_name();
                String category = Commons.cartItems.get(i).getCategory();
                String subcategory = Commons.cartItems.get(i).getSubcategory();
                String gender = Commons.cartItems.get(i).getGender();
                String genderKey = Commons.cartItems.get(i).getGender_key();
                String price = df.format(Commons.cartItems.get(i).getNew_price() > 0?Commons.cartItems.get(i).getNew_price():Commons.cartItems.get(i).getPrice());
                String priceUnit = Commons.cartItems.get(i).getUnit();
                String quantity = String.valueOf(Commons.cartItems.get(i).getQuantity());
                String pictureUrl = Commons.cartItems.get(i).getPicture_url();
                String deliveryDays = String.valueOf(Commons.cartItems.get(i).getDelivery_days());
                String deliveryPrice = df.format(Commons.cartItems.get(i).getDelivery_price());

                jsonObj=new JSONObject();

                try {
                    jsonObj.put("member_id",userId);
                    jsonObj.put("vendor_id",vendorId);
                    jsonObj.put("store_id",storeId);
                    jsonObj.put("store_name",storeName);
                    jsonObj.put("product_id",productId);
                    jsonObj.put("product_name",productName);
                    jsonObj.put("category",category);
                    jsonObj.put("subcategory",subcategory);
                    jsonObj.put("gender",gender);
                    jsonObj.put("gender_key",genderKey);
                    jsonObj.put("price",price);
                    jsonObj.put("unit",priceUnit);
                    jsonObj.put("quantity",quantity);
                    jsonObj.put("picture_url",pictureUrl);
                    jsonObj.put("delivery_days",deliveryDays);
                    jsonObj.put("delivery_price",deliveryPrice);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArr.put(jsonObj);
            }
            JSONObject scheduleObj = new JSONObject();
            scheduleObj.put("orderItems", jsonArr);
            orderItemStr = scheduleObj.toString();
            Log.d("ITEMSTR!!!", orderItemStr);
            return orderItemStr;
        }

        return orderItemStr;
    }

    public void uploadOrder(final String orderItemStr) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "uploadOrder")
                .addBodyParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
                .addBodyParameter("orderID", RandomStringUtils.randomAlphanumeric(12).toUpperCase())
                .addBodyParameter("price", df.format(Commons.totalPrice))
                .addBodyParameter("unit", Constants.currency)
                .addBodyParameter("shipping", df.format(deliveryPrice))
                .addBodyParameter("email", Commons.thisUser.get_email())
                .addBodyParameter("address", address.getText().toString().trim())
                .addBodyParameter("address_line", addressLine.getText().toString().trim())
                .addBodyParameter("latitude", String.valueOf(Commons.addresses.get(Commons.addrId).getLatLng().latitude))
                .addBodyParameter("longitude", String.valueOf(Commons.addresses.get(Commons.addrId).getLatLng().longitude))
                .addBodyParameter("phone_number", phone.getText().toString())
                .addBodyParameter("coupon_id", String.valueOf(couponId))
                .addBodyParameter("discount", String.valueOf(pDiscount))
                .addBodyParameter("orderItems", orderItemStr)
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
                                String orderID = response.getString("orderID");
                                long orderTimestamp = Long.parseLong(response.getString("date_time"));

                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                                String date = dateFormat.format(new Date(orderTimestamp));

                                Intent intent = new Intent(getApplicationContext(), OrderPlacedActivity.class);
                                intent.putExtra("orderID", orderID);
                                intent.putExtra("order_date", date);
                                startActivity(intent);
                                finish();

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
                        showToast(error.getErrorDetail());
                    }
                });

    }

    private void getCoupons() {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getCoupons")
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
                                coupons.clear();
                                JSONArray dataArr = response.getJSONArray("availables");
                                for(int i=0; i<dataArr.length(); i++) {
                                    JSONObject object = (JSONObject) dataArr.get(i);
                                    Coupon coupon = new Coupon();
                                    coupon.setId(object.getInt("id"));
                                    coupon.setDiscount(object.getInt("discount"));
                                    coupon.setExpireTime(object.getLong("expire_time"));
                                    coupons.add(coupon);
                                }

                                if(coupons.isEmpty()){
                                    ((TextView)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                                }else ((TextView)findViewById(R.id.no_result)).setVisibility(View.GONE);

                                couponListAdapter.setDatas(coupons);
                                ((ListView)findViewById(R.id.list)).setAdapter(couponListAdapter);

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
                        showToast(error.getErrorDetail());
                    }
                });
    }

}




















































