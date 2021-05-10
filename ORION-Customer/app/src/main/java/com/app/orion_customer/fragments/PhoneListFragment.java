package com.app.orion_customer.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_customer.R;
import com.app.orion_customer.adapters.PhoneListAdapter;
import com.app.orion_customer.base.BaseActivity;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.commons.ReqConst;
import com.app.orion_customer.models.Phone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class PhoneListFragment extends Fragment {
    private int mPage;

    ArrayList<Phone> phones = new ArrayList<>();
    PhoneListAdapter phoneListAdapter = null;
    FrameLayout progressBar;
    ListView listView;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_phone, viewGroup, false);

        Commons.phoneListFragment = this;

        progressBar = (FrameLayout)view.findViewById(R.id.loading_bar);
        listView = (ListView)view.findViewById(R.id.list);

        refreshPhone();

        setupUI(view, getActivity());

        return view;
    }

    public static PhoneListFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("PhoneListFragement Page", page);
        PhoneListFragment fragment = new PhoneListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt("PhoneListFragement");
        Log.d("Pager NO", String.valueOf(mPage));
    }

    public void getPhones() {
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
                                refreshPhone();
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

    private void refreshPhone(){
        if(Commons.phones.isEmpty()){
            ((FrameLayout)view.findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
        }
        else {
            ((FrameLayout)view.findViewById(R.id.no_result)).setVisibility(View.GONE);
        }

        if(getActivity() != null){
            phoneListAdapter = new PhoneListAdapter(getActivity());
            phoneListAdapter.setDatas(Commons.phones);
            listView.setAdapter(phoneListAdapter);
        }
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

    public void deletePhoneNumber(Phone phone) {

        ((BaseActivity) getActivity()).showAlertDialogForQuestion("Warning!", "Are you sure you want to delete this phone number?", getActivity(), null, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                progressBar.setVisibility(View.VISIBLE);
                AndroidNetworking.post(ReqConst.SERVER_URL + "delPhone")
                        .addBodyParameter("phone_id", String.valueOf(phone.getId()))
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
                                        int index = Commons.phones.indexOf(phone);
                                        Commons.phones.remove(index);
                                        refreshPhone();
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
                return null;
            }
        });

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




























