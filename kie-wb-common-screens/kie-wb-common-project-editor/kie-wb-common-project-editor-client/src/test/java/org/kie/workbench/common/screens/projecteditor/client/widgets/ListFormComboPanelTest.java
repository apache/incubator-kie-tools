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

package org.kie.workbench.common.screens.projecteditor.client.widgets;

import java.util.HashMap;

import org.guvnor.common.services.project.model.HasListFormComboPanelProperties;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.widgets.client.popups.text.FormPopup;
import org.kie.workbench.common.widgets.client.popups.text.PopupSetFieldCommand;
import org.mockito.ArgumentCaptor;

import static org.jgroups.util.Util.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ListFormComboPanelTest {

    private ListFormComboPanel<HasListFormComboPanelProperties> panel;
    private ListFormComboPanelView view;
    private ListFormComboPanelView.Presenter presenter;
    private Form form;
    private FormPopup namePopup;
    private HashMap<String, HasListFormComboPanelProperties> items;

    @Before
    public void setUp() throws Exception {
        view = mock(ListFormComboPanelView.class);
        form = mock(Form.class);
        namePopup = mock(FormPopup.class);
        panel = new ListFormComboPanel<HasListFormComboPanelProperties>(view, form, namePopup) {
            @Override
            protected HasListFormComboPanelProperties createNew(String name) {
                return newItem(name);
            }
        };

        items = new HashMap<String, HasListFormComboPanelProperties>();

        presenter = panel;
    }

    @Test
    public void testDefault() throws Exception {
        items.put("a", createItem("a", false));
        items.put("b", createItem("b", true));
        items.put("c", createItem("c", false));
        panel.setItems(items);

        presenter.onSelect("a");
        presenter.onSelect("b");
        presenter.onSelect("c");

        verify(view, times(2)).enableMakeDefault();
        verify(view).disableMakeDefault();

    }

    @Test
    public void testAddNew() throws Exception {
        panel.setItems(items);

        presenter.onAdd();

        ArgumentCaptor<PopupSetFieldCommand> commandArgumentCaptor = ArgumentCaptor.forClass(PopupSetFieldCommand.class);
        verify(namePopup).show(commandArgumentCaptor.capture());

        commandArgumentCaptor.getValue().setName("kbase1");

        verify(view).addItem("kbase1");
        ArgumentCaptor<HasListFormComboPanelProperties> propertiesArgumentCaptor = ArgumentCaptor.forClass(HasListFormComboPanelProperties.class);
        verify(form).setModel(propertiesArgumentCaptor.capture());
        assertEquals("kbase1", propertiesArgumentCaptor.getValue().getName());
        verify(view).enableItemEditingButtons();
        verify(view).enableMakeDefault();
        verify(view).setSelected("kbase1");
        assertTrue(items.containsKey("kbase1"));

    }

    @Test
    public void testDelete() throws Exception {
        items.put("kbase", newItem("kbase"));
        panel.setItems(items);

        presenter.onSelect("kbase");
        presenter.onRemove();

        verify(view).disableMakeDefault();
        verify(view).disableItemEditingButtons();
        verify(view).remove("kbase");
        verify(form).clear();

        assertTrue(items.isEmpty());
    }

    private HasListFormComboPanelProperties createItem(final String name, final boolean b) {
        HasListFormComboPanelProperties item = new HasListFormComboPanelProperties() {

            String theName;
            boolean theDefault;

            @Override
            public String getName() {
                return theName;
            }

            @Override
            public void setName(String theName) {
                this.theName = theName;
            }

            @Override
            public boolean isDefault() {
                return theDefault;
            }

            @Override
            public void setDefault(boolean theDefault) {
                this.theDefault = theDefault;
            }
        };

        item.setName(name);
        item.setDefault(b);

        return item;
    }

    private HasListFormComboPanelProperties newItem(String name) {
        HasListFormComboPanelProperties result = new HasListFormComboPanelProperties() {
            String name;
            boolean isDefault;

            @Override public String getName() {
                return name;
            }

            @Override public void setName(String name) {
                this.name = name;
            }

            @Override public boolean isDefault() {
                return isDefault;
            }

            @Override public void setDefault(boolean theDefault) {
                isDefault = theDefault;
            }
        };

        result.setName(name);

        return result;
    }
}
