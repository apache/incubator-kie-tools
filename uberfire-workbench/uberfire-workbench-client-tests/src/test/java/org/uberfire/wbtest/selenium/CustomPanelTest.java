/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.selenium;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.wbtest.client.panels.custom.CustomPanelMakerScreen;
import org.uberfire.wbtest.client.perspective.ListPerspectiveActivity;

import static org.junit.Assert.*;

public class CustomPanelTest extends AbstractSeleniumTest {

    private CustomPanelScreenWrapper screen;

    @Before
    public void setupScreenObject() {
        driver.get(baseUrl + "#" + CustomPanelMakerScreen.class.getName());
        screen = new CustomPanelScreenWrapper(driver);
    }

    @Test
    public void activitiesLaunchedInCustomPanelsShouldAppear() throws Exception {
        String id = screen.createNewCustomPopup();
        assertTrue(screen.customPopupExistsInDom(id));
        assertEquals(1,
                     screen.getLiveInstanceCount());
        assertEquals(1,
                     screen.getTotalInstanceCount());
    }

    @Test
    public void activitiesLaunchedInCustomPanelsShouldDisposeWhenPlaceClosed() throws Exception {
        String id = screen.createNewCustomPopup();
        screen.closeLatestNewPopupUsingPlaceManager();

        driver.manage().timeouts().implicitlyWait(1,
                                                  TimeUnit.SECONDS);
        assertFalse(screen.customPopupExistsInDom(id));
        assertEquals(0,
                     screen.getLiveInstanceCount());
        assertEquals(1,
                     screen.getTotalInstanceCount());
    }

    @Test
    public void activitiesLaunchedInCustomPanelsShouldDisposeWhenRemovedFromDom() throws Exception {
        String id = screen.createNewCustomPopup();
        screen.closeLatestNewPopupByRemovingFromDom();

        driver.manage().timeouts().implicitlyWait(1,
                                                  TimeUnit.SECONDS);
        assertFalse(screen.customPopupExistsInDom(id));
        assertEquals(0,
                     screen.getLiveInstanceCount());
        assertEquals(1,
                     screen.getTotalInstanceCount());
    }

    @Test
    public void liveCustomPanelsShouldNotBreakPerspectiveSwitching() throws Exception {
        screen.createNewCustomPopup();

        driver.get(baseUrl + "#" + ListPerspectiveActivity.class.getName());

        // if this times out, the perspective switch failed
        driver.manage().timeouts().implicitlyWait(2,
                                                  TimeUnit.SECONDS);
        new ResizeWidgetWrapper(driver,
                                "listPerspectiveDefault").find();

        // TODO activity lifecycle bulletproofing (coming soon!) will defeat this test.
        // all tests should check for errors in their teardown (this should be done in the abstract UF test class)
    }

    @Test
    public void removedCustomPanelsShouldNotBreakPerspectiveSwitching() throws Exception {
        screen.createNewCustomPopup();
        screen.closeLatestNewPopupUsingPlaceManager();

        driver.get(baseUrl + "#" + ListPerspectiveActivity.class.getName());

        // if this times out, the perspective switch failed
        driver.manage().timeouts().implicitlyWait(2,
                                                  TimeUnit.SECONDS);
        new ResizeWidgetWrapper(driver,
                                "listPerspectiveDefault").find();

        // TODO activity lifecycle bulletproofing (coming soon!) will defeat this test.
        // all tests should check for errors in their teardown (this should be done in the abstract UF test class)
    }

    @Test
    public void customPanelContainersShouldBeReusable() throws Exception {
        String id = screen.createReusableCustomPopup();
        assertTrue(screen.customPopupExistsInDom(id));
        assertEquals(1,
                     screen.getLiveInstanceCount());
        assertEquals(1,
                     screen.getTotalInstanceCount());

        screen.closeReusablePopupUsingPlaceManager();

        driver.manage().timeouts().implicitlyWait(1,
                                                  TimeUnit.SECONDS);
        assertFalse(screen.customPopupExistsInDom(id));
        setNormalTimeout();
        assertEquals(0,
                     screen.getLiveInstanceCount());
        assertEquals(1,
                     screen.getTotalInstanceCount());

        id = screen.createReusableCustomPopup();
        assertTrue(screen.customPopupExistsInDom(id));
        assertEquals(1,
                     screen.getLiveInstanceCount());
        assertEquals(2,
                     screen.getTotalInstanceCount());
    }
}
