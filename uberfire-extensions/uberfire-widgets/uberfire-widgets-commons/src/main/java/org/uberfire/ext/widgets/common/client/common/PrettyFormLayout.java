/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.widgets.common.client.common;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Form;

/**
 * Uses ext forms to do a prettier layout.
 */
public class PrettyFormLayout extends Composite {

    private VerticalPanel layout = new VerticalPanel();
    private FlexTable currentTable;
    private String sectionName;

    public PrettyFormLayout() {
        layout.setWidth( "100%" );
        initWidget( layout );
    }

    public void startSection() {
        this.currentTable = new FlexTable();
    }

    public void startSection( String title ) {
        startSection();
        this.sectionName = title;
    }

    public void clear() {
        this.layout.clear();
    }

    public void addHeader( Image img,
                           String name,
                           Image edit ) {
        HorizontalPanel h = new HorizontalPanel();
        h.add( img );
        h.add( new HTML( "&nbsp;" ) );
        h.add( new Label( name ) );
        if ( edit != null ) {
            h.add( edit );
        }

        Form f = newForm( null );

        f.add( h );
        layout.add( f );
    }

    public void addHeader( Image img,
                           Widget content ) {
        HorizontalPanel h = new HorizontalPanel();
        h.add( img );
        h.add( new HTML( "&nbsp;" ) );
        h.add( content );
        Form f = newForm( null );
        f.add( h );
        layout.add( f );
    }

    private Form newForm( final String hdr ) {
        Form fp = new Form();
        fp.setWidth( "100%" );
        fp.addStyleName( "guvnor-FormPanel" );
        if ( hdr != null ) {
            fp.setTitle( hdr );
        }
        return fp;
    }

    public void endSection() {
        Form f = newForm( this.sectionName );

        f.add( this.currentTable );

        this.layout.add( f );
        this.sectionName = null;
    }

    public void addRow( final Widget widget ) {
        //TODO ARIA: what to do with widget has no visible label?

        int row = currentTable.getRowCount();
        currentTable.setWidget( row,
                                0,
                                widget );
        currentTable.getFlexCellFormatter().setColSpan( row,
                                                        0,
                                                        2 );
    }

    public int addAttribute( String lbl,
                             final Widget categories ) {
        String id = DOM.createUniqueId();
        categories.getElement().setAttribute( "aria-labelledby", id );
        categories.getElement().setAttribute( "aria-required", String.valueOf( true ) );
        categories.getElement().setTabIndex( 0 );

        int row = currentTable.getRowCount();
        Label label = new Label( lbl );
        label.getElement().setAttribute( "id", id );

        currentTable.setWidget( row,
                                0,
                                label );
        currentTable.setWidget( row,
                                1,
                                categories );
        currentTable.getFlexCellFormatter().setHorizontalAlignment( row,
                                                                    0,
                                                                    HasHorizontalAlignment.ALIGN_RIGHT );
        currentTable.getFlexCellFormatter().setVerticalAlignment( row,
                                                                  0,
                                                                  HasVerticalAlignment.ALIGN_TOP );

        return row;
    }

    public void removeRow( int row ) {

        currentTable.removeRow( row );
    }
}
