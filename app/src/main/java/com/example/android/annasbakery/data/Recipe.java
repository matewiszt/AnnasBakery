package com.example.android.annasbakery.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

// Recipe object for one single recipe
public class Recipe implements Parcelable {

    // Public constructor
    public Recipe(int id, String name, ArrayList<Ingredient> ingredients, ArrayList<Step> steps, int servings, String imagePath) {
        mId = id;
        mName = name;
        mIngredients = ingredients;
        mSteps = steps;
        mServings = servings;
        mImagePath = imagePath;
    }

    // Parcelable constructor
    private Recipe(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mIngredients = in.readArrayList(Ingredient.class.getClassLoader());
        mSteps = in.readArrayList(Step.class.getClassLoader());
        mServings = in.readInt();
        mImagePath = in.readString();
    }

    // JSON key constants for serializing the JSON
    private static final String JSON_KEY_ID = "id";
    private static final String JSON_KEY_NAME = "name";
    private static final String JSON_KEY_INGREDIENTS = "ingredients";
    private static final String JSON_KEY_STEPS = "steps";
    private static final String JSON_KEY_SERVINGS = "servings";
    private static final String JSON_KEY_IMAGE = "image";

    // Properties of the class
    @SerializedName(JSON_KEY_ID)
    private int mId;

    @SerializedName(JSON_KEY_NAME)
    private String mName;

    @SerializedName(JSON_KEY_INGREDIENTS)
    private ArrayList<Ingredient> mIngredients = null;

    @SerializedName(JSON_KEY_STEPS)
    private ArrayList<Step> mSteps = null;

    @SerializedName(JSON_KEY_SERVINGS)
    private int mServings;

    @SerializedName(JSON_KEY_IMAGE)
    private String mImagePath;

    // Getter methods
    public int getId() { return mId; }

    public String getName() { return mName; }

    public ArrayList<Ingredient> getIngredients() { return mIngredients; }

    public ArrayList<Step> getSteps() { return mSteps; }

    public int getServings() { return mServings; }

    public String getImagePath() { return mImagePath; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeList(mIngredients);
        dest.writeList(mSteps);
        dest.writeInt(mServings);
        dest.writeString(mImagePath);
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel parcel) {
            return new Recipe(parcel);
        }

        @Override
        public Recipe[] newArray(int i) {
            return new Recipe[i];
        }

    };
}
