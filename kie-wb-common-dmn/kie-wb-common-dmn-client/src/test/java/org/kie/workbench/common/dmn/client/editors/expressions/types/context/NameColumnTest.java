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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.function.Consumer;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.mocks.MockHasDOMElementResourcesHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.BaseDOMElementSingletonColumnTest;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextBoxDOMElement;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class NameColumnTest extends BaseDOMElementSingletonColumnTest<TextBoxSingletonDOMElementFactory, TextBoxDOMElement, TextBox, NameColumn, ContextGrid> {

    @Mock
    private TextBoxSingletonDOMElementFactory factory;

    @Mock
    private TextBoxDOMElement domElement;

    @Mock
    private TextBox widget;

    @Override
    protected TextBoxSingletonDOMElementFactory getFactory() {
        return factory;
    }

    @Override
    protected TextBoxDOMElement getDomElement() {
        return domElement;
    }

    @Override
    protected TextBox getWidget() {
        return widget;
    }

    @Override
    protected ContextGrid getGridWidget() {
        return mock(ContextGrid.class);
    }

    @Override
    protected NameColumn getColumn() {
        return new NameColumn(headerMetaData,
                              factory,
                              gridWidget);
    }

    @Override
    public void checkEdit() {
        doReturn(0).when(context).getRowIndex();
        model.appendRow(new BaseGridRow());
        model.appendRow(new BaseGridRow());

        super.checkEdit();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkEditLastRow() {
        doReturn(0).when(context).getRowIndex();
        model.appendRow(new BaseGridRow());

        final GridCell<String> cell = new BaseGridCell<>(new BaseGridCellValue<>("value"));

        column.edit(cell,
                    context,
                    result -> {/*Nothing*/});

        verify(factory,
               never()).attachDomElement(any(GridBodyCellRenderContext.class),
                                         any(Consumer.class),
                                         any(Consumer.class));
    }

    @Test
    public void checkHeaderDOMElementsAreDestroyed() {
        final MockHasDOMElementResourcesHeaderMetaData mockHeaderMetaData = mock(MockHasDOMElementResourcesHeaderMetaData.class);
        column.getHeaderMetaData().add(mockHeaderMetaData);

        column.destroyResources();

        verify(mockHeaderMetaData).destroyResources();
    }
}
