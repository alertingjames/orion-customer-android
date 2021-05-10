package com.app.orion_customer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.app.orion_customer.R;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.models.Coupon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CouponListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Coupon> _datas = new ArrayList<>();
    private ArrayList<Coupon> _alldatas = new ArrayList<>();

    public CouponListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Coupon> datas) {

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
            convertView = inflater.inflate(R.layout.item_coupon, parent, false);

            holder.discountBox = (TextView) convertView.findViewById(R.id.discount);
            holder.expireTimeBox = (TextView) convertView.findViewById(R.id.expire_time);
            holder.optionBox = (TextView) convertView.findViewById(R.id.option);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Coupon entity = (Coupon) _datas.get(position);
        holder.discountBox.setText(String.valueOf(entity.getDiscount()) + "%");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String date = dateFormat.format(new Date(entity.getExpireTime()));
        holder.expireTimeBox.setText(date);
        holder.optionBox.setText(entity.getOption());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Commons.checkoutActivity != null){
                    Commons.checkoutActivity.couponId = entity.getId();
                    Commons.checkoutActivity.applyCoupon(entity.getDiscount());
                }
            }
        });

        return convertView;
    }

    class CustomHolder {

        TextView discountBox, expireTimeBox, optionBox;
    }
}











