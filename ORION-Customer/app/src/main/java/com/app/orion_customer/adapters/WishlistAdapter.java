package com.app.orion_customer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityOptionsCompat;

import com.app.orion_customer.R;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.main.ViewImageActivity;
import com.app.orion_customer.models.CartItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WishlistAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<CartItem> _datas = new ArrayList<>();
    private ArrayList<CartItem> _alldatas = new ArrayList<>();

    public WishlistAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<CartItem> datas) {

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
            convertView = inflater.inflate(R.layout.item_wishlist, parent, false);

            holder.picture = (ImageView) convertView.findViewById(R.id.pictureBox);
            holder.price = (TextView) convertView.findViewById(R.id.priceBox);
            holder.oldPrice= (TextView) convertView.findViewById(R.id.oldPriceBox);
            holder.productNameBox= (TextView) convertView.findViewById(R.id.productNameBox);
            holder.storeNameBox= (TextView) convertView.findViewById(R.id.storeNameBox);
            holder.oldPriceFrame= (FrameLayout) convertView.findViewById(R.id.oldPriceFrame);
            holder.delButton = (ImageView) convertView.findViewById(R.id.btn_delete);
            holder.cartButton = (ImageView) convertView.findViewById(R.id.btn_cart);
            holder.layout = (LinearLayout)convertView.findViewById(R.id.layout);
            holder.deliveryBox= (TextView) convertView.findViewById(R.id.deliveryBox);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final CartItem entity = (CartItem) _datas.get(position);

        Typeface bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        Typeface normal = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");

        if(entity.getNew_price() == 0){
            holder.oldPriceFrame.setVisibility(View.GONE);
            holder.price.setText(String.valueOf(entity.getPrice()) + " SGD");
        }
        else {
            holder.oldPriceFrame.setVisibility(View.VISIBLE);
            holder.price.setText(String.valueOf(entity.getNew_price()) + " SGD");
            holder.oldPrice.setText(String.valueOf(entity.getPrice()) + " SGD");
        }

        holder.productNameBox.setText(entity.getProduct_name());
        holder.storeNameBox.setText(entity.getStore_name());

        holder.storeNameBox.setTypeface(normal);
        holder.productNameBox.setTypeface(bold);

        holder.deliveryBox.setText(String.valueOf(entity.getDelivery_days()) + " Days, " + String.valueOf(entity.getDelivery_price()) + " SGD");
        holder.deliveryBox.setTypeface(normal);

        holder.price.setTypeface(normal);
        holder.oldPrice.setTypeface(normal);

        if(entity.getPicture_url().length() > 0){
            Picasso.with(_context)
                    .load(entity.getPicture_url())
                    .into(holder.picture);
        }

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Commons.cartActivity != null){
                    Commons.cartActivity.productInfo(String.valueOf(entity.getProduct_id()));
                }else {
                    Intent intent = new Intent(_context, ViewImageActivity.class);
                    intent.putExtra("image", entity.getPicture_url());
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) _context, v, _context.getString(R.string.transition));
                    _context.startActivity(intent, options.toBundle());
                }

            }
        });

        holder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.wishlistFragment.delWishlistItem(entity);
            }
        });

        holder.cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.wishlistFragment.addToCart(entity);
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            for (CartItem item : _alldatas){

                if (item != null) {

                    String value = item.getProduct_name().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(item);
                    }else {
                        value = item.getCategory().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(item);
                        }else {
                            value = String.valueOf((item).getPrice());
                            if (value.contains(charText)) {
                                _datas.add(item);
                            }else {
                                value = item.getStore_name().toLowerCase();
                                if (value.contains(charText)) {
                                    _datas.add(item);
                                }else {
                                    value = item.getSubcategory().toLowerCase();
                                    if (value.contains(charText)) {
                                        _datas.add(item);
                                    }else {
                                        value = item.getGender().toLowerCase();
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
        notifyDataSetChanged();
    }

    class CustomHolder {

        ImageView picture;
        TextView price, oldPrice, productNameBox, storeNameBox, deliveryBox;
        FrameLayout oldPriceFrame;
        LinearLayout layout;
        ImageView delButton, cartButton;
    }
}
































