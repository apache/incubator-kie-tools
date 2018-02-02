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

package org.kie.workbench.common.widgets.client.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.enterprise.event.Event;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.ArgumentCaptor;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@RunWith(GwtMockitoTestRunner.class)
public class DefaultNewResourceHandlerTest {

    private DefaultNewResourceHandler handler;

    private WorkspaceProjectContext context;
    private KieModuleService moduleService;
    private Caller<KieModuleService> moduleServiceCaller;
    private ValidationService validationService;
    private Caller<ValidationService> validationServiceCaller;
    private PlaceManager placeManager;
    private Event<NotificationEvent> notificationEvent;
    private Event<NewResourceSuccessEvent> newResourceSuccessEventMock;
    private BusyIndicatorView busyIndicatorView;

    @Before
    public void setup() {
        context = mock(WorkspaceProjectContext.class);
        moduleService = mock(KieModuleService.class);
        moduleServiceCaller = new CallerMock<>(moduleService);
        validationService = mock(ValidationService.class);
        validationServiceCaller = new CallerMock<>(validationService);
        placeManager = mock(PlaceManager.class);
        notificationEvent = new EventSourceMock<NotificationEvent>() {
            @Override
            public void fire(final NotificationEvent event) {
            }
        };
        newResourceSuccessEventMock = spy(new EventSourceMock<NewResourceSuccessEvent>() {
            @Override
            public void fire(final NewResourceSuccessEvent event) {
            }
        });
        busyIndicatorView = mock(BusyIndicatorView.class);

        handler = new DefaultNewResourceHandler(context,
                                                moduleServiceCaller,
                                                validationServiceCaller,
                                                placeManager,
                                                notificationEvent,
                                                busyIndicatorView) {
            {
                newResourceSuccessEvent = newResourceSuccessEventMock;
            }

            @Override
            public String getDescription() {
                return "mock";
            }

            @Override
            public IsWidget getIcon() {
                return null;
            }

            @Override
            public ResourceTypeDefinition getResourceType() {
                final ResourceTypeDefinition resourceType = mock(ResourceTypeDefinition.class);
                when(resourceType.getPrefix()).thenReturn("");
                when(resourceType.getSuffix()).thenReturn("suffix");
                return resourceType;
            }

            @Override
            public void create(final org.guvnor.common.services.project.model.Package pkg,
                               final String baseFileName,
                               final NewResourcePresenter presenter) {

            }
        };
    }

    @Test
    public void testValidateValidFileName() {
        final org.guvnor.common.services.project.model.Package pkg = mock(Package.class);
        final ValidatorWithReasonCallback callback = mock(ValidatorWithReasonCallback.class);
        when(validationService.isFileNameValid("filename.suffix")).thenReturn(true);

        handler.validate("filename",
                         callback);

        verify(callback,
               times(1)).onSuccess();
        verify(callback,
               never()).onFailure();
        verify(callback,
               never()).onFailure(any(String.class));
    }

    @Test
    public void testValidateInvalidFileName() {
        final org.guvnor.common.services.project.model.Package pkg = mock(Package.class);
        final ValidatorWithReasonCallback callback = mock(ValidatorWithReasonCallback.class);
        when(validationService.isFileNameValid("filename.suffix")).thenReturn(false);

        handler.validate("filename",
                         callback);

        verify(callback,
               times(1)).onFailure(any(String.class));
        verify(callback,
               never()).onFailure();
        verify(callback,
               never()).onSuccess();
    }

    @Test
    public void testAcceptContextWithContextWithNoProject() {
        final Callback<Boolean, Void> callback = mock(Callback.class);
        when(context.getActiveModule()).thenReturn(Optional.empty());

        handler.acceptContext(callback);
        verify(callback,
               times(1)).onSuccess(false);
    }

    @Test
    public void testAcceptContextWithContextWithProject() {
        final Callback<Boolean, Void> callback = mock(Callback.class);
        when(context.getActiveModule()).thenReturn(Optional.of(mock(Module.class)));

        handler.acceptContext(callback);
        verify(callback,
               times(1)).onSuccess(true);
    }

    @Test
    public void testGetCommand() {
        final NewResourcePresenter presenter = mock(NewResourcePresenter.class);
        final Command command = handler.getCommand(presenter);
        assertNotNull(command);

        command.execute();
        verify(presenter,
               times(1)).show(handler);
    }

    @Test
    public void testCreateSuccessCallback() {
        final ArgumentCaptor<Path> pathArgumentCaptor = ArgumentCaptor.forClass(Path.class);
        final NewResourcePresenter presenter = mock(NewResourcePresenter.class);

        final Path path = mock(Path.class);
        handler.getSuccessCallback(presenter).callback(path);

        verify(busyIndicatorView,
               times(1)).hideBusyIndicator();
        verify(presenter,
               times(1)).complete();
        verify(newResourceSuccessEventMock,
               times(1)).fire(any(NewResourceSuccessEvent.class));
        verify(placeManager,
               times(1)).goTo(pathArgumentCaptor.capture());

        assertEquals(path,
                     pathArgumentCaptor.getValue());
    }
}
