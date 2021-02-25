/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench;

import java.util.Arrays;
import java.util.Collections;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class WorkbenchStartupTest {

    /**
     * The thing we are unit testing
     */
    @InjectMocks
    @Spy
    Workbench workbench;

    @Mock
    SyncBeanManager bm;
    @Mock
    PanelManager panelManager;
    @Mock
    WorkbenchLayout layout;
    @Mock
    PlaceManager placeManager;
    @Mock
    SyncBeanDef<PerspectiveActivity> perspectiveBean1;
    @Mock
    SyncBeanDef<PerspectiveActivity> perspectiveBean2;
    @Mock
    PerspectiveActivity perspectiveActivity1;
    @Mock
    PerspectiveActivity perspectiveActivity2;
    @Mock
    Logger logger;
    @Mock
    ActivityBeansCache activityBeansCache;

    @Before
    public void setup() {
        when(bm.lookupBeans(any(Class.class))).thenReturn(Collections.emptyList());
        when(dragController.getBoundaryPanel()).thenReturn(new AbsolutePanel());
        doNothing().when(workbench).addLayoutToRootPanel(any(WorkbenchLayout.class));
        when(bm.lookupBeans(PerspectiveActivity.class)).thenReturn(Arrays.asList(perspectiveBean1,
                                                                                 perspectiveBean2));
        when(perspectiveBean1.getInstance()).thenReturn(perspectiveActivity1);
        when(perspectiveBean2.getInstance()).thenReturn(perspectiveActivity2);
        when(perspectiveActivity1.getIdentifier()).thenReturn("perspective1");
        when(perspectiveActivity2.getIdentifier()).thenReturn("perspective2");
        when(perspectiveActivity2.isDefault()).thenReturn(true);
    }

    @Test
    public void goToHomePerspective() throws Exception {
        workbench.startIfNotBlocked();
        verify(placeManager).goTo(new DefaultPlaceRequest(perspectiveActivity1.getIdentifier()));
    }

    @Test
    public void goToDefaultPerspective() throws Exception {
        when(perspectiveActivity1.isDefault()).thenReturn(true);
        when(perspectiveActivity2.isDefault()).thenReturn(false);
        workbench.startIfNotBlocked();
        verify(placeManager).goTo(new DefaultPlaceRequest(perspectiveActivity1.getIdentifier()));
    }

    @Test
    public void goToNoWhere() throws Exception {
        when(perspectiveActivity2.isDefault()).thenReturn(false);
        workbench.startIfNotBlocked();
        verify(placeManager,
               never()).goTo(any(PlaceRequest.class));
    }
}
