package com.example.recipes.presentation.ui.viewpager

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.recipes.R

@RunWith(AndroidJUnit4::class)
@LargeTest
class ViewPagerActivityTest {

    @get:Rule
    val viewPagerActivityRule = ActivityScenarioRule(ViewPagerActivity::class.java)

    @Test
    fun chekNextClick() {
        onView(withText(R.string.first_slide)).check(matches(isDisplayed()))

        onView(withText(R.string.next)).check(matches(isDisplayed()))

        onView(withId(R.id.btnNext)).perform(click())
        onView(withText(R.string.second_slide)).check(matches(isDisplayed()))
        onView(withId(R.id.btnNext)).perform(click())
        onView(withText(R.string.third_slide)).check(matches(isDisplayed()))
        onView(withId(R.id.btnNext)).perform(click())
        onView(withText(R.string.fourth_slide)).check(matches(isDisplayed()))

        onView(withId(R.id.btnSkip)).noActivity()
        onView(withText(R.string.start)).perform(click())
        onView(withId(R.id.viewPager)).noActivity()
    }

    @Test
    fun checkSwipes() {
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withText(R.string.second_slide)).check(matches(isDisplayed()))
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withText(R.string.third_slide)).check(matches(isDisplayed()))
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withText(R.string.fourth_slide)).check(matches(isDisplayed()))

        onView(withText(R.string.start)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSkip)).noActivity()

        onView(withId(R.id.viewPager)).perform(swipeRight())
        onView(withText(R.string.third_slide)).check(matches(isDisplayed()))
        onView(withId(R.id.viewPager)).perform(swipeRight())
        onView(withText(R.string.second_slide)).check(matches(isDisplayed()))
        onView(withId(R.id.viewPager)).perform(swipeRight())
        onView(withText(R.string.first_slide)).check(matches(isDisplayed()))

    }

    @Test
    fun checkSkipClick() {
        onView(withText(R.string.skip)).check(matches(isDisplayed())).check(matches(isClickable()))
        onView(withText(R.string.skip)).perform(click())
        onView(withId(R.id.viewPager)).noActivity()
    }

}