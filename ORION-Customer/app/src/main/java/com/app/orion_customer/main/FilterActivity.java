package com.app.orion_customer.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.orion_customer.R;
import com.app.orion_customer.base.BaseActivity;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.commons.Constants;

import io.apptik.widget.MultiSlider;

public class FilterActivity extends BaseActivity {

    MultiSlider multiSlider;
    TextView minVal, maxVal;
    TextView priceUp, priceDown, nameAsc, nameDesc;
    int minPrice = 0;
    int maxPrice = 0;
    int priceSort = 0;
    int nameSort = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        ((TextView)findViewById(R.id.title)).setTypeface(bold);
        ((TextView)findViewById(R.id.priceRangeCaption)).setTypeface(normal);
        ((TextView)findViewById(R.id.sortCaption)).setTypeface(normal);
        ((TextView)findViewById(R.id.saveButton)).setTypeface(bold);
        ((TextView)findViewById(R.id.unSaveButton)).setTypeface(bold);

        priceUp = (TextView)findViewById(R.id.price_up);
        priceDown = (TextView)findViewById(R.id.price_down);
        nameAsc = (TextView)findViewById(R.id.name_ascending);
        nameDesc = (TextView)findViewById(R.id.name_descending);

        multiSlider = (MultiSlider) findViewById(R.id.range_slider);
        multiSlider.setMin(0);
        multiSlider.setMax(1000);

        minVal = (TextView)findViewById(R.id.minVal);
        maxVal = (TextView)findViewById(R.id.maxVal);

        minPrice = Commons.minPriceVal;
        maxPrice = Commons.maxPriceVal > 1000? 1000:Commons.maxPriceVal;

        minVal.setText(numberCalculation(minPrice));
        maxVal.setText(numberCalculation(maxPrice));

        multiSlider.getThumb(0).setValue(minPrice);
        multiSlider.getThumb(1).setValue(maxPrice);

        multiSlider.setOnThumbValueChangeListener(new MultiSlider.SimpleChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int
                    thumbIndex, int value) {
                if (thumbIndex == 0) {
                    minVal.setText(numberCalculation(value));
                    minPrice = value;
                } else {
                    maxVal.setText(numberCalculation(value));
                    maxPrice = value;
                }
            }
        });

        if(Commons.priceSort == 0)resetPriceButtons();
        else if(Commons.priceSort == 1)priceUp();
        else if(Commons.priceSort == 2)priceDown();

        if(Commons.nameSort == 0)resetNameButtons();
        else if(Commons.nameSort == 1)nameAsc();
        else if(Commons.nameSort == 2)nameDesc();

    }

    private String numberCalculation(long number) {
        if (number < 1000)
            return "" + number;
        int exp = (int) (Math.log(number) / Math.log(1000));
        return String.format("%.1f %c", number / Math.pow(1000, exp), "kMGTPE".charAt(exp-1));
    }

    private void resetPriceButtons(){
        priceSort = 0;
        priceUp.setBackgroundResource(R.drawable.green_round_stroke);
        priceUp.setTextColor(getColor(R.color.colorPrimary));
        priceUp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow, 0);
        priceDown.setBackgroundResource(R.drawable.green_round_stroke);
        priceDown.setTextColor(getColor(R.color.colorPrimary));
        priceDown.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow, 0);
    }

    private void resetNameButtons(){
        nameSort = 0;
        nameAsc.setBackgroundResource(R.drawable.green_round_stroke);
        nameAsc.setTextColor(getColor(R.color.colorPrimary));
        nameAsc.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow, 0);
        nameDesc.setBackgroundResource(R.drawable.green_round_stroke);
        nameDesc.setTextColor(getColor(R.color.colorPrimary));
        nameDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow, 0);
    }

    public void priceUp(View view){
        priceUp();
    }

    private void priceUp(){
        resetPriceButtons();
        priceUp.setBackgroundResource(R.drawable.green_round_fill);
        priceUp.setTextColor(Color.WHITE);
        priceUp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow_white, 0);
        priceSort = 1;
    }

    public void priceDown(View view){
        priceDown();
    }

    private void priceDown(){
        resetPriceButtons();
        priceDown.setBackgroundResource(R.drawable.green_round_fill);
        priceDown.setTextColor(Color.WHITE);
        priceDown.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_white, 0);
        priceSort = 2;
    }

    public void nameAsc(View view){
        nameAsc();
    }

    private void nameAsc(){
        resetNameButtons();
        nameAsc.setBackgroundResource(R.drawable.green_round_fill);
        nameAsc.setTextColor(Color.WHITE);
        nameAsc.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow_white, 0);
        nameSort = 1;
    }

    public void nameDesc(View view){
        nameDesc();
    }

    private void nameDesc(){
        resetNameButtons();
        nameDesc.setBackgroundResource(R.drawable.green_round_fill);
        nameDesc.setTextColor(Color.WHITE);
        nameDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_white, 0);
        nameSort = 2;
    }

    public void saveFilter(View view){
        Commons.minPriceVal = minPrice;
        Commons.maxPriceVal = maxPrice;
        Commons.priceSort = priceSort;
        Commons.nameSort = nameSort;
        showToast("Filter applied");
        finish();
    }

    public void unSaveFilter(View view){
        Commons.minPriceVal = 0;
        Commons.maxPriceVal = Constants.MAX_PRODUCT_PRICE;
        Commons.priceSort = 0;
        Commons.nameSort = 0;
        showToast("Filter canceled");
        finish();
    }

    public void back(View view){
        onBackPressed();
    }
}






























