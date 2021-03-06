package com.app.orion_customer.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_customer.R;
import com.app.orion_customer.adapters.StoreProductListAdapter;
import com.app.orion_customer.classes.CustomGridView;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.commons.ReqConst;
import com.app.orion_customer.main.LoginActivity;
import com.app.orion_customer.main.RateStoreActivity;
import com.app.orion_customer.models.Product;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoreProductListFragment extends Fragment {

    private int mPage = 0;
    RoundedImageView logoBox;
    TextView label, label1;
    TextView nameBox, phoneBox, addressBox;
    FloatingActionButton rateButton;
    ImageView backButton;
    CustomGridView listBox;
    RatingBar ratingBar;
    TextView ratingsBox, reviewsBox;
    FrameLayout noResultLayout;
    FrameLayout progressBar;
    ArrayList<Product> products = new ArrayList<>();
    StoreProductListAdapter adapter = null;

    public ImageView likeButton;
    public TextView likesBox;
    public int prodId = 0;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_store_products, viewGroup, false);

        Commons.storeProductListFragment = this;

        progressBar = (FrameLayout) view.findViewById(R.id.loading_bar);

        logoBox = (RoundedImageView)view.findViewById(R.id.logoBox);

        label = (TextView) view.findViewById(R.id.lb);
        label1 = (TextView) view.findViewById(R.id.lb1);

        nameBox = (TextView) view.findViewById(R.id.nameBox);
        phoneBox = (TextView) view.findViewById(R.id.phoneBox);
        addressBox = (TextView) view.findViewById(R.id.addressBox);
        rateButton = (FloatingActionButton) view.findViewById(R.id.btn_rate);
        backButton = (ImageView) view.findViewById(R.id.btn_back);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingbar);
        ratingsBox = (TextView) view.findViewById(R.id.ratings);

        reviewsBox = (TextView) view.findViewById(R.id.reviewsBox);

        listBox = (CustomGridView) view.findViewById(R.id.list);
        noResultLayout = (FrameLayout) view.findViewById(R.id.no_result);

        Typeface normal = Typeface.createFromAsset(getActivity().getAssets(), "futura book font.ttf");

        label.setTypeface(normal);
        label1.setTypeface(normal);
        nameBox.setTypeface(normal);
        phoneBox.setTypeface(normal);
        addressBox.setTypeface(normal);

        ((TextView)view.findViewById(R.id.lb2)).setTypeface(normal);

        Glide.with(getActivity()).load(Commons.store.getLogoUrl()).into(logoBox);
        nameBox.setText(Commons.store.getName());
        phoneBox.setText(Commons.store.getPhoneNumber());
        addressBox.setText(Commons.store.getAddress());
        ratingBar.setRating(Commons.store.getRatings());
        ratingsBox.setText(String.valueOf(Commons.store.getRatings()));
        reviewsBox.setText(String.valueOf(Commons.store.getReviews()));


        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Commons.thisUser != null){
                    Intent intent = new Intent(getActivity(), RateStoreActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Commons.storeProductListFragment = null;
    }

    private void getStoreProducts(String storeId) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getstoreproducts")
                .addBodyParameter("store_id", storeId)
                .addBodyParameter("member_id", (Commons.thisUser == null)?"0":String.valueOf(Commons.thisUser.get_idx()))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        progressBar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                products.clear();
                                JSONArray jsonArray = response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Product product = new Product();
                                    product.setIdx(object.getInt("id"));
                                    product.setStoreId(object.getInt("store_id"));
                                    product.setUserId(object.getInt("member_id"));
                                    product.setBrandId(object.getInt("brand_id"));
                                    product.setName(object.getString("name"));
                                    product.setPicture_url(object.getString("picture_url"));
                                    product.setCategory(object.getString("category"));
                                    product.setSubcategory(object.getString("subcategory"));
                                    product.setGender(object.getString("gender"));
                                    product.setGenderKey(object.getString("gender_key"));
                                    product.setPrice(Double.parseDouble(object.getString("price")));
                                    product.setNew_price(Double.parseDouble(object.getString("new_price")));
                                    product.setUnit(object.getString("unit"));
                                    product.setDescription(object.getString("description"));
                                    product.setRegistered_time(object.getString("registered_time"));
                                    product.setStatus(object.getString("status"));
                                    product.setBrand_name(object.getString("brand_name"));
                                    product.setBrand_logo(object.getString("brand_logo"));
                                    product.setDelivery_price(Double.parseDouble(object.getString("delivery_price")));
                                    product.setDelivery_days(Integer.parseInt(object.getString("delivery_days")));

                                    product.setLikes(object.getInt("likes"));
                                    product.setRatings(Float.parseFloat(object.getString("ratings")));

                                    if(object.getString("isLiked").equals("yes"))
                                        product.setLiked(true);
                                    else if(object.getString("isLiked").equals("no"))
                                        product.setLiked(false);
                                    else if(object.getString("isLiked").length() == 0)
                                        product.setLiked(false);

                                    products.add(product);
                                }

                                if(products.isEmpty()){
                                    noResultLayout.setVisibility(View.VISIBLE);
                                    ((TextView)view.findViewById(R.id.lb2)).setVisibility(View.GONE);
                                }
                                else {
                                    noResultLayout.setVisibility(View.GONE);
                                    ((TextView)view.findViewById(R.id.lb2)).setVisibility(View.VISIBLE);
                                    ((TextView)view.findViewById(R.id.lb2)).setText(String.valueOf(products.size()) + " items found");
                                }
                                if(getActivity() != null){
                                    adapter = new StoreProductListAdapter(getActivity());
                                    adapter.setDatas(products);
                                    listBox.setAdapter(adapter);
                                }

                            }else {
                                showToast("Server issue.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("ERROR!!!", error.getErrorBody());
                        progressBar.setVisibility(View.GONE);
                        showToast(error.getErrorDetail());
//                        toInit();
                    }
                });
    }

    public void showToast(String content){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.toast_view, null);
                TextView textView=(TextView)dialogView.findViewById(R.id.text);
                textView.setText(content);
                Toast toast=new Toast(getActivity());
                toast.setView(dialogView);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getStoreProducts(String.valueOf(Commons.store.getId()));
    }

    public void likeProduct(String productId){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "likeProduct")
                .addBodyParameter("product_id", productId)
                .addBodyParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        progressBar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                if(likeButton != null){
                                    likeButton.setBackgroundResource(R.drawable.ic_liked);
                                    for(Product product: products){
                                        if(product.getIdx() == prodId){
                                            product.setLiked(true);
                                            product.setLikes(product.getLikes() + 1);
                                            likesBox.setText(String.valueOf(product.getLikes()));
                                        }
                                    }
                                }
                            }else {
                                showToast("Server issue.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("ERROR!!!", error.getErrorBody());
                        progressBar.setVisibility(View.GONE);
                        showToast(error.getErrorDetail());
//                        toInit();
                    }
                });
    }

    public void unLikeProduct(String productId){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "unLikeProduct")
                .addBodyParameter("product_id", productId)
                .addBodyParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        progressBar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                if(likeButton != null){
                                    likeButton.setBackgroundResource(R.drawable.ic_like);
                                    for(Product product: products){
                                        if(product.getIdx() == prodId){
                                            product.setLiked(false);
                                            if(product.getLikes() > 0){
                                                product.setLikes(product.getLikes() - 1);
                                                likesBox.setText(String.valueOf(product.getLikes()));
                                            }
                                        }
                                    }
                                }
                            }else {
                                showToast("Server issue.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("ERROR!!!", error.getErrorBody());
                        progressBar.setVisibility(View.GONE);
                        showToast(error.getErrorDetail());
//                        toInit();
                    }
                });
    }

}




























