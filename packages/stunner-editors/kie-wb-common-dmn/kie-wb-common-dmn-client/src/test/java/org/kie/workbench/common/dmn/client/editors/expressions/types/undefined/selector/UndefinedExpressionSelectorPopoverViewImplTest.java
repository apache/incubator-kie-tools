/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector;

import java.util.Arrays;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorTextItemView;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.mvp.Command;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class UndefinedExpressionSelectorPopoverViewImplTest {

    private static final String DEFINITION_NAME1 = "definition1";

    private static final String DEFINITION_NAME2 = "definition2";

    @Mock
    private UnorderedList itemsContainer;

    @Mock
    private ManagedInstance<ListSelectorTextItemView> listSelectorTextItemViews;

    @Mock
    private Div popoverElement;

    @Mock
    private Div popoverContentElement;

    @Mock
    private JQueryProducer.JQuery<Popover> jQueryPopover;

    @Mock
    private UndefinedExpressionSelectorPopoverView.Presenter presenter;

    @Mock
    private ExpressionEditorDefinition expressionEditorDefinition1;

    @Mock
    private ExpressionEditorDefinition expressionEditorDefinition2;

    @Mock
    private ListSelectorTextItemView textItemView1;

    @Mock
    private ListSelectorTextItemView textItemView2;

    @Mock
    private HTMLElement textElement1;

    @Mock
    private HTMLElement textElement2;

    private UndefinedExpressionSelectorPopoverViewImpl view;

    @Before
    public void setUp() throws Exception {
        view = new UndefinedExpressionSelectorPopoverViewImpl(itemsContainer,
                                                              listSelectorTextItemViews,
                                                              popoverElement,
                                                              popoverContentElement,
                                                              jQueryPopover);
        view.init(presenter);

        when(expressionEditorDefinition1.getName()).thenReturn(DEFINITION_NAME1);
        when(textItemView1.getElement()).thenReturn(textElement1);

        when(expressionEditorDefinition2.getName()).thenReturn(DEFINITION_NAME2);
        when(textItemView2.getElement()).thenReturn(textElement2);

        when(listSelectorTextItemViews.get()).thenReturn(textItemView1, textItemView2);
    }

    @Test
    public void testSetExpressionEditorDefinitions() {
        view.setExpressionEditorDefinitions(Arrays.asList(expressionEditorDefinition1, expressionEditorDefinition2));

        verify(textItemView1).setText(DEFINITION_NAME1);
        verify(textItemView2).setText(DEFINITION_NAME2);
        verify(itemsContainer).appendChild(textElement1);
        verify(itemsContainer).appendChild(textElement2);

        final ArgumentCaptor<Command> commandCaptor1 = ArgumentCaptor.forClass(Command.class);
        verify(textItemView1).addClickHandler(commandCaptor1.capture());

        commandCaptor1.getValue().execute();
        verify(presenter).onExpressionEditorDefinitionSelected(eq(expressionEditorDefinition1));

        final ArgumentCaptor<Command> commandCaptor2 = ArgumentCaptor.forClass(Command.class);
        verify(textItemView2).addClickHandler(commandCaptor2.capture());

        commandCaptor2.getValue().execute();
        verify(presenter).onExpressionEditorDefinitionSelected(eq(expressionEditorDefinition2));
    }
}
