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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextArea;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextAreaDOMElement;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class RelationColumnTest {

    @Mock
    private GridColumn.HeaderMetaData headerMetaData;

    @Mock
    private TextAreaSingletonDOMElementFactory factory;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private GridBodyCellRenderContext context;

    @Mock
    private Callback callback;

    @Mock
    private TextArea textArea;

    @Mock
    private TextAreaDOMElement textAreaDOMElement;

    @Captor
    private ArgumentCaptor<Callback<TextAreaDOMElement>> textAreaDOMElementCallback;

    private RelationColumn relationColumn;

    @Before
    public void setUp() throws Exception {
        relationColumn = new RelationColumn(headerMetaData,
                                            factory,
                                            gridWidget);

        doReturn(textArea).when(textAreaDOMElement).getWidget();
    }

    @Test
    public void testEditNullCell() throws Exception {
        relationColumn.edit(null, context, callback);
        verify(factory).attachDomElement(eq(context), textAreaDOMElementCallback.capture(), any(Callback.class));
        textAreaDOMElementCallback.getValue().callback(textAreaDOMElement);
        verify(textArea).setValue("");
    }

    @Test
    public void testEditEmptyCell() throws Exception {
        relationColumn.edit(new BaseGridCell<>(null), context, callback);
        verify(factory).attachDomElement(eq(context), textAreaDOMElementCallback.capture(), any(Callback.class));
        textAreaDOMElementCallback.getValue().callback(textAreaDOMElement);
        verify(textArea).setValue("");
    }

    @Test
    public void testEditCell() throws Exception {
        final String cellValue = "abc";
        relationColumn.edit(new BaseGridCell<>(new BaseGridCellValue<>(cellValue)), context, callback);
        verify(factory).attachDomElement(eq(context), textAreaDOMElementCallback.capture(), any(Callback.class));
        textAreaDOMElementCallback.getValue().callback(textAreaDOMElement);
        verify(textArea).setValue(cellValue);
    }
}
