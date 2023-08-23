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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RuleAnnotationClauseColumnTest {

    @Mock
    private Supplier<List<GridColumn.HeaderMetaData>> headerMetaDataSupplier;

    @Mock
    private TextAreaSingletonDOMElementFactory factory;

    private final double width = 100;

    @Mock
    private DecisionTableGrid gridWidget;

    private RuleAnnotationClauseColumn column;

    @Before
    public void setup() {

        when(headerMetaDataSupplier.get()).thenReturn(new ArrayList<>());
        column = spy(new RuleAnnotationClauseColumn(headerMetaDataSupplier,
                                                    factory,
                                                    width,
                                                    gridWidget));
    }

    @Test
    public void testEdit() {

        final GridCell<String> cell = mock(GridCell.class);
        final GridBodyCellEditContext context = mock(GridBodyCellEditContext.class);
        final Consumer callback = mock(Consumer.class);
        final Consumer onDisplay = mock(Consumer.class);
        final Consumer onCreation = mock(Consumer.class);

        doReturn(onDisplay).when(column).getTextAreaDOMElementConsumerOnDisplay();
        doReturn(onCreation).when(column).getTextAreaDOMElementConsumerOnCreation(cell);

        column.edit(cell, context, callback);

        verify(factory).attachDomElement(context,
                                         onCreation,
                                         onDisplay);
    }

    @Test
    public void testDestroyResources() {

        doNothing().when(column).superDestroyResources();

        column.destroyResources();

        verify(column).superDestroyResources();
        verify(factory).destroyResources();
    }

    @Test
    public void testSetWidth() {

        doNothing().when(column).superSetWidth(width);
        doNothing().when(column).updateWidthOfPeers();

        column.setWidth(width);

        verify(column).superSetWidth(width);
        verify(column).updateWidthOfPeers();
    }
}