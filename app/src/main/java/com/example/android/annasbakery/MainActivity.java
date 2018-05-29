package com.example.android.annasbakery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.annasbakery.data.Recipe;
import com.example.android.annasbakery.data.Step;
import com.example.android.annasbakery.network.ApiFactory;
import com.example.android.annasbakery.ui.RecipeDetailFragment;
import com.example.android.annasbakery.ui.RecipeListAdapter;
import com.example.android.annasbakery.ui.RecipeListFragment;
import com.example.android.annasbakery.ui.StepAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// This activity displays a list of recipes and (if we are on tablet width) also the selected recipe's details
public class MainActivity extends AppCompatActivity
        implements RecipeListAdapter.RecipeListItemClickHandler,
        StepAdapter.StepClickHandler {

    public static final String DETAIL_KEY = "detail";
    public static final String DETAIL_POSITION_KEY = "detail_position";
    public static final String STEP_KEY = "step";
    public static final String TWO_PANE_KEY = "two_pane";
    private boolean mHasTwoPane = false;
    private ArrayList<Recipe> mRecipes;
    private Recipe mRecipe;

    @BindView(R.id.recipe_list_container)
    FrameLayout mListContainer;
    @BindView(R.id.recipe_list_empty)
    TextView mEmptyTextView;
    @BindView(R.id.recipe_list_progress_bar)
    ProgressBar mProgressBar;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @NonNull
    @VisibleForTesting
    public SimpleIdlingResource getIdlingInstance() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Do the Butterknife binding
        ButterKnife.bind(this);

        // If the screen has the detail layout element (only above 600dp screen width), we have two panes
        if (findViewById(R.id.recipe_detail_layout) != null) mHasTwoPane = true;

        // Show the ProgressBar until we try to load the data
        showProgressBar();

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(DETAIL_KEY)){
            mRecipe = extras.getParcelable(DETAIL_KEY);
        }

        // Check if the device is connected to the internet
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // If the device is connected to the internet, load the recipes, otherwise show the empty text
        if (isConnected) {
            loadRecipes();
        } else {
            showEmptyView();
        }
        getIdlingInstance();
    }

    /*
     * When starting, repeat the behaviour in onCreate to check the connection again
     */
    @Override
    protected void onStart() {
        super.onStart();

        showProgressBar();

        // Check if the device is connected to the internet
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // If the device is connected to the internet, load the recipes
        if (isConnected) {
            loadRecipes();
        } else {
            showEmptyView();
        }
    }

    /*
     * Initialize the fragments
     */
    private void initFragments() {

        // Create a new RecipeListAdapter and a new RecipeListFragment instances and set the recipe data
        RecipeListAdapter adapter = new RecipeListAdapter(this, this);
        RecipeListFragment listFragment = new RecipeListFragment();
        adapter.setRecipeData(mRecipes);
        if (mHasTwoPane && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            listFragment.setGridNumber(1);
        }
        listFragment.setAdapter(adapter);
        getSupportFragmentManager().beginTransaction().replace(R.id.recipe_list_container, listFragment).commitAllowingStateLoss();

        //If we have two panes, create a new RecipeDetailFragment with the first recipe, too
        if (mHasTwoPane) {

            RecipeDetailFragment detailFragment = new RecipeDetailFragment();
            StepAdapter stepAdapter = new StepAdapter(this, this);
            mRecipe = mRecipes.get(0);
            detailFragment.setRecipe(mRecipe);
            stepAdapter.setData(mRecipe.getSteps());
            detailFragment.setStepAdapter(stepAdapter);
            getSupportFragmentManager().beginTransaction().replace(R.id.recipe_detail_container, detailFragment).commitAllowingStateLoss();

        }

    }

    // Try to make an API call to get the recipe data
    private void loadRecipes() {

        // Create a call with the API Factory and implement its callbacks
        Call<ArrayList<Recipe>> call = ApiFactory.getRecipes();

        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Recipe>> call, @NonNull Response<ArrayList<Recipe>> response) {

                // On success, set the response body as the recipe list, show it and initialize the fragments
                mRecipes = response.body();
                showList();
                initFragments();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable t) {

                // On failure, show the empty text
                showEmptyView();
            }
        });
    }

    @Override
    public void onRecipeClickHandler(Recipe recipe, int position) {

        mRecipe = recipe;

        //No matter if we have one or two panes, save the clicked recipe's position into a preference and notify the widget
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(MainActivity.DETAIL_POSITION_KEY, position);
        editor.apply();
        RecipeWidgetProvider.sendUpdateBroadcast(this);

        // If we have two panes, replace the actual RecipeDetailFragment with the clicked one
        if (mHasTwoPane) {

            RecipeDetailFragment detailFragment = new RecipeDetailFragment();
            StepAdapter stepAdapter = new StepAdapter(this, this);
            detailFragment.setRecipe(mRecipe);
            stepAdapter.setData(mRecipe.getSteps());
            detailFragment.setStepAdapter(stepAdapter);
            getSupportFragmentManager().beginTransaction().replace(R.id.recipe_detail_container, detailFragment).commit();

        } else {

            //If we have only one pane, launch the DetailActivity with the clicked recipe as extra
            Intent detailLaunchIntent = new Intent(MainActivity.this, DetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(DETAIL_KEY, recipe);
            detailLaunchIntent.putExtras(bundle);
            startActivity(detailLaunchIntent);

        }
    }

    // Handle the clicks on the step items
    @Override
    public void onStepClickHandler(Step step) {
        // Launch the StepActivity on step click
        Intent stepLaunchIntent = new Intent(MainActivity.this, StepActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(STEP_KEY, step);
        bundle.putParcelable(DETAIL_KEY, mRecipe); // Pass also the Recipe object for later use
        if (mHasTwoPane) {
            bundle.putBoolean(TWO_PANE_KEY, mHasTwoPane);
        }
        stepLaunchIntent.putExtras(bundle);
        startActivity(stepLaunchIntent);
    }

    // Show the ProgressBar, hide the list container and the empty view
    private void showProgressBar() {
        mEmptyTextView.setVisibility(View.GONE);
        mListContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    // Show the empty text, hide the list container and the ProgressBar
    private void showEmptyView() {
        mListContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mEmptyTextView.setVisibility(View.VISIBLE);
    }

    // Show the list container, hide the ProgressBar and the empty view
    private void showList() {
        mProgressBar.setVisibility(View.GONE);
        mEmptyTextView.setVisibility(View.GONE);
        mListContainer.setVisibility(View.VISIBLE);
    }
}
