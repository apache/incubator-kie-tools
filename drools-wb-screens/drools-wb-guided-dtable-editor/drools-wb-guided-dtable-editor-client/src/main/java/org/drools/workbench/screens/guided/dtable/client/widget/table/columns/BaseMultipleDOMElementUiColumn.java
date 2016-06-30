/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.multiple.HasMultipleDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.dom.multiple.MultipleDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.multiple.impl.BaseGridColumnMultipleDOMElementRenderer;

/**
 * Base column for Decision Tables.
 * @param <T> The Type of value presented by this column
 * @param <F> The Factory to create DOMElements for this column
 */
public abstract class BaseMultipleDOMElementUiColumn<T, W extends Widget, E extends BaseDOMElement, F extends MultipleDOMElementFactory<W, E>> extends BaseUiColumn<T> implements HasMultipleDOMElementResources {

    protected F factory;

    public BaseMultipleDOMElementUiColumn( final List<HeaderMetaData> headerMetaData,
                                           final BaseGridColumnMultipleDOMElementRenderer<T, W, E> columnRenderer,
                                           final double width,
                                           final boolean isResizable,
                                           final boolean isVisible,
                                           final GuidedDecisionTablePresenter.Access access,
                                           final F factory ) {
        super( headerMetaData,
               columnRenderer,
               width,
               isResizable,
               isVisible,
               access );
        setResizable( isResizable );
        setVisible( isVisible );
        this.factory = factory;
    }

    @Override
    public void initialiseResources() {
        factory.initialiseResources();
    }

    @Override
    public void freeUnusedResources() {
        factory.freeUnusedResources();
    }

    @Override
    public void destroyResources() {
        factory.destroyResources();
    }

}
