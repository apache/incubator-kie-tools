/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.docks.view.items;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class SideDockItemTest {

    @Mock
    private ImageResource imageResource, imageResourceFocused;

    @Mock
    private PlaceRequest placeRequest;

    private ParameterizedCommand<String> emptyCommand;

    private UberfireDock dockWithFontIcon, dockWithImageIcon;

    private SideDockItem sideDockWithFontIcon, sideDockWithImageIcon;

    @Before
    public void setup() {
        dockWithFontIcon = new UberfireDock(UberfireDockPosition.EAST,
                                            "BRIEFCASE",
                                            placeRequest,
                                            "")
                .withSize(450)
                .withLabel("dock");
        dockWithImageIcon = new UberfireDock(UberfireDockPosition.EAST,
                                             imageResource,
                                             imageResourceFocused,
                                             placeRequest,
                                             "")
                .withSize(450)
                .withLabel("dock");

        sideDockWithFontIcon = spy(new SideDockItem(dockWithFontIcon,
                                                    emptyCommand,
                                                    emptyCommand));
        sideDockWithImageIcon = spy(new SideDockItem(dockWithImageIcon,
                                                     emptyCommand,
                                                     emptyCommand));

        emptyCommand = parameter -> {
        };
    }

    @Test
    public void createSideDockItemWithFontIconTest() {
        sideDockWithFontIcon.createButton();

        verify(sideDockWithFontIcon).configureIcon(any(Button.class),
                                                   eq((ImageResource) null));
        verify(sideDockWithFontIcon,
               never()).configureImageIcon(any(Button.class),
                                           any(ImageResource.class));
    }

    @Test
    public void openSideDockItemWithFontIconTest() {
        sideDockWithFontIcon.open();

        verify(sideDockWithFontIcon,
               never()).configureImageIcon(any(Button.class),
                                           any(ImageResource.class));
    }

    @Test
    public void closeSideDockItemWithFontIconTest() {
        sideDockWithFontIcon.close();

        verify(sideDockWithFontIcon,
               never()).configureImageIcon(any(Button.class),
                                           any(ImageResource.class));
    }

    @Test
    public void createSideDockItemWithImageIconTest() {
        sideDockWithImageIcon.createButton();

        verify(sideDockWithImageIcon).configureIcon(any(Button.class),
                                                    eq(imageResource));
        verify(sideDockWithImageIcon).configureImageIcon(any(Button.class),
                                                         eq(imageResource));
    }

    @Test
    public void openSouthDockItemWithImageIconTest() {
        sideDockWithImageIcon.open();

        verify(sideDockWithImageIcon).configureImageIcon(any(Button.class),
                                                         eq(imageResourceFocused));
    }

    @Test
    public void closeSouthDockItemWithImageIconTest() {
        sideDockWithImageIcon.close();

        verify(sideDockWithImageIcon).configureImageIcon(any(Button.class),

                                                         eq(imageResource));
    }

    @Test
    public void createSideDockItemWithTooltipTest() {
        final String dock_screenID = "SCREEN_ID";
        final String dock_label = "DOCK TITLE";
        final String dock_tooltip = "DOCK TOOLTIP";

        UberfireDock dock1 = new UberfireDock(UberfireDockPosition.EAST,
                                              "BRIEFCASE",
                                              placeRequest,
                                              "")
                .withLabel(dock_label)
                .withTooltip(dock_tooltip);
        SideDockItem tested1 = spy(new SideDockItem(dock1, emptyCommand, emptyCommand));
        tested1.createButton();
        verify(tested1).configureTooltip(any(Tooltip.class), eq(dock_tooltip));

        UberfireDock dock2 = new UberfireDock(UberfireDockPosition.EAST,
                                              "BRIEFCASE",
                                              placeRequest,
                                              "")
                .withLabel(dock_label);
        SideDockItem tested2 = spy(new SideDockItem(dock2, emptyCommand, emptyCommand));
        tested2.createButton();
        verify(tested2).configureTooltip(any(Tooltip.class), eq(dock_label));

        UberfireDock dock3 = new UberfireDock(UberfireDockPosition.EAST,
                                              "BRIEFCASE",
                                              new DefaultPlaceRequest(dock_screenID),
                                              "");
        SideDockItem tested3 = spy(new SideDockItem(dock3, emptyCommand, emptyCommand));
        tested3.createButton();
        verify(tested3).configureTooltip(any(Tooltip.class), eq(dock_screenID));

        Tooltip tooltip = new Tooltip();
        sideDockWithImageIcon.configureTooltip(tooltip, sideDockWithImageIcon.getLabel());
        assertEquals(sideDockWithImageIcon.getLabel(), tooltip.getTitle());
        assertEquals(Placement.LEFT, tooltip.getPlacement());
    }
}
