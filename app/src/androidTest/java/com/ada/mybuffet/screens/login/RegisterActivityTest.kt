package com.ada.mybuffet.screens.login


import android.os.IBinder
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.ada.mybuffet.MainActivity
import com.ada.mybuffet.R
import com.ada.mybuffet.ToastMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class RegisterActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testEmailTextView() {
        val textView = onView(
            allOf(
                withId(R.id.register_tv_email), withText("Email"),
                withParent(
                    allOf(
                        withId(R.id.register_constraintLayout_card),
                        withParent(withId(R.id.register_cardView_login_form))
                    )
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Email")))
    }


    @Test
    fun testPasswordTextView() {
        val textView = onView(
            allOf(
                withId(R.id.register_tv_password), withText("Password"),
                withParent(
                    allOf(
                        withId(R.id.register_constraintLayout_card),
                        withParent(withId(R.id.register_cardView_login_form))
                    )
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Password")))
    }

    @Test
    fun testRepeatPasswordTextView() {
        val textView = onView(
            allOf(
                withId(R.id.register_tv_repeat_password), withText("Repeat Password"),
                withParent(
                    allOf(
                        withId(R.id.register_constraintLayout_card),
                        withParent(withId(R.id.register_cardView_login_form))
                    )
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Repeat Password")))
    }

    @Test
    fun testIfLogoImageExists() {
        val imageView = onView(
            allOf(
                withId(R.id.register_img_logo),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        imageView.check(matches(isDisplayed()))
    }

    @Test
    fun testIfSignUpButtonExists() {
        val button = onView(
            allOf(
                withId(R.id.register_bT_logIn), withText("SIGN UP"),
                withParent(
                    allOf(
                        withId(R.id.register_constraintLayout_card),
                        withParent(withId(R.id.register_cardView_login_form))
                    )
                ),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))
    }

    @Test
    fun testAlreadyHaveAccountTextView() {
        val textView = onView(
            allOf(
                withId(R.id.register_tv_go_to_login), withText("Already have an account? Sign in."),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Already have an account? Sign in.")))
    }



    @Test
    fun testSignUpButton_showToastWhenFieldsEmpty() {
        // press button
        val circularProgressButton = onView(
            allOf(withId(R.id.register_bT_logIn), withText("Sign Up"),
                childAtPosition(
                    allOf(withId(R.id.register_constraintLayout_card),
                        childAtPosition(
                            withId(R.id.register_cardView_login_form),
                            0)),
                    0),
                isDisplayed()))
        circularProgressButton.perform(ViewActions.click())

        // check if toast is displayed
        onView(withText(R.string.register_fields_not_filled))
            .inRoot(ToastMatcher())
            .check(matches(withText(R.string.register_fields_not_filled)))
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
