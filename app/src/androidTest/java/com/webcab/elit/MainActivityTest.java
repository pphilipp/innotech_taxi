package com.webcab.elit;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;


/**
 * Created by Sergey on 08.12.2015.
 */
@RunWith(AndroidJUnit4.class)

@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);




    /*
    Get code button must be active only if valid telephone number is entered. There are for now few
     country formats. Validation by amount of symbols in string
     */
    @Test
    public void checkGetCodeBtn_Enabled() {

        String VALID_PHONE_UA = "+380507777777";
        String VALID_PHONE_RU = "+78050777777";
        String VALID_PHONE_USA = "+18050777777";
        String INVALID_PHONE = "+38050777777";

        //is not enabled for invalid phones
        onView(withId(R.id.editText1)).perform(clearText(), typeText(INVALID_PHONE), closeSoftKeyboard());
        onView(withId(R.id.button_call)).check(matches(not(isEnabled())));

        //is enabled for each of valid phones
        //ua
        onView(withId(R.id.editText1)).perform(clearText(), typeText(VALID_PHONE_UA), closeSoftKeyboard());
        onView(withId(R.id.button_call)).check(matches(isEnabled()));

        //usa
        onView(withId(R.id.editText1)).perform(clearText(), typeText(VALID_PHONE_USA), closeSoftKeyboard());
        onView(withId(R.id.button_call)).check(matches(isEnabled()));

        //ru
        onView(withId(R.id.editText1)).perform(clearText(), typeText(VALID_PHONE_RU), closeSoftKeyboard());
        onView(withId(R.id.button_call)).check(matches(isEnabled()));

    }

    /*
    Get code button must be active if 4 symbol code is entered
     */
    @Test
    public void checkConfirmBtn() {
        //button must be enabled if there are 4 symbols in editTex field
        String VALID_CODE = "1111";
        String INVALID_CODE = "111";

        //button is not enabled for invalid code
        onView(withId(R.id.editText2)).perform(clearText(), typeText(INVALID_CODE), closeSoftKeyboard());
        onView(withId(R.id.button2)).check(matches(not(isEnabled())));

        //button is enabled for valid code
        onView(withId(R.id.editText2)).perform(clearText(), typeText(VALID_CODE), closeSoftKeyboard());
        onView(withId(R.id.button2)).check(matches(isEnabled()));
    }

    /*
    Help button must open dialog with country formats propositions
     */
    @Test
    public void checkHelpButton() {
        //Alert dialog with country choice must be shown
        onView(withId(R.id.help)).perform(click());

        onView(withText("+380 XX XXX XX XX")).check(matches(isDisplayed()));
    }

}
