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

package org.kie.workbench.projecteditor.client.widgets;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.widgets.client.popups.text.FormPopup;
import org.kie.workbench.projecteditor.client.widgets.Form;
import org.kie.workbench.projecteditor.client.widgets.ListFormComboPanel;
import org.kie.workbench.projecteditor.client.widgets.ListFormComboPanelView;
import org.kie.guvnor.project.model.HasListFormComboPanelProperties;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class ListFormComboPanelTest {

    private ListFormComboPanel<HasListFormComboPanelProperties> panel;
    private ListFormComboPanelView view;
    private ListFormComboPanelView.Presenter presenter;

    @Before
    public void setUp() throws Exception {
        view = mock(ListFormComboPanelView.class);
        Form form = mock(Form.class);
        FormPopup namePopup = mock(FormPopup.class);
        panel = new ListFormComboPanel<HasListFormComboPanelProperties>(view, form, namePopup) {
            @Override
            protected HasListFormComboPanelProperties createNew(String name) {
                return null;  //TODO -Rikkola-
            }
        };

        presenter = panel;
    }

    @Test
    public void testDefault() throws Exception {
        Map<String, HasListFormComboPanelProperties> items = new HashMap<String, HasListFormComboPanelProperties>();
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
}
