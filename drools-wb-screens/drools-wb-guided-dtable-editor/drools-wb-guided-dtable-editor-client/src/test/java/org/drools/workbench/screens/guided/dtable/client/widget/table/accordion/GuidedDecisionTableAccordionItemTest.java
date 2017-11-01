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

package org.drools.workbench.screens.guided.dtable.client.widget.table.accordion;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTableAccordionItemTest {

    @Mock
    private GuidedDecisionTableAccordionItem.View view;

    @Mock
    private TranslationService translationService;

    private GuidedDecisionTableAccordionItem item;

    @Before
    public void setup() throws Exception {
        item = spy(new GuidedDecisionTableAccordionItem(view,
                                                        translationService));
    }

    @Test
    public void testSetup() throws Exception {
        item.setup();

        verify(view).init(item);
    }

    @Test
    public void testInit() throws Exception {
        final GuidedDecisionTableAccordionItem.Type type = GuidedDecisionTableAccordionItem.Type.CONDITION;
        final Widget widget = mock(Widget.class);

        item.init("uuid",
                  type,
                  widget
        );

        verify(item).refreshView();

        assertEquals(type,
                     item.getType());
        assertEquals(widget,
                     item.getContent());
    }

    @Test
    public void testGetTitle() throws Exception {
        final GuidedDecisionTableAccordionItem.Type type = GuidedDecisionTableAccordionItem.Type.CONDITION;
        final String title = "title";

        when(translationService.format(type.getTitleKey())).thenReturn(title);
        when(item.getType()).thenReturn(type);

        final String itemTitle = item.getTitle();

        assertEquals(title,
                     itemTitle);
    }

    @Test
    public void testSetOpen() throws Exception {
        final boolean isOpen = false;

        item.setOpen(isOpen);

        verify(view).setOpen(isOpen);
    }

    @Test
    public void testRefreshView() throws Exception {
        final GuidedDecisionTableAccordionItem.Type type = GuidedDecisionTableAccordionItem.Type.CONDITION;
        final String title = "title";
        final Widget widget = mock(Widget.class);

        when(item.getType()).thenReturn(type);
        when(item.getTitle()).thenReturn(title);
        when(item.getContent()).thenReturn(widget);

        item.refreshView();

        verify(view).setItemId(anyString());
        verify(view).setTitle(title);
        verify(view).setContent(widget);
    }
}
