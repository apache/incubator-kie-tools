/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.defaulteditor.client.editor;

import java.util.function.Consumer;
import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorNewFileUpload;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.category.Others;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NewFileUploaderTest {

    @Mock
    private PlaceManager placeManager;

    @GwtMock
    private DefaultEditorNewFileUpload options;

    private AnyResourceTypeDefinition resourceType = new AnyResourceTypeDefinition(new Others());

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private org.guvnor.common.services.project.model.Package pkg;

    @Mock
    private Path pkgResourcesPath;

    @Mock
    private NewResourcePresenter presenter;

    private Event<NewResourceSuccessEvent> newResourceSuccessEventMock = spy(new EventSourceMock<NewResourceSuccessEvent>() {
        @Override
        public void fire(final NewResourceSuccessEvent event) {
            //Do nothing. Default implementation throws an Exception
        }
    });

    private Event<NotificationEvent> mockNotificationEvent = new EventSourceMock<NotificationEvent>() {
        @Override
        public void fire(final NotificationEvent event) {
            //Do nothing. Default implementation throws an Exception
        }
    };

    @Mock
    private KieModuleService moduleService;

    private NewFileUploader uploader;

    @Before
    public void setup() {
        uploader = new NewFileUploader(placeManager,
                                       options,
                                       resourceType,
                                       busyIndicatorView,
                                       new CallerMock<>(moduleService)) {
            {
                super.notificationEvent = mockNotificationEvent;
                super.newResourceSuccessEvent = newResourceSuccessEventMock;
            }

            @Override
            String encode(final String uri) {
                //Tests don't concern themselves with URI encoding
                return uri;
            }
        };
        when(moduleService.resolveDefaultPath(pkg, "txt")).thenReturn(pkgResourcesPath);
        when(pkgResourcesPath.toURI()).thenReturn("default://p0/src/main/resources");
        when(options.getFormFileName()).thenReturn("file.txt");
    }

    @Test
    public void testCreateFileNameWithExtension() {
        uploader.create(pkg,
                        "file.txt",
                        presenter);

        verify(moduleService,
               times(1)).resolveDefaultPath(pkg, "txt");
        verify(busyIndicatorView,
               times(1)).showBusyIndicator(any(String.class));
        verify(options,
               times(1)).setFileName("file.txt");
        verify(options,
               times(1)).upload(any(Consumer.class),
                                any(Consumer.class));
    }

    @Test
    public void testCreateFileNameWithoutExtension() {
        uploader.create(pkg,
                        "file",
                        presenter);

        verify(moduleService,
               times(1)).resolveDefaultPath(pkg, "txt");
        verify(busyIndicatorView,
               times(1)).showBusyIndicator(any(String.class));
        verify(options,
               times(1)).setFileName("file.txt");
        verify(options,
               times(1)).upload(any(Consumer.class),
                                any(Consumer.class));
    }

    @Test
    public void testCreateSuccess() {
        final ArgumentCaptor<Consumer> commandArgumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        final ArgumentCaptor<Path> pathArgumentCaptor = ArgumentCaptor.forClass(Path.class);
        final ArgumentCaptor<NewResourceSuccessEvent> newResourceSuccessEventArgumentCaptor = ArgumentCaptor.forClass(NewResourceSuccessEvent.class);

        uploader.create(pkg,
                        "file",
                        presenter);

        verify(moduleService,
               times(1)).resolveDefaultPath(pkg, "txt");
        verify(busyIndicatorView,
               times(1)).showBusyIndicator(any(String.class));
        verify(options,
               times(1)).upload(commandArgumentCaptor.capture(),
                                any(Consumer.class));

        //Emulate a successful upload
        final Consumer command = commandArgumentCaptor.getValue();
        assertNotNull(command);

        command.accept("OK");

        verify(busyIndicatorView,
               times(1)).hideBusyIndicator();
        verify(presenter,
               times(1)).complete();
        verify(newResourceSuccessEventMock,
               times(1)).fire(newResourceSuccessEventArgumentCaptor.capture());
        verify(placeManager,
               times(1)).goTo(pathArgumentCaptor.capture());

        //Check navigation
        final Path routedPath = pathArgumentCaptor.getValue();
        assertEquals("default://p0/src/main/resources/file.txt",
                     routedPath.toURI());
        final NewResourceSuccessEvent newResourceSuccessEvent = newResourceSuccessEventArgumentCaptor.getValue();
        assertEquals("default://p0/src/main/resources/file.txt",
                     newResourceSuccessEvent.getPath().toURI());
    }

    @Test
    public void testCreateFailure() {
        final ArgumentCaptor<Consumer> commandArgumentCaptor = ArgumentCaptor.forClass(Consumer.class);

        uploader.create(pkg,
                        "file",
                        presenter);

        verify(moduleService,
               times(1)).resolveDefaultPath(pkg, "txt");
        verify(busyIndicatorView,
               times(1)).showBusyIndicator(any(String.class));
        verify(options,
               times(1)).upload(any(Consumer.class),
                                commandArgumentCaptor.capture());

        //Emulate a successful upload
        final Consumer command = commandArgumentCaptor.getValue();
        assertNotNull(command);

        command.accept("FAIL");

        verify(busyIndicatorView,
               times(1)).hideBusyIndicator();
        verify(presenter,
               never()).complete();
        verify(placeManager,
               never()).goTo(any(Path.class));
    }

    @Test
    public void testNoFileSelected() {
        when(options.getFormFileName()).thenReturn(null);

        uploader.create(pkg,
                        "file",
                        presenter);

        verify(busyIndicatorView,
               never()).showBusyIndicator(any(String.class));
        verify(options,
               never()).upload(any(Consumer.class),
                               any(Consumer.class));
    }
}
