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


package org.uberfire.client.workbench;

import java.lang.annotation.Annotation;
import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.util.MockIOCBeanDef;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(DockLayoutPanel.class)
public class WorkbenchEntryPointTest {

    @Mock
    private SyncBeanManager iocManager;

    @Mock
    private FlowPanel dockContainer;

    @Mock
    private Activity dockActivity;

    @Mock
    private SimpleLayoutPanel dockPanel;

    @Spy
    @InjectMocks
    private WorkbenchEntryPoint workbenchEntryPoint;

    private static final String DOCK_ID = "DockTest";
    private static final PlaceRequest DOCK_PLACE = new DefaultPlaceRequest(DOCK_ID);

    @Test(expected = RuntimeException.class)
    public void testOpenDockActivityNotFound() {
        workbenchEntryPoint.openDock(new DefaultPlaceRequest("Invalid"), dockContainer);
    }

    @Test(expected = RuntimeException.class)
    public void testOpenDockActivityNotDockType() {
        when(dockActivity.isType(ActivityResourceType.DOCK.name())).thenReturn(false);
        makeDockBean(dockActivity, Dependent.class);

        workbenchEntryPoint.openDock(DOCK_PLACE, dockContainer);
    }

    @Test
    public void testOpenDock() {
        when(dockActivity.isType(ActivityResourceType.DOCK.name())).thenReturn(true);
        makeDockBean(dockActivity, Dependent.class);

        workbenchEntryPoint.openDock(DOCK_PLACE, dockContainer);

        verify(dockActivity).onStartup(DOCK_PLACE);
        verify(dockActivity).onOpen();
        verify(workbenchEntryPoint).createPanel(any());
    }

    @Test
    public void testCloseDockActivityNotFound() {
        workbenchEntryPoint.closeDock(dockActivity, dockContainer, dockPanel);

        verify(dockActivity, never()).onClose();
    }

    @Test
    public void testCloseDockActivityNotDependent() {
        when(dockActivity.isType(ActivityResourceType.DOCK.name())).thenReturn(true);
        makeDockBean(dockActivity, ApplicationScoped.class);

        workbenchEntryPoint.openDock(DOCK_PLACE, dockContainer);
        workbenchEntryPoint.closeDock(dockActivity, dockContainer, dockPanel);

        verify(dockActivity).onClose();
        verify(iocManager, never()).destroyBean(any());
    }

    @Test
    public void testCloseDock() {
        when(dockActivity.isType(ActivityResourceType.DOCK.name())).thenReturn(true);
        makeDockBean(dockActivity, Dependent.class);

        workbenchEntryPoint.openDock(DOCK_PLACE, dockContainer);
        workbenchEntryPoint.closeDock(dockActivity, dockContainer, dockPanel);

        verify(dockActivity).onClose();
        verify(iocManager).destroyBean(any());
    }

    @SuppressWarnings("unchecked")
    private <T> void makeDockBean(final T beanInstance,
                                  final Class<? extends Annotation> scope) {
        final Class<T> type = (Class<T>) Activity.class;
        final SyncBeanDef<T> beanDef = new MockIOCBeanDef<>(beanInstance,
                                                            type,
                                                            scope,
                                                            null,
                                                            DOCK_ID,
                                                            true);
        when(iocManager.lookupBeans(type)).thenReturn(Collections.singletonList(beanDef));
    }
}