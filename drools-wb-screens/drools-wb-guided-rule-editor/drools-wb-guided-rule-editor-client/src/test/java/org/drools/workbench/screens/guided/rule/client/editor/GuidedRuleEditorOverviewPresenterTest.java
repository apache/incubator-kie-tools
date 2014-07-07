/*
 * Copyright 2014 JBoss Inc
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

package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.List;

import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.model.GuidedEditorContent;
import org.drools.workbench.screens.guided.rule.service.GuidedRuleEditorService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.socialscreen.client.OverviewScreenView;
import org.kie.workbench.common.screens.socialscreen.client.discussion.VersionRecordManager;
import org.kie.workbench.common.screens.socialscreen.model.Overview;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.mockito.ArgumentCaptor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.workbench.type.ClientTypeRegistry;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GuidedRuleEditorOverviewPresenterTest {

    private OverviewScreenView.Presenter presenter;
    private OverviewScreenView view;
    private GuidedRuleEditorOverviewPresenter editor;

    private GuidedRuleEditorService service = spy(new GuidedRuleEditorServiceMock());
    private Overview overview;
    private SaveOperationService saveOperationService;

    private RemoteCallback<?> callback;
    private VersionRecordManager versionRecordManager;

    @Before
    public void setUp() throws Exception {
        ClientTypeRegistry clientTypeRegistry = mock(ClientTypeRegistry.class);
        view = mock(OverviewScreenView.class);
        versionRecordManager = mock(VersionRecordManager.class);

        saveOperationService = mock(SaveOperationService.class);
        editor = new GuidedRuleEditorOverviewPresenter(
                clientTypeRegistry,
                versionRecordManager,
                view);
        presenter = editor;

        overview = new Overview();
    }

    @Test
    public void testPresenterSet() throws Exception {
        verify(view).setPresenter(presenter);
    }

    @Test
    public void testAddingDescription() throws Exception {

        Metadata metadata = new Metadata();
        overview.setMetadata(metadata);

        ObservablePath path = mock(ObservablePath.class);
        PlaceRequest place = mock(PlaceRequest.class);
//        editor.setContent(path, place);

        presenter.onDescriptionEdited("Hello");

        ArgumentCaptor<CommandWithCommitMessage> commandWithCommitMessageArgumentCaptor = ArgumentCaptor.forClass(CommandWithCommitMessage.class);

        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
//        verify(versionMenuBuilder).buildMenu(commandArgumentCaptor.capture());
        commandArgumentCaptor.getValue().execute();

        verify(saveOperationService).save(eq(path), commandWithCommitMessageArgumentCaptor.capture());

        commandWithCommitMessageArgumentCaptor.getValue().execute("Added a description");

//        verify(service).save(path, overview.getModel(), metadata, "Added a description");

        assertEquals(overview.getMetadata().getDescription(), "Hello");
    }

    @Test
    public void testChangeVersion() throws Exception {

        ObservablePath path = mock(ObservablePath.class);
        PlaceRequest place = mock(PlaceRequest.class);
//        editor.setContent(path, place);

        ArgumentCaptor<Callback> callbackArgumentCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(versionRecordManager).addVersionSelectionCallback(callbackArgumentCaptor.capture());

        callbackArgumentCaptor.getValue().callback("v1");


    }

    private class GuidedRuleEditorServiceCallerMock implements Caller<GuidedRuleEditorService> {

        @Override
        public GuidedRuleEditorService call() {
            return service;
        }

        @Override
        public GuidedRuleEditorService call(RemoteCallback<?> remoteCallback) {
            callback = remoteCallback;
            return service;
        }

        @Override
        public GuidedRuleEditorService call(RemoteCallback<?> remoteCallback, ErrorCallback<?> errorCallback) {
            callback = remoteCallback;
            return service;
        }
    }

    private class GuidedRuleEditorServiceMock
            implements GuidedRuleEditorService {

        @Override public GuidedEditorContent loadContent(Path path) {

            return null;
        }

        @Override public Path copy(Path path, String newName, String comment) {
            return null;
        }

        @Override public Path create(Path context, String fileName, RuleModel content, String comment) {
            return null;
        }

        @Override public void delete(Path path, String comment) {

        }

        @Override public RuleModel load(Path path) {
            return null;
        }

        @Override public Path rename(Path path, String newName, String comment) {
            return null;
        }

        @Override public Path save(Path path, RuleModel content, Metadata metadata, String comment) {
            return null;
        }

        @Override public List<ValidationMessage> validate(Path path, RuleModel content) {
            return null;
        }

        @Override public String toSource(Path path, RuleModel model) {
            return null;
        }
    }
}
