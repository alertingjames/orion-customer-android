package com.app.orion_customer.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_customer.R;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.commons.ReqConst;
import com.app.orion_customer.main.AddressPhoneListActivity;
import com.app.orion_customer.main.LocationTrackingActivity;
import com.app.orion_customer.main.LoginActivity;
import com.app.orion_customer.models.Address;
import com.app.orion_customer.models.Phone;
import com.google.android.gms.maps.model.LatLng;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CustomerSettingsFragment extends Fragment {

    public Typeface bold, normal;
    ImageView backButton;
    TextView titleBox, label, label1, label2, label3;
    TextView phoneBox, addressBox;
    Switch notiSwitchButton;
    Switch myLocationSwitchButton, driverLocationSwitchButton, mapViewSwitchButton;

    FrameLayout progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_customer_settings, viewGroup, false);

        progressBar = (FrameLayout) view.findViewById(R.id.loading_bar);

        label = (TextView)view.findViewById(R.id.lb);
        label1 = (TextView)view.findViewById(R.id.lb1);
        label2 = (TextView)view.findViewById(R.id.lb2);
        label3 = (TextView)view.findViewById(R.id.lb3);

        backButton = (ImageView)view.findViewById(R.id.btn_back);
        titleBox = (TextView)view.findViewById(R.id.titleBox);

        phoneBox = (TextView)view.findViewById(R.id.phone);
        addressBox = (TextView)view.findViewById(R.id.address);

        bold = Typeface.createFromAsset(getActivity().getAssets(), "futura medium bt.ttf");
        normal = Typeface.createFromAsset(getActivity().getAssets(), "futura book font.ttf");

        label.setTypeface(bold);
        label1.setTypeface(bold);
        label2.setTypeface(bold);
        label3.setTypeface(bold);

        myLocationSwitchButton = (Switch)view.findViewById(R.id.locationSetting);
        driverLocationSwitchButton = (Switch)view.findViewById(R.id.driverLocationSetting);
        mapViewSwitchButton = (Switch) view.findViewById(R.id.mapviewSetting);

        if(Commons.curMapTypeIndex == 2)mapViewSwitchButton.setChecked(true);
        else mapViewSwitchButton.setChecked(false);

        if(Commons.mapCameraMoveF)myLocationSwitchButton.setChecked(true);
        else myLocationSwitchButton.setChecked(false);

        if(Commons.driverMapCameraMoveF)driverLocationSwitchButton.setChecked(true);
        else driverLocationSwitchButton.setChecked(false);

        notiSwitchButton = (Switch)view.findViewById(R.id.notification);
        notiSwitchButton.setTypeface(normal);
        notiSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //
                }else {
                    //
                }
            }
        });

        myLocationSwitchButton.setTypeface(normal);
        myLocationSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Commons.mapCameraMoveF = true;
                    if(Commons.driverMapCameraMoveF) {
                        Commons.driverMapCameraMoveF = false;
                        driverLocationSwitchButton.setChecked(false);
                    }
                }else {
                    Commons.mapCameraMoveF = false;
                }
            }
        });

        driverLocationSwitchButton.setTypeface(normal);
        driverLocationSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Commons.driverMapCameraMoveF = true;
                    if(Commons.mapCameraMoveF) {
                        Commons.mapCameraMoveF = false;
                        myLocationSwitchButton.setChecked(false);
                    }
                }else {
                    Commons.driverMapCameraMoveF = false;
                }
            }
        });

        mapViewSwitchButton.setTypeface(normal);
        mapViewSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Commons.curMapTypeIndex = 2;
                }else {
                    Commons.curMapTypeIndex = 1;
                }
                Commons.googleMap.setMapType(LocationTrackingActivity.MAP_TYPES[Commons.curMapTypeIndex]);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        titleBox.setTypeface(bold);
        phoneBox.setTypeface(bold);
        addressBox.setTypeface(normal);

        phoneBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(progressBar.getVisibility() == View.VISIBLE)return;
                if(Commons.thisUser != null){
                    Intent intent = new Intent(getActivity(), AddressPhoneListActivity.class);
                    intent.putExtra("option", "phone");
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        addressBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(progressBar.getVisibility() == View.VISIBLE)return;
                if(Commons.thisUser != null){
                    Intent intent = new Intent(getActivity(), AddressPhoneListActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
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

    private void getPhones() {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getPhones")
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
                                Commons.phones.clear();
                                JSONArray dataArr = response.getJSONArray("data");
                                for(int i=0; i<dataArr.length(); i++) {
                                    JSONObject object = (JSONObject) dataArr.get(i);
                                    Phone phone = new Phone();
                                    phone.setId(object.getInt("id"));
                                    phone.setUserId(object.getInt("member_id"));
                                    phone.setPhone_number(object.getString("phone_number"));
                                    phone.setStatus(object.getString("status"));

                                    Commons.phones.add(phone);
                                }

                                if(Commons.phones.size() > 0)phoneBox.setText("Phone Numbers (" + String.valueOf(Commons.phones.size()) + ")");

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
                        progressBar.setVisibility(View.GONE);
                        showToast(error.getErrorDetail());
                    }
                });
    }

    private void getAddresses() {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getAddresses")
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
                                Commons.addresses.clear();
                                JSONArray dataArr = response.getJSONArray("data");
                                for(int i=0; i<dataArr.length(); i++) {
                                    JSONObject object = (JSONObject) dataArr.get(i);
                                    Address address = new Address();
                                    address.setId(object.getInt("id"));
                                    address.setUserId(object.getInt("member_id"));
                                    address.setAddress(object.getString("address"));
                                    address.setArea(object.getString("area"));
                                    address.setStreet(object.getString("street"));
                                    address.setHouse(object.getString("house"));
                                    address.setStatus(object.getString("status"));
                                    double lat = 0.0d, lng = 0.0d;
                                    if(object.getString("latitude").length() > 0){
                                        lat = Double.parseDouble(object.getString("latitude"));
                                        lng = Double.parseDouble(object.getString("longitude"));
                                    }
                                    address.setLatLng(new LatLng(lat, lng));

                                    Commons.addresses.add(address);
                                }

                                if(Commons.addresses.size() > 0)addressBox.setText("Addresses (" + String.valueOf(Commons.addresses.size()) + ")");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        if(Commons.thisUser != null){
            getPhones();
            getAddresses();
        }

    }



}




















