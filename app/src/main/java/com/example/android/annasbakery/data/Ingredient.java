package com.example.android.annasbakery.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

// Class for one single Ingredient
public class Ingredient implements Parcelable {

    // Public constructor
    public Ingredient(double quantity, String measure, String ingredientName) {
        mQuantity = quantity;
        mMeasure = measure;
        mIngredient = ingredientName;
    }

    // Parcelable constructor
    private Ingredient(Parcel in) {
        mQuantity = in.readDouble();
        mMeasure = in.readString();
        mIngredient = in.readString();
    }

    // JSON keys
    private static final String JSON_KEY_QUANTITY = "quantity";
    private static final String JSON_KEY_MEASURE = "measure";
    private static final String JSON_KEY_INGREDIENT = "ingredient";

    // Properties
    @SerializedName(JSON_KEY_QUANTITY)
    private double mQuantity;

    @SerializedName(JSON_KEY_MEASURE)
    private String mMeasure;

    @SerializedName(JSON_KEY_INGREDIENT)
    private String mIngredient;

    // Getter methods
    public double getQuantity() { return mQuantity; }

    public String getMeasure() { return mMeasure; }

    public String getIngredientName() { return mIngredient; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mQuantity);
        dest.writeString(mMeasure);
        dest.writeString(mIngredient);
    }

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel parcel) {
            return new Ingredient(parcel);
        }

        @Override
        public Ingredient[] newArray(int i) {
            return new Ingredient[i];
        }

    };
}