package org.uberfire.wbtest.selenium;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class AbstractSeleniumTest {

    static final int WINDOW_HEIGHT = 700;
    static final int WINDOW_WIDTH = 1000;

    protected WebDriver driver;
    protected String baseUrl;

    @Before
    public void setUp() throws Exception {
      driver = new FirefoxDriver();
      baseUrl = "http://localhost:8080/index.html";
      driver.manage().timeouts().implicitlyWait( 30, TimeUnit.SECONDS );
      driver.manage().window().setSize( new Dimension( AbstractSeleniumTest.WINDOW_WIDTH, AbstractSeleniumTest.WINDOW_HEIGHT ) );
    }

    @After
    public void tearDown() throws Exception {
      driver.quit();
    }

}
