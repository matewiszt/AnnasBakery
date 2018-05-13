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
import com.example.android.annasbakery.data.Step;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

    private Context mContext;
    private ArrayList<Step> mSteps;
    private StepClickHandler mClickHandler;

    // Public constructor
    public StepAdapter(Context context, StepClickHandler handler){
        mContext = context;
        mClickHandler = handler;
    }

    // Create an interface for the step clicks
    public interface StepClickHandler{
        void onStepClickHandler(Step step);
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Bind the views
        @BindView(R.id.step_iv)
        ImageView mImageView;
        @BindView(R.id.step_icon_iv)
        ImageView mIconImageView;
        @BindView(R.id.step_desc_tv)
        TextView mDescTextView;
        @BindView(R.id.step_order_tv)
        TextView mOrderTextView;

        public StepViewHolder(View view){
            super(view);

            // Bind the views and set the click listener
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // On click, pass the corresponding Step object
            Step clickedStep = mSteps.get(getAdapterPosition());
            mClickHandler.onStepClickHandler(clickedStep);
        }
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate a view from the correct layout file and create a StepViewHolder from it
        View inflatedView = LayoutInflater.from(mContext).inflate(R.layout.step_item, parent, false);
        return new StepViewHolder(inflatedView);

    }

    @Override
    public void onBindViewHolder(final StepViewHolder holder, int position) {

        // Get the current Step object and extract the data from it
        Step currentStep = mSteps.get(position);
        String thumbnailUrl = currentStep.getThumbnailUrl();
        final String shortDesc = currentStep.getShortDescription();

        // If there is a thumbnail Url, try to load the image
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Picasso.with(mContext).load(thumbnailUrl)
                    .into(holder.mImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            // If the image load is successful, show the image and hide the icon
                            holder.mIconImageView.setVisibility(View.GONE);
                            holder.mImageView.setContentDescription(shortDesc);
                        }

                        @Override
                        public void onError() {
                            // If the image load failed, show the icon and hide the imageView
                            holder.mImageView.setVisibility(View.GONE);
                            holder.mIconImageView.setContentDescription(shortDesc);
                            holder.mIconImageView.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            // If the image load failed, show the icon and hide the imageView
            holder.mImageView.setVisibility(View.GONE);
            holder.mIconImageView.setContentDescription(shortDesc);
            holder.mIconImageView.setVisibility(View.VISIBLE);
        }

        holder.mDescTextView.setText(shortDesc);
        holder.mOrderTextView.setText(String.valueOf(position + 1));

    }

    @Override
    public int getItemCount() {
        if (mSteps == null || mSteps.size() == 0) {
            return 0;
        }
        return mSteps.size();
    }

    /*
     * Set the data to the adapter
     * @param steps: the list of Step objects
     */
    public void setData(ArrayList<Step> steps){
        mSteps = steps;
        notifyDataSetChanged();
    }
}
