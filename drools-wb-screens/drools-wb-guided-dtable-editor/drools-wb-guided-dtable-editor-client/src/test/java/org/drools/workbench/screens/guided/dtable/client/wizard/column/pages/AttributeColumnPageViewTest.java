/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.gwtbootstrap3.client.ui.ListBox;
import org.jboss.errai.common.client.dom.Div;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AttributeColumnPageViewTest {

    @Mock
    private AttributeColumnPage page;
    @Mock
    private ListBox attributeListBox;

    private AttributeColumnPageView view;

    @Before
    public void setUp() throws Exception {
        view  = spy(new AttributeColumnPageView(attributeListBox, mock(Div.class)));
        view.init(page);
    }

    @Test
    public void testInitView() throws Exception {
        verify(view).hideAttributeDescription();
    }

    @Test
    public void testAttributeSelectedFirstTime() throws Exception {
        final String selectedAttribute = Attribute.NO_LOOP.getAttributeName();
        when(attributeListBox.getSelectedItemText()).thenReturn(selectedAttribute);
        when(view.isAttributeDescriptionHidden()).thenReturn(true);
        view.onSelectAttribute(null);

        verify(view).showAttributeDescription();
    }

    @Test
    public void testAttributeSelectedNotFirstTime() throws Exception {
        final String selectedAttribute = Attribute.NO_LOOP.getAttributeName();
        when(attributeListBox.getSelectedItemText()).thenReturn(selectedAttribute);
        when(view.isAttributeDescriptionHidden()).thenReturn(false);
        view.onSelectAttribute(null);

        verify(view, never()).showAttributeDescription();
    }
}
