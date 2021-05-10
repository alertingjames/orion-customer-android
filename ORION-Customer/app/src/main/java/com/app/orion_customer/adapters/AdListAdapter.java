package com.app.orion_customer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.app.orion_customer.R;
import com.app.orion_customer.models.Advertisement;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdListAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Advertisement> _datas = new ArrayList<>();
    private ArrayList<Advertisement> _alldatas = new ArrayList<>();

    public AdListAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setDatas(ArrayList<Advertisement> datas) {

        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_image_list, collection, false);
        ImageView pagerImage = (ImageView) layout.findViewById(R.id.picrure);

        final Advertisement entity = (Advertisement) _datas.get(position);
        if(entity.getPicture_res() > 0){
            pagerImage.setImageResource(entity.getPicture_res());
        }else if(entity.getPicture_url().length() > 0){
            Picasso.with(context)
                    .load(entity.getPicture_url())
                    .into(pagerImage);
        }

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        collection.addView(layout);

        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
        container.removeView((View) view);
    }

    @Override
    public int getCount() {
        return this._datas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}