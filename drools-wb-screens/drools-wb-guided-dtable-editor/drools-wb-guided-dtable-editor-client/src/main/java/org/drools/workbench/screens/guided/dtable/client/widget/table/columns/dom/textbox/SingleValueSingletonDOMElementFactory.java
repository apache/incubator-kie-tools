/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.BaseSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

/**
 * A DOMElement Factory for single-instance single-value DOMElements.
 */
public abstract class SingleValueSingletonDOMElementFactory<T, W extends Widget, E extends SingleValueDOMElement<T, W>> extends BaseSingletonDOMElementFactory<T, W, E> {

    public SingleValueSingletonDOMElementFactory(final GridLienzoPanel gridPanel,
                                                 final GridLayer gridLayer,
                                                 final GuidedDecisionTableView gridWidget) {
        super(gridPanel,
              gridLayer,
              gridWidget);
    }

    /**
     * Convert from the given typed value to a String
     * @param value The value to be converted
     * @return A String representing the type
     */
    public abstract String convert(final T value);

    /**
     * Convert from the given String to a typed value
     * @param value The String to be converted
     * @return A typed value
     */
    public abstract T convert(final String value);
}
