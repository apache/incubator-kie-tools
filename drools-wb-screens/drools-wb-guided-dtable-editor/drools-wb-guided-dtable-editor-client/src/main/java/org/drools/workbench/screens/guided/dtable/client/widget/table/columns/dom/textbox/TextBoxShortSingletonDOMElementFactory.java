/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox;

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.uberfire.ext.widgets.common.client.common.NumericShortTextBox;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class TextBoxShortSingletonDOMElementFactory extends TextBoxSingletonDOMElementFactory<Short, NumericShortTextBox> {

    public TextBoxShortSingletonDOMElementFactory( final GridLayer gridLayer,
                                                   final GuidedDecisionTableView gridWidget ) {
        super( gridLayer,
               gridWidget );
    }

    @Override
    public NumericShortTextBox createWidget() {
        return new NumericShortTextBox( true );
    }

    @Override
    public String convert( final Short value ) {
        if ( value == null ) {
            return "";
        }
        return value.toString();
    }

    @Override
    public Short convert( final String value ) {
        try {
            return new Short( value );
        } catch ( NumberFormatException nfe ) {
            return new Short( "0" );
        }
    }

}
