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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox;

import java.math.BigInteger;

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

public class ListBoxBigIntegerSingletonDOMElementFactory extends ListBoxSingletonDOMElementFactory<BigInteger, ListBox> {

    public ListBoxBigIntegerSingletonDOMElementFactory( final GridLienzoPanel gridPanel,
                                                        final GridLayer gridLayer,
                                                        final GuidedDecisionTableView gridWidget ) {
        super( gridPanel,
               gridLayer,
               gridWidget );
    }

    @Override
    public ListBox createWidget() {
        return new ListBox();
    }

    @Override
    public String convert( final BigInteger value ) {
        return value.toString();
    }

    @Override
    public BigInteger convert( final String value ) {
        try {
            return new BigInteger( value );
        } catch ( NumberFormatException nfe ) {
            return new BigInteger( "0" );
        }
    }

}
