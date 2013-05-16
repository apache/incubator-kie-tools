/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.projecteditor.client.forms;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.widgets.common.client.popups.text.FormPopup;
import org.kie.workbench.widgets.common.client.popups.text.PopupSetFieldCommand;
import org.kie.guvnor.project.model.KBaseModel;
import org.kie.guvnor.project.model.KModuleModel;
import org.kie.workbench.projecteditor.client.widgets.ListFormComboPanelView;
import org.kie.workbench.services.shared.metadata.model.Metadata;
import org.mockito.ArgumentCaptor;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Command;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class KModuleEditorPanelTest {

    private Path path;
    private KModuleEditorPanelView view;
    private MockProjectEditorServiceCaller projectEditorServiceCaller;
    private KModuleEditorPanel screenK;
    private ListFormComboPanelView.Presenter presenter;
    private FormPopup nameNamePopup;
    private KBaseForm form;

    @Before
    public void setUp() throws Exception {
        path = mock(Path.class);
        view = mock(KModuleEditorPanelView.class);
        projectEditorServiceCaller = new MockProjectEditorServiceCaller();

        nameNamePopup = mock(FormPopup.class);
        form = mock(KBaseForm.class);
        screenK = new KModuleEditorPanel(projectEditorServiceCaller, form, nameNamePopup, view);
        presenter = screenK;
    }

    @Test
    public void testShowEmptyModel() throws Exception {
        projectEditorServiceCaller.setUpModelForLoading(new KModuleModel());

        verify(view, never()).addItem(anyString());
    }

    @Test
    public void testShowModelWithSessions() throws Exception {

        KModuleModel kModuleModel = new KModuleModel();
        kModuleModel.add(createKBaseConfiguration("First"));
        kModuleModel.add(createKBaseConfiguration("Second"));
        kModuleModel.add(createKBaseConfiguration("Third"));
        projectEditorServiceCaller.setUpModelForLoading(kModuleModel);
        screenK.init(path, false);

        verify(view).addItem("First");
        verify(view).addItem("Second");
        verify(view).addItem("Third");
        verify(view, times(3)).addItem(anyString());
    }

    @Test
    public void testSelectKBase() throws Exception {

        KModuleModel kModuleModel = new KModuleModel();
        KBaseModel theOne = createKBaseConfiguration("TheOne");
        kModuleModel.add(theOne);
        projectEditorServiceCaller.setUpModelForLoading(kModuleModel);
        screenK.init(path, false);

        presenter.onSelect("TheOne");

        verify(form).setModel(theOne);
    }

    @Test
    public void testAddKBase() throws Exception {

        KModuleModel kModuleModel = new KModuleModel();
        projectEditorServiceCaller.setUpModelForLoading(kModuleModel);
        screenK.init(path, false);

        presenter.onAdd();

        ArgumentCaptor<PopupSetFieldCommand> addKBaseCommandArgumentCaptor = ArgumentCaptor.forClass(PopupSetFieldCommand.class);
        verify(nameNamePopup).show(addKBaseCommandArgumentCaptor.capture());
        addKBaseCommandArgumentCaptor.getValue().setName("TheOne");

        verify(nameNamePopup).setOldName(""); // Old name should be "" since there is no old name.
        assertNotNull(kModuleModel.get("TheOne"));
        verify(view).addItem("TheOne");
        verify(view).setSelected("TheOne");
        verify(form).setModel(kModuleModel.get("TheOne"));
    }

    @Test
    public void testRemoveKBase() throws Exception {

        KModuleModel kModuleModel = new KModuleModel();
        kModuleModel.add(createKBaseConfiguration("RemoveMe"));
        projectEditorServiceCaller.setUpModelForLoading(kModuleModel);
        screenK.init(path, false);

        presenter.onSelect("RemoveMe");

        presenter.onRemove();

        assertNull(kModuleModel.get("RemoveMe"));
        verify(view).remove("RemoveMe");
    }

    @Test
    public void testRename() throws Exception {

        KModuleModel kModuleModel = new KModuleModel();
        kModuleModel.add(createKBaseConfiguration("RenameMe"));
        projectEditorServiceCaller.setUpModelForLoading(kModuleModel);
        screenK.init(path, false);

        presenter.onSelect("RenameMe");

        presenter.onRename();

        ArgumentCaptor<PopupSetFieldCommand> addKBaseCommandArgumentCaptor = ArgumentCaptor.forClass(PopupSetFieldCommand.class);
        verify(nameNamePopup).show(addKBaseCommandArgumentCaptor.capture());
        addKBaseCommandArgumentCaptor.getValue().setName("NewName");

        verify(nameNamePopup).setOldName("RenameMe");
        assertNull(kModuleModel.get("RenameMe"));
        assertNotNull(kModuleModel.get("NewName"));
    }

    @Test
    public void testRemoveKBaseNoItemSelected() throws Exception {

        KModuleModel kModuleModel = new KModuleModel();
        kModuleModel.add(createKBaseConfiguration("CantRemoveMe"));
        projectEditorServiceCaller.setUpModelForLoading(kModuleModel);
        screenK.init(path, false);

        presenter.onRemove();

        verify(view).showPleaseSelectAnItem();

        assertNotNull(kModuleModel.get("CantRemoveMe"));
        verify(view, never()).remove("CantRemoveMe");
    }

    @Test
    public void testDoubleClickRemoveSecondTimeWithoutATarget() throws Exception {

        KModuleModel kModuleModel = new KModuleModel();
        kModuleModel.add(createKBaseConfiguration("RemoveMe"));
        kModuleModel.add(createKBaseConfiguration("CantRemoveMe"));
        projectEditorServiceCaller.setUpModelForLoading(kModuleModel);
        screenK.init(path, false);

        // Select one and remove.
        presenter.onSelect("RemoveMe");
        presenter.onRemove();

        // Click again, nothing is selected.
        presenter.onRemove();

        verify(view).showPleaseSelectAnItem();

        assertNotNull(kModuleModel.get("CantRemoveMe"));
        verify(view, never()).remove("CantRemoveMe");
    }

    @Test
    public void testSave() throws Exception {
        KModuleModel kModuleModel = new KModuleModel();
        projectEditorServiceCaller.setUpModelForLoading(kModuleModel);
        screenK.init(path, false);

        Metadata metadata = mock(Metadata.class);
        screenK.save("my commit message", new Command() {
            @Override
            public void execute() {

            }
        }, metadata);

        assertEquals(kModuleModel, projectEditorServiceCaller.getSavedModel());
        verify(view).showSaveSuccessful("kmodule.xml");
    }

    private KBaseModel createKBaseConfiguration(String name) {
        KBaseModel knowledgeBaseConfiguration = new KBaseModel();
        knowledgeBaseConfiguration.setName(name);
        return knowledgeBaseConfiguration;
    }
}

