package com.app.orion_customer.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.orion_customer.R;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.fragments.ProductDetailFragment;
import com.app.orion_customer.models.Product;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoreProductListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Product> _datas = new ArrayList<>();
    private ArrayList<Product> _alldatas = new ArrayList<>();

    public StoreProductListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Product> datas) {

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
            convertView = inflater.inflate(R.layout.item_product_list, parent, false);

            holder.picture = (ImageView) convertView.findViewById(R.id.pictureBox);
            holder.price = (TextView) convertView.findViewById(R.id.priceBox);
            holder.oldPrice= (TextView) convertView.findViewById(R.id.oldPriceBox);
            holder.oldPriceFrame= (FrameLayout) convertView.findViewById(R.id.oldPriceFrame);
            holder.deliveryBox= (TextView) convertView.findViewById(R.id.deliveryBox);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbar);
            holder.ratings = (TextView) convertView.findViewById(R.id.ratings);
            holder.likes = (TextView) convertView.findViewById(R.id.likes);
            holder.likeButton = (ImageView) convertView.findViewById(R.id.btn_like);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Product entity = (Product) _datas.get(position);

        if(entity.getNew_price() == 0){
            holder.oldPriceFrame.setVisibility(View.GONE);
            holder.price.setText(String.valueOf(entity.getPrice()) + " SGD");
        }
        else {
            holder.oldPriceFrame.setVisibility(View.VISIBLE);
            holder.price.setText(String.valueOf(entity.getNew_price()) + " SGD");
            holder.oldPrice.setText(String.valueOf(entity.getPrice()) + " SGD");
        }

        Typeface book = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");
        holder.price.setTypeface(book);
        holder.oldPrice.setTypeface(book);

        holder.likes.setText(String.valueOf(entity.getLikes()));
        holder.ratings.setText(String.valueOf(entity.getRatings()));
        holder.ratingBar.setRating(entity.getRatings());
        holder.deliveryBox.setTypeface(book);
        holder.deliveryBox.setText(String.valueOf(entity.getDelivery_days()) + " Days, " + String.valueOf(entity.getDelivery_price()) + " SGD");

        if(entity.isLiked())holder.likeButton.setBackgroundResource(R.drawable.ic_liked);
        else if(!entity.isLiked())holder.likeButton.setBackgroundResource(R.drawable.ic_like);

        if(Commons.thisUser == null)holder.likeButton.setVisibility(View.GONE);
        else holder.likeButton.setVisibility(View.VISIBLE);

        if(entity.getPicture_url().length() > 0){
            View finalConvertView = convertView;
            Picasso.with(_context)
                    .load(entity.getPicture_url())
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

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Commons.storeProductListFragment != null){
                    if(entity.isLiked())Commons.storeProductListFragment.unLikeProduct(String.valueOf(entity.getIdx()));
                    else Commons.storeProductListFragment.likeProduct(String.valueOf(entity.getIdx()));
                    Commons.storeProductListFragment.likeButton = holder.likeButton;
                    Commons.storeProductListFragment.likesBox = holder.likes;
                    Commons.storeProductListFragment.prodId = entity.getIdx();
                }else if(Commons.categoryProductListFragment != null){
                    if(entity.isLiked())Commons.categoryProductListFragment.unLikeProduct(String.valueOf(entity.getIdx()));
                    else Commons.categoryProductListFragment.likeProduct(String.valueOf(entity.getIdx()));
                    Commons.categoryProductListFragment.likeButton = holder.likeButton;
                    Commons.categoryProductListFragment.likesBox = holder.likes;
                    Commons.categoryProductListFragment.prodId = entity.getIdx();
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.product = entity;
                Fragment fragment = new ProductDetailFragment();
                FragmentManager manager = Commons.mainActivity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.activity, fragment);
                transaction.addToBackStack(null).commit();
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
            for (Product product : _alldatas){

                if (product instanceof Product) {

                    String value = ((Product) product).getName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(product);
                    }else {
                        value = ((Product) product).getCategory().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(product);
                        }else {
                            value = String.valueOf(((Product) product).getPrice());
                            if (value.contains(charText)) {
                                _datas.add(product);
                            }else {
                                value = ((Product) product).getDescription().toLowerCase();
                                if (value.contains(charText)) {
                                    _datas.add(product);
                                }else {
                                    value = ((Product) product).getGender().toLowerCase();
                                    if (value.contains(charText)) {
                                        _datas.add(product);
                                    }else {
                                        value = ((Product) product).getGenderKey().toLowerCase();
                                        if (value.contains(charText)) {
                                            _datas.add(product);
                                        }else {
                                            value = ((Product) product).getSubcategory().toLowerCase();
                                            if (value.contains(charText)) {
                                                _datas.add(product);
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

        ImageView picture;
        TextView price, oldPrice, deliveryBox;
        FrameLayout oldPriceFrame;
        RatingBar ratingBar;
        TextView ratings, likes;
        ImageView likeButton;
    }
}
































