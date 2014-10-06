package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.uberfire.wbtest.client.main.DefaultPerspectiveActivity;
import org.uberfire.wbtest.client.splash.SplashyPerspective;
import org.uberfire.wbtest.client.splash.SplashyPerspectiveSplashScreen;
import org.uberfire.wbtest.client.splash.SplashyScreen;
import org.uberfire.wbtest.client.splash.SplashyScreenSplashScreen;

/**
 * Tests for various scenarios involving splash screens.
 */
public class SplashScreenTest extends AbstractSeleniumTest {

    @Test
    public void splashScreenShouldShowEveryTimeWhenNavigatingToInterceptedPerspective() throws Exception {

        // first visit to splashy perspective
        driver.get( baseUrl + "#" + SplashyPerspective.class.getName() + "?debugId=1" );
        WebElement splashLabel = driver.findElement( By.id( "SplashyPerspectiveSplashScreen-1" ) );
        waitUntilDisplayed( 5000, splashLabel );
        driver.findElement( By.id("gwt-debug-SplashModalFooter-close") ).click();
        waitUntilGone( 5000, By.id( "SplashyPerspectiveSplashScreen-1" ) );

        // leave splashy perspective
        driver.get( baseUrl + "#" + DefaultPerspectiveActivity.class.getName() );
        waitForDefaultPerspective();

        // second visit to splashy perspective (same assertions again)
        driver.get( baseUrl + "#" + SplashyPerspective.class.getName() + "?debugId=2" );
        splashLabel = driver.findElement( By.id( "SplashyPerspectiveSplashScreen-2" ) );
        waitUntilDisplayed( 5000, splashLabel );
        driver.findElement( By.id("gwt-debug-SplashModalFooter-close") ).click();
        waitUntilGone( 5000, By.id( "SplashyPerspectiveSplashScreen-2" ) );
    }

    @Test
    public void splashScreenShouldNotShowAgainAfterPreferenceChanged() throws Exception {

        // first visit to splashy perspective, where we say "don't show again!"
        driver.get( baseUrl + "#" + SplashyPerspective.class.getName() + "?debugId=1" );
        WebElement splashLabel = driver.findElement( By.id( "SplashyPerspectiveSplashScreen-1" ) );
        waitUntilDisplayed( 5000, splashLabel );
        driver.findElement( By.id("gwt-debug-SplashModalFooter-dontShowAgain-input") ).click();
        driver.findElement( By.id("gwt-debug-SplashModalFooter-close") ).click();
        waitUntilGone( 5000, By.id( "SplashyPerspectiveSplashScreen-1" ) );

        // leave splashy perspective
        driver.get( baseUrl + "#" + DefaultPerspectiveActivity.class.getName() );
        waitForDefaultPerspective();

        // second visit to splashy perspective should not produce a splash screen
        driver.get( baseUrl + "#" + SplashyPerspective.class.getName() + "?debugId=2" );
        driver.manage().timeouts().implicitlyWait( 5000, TimeUnit.MILLISECONDS );
        List<WebElement> splashLabels = driver.findElements( By.id( "SplashyPerspectiveSplashScreen-2" ) );
        assertEquals( 0, splashLabels.size() );
    }

    @Test
    public void splashListShouldStayUpToDate() throws Exception {

        // go to perspective and dismiss its splash screen
        driver.get( baseUrl + "#" + SplashyPerspective.class.getName() + "?debugId=1" );
        WebElement perspectiveSplashLabel = driver.findElement( By.id( "SplashyPerspectiveSplashScreen-1" ) );
        waitUntilDisplayed( 5000, perspectiveSplashLabel );
        driver.findElement( By.id( "gwt-debug-SplashModalFooter-close" ) ).click();
        waitUntilGone( 5000, By.id( "SplashyPerspectiveSplashScreen-1" ) );

        // add another screen that has its own splash screen, and dismiss that too
        driver.get( baseUrl + "#" + SplashyScreen.class.getName() + "?debugId=1" );
        WebElement screenSplashLabel = driver.findElement( By.id( "SplashyScreenSplashScreen-1" ) );
        waitUntilDisplayed( 5000, screenSplashLabel );
        driver.findElement( By.id( "gwt-debug-SplashModalFooter-close" ) ).click();
        waitUntilGone( 5000, By.id( "SplashyScreenSplashScreen-1" ) );

        // now verify both splash screens are in the list
        SplashListWrapper splashList = new SplashListWrapper( driver, "SplashyScreen-1-SplashList" );
        List<String> contents = splashList.getContents();
        assertTrue( contents.contains( SplashyPerspectiveSplashScreen.class.getName() ) );
        assertTrue( contents.contains( SplashyScreenSplashScreen.class.getName() ) );
        assertEquals( 2, contents.size() );
    }

    /**
     * Polls the given element until it is displayed by the browser. This is needed because splash screens have animated
     * transitions and they aren't visible right away.
     * <p>
     * If the element still isn't visible after {@code timeoutMillis} has elapsed, this method will cause a test failure.
     *
     * @param timeoutMillis
     *            amount of time to poll for
     * @param checkMe
     *            the element to check
     * @throws InterruptedException
     *             if the thread is interrupted while sleeping between polling checks
     */
    protected void waitUntilDisplayed( int timeoutMillis, WebElement checkMe ) throws InterruptedException {
        final long deadline = System.currentTimeMillis() + timeoutMillis;
        while ( System.currentTimeMillis() < deadline ) {
            if ( checkMe.isDisplayed() ) {
                return;
            }
            Thread.sleep( 500 );
        }
        fail( "Element still not visible after " + timeoutMillis + "ms: " + checkMe );
    }

    private void waitUntilGone( int timeoutMillis, By findBy ) throws InterruptedException {
        try {
            driver.manage().timeouts().implicitlyWait( 100, TimeUnit.MILLISECONDS );
            final long deadline = System.currentTimeMillis() + timeoutMillis;
            while ( System.currentTimeMillis() < deadline ) {
                if ( driver.findElements( findBy ).size() == 0 ) {
                    return;
                }
                Thread.sleep( 500 );
            }
            fail( "Element still present after " + timeoutMillis + "ms" );
        } finally {
            setNormalTimeout();
        }
    }
}
