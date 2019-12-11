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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.bus.client.framework.ClientMessageBusImpl;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
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
import org.uberfire.client.workbench.events.ApplicationReadyEvent;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.PermissionManager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
    WorkbenchPickupDragController dragController;
    @Mock
    WorkbenchDragAndDropManager dndManager;
    @Mock
    PanelManager panelManager;
    @Mock
    StubAppReadyEventSource appReadyEvent;
    @Mock
    User identity;
    @Mock(extraInterfaces = ClientMessageBus.class)
    ClientMessageBusImpl bus;
    @Mock
    WorkbenchLayout layout;
    @Mock
    LayoutSelection layoutSelection;
    @Mock
    PermissionManager permissionManager;
    @Mock
    PlaceManager placeManager;
    @Mock
    AuthorizationManager authorizationManager;
    @Mock
    AuthorizationPolicy authorizationPolicy;
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
        when(permissionManager.getAuthorizationPolicy()).thenReturn(authorizationPolicy);
        when(authorizationManager.authorize(any(Resource.class),
                                            any(User.class))).thenReturn(true);
        when(bm.lookupBeans(PerspectiveActivity.class)).thenReturn(Arrays.asList(perspectiveBean1,
                                                                                 perspectiveBean2));
        when(perspectiveBean1.getInstance()).thenReturn(perspectiveActivity1);
        when(perspectiveBean2.getInstance()).thenReturn(perspectiveActivity2);
        when(perspectiveActivity1.getIdentifier()).thenReturn("perspective1");
        when(perspectiveActivity2.getIdentifier()).thenReturn("perspective2");
        when(perspectiveActivity2.isDefault()).thenReturn(true);
    }

    @Test
    public void shouldNotStartWhenBlocked() throws Exception {
        verify(appReadyEvent,
               never()).fire(any(ApplicationReadyEvent.class));
        workbench.addStartupBlocker(WorkbenchStartupTest.class);
        workbench.startIfNotBlocked();
        verify(appReadyEvent,
               never()).fire(any(ApplicationReadyEvent.class));
    }

    @Test
    public void shouldStartWhenUnblocked() throws Exception {
        workbench.addStartupBlocker(WorkbenchStartupTest.class);
        workbench.removeStartupBlocker(WorkbenchStartupTest.class);
        verify(appReadyEvent,
               times(1)).fire(any(ApplicationReadyEvent.class));
    }

    @Test
    public void shouldStartOnAfterInitIfNeverBlocked() throws Exception {
        workbench.startIfNotBlocked();
        verify(appReadyEvent,
               times(1)).fire(any(ApplicationReadyEvent.class));
    }

    @Test
    public void goToHomePerspective() throws Exception {
        when(authorizationPolicy.getHomePerspective(identity)).thenReturn("perspective1");
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

    @Test
    public void workbenchCloseCommandTest() {
        workbench.workbenchCloseCommand.execute();
        verify(placeManager).closeAllPlaces();
    }

    @Test
    public void workbenchClosingCommandWithUnsavedChangesTest() {
        doReturn(false).when(placeManager).canCloseAllPlaces();
        final Window.ClosingEvent event = mock(Window.ClosingEvent.class);

        workbench.workbenchClosingCommand.execute(event);

        verify(event).setMessage(anyString());
    }

    @Test
    public void workbenchClosingCommandWithoutUnsavedChangesTest() {
        doReturn(true).when(placeManager).canCloseAllPlaces();
        final Window.ClosingEvent event = mock(Window.ClosingEvent.class);

        workbench.workbenchClosingCommand.execute(event);

        verify(event, never()).setMessage(anyString());
    }

    /**
     * Mockito failed to produce a valid mock for a raw {@code Event<ApplicationReadyEvent>} due to classloader issues.
     * This class provides it something that it can mock properly.
     */
    public static class StubAppReadyEventSource implements Event<ApplicationReadyEvent> {

        @Override
        public void fire(ApplicationReadyEvent event) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public Event<ApplicationReadyEvent> select(Annotation... qualifiers) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public <U extends ApplicationReadyEvent> Event<U> select(Class<U> subtype,
                                                                 Annotation... qualifiers) {
            throw new UnsupportedOperationException("Not implemented.");
        }
    }
}
