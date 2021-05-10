package com.app.orion_customer.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_customer.R;
import com.app.orion_customer.adapters.StoreListAdapter;
import com.app.orion_customer.classes.CustomGridView;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.commons.ReqConst;
import com.app.orion_customer.models.Store;
import com.google.android.gms.maps.model.LatLng;
import com.rd.PageIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class StoreListFragment extends Fragment {

    GridView list;
    public FrameLayout progressBar;
    ImageView backButton;
    TextView categoryBox, label;

    ArrayList<Store> stores = new ArrayList<>();
    StoreListAdapter adapter = null;

    View view;
    Typeface bold, normal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_stores, viewGroup, false);

        progressBar = (FrameLayout) view.findViewById(R.id.loading_bar);
        backButton = (ImageView) view.findViewById(R.id.btn_back);

        list = (GridView) view.findViewById(R.id.list);

        bold = Typeface.createFromAsset(getActivity().getAssets(), "futura medium bt.ttf");
        normal = Typeface.createFromAsset(getActivity().getAssets(), "futura book font.ttf");

        categoryBox = (TextView)view.findViewById(R.id.categoryBox);
        label = (TextView)view.findViewById(R.id.lb);

        categoryBox.setTypeface(bold);
        label.setTypeface(normal);

        categoryBox.setText(Commons.selectedCategory);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getStores();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void getStores() {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getcstores")
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

                                    JSONArray genderArray = object.getJSONArray("genders");
                                    ArrayList<String> genders = new ArrayList<>();
                                    for(int j=0;j<genderArray.length(); j++) {
                                        String genderStr = genderArray.getString(j);
                                        genders.add(genderStr);
                                    }
                                    store.setGenderList(genders);

                                    JSONArray categoryArray = object.getJSONArray("categories");
                                    ArrayList<String> categories = new ArrayList<>();
                                    for(int j=0;j<categoryArray.length(); j++) {
                                        String categoryStr = categoryArray.getString(j);
                                        categories.add(categoryStr);
                                    }
                                    store.setCategoryList(categories);

                                    JSONArray subcategoryArray = object.getJSONArray("subcategories");
                                    ArrayList<String> subcategories = new ArrayList<>();
                                    for(int j=0;j<subcategoryArray.length(); j++) {
                                        String subcategoryStr = subcategoryArray.getString(j);
                                        subcategories.add(subcategoryStr);
                                    }
                                    store.setSubcategoryList(subcategories);

                                    if(store.getCategoryList().contains(Commons.selectedCategory)){
                                        stores.add(store);
                                    }
                                }

                                Collections.sort(stores);

                                if(stores.isEmpty())((FrameLayout)view.findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                                else ((FrameLayout)view.findViewById(R.id.no_result)).setVisibility(View.GONE);
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

}




































