package com.app.orion_customer.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.orion_customer.R;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.fragments.CategoryProductListFragment;
import com.app.orion_customer.fragments.StoreProductListFragment;
import com.app.orion_customer.models.Store;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoreListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Store> _datas = new ArrayList<>();
    private ArrayList<Store> _alldatas = new ArrayList<>();

    public StoreListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Store> datas) {

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
            convertView = inflater.inflate(R.layout.item_store_list, parent, false);

            holder.picture = (RoundedImageView) convertView.findViewById(R.id.pictureBox);
            holder.name = (TextView) convertView.findViewById(R.id.nameBox);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbar);
            holder.ratings = (TextView) convertView.findViewById(R.id.ratings);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Store entity = (Store) _datas.get(position);

        holder.name.setText(entity.getName());

        Typeface bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        holder.name.setTypeface(bold);

        holder.ratingBar.setRating(entity.getRatings());
        holder.ratings.setText(String.valueOf(entity.getRatings()));

        Picasso.with(_context).setLoggingEnabled(true);

        if(entity.getLogoUrl().length() > 0){
            View finalConvertView = convertView;
            Picasso.with(_context)
                    .load(entity.getLogoUrl()).error(R.drawable.noresult)
                    .into(holder.picture, new Callback() {
                        @Override
                        public void onSuccess() {
                            ((ProgressBar) finalConvertView.findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            ((ProgressBar) finalConvertView.findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
                        }
                    });
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.store = entity;
                if(Commons.mainActivity != null){
                    if(Commons.selectedCategory.length() > 0){
                        Fragment fragment = new CategoryProductListFragment();
                        FragmentManager manager = Commons.mainActivity.getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.container, fragment);
                        transaction.addToBackStack(null).commit();
                    }else {
                        Fragment fragment = new StoreProductListFragment();
                        FragmentManager manager = Commons.mainActivity.getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.container, fragment);
                        transaction.addToBackStack(null).commit();
                    }
                }
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
            for (Store store : _alldatas){

                if (store instanceof Store) {

                    String value = ((Store) store).getName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(store);
                    }else {
                        value = ((Store) store).getPhoneNumber().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(store);
                        }else {
                            value = ((Store) store).getAddress().toLowerCase();
                            if (value.contains(charText)) {
                                _datas.add(store);
                            }
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        RoundedImageView picture;
        TextView name, ratings;
        RatingBar ratingBar;
    }
}








