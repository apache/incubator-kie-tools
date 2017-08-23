/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import java.util.Date;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.client.ui.Widget;
import org.appformer.project.datamodel.oracle.DropDownData;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.uberfire.ext.widgets.common.client.common.DatePicker;

/**
 * A Popup drop-down Editor ;-)
 */
public abstract class AbstractProxyPopupDropDownDatePicker implements ProxyPopupDropDown<Date> {

    private static final String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();

    private final DatePicker datePicker;

    public AbstractProxyPopupDropDownDatePicker( final AbstractProxyPopupDropDownEditCell proxy ) {
        this.datePicker = new DatePicker();
        datePicker.setFormat( DATE_FORMAT );

        // Hide the panel and call valueUpdater.update when a date is selected
        datePicker.addValueChangeHandler( new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange( final ValueChangeEvent<Date> event ) {
                proxy.commit();
            }
        } );
    }

    @Override
    public void render( final Cell.Context context,
                        final Date value,
                        final SafeHtmlBuilder sb,
                        final SafeHtmlRenderer<String> renderer ) {
        //Render value
        if ( value != null ) {
            sb.append( renderer.render( ( value == null ? "" : convertToString( value ) ) ) );
        }
    }

    @Override
    public void setValue( final Date value ) {
        Date date = value;
        if ( value == null ) {
            Date d = new Date();
            int year = d.getYear();
            int month = d.getMonth();
            int dom = d.getDate();
            date = new Date( year,
                             month,
                             dom );
        }
        datePicker.setValue( date );
    }

    @Override
    public void setDropDownData( final DropDownData dd ) {
        throw new UnsupportedOperationException( "Only single values are supported" );
    }

    // Commit the change
    @Override
    public Date getValue() {
        return datePicker.getValue();
    }

    // Start editing the cell
    @Override
    public void startEditing( final Cell.Context context,
                              final Element parent,
                              final Date value ) {
        Date date = value;
        if ( value == null ) {
            Date d = new Date();
            int year = d.getYear();
            int month = d.getMonth();
            int dom = d.getDate();
            date = new Date( year,
                             month,
                             dom );
        }
        datePicker.setValue( date );
    }

    @Override
    public void setFocus( final boolean focused ) {
        //DatePicker does not implement setFocus
    }

    @Override
    public Widget asWidget() {
        return datePicker;
    }

}
