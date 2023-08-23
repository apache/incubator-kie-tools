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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
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
public class KindPopoverViewImplTest {

    @Mock
    private KindPopoverView.Presenter presenter;

    @Mock
    private UnorderedList definitionsContainer;

    @Mock
    private ManagedInstance<ListSelectorTextItemView> listSelectorTextItemViews;

    @Mock
    private Div popoverElement;

    @Mock
    private Div popoverContentElement;

    @Mock
    private JQueryProducer.JQuery<Popover> jQueryPopover;

    @Mock
    private ListSelectorTextItemView textItemView1;

    @Mock
    private ListSelectorTextItemView textItemView2;

    @Mock
    private ListSelectorTextItemView textItemView3;

    @Mock
    private HTMLElement textElement1;

    @Mock
    private HTMLElement textElement2;

    @Mock
    private HTMLElement textElement3;

    private KindPopoverViewImpl view;

    @Before
    public void setup() {
        view = new KindPopoverViewImpl(definitionsContainer,
                                       listSelectorTextItemViews,
                                       popoverElement,
                                       popoverContentElement,
                                       jQueryPopover);

        view.init(presenter);

        when(textItemView1.getElement()).thenReturn(textElement1);
        when(textItemView2.getElement()).thenReturn(textElement2);
        when(textItemView3.getElement()).thenReturn(textElement3);

        when(listSelectorTextItemViews.get()).thenReturn(textItemView1, textItemView2, textItemView3);
    }

    @Test
    public void testSetFunctionKinds() {
        view.setFunctionKinds(FunctionDefinition.Kind.values());

        verify(textItemView1).setText(FunctionDefinition.Kind.FEEL.name());
        verify(textItemView2).setText(FunctionDefinition.Kind.JAVA.name());
        verify(textItemView3).setText(FunctionDefinition.Kind.PMML.name());
        verify(definitionsContainer).appendChild(textElement1);
        verify(definitionsContainer).appendChild(textElement2);
        verify(definitionsContainer).appendChild(textElement3);

        final ArgumentCaptor<Command> commandCaptor1 = ArgumentCaptor.forClass(Command.class);
        verify(textItemView1).addClickHandler(commandCaptor1.capture());

        commandCaptor1.getValue().execute();
        verify(presenter).onFunctionKindSelected(eq(FunctionDefinition.Kind.FEEL));

        final ArgumentCaptor<Command> commandCaptor2 = ArgumentCaptor.forClass(Command.class);
        verify(textItemView2).addClickHandler(commandCaptor2.capture());

        commandCaptor2.getValue().execute();
        verify(presenter).onFunctionKindSelected(eq(FunctionDefinition.Kind.JAVA));

        final ArgumentCaptor<Command> commandCaptor3 = ArgumentCaptor.forClass(Command.class);
        verify(textItemView3).addClickHandler(commandCaptor3.capture());

        commandCaptor3.getValue().execute();
        verify(presenter).onFunctionKindSelected(eq(FunctionDefinition.Kind.PMML));
    }
}