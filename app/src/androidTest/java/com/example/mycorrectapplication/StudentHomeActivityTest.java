package com.example.mycorrectapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

import android.content.Intent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import junit.framework.TestCase;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StudentHomeActivityTest extends TestCase {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Rule
    public ActivityTestRule studentHomeActivityRule = new ActivityTestRule(StudentHomeActivity.class, false, false);


    @Test
    public void A_loginActivityTest() {
        onView(withText("Welcome to iLearn!")).check(matches(isDisplayed()));
        onView(withHint("Enter Username")).perform(typeText("amali"), closeSoftKeyboard());
        onView(withHint("Enter Password")).perform(typeText("amali"), closeSoftKeyboard());
        onView(withText("LOGIN")).perform(click());
    }

    @Test
    public void B_studentHomeActivityTest() {
        Intent intent = new Intent();
        studentHomeActivityRule.launchActivity(intent);
        onView(withText("Hi Amali Perera!")).check(matches(isDisplayed()));
        onView(withText("MY CLASSROOMS")).perform(click());
        onView(withText("TEST")).perform(click());
        onView(withText("test lesson")).perform(click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withHint("Enter Answer")).perform(typeText("test"), closeSoftKeyboard());
        onView(withText("Submit")).perform(click());
    }
}