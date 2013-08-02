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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.HashMap;
import java.util.Map;

import org.guvnor.common.services.project.model.KSessionModel;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.projecteditor.client.widgets.ListFormComboPanelView;
import org.kie.workbench.common.widgets.client.popups.text.FormPopup;
import org.kie.workbench.common.widgets.client.popups.text.PopupSetFieldCommand;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopup;
import org.mockito.ArgumentCaptor;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class KSessionsPanelTest {

    private KSessionsPanelView view;
    private KSessionsPanel kSessionsPanel;
    private ListFormComboPanelView.Presenter presenter;
    private TextBoxFormPopup namePopup;
    private KSessionForm form;

    @Before
    public void setUp() throws Exception {
        view = mock(KSessionsPanelView.class);
        namePopup = mock(TextBoxFormPopup.class);
        form = mock(KSessionForm.class);
        kSessionsPanel = new KSessionsPanel(view, form, namePopup);
        presenter = kSessionsPanel;
    }

    @Test
    public void testShowEmptyList() throws Exception {
        kSessionsPanel.setItems(new HashMap<String, KSessionModel>());

        verify(view).setPresenter(kSessionsPanel);
        verify(view).clearList();
        verify(view, never()).addItem(anyString());
    }

    @Test
    public void testAddKSession() throws Exception {
        Map<String, KSessionModel> sessions = new HashMap<String, KSessionModel>();
        kSessionsPanel.setItems(sessions);

        presenter.onAdd();


        ArgumentCaptor<PopupSetFieldCommand> addKSessionCommandArgumentCaptor = ArgumentCaptor.forClass(PopupSetFieldCommand.class);
        verify(namePopup).show(addKSessionCommandArgumentCaptor.capture());

        addKSessionCommandArgumentCaptor.getValue().setName("TheOne");

        assertEquals(1, sessions.size());
        assertNotNull(sessions.get("TheOne"));
        verify(view).addItem("TheOne");
        verify(view).setSelected("TheOne");
        verify(form).setModel(sessions.get("TheOne"));
    }

    @Test
    public void testRemoveKSession() throws Exception {
        Map<String, KSessionModel> sessions = new HashMap<String, KSessionModel>();
        KSessionModel model = new KSessionModel();
        model.setName("RemoveMe");
        sessions.put(model.getName(), model);

        kSessionsPanel.setItems(sessions);

        presenter.onSelect("RemoveMe");

        presenter.onRemove();

        assertNull(sessions.get("RemoveMe"));
        verify(view).remove("RemoveMe");
    }

    @Test
    public void testRemoveKSessionNoItemSelected() throws Exception {
        Map<String, KSessionModel> sessions = new HashMap<String, KSessionModel>();
        KSessionModel model = createKSession("CantRemoveMe");
        sessions.put(model.getName(), model);

        kSessionsPanel.setItems(sessions);

        presenter.onRemove();

        verify(view).showPleaseSelectAnItem();

        assertEquals(model, sessions.get("CantRemoveMe"));
        verify(view, never()).remove("CantRemoveMe");
    }

    @Test
    public void testDoubleClickRemoveSecondTimeWithoutATarget() throws Exception {

        Map<String, KSessionModel> sessions = new HashMap<String, KSessionModel>();
        KSessionModel model = createKSession("CantRemoveMe");
        sessions.put(model.getName(), model);
        KSessionModel model2 = createKSession("RemoveMe");
        sessions.put(model2.getName(), model2);

        kSessionsPanel.setItems(sessions);

        // Select one and remove.
        presenter.onSelect("RemoveMe");
        presenter.onRemove();

        // Click again, nothing is selected.
        presenter.onRemove();

        verify(view).showPleaseSelectAnItem();

        assertEquals(model, sessions.get("CantRemoveMe"));
        verify(view, never()).remove("CantRemoveMe");
    }

    private KSessionModel createKSession(String cantRemoveMe) {
        KSessionModel model = new KSessionModel();
        model.setName(cantRemoveMe);
        return model;
    }
}
