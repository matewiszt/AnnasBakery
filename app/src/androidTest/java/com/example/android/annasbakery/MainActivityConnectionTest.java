package com.example.android.annasbakery;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.annasbakery.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityConnectionTest {


    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingInstance();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    /**
     * Test: It turns off the Wi-Fi and checks MainActivity's behaviour
     */
    @Test
    public void notConnected_emptyViewPopulated() {

        // Turn off the Wi-Fi and check if the EmptyView is displayed and the Recipe list RecyclerView not
        WifiManager wifiManager = (WifiManager)mActivityTestRule.getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        // Make the thread sleep for a while to give time to Wi-Fi disabling to take effect
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        onView(withId(R.id.recipe_list_empty)).check(matches(isDisplayed()));
        onView(withId(R.id.recipe_list_container)).check(matches(not(isDisplayed())));

        wifiManager.setWifiEnabled(true);

    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
