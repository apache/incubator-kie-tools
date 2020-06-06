/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scorecardxls.client.handlers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.gwt.user.client.Command;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scorecardxls.client.type.ScoreCardXLSResourceType;
import org.guvnor.common.services.project.categories.Decision;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.resources.EditorIds;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.kie.workbench.common.widgets.client.widget.AttachmentFileWidget;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NewScoreCardXLSHandlerTest {

    private NewScoreCardXLSHandler handler;

    @Mock
    private EventSourceMock<NewResourceSuccessEvent> newResourceSuccessEventMock;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Mock
    private ClientMessageBus clientMessageBus;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private User user;

    @GwtMock
    private AttachmentFileWidget uploadWidget;

    @Captor
    private ArgumentCaptor<Command> successCmdCaptor;

    @Captor
    private ArgumentCaptor<Command> failureCmdCaptor;

    @Captor
    private ArgumentCaptor<Path> newPathCaptor;

    @Captor
    private ArgumentCaptor<ResourceRef> refArgumentCaptor;

    private ScoreCardXLSResourceType resourceType = new ScoreCardXLSResourceType(new Decision());

    @Mock
    private EventSourceMock<NotificationEvent> mockNotificationEvent;

    @Before
    public void setup() {
        handler = new NewScoreCardXLSHandler(placeManager,
                                             resourceType,
                                             busyIndicatorView,
                                             clientMessageBus,
                                             authorizationManager,
                                             sessionInfo) {
            {
                this.notificationEvent = mockNotificationEvent;
                this.newResourceSuccessEvent = newResourceSuccessEventMock;
            }

            @Override
            protected String encode(final String fileName) {
                return NewScoreCardXLSHandlerTest.this.encode(fileName);
            }

            @Override
            protected String getClientId() {
                return "123";
            }
        };
        handler.setUploadWidget(uploadWidget);
        when(sessionInfo.getIdentity()).thenReturn(user);
    }

    @Test
    public void testSuccess() {
        final String fileName = "fileName";
        final Package pkg = mock(Package.class);
        final Path resourcesPath = PathFactory.newPath("resources",
                                                       "default://project/src/main/resources");

        when(pkg.getPackageMainResourcesPath()).thenReturn(resourcesPath);
        handler.create(pkg,
                       fileName,
                       newResourcePresenter);

        verify(uploadWidget,
               times(1)).submit(eq(resourcesPath),
                                eq(fileName + "." + resourceType.getSuffix()),
                                any(String.class),
                                successCmdCaptor.capture(),
                                failureCmdCaptor.capture());

        successCmdCaptor.getValue().execute();

        verify(busyIndicatorView,
               times(1)).hideBusyIndicator();
        verify(newResourcePresenter,
               times(1)).complete();
        verify(mockNotificationEvent,
               times(1)).fire(any(NotificationEvent.class));
        verify(newResourceSuccessEventMock,
               times(1)).fire(any(NewResourceSuccessEvent.class));
        verify(placeManager,
               times(1)).goTo(newPathCaptor.capture());

        assertEquals("default://project/src/main/resources/fileName.sxls",
                     newPathCaptor.getValue().toURI());
    }

    @Test
    public void testSuccessMultiByteProjectName() {
        final String fileName = "fileName";
        final Package pkg = mock(Package.class);
        final Path resourcesPath = PathFactory.newPath("resources",
                                                       "default://" + encode("ああ") + "/src/main/resources");

        when(pkg.getPackageMainResourcesPath()).thenReturn(resourcesPath);

        handler.create(pkg,
                       fileName,
                       newResourcePresenter);

        verify(uploadWidget,
               times(1)).submit(eq(resourcesPath),
                                eq(fileName + "." + resourceType.getSuffix()),
                                any(String.class),
                                successCmdCaptor.capture(),
                                failureCmdCaptor.capture());

        successCmdCaptor.getValue().execute();

        verify(busyIndicatorView,
               times(1)).hideBusyIndicator();
        verify(newResourcePresenter,
               times(1)).complete();
        verify(mockNotificationEvent,
               times(1)).fire(any(NotificationEvent.class));
        verify(newResourceSuccessEventMock,
               times(1)).fire(any(NewResourceSuccessEvent.class));
        verify(placeManager,
               times(1)).goTo(newPathCaptor.capture());

        assertEquals("default://%E3%81%82%E3%81%82/src/main/resources/fileName.sxls",
                     newPathCaptor.getValue().toURI());
    }

    @Test
    public void testSuccessMultiByteFileName() {
        final String fileName = "あああ";
        final Package pkg = mock(Package.class);
        final Path resourcesPath = PathFactory.newPath("resources",
                                                       "default://project/src/main/resources");

        when(pkg.getPackageMainResourcesPath()).thenReturn(resourcesPath);

        handler.create(pkg,
                       fileName,
                       newResourcePresenter);

        verify(uploadWidget,
               times(1)).submit(eq(resourcesPath),
                                eq(fileName + "." + resourceType.getSuffix()),
                                any(String.class),
                                successCmdCaptor.capture(),
                                failureCmdCaptor.capture());

        successCmdCaptor.getValue().execute();

        verify(busyIndicatorView,
               times(1)).hideBusyIndicator();
        verify(newResourcePresenter,
               times(1)).complete();
        verify(mockNotificationEvent,
               times(1)).fire(any(NotificationEvent.class));
        verify(newResourceSuccessEventMock,
               times(1)).fire(any(NewResourceSuccessEvent.class));
        verify(placeManager,
               times(1)).goTo(newPathCaptor.capture());

        assertEquals("default://project/src/main/resources/%E3%81%82%E3%81%82%E3%81%82.sxls",
                     newPathCaptor.getValue().toURI());
    }

    @Test
    public void testSuccessMultiByteProjectNameAndFileName() {
        final String fileName = "あああ";
        final Package pkg = mock(Package.class);
        final Path resourcesPath = PathFactory.newPath("resources",
                                                       "default://" + encode("ああ") + "/src/main/resources");

        when(pkg.getPackageMainResourcesPath()).thenReturn(resourcesPath);

        handler.create(pkg,
                       fileName,
                       newResourcePresenter);

        verify(uploadWidget,
               times(1)).submit(eq(resourcesPath),
                                eq(fileName + "." + resourceType.getSuffix()),
                                any(String.class),
                                successCmdCaptor.capture(),
                                failureCmdCaptor.capture());

        successCmdCaptor.getValue().execute();

        verify(busyIndicatorView,
               times(1)).hideBusyIndicator();
        verify(newResourcePresenter,
               times(1)).complete();
        verify(mockNotificationEvent,
               times(1)).fire(any(NotificationEvent.class));
        verify(newResourceSuccessEventMock,
               times(1)).fire(any(NewResourceSuccessEvent.class));
        verify(placeManager,
               times(1)).goTo(newPathCaptor.capture());

        assertEquals("default://%E3%81%82%E3%81%82/src/main/resources/%E3%81%82%E3%81%82%E3%81%82.sxls",
                     newPathCaptor.getValue().toURI());
    }

    @Test
    public void testGetServletUrl() {
        assertEquals("scorecardxls/file?clientId=123", handler.getServletUrl());
    }

    @Test
    public void checkCanCreateWhenFeatureDisabled() {
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(ResourceAction.READ),
                                            eq(user))).thenReturn(false);

        assertFalse(handler.canCreate());
        assertResourceRef();
    }

    @Test
    public void checkCanCreateWhenFeatureEnabled() {
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(ResourceAction.READ),
                                            eq(user))).thenReturn(true);

        assertTrue(handler.canCreate());
        assertResourceRef();
    }

    private String encode(final String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            fail(uee.getMessage());
        }
        return "";
    }

    private void assertResourceRef() {
        verify(authorizationManager).authorize(refArgumentCaptor.capture(),
                                               eq(ResourceAction.READ),
                                               eq(user));
        assertEquals(EditorIds.XLS_SCORE_CARD,
                     refArgumentCaptor.getValue().getIdentifier());
        assertEquals(ActivityResourceType.EDITOR,
                     refArgumentCaptor.getValue().getResourceType());
    }
}
