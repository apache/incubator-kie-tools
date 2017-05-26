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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.BaseSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

/**
 * Base DOM Element Factory for the Guided Decision Table Editor for singleton overlays.
 * This implementation separates "flushing" the value from the Widget to the Model from
 * destroying the Widget.
 * @param <T> Data-type handled by this factory.
 * @param <W> Widget representing the data-type.
 * @param <E> DOM element representing the data-type.
 */
public abstract class BaseDTSingletonDOMElementFactory<T, W extends Widget, E extends BaseDOMElement<T, W>> extends BaseSingletonDOMElementFactory<T, W, E> {

    public BaseDTSingletonDOMElementFactory(final GridLienzoPanel gridPanel,
                                            final GridLayer gridLayer,
                                            final GridWidget gridWidget) {
        super(gridPanel,
              gridLayer,
              gridWidget);
    }

    @Override
    public void destroyResources() {
        if (e != null) {
            e.detach();
            widget = null;
            e = null;
        }
    }

    @Override
    public void flush() {
        if (e != null) {
            if (widget != null) {
                e.flush(getValue());
            }
        }
    }
}
