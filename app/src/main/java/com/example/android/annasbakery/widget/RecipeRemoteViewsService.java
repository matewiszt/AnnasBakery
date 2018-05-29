package com.example.android.annasbakery.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.annasbakery.activity.MainActivity;
import com.example.android.annasbakery.R;
import com.example.android.annasbakery.data.Ingredient;
import com.example.android.annasbakery.data.Recipe;
import com.example.android.annasbakery.network.ApiFactory;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeRemoteViewsFactory(this.getApplicationContext());
    }
}

class RecipeRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ArrayList<Ingredient> mIngredients;

    public RecipeRemoteViewsFactory(Context context){
        mContext = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
            loadData();
    }

    @Override
    public void onDestroy() {
        if (mIngredients != null) {mIngredients = null;}
    }

    @Override
    public int getCount() {
        return mIngredients == null ? 0: mIngredients.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Ingredient actualIngredient = mIngredients.get(position);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_item);
        String ingredientName = actualIngredient.getIngredientName();
        double ingredientQuantity = actualIngredient.getQuantity();
        String ingredientMeasure = actualIngredient.getMeasure();
        String ingredientString = String.format(mContext.getString(R.string.recipe_ingredients_text), ingredientQuantity, ingredientMeasure, ingredientName);
        rv.setTextViewText(R.id.widget_ingredient_tv, ingredientString);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
       return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void loadData(){
        Call<ArrayList<Recipe>> call = ApiFactory.getRecipes();
        try {
            Response<ArrayList<Recipe>> response = call.execute();
            if (response != null){
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                int position = preferences.getInt(MainActivity.DETAIL_POSITION_KEY, 0);
                Recipe recipe = response.body().get(position);
                mIngredients = recipe.getIngredients();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
