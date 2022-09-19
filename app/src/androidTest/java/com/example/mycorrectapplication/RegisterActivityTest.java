package com.example.mycorrectapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest extends TestCase {

    @Rule
    public ActivityScenarioRule<RegisterActivity> activityRule =
            new ActivityScenarioRule<>(RegisterActivity.class);

    @Test
    public void registerUser() {
        onView(withHint("Enter Name")).perform(typeText("teacher"), closeSoftKeyboard());
        onView(withHint("Enter Username")).perform(typeText("teacher"), closeSoftKeyboard());
        onView(withHint("Enter Password")).perform(typeText("teacher"), closeSoftKeyboard());
        onView(withHint("Re-enter Password")).perform(typeText("teacher"), closeSoftKeyboard());
        onView(withText("REGISTER")).perform(click());
    }
}