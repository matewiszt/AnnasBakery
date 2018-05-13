package com.example.android.annasbakery.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

// Step object for recipe preparation steps
public class Step implements Parcelable {

    // Public constructor
    public Step(int id, String shortDesc, String desc, String videoUrl, String thumbnailUrl) {
        mStepId = id;
        mShortDescription = shortDesc;
        mDescription = desc;
        mVideoUrl = videoUrl;
        mThumbnailUrl = thumbnailUrl;
    }

    // Parcelable constructor
    private Step(Parcel in) {
        mStepId = in.readInt();
        mShortDescription = in.readString();
        mDescription = in.readString();
        mVideoUrl = in.readString();
        mThumbnailUrl = in.readString();
    }

    // JSON keys
    private static final String JSON_KEY_STEP_ID = "id";
    private static final String JSON_KEY_SHORT_DESC = "shortDescription";
    private static final String JSON_KEY_DESC = "description";
    private static final String JSON_KEY_VIDEO_URL = "videoURL";
    private static final String JSON_KEY_THUMB_URL = "thumbnailURL";

    // Properties
    @SerializedName(JSON_KEY_STEP_ID)
    private int mStepId;

    @SerializedName(JSON_KEY_SHORT_DESC)
    private String mShortDescription;

    @SerializedName(JSON_KEY_DESC)
    private String mDescription;

    @SerializedName(JSON_KEY_VIDEO_URL)
    private String mVideoUrl;

    @SerializedName(JSON_KEY_THUMB_URL)
    private String mThumbnailUrl;

    // Getter methods
    public int getId() { return mStepId; }

    public String getShortDescription() { return mShortDescription; }

    public String getDescription() { return mDescription; }

    public String getVideoUrl() { return mVideoUrl; }

    public String getThumbnailUrl() { return mThumbnailUrl; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mStepId);
        dest.writeString(mShortDescription);
        dest.writeString(mDescription);
        dest.writeString(mVideoUrl);
        dest.writeString(mThumbnailUrl);
    }

    public static final Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel parcel) {
            return new Step(parcel);
        }

        @Override
        public Step[] newArray(int i) {
            return new Step[i];
        }

    };
}
