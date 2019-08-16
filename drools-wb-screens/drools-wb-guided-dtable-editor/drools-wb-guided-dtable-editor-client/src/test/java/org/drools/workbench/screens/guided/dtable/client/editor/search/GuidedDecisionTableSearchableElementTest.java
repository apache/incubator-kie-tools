/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.search;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTableSearchableElementTest {

    @Mock
    private GuidedDecisionTableModellerView.Presenter modeller;

    @Mock
    private GuidedDecisionTableGridHighlightHelper highlightHelper;

    private GuidedDecisionTableSearchableElement element;

    @Before
    public void setup() {
        element = spy(new GuidedDecisionTableSearchableElement());
    }

    @Test
    public void testMatchesWhenItReturnsTrue() {
        element.setValue("element");
        final boolean matches = element.matches("ELE");
        assertTrue(matches);
    }

    @Test
    public void testMatchesWhenItReturnsFalse() {
        element.setValue("element");
        final boolean matches = element.matches("LEE");
        assertFalse(matches);
    }

    @Test
    public void testOnFound() {

        final GuidedDecisionTableView widget = mock(GuidedDecisionTableView.class);
        final int row = 2;
        final int column = 4;

        element.setModeller(modeller);
        element.setHighlightHelper(highlightHelper);
        element.setRow(row);
        element.setColumn(column);
        element.setWidget(widget);

        element.onFound().execute();

        verify(highlightHelper).highlight(row, column, widget, modeller);
    }
}
