package com.example.android.annasbakery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.annasbakery.R;
import com.example.android.annasbakery.data.Recipe;
import com.example.android.annasbakery.data.Step;
import com.example.android.annasbakery.ui.RecipeDetailFragment;
import com.example.android.annasbakery.ui.StepAdapter;

// Shows the details of a selected recipe
public class DetailActivity extends AppCompatActivity implements StepAdapter.StepClickHandler {

    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Activate up navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Get the recipe data from the intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mRecipe = extras.getParcelable(MainActivity.DETAIL_KEY);

            // Create a new RecipeDetailFragment instance with the recipe got from the intent
            if (mRecipe != null) {
                setTitle(mRecipe.getName());
                RecipeDetailFragment fragment = new RecipeDetailFragment();
                StepAdapter stepAdapter = new StepAdapter(this, this);
                fragment.setRecipe(mRecipe);
                stepAdapter.setData(mRecipe.getSteps());
                fragment.setStepAdapter(stepAdapter);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, fragment).commit();
            }
        }
    }

    // Handle the clicks on the Step items
    @Override
    public void onStepClickHandler(Step step) {
        // Launch the StepActivity on step click
        Intent stepLaunchIntent = new Intent(DetailActivity.this, StepActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainActivity.STEP_KEY, step);
        bundle.putParcelable(MainActivity.DETAIL_KEY, mRecipe);// Pass also the Recipe object for later use
        stepLaunchIntent.putExtras(bundle);
        startActivity(stepLaunchIntent);
    }
}
