/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client.menu.common;

import java.util.function.Supplier;

import javax.enterprise.event.Event;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.RenameInProgressEvent;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SaveAndRenameCommandBuilderTest {

    @Mock
    public SupportsSaveAndRename<String, DefaultMetadata> service;

    @Mock
    private RenamePopUpPresenter renamePopUpPresenter;

    @Mock
    private RenamePopUpPresenter.View renamePopUpPresenterView;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private Event<NotificationEvent> notification;

    @Mock
    private Path path;

    @Mock
    private DefaultMetadata metadata;

    @Mock
    private Validator validator;

    @Mock
    private ParameterizedCommand<Path> onSuccess;

    @Mock
    private Command onError;

    @Mock
    private NotificationEvent notificationEvent;

    @Mock
    private EventSourceMock<RenameInProgressEvent> renameInProgressEvent;

    @Mock
    private Command beforeSaveAndRenameCommand;

    private SaveAndRenameCommandBuilder<String, DefaultMetadata> builder;

    private Caller<SupportsSaveAndRename<String, DefaultMetadata>> renameCaller;

    private boolean isDirty = true;

    private String content = "content";

    private Supplier<Path> pathSupplierFake = () -> path;

    private Supplier<DefaultMetadata> metadataSupplierFake = () -> metadata;

    private Supplier<String> contentSupplierFake = () -> content;

    private Supplier<Boolean> isDirtySupplierFake = () -> isDirty;

    @Before
    public void setup() {
        builder = spy(new SaveAndRenameCommandBuilder<>(renamePopUpPresenter, busyIndicatorView, notification, renameInProgressEvent));
        renameCaller = spy(new CallerMock<>(service));

        doReturn(renamePopUpPresenterView).when(renamePopUpPresenter).getView();
        doReturn(notificationEvent).when(builder).makeItemRenamedSuccessfullyEvent();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWhenPathSupplierIsNull() throws Exception {

        builder
                .addValidator(validator)
                .addRenameService(renameCaller)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWhenValidatorIsNull() throws Exception {

        builder
                .addPathSupplier(pathSupplierFake)
                .addRenameService(renameCaller)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWhenRenameCallerIsNull() throws Exception {

        builder
                .addPathSupplier(pathSupplierFake)
                .addValidator(validator)
                .build();
    }

    @Test
    public void testBuildWhenRequiredParametersArePresent() throws Exception {

        final CommandWithFileNameAndCommitMessage renameCommand = mock(CommandWithFileNameAndCommitMessage.class);
        final CommandWithFileNameAndCommitMessage saveAndRenameCommand = mock(CommandWithFileNameAndCommitMessage.class);

        doReturn(renameCommand).when(builder).makeRenameCommand();
        doReturn(saveAndRenameCommand).when(builder).makeSaveAndRenameCommand();

        final Command command = builder
                .addPathSupplier(pathSupplierFake)
                .addValidator(validator)
                .addRenameService(renameCaller)
                .addMetadataSupplier(metadataSupplierFake)
                .addContentSupplier(contentSupplierFake)
                .addIsDirtySupplier(isDirtySupplierFake)
                .build();

        command.execute();

        verify(renamePopUpPresenter).show(path, validator, isDirty, renameCommand, saveAndRenameCommand);
    }

    @Test
    public void testMakeSaveAndRenameCommand() throws Exception {

        final String newFileName = "newFileName";
        final String commitMessage = "commitMessage";
        final FileNameAndCommitMessage message = new FileNameAndCommitMessage(newFileName, commitMessage);

        doNothing().when(builder).showBusyIndicator();
        doReturn(path).when(service).saveAndRename(path, newFileName, metadata, content, commitMessage);

        builder
                .addRenameService(renameCaller)
                .addPathSupplier(pathSupplierFake)
                .makeSaveAndRenameCommand()
                .execute(message);

        final InOrder inOrder = inOrder(builder);

        inOrder.verify(builder).showBusyIndicator();
        inOrder.verify(builder).callSaveAndRename(message);
        inOrder.verify(builder).hideRenamePopup();
        inOrder.verify(builder).hideBusyIndicator();
        inOrder.verify(builder).notifyItemRenamedSuccessfully();
    }

    @Test
    public void testMakeRenameCommand() throws Exception {

        final String newFileName = "newFileName";
        final String commitMessage = "commitMessage";
        final FileNameAndCommitMessage message = new FileNameAndCommitMessage(newFileName, commitMessage);

        doNothing().when(builder).showBusyIndicator();
        doReturn(path).when(service).rename(path, newFileName, commitMessage);

        builder
                .addRenameService(renameCaller)
                .addPathSupplier(pathSupplierFake)
                .makeRenameCommand()
                .execute(message);

        final InOrder inOrder = inOrder(builder);

        inOrder.verify(builder).showBusyIndicator();
        inOrder.verify(builder).callRename(message);
        inOrder.verify(builder).hideRenamePopup();
        inOrder.verify(builder).hideBusyIndicator();
        inOrder.verify(builder).notifyItemRenamedSuccessfully();
    }

    @Test
    public void callSaveAndRename() {
        final String newFileName = "newFileName";
        final String commitMessage = "commitMessage";
        final FileNameAndCommitMessage message = new FileNameAndCommitMessage(newFileName, commitMessage);

        builder
                .addRenameService(renameCaller)
                .addPathSupplier(pathSupplierFake)
                .addMetadataSupplier(metadataSupplierFake)
                .addContentSupplier(contentSupplierFake)
                .addSuccessCallback(onSuccess)
                .addBeforeSaveAndRenameCommand(beforeSaveAndRenameCommand)
                .callSaveAndRename(message);

        verify(beforeSaveAndRenameCommand, only()).execute();
        verify(renameCaller, only()).call(isA(RemoteCallback.class),
                                          isA(SaveAndRenameCommandBuilder.SaveAndRenameErrorCallback.class));
        verify(service, only()).saveAndRename(eq(path),
                                              eq(newFileName),
                                              eq(metadata),
                                              eq(content),
                                              eq(commitMessage));
    }

    @Test
    public void testOnSuccess() throws Exception {

        final RenameInProgressEvent renameInProgressEvent = mock(RenameInProgressEvent.class);

        doReturn(renameInProgressEvent).when(builder).makeRenameInProgressEvent();

        builder
                .addSuccessCallback(onSuccess)
                .onSuccess()
                .callback(path);

        final InOrder inOrder = inOrder(onSuccess, builder);

        inOrder.verify(builder).notifyRenameInProgress();
        inOrder.verify(onSuccess).execute(path);
        inOrder.verify(builder).hideRenamePopup();
        inOrder.verify(builder).hideBusyIndicator();
        inOrder.verify(builder).notifyItemRenamedSuccessfully();
    }

    @Test
    public void testNotifyRenameInProgress() {

        final RenameInProgressEvent event = mock(RenameInProgressEvent.class);

        doReturn(event).when(builder).makeRenameInProgressEvent();

        builder.notifyRenameInProgress();

        verify(renameInProgressEvent).fire(event);
    }

    @Test
    public void testMakeRenameInProgressEvent() {

        final Path path = mock(Path.class);

        doReturn(path).when(builder).getPath();

        final RenameInProgressEvent event = builder.makeRenameInProgressEvent();

        assertEquals(path, event.getPath());
    }

    @Test
    public void testOnErrorWhenFileAlreadyExists() throws Exception {

        final Message message = mock(Message.class);
        final Throwable throwable = mock(Throwable.class);

        doReturn("FileAlreadyExistsException").when(throwable).getMessage();

        final boolean error = builder
                .addErrorCallback(onError)
                .onError()
                .error(message, throwable);

        verify(busyIndicatorView).hideBusyIndicator();
        verify(builder).handleDuplicatedFileName();
        verify(onError, never()).execute();
        verify(builder, never()).hideRenamePopup();

        assertFalse(error);
    }

    @Test
    public void testOnErrorWhenFileDoesNotExist() throws Exception {

        final Message message = mock(Message.class);
        final Throwable throwable = mock(Throwable.class);
        final SaveAndRenameCommandBuilder.SaveAndRenameErrorCallback onSaveAndRenameError = builder.addErrorCallback(onError).onError();
        final SaveAndRenameCommandBuilder.SaveAndRenameErrorCallback onErrorSpy = spy(onSaveAndRenameError);

        doReturn("").when(throwable).getMessage();
        doReturn(true).when(onErrorSpy).callSuper(message, throwable);

        final boolean error = onErrorSpy.error(message, throwable);

        verify(onError).execute();
        verify(builder).hideRenamePopup();
        verify(busyIndicatorView, never()).hideBusyIndicator();
        verify(builder, never()).handleDuplicatedFileName();

        assertTrue(error);
    }
}
