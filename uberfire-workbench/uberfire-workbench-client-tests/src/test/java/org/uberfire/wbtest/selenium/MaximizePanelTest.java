package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;
import static org.uberfire.wbtest.selenium.SeleniumConditions.*;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.uberfire.wbtest.client.panels.maximize.MaximizeTestPerspective;


public class MaximizePanelTest extends AbstractSeleniumTest {

    private MultiTabPanelWrapper tabPanel;
    private MultiListPanelWrapper listPanel;
    private SimplePanelWrapper simplePanel;

    @Before
    public void setup() {
        driver.get( baseUrl + "#" + MaximizeTestPerspective.class.getName() );

        tabPanel = new MultiTabPanelWrapper( driver, MaximizeTestPerspective.TAB_PANEL_ID );
        listPanel = new MultiListPanelWrapper( driver, MaximizeTestPerspective.LIST_PANEL_ID );
        simplePanel = new SimplePanelWrapper( driver, MaximizeTestPerspective.SIMPLE_PANEL_ID );

        driver.manage().timeouts().implicitlyWait( 1, TimeUnit.SECONDS );
    }

    @Test
    public void maximizeButtonShouldWorkOnTabbedPanel() throws Exception {
        MaximizeTestScreenWrapper tabPanelScreen4 =
                new MaximizeTestScreenWrapper( driver,
                                               MaximizeTestPerspective.TAB_PANEL_SCREEN_4_ID );
        Dimension reportedSizeBefore = tabPanelScreen4.getReportedSize();

        tabPanel.clickMaximizeButton();

        Dimension reportedSizeAfter = tabPanelScreen4.getReportedSize();
        assertBigger( reportedSizeBefore, reportedSizeAfter );
        assertObscuredBy( tabPanel, listPanel );
        assertObscuredBy( tabPanel, simplePanel );
    }

    @Test
    public void maximizeButtonShouldWorkOnListPanel() throws Exception {
        MaximizeTestScreenWrapper listPanelScreen2 =
                new MaximizeTestScreenWrapper( driver,
                                               MaximizeTestPerspective.LIST_PANEL_SCREEN_2_ID );
        Dimension reportedSizeBefore = listPanelScreen2.getReportedSize();

        listPanel.clickMaximizeButton();

        Dimension reportedSizeAfter = listPanelScreen2.getReportedSize();
        assertBigger( reportedSizeBefore, reportedSizeAfter );
        assertObscuredBy( listPanel, tabPanel );
        assertObscuredBy( listPanel, simplePanel );
    }

    @Test
    public void maximizeButtonShouldWorkOnSimplePanel() throws Exception {
        MaximizeTestScreenWrapper simplePanelScreen5 =
                new MaximizeTestScreenWrapper( driver,
                                               MaximizeTestPerspective.SIMPLE_PANEL_SCREEN_5_ID );
        Dimension reportedSizeBefore = simplePanelScreen5.getReportedSize();

        simplePanel.clickMaximizeButton();

        Thread.sleep( 3000 );
        Dimension reportedSizeAfter = simplePanelScreen5.getReportedSize();
        assertBigger( reportedSizeBefore, reportedSizeAfter );
        assertObscuredBy( simplePanel, tabPanel );
        assertObscuredBy( simplePanel, listPanel );
    }

    @Test
    public void maximizedTabPanelShouldTrackWindowSize() throws Exception {
        MaximizeTestScreenWrapper tabPanelScreen4 =
                new MaximizeTestScreenWrapper( driver,
                                               MaximizeTestPerspective.TAB_PANEL_SCREEN_4_ID );

        tabPanel.clickMaximizeButton();

        Dimension originalMaximizedSize = tabPanelScreen4.getReportedSize();
        driver.manage().window().setSize( new Dimension( WINDOW_WIDTH + 50, WINDOW_HEIGHT - 40 ) );
        new WebDriverWait( driver, 5 )
                .until( reportedSizeIs( tabPanelScreen4,
                                        new Dimension( originalMaximizedSize.width + 50,
                                                       originalMaximizedSize.height - 40 ) ) );
    }

    @Test
    public void maximizedListPanelShouldTrackWindowSize() throws Exception {
        MaximizeTestScreenWrapper listPanelScreen2 =
                new MaximizeTestScreenWrapper( driver,
                                               MaximizeTestPerspective.LIST_PANEL_SCREEN_2_ID );

        listPanel.clickMaximizeButton();

        Dimension originalMaximizedSize = listPanelScreen2.getReportedSize();
        driver.manage().window().setSize( new Dimension( WINDOW_WIDTH + 50, WINDOW_HEIGHT - 40 ) );
        new WebDriverWait( driver, 5 )
                .until( reportedSizeIs( listPanelScreen2,
                                        new Dimension( originalMaximizedSize.width + 50,
                                                       originalMaximizedSize.height - 40 ) ) );
    }

    /**
     * Asserts that {@code after} is bigger in both dimensions than {@code before}.
     */
    private void assertBigger( Dimension before,
                               Dimension after ) {
        Assert.assertTrue( "Element did not grow in width. before=" + before + ", after=" + after,
                           before.width < after.width );
        Assert.assertTrue( "Element did not grow in height. before=" + before + ", after=" + after,
                           before.height < after.height );
    }

    private void assertObscuredBy( AbstractWorkbenchPanelWrapper big, AbstractWorkbenchPanelWrapper little ) {
        assertTrue( "Smaller panel " + little.getLocation() + " " + little.getSize() +
                    " is not obscured by " + big.getLocation() + big.getSize(),
                    little.isObscuredBy( big ) );
    }
}
