package com.app.orion_customer.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_customer.R;
import com.app.orion_customer.adapters.AdListAdapter;
import com.app.orion_customer.adapters.StoreListAdapter;
import com.app.orion_customer.classes.CustomGridView;
import com.app.orion_customer.commons.ReqConst;
import com.app.orion_customer.models.Advertisement;
import com.app.orion_customer.models.Store;
import com.google.android.gms.maps.model.LatLng;
import com.rd.PageIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainHomeFragement extends Fragment {
    private int mPage;

    CustomGridView list;
    public FrameLayout progressBar;

    ArrayList<Store> stores = new ArrayList<>();
    StoreListAdapter adapter = null;

    ViewPager pager;
    PageIndicatorView pageIndicatorView;
    ArrayList<Advertisement> advertisements = new ArrayList<>();
    AdListAdapter adListAdapter = null;

    final static int[] ads = {
            R.drawable.ad1, R.drawable.ad2, R.drawable.ad3, R.drawable.ad4, R.drawable.ad5, R.drawable.ad6, R.drawable.ad7
    };

    View view;
    Typeface bold, normal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_main, viewGroup, false);

        progressBar = (FrameLayout) view.findViewById(R.id.loading_bar);
        pager = (ViewPager) view.findViewById(R.id.viewPager);
        pageIndicatorView = (PageIndicatorView) view.findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setRadius(3);
        pageIndicatorView.setPadding(10);

        list = (CustomGridView) view.findViewById(R.id.list);

        bold = Typeface.createFromAsset(getActivity().getAssets(), "futura medium bt.ttf");
        normal = Typeface.createFromAsset(getActivity().getAssets(), "futura book font.ttf");

        ((TextView)view.findViewById(R.id.lb)).setTypeface(bold);
        ((TextView)view.findViewById(R.id.lb1)).setTypeface(normal);

        return view;
    }

    public static MainHomeFragement newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("MainHomeFragement Page", page);
        MainHomeFragement fragment = new MainHomeFragement();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt("MainHomeFragement Home");
        Log.d("Pager NO", String.valueOf(mPage));

    }

    @Override
    public void onResume() {
        super.onResume();

        getStores();
        getAds();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void getStores() {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getstores")
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
                                stores.clear();
                                JSONArray jsonArray = response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Store store = new Store();
                                    store.setId(object.getInt("id"));
                                    store.setUserId(object.getInt("member_id"));
                                    store.setName(object.getString("name"));
                                    store.setPhoneNumber(object.getString("phone_number"));
                                    store.setAddress(object.getString("address"));
                                    store.setRatings(Float.parseFloat(object.getString("ratings")));
                                    store.setReviews(object.getInt("reviews"));
                                    store.setLogoUrl(object.getString("logo_url"));
                                    store.set_registered_time(object.getString("registered_time"));
                                    store.set_status(object.getString("status"));
                                    double lat = 0.0d, lng = 0.0d;
                                    if(object.getString("latitude").length() > 0){
                                        lat = Double.parseDouble(object.getString("latitude"));
                                        lng = Double.parseDouble(object.getString("longitude"));
                                    }
                                    store.setLatLng(new LatLng(lat, lng));

                                    stores.add(store);
                                }

                                if(stores.isEmpty()){
                                    ((FrameLayout)view.findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                                    ((TextView)view.findViewById(R.id.lb1)).setVisibility(View.GONE);
                                }
                                else {
                                    ((FrameLayout)view.findViewById(R.id.no_result)).setVisibility(View.GONE);
                                    ((TextView)view.findViewById(R.id.lb1)).setVisibility(View.VISIBLE);
                                    ((TextView)view.findViewById(R.id.lb1)).setText(String.valueOf(stores.size()) + " items found");
                                }

                                if(getActivity() != null){
                                    adapter = new StoreListAdapter(getActivity());
                                    adapter.setDatas(stores);
                                    list.setAdapter(adapter);
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

    private void getAds() {
        AndroidNetworking.post(ReqConst.SERVER_URL + "getadvertisements")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){

                                JSONArray jsonArray = response.getJSONArray("data");
                                for(int i=0;i<jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Advertisement advertisement = new Advertisement();
                                    advertisement.setId(object.getInt("id"));
                                    advertisement.setStore_id(object.getInt("store_id"));
                                    advertisement.setStore_name(object.getString("store_name"));
                                    advertisement.setPicture_url(object.getString("picture_url"));

                                    advertisements.add(advertisement);
                                }

                                if(advertisements.isEmpty()){
                                    for(Integer adPic: ads){
                                        Advertisement advertisement = new Advertisement();
                                        advertisement.setPicture_res(adPic);
                                        advertisement.setStore_id(0);
                                        advertisement.setStore_name("");
                                        advertisements.add(advertisement);
                                    }
                                }

                                if(getActivity() != null){
                                    adListAdapter = new AdListAdapter(getActivity());
                                    adListAdapter.setDatas(advertisements);
                                    pager.setAdapter(adListAdapter);
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

                        if(advertisements.isEmpty()){
                            for(Integer adPic: ads){
                                Advertisement advertisement = new Advertisement();
                                advertisement.setPicture_res(adPic);
                                advertisement.setStore_id(0);
                                advertisement.setStore_name("");
                                advertisements.add(advertisement);
                            }
                        }

                        adListAdapter.setDatas(advertisements);
                        pager.setAdapter(adListAdapter);
                    }
                });
    }

}

































