/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.accordion;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTableAccordionTest {

    @Mock
    private GuidedDecisionTableAccordion.View view;

    @Mock
    private ManagedInstance<GuidedDecisionTableAccordionItem> itemManagedInstance;

    private GuidedDecisionTableAccordion accordion;

    @Before
    public void setup() throws Exception {
        accordion = spy(new GuidedDecisionTableAccordion(view,
                                                         itemManagedInstance));
    }

    @Test
    public void testSetup() throws Exception {
        accordion.setup();

        verify(view).init(accordion);
    }

    @Test
    public void testAddItem() throws Exception {
        final String parentId = "uuid";
        final GuidedDecisionTableAccordionItem item = mock(GuidedDecisionTableAccordionItem.class);
        final GuidedDecisionTableAccordionItem.Type type = GuidedDecisionTableAccordionItem.Type.CONDITION;
        final Widget widget = mock(Widget.class);
        final List<GuidedDecisionTableAccordionItem> itemsList = spy(new ArrayList<>());

        when(accordion.blankAccordionItem()).thenReturn(item);
        when(accordion.getItems()).thenReturn(itemsList);
        when(accordion.getParentId()).thenReturn(parentId);

        accordion.addItem(type,
                          widget);

        verify(view).addItem(item);
        verify(itemsList).add(item);
        verify(item).init(parentId,
                          type,
                          widget
        );
    }

    @Test
    public void testGetItemWhenItemExists() throws Exception {
        final GuidedDecisionTableAccordionItem.Type type = GuidedDecisionTableAccordionItem.Type.CONDITION;
        final GuidedDecisionTableAccordionItem expectedItem = mock(GuidedDecisionTableAccordionItem.class);
        final List<GuidedDecisionTableAccordionItem> itemsList = spy(new ArrayList<GuidedDecisionTableAccordionItem>() {{
            add(expectedItem);
        }});

        when(expectedItem.getType()).thenReturn(type);
        when(accordion.getItems()).thenReturn(itemsList);

        final GuidedDecisionTableAccordionItem item = accordion.getItem(type);

        assertEquals(expectedItem,
                     item);
    }

    @Test
    public void testGetItemWhenItemDoesNotExist() throws Exception {
        final GuidedDecisionTableAccordionItem.Type type = GuidedDecisionTableAccordionItem.Type.CONDITION;
        final GuidedDecisionTableAccordionItem blankItem = mock(GuidedDecisionTableAccordionItem.class);
        final List<GuidedDecisionTableAccordionItem> itemsList = spy(new ArrayList<>());

        when(accordion.blankAccordionItem()).thenReturn(blankItem);
        when(accordion.getItems()).thenReturn(itemsList);

        final GuidedDecisionTableAccordionItem item = accordion.getItem(type);

        assertEquals(blankItem,
                     item);
    }

    @Test
    public void testBlankAccordionItem() throws Exception {
        final GuidedDecisionTableAccordionItem expectedItem = mock(GuidedDecisionTableAccordionItem.class);

        when(itemManagedInstance.get()).thenReturn(expectedItem);

        final GuidedDecisionTableAccordionItem item = accordion.blankAccordionItem();

        verify(itemManagedInstance).get();

        assertEquals(expectedItem,
                     item);
    }
}
