package com.app.orion_customer.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.orion_customer.R;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.fragments.StoreListFragment;
import com.app.orion_customer.models.Category;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Category> _datas = new ArrayList<>();
    private ArrayList<Category> _alldatas = new ArrayList<>();

    public CategoryListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Category> datas) {

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
            convertView = inflater.inflate(R.layout.item_category_list, parent, false);

            holder.picture = (ImageView) convertView.findViewById(R.id.pictureBox);
            holder.name = (TextView) convertView.findViewById(R.id.titleBox);
            holder.newinMark = (ImageView) convertView.findViewById(R.id.newin_mark);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Category entity = (Category) _datas.get(position);

        holder.name.setText(entity.getTitle());
        Typeface bold = Typeface.createFromAsset(_context.getAssets(), "futura medium bt.ttf");
        holder.name.setTypeface(bold);
        if(position == 0)holder.newinMark.setVisibility(View.VISIBLE);
        else holder.newinMark.setVisibility(View.GONE);

        if(entity.getResource() > 0){
            Picasso.with(_context)
                    .load(entity.getResource())
                    .into(holder.picture);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.selectedCategory = entity.getTitle();
                if(Commons.categoryListFragment != null){
                    Fragment fragment = new StoreListFragment();
                    FragmentManager manager = Commons.categoryListFragment.getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.addToBackStack(null).commit();
                }
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
            for (Category category : _alldatas){

                if (category instanceof Category) {

                    String value = category.getTitle().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(category);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        ImageView picture, newinMark;
        TextView name;
    }
}












