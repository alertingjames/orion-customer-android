package com.app.orion_customer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.app.orion_customer.R;
import com.app.orion_customer.models.Rating;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class RatingListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Rating> _datas = new ArrayList<>();
    private ArrayList<Rating> _alldatas = new ArrayList<>();

    public RatingListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Rating> datas) {

        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

    @Override
    public int getCount(){
        return _datas.size();
    }

    @Override
    public Object getItem(int position){
        return _datas.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.item_ratings_list, parent, false);

            holder.picture = (CircleImageView) convertView.findViewById(R.id.picture);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.subject = (TextView) convertView.findViewById(R.id.subject);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.ratings = (TextView) convertView.findViewById(R.id.ratings);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.ratingBar = (RatingBar)convertView.findViewById(R.id.ratingbar);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Rating entity = (Rating) _datas.get(position);

        Typeface bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        Typeface normal = Typeface.createFromAsset(_context.getAssets(), "futura book font.ttf");
        holder.name.setText(entity.getUserName());
        holder.name.setTypeface(bold);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String myDate = dateFormat.format(new Date(Long.parseLong(entity.getDate())));
        holder.date.setText(myDate);
        holder.ratingBar.setRating(entity.getRating());
        holder.ratings.setText(String.valueOf(entity.getRating()));

        holder.subject.setText(entity.getSubject());
        holder.description.setText(entity.getDescription());
        holder.subject.setTypeface(normal);
        holder.description.setTypeface(normal);
        Glide.with(_context)
                .load(entity.getUserPictureUrl())
                .into(holder.picture);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }

    public void filter(String charText){

        charText = charText.toLowerCase();
        _datas.clear();

        if(charText.length() == 0){
            _datas.addAll(_alldatas);
        }else {
            for (Rating rating : _alldatas){

                if (rating instanceof Rating) {

                    String value = ((Rating) rating).getUserName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(rating);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        CircleImageView picture;
        TextView name, subject, description, ratings, date;
        RatingBar ratingBar;
        ProgressBar progressBar;
    }
}

