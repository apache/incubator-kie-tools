/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.uberfire.ext.widgets.common.client.common.BooleanTextBox;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

public class TextBoxBooleanSingletonDOMElementFactory extends TextBoxSingletonDOMElementFactory<Boolean, BooleanTextBox> {

    public TextBoxBooleanSingletonDOMElementFactory(final GridLienzoPanel gridPanel,
                                                    final GridLayer gridLayer,
                                                    final GuidedDecisionTableView gridWidget) {
        super(gridPanel,
              gridLayer,
              gridWidget);
    }

    @Override
    public BooleanTextBox createWidget() {
        return new BooleanTextBox(true);
    }

    @Override
    public String convert(final Boolean value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    @Override
    public Boolean convert(final String value) {
        try {
            return new Boolean(value);
        } catch (Exception nfe) {
            return Boolean.TRUE;
        }
    }
}

