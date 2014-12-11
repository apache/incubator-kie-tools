package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.uberfire.wbtest.client.panels.docking.NestingScreen;
import org.uberfire.wbtest.client.perspective.TabbedPerspectiveActivity;


public class TabPanelTest extends AbstractSeleniumTest {

    private MultiTabPanelWrapper tabPanel;

    @Before
    public void setup() {
        driver.get( baseUrl + "#" + TabbedPerspectiveActivity.class.getName() );
        waitForTabbedPerspective();

        tabPanel = new MultiTabPanelWrapper( driver, TabbedPerspectiveActivity.TABBED_PANEL_ID );

        // close the screen that appeared by default with the tabbed perspective
        // (this also tests that the last/only tab can be closed properly)
        driver.findElement( By.cssSelector( "button.close" ) ).click();

        driver.manage().timeouts().implicitlyWait( 1, TimeUnit.SECONDS );
    }

    @Test
    public void extraTabsShouldOverflowIntoDropdownWhenCreated() throws Exception {
        makeOrSelectTab( "one tab with long name" );
        makeOrSelectTab( "two tab with long name" );
        makeOrSelectTab( "three tab with long name" );
        makeOrSelectTab( "four tab with long name" );
        makeOrSelectTab( "five tab with long name" );
        makeOrSelectTab( "six tab with long name" );
        makeOrSelectTab( "seven tab with long name" );
        makeOrSelectTab( "eight tab with long name" );
        makeOrSelectTab( "nine tab with long name" );
        makeOrSelectTab( "ten tab with long name" );

        int nonNestedTabCount = tabPanel.getNonNestedTabs().size();
        int nestedTabCount = tabPanel.getNestedTabs().size();

        System.out.println("extraTabsShouldOverflowIntoDropdown found " + nonNestedTabCount + " regular and " + nestedTabCount + " nested tabs");
        // we added enough tabs that some of them should have overflowed into the dropdown
        assertTrue( nestedTabCount > 0 );

        // there should be 11 tabs in total (one of them is the dropdown tab)
        assertEquals( 11, nonNestedTabCount + nestedTabCount );
    }

    @Test
    public void tabDisplayShouldBeRepeatableAtAnyWindowSize() throws Exception {
        makeOrSelectTab( "one" );
        makeOrSelectTab( "two" );
        makeOrSelectTab( "three" );
        makeOrSelectTab( "four" );
        makeOrSelectTab( "five" );
        makeOrSelectTab( "six" );
        makeOrSelectTab( "seven" );
        makeOrSelectTab( "eight" );

        // we'll verify that this tab remains selected even as it's moved in and out of the dropdown
        makeOrSelectTab( "six" );

        class WindowSizeInfo {
            int windowWidth;
            int nonNestedTabs;
            int nestedTabs;

            @Override
            public String toString() {
                return "WindowSizeInfo [windowWidth=" + windowWidth + ", nonNestedTabs=" + nonNestedTabs + ", nestedTabs=" + nestedTabs + "]";
            }
        }
        List<WindowSizeInfo> windowSizeInfos = new ArrayList<WindowSizeInfo>();

        // set the window to various sizes and remember how many of each type of tab was displayed

        // WARNING: if we make the window too narrow, the footer will squish up and get taller, interfering with
        // visibility of the items in the dropdown tab. This will cause false failures on the assertion that "six"
        // is still visible (selenium only returns visible text)
        for ( int width = 200; width <= WINDOW_WIDTH; width += 100 ) {
            driver.manage().window().setSize( new Dimension( width, WINDOW_HEIGHT ) );
            WindowSizeInfo info = new WindowSizeInfo();
            info.windowWidth = width;
            info.nonNestedTabs = tabPanel.getNonNestedTabs().size();
            info.nestedTabs = tabPanel.getNestedTabs().size();
            windowSizeInfos.add( info );

            // ensure selected tab has not changed due to resize
            assertEquals( "six", tabPanel.getSelectedTabLabel() );
        }

        // now revisit the various window sizes and verify we get the same result
        Collections.shuffle( windowSizeInfos );
        for ( WindowSizeInfo info : windowSizeInfos ) {
            driver.manage().window().setSize( new Dimension( info.windowWidth, WINDOW_HEIGHT ) );
            assertEquals( info.nonNestedTabs, tabPanel.getNonNestedTabs().size() );
            assertEquals( info.nestedTabs, tabPanel.getNestedTabs().size() );

            // ensure selected tab has not changed due to resize
            assertEquals( "six", tabPanel.getSelectedTabLabel() );
        }
    }

    @Test
    public void shouldBeAbleToSelectNonNestedTab() throws Exception {
        makeOrSelectTab( "one" );
        makeOrSelectTab( "two" );

        // this includes an implicit check that the "one" screen becomes visible
        makeOrSelectTab( "one" );

        // finally, make sure we didn't end up with a 3rd tab
        assertEquals( 2, tabPanel.getNonNestedTabs().size() );
    }

    @Test
    public void shouldBeAbleToSelectNestedTab() throws Exception {
        makeOrSelectTab( "one tab with long name" );
        makeOrSelectTab( "two tab with long name" );
        makeOrSelectTab( "three tab with long name" );
        makeOrSelectTab( "four tab with long name" );
        makeOrSelectTab( "five tab with long name" );
        makeOrSelectTab( "six tab with long name" );
        makeOrSelectTab( "seven tab with long name" );
        makeOrSelectTab( "eight tab with long name" );
        makeOrSelectTab( "nine tab with long name" );
        makeOrSelectTab( "ten tab with long name" );

        // this includes an implicit check that the "eight tab with long name" screen becomes visible
        makeOrSelectTab( "eight tab with long name" );

        assertTrue( tabPanel.getDropdownTabLabel().contains( "eight tab with long name" ) );

        // finally, make sure we didn't end up with an 11th tab (remember, one of these tabs is the dropdown tab)
        assertEquals( 11, tabPanel.getNestedTabs().size() + tabPanel.getNonNestedTabs().size() );
    }

    @Test
    public void nearbyTabShouldGetSelectedWhenActiveTabIsClosed() throws Exception {
        makeOrSelectTab( "one" );
        makeOrSelectTab( "two" );
        makeOrSelectTab( "three" );
        makeOrSelectTab( "four" );
        makeOrSelectTab( "five" );
        makeOrSelectTab( "six" );

        // close last tab
        assertEquals( "six", tabPanel.getSelectedTabLabel() );
        tabPanel.closeSelectedTab();
        assertEquals( "five", tabPanel.getSelectedTabLabel() );

        // close a middle tab
        makeOrSelectTab( "three" );
        tabPanel.closeSelectedTab();
        assertEquals( "two", tabPanel.getSelectedTabLabel() );

        // close first tab
        makeOrSelectTab( "one" );
        tabPanel.closeSelectedTab();
        assertEquals( "two", tabPanel.getSelectedTabLabel() );
    }

    @Test
    public void selectedTabShouldStaySelectedWhenOtherTabIsClosed() throws Exception {
        makeOrSelectTab( "one" );
        makeOrSelectTab( "two" );
        makeOrSelectTab( "three" );
        makeOrSelectTab( "four" );

        makeOrSelectTab( "three" );

        tabPanel.closeTab( "one" );
        assertEquals( "three", tabPanel.getSelectedTabLabel() );

        tabPanel.closeTab( "four" );
        assertEquals( "three", tabPanel.getSelectedTabLabel() );

        tabPanel.closeTab( "two" );
        assertEquals( "three", tabPanel.getSelectedTabLabel() );

        assertEquals( 1, tabPanel.getNonNestedTabs().size() );
    }

    private void waitForTabbedPerspective() {
        driver.findElement( By.cssSelector( "ul.nav-tabs" ) );
    }

    /**
     * Creates a new NestingScreen with the given ID or selects the one that already exists, and waits for its view
     * content to appear. Causes the test to fail if the screen's UI isn't visible within 5 seconds.
     *
     * @param id
     *            the place ID to give the screen. This will be the label text on the tab, as well as the text that
     *            appears in a GWT label in its content pane.
     */
    private void makeOrSelectTab( String id ) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        driver.get( baseUrl + "#" + NestingScreen.class.getName() + "?place=" + id );
        for ( int i = 0; i < 100; i++ ) {
            for ( WebElement div : driver.findElements( By.cssSelector( "div.gwt-Label" ) ) ) {
                if ( div.getText().contains( id ) ) {
                    assertTrue( div.isDisplayed() );
                    return;
                }
            }
            Thread.sleep( 50 );
        }
        fail( "Screen content for " + id + " did not appear after " + ( System.currentTimeMillis() - startTime ) + "ms" );
    }
}
