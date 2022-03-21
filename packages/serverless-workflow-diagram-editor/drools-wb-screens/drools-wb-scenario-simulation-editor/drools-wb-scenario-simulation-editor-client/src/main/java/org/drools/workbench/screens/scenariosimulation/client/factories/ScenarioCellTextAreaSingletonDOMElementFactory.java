/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.factories;

import org.drools.workbench.screens.scenariosimulation.client.domelements.ScenarioCellTextAreaDOMElement;
import org.gwtbootstrap3.client.ui.TextArea;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

public class ScenarioCellTextAreaSingletonDOMElementFactory extends AbstractTextBoxSingletonDOMElementFactory<BaseDOMElement<String, TextArea>> {

    public ScenarioCellTextAreaSingletonDOMElementFactory(GridLienzoPanel gridPanel, GridLayer gridLayer, GridWidget gridWidget) {
        super(gridPanel, gridLayer, gridWidget);
    }

    @Override
    protected BaseDOMElement<String, TextArea> createDomElementInternal(final TextArea widget,
                                                                        final GridLayer gridLayer,
                                                                        final GridWidget gridWidget) {
        return new ScenarioCellTextAreaDOMElement(widget, gridLayer, gridWidget);
    }
}

