package com.example.android.annasbakery;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.example.android.annasbakery.data.Recipe;
import com.example.android.annasbakery.network.ApiFactory;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeWidgetProvider extends AppWidgetProvider {

    private static Recipe mRecipe;
    private Context mContext;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateWidgets(context, appWidgetManager, appWidgetIds);
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        loadRecipes(context, appWidgetManager, appWidgetId);
    }

    private static void loadRecipes(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {

        // Create a call with the API Factory and implement its callbacks
        Call<ArrayList<Recipe>> call = ApiFactory.getRecipes();

        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Recipe>> call, @NonNull Response<ArrayList<Recipe>> response) {

                // On success, set the response body as the recipe list, show it and initialize the fragments
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                int position = preferences.getInt(MainActivity.DETAIL_POSITION_KEY, 0);
                mRecipe = response.body().get(position);
                Intent detailLaunchIntent = new Intent(context, DetailActivity.class);
                Bundle extras = new Bundle();
                extras.putParcelable(MainActivity.DETAIL_KEY, mRecipe);
                detailLaunchIntent.putExtras(extras);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, detailLaunchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // Construct the RemoteViews object
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
                Intent remoteViewsIntent = new Intent(context, RecipeRemoteViewsService.class);
                views.setRemoteAdapter(R.id.widget_ingredient_lv, remoteViewsIntent);
                views.setEmptyView(R.id.widget_ingredient_lv, R.id.widget_empty_tv);
                views.setTextViewText(R.id.widget_name_tv, mRecipe.getName());
                // Widgets allow click handlers to only launch pending intents
                views.setOnClickPendingIntent(R.id.widget_container_layout, pendingIntent);
                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable t) {
                // On failure, we don't provide the click handler because it will throw an error
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
                Intent remoteViewsIntent = new Intent(context, RecipeRemoteViewsService.class);
                views.setRemoteAdapter(R.id.widget_ingredient_lv, remoteViewsIntent);
                views.setEmptyView(R.id.widget_ingredient_lv, R.id.widget_empty_tv);
                views.setTextViewText(R.id.widget_name_tv, mRecipe.getName());
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        });
    }

    public static void sendUpdateBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, RecipeWidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all your widgets
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, RecipeWidgetProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(cn), R.id.widget_ingredient_lv);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(cn);
            updateWidgets(context, appWidgetManager, appWidgetIds);
        }
        super.onReceive(context, intent);
    }
}
