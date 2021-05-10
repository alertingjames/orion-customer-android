package com.app.orion_customer.adapters;

import android.content.Context;
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
import com.app.orion_customer.models.Phone;

import java.util.ArrayList;

public class PhoneListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Phone> _datas = new ArrayList<>();
    private ArrayList<Phone> _alldatas = new ArrayList<>();

    public PhoneListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Phone> datas) {

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
            convertView = inflater.inflate(R.layout.item_phone, parent, false);

            holder.phoneBox = (TextView) convertView.findViewById(R.id.phoneBox);
            holder.deleteButton = (ImageView) convertView.findViewById(R.id.btn_delete);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        Typeface font;
        font = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");

        final Phone entity = (Phone) _datas.get(position);
        holder.phoneBox.setText(entity.getPhone_number());
        holder.phoneBox.setTypeface(font);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.phoneListFragment.deletePhoneNumber(entity);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Commons.checkoutActivity != null){
                    Commons.phoneId = position;
                    Commons.phoneListFragment.getActivity().finish();
                }
            }
        });

        return convertView;
    }

    class CustomHolder {

        TextView phoneBox;
        ImageView deleteButton;
    }
}











