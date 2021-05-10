package com.app.orion_customer.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityOptionsCompat;

import com.app.orion_customer.R;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.commons.Constants;
import com.app.orion_customer.main.OrderItemDetailActivity;
import com.app.orion_customer.main.ViewImageActivity;
import com.app.orion_customer.models.Order;
import com.app.orion_customer.models.OrderItem;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderItemListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<OrderItem> _datas = new ArrayList<>();
    private ArrayList<OrderItem> _alldatas = new ArrayList<>();
    public static DecimalFormat df = new DecimalFormat("0.00");

    public OrderItemListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<OrderItem> datas) {

        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

    @Override
    public int getCount(){
        return _datas.size();
    }

    @Override
    public Object getItem(int position){
        return _datas.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.item_orderitem, parent, false);

            holder.orderIDBox = (TextView) convertView.findViewById(R.id.order_number);
            holder.orderDateBox = (TextView) convertView.findViewById(R.id.order_date);
            holder.priceBox = (TextView) convertView.findViewById(R.id.priceBox);
            holder.quantityBox = (TextView) convertView.findViewById(R.id.quantityBox);
            holder.pictureBox = (RoundedImageView) convertView.findViewById(R.id.pictureBox);
            holder.caption = (TextView) convertView.findViewById(R.id.caption);
            holder.storeNameBox = (TextView) convertView.findViewById(R.id.storeNameBox);
            holder.productNameBox = (TextView) convertView.findViewById(R.id.productNameBox);
            holder.statusBox = (TextView) convertView.findViewById(R.id.statusBox);
            holder.paymentStatusBox = (TextView) convertView.findViewById(R.id.payment_status);

            holder.contactButton = (ImageView) convertView.findViewById(R.id.btn_contact);
            holder.payButton = (ImageView) convertView.findViewById(R.id.btn_pay);
            holder.driverButton = (ImageView) convertView.findViewById(R.id.btn_driver);

            holder.producerButtonFrame = (LinearLayout)convertView.findViewById(R.id.producerButtonFrame);
            holder.cancelButton = (TextView)convertView.findViewById(R.id.btn_cancel);
            holder.progressButton = (TextView)convertView.findViewById(R.id.btn_progress);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final OrderItem entity = (OrderItem) _datas.get(position);

        Typeface normal = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");
        holder.caption.setTypeface(normal);
        Typeface bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        holder.priceBox.setTypeface(bold);
        holder.paymentStatusBox.setTypeface(bold);

        holder.contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + entity.getContact()));
                _context.startActivity(intent);
            }
        });

        if(entity.getPayment_status().equals("pay")){
            holder.payButton.setVisibility(View.GONE);
            holder.driverButton.setVisibility(View.VISIBLE);
        }
        else {
            if(entity.getStatus().equals("ready") || entity.getStatus().equals("delivered")) {
                holder.payButton.setVisibility(View.VISIBLE);
                holder.driverButton.setVisibility(View.VISIBLE);
            }else {
                holder.payButton.setVisibility(View.GONE);
                holder.driverButton.setVisibility(View.GONE);
            }
        }

        if(entity.getPayment_status().equals("pay")){
            holder.paymentStatusBox.setText("(PAID)");
            holder.paymentStatusBox.setTextColor(Color.GREEN);
        }else {
            holder.paymentStatusBox.setText("(UNPAID)");
            holder.paymentStatusBox.setTextColor(Color.GRAY);
        }

        holder.payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Order order:Commons.ordersActivity.orders){
                    if(order.getId() == entity.getOrder_id()){
                        Commons.order = order;
                        break;
                    }
                }
                Commons.orderItem = entity;
                Commons.ordersActivity.showAlertDialogForPayment();
            }
        });

        holder.driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.orderItem = entity;
                if(Commons.ordersActivity != null){
                    Commons.ordersActivity.getDriver(String.valueOf(entity.getId()));
                }
            }
        });

        holder.orderIDBox.setText(entity.getOrderID());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        String date = dateFormat.format(new Date(Long.parseLong(entity.getDate_time())));
        holder.orderDateBox.setText(date);
        holder.priceBox.setText(df.format(entity.getPrice()) + " " + Constants.currency);
        holder.productNameBox.setText(entity.getProduct_name());
        holder.storeNameBox.setText(entity.getStore_name());
        holder.productNameBox.setTypeface(bold);
        holder.storeNameBox.setTypeface(normal);
        holder.statusBox.setTypeface(bold);
        holder.quantityBox.setText("X " + String.valueOf(entity.getQuantity()));

        if(entity.getStatus().length() > 0){
            holder.statusBox.setVisibility(View.VISIBLE);
            holder.statusBox.setText(Commons.orderStatus.statusStr.get(entity.getStatus()));
        }

        if(entity.getPicture_url().length() > 0){
            Picasso.with(_context)
                    .load(entity.getPicture_url())
                    .into(holder.pictureBox);
        }

        holder.pictureBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, ViewImageActivity.class);
                intent.putExtra("image", entity.getPicture_url());
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) _context, v, _context.getString(R.string.transition));
                _context.startActivity(intent, options.toBundle());
            }
        });

        holder.producerButtonFrame.setVisibility(View.GONE);

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Commons.receivedOrdersActivity.cancelOrderItem(entity);
            }
        });

        holder.progressButton.setText(Commons.orderStatus.nextStatusStr.get(entity.getStatus()));

        holder.progressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!entity.getStatus().equals("delivered"))
//                    Commons.receivedOrdersActivity.progressOrderItem(entity, Commons.orderStatus.nextStatus.get(entity.getStatus()));
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Order order:Commons.ordersActivity.orders){
                    if(order.getId() == entity.getOrder_id()){
                        Commons.order = order;
                        break;
                    }
                }
                Commons.orderItem = entity;
                Intent intent = new Intent(_context, OrderItemDetailActivity.class);
                _context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void filter(String charText){

        charText = charText.toLowerCase();
        _datas.clear();

        if(charText.length() == 0){
            _datas.addAll(_alldatas);
        }else {
            for (OrderItem item : _alldatas){
                if (item instanceof OrderItem) {
                    String value = ((OrderItem) item).getOrderID().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(item);
                    }else {
                        value = ((OrderItem) item).getCategory().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(item);
                        }else {
                            value = String.valueOf(((OrderItem) item).getPrice());
                            if (value.contains(charText)) {
                                _datas.add(item);
                            }else {
                                value = ((OrderItem) item).getSubcategory().toLowerCase();
                                if (value.contains(charText)) {
                                    _datas.add(item);
                                }else {
                                    value = ((OrderItem) item).getStore_name().toLowerCase();
                                    if (value.contains(charText)) {
                                        _datas.add(item);
                                    }else {
                                        value = ((OrderItem) item).getCategory().toLowerCase();
                                        if (value.contains(charText)) {
                                            _datas.add(item);
                                        }else {
                                            value = ((OrderItem) item).getProduct_name().toLowerCase();
                                            if (value.contains(charText)) {
                                                _datas.add(item);
                                            }else {
                                                value = ((OrderItem) item).getGender().toLowerCase();
                                                if (value.contains(charText)) {
                                                    _datas.add(item);
                                                }else {
                                                    value = String.valueOf(((OrderItem) item).getQuantity());
                                                    if (value.contains(charText)) {
                                                        _datas.add(item);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        TextView caption, productNameBox, storeNameBox;
        TextView orderIDBox, orderDateBox, priceBox, quantityBox, statusBox, paymentStatusBox;
        TextView cancelButton, progressButton;
        RoundedImageView pictureBox;
        ImageView contactButton, driverButton, payButton;
        LinearLayout producerButtonFrame;
    }
}











