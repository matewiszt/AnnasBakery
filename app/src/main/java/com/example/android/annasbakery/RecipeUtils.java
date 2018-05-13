package com.example.android.annasbakery;

public class RecipeUtils {

    private RecipeUtils() {}

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
