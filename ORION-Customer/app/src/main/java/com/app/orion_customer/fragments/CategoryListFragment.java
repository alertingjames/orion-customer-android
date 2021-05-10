package com.app.orion_customer.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.app.orion_customer.R;
import com.app.orion_customer.adapters.CategoryListAdapter;
import com.app.orion_customer.commons.Commons;
import com.app.orion_customer.commons.Constants;
import com.app.orion_customer.models.Category;

import java.util.ArrayList;
import java.util.Collections;

public class CategoryListFragment extends Fragment {
    private int mPage;
    ListView listView;
    ArrayList<Category> categories = new ArrayList<>();
    CategoryListAdapter adapter = null;
    int[] categoryPictures = {
            R.drawable.new_in, R.drawable.accessories, R.drawable.activewear, R.drawable.babyproducts, R.drawable.bag2, R.drawable.clothing,
            R.drawable.foodwear, R.drawable.perfumes, R.drawable.shoes, R.drawable.stationery, R.drawable.sports, R.drawable.toiletry, R.drawable.toys,
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_categories, viewGroup, false);

        Commons.categoryListFragment = this;

        listView = (ListView)view.findViewById(R.id.list);

        for(int i=0;i<categoryPictures.length;i++){
            Category category = new Category();
            category.setTitle(Constants.categoryItems[i]);
            category.setResource(categoryPictures[i]);
            categories.add(category);
        }

//        Collections.sort(categories);

        if(getActivity() != null){
            adapter = new CategoryListAdapter(getActivity());
            adapter.setDatas(categories);
            listView.setAdapter(adapter);
        }

        return view;
    }

    public static CategoryListFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("MainHomeFragement Page", page);
        CategoryListFragment fragment = new CategoryListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt("MainHomeFragement CategoryListFragment");
        Log.d("Pager NO", String.valueOf(mPage));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Commons.categoryListFragment = null;
    }
}




























