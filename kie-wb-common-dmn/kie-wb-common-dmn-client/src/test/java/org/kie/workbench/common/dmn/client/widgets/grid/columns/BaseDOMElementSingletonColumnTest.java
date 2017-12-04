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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;
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
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseDOMElementSingletonColumnTest<F extends BaseSingletonDOMElementFactory, D extends BaseDOMElement, W extends Widget & HasValue & Focusable, C extends BaseGridColumn & HasSingletonDOMElementResource> {

    @Mock
    protected GridColumn.HeaderMetaData headerMetaData;

    @Mock
    protected GridBodyCellRenderContext context;

    @Mock
    protected DMNGridLayer gridLayer;

    @Mock
    protected GridWidget gridWidget;

    @Captor
    protected ArgumentCaptor<Callback<D>> domElementOnCreationCallbackCaptor;

    @Captor
    protected ArgumentCaptor<Callback<D>> domElementOnDisplayCallbackCaptor;

    protected GridData model;

    protected F factory;

    protected D domElement;

    protected W widget;

    protected C column;

    @Before
    public void setup() {
        this.model = new DMNGridData(gridLayer);
        this.factory = getFactory();
        this.domElement = getDomElement();
        this.widget = getWidget();
        this.column = getColumn();
        when(domElement.getWidget()).thenReturn(widget);
        when(gridWidget.getModel()).thenReturn(model);
    }

    protected abstract F getFactory();

    protected abstract D getDomElement();

    protected abstract W getWidget();

    protected abstract C getColumn();

    @Test
    @SuppressWarnings("unchecked")
    public void checkEdit() {
        final GridCell<String> cell = new BaseGridCell<>(new BaseGridCellValue<>("value"));

        column.edit(cell,
                    context,
                    result -> {/*Nothing*/});

        verify(factory).attachDomElement(eq(context),
                                         domElementOnCreationCallbackCaptor.capture(),
                                         domElementOnDisplayCallbackCaptor.capture());

        final Callback<D> domElementOnCreationCallback = domElementOnCreationCallbackCaptor.getValue();
        domElementOnCreationCallback.callback(domElement);
        verify(widget).setValue(eq("value"));

        final Callback<D> domElementOnDisplayCallback = domElementOnDisplayCallbackCaptor.getValue();
        domElementOnDisplayCallback.callback(domElement);
        verify(widget).setFocus(eq(true));
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
}
