package org.wordpress.android.e2e;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.wordpress.android.InitializationRule;
import org.wordpress.android.e2e.flows.LoginFlow;
import org.wordpress.android.support.BaseTest;
import org.wordpress.android.ui.accounts.LoginMagicLinkInterceptActivity;

import static org.wordpress.android.BuildConfig.E2E_SELF_HOSTED_USER_PASSWORD;
import static org.wordpress.android.BuildConfig.E2E_SELF_HOSTED_USER_SITE_ADDRESS;
import static org.wordpress.android.BuildConfig.E2E_SELF_HOSTED_USER_USERNAME;
import static org.wordpress.android.BuildConfig.E2E_WP_COM_PASSWORDLESS_USER_EMAIL;
import static org.wordpress.android.BuildConfig.E2E_WP_COM_USER_EMAIL;
import static org.wordpress.android.BuildConfig.E2E_WP_COM_USER_PASSWORD;
import static org.wordpress.android.BuildConfig.E2E_WP_COM_USER_SITE_ADDRESS;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
public class LoginTests extends BaseTest {
    @Rule(order = 0)
    public HiltAndroidRule rule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public InitializationRule initRule = new InitializationRule();

    @Rule(order = 2)
    public ActivityTestRule<LoginMagicLinkInterceptActivity> mMagicLinkActivityTestRule =
            new ActivityTestRule<>(LoginMagicLinkInterceptActivity.class, true, false);

    @Before
    public void setUp() {
        logoutIfNecessary();
    }

    @Test
    public void loginWithEmailPassword() {
        new LoginFlow().chooseContinueWithWpCom()
                       .enterEmailAddress(E2E_WP_COM_USER_EMAIL)
                       .enterPassword(E2E_WP_COM_USER_PASSWORD)
                       .confirmLogin(false);
    }

    @Test
    public void loginWithPasswordlessAccount() {
        new LoginFlow().chooseContinueWithWpCom()
                       .enterEmailAddress(E2E_WP_COM_PASSWORDLESS_USER_EMAIL)
                       .openMagicLink(mMagicLinkActivityTestRule)
                       .confirmLogin(false);
    }

    @Test
    public void loginWithSiteAddress() {
        new LoginFlow().chooseEnterYourSiteAddress()
                       .enterSiteAddress(E2E_WP_COM_USER_SITE_ADDRESS)
                       .enterEmailAddress(E2E_WP_COM_USER_EMAIL)
                       .enterPassword(E2E_WP_COM_USER_PASSWORD)
                       .confirmLogin(false);
    }

    @Test
    public void loginWithMagicLink() {
        new LoginFlow().chooseContinueWithWpCom()
                       .enterEmailAddress(E2E_WP_COM_USER_EMAIL)
                       .chooseMagicLink()
                       .openMagicLink(mMagicLinkActivityTestRule)
                       .confirmLogin(false);
    }

    @Test
    public void loginWithSelfHostedAccount() {
        new LoginFlow().chooseEnterYourSiteAddress()
                       .enterSiteAddress(E2E_SELF_HOSTED_USER_SITE_ADDRESS)
                       .enterUsernameAndPassword(E2E_SELF_HOSTED_USER_USERNAME, E2E_SELF_HOSTED_USER_PASSWORD)
                       .confirmLogin(true);
    }

    @After
    public void tearDown() {
        logoutIfNecessary();
    }
}
