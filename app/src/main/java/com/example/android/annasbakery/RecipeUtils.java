package com.example.android.annasbakery;

// Class for utilities
public class RecipeUtils {

    // Private constructor because we don't want to create an instance of this class
    private RecipeUtils() {}

    /*
     * Find the corresponding stock image for a recipe
     * @param name: the name of the cake
     * @return int: the resource id of the correct image
     */
    public static int findCorrectStockImage(String name) {
        switch (name){
            case "Brownies":
                return R.drawable.brownie;
            case "Yellow Cake":
                return R.drawable.yellow;
            case "Nutella Pie":
                return R.drawable.nutella;
            case "Cheesecake":
                return R.drawable.cheesecake;
            default:
                return R.drawable.recipe_image_placeholder;
        }
    }
}
