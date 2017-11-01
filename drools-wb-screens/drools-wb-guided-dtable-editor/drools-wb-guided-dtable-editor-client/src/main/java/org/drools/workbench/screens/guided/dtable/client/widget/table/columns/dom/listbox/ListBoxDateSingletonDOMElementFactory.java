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

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.gwtbootstrap3.client.ui.ListBox;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

public class ListBoxDateSingletonDOMElementFactory extends ListBoxSingletonDOMElementFactory<Date, ListBox> {

    private static final String droolsDateFormat = ApplicationPreferences.getDroolsDateFormat();
    private static final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat( droolsDateFormat );

    public ListBoxDateSingletonDOMElementFactory( final GridLienzoPanel gridPanel,
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
    public String convert( final Date value ) {
        return dateTimeFormat.format( value );
    }

    @Override
    public Date convert( final String value ) {
        try {
            return dateTimeFormat.parse( value );
        } catch ( IllegalArgumentException iae ) {
            return new Date();
        }
    }

}
