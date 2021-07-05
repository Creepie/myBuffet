package com.ada.mybuffet.screens.login


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import com.ada.mybuffet.MainActivity
import com.ada.mybuffet.R
import com.ada.mybuffet.testUtilities.SignedOutActivityTestRule
import com.ada.mybuffet.utils.EspressoIdlingResource
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get: Rule
    val signedOutActivityTestRule = SignedOutActivityTestRule()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginActivityTest() {

        // switch to login page:
        val materialTextView = onView(
            allOf(
                withId(R.id.register_tv_go_to_login), withText("Already have an account? Sign in."),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialTextView.perform(click())

        // enter test email
        val appCompatEditText = onView(
            allOf(
                withId(R.id.login_et_email),
                childAtPosition(
                    allOf(
                        withId(R.id.login_constraintLayout_card),
                        childAtPosition(
                            withId(R.id.login_cardView_login_form),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("test@test.com"), closeSoftKeyboard())

        // enter test password
        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.login_et_password),
                childAtPosition(
                    allOf(
                        withId(R.id.login_constraintLayout_card),
                        childAtPosition(
                            withId(R.id.login_cardView_login_form),
                            0
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("123456"), closeSoftKeyboard())

        // tap log in button
        val circularProgressButton = onView(
            allOf(
                withId(R.id.login_bT_logIn), withText("Log In"),
                childAtPosition(
                    allOf(
                        withId(R.id.login_constraintLayout_card),
                        childAtPosition(
                            withId(R.id.login_cardView_login_form),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        circularProgressButton.perform(click())


        // check if we are on the home screen by matching the home screen id
        onView(withId(R.id.home)).check(matches(isDisplayed()))
    }
    
    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
    }
