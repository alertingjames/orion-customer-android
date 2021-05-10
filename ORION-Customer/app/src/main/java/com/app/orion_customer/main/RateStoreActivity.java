package com.app.orion_customer.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.orion_customer.R;
import com.app.orion_customer.adapters.RatingListAdapter;
import com.app.orion_customer.base.BaseActivity;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.commons.ReqConst;
import com.app.orion_customer.models.Rating;
import com.app.orion_customer.models.User;
import com.google.android.gms.maps.model.LatLng;
import com.iamhabib.easy_preference.EasyPreference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RateStoreActivity extends BaseActivity {

    RoundedImageView storeLogoBox;
    FrameLayout progressBar;
    RatingBar ratingBar;
    TextView ratingBox;
    EditText subjectBox, feedbackBox;
    TextView title, label, label1, label2, label3;

    ListView ratingsList;
    ArrayList<Rating> ratings = new ArrayList<>();
    RatingListAdapter ratingListAdapter = new RatingListAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_store);

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(bold);
        title.setText(Commons.store.getName());

        label = (TextView) findViewById(R.id.lb);
        label1 = (TextView) findViewById(R.id.lb1);
        label.setTypeface(normal);
        label1.setTypeface(normal);

        label2 = (TextView) findViewById(R.id.lb3);
        label3 = (TextView) findViewById(R.id.lb4);
        label2.setTypeface(normal);
        label3.setTypeface(normal);

        progressBar = (FrameLayout)findViewById(R.id.loading_bar);
        storeLogoBox = (RoundedImageView) findViewById(R.id.store_logo);

        ratingBar = (RatingBar)findViewById(R.id.ratingbar);
        ratingBox = (TextView)findViewById(R.id.ratingBox);
        subjectBox = (EditText)findViewById(R.id.subjectBox);
        feedbackBox = (EditText)findViewById(R.id.feedbackBox);

        subjectBox.setTypeface(normal);
        feedbackBox.setTypeface(normal);

        ratingsList = (ListView) findViewById(R.id.ratingsList);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rt, boolean fromUser) {
                Float ratingvalue = (Float) ratingBar.getRating();
                ratingBox.setText(String.valueOf(ratingvalue));
            }
        });

        Picasso.with(getApplicationContext()).load(Commons.store.getLogoUrl()).into(storeLogoBox);

        ((RatingBar)findViewById(R.id.ratingbar_small)).setRating(Commons.store.getRatings());
        ((TextView)findViewById(R.id.ratings)).setText(String.valueOf(Commons.store.getRatings()));
        ((TextView)findViewById(R.id.reviews)).setText(String.valueOf(Commons.store.getReviews()));

        getStoreRatings();

        setupUI(findViewById(R.id.activity), this);

    }

    public void back(View view){
        onBackPressed();
    }

    public void submitFeedback(View view){
        if(subjectBox.getText().toString().trim().length() == 0){
            showToast("Enter subject.");
            return;
        }

        if(ratingBar.getRating() == 0){
            showToast("Please rate out of 5 stars");
            return;
        }

        if(feedbackBox.getText().toString().trim().length() == 0){
            showToast("Write your feedback.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "placeStoreFeedback")
                .addBodyParameter("member_id", String.valueOf(Commons.thisUser.get_idx()))
                .addBodyParameter("store_id", String.valueOf(Commons.store.getId()))
                .addBodyParameter("subject", subjectBox.getText().toString().trim())
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
                                ((RatingBar)findViewById(R.id.ratingbar_small)).setRating(Float.parseFloat(response.getString("ratings")));
                                ((TextView)findViewById(R.id.ratings)).setText(response.getString("ratings"));
                                ((TextView)findViewById(R.id.reviews)).setText(response.getString("reviews"));
                                Commons.store.setRatings(Float.parseFloat(response.getString("ratings")));
                                Commons.store.setReviews(Integer.parseInt(response.getString("reviews")));

                                getStoreRatings();

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

    private void getStoreRatings(){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getStoreRatings")
                .addBodyParameter("store_id", String.valueOf(Commons.store.getId()))
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
                                ratings.clear();
                                JSONArray dataArr = response.getJSONArray("data");
                                for(int i=0; i<dataArr.length(); i++) {
                                    JSONObject object = (JSONObject) dataArr.get(i);
                                    Rating rating = new Rating();
                                    rating.setIdx(object.getInt("id"));
                                    rating.setStoreId(object.getInt("store_id"));
                                    rating.setUserId(object.getInt("member_id"));
                                    rating.setUserName(object.getString("member_name"));
                                    rating.setUserPictureUrl(object.getString("member_photo"));
                                    rating.setRating(Float.parseFloat(object.getString("rating")));
                                    rating.setSubject(object.getString("subject"));
                                    rating.setDescription(object.getString("description"));
                                    rating.setDate(object.getString("date_time"));

                                    ratings.add(rating);

                                    if(rating.getStoreId() == Commons.store.getId() && rating.getUserId() == Commons.thisUser.get_idx()){
                                        subjectBox.setText(rating.getSubject());
                                        ratingBar.setRating(rating.getRating());
                                        ratingBox.setText(String.valueOf(rating.getRating()));
                                        feedbackBox.setText(rating.getDescription());
                                        ((TextView)findViewById(R.id.lb)).setText("Update your feedback");
                                    }

                                    else  if(i == dataArr.length() - 1){
                                        ((TextView)findViewById(R.id.lb)).setText("Please place your feedback for this store");
                                    }
                                }

                                if(ratings.isEmpty()){
                                    ((FrameLayout)findViewById(R.id.no_result_ratings)).setVisibility(View.VISIBLE);
                                }else {
                                    ((FrameLayout)findViewById(R.id.no_result_ratings)).setVisibility(View.GONE);
                                }
                                ratingListAdapter.setDatas(ratings);
                                ratingsList.setAdapter(ratingListAdapter);

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

}






































