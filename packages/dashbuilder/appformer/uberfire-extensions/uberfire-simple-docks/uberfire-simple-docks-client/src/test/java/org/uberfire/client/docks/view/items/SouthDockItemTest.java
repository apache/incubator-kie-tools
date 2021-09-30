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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class SouthDockItemTest {

    @Mock
    private ImageResource imageResource, imageResourceFocused;

    @Mock
    private PlaceRequest placeRequest;

    private ParameterizedCommand<String> emptyCommand;

    private UberfireDock dockWithFontIcon, dockWithImageIcon;

    private SouthDockItem southDockWithFontIcon, southDockWithImageIcon;

    @Before
    public void setup() {
        dockWithFontIcon = new UberfireDock(UberfireDockPosition.EAST,
                                            "BRIEFCASE",
                                            placeRequest,
                                            "").withSize(450).withLabel("dock");
        dockWithImageIcon = new UberfireDock(UberfireDockPosition.EAST,
                                             imageResource,
                                             imageResourceFocused,
                                             placeRequest,
                                             "").withSize(450).withLabel("dock");

        southDockWithFontIcon = spy(new SouthDockItem(dockWithFontIcon,
                                                      emptyCommand,
                                                      emptyCommand));
        southDockWithImageIcon = spy(new SouthDockItem(dockWithImageIcon,
                                                       emptyCommand,
                                                       emptyCommand));

        emptyCommand = parameter -> {

        };
    }

    @Test
    public void createSouthDockItemWithFontIconTest() {
        southDockWithFontIcon.createButton();

        InOrder ordenatedVerification = inOrder(southDockWithFontIcon);
        ordenatedVerification.verify(southDockWithFontIcon).configureText(any(Button.class),
                                                                          anyString());
        ordenatedVerification.verify(southDockWithFontIcon).configureIcon(any(Button.class),
                                                                          eq((ImageResource) null));
        ordenatedVerification.verify(southDockWithFontIcon,
                                     never()).configureImageIcon(any(Button.class),
                                                                 any(ImageResource.class));
    }

    @Test
    public void selectSouthDockItemWithFontIconTest() {
        southDockWithFontIcon.open();

        verify(southDockWithFontIcon,
               never()).configureImageIcon(any(Button.class),
                                           any(ImageResource.class));
    }

    @Test
    public void closeSouthDockItemWithFontIconTest() {
        southDockWithFontIcon.close();

        verify(southDockWithFontIcon,
               never()).configureImageIcon(any(Button.class),
                                           any(ImageResource.class));
    }

    @Test
    public void createSouthDockItemWithImageIconTest() {
        southDockWithImageIcon.createButton();

        verify(southDockWithImageIcon).configureIcon(any(Button.class),
                                                     eq(imageResource));
        verify(southDockWithImageIcon).configureImageIcon(any(Button.class),
                                                          eq(imageResource));
    }

    @Test
    public void openSouthDockItemWithImageIconTest() {
        southDockWithImageIcon.open();

        verify(southDockWithImageIcon).configureImageIcon(any(Button.class),
                                                          eq(imageResourceFocused));
    }

    @Test
    public void closeSouthDockItemWithImageIconTest() {
        southDockWithImageIcon.close();

        verify(southDockWithImageIcon).configureImageIcon(any(Button.class),
                                                          eq(imageResource));
    }
}
