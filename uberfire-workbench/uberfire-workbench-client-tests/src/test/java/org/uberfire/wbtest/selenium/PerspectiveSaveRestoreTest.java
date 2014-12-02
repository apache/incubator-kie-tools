package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;
import static org.uberfire.wbtest.selenium.UberAssertions.*;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.wbtest.client.dnd.DragAndDropPerspective;
import org.uberfire.wbtest.client.main.DefaultPerspectiveActivity;
import org.uberfire.wbtest.client.perspective.NonTransientMultiPanelPerspective;
import org.uberfire.wbtest.client.resize.ResizeTestScreenActivity;
import org.uberfire.workbench.model.CompassPosition;

/**
 * Tests for saving and restoring of perspective settings.
 */
public class PerspectiveSaveRestoreTest extends AbstractSeleniumTest {

    @Before
    public void setUpPerspective() {
        driver.get( baseUrl + "#" + NonTransientMultiPanelPerspective.class.getName() );
    }

    @Test
    public void testResizeWestPanel() throws Exception {
        WebElement splitterDragHandle = driver.findElement( By.className( "gwt-SplitLayoutPanel-HDragger" ) );

        Actions dragAndDrop = new Actions( driver );
        dragAndDrop.dragAndDropBy( splitterDragHandle, 123, 0 );
        dragAndDrop.perform();

        ResizeWidgetWrapper westScreen = new ResizeWidgetWrapper( driver, "west" );
        Dimension westScreenNewSize = westScreen.getReportedSize();
        
        driver.get( baseUrl + "#" + DefaultPerspectiveActivity.class.getName() );
        waitForDefaultPerspective();

        driver.get( baseUrl + "#" + NonTransientMultiPanelPerspective.class.getName() );
        ResizeWidgetWrapper westScreenReloaded = new ResizeWidgetWrapper( driver, "west" );
        assertEquals( westScreenNewSize, westScreenReloaded.getReportedSize() );
    }

    @Test
    public void testAddNewPanel() throws Exception {
        TopHeaderWrapper topHeader = new TopHeaderWrapper( driver );
        topHeader.addPanelToRoot( CompassPosition.SOUTH,
                                  SimpleWorkbenchPanelPresenter.class,
                                  ResizeTestScreenActivity.class,
                                  "debugId", "newSouthPanel" );

        driver.get( baseUrl + "#" + DefaultPerspectiveActivity.class.getName() );
        waitForDefaultPerspective();

        driver.get( baseUrl + "#" + NonTransientMultiPanelPerspective.class.getName() );
        ResizeWidgetWrapper newSouthScreen = new ResizeWidgetWrapper( driver, "newSouthPanel" );

        // the real test is the above line, which should throw an exception if the screen is missing
        // when we come back to the saved perspective. But it feels dumb not to assert something!
        assertNotNull( newSouthScreen );
    }
}
