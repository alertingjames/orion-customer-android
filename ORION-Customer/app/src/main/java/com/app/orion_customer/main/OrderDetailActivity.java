package com.app.orion_customer.main;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.orion_customer.R;
import com.app.orion_customer.base.BaseActivity;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.commons.Constants;
import com.app.orion_customer.models.CartItem;
import com.app.orion_customer.models.OrderItem;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderDetailActivity extends BaseActivity {

    TextView orderIDBox, orderDateBox, phoneBox, addressBox, addressLineBox, subTotalBox, shippingBox, totalBox, bonusBox, deliveryBox;
    LinearLayout itemsBox, bonusFrame;
    public static DecimalFormat df = new DecimalFormat("0.00");
    double deliveryPrice = 0.0d;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

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

        orderIDBox = (TextView)findViewById(R.id.order_number);
        orderDateBox = (TextView)findViewById(R.id.order_date);
        phoneBox = (TextView)findViewById(R.id.phone);
        addressBox = (TextView)findViewById(R.id.address);
        addressLineBox = (TextView)findViewById(R.id.address2);
        subTotalBox = (TextView)findViewById(R.id.subtotal_price);
        shippingBox = (TextView)findViewById(R.id.shipping_price);
        totalBox = (TextView)findViewById(R.id.total_price);
        itemsBox = (LinearLayout)findViewById(R.id.items);
        bonusFrame = (LinearLayout)findViewById(R.id.bonusFrame);
        bonusBox = (TextView)findViewById(R.id.bonus);
        deliveryBox = (TextView)findViewById(R.id.deliveryBox);

        if(Commons.order.getDiscount() > 0)bonusFrame.setVisibility(View.VISIBLE);

        orderIDBox.setText(Commons.order.getOrderID());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        String date = dateFormat.format(new Date(Long.parseLong(Commons.order.getDate())));
        orderDateBox.setText(date);
        phoneBox.setText(Commons.order.getPhone_number());
        addressBox.setText(Commons.order.getAddress());
        addressLineBox.setText(Commons.order.getAddress_line());

        itemsBox.removeAllViews();

        ArrayList<Integer> storeIds = new ArrayList<>();
        Commons.cartItems.clear();

        for(OrderItem orderItem:Commons.order.getItems()){
            LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_order_picture, null);
            RoundedImageView picture = (RoundedImageView) view.findViewById(R.id.item_picture);
            TextView mask = (TextView)view.findViewById(R.id.txt_mask);
            mask.setText("X " + String.valueOf(orderItem.getQuantity()));
            Picasso.with(getApplicationContext()).load(orderItem.getPicture_url()).into(picture);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), CheckoutItemsActivity.class);
                    startActivity(intent);
                }
            });
            itemsBox.addView(view);

            CartItem cartItem = new CartItem();
            cartItem.setStore_name(orderItem.getStore_name());
            cartItem.setProduct_name(orderItem.getProduct_name());
            cartItem.setGender(orderItem.getGender());
            cartItem.setGender_key(orderItem.getGender_key());
            cartItem.setDelivery_price(orderItem.getDelivery_price());
            cartItem.setDelivery_days(orderItem.getDelivery_days());
            cartItem.setPrice(orderItem.getPrice());
            cartItem.setQuantity(orderItem.getQuantity());
            cartItem.setPicture_url(orderItem.getPicture_url());
            cartItem.setStatus(orderItem.getStatus());

            Commons.cartItems.add(cartItem);

        }

        deliveryPrice = Commons.order.getShipping();

//        double bonus = ((Commons.order.getPrice() - deliveryPrice)*100/(100 - Commons.order.getDiscount())) * Commons.order.getDiscount()/100;
        subTotalBox.setText(df.format(Commons.order.getPrice() - deliveryPrice) + " " + Constants.currency);
        bonusBox.setText("-" + df.format(Commons.order.getDiscount()) + "%");
        shippingBox.setText(String.valueOf(deliveryPrice) + " " + Constants.currency);
        totalBox.setText(df.format(Commons.order.getPrice()) + " " + Constants.currency);


        phoneBox.setTypeface(bold);
        addressBox.setTypeface(bold);
        addressLineBox.setTypeface(normal);
        ((TextView)findViewById(R.id.method)).setTypeface(bold);
        ((TextView)findViewById(R.id.subtotal_price)).setTypeface(bold);
        ((TextView)findViewById(R.id.shipping_price)).setTypeface(bold);
        ((TextView)findViewById(R.id.total_price)).setTypeface(bold);
        deliveryBox.setTypeface(bold);

    }

    public void toDeliveries(View view){
        Intent intent = new Intent(getApplicationContext(), CheckoutItemsActivity.class);
        startActivity(intent);
    }

    public void back(View view){
        onBackPressed();
    }
}









