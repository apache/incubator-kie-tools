/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.docks.navigator;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock.DOCK_SIZE;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionNavigatorPresenter_DecisionNavigator;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorDockTest {

    @Mock
    private UberfireDocks uberfireDocks;

    @Mock
    private DecisionNavigatorPresenter decisionNavigatorPresenter;

    @Mock
    private TranslationService translationService;

    private DecisionNavigatorDock dock;

    @Before
    public void setup() {
        dock = spy(new DecisionNavigatorDock(uberfireDocks,
                                             decisionNavigatorPresenter,
                                             translationService));
    }

    @Test
    public void testInit() {
        final UberfireDock expectedUberfireDock = mock(UberfireDock.class);

        doReturn(expectedUberfireDock).when(dock).makeUberfireDock();

        dock.init();

        final UberfireDock actualUberfireDock = dock.getUberfireDock();

        assertEquals(expectedUberfireDock, actualUberfireDock);

        verify(uberfireDocks).add(expectedUberfireDock);
        verify(uberfireDocks).show(UberfireDockPosition.EAST);
    }

    @Test
    public void testInitNotNullUberfireDock() {
        final UberfireDock expectedUberfireDock = mock(UberfireDock.class);

        doReturn(expectedUberfireDock).when(dock).makeUberfireDock();
        dock.uberfireDock = expectedUberfireDock;

        dock.init();

        final UberfireDock actualUberfireDock = dock.getUberfireDock();

        assertEquals(expectedUberfireDock, actualUberfireDock);

        verify(uberfireDocks, never()).add(any());
        verify(uberfireDocks, never()).show(any());
    }

    @Test
    public void testSetupCanvasHandler() {
        dock.reload();
        verify(decisionNavigatorPresenter).refresh();
    }

    @Test
    public void testResetContent() {

        dock.resetContent();

        verify(decisionNavigatorPresenter).removeAllElements();
    }

    @Test
    public void testOpenWhenItIsOpened() {

        final UberfireDockPosition position = UberfireDockPosition.EAST;

        dock.setOpened(true);
        doReturn(position).when(dock).position();

        dock.open();

        assertTrue(dock.isOpened());
        verify(uberfireDocks, never()).open(any());
    }

    @Test
    public void testOpenWhenItIsNotOpened() {

        final UberfireDock uberfireDock = mock(UberfireDock.class);

        dock.setOpened(false);
        doReturn(uberfireDock).when(dock).getUberfireDock();

        dock.open();

        assertTrue(dock.isOpened());
        verify(uberfireDocks).open(uberfireDock);
    }

    @Test
    public void testCloseWhenItIsOpened() {

        final UberfireDock uberfireDock = mock(UberfireDock.class);

        dock.setOpened(true);
        doReturn(uberfireDock).when(dock).getUberfireDock();

        dock.close();

        assertFalse(dock.isOpened());
        verify(uberfireDocks).close(uberfireDock);
    }

    @Test
    public void testCloseWhenItIsNotOpened() {

        final UberfireDock uberfireDock = mock(UberfireDock.class);

        dock.setOpened(false);
        doReturn(uberfireDock).when(dock).getUberfireDock();

        dock.close();

        assertFalse(dock.isOpened());
        verify(uberfireDocks, never()).close(uberfireDock);
    }

    @Test
    public void testDestroyNullUberfireDock() {
        final UberfireDock uberfireDock = mock(UberfireDock.class);

        doReturn(uberfireDock).when(dock).getUberfireDock();

        dock.destroy();

        verify(uberfireDocks, never()).remove(uberfireDock);
    }

    @Test
    public void testDestroyWithUberfireDock() {
        final UberfireDock uberfireDock = mock(UberfireDock.class);

        doReturn(uberfireDock).when(dock).getUberfireDock();
        dock.uberfireDock = uberfireDock;

        dock.destroy();

        verify(uberfireDocks, times(1)).remove(uberfireDock);
    }

    @Test
    public void testMakeUberfireDock() {

        final UberfireDockPosition expectedPosition = UberfireDockPosition.EAST;
        final String expectedIcon = IconType.MAP.toString();
        final String expectedPlaceRequestIdentifier = DecisionNavigatorPresenter.IDENTIFIER;
        final Double expectedSize = DOCK_SIZE;
        final String expectedLabel = "DecisionNavigator";

        when(translationService.format(DecisionNavigatorPresenter_DecisionNavigator)).thenReturn(expectedLabel);

        final UberfireDock uberfireDock = dock.makeUberfireDock();

        final UberfireDockPosition actualPosition = uberfireDock.getDockPosition();
        final String actualIcon = uberfireDock.getIconType();
        final String actualPlaceRequestIdentifier = uberfireDock.getPlaceRequest().getIdentifier();
        final Double actualSize = uberfireDock.getSize();
        final String actualLabel = uberfireDock.getLabel();

        assertEquals(expectedPosition, actualPosition);
        assertEquals(expectedIcon, actualIcon);
        assertEquals(expectedPlaceRequestIdentifier, actualPlaceRequestIdentifier);
        assertEquals(expectedSize, actualSize);
        assertEquals(expectedLabel, actualLabel);
    }
}
