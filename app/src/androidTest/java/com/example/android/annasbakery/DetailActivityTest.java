package com.example.android.annasbakery;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {

    private static final String CAKE_NAME = "Nutella Pie";
    private static final String STEP_SHORT_DESC = "Recipe Introduction";
    private static final Intent DETAIL_ACTIVITY_INTENT = new Intent(InstrumentationRegistry.getTargetContext(), DetailActivity.class);
    private static final Intent MAIN_ACTIVITY_INTENT = new Intent(InstrumentationRegistry.getTargetContext(), MainActivity.class);

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Test 1: This case performs a click on a recipe list item
     */
    @Test
    public void recipeClick_launchesDetailActivity() {

        // Perform a click on a recipe and check if a DetailActivity with the right cake name is launched
        onView(withId(R.id.recipe_list_rv))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(CAKE_NAME)), click()));

        onView(withId(R.id.recipe_detail_name_tv)).check(matches(withText(CAKE_NAME)));
    }

    /**
     * Test 2: This case performs a click on a recipe list item and after another on a step item
     */
    @Test
    public void onStepClick_launchesStepActivity() {

        // Click on a recipe, launch the DetailActivity
        onView(withId(R.id.recipe_list_rv))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(CAKE_NAME)), click()));

        // Check if there is a step item, scroll to it anc click it, it launches the StepActivity
        onView(withId(R.id.recipe_steps_rv)).check(matches(hasDescendant(withText(STEP_SHORT_DESC))));
        onView(withId(R.id.recipe_steps_rv))
                .perform(scrollTo());
        onView(withId(R.id.recipe_steps_rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        0, click()));

        // Check if the corresponding TextView has the step's short description
        onView(withId(R.id.step_short_desc_tv)).check(matches(withText(STEP_SHORT_DESC)));
    }

    /**
     * Test 3: This case performs a click on a recipe list item and after another on a step item with no video
     */
    @Test
    public void onStepLaunch_withoutVideo() {

        // Click on a recipe, launch the DetailActivity
        onView(withId(R.id.recipe_list_rv))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(CAKE_NAME)), click()));

        // Scroll to the steps RecyclerView anc click an item without a video, it launches the StepActivity
        onView(withId(R.id.recipe_steps_rv))
                .perform(scrollTo());
        onView(withId(R.id.recipe_steps_rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        1, click()));

        // Check if the empty ImageView is displayed
        onView(withId(R.id.step_empty_iv)).check(matches(isDisplayed()));
    }
}
