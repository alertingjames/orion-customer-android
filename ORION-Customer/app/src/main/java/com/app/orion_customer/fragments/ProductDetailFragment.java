package com.app.orion_customer.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_customer.R;
import com.app.orion_customer.adapters.CartItemListAdapter;
import com.app.orion_customer.adapters.PictureListAdapter;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.commons.ReqConst;
import com.app.orion_customer.main.CartActivity;
import com.app.orion_customer.main.LoginActivity;
import com.app.orion_customer.models.CartItem;
import com.app.orion_customer.models.Picture;
import com.app.orion_customer.models.Rating;
import com.bumptech.glide.Glide;
import com.github.mmin18.widget.RealtimeBlurView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.rd.PageIndicatorView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDetailFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    AVLoadingIndicatorView progressBar;
    Toolbar toolbar;
    private int mMaxScrollSize;
    private boolean mIsImageHidden = false;

    TextView priceBox, oldPriceBox, brandNameBox, categoryBox, storeNameBox, nameBox, descriptionBox, likeCountBox, cartCountBox, deliveryBox;
    FrameLayout oldPriceFrame, likesLayout, cartLayout, likesCountFrame, cartCountFrame;
    CircleImageView storeLogoBox;
    RoundedImageView brandLogoBox;
    private View mFab, mFab2, mFab3;

    RatingBar ratingBar;
    TextView ratingsBox;

    ViewPager pager;
    PageIndicatorView pageIndicatorView;
    ArrayList<Picture> pictures = new ArrayList<>();
    PictureListAdapter adapter = null;

    Typeface bold, normal;
    View view;

    private int count = 1;
    SharedPreferences shref;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_product_detail, viewGroup, false);

        shref = getActivity().getSharedPreferences("CART", Context.MODE_PRIVATE);

        bold = Typeface.createFromAsset(getActivity().getAssets(), "futura medium bt.ttf");
        normal = Typeface.createFromAsset(getActivity().getAssets(), "futura book font.ttf");

        mFab = view.findViewById(R.id.flexible_example_fab);
        mFab2 = view.findViewById(R.id.btn_feedback);
        mFab3 = view.findViewById(R.id.btn_wishlist);

        toolbar = (Toolbar) view.findViewById(R.id.flexible_example_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ratingBar = (RatingBar)view.findViewById(R.id.ratingbar);
        ratingsBox = (TextView)view.findViewById(R.id.ratings);
        ratingBar.setRating(Commons.product.getRatings());
        ratingsBox.setText(String.valueOf(Commons.product.getRatings()));

        AppBarLayout appbarLayout = (AppBarLayout) view.findViewById(R.id.flexible_example_appbar);

        //     setTitle(Commons.product.getName());

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogForAddToCart(getActivity());
            }
        });

        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Commons.thisUser == null){
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }else {
                    getProductRatings();
                }
            }
        });

        mFab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToWishlist();
            }
        });

        progressBar = (AVLoadingIndicatorView)view.findViewById(R.id.loading_bar);

        priceBox = (TextView)view.findViewById(R.id.priceBox);
        oldPriceBox = (TextView)view.findViewById(R.id.oldPriceBox);
        brandNameBox = (TextView)view.findViewById(R.id.brandNameBox);
        categoryBox = (TextView)view.findViewById(R.id.categoryBox);
        storeNameBox = (TextView)view.findViewById(R.id.storeNameBox);
        nameBox = (TextView)view.findViewById(R.id.nameBox);
        descriptionBox = (TextView)view.findViewById(R.id.descriptionBox);
        oldPriceFrame = (FrameLayout)view.findViewById(R.id.oldPriceFrame);

        deliveryBox = (TextView)view.findViewById(R.id.deliveryBox);
        deliveryBox.setTypeface(normal);
        deliveryBox.setText(String.valueOf(Commons.product.getDelivery_days()) + " Days, " + String.valueOf(Commons.product.getDelivery_price()) + " SGD");

        likesLayout = (FrameLayout)view.findViewById(R.id.likesLayout);
        likesCountFrame = (FrameLayout)view.findViewById(R.id.likesCountFrame);
        likeCountBox = (TextView)view.findViewById(R.id.likesCountBox);

        cartLayout = (FrameLayout)view.findViewById(R.id.cartLayout);
        cartCountFrame = (FrameLayout)view.findViewById(R.id.cartCountFrame);
        cartCountBox = (TextView)view.findViewById(R.id.cartCountBox);

        ((TextView)view.findViewById(R.id.caption)).setTypeface(bold);
        priceBox.setTypeface(bold);
        brandNameBox.setTypeface(bold);
        oldPriceBox.setTypeface(normal);
        descriptionBox.setTypeface(normal);
        categoryBox.setTypeface(normal);

        categoryBox.setText(Commons.product.getCategory() + "|" + Commons.product.getSubcategory() + "|" + Commons.product.getGender());

        brandLogoBox = (RoundedImageView) view.findViewById(R.id.brandLogoBox);

        storeLogoBox = (CircleImageView)view.findViewById(R.id.storeLogoBox);
        Glide.with(this).load(Commons.store.getLogoUrl()).into(storeLogoBox);

        storeNameBox.setText(Commons.store.getName());

        storeNameBox.setTypeface(bold);
        brandNameBox.setTypeface(bold);
        nameBox.setTypeface(bold);

        cartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
            }
        });

        likesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                intent.putExtra("option", "wishlist");
                startActivity(intent);
            }
        });

        likesCountFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                intent.putExtra("option", "wishlist");
                startActivity(intent);
            }
        });

        pager = view.findViewById(R.id.viewPager);
        pageIndicatorView = view.findViewById(R.id.pageIndicatorView);

        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();

        return view;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(verticalOffset)) * 100
                / mMaxScrollSize;

        Log.d("Percentage+++", String.valueOf(currentScrollPercentage));

        if (currentScrollPercentage >= 10) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;

                ViewCompat.animate(mFab).scaleY(0).scaleX(0).start();
                mFab2.setVisibility(View.VISIBLE);
                ViewCompat.animate(mFab2).scaleY(1).scaleX(1).start();
                ViewCompat.animate(mFab3).scaleY(0).scaleX(0).start();

                ((RealtimeBlurView)view.findViewById(R.id.real_time_blur_view)).setVisibility(View.VISIBLE);
                ((RealtimeBlurView)view.findViewById(R.id.real_time_blur_view))
                        .animate()
                        .alpha(1.0f)
                        .setDuration(500)
                        .start();
            }
        }else if (currentScrollPercentage <= 20) {
            if (mIsImageHidden) {
                mIsImageHidden = false;

                ViewCompat.animate(mFab).scaleY(1).scaleX(1).start();
                ViewCompat.animate(mFab2).scaleY(0).scaleX(0).start();
                ViewCompat.animate(mFab3).scaleY(1).scaleX(1).start();

                ((RealtimeBlurView)view.findViewById(R.id.real_time_blur_view))
                        .animate()
                        .alpha(0.0f)
                        .setDuration(500)
                        .start();
                ((RealtimeBlurView)view.findViewById(R.id.real_time_blur_view)).setVisibility(View.GONE);
            }
        }
    }

    private void getProductPictures(String productId) {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getProductPictures")
                .addBodyParameter("product_id", productId)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        progressBar.setVisibility(View.GONE);
                        try {
                            String success = response.getString("result_code");
                            Log.d("Rcode=====> :",success);
                            if (success.equals("0")) {
                                pictures.clear();
                                JSONArray dataArr = response.getJSONArray("data");
                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject object = (JSONObject) dataArr.get(i);
                                    Picture picture = new Picture();
                                    picture.setIdx(object.getInt("id"));
                                    picture.setUrl(object.getString("picture_url"));
                                    picture.setFile(null);
                                    pictures.add(picture);
                                }

                                Commons.product.set_pictureList(pictures);
                                if(getActivity() != null){
                                    adapter = new PictureListAdapter(getActivity());
                                    adapter.setDatas(pictures);
                                    adapter.notifyDataSetChanged();
                                    pager.setAdapter(adapter);
                                }

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

        initCart();
        initWishlist();

        toolbar.setTitle(Commons.product.getName());
        nameBox.setText(Commons.product.getName());
        if(Commons.product.getNew_price() == 0){
            oldPriceFrame.setVisibility(View.GONE);
            priceBox.setText(String.valueOf(Commons.product.getPrice()) + " SGD");
        }
        else {
            oldPriceFrame.setVisibility(View.VISIBLE);
            priceBox.setText(String.valueOf(Commons.product.getNew_price()) + " SGD");
            oldPriceBox.setText(String.valueOf(Commons.product.getPrice()) + " SGD");
        }

        descriptionBox.setText(Commons.product.getDescription());

        if(Commons.product.getLikes() == 0)likesLayout.setVisibility(View.GONE);
        else {
            likesLayout.setVisibility(View.VISIBLE);
            likeCountBox.setText(String.valueOf(Commons.product.getLikes()));
            likesLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        Glide.with(this).load(Commons.product.getBrand_logo()).into(brandLogoBox);
        brandNameBox.setText(Commons.product.getBrand_name());

        getProductPictures(String.valueOf(Commons.product.getIdx()));

    }

    public void showAlertDialogForAddToCart(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_count_to_cart, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        TextView title = (TextView)view.findViewById(R.id.title);
        title.setTypeface(bold);
        TextView countBox = (TextView)view.findViewById(R.id.count);
        countBox.setTypeface(bold);

        ImageView plusButton = (ImageView)view.findViewById(R.id.btn_increase);
        ImageView minusButton = (ImageView)view.findViewById(R.id.btn_decrease);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                countBox.setText(String.valueOf(count));
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                if(count < 1) count = 1;
                countBox.setText(String.valueOf(count));
            }
        });

        ImageButton cartButton = (ImageButton) view.findViewById(R.id.btn_to_cart);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CartItem item = new CartItem();
                item.setUser_id(Commons.product.getUserId());
                item.setStore_id(Commons.product.getStoreId());
                item.setBrand_id(Commons.product.getBrandId());
                item.setProduct_id(Commons.product.getIdx());
                item.setStore_name(Commons.store.getName());
                item.setProduct_name(Commons.product.getName());
                item.setCategory(Commons.product.getCategory());
                item.setSubcategory(Commons.product.getSubcategory());
                item.setGender(Commons.product.getGender());
                item.setGender_key(Commons.product.getGenderKey());
                item.setPrice(Commons.product.getPrice());
                item.setNew_price(Commons.product.getNew_price());
                item.setPicture_url(Commons.product.getPicture_url());
                item.setDelivery_price(Commons.product.getDelivery_price());
                item.setDelivery_days(Commons.product.getDelivery_days());
                item.setQuantity(count);
                item.setDate_time(String.valueOf(new Date().getTime()));
                item.setStatus("");

                if(Commons.cartItems.size() > 0){
                    for(CartItem item1:Commons.cartItems){
                        if(item1.getStore_id() == item.getStore_id() && item1.getProduct_id() == item.getProduct_id()){
                            item1.setQuantity(item1.getQuantity() + count);
                            break;
                        }else {
                            int index = Commons.cartItems.indexOf(item1);
                            if(index == Commons.cartItems.size() - 1){
                                Commons.cartItems.add(item);
                            }
                        }
                    }
                }else {
                    Commons.cartItems.add(item);
                }

                Gson gson = new Gson();
                String json = gson.toJson(Commons.cartItems);

                editor = shref.edit();
                editor.remove("cart").commit();
                editor.putString("cart", json);
                editor.commit();

                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);

                dialog.dismiss();
            }
        });

        ImageView cancelButton = (ImageView)view.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        // Set alert dialog width equal to screen width 80%
        int dialogWindowWidth = (int) (displayWidth * 0.8f);
        // Set alert dialog height equal to screen height 80%
        //    int dialogWindowHeight = (int) (displayHeight * 0.8f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        //      layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        dialog.getWindow().setAttributes(layoutParams);
        dialog.setCancelable(false);
    }

    public void showAlertDialogForFeedback(Activity activity, float myRatings, String myFeedback, int allReviewsCount, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_feedback, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        TextView titleBox = (TextView)view.findViewById(R.id.title);
        titleBox.setTypeface(bold);
        titleBox.setText(title);
        TextView ratingBox = (TextView)view.findViewById(R.id.ratingBox);
        ratingBox.setTypeface(normal);
        ratingBox.setText(String.valueOf(myRatings));
        RatingBar ratingBar0 = (RatingBar) view.findViewById(R.id.ratingbar0);
        ratingBar0.setRating(Commons.product.getRatings());
        TextView ratings = (TextView)view.findViewById(R.id.ratings);
        ratings.setText(String.valueOf(Commons.product.getRatings()));
        TextView reviewsBox = (TextView)view.findViewById(R.id.reviews);
        reviewsBox.setText(String.valueOf(allReviewsCount));
        ratings.setText(String.valueOf(Commons.product.getRatings()));
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingbar);
        ratingBar.setRating(myRatings);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rt, boolean fromUser) {
                Float ratingvalue = (Float) ratingBar.getRating();
                ratingBox.setText(String.valueOf(ratingvalue));
            }
        });
        EditText feedbackBox = (EditText) view.findViewById(R.id.feedbackBox);
        feedbackBox.setTypeface(normal);
        feedbackBox.setText(myFeedback);
        TextView submitButton = (TextView) view.findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitFeedback(ratingBar, feedbackBox);

                dialog.dismiss();
            }
        });

        ImageView cancelButton = (ImageView)view.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        // Set alert dialog width equal to screen width 80%
        int dialogWindowWidth = (int) (displayWidth * 0.95f);
        // Set alert dialog height equal to screen height 80%
        //    int dialogWindowHeight = (int) (displayHeight * 0.8f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        //      layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        dialog.getWindow().setAttributes(layoutParams);
        dialog.setCancelable(false);
    }

    private void getProductRatings(){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getProductRatings")
                .addBodyParameter("product_id", String.valueOf(Commons.product.getIdx()))
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
                                JSONObject object = response.getJSONObject("data");
                                Rating rating = new Rating();
                                rating.setIdx(object.getInt("id"));
                                rating.setProductId(object.getInt("product_id"));
                                rating.setUserId(object.getInt("member_id"));
                                rating.setUserName(object.getString("member_name"));
                                rating.setUserPictureUrl(object.getString("member_photo"));
                                rating.setRating(Float.parseFloat(object.getString("rating")));
                                rating.setSubject(object.getString("subject"));
                                rating.setDescription(object.getString("description"));
                                rating.setDate(object.getString("date_time"));

                                int allreviews = response.getInt("allreviews");
                                showAlertDialogForFeedback(getActivity(), rating.getRating(), rating.getDescription(), allreviews, "Update feedback");
                            }else if(result.equals("1")){
                                showAlertDialogForFeedback(getActivity(), 0.0f, "", 0, "Place feedback");
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
                    }
                });
    }

    public void submitFeedback(RatingBar ratingBar, EditText feedbackBox){

        if(ratingBar.getRating() == 0){
            showToast("Please rate out of 5 stars");
            return;
        }

        if(feedbackBox.getText().toString().trim().length() == 0){
            showToast("Write your feedback.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "placeProductFeedback")
                .addBodyParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
                .addBodyParameter("product_id", String.valueOf(Commons.product.getIdx()))
                .addBodyParameter("rating", String.valueOf(ratingBar.getRating()))
                .addBodyParameter("description", feedbackBox.getText().toString().trim())
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
                                showToast("Your feedback submited.");
                                Commons.product.setRatings(Float.parseFloat(response.getString("ratings")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

    private void initCart(){

        Gson gson = new Gson();
        String response = shref.getString("cart" , "");
        ArrayList<CartItem> cartItems = gson.fromJson(response,
                new TypeToken<List<CartItem>>(){}.getType());

        if(cartItems != null){
            Commons.cartItems.clear();
            Commons.cartItems.addAll(cartItems);
            if(Commons.cartItems.size() > 0){
                cartCountFrame.setVisibility(View.VISIBLE);
                int i = 0;
                for(CartItem item:Commons.cartItems){
                    i = i + item.getQuantity();
                }
                cartCountBox.setText(String.valueOf(i));
            }else {
                cartCountFrame.setVisibility(View.GONE);
            }
        }

    }

    private void initWishlist(){

        Gson gson = new Gson();
        String response = shref.getString("wishlist" , "");
        ArrayList<CartItem> wishlistItems = gson.fromJson(response,
                new TypeToken<List<CartItem>>(){}.getType());

        if(wishlistItems != null){
            Commons.wishlistItems.clear();
            Commons.wishlistItems.addAll(wishlistItems);
            if(Commons.wishlistItems.size() > 0){
                likesCountFrame.setVisibility(View.VISIBLE);
                likeCountBox.setText(String.valueOf(Commons.wishlistItems.size()));
            }else {
                likesCountFrame.setVisibility(View.GONE);
            }
        }

    }

    private void addToWishlist() {

        CartItem item = new CartItem();
        item.setUser_id(Commons.product.getUserId());
        item.setStore_id(Commons.product.getStoreId());
        item.setBrand_id(Commons.product.getBrandId());
        item.setProduct_id(Commons.product.getIdx());
        item.setStore_name(Commons.store.getName());
        item.setProduct_name(Commons.product.getName());
        item.setCategory(Commons.product.getCategory());
        item.setSubcategory(Commons.product.getSubcategory());
        item.setGender(Commons.product.getGender());
        item.setGender_key(Commons.product.getGenderKey());
        item.setPrice(Commons.product.getPrice());
        item.setNew_price(Commons.product.getNew_price());
        item.setPicture_url(Commons.product.getPicture_url());
        item.setDate_time(String.valueOf(new Date().getTime()));
        item.setDelivery_price(Commons.product.getDelivery_price());
        item.setDelivery_days(Commons.product.getDelivery_days());
        item.setStatus("");

        if(Commons.wishlistItems.size() > 0){
            for(CartItem item1:Commons.wishlistItems){
                if(item1.getStore_id() == item.getStore_id() && item1.getProduct_id() == item.getProduct_id()){
                    showToast("The product already exists in wishlist.");
                    return;
                }else {
                    int index = Commons.wishlistItems.indexOf(item1);
                    if(index == Commons.wishlistItems.size() - 1){
                        Commons.wishlistItems.add(item);
                    }
                }
            }
        }else {
            Commons.wishlistItems.add(item);
        }

        Gson gson = new Gson();
        String json = gson.toJson(Commons.wishlistItems);

        editor = shref.edit();
        editor.remove("wishlist").commit();
        editor.putString("wishlist", json);
        editor.commit();

        Intent intent = new Intent(getActivity(), CartActivity.class);
        startActivity(intent);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}


























