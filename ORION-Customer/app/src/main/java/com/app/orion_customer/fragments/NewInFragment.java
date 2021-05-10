package com.app.orion_customer.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.app.orion_customer.R;

public class NewInFragment extends Fragment {
    private int mPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_newin, viewGroup, false);



        return view;
    }

    public static NewInFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("MainHomeFragement Page", page);
        NewInFragment fragment = new NewInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt("MainHomeFragement NewInFragment");
        Log.d("Pager NO", String.valueOf(mPage));
    }

}