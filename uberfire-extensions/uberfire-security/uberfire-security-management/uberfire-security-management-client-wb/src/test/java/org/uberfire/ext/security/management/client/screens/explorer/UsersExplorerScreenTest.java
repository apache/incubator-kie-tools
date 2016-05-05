/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.screens.explorer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.screens.BaseScreen;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.ReadUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.explorer.UsersExplorer;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import java.util.ArrayList;
import java.util.List;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class UsersExplorerScreenTest {

    @Mock BaseScreen baseScreen;
    @Mock ErrorPopupPresenter errorPopupPresenter;
    @Mock UsersExplorer usersExplorer;
    @Mock PlaceManager placeManager;
    @Mock ClientUserSystemManager clientUserSystemManager;
    
    @InjectMocks UsersExplorerScreen tested;

    @Before
    public void setup() {
        when(clientUserSystemManager.isUserCapabilityEnabled(any(Capability.class))).thenReturn(true);
    }

    @Test
    public void testGetWidget() {
        assertEquals(baseScreen, tested.getWidget());
        
    }
    @Test
    public void testOnClose() {
        tested.onClose();
        verify(usersExplorer, times(1)).clear();
    }

    @Test
    public void testShow() {
        final MenuItem menuItem = mock(MenuItem.class);
        final List<MenuItem> menuItemList = new ArrayList<MenuItem>(1);
        menuItemList.add(menuItem);
        final Menus menus = mock(Menus.class);
        when(menus.getItems()).thenReturn(menuItemList);
        tested.menu = menus;
        tested.show();
        verify(usersExplorer, times(1)).show();
        verify(menuItem, times(1)).setEnabled(true);
        verify(usersExplorer, times(0)).clear();
    }

    @Test
    public void testNewUser() {
        tested.newUser();
        verify(placeManager, times(1)).goTo(any(PlaceRequest.class));
    }

    @Test
    public void testShowError() {
        tested.showError("error");
        verify(errorPopupPresenter, times(1)).showMessage("error");
    }

    @Test
    public void testOnUserRead() {
        final ReadUserEvent readUserEvent = mock(ReadUserEvent.class);
        when(readUserEvent.getIdentifier()).thenReturn("user1");
        tested.onUserRead(readUserEvent);
        verify(placeManager, times(1)).goTo(any(PlaceRequest.class));
    }

    @Test
    public void testOnErrorEvent() {
        final OnErrorEvent onErrorEvent = mock(OnErrorEvent.class);
        when(onErrorEvent.getMessage()).thenReturn("error1");
        tested.onErrorEvent(onErrorEvent);
        verify(errorPopupPresenter, times(1)).showMessage("error1");
    }
    
}
