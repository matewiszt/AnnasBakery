package com.example.android.annasbakery;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.annasbakery.activity.MainActivity;
import com.example.android.annasbakery.data.Recipe;
import com.example.android.annasbakery.network.ApiFactory;
import com.example.android.annasbakery.ui.RecipeListAdapter;
import com.example.android.annasbakery.ui.RecipeListFragment;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * This test demos the workflow of the MainActivity
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {

    // This is the name of our mock cake
    private static final String CAKE_NAME = "Nutella Pie";
    private static final Intent MY_ACTIVITY_INTENT = new Intent(InstrumentationRegistry.getTargetContext(), MainActivity.class);
    private IdlingResource mIdlingResource;

    // This is a mock API Factory for testing
    public class MockedRecipeApiFactory implements ApiFactory.RecipeApiFactory {

        private final BehaviorDelegate<ApiFactory.RecipeApiFactory> mDelegate;

        public MockedRecipeApiFactory(BehaviorDelegate<ApiFactory.RecipeApiFactory> service) {
            mDelegate = service;
        }

        @Override
        public Call<ArrayList<Recipe>> fetchRecipes() {
            return mDelegate.returningResponse(TestUtils.generateMockRecipes()).fetchRecipes();
        }
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingInstance();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    /**
     * Test 1: This case mocks the response of the API endpoint
     */
    @Test
    public void mockHttpResponse() {
        // Creates a MockRetrofit with the same behaviour as the ApiFactory one
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiFactory.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();

        MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();

        // Delegates the behaviour of the RecipeApiFactory to the MockedRecipeApiFactory
        BehaviorDelegate<ApiFactory.RecipeApiFactory> delegate = mockRetrofit.create(ApiFactory.RecipeApiFactory.class);
        ApiFactory.RecipeApiFactory mockedService = new MockedRecipeApiFactory(delegate);

        Call<ArrayList<Recipe>> call = mockedService.fetchRecipes();

        ArrayList<Recipe> recipes = null;
        Response<ArrayList<Recipe>> response = null;
        try {
            response = call.execute();
            recipes = response.body();
        } catch (IOException e){
            e.printStackTrace();
        }

        // Check if the response is successful, its size is 1 and the name of the Recipe is the expected one
        Assert.assertTrue(response.isSuccessful());
        Assert.assertTrue(recipes.size() == 1);
        Assert.assertTrue(recipes.get(0).getName().equals(CAKE_NAME));
    }

    /**
     * Test 2: This case mocks the behaviour of the RecipeListFragment addition (initFragments() in MainActivity)
     */
    @Test
    public void onCreate_addsRecipeListFragment() {
        RecipeListAdapter adapter = new RecipeListAdapter(InstrumentationRegistry.getTargetContext(), null);
        RecipeListFragment listFragment = new RecipeListFragment();
        adapter.setRecipeData(TestUtils.generateMockRecipes());
        listFragment.setAdapter(adapter);
        mActivityTestRule.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.recipe_list_container, listFragment).commitAllowingStateLoss();
        // Checks that if we add the RecipeListFragment manually with mock data, the MainActivity has the right data
        onView(withId(R.id.recipe_list_rv)).check(matches(hasDescendant(withText(CAKE_NAME))));
    }

    /**
     * Test 3: This case simply launches MainActivity and mocks its behaviour
     */
    @Test
    public void activity_populatesRecipeList() {
        mActivityTestRule.launchActivity(MY_ACTIVITY_INTENT);

        // Checks that if we MainActivity has the right data if we launch it and the EmptyView and the ProgressBar is hidden
        onView(withId(R.id.recipe_list_rv)).check(matches(hasDescendant(withText(CAKE_NAME))));
        onView(withId(R.id.recipe_list_empty)).check(matches(not(isDisplayed())));
        onView(withId(R.id.recipe_list_progress_bar)).check(matches(not(isDisplayed())));
    }

    /**
     * Test 4: This case launches MainActivity and performs a screen rotation
     */
    @Test
    public void onScreenRotation_keepsData() {
        mActivityTestRule.launchActivity(MY_ACTIVITY_INTENT);
        // Rotates to landscape and checks the expected data
        onView(withId(R.id.recipe_list_rv)).check(matches(hasDescendant(withText(CAKE_NAME))));

        // Rotates to portrait and checks the expected data
        TestUtils.rotateScreen(mActivityTestRule.getActivity());
        onView(withId(R.id.recipe_list_rv)).check(matches(hasDescendant(withText(CAKE_NAME))));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
