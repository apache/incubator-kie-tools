package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.uberfire.wbtest.client.panels.custom.CustomPanelMakerScreen;


public class CustomPanelTest extends AbstractSeleniumTest {

    private CustomPanelScreenWrapper screen;

    @Before
    public void setupScreenObject() {
        driver.get( baseUrl );
        driver.get( baseUrl + "#" + CustomPanelMakerScreen.class.getName() );
        screen = new CustomPanelScreenWrapper( driver );
    }

    @Test
    public void activitiesLaunchedInCustomPanelsShouldAppear() throws Exception {
        String id = screen.createCustomPopup();
        assertTrue( screen.customPopupExistsInDom( id ) );
        assertEquals( 1, screen.getLiveInstanceCount() );
        assertEquals( 1, screen.getTotalInstanceCount() );
    }

    @Test
    public void activitiesLaunchedInCustomPanelsShouldDisposeWhenPlaceClosed() throws Exception {
        String id = screen.createCustomPopup();
        screen.closeLatestPopupUsingPlaceManager();

        driver.manage().timeouts().implicitlyWait( 1, TimeUnit.SECONDS );
        assertFalse( screen.customPopupExistsInDom( id ) );
        assertEquals( 0, screen.getLiveInstanceCount() );
        assertEquals( 1, screen.getTotalInstanceCount() );
    }

    @Test @Ignore // TODO implement dom removal cleanup logic
    public void activitiesLaunchedInCustomPanelsShouldDisposeWhenRemovedFromDom() throws Exception {
        String id = screen.createCustomPopup();
        screen.closeLatestPopupByRemovingFromDom();

        driver.manage().timeouts().implicitlyWait( 1, TimeUnit.SECONDS );
        assertFalse( screen.customPopupExistsInDom( id ) );
        assertEquals( 0, screen.getLiveInstanceCount() );
        assertEquals( 1, screen.getTotalInstanceCount() );
    }

}
