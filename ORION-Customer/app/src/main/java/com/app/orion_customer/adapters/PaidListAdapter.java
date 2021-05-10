package com.app.orion_customer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.app.orion_customer.R;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.main.PaidDetailActivity;
import com.app.orion_customer.models.Address;
import com.app.orion_customer.models.CartItem;
import com.app.orion_customer.models.Paid;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaidListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Paid> _datas = new ArrayList<>();
    private ArrayList<Paid> _alldatas = new ArrayList<>();
    public static DecimalFormat df = new DecimalFormat("0.00");

    public PaidListAdapter(Context context){
        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Paid> datas) {
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
            convertView = inflater.inflate(R.layout.item_payments, parent, false);

            holder.dateTimeBox = (TextView) convertView.findViewById(R.id.dateTimeBox);
            holder.storeNameBox = (TextView) convertView.findViewById(R.id.storeNameBox);
            holder.itemsCountBox = (TextView) convertView.findViewById(R.id.itemsCountBox);

            holder.orderIDBox = (TextView) convertView.findViewById(R.id.orderIDBox);
            holder.priceBox = (TextView) convertView.findViewById(R.id.priceBox);
            holder.unitBox = (TextView) convertView.findViewById(R.id.unitBox);
            holder.statusBox = (TextView) convertView.findViewById(R.id.statusBox);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        Typeface bold, normal;

        bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        normal = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");

        holder.dateTimeBox.setTypeface(bold);
        holder.storeNameBox.setTypeface(normal);
        holder.itemsCountBox.setTypeface(normal);
        holder.orderIDBox.setTypeface(bold);
        holder.priceBox.setTypeface(bold);
        holder.statusBox.setTypeface(bold);
        holder.unitBox.setTypeface(bold);

        final Paid entity = (Paid) _datas.get(position);
        holder.storeNameBox.setText(entity.getStore_name());
        holder.orderIDBox.setText("ID: " + entity.getOrderID());
        holder.itemsCountBox.setText("Items: " + String.valueOf(entity.getItemsCount()));
        holder.priceBox.setText(df.format(entity.getPaid_amount()/100));
        holder.statusBox.setText(entity.getPayment_status().equals("pay")?"PAID":entity.getPayment_status().equals("refund")?"REFUNDED":"PAID");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String myDate = dateFormat.format(new Date(Long.parseLong(entity.getPaid_time())));
        holder.dateTimeBox.setText(myDate);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.paid = entity;
                Intent intent = new Intent(_context, PaidDetailActivity.class);
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
            for (Paid paid : _alldatas){
                if (paid != null) {
                    String value = paid.getStore_name().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(paid);
                    }else {
                        value = paid.getOrderID().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(paid);
                        }else {
                            value = String.valueOf(paid.getPaid_amount());
                            if (value.contains(charText)) {
                                _datas.add(paid);
                            }else {
                                value = String.valueOf((paid.getItemsCount()));
                                if (value.contains(charText)) {
                                    _datas.add(paid);
                                }else {
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                                    value = dateFormat.format(new Date(Long.parseLong(paid.getPaid_time())));
                                    if (value.contains(charText)) {
                                        _datas.add(paid);
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
        TextView dateTimeBox, orderIDBox, itemsCountBox, storeNameBox, priceBox, unitBox, statusBox;
    }
}











