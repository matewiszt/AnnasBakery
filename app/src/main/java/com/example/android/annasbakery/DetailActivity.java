package com.example.android.annasbakery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.annasbakery.data.Recipe;
import com.example.android.annasbakery.data.Step;
import com.example.android.annasbakery.ui.RecipeDetailFragment;
import com.example.android.annasbakery.ui.StepAdapter;

// Shows the details of a selected recipe
public class DetailActivity extends AppCompatActivity implements StepAdapter.StepClickHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Activate up navigation
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Get the recipe data from the intent extras
        Bundle extras = getIntent().getExtras();
        Recipe recipe;
        if (extras != null) {
            recipe = extras.getParcelable(MainActivity.DETAIL_KEY);

            // Create a new RecipeDetailFragment instance with the recipe got from the intent
            if (recipe != null) {
                setTitle(recipe.getName());
                RecipeDetailFragment fragment = new RecipeDetailFragment();
                StepAdapter stepAdapter = new StepAdapter(this, this);
                fragment.setRecipe(recipe);
                stepAdapter.setData(recipe.getSteps());
                fragment.setStepAdapter(stepAdapter);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, fragment).commit();
            }
        }
    }

    // Handle the clicks on the Step items
    @Override
    public void onStepClickHandler(Step step) {
        Intent stepLaunchIntent = new Intent(DetailActivity.this, StepActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainActivity.STEP_KEY, step);
        stepLaunchIntent.putExtras(bundle);
        startActivity(stepLaunchIntent);
    }
}
