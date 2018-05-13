package com.example.android.annasbakery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.annasbakery.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListFragment extends Fragment {

    private RecipeListAdapter mAdapter;
    private int mGridNumber = 2;
    @BindView(R.id.recipe_list_rv) RecyclerView mRecyclerView;

    // Public constructor
    public RecipeListFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate a root View from the fragment's layout file
        View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        ButterKnife.bind(this, rootView);

        // Set the LayoutManager and the Adapter for the RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), mGridNumber, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    /*
     * Set the adapter to the RecyclerView
     * @param adapter: the adapter to use
     */
    public void setAdapter(RecipeListAdapter adapter){
        mAdapter = adapter;
    }

    /*
     * Set the grid number for GridView
     * @param numberOfGrids: the number of grids
     */
    public void setGridNumber(int numberOfGrids){
        mGridNumber = numberOfGrids;
    }
}
