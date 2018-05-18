package com.example.android.annasbakery;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;

import com.example.android.annasbakery.data.Ingredient;
import com.example.android.annasbakery.data.Recipe;
import com.example.android.annasbakery.data.Step;

import java.util.ArrayList;

public class TestUtils {

    private TestUtils() {}

    public static ArrayList<Recipe> generateMockRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        int id = 1;
        String name = "Nutella Pie";
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        Ingredient ingredient1 = new Ingredient(2, "CUP", "Graham Cracker crumbs");
        Ingredient ingredient2 = new Ingredient(6, "TBLSP", "unsalted butter, melted");
        ingredients.add(ingredient1);
        ingredients.add(ingredient2);
        ArrayList<Step> steps = new ArrayList<Step>();
        Step step1 = new Step(0, "Recipe Introduction", "Recipe Introduction", "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4", "");
        Step step2 = new Step(1, "Starting prep", "1. Preheat the oven to 350\u00b0F. Butter a 9\" deep dish pie pan.", "", "");
        steps.add(step1);
        steps.add(step2);
        int servings = 8;
        String imagePath = "";
        Recipe recipe = new Recipe(id, name, ingredients, steps, servings, imagePath);
        recipes.add(recipe);
        return recipes;
    }

    public static void rotateScreen(Activity activity) {
        Context context = InstrumentationRegistry.getTargetContext();
        int orientation
                = context.getResources().getConfiguration().orientation;

        activity.setRequestedOrientation(
                (orientation == Configuration.ORIENTATION_PORTRAIT) ?
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
