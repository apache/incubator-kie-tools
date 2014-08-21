package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.uberfire.wbtest.client.perspective.ListPerspectiveActivity;
import org.uberfire.wbtest.client.perspective.SimplePerspectiveActivity;
import org.uberfire.wbtest.client.perspective.StaticPerspectiveActivity;
import org.uberfire.wbtest.client.perspective.TabbedPerspectiveActivity;


public class WorkbenchResizeTest {

    private static final int WINDOW_HEIGHT = 700;
    private static final int WINDOW_WIDTH = 1000;

    private WebDriver driver;
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
      driver = new FirefoxDriver();
      baseUrl = "http://localhost:8080/index.html";
      driver.manage().timeouts().implicitlyWait( 30, TimeUnit.SECONDS );
      driver.manage().window().setSize( new Dimension( WINDOW_WIDTH, WINDOW_HEIGHT ) );
      driver.get( baseUrl );
    }

    @After
    public void tearDown() throws Exception {
      driver.quit();
    }

    @Test
    public void testDefaultPerspectiveSize() throws Exception {
        ResizeWidgetWrapper widgetWrapper = new ResizeWidgetWrapper( driver, "simplePerspectiveDefault" );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getReportedSize() );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getActualSize() );
    }

    @Test
    public void testSimplePerspectiveSize() throws Exception {
        // the default perspective is SimplePerspectiveActivity, so we switch away and switch back
        driver.get( baseUrl + "#" + ListPerspectiveActivity.class.getName() );

        driver.get( baseUrl + "#" + SimplePerspectiveActivity.class.getName() );

        ResizeWidgetWrapper widgetWrapper = new ResizeWidgetWrapper( driver, "simplePerspectiveDefault" );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getReportedSize() );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getActualSize() );
    }

    @Test
    public void testListPerspectiveSize() throws Exception {
        driver.get( baseUrl + "#" + ListPerspectiveActivity.class.getName() );

        ResizeWidgetWrapper widgetWrapper = new ResizeWidgetWrapper( driver, "listPerspectiveDefault" );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getReportedSize() );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getActualSize() );
    }

    @Test
    public void testTabbedPerspectiveSize() throws Exception {
        driver.get( baseUrl + "#" + TabbedPerspectiveActivity.class.getName() );

        ResizeWidgetWrapper widgetWrapper = new ResizeWidgetWrapper( driver, "tabbedPerspectiveDefault" );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getReportedSize() );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getActualSize() );
    }

    @Test
    public void testStaticPerspectiveSize() throws Exception {
        driver.get( baseUrl + "#" + StaticPerspectiveActivity.class.getName() );

        ResizeWidgetWrapper widgetWrapper = new ResizeWidgetWrapper( driver, "staticPerspectiveDefault" );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getReportedSize() );
        assertEquals( new Dimension( WINDOW_WIDTH, 20 ), widgetWrapper.getActualSize() );
    }

}
