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

package org.drools.workbench.screens.dtablexls.client.handlers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.gwt.user.client.Command;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.dtablexls.client.type.DecisionTableXLSResourceType;
import org.drools.workbench.screens.dtablexls.client.type.DecisionTableXLSXResourceType;
import org.guvnor.common.services.project.categories.Decision;
import org.guvnor.common.services.project.model.Package;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.kie.workbench.common.widgets.client.widget.AttachmentFileWidget;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NewDecisionTableXLSHandlerTest {

    private NewDecisionTableXLSHandler handler;

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
    private AttachmentFileWidget uploadWidget;

    @Mock
    private ValidationService validationService;

    @Mock
    private Caller<ValidationService> validationServiceCaller;

    @Captor
    private ArgumentCaptor<Command> successCmdCaptor;

    @Captor
    private ArgumentCaptor<Command> failureCmdCaptor;

    @Captor
    private ArgumentCaptor<Path> newPathCaptor;

    private DecisionTableXLSResourceType decisionTableXLSResourceType = new DecisionTableXLSResourceType(new Decision());
    private DecisionTableXLSXResourceType decisionTableXLSXResourceType = new DecisionTableXLSXResourceType(new Decision());

    @Mock
    private EventSourceMock<NotificationEvent> mockNotificationEvent;

    @Before
    public void setup() {
        handler = new NewDecisionTableXLSHandler(placeManager,
                                                 decisionTableXLSResourceType,
                                                 decisionTableXLSXResourceType,
                                                 busyIndicatorView,
                                                 clientMessageBus) {
            {
                this.notificationEvent = mockNotificationEvent;
                this.newResourceSuccessEvent = newResourceSuccessEventMock;
                this.validationService = validationServiceCaller;
            }

            @Override
            protected String encode(final String fileName) {
                return NewDecisionTableXLSHandlerTest.this.encode(fileName);
            }

            @Override
            protected String getClientId() {
                return "123";
            }
        };

        when(validationServiceCaller.call(any(RemoteCallback.class))).thenReturn(validationService);

        handler.setUploadWidget(uploadWidget);
    }

    @Test
    public void testSuccess() {
        final String fileName = "fileName";
        final Package pkg = mock(Package.class);
        final Path resourcesPath = PathFactory.newPath("resources",
                                                       "default://project/src/main/resources");

        when(pkg.getPackageMainResourcesPath()).thenReturn(resourcesPath);
        when(uploadWidget.getFilenameSelectedToUpload()).thenReturn(fileName + ".xls");

        handler.create(pkg,
                       fileName,
                       newResourcePresenter);

        verify(uploadWidget,
               times(1)).submit(eq(resourcesPath),
                                eq(fileName + "." + decisionTableXLSResourceType.getSuffix()),
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

        assertEquals("default://project/src/main/resources/fileName.xls",
                     newPathCaptor.getValue().toURI());
    }

    @Test
    public void testSuccessXLSX() {
        final String fileName = "fileName";
        final Package pkg = mock(Package.class);
        final Path resourcesPath = PathFactory.newPath("resources",
                                                       "default://project/src/main/resources");

        when(pkg.getPackageMainResourcesPath()).thenReturn(resourcesPath);
        when(uploadWidget.getFilenameSelectedToUpload()).thenReturn(fileName + ".xlsx");

        handler.create(pkg,
                       fileName,
                       newResourcePresenter);

        verify(uploadWidget,
               times(1)).submit(eq(resourcesPath),
                                eq(fileName + "." + decisionTableXLSXResourceType.getSuffix()),
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

        assertEquals("default://project/src/main/resources/fileName.xlsx",
                     newPathCaptor.getValue().toURI());
    }

    @Test
    public void testSuccessMultiByteProjectName() {
        final String fileName = "fileName";
        final Package pkg = mock(Package.class);
        final Path resourcesPath = PathFactory.newPath("resources",
                                                       "default://あああ/src/main/resources");

        when(pkg.getPackageMainResourcesPath()).thenReturn(resourcesPath);
        when(uploadWidget.getFilenameSelectedToUpload()).thenReturn(fileName + ".xls");

        handler.create(pkg,
                       fileName,
                       newResourcePresenter);

        verify(uploadWidget,
               times(1)).submit(eq(resourcesPath),
                                eq(fileName + "." + decisionTableXLSResourceType.getSuffix()),
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

        verify(placeManager,
               times(1)).goTo(newPathCaptor.capture());

        assertEquals("default://あああ/src/main/resources/fileName.xls",
                     newPathCaptor.getValue().toURI());
    }

    @Test
    public void testSuccessMultiByteFileName() {
        final String fileName = "あああ";
        final Package pkg = mock(Package.class);
        final Path resourcesPath = PathFactory.newPath("resources",
                                                       "default://project/src/main/resources");

        when(pkg.getPackageMainResourcesPath()).thenReturn(resourcesPath);
        when(uploadWidget.getFilenameSelectedToUpload()).thenReturn(fileName + ".xls");

        handler.create(pkg,
                       fileName,
                       newResourcePresenter);

        verify(uploadWidget,
               times(1)).submit(eq(resourcesPath),
                                eq(fileName + "." + decisionTableXLSResourceType.getSuffix()),
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

        verify(placeManager,
               times(1)).goTo(newPathCaptor.capture());

        assertEquals("default://project/src/main/resources/%E3%81%82%E3%81%82%E3%81%82.xls",
                     newPathCaptor.getValue().toURI());
    }

    @Test
    public void testSuccessMultiByteProjectNameAndFileName() {
        final String fileName = "あああ";
        final Package pkg = mock(Package.class);
        final Path resourcesPath = PathFactory.newPath("resources",
                                                       "default://" + encode("ああ") + "/src/main/resources");

        when(pkg.getPackageMainResourcesPath()).thenReturn(resourcesPath);
        when(uploadWidget.getFilenameSelectedToUpload()).thenReturn(fileName + ".xls");

        handler.create(pkg,
                       fileName,
                       newResourcePresenter);

        verify(uploadWidget,
               times(1)).submit(eq(resourcesPath),
                                eq(fileName + "." + decisionTableXLSResourceType.getSuffix()),
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

        verify(placeManager,
               times(1)).goTo(newPathCaptor.capture());

        assertEquals("default://%E3%81%82%E3%81%82/src/main/resources/%E3%81%82%E3%81%82%E3%81%82.xls",
                     newPathCaptor.getValue().toURI());
    }

    @Test
    public void testGetServletUrl() {
        assertEquals("dtablexls/file?clientId=123", handler.getServletUrl());
    }

    @Test
    public void testValidateNullFile() {
        final ValidatorWithReasonCallback validatorWithReasonCallback = mock(ValidatorWithReasonCallback.class);
        when(uploadWidget.getFilenameSelectedToUpload()).thenReturn(null);

        handler.validate("filename", validatorWithReasonCallback);

        verify(uploadWidget).addStyleName(ValidationState.ERROR.getCssName());
    }

    @Test
    public void testValidateEmptyFile() {
        final ValidatorWithReasonCallback validatorWithReasonCallback = mock(ValidatorWithReasonCallback.class);
        when(uploadWidget.getFilenameSelectedToUpload()).thenReturn("");

        handler.validate("filename", validatorWithReasonCallback);

        verify(uploadWidget).addStyleName(ValidationState.ERROR.getCssName());
    }

    @Test
    public void testValidateNonEmptyFile() {
        final ValidatorWithReasonCallback validatorWithReasonCallback = mock(ValidatorWithReasonCallback.class);
        when(uploadWidget.getFilenameSelectedToUpload()).thenReturn("table.xls");

        handler.validate("filename", validatorWithReasonCallback);

        verify(uploadWidget, never()).addStyleName(ValidationState.ERROR.getCssName());
    }

    private String encode(final String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            fail(uee.getMessage());
        }
        return "";
    }
}
