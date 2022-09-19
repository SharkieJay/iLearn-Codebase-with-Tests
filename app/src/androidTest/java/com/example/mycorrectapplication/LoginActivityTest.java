package com.example.mycorrectapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.endsWith;

import android.content.Intent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.mycorrectapplication.database.classrooms.ClassroomHelper;
import com.example.mycorrectapplication.database.teacher.TeacherHelper;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginActivityTest extends TestCase {
    JSONArray classroom;
    JSONObject lesson;
    TeacherHelper service;

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Rule
    public ActivityTestRule teacherHomeActivityRule = new ActivityTestRule(TeacherHomeActivity.class, false, false);

    @Rule
    public ActivityTestRule teacherClassroomActivity = new ActivityTestRule(TeacherClassroomActivity.class, false, false);

    @Rule
    public ActivityTestRule teacherClassroomViewActivity = new ActivityTestRule(TeacherClassroomViewActivity.class, false, false);

    @Rule
    public ActivityTestRule teacherLessonCreateActivity = new ActivityTestRule(TeacherLessonViewActivity.class, false, false);

    @Rule
    public ActivityTestRule teacherLessonViewActivity = new ActivityTestRule(TeacherLessonCreateActivity.class, false, false);

    @Test
    public void A_loginActivityTest() {
        onView(withText("Welcome to iLearn!")).check(matches(isDisplayed()));
        onView(withHint("Enter Username")).perform(typeText("t"), closeSoftKeyboard());
        onView(withHint("Enter Password")).perform(typeText("t"), closeSoftKeyboard());
        onView(withText("LOGIN")).perform(click());
    }

    @Test
    public void B_teacherActivityTest() {
        Intent intent = new Intent();
        teacherHomeActivityRule.launchActivity(intent);
        onView(withText("Hi t!")).check(matches(isDisplayed()));
        onView(withText("CLASSROOMS")).perform(click());
    }

    @Test
    public void C_classroomActivityTest() {
        Intent intent = new Intent();
        intent.putExtra("teacherId", "t");
        teacherClassroomActivity.launchActivity(intent);
        onView(withText("Classrooms")).check(matches(isDisplayed()));
        onView(withText("VIEW / UPDATE CLASSROOMS")).perform(click());
    }

    @Test
    public void D_classroomViewActivityTest() {
       try {
            Intent intent = new Intent();
            teacherClassroomViewActivity.launchActivity(intent);
            onView(withText("Classrooms")).check(matches(isDisplayed()));
            onView(withText("TEST")).perform(click());
            ClassroomHelper service = new ClassroomHelper(teacherClassroomViewActivity.getActivity().getApplicationContext());
            this.classroom = service.getClassrooms("t");
        } finally {
           onView(withText("Lessons")).perform(click());
       }
    }

    @Test
    public void E_classroomConfigActivityTest() {
        Intent intent = new Intent();
        intent.putExtra("classID", "t_test");
        intent.putExtra("classname", "test");
        teacherLessonCreateActivity.launchActivity(intent);
        onView(withText("TEST")).perform(click());
    }
}