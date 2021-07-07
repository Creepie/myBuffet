package com.ada.mybuffet.testUtilities

import android.app.Activity
import androidx.test.rule.ActivityTestRule
import com.google.firebase.auth.FirebaseAuth
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/*
 * Applying this rule to a test makes sure that the user is logged out before the tests run.
 * E.g. useful for testing login & sign up processes
 */
class SignedOutActivityTestRule: TestWatcher() {

    override fun starting(description: Description?) {
        // sign out user
        FirebaseAuth.getInstance().signOut()

        super.starting(description)
    }
}