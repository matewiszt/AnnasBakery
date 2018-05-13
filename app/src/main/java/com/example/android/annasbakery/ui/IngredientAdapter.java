package com.example.android.annasbakery.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.annasbakery.R;
import com.example.android.annasbakery.data.Ingredient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private Context mContext;
    private ArrayList<Ingredient> mIngredients;

    public IngredientAdapter(Context context){
        mContext = context;
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ingredient_tv)
        TextView mIngredientTextView;

        public IngredientViewHolder(View view){
            super(view);

            // Bind the Butterknife and set the OnClickListener
            ButterKnife.bind(this, view);
        }

    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View newView = LayoutInflater.from(mContext).inflate(R.layout.ingredient_item, parent, false);
        return new IngredientViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {

        Ingredient currentItem = mIngredients.get(position);
        String ingredientName = currentItem.getIngredientName();
        double ingredientQuantity = currentItem.getQuantity();
        String ingredientMeasure = currentItem.getMeasure();
        String ingredientString = String.format(mContext.getString(R.string.recipe_ingredients_text), ingredientQuantity, ingredientMeasure, ingredientName);

        holder.mIngredientTextView.setText(ingredientString);
    }

    @Override
    public int getItemCount() {
        if (mIngredients == null || mIngredients.size() == 0){
            return 0;
        }
        return mIngredients.size();
    }

    /*
     * Set the data to the adapter
     * @param ingredients: the list of Ingredient objects
     */
    public void setData(ArrayList<Ingredient> ingredients){

        // Refresh the recipe list and notify the loader
        mIngredients = ingredients;
        notifyDataSetChanged();
    }
}
