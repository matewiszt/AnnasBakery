package com.example.android.annasbakery.network;

import com.example.android.annasbakery.data.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

// This class performs the API calls to get the list of recipes
public class ApiFactory {

    // Private constructor - never used
    private ApiFactory() {}

    // The base URL of our API endpoint
    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    // The interface of our ApiFactory
    private interface RecipeApiFactory {

        // Create a call with method GET with the given endpoint
        @GET("baking.json")
        Call<ArrayList<Recipe>> fetchRecipes();

    }

    /*
     * Implement the fetchRecipes abstract method to get the list of recipes
     * @return Call<ArrayList<Recipe>> list of Recipe objects
     */
    public static Call<ArrayList<Recipe>> getRecipes() {

        // Create a basic REST adapter which points to the BASE_URL
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RecipeApiFactory apiFactory = retrofit.create(RecipeApiFactory.class);

        return apiFactory.fetchRecipes();

    }
}
