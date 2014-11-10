/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The GWT StackPanel is not working as we want. So doing a custom one.
 */
public class LazyStackPanel extends Composite
        implements
        HasSelectionHandlers<LazyStackPanelRow> {

    private FlexTable flexTable = new FlexTable();
    private int       rowIndex  = 0;

    public LazyStackPanel() {

        initWidget( flexTable );

        flexTable.setStyleName( "guvnor-lazyStackPanel" );

        addSelectionHandler( new SelectionHandler<LazyStackPanelRow>() {

            public void onSelection( SelectionEvent<LazyStackPanelRow> event ) {
                LazyStackPanelRow row = event.getSelectedItem();
                if ( row.isExpanded() ) {
                    row.compress();
                } else {
                    row.expand();
                }
            }
        } );
    }

    /**
     * Add a new (collapsed) element to the stack.
     * @param headerText
     * @param contentLoad
     */
    public void add( String headerText,
                     LoadContentCommand contentLoad ) {
        this.add( headerText,
                  contentLoad,
                  false );
    }

    public void add( String headerText,
                     LoadContentCommand contentLoad,
                     boolean expanded ) {
        LazyStackPanelHeader header = new LazyStackPanelHeader( headerText );
        add( header,
             contentLoad,
             expanded );
    }

    public void add( String headerText,
                     ImageResource icon,
                     LoadContentCommand contentLoad ) {
        LazyStackPanelHeader header = new LazyStackPanelHeader( headerText,
                                                                icon );
        add( header,
             contentLoad,
             false );
    }

    /**
     * Add a new (collapsed) element to the stack.
     * @param header
     * @param contentLoad
     */
    public void add( AbstractLazyStackPanelHeader header,
                     LoadContentCommand contentLoad ) {
        this.add( header,
                  contentLoad,
                  false );
    }

    public void add( AbstractLazyStackPanelHeader header,
                     LoadContentCommand contentLoad,
                     boolean expanded ) {
        final LazyStackPanelRow row = new LazyStackPanelRow( header,
                                                             contentLoad );

        header.addOpenHandler( new OpenHandler<AbstractLazyStackPanelHeader>() {

            public void onOpen( OpenEvent<AbstractLazyStackPanelHeader> event ) {
                selectRow( row );
            }
        } );

        header.addCloseHandler( new CloseHandler<AbstractLazyStackPanelHeader>() {

            public void onClose( com.google.gwt.event.logical.shared.CloseEvent<AbstractLazyStackPanelHeader> event ) {
                selectRow( row );
            }
        } );

        addHeaderRow( row );

        addContentRow( row.getContentPanel() );

        if ( expanded ) {
            header.expand();
        }
    }

    private void addHeaderRow( final LazyStackPanelRow row ) {
        flexTable.setWidget( rowIndex,
                             0,
                             row );
        flexTable.getFlexCellFormatter().setStyleName( rowIndex,
                                                       0,
                                                       "guvnor-LazyStackPanel-row-header" );
        rowIndex++;
    }

    private void addContentRow( final SimplePanel panel ) {
        flexTable.setWidget( rowIndex++,
                             0,
                             panel );
    }

    private void selectRow( LazyStackPanelRow row ) {
        SelectionEvent.fire( this,
                             row );
    }

    @Override
    public HandlerRegistration addSelectionHandler( SelectionHandler<LazyStackPanelRow> handler ) {
        return addHandler( handler,
                           SelectionEvent.getType() );
    }

    public void swap( int firstIndex,
                      int secondIndex ) {

        // Every list item is made of the header and content row.
        // So we have twice as many rows.
        firstIndex = firstIndex * 2;
        secondIndex = secondIndex * 2;

        Widget firstHeader = flexTable.getWidget( firstIndex,
                                                  0 );
        Widget firstContent = flexTable.getWidget( firstIndex + 1,
                                                   0 );
        Widget secondHeader = flexTable.getWidget( secondIndex,
                                                   0 );
        Widget secondContent = flexTable.getWidget( secondIndex + 1,
                                                    0 );

        flexTable.setWidget( firstIndex,
                             0,
                             secondHeader );
        flexTable.setWidget( firstIndex + 1,
                             0,
                             secondContent );
        flexTable.setWidget( secondIndex,
                             0,
                             firstHeader );
        flexTable.setWidget( secondIndex + 1,
                             0,
                             firstContent );
    }

    public Iterator<AbstractLazyStackPanelHeader> getHeaderIterator() {
        List<AbstractLazyStackPanelHeader> result = new ArrayList<AbstractLazyStackPanelHeader>();
        Iterator<Widget> iterator = flexTable.iterator();

        while ( iterator.hasNext() ) {
            Widget widget = (Widget) iterator.next();
            if ( widget instanceof LazyStackPanelRow ) {
                result.add( ( (LazyStackPanelRow) widget ).getHeader() );
            }
        }

        return result.iterator();
    }

    public void remove( int index ) {

        index = index * 2;

        flexTable.removeRow( index + 1 );
        flexTable.removeRow( index );
    }

    public void clean() {
        flexTable.removeAllRows();
        rowIndex = 0;
    }
}
