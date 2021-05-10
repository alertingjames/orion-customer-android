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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.app.orion_customer.R;
import com.app.orion_customer.adapters.CartItemListAdapter;
import com.app.orion_customer.adapters.WishlistAdapter;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.main.CartActivity;
import com.app.orion_customer.main.MainActivity;
import com.app.orion_customer.models.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WishlistFragment extends Fragment {
    private int mPage;
    ListView listView;
    FrameLayout noResultLayout;
    TextView label1, label2;
    Typeface bold, normal;

    WishlistAdapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_wishlist, viewGroup, false);

        Commons.wishlistFragment = this;

        bold = Typeface.createFromAsset(getActivity().getAssets(), "futura medium bt.ttf");
        normal = Typeface.createFromAsset(getActivity().getAssets(), "futura book font.ttf");

        listView = (ListView)view.findViewById(R.id.list);
        noResultLayout = (FrameLayout) view.findViewById(R.id.no_result);
        label1 = (TextView) view.findViewById(R.id.lb1);
        label2 = (TextView)view.findViewById(R.id.lb2);
        label1.setTypeface(bold);
        label2.setTypeface(normal);

        label2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
        });

        setupUI(view, getActivity());

        return view;
    }

    public static WishlistFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("WishlistFragment Page", page);
        WishlistFragment fragment = new WishlistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt("WishlistFragment");
        Log.d("Pager NO", String.valueOf(mPage));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void delWishlistItem(CartItem item){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_alert_question, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        TextView titleBox = (TextView)view.findViewById(R.id.title);
        titleBox.setTypeface(bold);
        titleBox.setText("Warning!");
        TextView contentBox = (TextView)view.findViewById(R.id.content);
        contentBox.setTypeface(normal);
        contentBox.setText("Are you sure you want to delete?");
        TextView noButton = (TextView)view.findViewById(R.id.no_button);
        noButton.setTypeface(bold);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        TextView yesButton = (TextView)view.findViewById(R.id.yes_button);
        yesButton.setTypeface(bold);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = Commons.wishlistItems.indexOf(item);
                Commons.wishlistItems.remove(index);
                refreshWishlist(Commons.wishlistItems);

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

    private void refreshWishlist(ArrayList<CartItem> items){

        SharedPreferences shref;
        SharedPreferences.Editor editor;
        shref = getActivity().getSharedPreferences("CART", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = gson.toJson(items);

        editor = shref.edit();
        editor.remove("wishlist").commit();
        editor.putString("wishlist", json);
        editor.commit();

        if(items.isEmpty()){
            noResultLayout.setVisibility(View.VISIBLE);
        }else {
            noResultLayout.setVisibility(View.GONE);
        }

        if(getActivity() != null){
            adapter = new WishlistAdapter(getActivity());
            adapter.setDatas(items);
            listView.setAdapter(adapter);
        }
    }

    public void addToCart(CartItem item){
        showAlertDialogForAddToCart(getActivity(), item);
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

    int count = 1;

    public void showAlertDialogForAddToCart(Activity activity, CartItem item){
        count = 1;
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

                if(Commons.cartItems.size() > 0){
                    for(CartItem item1:Commons.cartItems){
                        if(item1.getProduct_id() == item.getProduct_id() && item1.getStore_id() == item.getStore_id()){
                            item1.setQuantity(item1.getQuantity() + count);
                            break;
                        }else {
                            int index1 = Commons.cartItems.indexOf(item1);
                            if (index1 == Commons.cartItems.size() - 1) {
                                item.setQuantity(count);
                                Commons.cartItems.add(item);
                            }
                        }
                    }
                }else {
                    item.setQuantity(count);
                    Commons.cartItems.add(item);
                }

                int index = Commons.wishlistItems.indexOf(item);
                Commons.wishlistItems.remove(index);
                refreshWishlist(Commons.wishlistItems);

                SharedPreferences shref;
                SharedPreferences.Editor editor;
                shref = getActivity().getSharedPreferences("CART", Context.MODE_PRIVATE);

                Gson gson = new Gson();
                String json = gson.toJson(Commons.cartItems);

                editor = shref.edit();
                editor.remove("cart").commit();
                editor.putString("cart", json);
                editor.commit();

                startActivity(getActivity().getIntent());
                getActivity().finish();
                getActivity().overridePendingTransition(0,0);

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

    @Override
    public void onResume() {
        super.onResume();
        Commons.wishlistFragment = this;
        initWishlist();
    }

    @Override
    public void onStart() {
        super.onStart();
        Commons.wishlistFragment = this;
    }

    public void initWishlist(){

        SharedPreferences shref;
        SharedPreferences.Editor editor;

        shref = getActivity().getSharedPreferences("CART", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String response = shref.getString("wishlist" , "");
        ArrayList<CartItem> wishlistItems = gson.fromJson(response,
                new TypeToken<List<CartItem>>(){}.getType());

        if(wishlistItems != null){
            Commons.wishlistItems.clear();
            Commons.wishlistItems.addAll(wishlistItems);
            refreshWishlist(Commons.wishlistItems);
        }else {
            Commons.wishlistItems.clear();
            refreshWishlist(Commons.wishlistItems);
        }

    }

    public void setupUI(View view, Activity activity) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    try{
                        hideSoftKeyboard(activity);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, activity);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

}

































