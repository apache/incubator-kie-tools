package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.wbtest.client.dnd.DragAndDropPerspective;

/**
 * Tests for drag-and-drop movement of parts.
 */
public class DragAndDropTest extends AbstractSeleniumTest {

    @Before
    public void setUpPerspective() {
        driver.get( baseUrl + "#" + DragAndDropPerspective.class.getName() );
    }

    @Test
    public void testDragFromListPanelToSouthOfSelf() throws Exception {
        WebElement listDragHandle = driver.findElement( By.id( "gwt-debug-" + ListBarWidget.DEBUG_TITLE_PREFIX + "DnD-2" ) );

        // make sure we're grabbing the right thing
        assertEquals( "DnD-2", listDragHandle.getText() );

        // get the compass to appear
        Actions dragAndDrop = new Actions( driver );
        dragAndDrop.clickAndHold( listDragHandle );
        dragAndDrop.moveByOffset( 0, 50 );
        dragAndDrop.perform();

        // now find the south point of the compass and drop on it
        WebElement compassSouth = driver.findElement( By.id( "gwt-debug-CompassWidget-south" ) );
        dragAndDrop.click( compassSouth );
        dragAndDrop.perform();

        // The DnD-1 screen should have appeared now to take the place of DnD-2, which we have moved away
        WebElement dnd1Screen = driver.findElement( By.id( "DragAndDropScreen-1" ) );
        WebElement dnd2Screen = driver.findElement( By.id( "DragAndDropScreen-2" ) );

        // to prove it worked, we should ensure DnD-2 is south of DnD-1
        assertTrue( dnd1Screen.isDisplayed() );
        assertTrue( dnd1Screen.getLocation().y < dnd2Screen.getLocation().y );

    }
}
