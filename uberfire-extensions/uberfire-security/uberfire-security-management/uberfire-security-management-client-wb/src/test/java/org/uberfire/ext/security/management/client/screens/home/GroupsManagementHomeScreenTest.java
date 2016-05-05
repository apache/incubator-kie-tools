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

package org.uberfire.ext.security.management.client.screens.home;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.screens.BaseScreen;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class GroupsManagementHomeScreenTest {

    @Mock ErrorPopupPresenter errorPopupPresenter;
    @Mock ClientUserSystemManager clientUserSystemManager;
    @Mock BaseScreen baseScreen;
    @Mock EntitiesManagementHome view;

    @InjectMocks GroupsManagementHomeScreen tested;

    @Before
    public void setup() {
        when(clientUserSystemManager.isGroupCapabilityEnabled(any(Capability.class))).thenReturn(true);
    }

    @Test
    public void testInit() {
        tested.init();
        verify(baseScreen, times(1)).init(view);
    }

    @Test
    public void testShow() {
        tested.show();
        final ArgumentCaptor<List> itemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(view, times(1)).show(anyString(), itemsCaptor.capture());
        final List items = itemsCaptor.getValue();
        assertNotNull(items);
        assertTrue(items.size() == 4);
    }
}
