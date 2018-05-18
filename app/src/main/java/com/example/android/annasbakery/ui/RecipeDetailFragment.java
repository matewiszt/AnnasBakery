package com.example.android.annasbakery.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.annasbakery.MainActivity;
import com.example.android.annasbakery.R;
import com.example.android.annasbakery.RecipeUtils;
import com.example.android.annasbakery.data.Recipe;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailFragment extends Fragment {

    private Recipe mRecipe;
    private StepAdapter mStepAdapter;

    // Bind the views
    @BindView(R.id.recipe_detail_name_tv) TextView mNameTextView;
    @BindView(R.id.recipe_detail_iv) ImageView mImageView;
    @BindView(R.id.recipe_detail_servings_tv) TextView mServingsTextView;
    @BindView(R.id.recipe_ingredients_rv)
    RecyclerView mIngredientsRecyclerView;
    @BindView(R.id.recipe_steps_rv)
    RecyclerView mStepsRecyclerView;

    // Public constructor
    public RecipeDetailFragment() {}

    // Save the recipe for rotating
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.DETAIL_KEY, mRecipe);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Restore mRecipe after state restore
        if(savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(MainActivity.DETAIL_KEY);
        }
        Context context = container.getContext();

        // Create the rootView and bind the data
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, rootView);

        // Get the name and set it
        final String name = mRecipe.getName();
        String imagePath = mRecipe.getImagePath();
        mNameTextView.setText(name);
        mImageView.setContentDescription(name);

        // If there is an image, load it into mImageView, if no or the load failed, load the corresponding stock image
        if (!TextUtils.isEmpty(imagePath)) {
            Picasso.with(context).load(imagePath)
                    .into(mImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            mImageView.setImageResource(RecipeUtils.findCorrectStockImage(name));
                        }
                    });
        } else {
            mImageView.setImageResource(RecipeUtils.findCorrectStockImage(name));
        }

        // Get the servings, format the text and set it
        int servings = mRecipe.getServings();
        mServingsTextView.setText(String.format(getString(R.string.recipe_detail_servings_text), servings));

        // Instantiate an IngredientAdapter and set the ingredients to the RecyclerView
        IngredientAdapter ingredientAdapter = new IngredientAdapter(context);
        ingredientAdapter.setData(mRecipe.getIngredients());
        LinearLayoutManager ingredientLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mIngredientsRecyclerView.setLayoutManager(ingredientLayoutManager);
        mIngredientsRecyclerView.setNestedScrollingEnabled(false);
        mIngredientsRecyclerView.setAdapter(ingredientAdapter);

        // Instantiate a StepAdapter and set it to the RecyclerView
        LinearLayoutManager stepLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mStepsRecyclerView.setLayoutManager(stepLayoutManager);
        mStepsRecyclerView.setNestedScrollingEnabled(false);
        mStepsRecyclerView.setAdapter(mStepAdapter);

        return rootView;
    }

    /*
     * Set the recipe object of this Fragment
     * @param recipe: the Recipe Object to assign to the mRecipe global variable
     */
    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    /*
     * Set the adapter to the RecyclerView
     * @param adapter: the adapter to use
     */
    public void setStepAdapter(StepAdapter adapter){
        mStepAdapter = adapter;
    }
}
