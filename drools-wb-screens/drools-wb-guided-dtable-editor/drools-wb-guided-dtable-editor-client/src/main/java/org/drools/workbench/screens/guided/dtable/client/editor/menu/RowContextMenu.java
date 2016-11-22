/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.Clipboard;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectionsChangedEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

@Dependent
public class RowContextMenu extends BaseMenu implements IsWidget,
                                                        RowContextMenuView.Presenter {

    private RowContextMenuView view;
    private Clipboard clipboard;

    @Inject
    public RowContextMenu( final RowContextMenuView view,
                           final Clipboard clipboard ) {
        this.view = view;
        this.clipboard = clipboard;
    }

    @PostConstruct
    void setup() {
        view.init( this );
    }

    @Override
    public void onDecisionTableSelectedEvent( final @Observes DecisionTableSelectedEvent event ) {
        super.onDecisionTableSelectedEvent( event );
    }

    @Override
    public void onDecisionTableSelectionsChangedEvent( final @Observes DecisionTableSelectionsChangedEvent event ) {
        super.onDecisionTableSelectionsChangedEvent( event );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void show( final int mx,
                      final int my ) {
        view.show( mx,
                   my );
    }

    @Override
    public void hide() {
        view.hide();
    }

    @Override
    public void initialise() {
        if ( activeDecisionTable == null || !activeDecisionTable.getAccess().isEditable() ) {
            disableMenuItems();
            return;
        }
        final List<GridData.SelectedCell> selections = activeDecisionTable.getView().getModel().getSelectedCells();
        if ( selections == null || selections.isEmpty() ) {
            disableMenuItems();
            return;
        }
        final Map<Integer, Boolean> rowUsage = new HashMap<>();
        for ( GridData.SelectedCell sc : selections ) {
            rowUsage.put( sc.getRowIndex(),
                          true );
        }
        enableMenuItemsForClipboard();
        enableMenuItemsForRowOperations( rowUsage.keySet().size() == 1 );
    }

    @Override
    public void onCut() {
        if ( activeDecisionTable != null ) {
            activeDecisionTable.onCut();
        }
        hide();
    }

    @Override
    public void onCopy() {
        if ( activeDecisionTable != null ) {
            activeDecisionTable.onCopy();
        }
        hide();
    }

    @Override
    public void onPaste() {
        if ( activeDecisionTable != null ) {
            activeDecisionTable.onPaste();
        }
        hide();
    }

    @Override
    public void onInsertRowAbove() {
        if ( activeDecisionTable != null ) {
            activeDecisionTable.onInsertRowAbove();
        }
        hide();
    }

    @Override
    public void onInsertRowBelow() {
        if ( activeDecisionTable != null ) {
            activeDecisionTable.onInsertRowBelow();
        }
        hide();
    }

    @Override
    public void onDeleteSelectedRows() {
        if ( activeDecisionTable != null ) {
            activeDecisionTable.onDeleteSelectedRows();
        }
        hide();
    }

    private void disableMenuItems() {
        view.enableCutMenuItem( false );
        view.enableCopyMenuItem( false );
        view.enablePasteMenuItem( false );
        view.enableInsertRowAboveMenuItem( false );
        view.enableInsertRowBelowMenuItem( false );
        view.enableDeleteRowMenuItem( false );
    }

    private void enableMenuItemsForClipboard() {
        view.enableCutMenuItem( true );
        view.enableCopyMenuItem( true );
        view.enablePasteMenuItem( clipboard.hasData() );
    }

    private void enableMenuItemsForRowOperations( final boolean isSingleRow ) {
        view.enableInsertRowAboveMenuItem( isSingleRow );
        view.enableInsertRowBelowMenuItem( isSingleRow );
        view.enableDeleteRowMenuItem( true );
    }

}
