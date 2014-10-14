package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.uberfire.wbtest.client.main.DefaultScreenActivity;

/**
 * All Selenium-based UberFire tests extend this base class, which provides the basic boilerplate operations of loading
 * the test app, waiting for it to initialize, and after the tests, checking for uncaught exceptions that happened while
 * it was running.
 */
public class AbstractSeleniumTest {

    static final int WINDOW_HEIGHT = 700;
    static final int WINDOW_WIDTH = 1000;

    /**
     * The WebDriver that tests should use to interact with the browser. When each test method starts, the browser will
     * be on {@link #baseUrl} and the UberFire app will be fully initialized.
     */
    protected WebDriver driver;

    /**
     * Base URL for the test app. Going to this URL will load the GWT host page. Subclasses should not change the value
     * of this field.
     */
    protected String baseUrl;

    /**
     * Tests in subclasses can set this to true to disable the check for uncaught exceptions after the test is finished.
     */
    protected boolean skipUncaughtExceptionCheck;

    /**
     * Sets up the selenium driver, loads the default perspective, and waits for its screen to appear. This lets
     * subclass {@code @Before} methods or the tests themselves navigate directly to their screen or perspective of
     * interest.
     */
    @Before
    public final void setUp() throws Exception {
      driver = new FirefoxDriver();
      baseUrl = "http://localhost:8080/index.html";
      setNormalTimeout();
      driver.manage().window().setSize( new Dimension( AbstractSeleniumTest.WINDOW_WIDTH, AbstractSeleniumTest.WINDOW_HEIGHT ) );

      driver.get( baseUrl );
      waitForDefaultPerspective();
    }

    /**
     * Causes the current test to fail if there were any uncaught exceptions thrown while it was running. Automatically
     * runs after every test, unless the test set {@link #skipUncaughtExceptionCheck} to true.
     */
    @After
    public final void detectUncaughtExceptions() {
        try {
            if ( !skipUncaughtExceptionCheck ) {
                WebElement alerterStatus = driver.findElement( By.id( "UncaughtExceptionAlerter-statusLabel" ) );
                if ( !alerterStatus.getText().equals( "0 uncaught exceptions" ) ) {
                    WebElement alerterLog = driver.findElement( By.id( "UncaughtExceptionAlerter-exceptionLog" ) );
                    fail( "Uncaught exceptions detected:\n" +
                            alerterLog.getAttribute( "value" ) +
                            "\nNote: to get Java line numbers in the stack trace, run like this:" +
                            "\n    mvn clean verify -Dgwt.style=PRETTY -Dit.test=" + getClass().getSimpleName() );
                }
            }
        } finally {
            driver.quit();
        }
    }

    /**
     * Sets the WebDriver implicit timeout to a "normal" amount (currently 30 seconds). Tests that need to set the
     * timeout shorter temporarily can call this method to set it back to normal.
     * <p>
     * Also, tests that want a different implicit timeout can override this method (it is used from this base class's
     * setUp() method).
     */
    protected void setNormalTimeout() {
        driver.manage().timeouts().implicitlyWait( 30, TimeUnit.SECONDS );
    }

    /**
     * Blocks execution of the test until the {@link DefaultScreenActivity} is in the DOM. This is normally done
     * automatically in the setup method, but tests that refresh the page (or go back to the default perspective) will
     * need to call this explicitly.
     */
    protected void waitForDefaultPerspective() {
        driver.findElement( By.id( "gwt-debug-" + DefaultScreenActivity.DEBUG_ID ) );
    }

}
