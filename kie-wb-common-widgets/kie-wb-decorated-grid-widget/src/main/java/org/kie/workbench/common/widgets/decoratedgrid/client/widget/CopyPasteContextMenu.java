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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget;

import java.util.SortedSet;
import java.util.TreeSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import org.kie.workbench.common.widgets.decoratedgrid.client.resources.i18n.Constants;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.CopyRowsEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteColumnEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.MoveColumnsEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.PasteRowsEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.RowGroupingChangeEvent;

/**
 * A context menu for the copying\pasting rows
 */
public class CopyPasteContextMenu extends AbstractContextMenu
        implements
        DeleteColumnEvent.Handler,
        MoveColumnsEvent.Handler,
        RowGroupingChangeEvent.Handler {

    private SortedSet<Integer> context = new TreeSet<Integer>();
    private final ContextMenuItem itemCopy;
    private final ContextMenuItem itemPaste;
    private final EventBus eventBus;

    public CopyPasteContextMenu( final EventBus eventBus ) {
        this.eventBus = eventBus;

        itemCopy = new ContextMenuItem( Constants.INSTANCE.Copy(),
                                        true,
                                        new ClickHandler() {

                                            public void onClick( ClickEvent event ) {
                                                itemPaste.setEnabled( true );
                                                CopyRowsEvent cpre = new CopyRowsEvent( context );
                                                eventBus.fireEvent( cpre );
                                                hide();
                                            }

                                        } );
        addContextMenuItem( itemCopy );

        itemPaste = new ContextMenuItem( Constants.INSTANCE.Paste(),
                                         false,
                                         new ClickHandler() {

                                             public void onClick( ClickEvent event ) {
                                                 int targetRowIndex = context.first();
                                                 PasteRowsEvent pre = new PasteRowsEvent( targetRowIndex );
                                                 eventBus.fireEvent( pre );
                                                 hide();
                                             }

                                         } );
        addContextMenuItem( itemPaste );

        //Wire-up event handlers
        eventBus.addHandler( DeleteColumnEvent.TYPE,
                             this );
        eventBus.addHandler( MoveColumnsEvent.TYPE,
                             this );
        eventBus.addHandler( RowGroupingChangeEvent.TYPE,
                             this );
    }

    public void setContextRows( SortedSet<Integer> context ) {
        this.context = context;
    }

    //If a column is deleted invalidate "Paste" as the current "copied" row's content is invalid
    public void onDeleteColumn( DeleteColumnEvent event ) {
        reset();
    }

    //If a column is moved invalidate "Paste" as the current "copied" row's content is invalid
    public void onMoveColumns( MoveColumnsEvent event ) {
        reset();
    }

    //If row's grouping is changed invalidate "Paste" as the copied rows will be inconsistent with the current state of the table
    public void onRowGroupingChange( RowGroupingChangeEvent event ) {
        reset();
    }

    private void reset() {
        context.clear();
        itemPaste.setEnabled( false );
        CopyRowsEvent cpre = new CopyRowsEvent();
        eventBus.fireEvent( cpre );
    }

}
