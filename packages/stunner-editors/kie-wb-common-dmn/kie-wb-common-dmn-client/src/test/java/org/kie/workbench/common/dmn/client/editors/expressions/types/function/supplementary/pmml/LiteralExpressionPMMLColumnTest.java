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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml;

import java.util.List;
import java.util.function.Consumer;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.ui.ListBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.BaseDOMElementSingletonColumnTest;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.ListBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.ListBoxDOMElement;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class LiteralExpressionPMMLColumnTest extends BaseDOMElementSingletonColumnTest<ListBoxSingletonDOMElementFactory, ListBoxDOMElement, ListBox, LiteralExpressionPMMLColumn, LiteralExpressionPMMLGrid> {

    private static final String LISTBOX_ENTRY1 = "entry1";

    private static final String LISTBOX_ENTRY2 = "entry2";

    @Mock
    private ListBoxSingletonDOMElementFactory factory;

    @Mock
    private ListBoxDOMElement domElement;

    @Mock
    private ListBox widget;

    @Override
    protected ListBoxSingletonDOMElementFactory getFactory() {
        return factory;
    }

    @Override
    protected ListBoxDOMElement getDomElement() {
        return domElement;
    }

    @Override
    protected ListBox getWidget() {
        return widget;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected LiteralExpressionPMMLGrid getGridWidget() {
        final LiteralExpressionPMMLGrid gridWidget = mock(LiteralExpressionPMMLGrid.class);
        doAnswer(i -> {
            final Consumer<List<String>> consumer = (Consumer) i.getArguments()[0];
            consumer.accept(asList(LISTBOX_ENTRY1, LISTBOX_ENTRY2));
            return null;
        }).when(gridWidget).loadValues(any(Consumer.class));

        return gridWidget;
    }

    @Override
    protected LiteralExpressionPMMLColumn getColumn() {
        return new LiteralExpressionPMMLColumn(factory,
                                               DMNGridColumn.DEFAULT_WIDTH,
                                               gridWidget);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListBoxInitialisation() {
        final GridCell<String> cell = new BaseGridCell<>(new BaseGridCellValue<>(LISTBOX_ENTRY1));

        column.edit(cell,
                    context,
                    result -> {/*Nothing*/});

        assertCellEdit(LISTBOX_ENTRY1);

        verify(widget).clear();
        verify(widget).addItem(LISTBOX_ENTRY1, "\"" + LISTBOX_ENTRY1 + "\"");
        verify(widget).addItem(LISTBOX_ENTRY2, "\"" + LISTBOX_ENTRY2 + "\"");
    }
}
