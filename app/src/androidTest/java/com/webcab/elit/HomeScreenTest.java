package com.webcab.elit;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.DatePicker;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by Sergey on 22.12.2015.
 */
public class HomeScreenTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(HomeScreen.class);



    /*
    date picker must be shown on "go now" button click
     */
    @Test
    public void testTimeBtn() {

        //clear form before test
        onView(withId(R.id.but_clear)).perform(click());
        onView(withText(R.string.yes)).perform(click());

        onView(withText(R.string.go_now)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(setDate());
        onView(withText(R.string.ready)).perform(click());
        onView(withText(R.string.ready)).perform(click());

        onView(withId(R.id.txt_go_now)).check(matches(not(withText(R.string.go_now))));
    }

    /*
    Check AddressTo to lead in address view and enter address
     */
    @Test
    public void addressToBtnTest() {

        onView(withId(R.id.bt_to)).perform(click());
        onView(withId(R.id.button3)).check(matches(withText(R.string.address_where)));
        onView(withId(R.id.bt_cancel)).perform(click());
    }

    /*
    Check AddressWhere to lead in address view and enter address
     */
    @Test
    public void addressWhereBtnTest() {

        onView(withId(R.id.bt_from)).perform(click());
        onView(withId(R.id.button3)).check(matches(withText(R.string.address_from)));
        onView(withId(R.id.bt_cancel)).perform(click());
    }

    /*
    Check features button
     */
    @Test
    public void featuresBtnTest() {
        onView(withId(R.id.bt_dop)).perform(click());
        onView(withId(R.id.button3)).check(matches(withText(R.string.features)));
        onView(withId(R.id.bt_cancel)).perform(click());
    }

    /*
    Check Client button
     */
    @Test
    public void clientBtnTest() {
        onView(withId(R.id.bt_client)).perform(scrollTo(), click());
        onView(withId(R.id.button3)).check(matches(withText(R.string.client_data)));
        onView(withId(R.id.bt_cancel)).perform(click());
    }



    /*
    Test upper button bar
     */
    @Test
    public void upperButtonTest() {
        //map button
        onView(withId(R.id.it_4)).perform(click());
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        //templates button
        onView(withId(R.id.it_2)).perform(click());
        onView(withText(R.string.templates)).check(matches(isDisplayed()));

        //cabinet button
        onView(withId(R.id.it_3)).perform(click());
        onView(withText(R.string.cabinet)).check(matches(isDisplayed()));

        //order button
        onView(withId(R.id.it_1)).perform(click());
        onView(withId(R.id.content)).check(matches(isDisplayed()));
    }

    /*
    Method for date check
     */
    public static ViewAction setDate() {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {

                //get tomorrow to make form think the order is preliminary
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH) + 1;

                ((DatePicker) view).updateDate(year, month, day);
            }

            @Override
            public String getDescription() {
                return "Set the date into the datepicker(day, month, year)";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(DatePicker.class);
            }
        };

    }
}
