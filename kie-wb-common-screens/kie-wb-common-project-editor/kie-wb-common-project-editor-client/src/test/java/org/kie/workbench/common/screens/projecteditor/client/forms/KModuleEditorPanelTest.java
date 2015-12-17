/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.projecteditor.client.widgets.ListFormComboPanelView;
import org.kie.workbench.common.widgets.client.popups.text.PopupSetFieldCommand;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopup;
import org.mockito.ArgumentCaptor;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class KModuleEditorPanelTest {

    private KModuleEditorPanelView view;
    private KModuleEditorPanel screenK;
    private ListFormComboPanelView.Presenter presenter;
    private TextBoxFormPopup nameNamePopup;
    private KBaseForm form;

    @Before
    public void setUp() throws Exception {
        view = mock(KModuleEditorPanelView.class);

        nameNamePopup = mock(TextBoxFormPopup.class);
        form = mock(KBaseForm.class);
        screenK = new KModuleEditorPanel( form, nameNamePopup, view);
        presenter = screenK;
    }

    @Test
    public void testShowEmptyModel() throws Exception {
        verify(view, never()).addItem(anyString());
    }

    @Test
    public void testShowModelWithSessions() throws Exception {

        KModuleModel kModuleModel = new KModuleModel();
        kModuleModel.add(createKBaseConfiguration("First"));
        kModuleModel.add(createKBaseConfiguration("Second"));
        kModuleModel.add(createKBaseConfiguration("Third"));
        screenK.setData(kModuleModel, false);

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
        screenK.setData(kModuleModel, false);

        presenter.onSelect("TheOne");

        verify(form).setModel(theOne);
    }

    @Test
    public void testAddKBase() throws Exception {

        KModuleModel kModuleModel = new KModuleModel();
        screenK.setData(kModuleModel, false);

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
        screenK.setData(kModuleModel, false);

        presenter.onSelect("RemoveMe");

        presenter.onRemove();

        assertNull(kModuleModel.get("RemoveMe"));
        verify(view).remove("RemoveMe");
    }

    @Test
    public void testRename() throws Exception {

        KModuleModel kModuleModel = new KModuleModel();
        kModuleModel.add(createKBaseConfiguration("RenameMe"));
        screenK.setData(kModuleModel, false);

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
        screenK.setData(kModuleModel, false);

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
        screenK.setData(kModuleModel, false);

        // Select one and remove.
        presenter.onSelect("RemoveMe");
        presenter.onRemove();

        // Click again, nothing is selected.
        presenter.onRemove();

        verify(view).showPleaseSelectAnItem();

        assertNotNull(kModuleModel.get("CantRemoveMe"));
        verify(view, never()).remove("CantRemoveMe");
    }

    private KBaseModel createKBaseConfiguration(String name) {
        KBaseModel knowledgeBaseConfiguration = new KBaseModel();
        knowledgeBaseConfiguration.setName(name);
        return knowledgeBaseConfiguration;
    }
}

