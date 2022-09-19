package com.example.mycorrectapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TeacherHomeActivityTest extends TestCase {

    @Rule
    public ActivityScenarioRule<TeacherHomeActivity> activityRule =
            new ActivityScenarioRule<>(TeacherHomeActivity.class);

    @Test
    public void teacherHomeActivityTest() {
        onView(withText("Hi t!")).check(matches(isDisplayed()));
    }

}