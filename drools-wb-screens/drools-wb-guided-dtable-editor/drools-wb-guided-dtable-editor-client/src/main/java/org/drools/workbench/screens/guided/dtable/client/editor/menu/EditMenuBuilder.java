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

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.Clipboard;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectionsChangedEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@Dependent
public class EditMenuBuilder extends BaseMenu implements MenuFactory.CustomMenuBuilder,
                                                         EditMenuView.Presenter {

    public interface SupportsEditMenu {

        void onCut();

        void onCopy();

        void onPaste();

        void onDeleteSelectedCells();

        void onDeleteSelectedColumns();

        void onDeleteSelectedRows();

        void onOtherwiseCell();

    }

    private EditMenuView view;
    private Clipboard clipboard;

    @Inject
    public EditMenuBuilder( final EditMenuView view,
                            final Clipboard clipboard ) {
        this.view = view;
        this.clipboard = clipboard;
    }

    @PostConstruct
    void setup() {
        view.init( this );
    }

    @Override
    public void push( final MenuFactory.CustomMenuBuilder element ) {
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public IsWidget build() {
                return view;
            }

            @Override
            public boolean isEnabled() {
                return view.isEnabled();
            }

            @Override
            public void setEnabled( final boolean enabled ) {
                view.setEnabled( enabled );
            }
        };
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
        enableMenuItems( selections );
        setupOtherwiseCellEntry( selections );
    }

    @Override
    public void onCut() {
        if ( activeDecisionTable != null ) {
            activeDecisionTable.onCut();
        }
    }

    @Override
    public void onCopy() {
        if ( activeDecisionTable != null ) {
            activeDecisionTable.onCopy();
        }
    }

    @Override
    public void onPaste() {
        if ( activeDecisionTable != null ) {
            activeDecisionTable.onPaste();
        }
    }

    @Override
    public void onDeleteSelectedCells() {
        if ( activeDecisionTable != null ) {
            activeDecisionTable.onDeleteSelectedCells();
        }
    }

    @Override
    public void onDeleteSelectedColumns() {
        if ( activeDecisionTable != null ) {
            activeDecisionTable.onDeleteSelectedColumns();
        }
    }

    @Override
    public void onDeleteSelectedRows() {
        if ( activeDecisionTable != null ) {
            activeDecisionTable.onDeleteSelectedRows();
        }
    }

    @Override
    public void onOtherwiseCell() {
        if ( activeDecisionTable != null ) {
            view.setOtherwiseCell( true );
            activeDecisionTable.onOtherwiseCell();
        }
    }

    private void disableMenuItems() {
        view.enableCutMenuItem( false );
        view.enableCopyMenuItem( false );
        view.enablePasteMenuItem( false );
        view.enableDeleteCellMenuItem( false );
        view.enableDeleteColumnMenuItem( false );
        view.enableDeleteRowMenuItem( false );
        view.enableOtherwiseCellMenuItem( false );
    }

    private void enableMenuItems( final List<GridData.SelectedCell> selections ) {
        final boolean enabled = selections.size() > 0;
        final boolean isOtherwiseEnabled = isOtherwiseEnabled( selections );
        final boolean isOnlyMandatoryColumnSelected = isOnlyMandatoryColumnSelected( selections );

        view.enableCutMenuItem( enabled );
        view.enableCopyMenuItem( enabled );
        view.enablePasteMenuItem( clipboard.hasData() );
        view.enableDeleteCellMenuItem( enabled );
        view.enableDeleteColumnMenuItem( enabled && !isOnlyMandatoryColumnSelected );
        view.enableDeleteRowMenuItem( enabled );
        view.enableOtherwiseCellMenuItem( isOtherwiseEnabled );
    }

    private void setupOtherwiseCellEntry( final List<GridData.SelectedCell> selections ) {
        if ( selections.size() != 1 ) {
            view.setOtherwiseCell( false );
            return;
        }
        final GridData.SelectedCell selection = selections.get( 0 );
        final int rowIndex = selection.getRowIndex();
        final int columnIndex = findUiColumnIndex( selection.getColumnIndex() );
        final boolean isOtherwiseCell = activeDecisionTable.getModel().getData().get( rowIndex ).get( columnIndex ).isOtherwise();
        view.setOtherwiseCell( isOtherwiseCell );
    }

    //Check whether the "otherwise" menu item can be enabled
    private boolean isOtherwiseEnabled( final List<GridData.SelectedCell> selections ) {
        if ( selections.size() != 1 ) {
            return false;
        }
        boolean isOtherwiseEnabled = true;
        final GridData.SelectedCell selection = selections.get( 0 );
        final int columnIndex = findUiColumnIndex( selection.getColumnIndex() );
        final BaseColumn column = activeDecisionTable.getModel().getExpandedColumns().get( columnIndex );
        isOtherwiseEnabled = isOtherwiseEnabled && canAcceptOtherwiseValues( column );
        return isOtherwiseEnabled;
    }

    //Check whether column selection is only RowNumberColumn or DescriptionColumn. These cannot be deleted.
    private boolean isOnlyMandatoryColumnSelected( final List<GridData.SelectedCell> selections ) {
        boolean isOnlyMandatoryColumnSelected = true;
        for ( GridData.SelectedCell sc : selections ) {
            final int columnIndex = findUiColumnIndex( sc.getColumnIndex() );
            final BaseColumn column = activeDecisionTable.getModel().getExpandedColumns().get( columnIndex );
            if ( !( ( column instanceof RowNumberCol52 ) || ( column instanceof DescriptionCol52 ) ) ) {
                isOnlyMandatoryColumnSelected = false;
            }
        }
        return isOnlyMandatoryColumnSelected;
    }

    private int findUiColumnIndex( final int modelColumnIndex ) {
        final List<GridColumn<?>> columns = activeDecisionTable.getView().getModel().getColumns();
        for ( int uiColumnIndex = 0; uiColumnIndex < columns.size(); uiColumnIndex++ ) {
            final GridColumn<?> c = columns.get( uiColumnIndex );
            if ( c.getIndex() == modelColumnIndex ) {
                return uiColumnIndex;
            }
        }
        throw new IllegalStateException( "Column was not found!" );
    }

    // Check whether the given column can accept "otherwise" values
    private boolean canAcceptOtherwiseValues( final BaseColumn column ) {
        if ( !( column instanceof ConditionCol52 ) ) {
            return false;
        }
        final ConditionCol52 cc = (ConditionCol52) column;

        //Check column contains literal values and uses the equals operator
        if ( cc.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_LITERAL ) {
            return false;
        }

        //Check operator is supported
        if ( cc.getOperator() == null ) {
            return false;
        }
        if ( cc.getOperator().equals( "==" ) ) {
            return true;
        }
        if ( cc.getOperator().equals( "!=" ) ) {
            return true;
        }
        return false;
    }

}
