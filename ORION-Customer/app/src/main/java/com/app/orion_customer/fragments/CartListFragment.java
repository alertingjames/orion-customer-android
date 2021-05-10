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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.app.orion_customer.R;
import com.app.orion_customer.adapters.CartItemListAdapter;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.main.CartActivity;
import com.app.orion_customer.main.CheckoutActivity;
import com.app.orion_customer.main.CheckoutItemsActivity;
import com.app.orion_customer.main.MainActivity;
import com.app.orion_customer.models.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartListFragment extends Fragment {
    private int mPage;
    ListView listView;
    FrameLayout noResultLayout;
    TextView label1, label2, totalBox;
    Typeface bold, normal;
    TextView checkoutButton;
    LinearLayout totalLayout;

    CartItemListAdapter adapter = null;
    SharedPreferences shref;
    SharedPreferences.Editor editor;

    double total = 0.0d;
    public static DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_cartlist, viewGroup, false);

        Commons.cartListFragment = this;

        bold = Typeface.createFromAsset(getActivity().getAssets(), "futura medium bt.ttf");
        normal = Typeface.createFromAsset(getActivity().getAssets(), "futura book font.ttf");

        listView = (ListView)view.findViewById(R.id.list);
        noResultLayout = (FrameLayout) view.findViewById(R.id.no_result);
        label1 = (TextView) view.findViewById(R.id.lb1);
        label2 = (TextView)view.findViewById(R.id.lb2);
        label1.setTypeface(bold);
        label2.setTypeface(normal);

        totalBox = (TextView)view.findViewById(R.id.total_price);
        totalBox.setTypeface(normal);

        totalLayout = (LinearLayout)view.findViewById(R.id.totalFrame);

        shref = getActivity().getSharedPreferences("CART", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String response = shref.getString("cart" , "");
        ArrayList<CartItem> cartItems = gson.fromJson(response,
                new TypeToken<List<CartItem>>(){}.getType());

        if(cartItems != null){
            Commons.cartItems.clear();
            Commons.cartItems.addAll(cartItems);
            if(getActivity() != null){
                if(Commons.cartItems.isEmpty()){
                    noResultLayout.setVisibility(View.VISIBLE);
                    totalLayout.setVisibility(View.GONE);
                }else {
                    noResultLayout.setVisibility(View.GONE);
                    totalLayout.setVisibility(View.VISIBLE);
                }
                adapter = new CartItemListAdapter(getActivity());
                adapter.setDatas(Commons.cartItems);
                listView.setAdapter(adapter);
                refreshTotal();
            }
        }else {
            Commons.cartItems.clear();
            noResultLayout.setVisibility(View.VISIBLE);
            totalLayout.setVisibility(View.GONE);
        }

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

        checkoutButton = (TextView)view.findViewById(R.id.btn_checkout);
        checkoutButton.setTypeface(bold);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                startActivity(intent);
            }
        });

        setupUI(view, getActivity());

        return view;
    }

    public static CartListFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("CartFragment Page", page);
        CartListFragment fragment = new CartListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt("CartFragment");
        Log.d("Pager NO", String.valueOf(mPage));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void delCartItem(CartItem item){

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
                int index = Commons.cartItems.indexOf(item);
                Commons.cartItems.remove(index);

                refreshCart(Commons.cartItems);

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

    private void refreshCart(ArrayList<CartItem> items){
        Gson gson = new Gson();
        String json = gson.toJson(items);

        editor = shref.edit();
        editor.remove("cart").commit();
        editor.putString("cart", json);
        editor.commit();

        if(items.isEmpty()){
            noResultLayout.setVisibility(View.VISIBLE);
            totalLayout.setVisibility(View.GONE);
        }else {
            noResultLayout.setVisibility(View.GONE);
            totalLayout.setVisibility(View.VISIBLE);
        }

        if(getActivity() != null){
            if(items.isEmpty()){
                noResultLayout.setVisibility(View.VISIBLE);
                totalLayout.setVisibility(View.GONE);
            }else {
                noResultLayout.setVisibility(View.GONE);
                totalLayout.setVisibility(View.VISIBLE);
            }
            adapter = new CartItemListAdapter(getActivity());
            adapter.setDatas(items);
            listView.setAdapter(adapter);

            refreshTotal();
        }
    }

    public void updateCartItem(ImageView button, CartItem item, int quantity){
        item.setQuantity(quantity);
        refreshCart(Commons.cartItems);
        button.setVisibility(View.GONE);
    }

    public void addToWishlist(CartItem item){
        if(Commons.wishlistItems.size() > 0){
            for(CartItem item1:Commons.wishlistItems){
                if(item1.getProduct_id() == item.getProduct_id() && item1.getStore_id() == item.getStore_id()){
                    showToast("The product already exists in wishlist.");
                    return;
                }else {
                    int index1 = Commons.wishlistItems.indexOf(item1);
                    if(index1 == Commons.wishlistItems.size() - 1){
                        Commons.wishlistItems.add(item);
                    }
                }
            }
        }else {
            Commons.wishlistItems.add(item);
        }

        int index = Commons.cartItems.indexOf(item);
        Commons.cartItems.remove(index);
        refreshCart(Commons.cartItems);

        Gson gson = new Gson();
        String json = gson.toJson(Commons.wishlistItems);
        editor = shref.edit();
        editor.remove("wishlist").commit();
        editor.putString("wishlist", json);
        editor.commit();

        startActivity(getActivity().getIntent().putExtra("option", "wishlist"));
        getActivity().finish();
        getActivity().overridePendingTransition(0,0);
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

    private void refreshTotal(){
        total = 0.0d;
        for(CartItem cartItem: Commons.cartItems){
            total += (cartItem.getNew_price() > 0?cartItem.getNew_price():cartItem.getPrice()) * cartItem.getQuantity();
        }
        totalBox.setText(df.format(total) + " SGD");
    }

    @Override
    public void onResume() {
        super.onResume();
        Commons.cartListFragment = this;
    }

    @Override
    public void onStart() {
        super.onStart();
        Commons.cartListFragment = this;
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




























