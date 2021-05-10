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
import androidx.fragment.app.Fragment;

import com.app.orion_customer.R;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.main.CartActivity;
import com.app.orion_customer.main.ViewImageActivity;
import com.app.orion_customer.models.CartItem;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class CartItemListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<CartItem> _datas = new ArrayList<>();
    private ArrayList<CartItem> _alldatas = new ArrayList<>();
    public static DecimalFormat df = new DecimalFormat("0.00");

    public CartItemListAdapter(Context context){

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
            convertView = inflater.inflate(R.layout.item_cart, parent, false);

            holder.storeNameBox = (TextView) convertView.findViewById(R.id.storeNameBox);
            holder.productNameBox = (TextView) convertView.findViewById(R.id.productNameBox);
            holder.priceBox = (TextView) convertView.findViewById(R.id.priceBox);
            holder.quantityBox = (TextView) convertView.findViewById(R.id.quantityBox);
            holder.pictureBox = (ImageView) convertView.findViewById(R.id.pictureBox);
            holder.decreaseButton = (ImageView)convertView.findViewById(R.id.btn_decrease);
            holder.increaseButton = (ImageView) convertView.findViewById(R.id.btn_increase);
            holder.deleteButton = (ImageView) convertView.findViewById(R.id.btn_delete);
            holder.layout = (LinearLayout)convertView.findViewById(R.id.layout);

            holder.wishlistButton = (ImageView) convertView.findViewById(R.id.btn_wishlist);
            holder.okButton = (ImageView) convertView.findViewById(R.id.btn_ok);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final CartItem entity = (CartItem) _datas.get(position);

        Typeface bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        Typeface normal = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");

        holder.storeNameBox.setText(entity.getStore_name());
        holder.productNameBox.setText(entity.getProduct_name());

        holder.storeNameBox.setTypeface(normal);
        holder.productNameBox.setTypeface(bold);

        if(entity.getNew_price() == 0){
            holder.priceBox.setText(df.format(entity.getPrice()) + " " + "SGD");
        }
        else {
            holder.priceBox.setText(df.format(entity.getNew_price()) + " " + "SGD");
        }

        holder.priceBox.setTypeface(normal);

        holder.quantityBox.setText(String.valueOf(entity.getQuantity()));

        holder.decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(holder.quantityBox.getText().toString()) > 1){
                    holder.okButton.setVisibility(View.VISIBLE);
                    holder.quantityBox.setText(String.valueOf(Integer.parseInt(holder.quantityBox.getText().toString()) - 1));
                }
            }
        });

        holder.increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.okButton.setVisibility(View.VISIBLE);
                holder.quantityBox.setText(String.valueOf(Integer.parseInt(holder.quantityBox.getText().toString()) + 1));
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.cartListFragment.delCartItem(entity);

            }
        });

        holder.wishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.cartListFragment.addToWishlist(entity);
            }
        });

        holder.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.cartListFragment.updateCartItem(holder.okButton, entity, Integer.parseInt(holder.quantityBox.getText().toString()));
            }
        });

        if(entity.getPicture_url().length() > 0){
            Picasso.with(_context)
                    .load(entity.getPicture_url())
                    .error(R.drawable.noresult)
                    .placeholder(R.drawable.noresult)
                    .into(holder.pictureBox);
        }

        holder.pictureBox.setOnClickListener(new View.OnClickListener() {
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
            for (CartItem cart : _alldatas){
                if (cart instanceof CartItem) {
                    String value = ((CartItem) cart).getProduct_name().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(cart);
                    }else {
                        value = ((CartItem) cart).getCategory().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(cart);
                        }else {
                            value = String.valueOf(((CartItem) cart).getPrice());
                            if (value.contains(charText)) {
                                _datas.add(cart);
                            }else {
                                value = String.valueOf(((CartItem) cart).getQuantity());
                                if (value.contains(charText)) {
                                    _datas.add(cart);
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

        ImageView pictureBox;
        TextView storeNameBox, productNameBox;
        TextView priceBox;
        TextView quantityBox;
        LinearLayout layout;
        ImageView deleteButton, decreaseButton, increaseButton, okButton, wishlistButton;
    }
}











