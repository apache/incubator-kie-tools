/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.treegrid;

import java.util.Arrays;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTableSectionElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeTreeGridView_AttributeTooltip;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeTreeGridView_TypeTooltip;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeTreeGridViewTest {

    @Mock
    private HTMLTableSectionElement gridItems;

    @Mock
    private HTMLElement attributeTooltip;

    @Mock
    private HTMLElement typeTooltip;

    @Mock
    private TranslationService translationService;

    private DataTypeTreeGridView view;

    @Before
    public void setup() {
        view = spy(new DataTypeTreeGridView(gridItems, attributeTooltip, typeTooltip, translationService));
    }

    @Test
    public void testSetupTooltips() {

        final HTMLElement htmlElement = mock(HTMLElement.class);
        final String attributeTooltip = "AttributeTooltip";
        final String typeTooltip = "TypeTooltip";

        when(translationService.format(DataTypeTreeGridView_AttributeTooltip)).thenReturn(attributeTooltip);
        when(translationService.format(DataTypeTreeGridView_TypeTooltip)).thenReturn(typeTooltip);
        doReturn(htmlElement).when(view).getElement();

        view.setupTooltips();

        verify(this.attributeTooltip).setAttribute("title", attributeTooltip);
        verify(this.typeTooltip).setAttribute("title", typeTooltip);
    }

    @Test
    public void testSetupGridItems() {

        final DataTypeTreeGridItem gridItem1 = mock(DataTypeTreeGridItem.class);
        final DataTypeTreeGridItem gridItem2 = mock(DataTypeTreeGridItem.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);

        when(gridItem1.getElement()).thenReturn(element1);
        when(gridItem2.getElement()).thenReturn(element2);

        view.setupGridItems(Arrays.asList(gridItem1, gridItem2));

        verify(gridItems).appendChild(eq(element1));
        verify(gridItems).appendChild(eq(element2));
    }
}
