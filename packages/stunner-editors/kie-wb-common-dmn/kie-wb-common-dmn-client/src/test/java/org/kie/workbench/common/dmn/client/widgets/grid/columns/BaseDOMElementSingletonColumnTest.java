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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.function.Consumer;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.HasSingletonDOMElementResource;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.BaseSingletonDOMElementFactory;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseDOMElementSingletonColumnTest<F extends BaseSingletonDOMElementFactory, D extends BaseDOMElement & TakesValue<String> & Focusable, W extends Widget & Focusable, C extends BaseGridColumn & HasSingletonDOMElementResource, G extends BaseExpressionGrid> {

    private static final String DEFAULT_VALUE = "";

    @Mock
    protected GridColumn.HeaderMetaData headerMetaData;

    @Mock
    protected GridBodyCellRenderContext context;

    @Captor
    protected ArgumentCaptor<Consumer<D>> domElementOnCreationCallbackCaptor;

    @Captor
    protected ArgumentCaptor<Consumer<D>> domElementOnDisplayCallbackCaptor;

    protected GridData model;

    protected F factory;

    protected D domElement;

    protected W widget;

    protected G gridWidget;

    protected C column;

    @Before
    public void setup() {
        this.model = new DMNGridData();
        this.factory = getFactory();
        this.domElement = getDomElement();
        this.widget = getWidget();
        this.gridWidget = getGridWidget();
        this.column = getColumn();
        when(domElement.getWidget()).thenReturn(widget);
        when(gridWidget.getModel()).thenReturn(model);
    }

    protected abstract F getFactory();

    protected abstract D getDomElement();

    protected abstract W getWidget();

    protected abstract G getGridWidget();

    protected abstract C getColumn();

    @Test
    @SuppressWarnings("unchecked")
    public void checkEdit() {
        final String value = "value";
        final GridCell<String> cell = new BaseGridCell<>(new BaseGridCellValue<>(value));

        column.edit(cell,
                    context,
                    result -> {/*Nothing*/});

        assertCellEdit(value);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkEditNullCell() {
        column.edit(null,
                    context,
                    result -> {/*Nothing*/});

        assertCellEdit(DEFAULT_VALUE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkEditNullCellValue() {
        final GridCell<String> cell = new BaseGridCell<>(null);

        column.edit(cell,
                    context,
                    result -> {/*Nothing*/});

        assertCellEdit(DEFAULT_VALUE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkEditNullCellValueValue() {
        final GridCell<String> cell = new BaseGridCell<>(new BaseGridCellValue<>(null));

        column.edit(cell,
                    context,
                    result -> {/*Nothing*/});

        assertCellEdit(DEFAULT_VALUE);
    }

    @SuppressWarnings("unchecked")
    protected void assertCellEdit(final String value) {
        verify(factory).attachDomElement(eq(context),
                                         domElementOnCreationCallbackCaptor.capture(),
                                         domElementOnDisplayCallbackCaptor.capture());

        final Consumer<D> domElementOnCreationCallback = domElementOnCreationCallbackCaptor.getValue();
        domElementOnCreationCallback.accept(domElement);
        verify(domElement).setValue(eq(value));

        final Consumer<D> domElementOnDisplayCallback = domElementOnDisplayCallbackCaptor.getValue();
        domElementOnDisplayCallback.accept(domElement);
        verify(domElement).setFocus(eq(true));
    }

    @Test
    public void checkFlushFactory() {
        column.flush();

        verify(factory).flush();
    }

    @Test
    public void checkDestroyFactoryResources() {
        column.destroyResources();

        verify(factory).destroyResources();
    }

    @Test
    public void testDestroyHeaderMetadataAdditionalDomElements() {
        final EditableHeaderMetaData editableHeaderMetaData = mock(EditableHeaderMetaData.class);
        column.getHeaderMetaData().add(editableHeaderMetaData);

        column.destroyResources();

        verify(editableHeaderMetaData).destroyResources();
    }
}
