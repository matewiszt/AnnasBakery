package com.example.android.annasbakery.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.annasbakery.R;
import com.example.android.annasbakery.RecipeUtils;
import com.example.android.annasbakery.data.Recipe;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeListViewHolder> {

    private Context mContext;
    private ArrayList<Recipe> mRecipes;
    private RecipeListItemClickHandler mClickHandler;

    /*
     * Public constructor
     * @param context: the Context of the adapter
     * @param handler: the click handler of the recipe list item
     */
    public RecipeListAdapter(Context context, RecipeListItemClickHandler handler) {
        mContext = context;
        mClickHandler = handler;
    }

    // Create the interface for the click handling
    public interface RecipeListItemClickHandler{
        void onRecipeClickHandler(Recipe recipe, int position);
    }

    public class RecipeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipe_list_item_name_tv)
        TextView mNameTextView;
        @BindView(R.id.recipe_list_item_iv)
        ImageView mImageView;

        public RecipeListViewHolder(View view){
            super(view);

            // Bind the Butterknife and set the OnClickListener
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            // Get the actual Recipe object and attach it to the onClickHandler
            Recipe recipe = mRecipes.get(getAdapterPosition());
            mClickHandler.onRecipeClickHandler(recipe, getAdapterPosition());
        }
    }

    @Override
    public RecipeListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Create a View from the recipe_list_item layout file and return a new ViewHolder instance created from it
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeListViewHolder(inflatedView);

    }

    @Override
    public void onBindViewHolder(final RecipeListViewHolder holder, int position) {

        // Get the actual Recipe object and get its properties
        Recipe actualRecipe = mRecipes.get(position);
        final String name = actualRecipe.getName();
        String imagePath = actualRecipe.getImagePath();

        // Set the properties to the correct views
        holder.mNameTextView.setText(name);
        holder.mImageView.setContentDescription(name);
        if (!TextUtils.isEmpty(imagePath)) {
            Picasso.with(mContext).load(imagePath)
                    .into(holder.mImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            // If the image load fails, load the stock image
                            holder.mImageView.setImageResource(RecipeUtils.findCorrectStockImage(name));
                        }
                    });
        } else {
            // If there is no image, load the stock image
            holder.mImageView.setImageResource(RecipeUtils.findCorrectStockImage(name));
        }
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null){
            return 0;
        }
        return mRecipes.size();
    }

    /*
     * Set the data to the adapter
     * @param recipes: the list of Recipe objects
     */
    public void setRecipeData(ArrayList<Recipe> recipes){

        mRecipes = (ArrayList<Recipe>) recipes;
        notifyDataSetChanged();
    }
}
