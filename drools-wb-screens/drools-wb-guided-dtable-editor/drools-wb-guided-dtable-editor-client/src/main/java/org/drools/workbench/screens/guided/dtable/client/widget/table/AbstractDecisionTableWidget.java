/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.auditlog.DeleteColumnAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.DeleteRowAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.InsertColumnAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.InsertRowAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.UpdateColumnAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Analysis;
import org.drools.workbench.models.guided.dtable.shared.model.AnalysisCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiffImpl;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.client.utils.DTCellValueUtilities;
import org.drools.workbench.screens.guided.dtable.client.utils.GuidedDecisionTableUtils;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.DecisionTableAnalyzer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.BoundFactsChangedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.InsertDecisionTableColumnEvent;
import org.drools.workbench.screens.guided.rule.client.editor.RuleAttributeWidget;
import org.drools.workbench.screens.guided.rule.client.util.GWTDateConverter;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.DecoratedGridCellValueAdaptor;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.CellStateChangedEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.CellStateChangedEvent.CellStateOperation;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.CopyRowsEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteColumnEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertColumnEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.MoveColumnsEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.PasteRowsEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SelectedCellChangeEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SetColumnVisibilityEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.ToggleMergingEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDataEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDefinitionEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateModelEvent;
import org.uberfire.security.Identity;

/**
 * An abstract Decision Table and the necessary boiler-plate to convert from
 * DTColumnConfig objects to the DynamicData related classes used by the
 * DecoratedGridWidget
 */
public abstract class AbstractDecisionTableWidget extends Composite
        implements
        SelectedCellChangeEvent.Handler,
        InsertRowEvent.Handler,
        DeleteRowEvent.Handler,
        AppendRowEvent.Handler,
        CopyRowsEvent.Handler,
        PasteRowsEvent.Handler,
        DeleteColumnEvent.Handler,
        InsertColumnEvent.Handler<BaseColumn, DTCellValue52>,
        MoveColumnsEvent.Handler,
        UpdateModelEvent.Handler {

    protected AbstractDecoratedDecisionTableGridWidget widget;
    protected GuidedDecisionTableUtils utils;
    protected DTCellValueUtilities cellUtils;

    protected final GuidedDecisionTable52 model;
    protected final AsyncPackageDataModelOracle oracle;
    protected final DecisionTableCellFactory cellFactory;
    protected final DecisionTableCellValueFactory cellValueFactory;
    protected final DecisionTableDropDownManager dropDownManager;
    protected final EventBus eventBus;
    protected final boolean isReadOnly;
    private final BRLRuleModel rm;

    //Current user's security context (for audit log)
    private final Identity identity;

    //Rows that have been copied in a copy-paste operation
    private List<List<DTCellValue52>> copiedRows = new ArrayList<List<DTCellValue52>>();

    protected static final DecisionTableResourcesProvider resources = new DecisionTableResourcesProvider();

    /**
     * Constructor
     */
    public AbstractDecisionTableWidget( GuidedDecisionTable52 model,
                                        AsyncPackageDataModelOracle oracle,
                                        Identity identity,
                                        boolean isReadOnly,
                                        EventBus eventBus ) {

        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        if ( oracle == null ) {
            throw new IllegalArgumentException( "oracle cannot be null" );
        }
        if ( identity == null ) {
            throw new IllegalArgumentException( "identity cannot be null" );
        }
        if ( eventBus == null ) {
            throw new IllegalArgumentException( "eventBus cannot be null" );
        }
        this.model = model;
        this.oracle = oracle;
        this.identity = identity;
        this.rm = new BRLRuleModel( model );
        this.utils = new GuidedDecisionTableUtils( model,
                                                   oracle );
        this.cellUtils = new DTCellValueUtilities( model,
                                                   oracle );
        this.eventBus = eventBus;
        this.isReadOnly = isReadOnly;

        //Ensure field data-type is set (field did not exist before 5.2)
        for ( CompositeColumn<?> cc : model.getConditions() ) {
            if ( cc instanceof Pattern52 ) {
                Pattern52 p = (Pattern52) cc;
                for ( ConditionCol52 col : p.getChildColumns() ) {
                    ConditionCol52 c = (ConditionCol52) col;
                    c.setFieldType( oracle.getFieldType( p.getFactType(),
                                                         c.getFactField() ) );
                }
            }
        }

        //Setup the DropDownManager that requires the Model and UI data to determine drop-down lists
        //for dependent enumerations. This needs to be called before the columns are created.
        this.dropDownManager = new DecisionTableDropDownManager( model,
                                                                 oracle );

        //Factories for new cell elements
        this.cellFactory = new DecisionTableCellFactory( model,
                                                         oracle,
                                                         dropDownManager,
                                                         isReadOnly,
                                                         eventBus );
        this.cellValueFactory = new DecisionTableCellValueFactory( model,
                                                                   oracle );

        //Date converter is injected so a GWT compatible one can be used here and another in testing
        DTCellValueUtilities.injectDateConvertor( GWTDateConverter.getInstance() );

        //Wire-up the events
        eventBus.addHandler( InsertRowEvent.TYPE,
                             this );
        eventBus.addHandler( DeleteRowEvent.TYPE,
                             this );
        eventBus.addHandler( AppendRowEvent.TYPE,
                             this );
        eventBus.addHandler( CopyRowsEvent.TYPE,
                             this );
        eventBus.addHandler( PasteRowsEvent.TYPE,
                             this );
        eventBus.addHandler( SelectedCellChangeEvent.TYPE,
                             this );
        eventBus.addHandler( DeleteColumnEvent.TYPE,
                             this );
        eventBus.addHandler( InsertDecisionTableColumnEvent.TYPE,
                             this );
        eventBus.addHandler( MoveColumnsEvent.TYPE,
                             this );
        eventBus.addHandler( UpdateModelEvent.TYPE,
                             this );
    }

    /**
     * Add a column to the table.
     * @param modelColumn The Decision Table column to insert
     */
    public void addColumn( ActionCol52 modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }
        model.getActionCols().add( modelColumn );
        addColumn( modelColumn,
                   cellValueFactory.makeColumnData( modelColumn ),
                   true );

        //Log addition of column
        model.getAuditLog().add( new InsertColumnAuditLogEntry( identity.getName(),
                                                                modelColumn ) );
    }

    /**
     * Add a column to the table.
     * @param modelColumn The Decision Table column to insert
     */
    public void addColumn( BRLActionColumn modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }
        model.getActionCols().add( modelColumn );
        addBRLActionVariableColumns( modelColumn.getChildColumns(),
                                     true );

        //Log addition of column
        model.getAuditLog().add( new InsertColumnAuditLogEntry( identity.getName(),
                                                                modelColumn ) );
    }

    /**
     * Add a column to the table.
     * @param modelColumn The Decision Table column to insert
     */
    public void addColumn( LimitedEntryBRLActionColumn modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }
        model.getActionCols().add( modelColumn );
        addColumn( modelColumn,
                   cellValueFactory.makeColumnData( modelColumn ),
                   true );

        //Log addition of column
        model.getAuditLog().add( new InsertColumnAuditLogEntry( identity.getName(),
                                                                modelColumn ) );
    }

    /**
     * Add a column to the table.
     * @param modelColumn The Decision Table column to insert
     */
    public void addColumn( BRLConditionColumn modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }
        model.getConditions().add( modelColumn );
        addBRLConditionVariableColumns( modelColumn.getChildColumns(),
                                        true );

        //Log addition of column
        model.getAuditLog().add( new InsertColumnAuditLogEntry( identity.getName(),
                                                                modelColumn ) );
    }

    /**
     * Add a column to the table.
     * @param modelColumn The Decision Table column to insert
     */
    public void addColumn( LimitedEntryBRLConditionColumn modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }
        model.getConditions().add( modelColumn );
        addColumn( modelColumn,
                   cellValueFactory.makeColumnData( modelColumn ),
                   true );

        //Log addition of column
        model.getAuditLog().add( new InsertColumnAuditLogEntry( identity.getName(),
                                                                modelColumn ) );
    }

    /**
     * Add a column to the table.
     * @param modelColumn The Decision Table column to insert
     */
    public void addColumn( AttributeCol52 modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }
        model.getAttributeCols().add( modelColumn );
        addColumn( modelColumn,
                   cellValueFactory.makeColumnData( modelColumn ),
                   true );

        //Log addition of column
        model.getAuditLog().add( new InsertColumnAuditLogEntry( identity.getName(),
                                                                modelColumn ) );
    }

    /**
     * Add a column to the table.
     * @param modelColumn The Decision Table column to insert
     */
    public void addColumn( MetadataCol52 modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }
        model.getMetadataCols().add( modelColumn );
        addColumn( modelColumn,
                   cellValueFactory.makeColumnData( modelColumn ),
                   true );

        //Log addition of column
        model.getAuditLog().add( new InsertColumnAuditLogEntry( identity.getName(),
                                                                modelColumn ) );
    }

    /**
     * Add a column to the table.
     * @param pattern The Pattern to which the column will be added
     * @param modelColumn The Decision Table column to insert
     */
    public void addColumn( Pattern52 pattern,
                           ConditionCol52 modelColumn ) {
        if ( pattern == null ) {
            throw new IllegalArgumentException( "pattern cannot be null." );
        }
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }

        //Add pattern if it does not already exist
        if ( !model.getConditions().contains( pattern ) ) {
            model.getConditions().add( pattern );

            //Signal patterns changed event
            BoundFactsChangedEvent pce = new BoundFactsChangedEvent( rm.getLHSBoundFacts() );
            eventBus.fireEvent( pce );
        }

        //Column needs to be added to pattern first so it can be correctly positioned
        pattern.getChildColumns().add( modelColumn );
        addColumn( modelColumn,
                   cellValueFactory.makeColumnData( modelColumn ),
                   true );

        //Log addition of column
        model.getAuditLog().add( new InsertColumnAuditLogEntry( identity.getName(),
                                                                modelColumn ) );
    }

    /**
     * Delete the given column
     * @param modelColumn
     */
    public void deleteColumn( ActionCol52 modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }

        int index = model.getExpandedColumns().indexOf( modelColumn );
        model.getActionCols().remove( modelColumn );
        deleteColumn( index,
                      true );

        //Log deletion of column
        model.getAuditLog().add( new DeleteColumnAuditLogEntry( identity.getName(),
                                                                modelColumn ) );
    }

    /**
     * Delete the given column
     * @param modelColumn
     */
    public void deleteColumn( BRLActionColumn modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }
        BRLActionVariableColumn firstColumn = modelColumn.getChildColumns().get( 0 );
        int firstColumnIndex = model.getExpandedColumns().indexOf( firstColumn );
        if ( firstColumnIndex >= 0 ) {
            int numberOfColumns = modelColumn.getChildColumns().size();
            deleteColumns( firstColumnIndex,
                           numberOfColumns,
                           true );
            model.getActionCols().remove( modelColumn );

            //Log deletion of column
            model.getAuditLog().add( new DeleteColumnAuditLogEntry( identity.getName(),
                                                                    modelColumn ) );
        }
    }

    /**
     * Delete the given column
     * @param modelColumn
     */
    public void deleteColumn( LimitedEntryBRLActionColumn modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }

        int index = model.getExpandedColumns().indexOf( modelColumn );
        model.getActionCols().remove( modelColumn );
        deleteColumn( index,
                      true );

        //Log deletion of column
        model.getAuditLog().add( new DeleteColumnAuditLogEntry( identity.getName(),
                                                                modelColumn ) );
    }

    /**
     * Delete the given column
     * @param modelColumn
     */
    public void deleteColumn( BRLConditionColumn modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }
        BRLConditionVariableColumn firstColumn = modelColumn.getChildColumns().get( 0 );
        int firstColumnIndex = model.getExpandedColumns().indexOf( firstColumn );
        if ( firstColumnIndex >= 0 ) {
            int numberOfColumns = modelColumn.getChildColumns().size();
            deleteColumns( firstColumnIndex,
                           numberOfColumns,
                           true );
            model.getConditions().remove( modelColumn );

            //Log deletion of column
            model.getAuditLog().add( new DeleteColumnAuditLogEntry( identity.getName(),
                                                                    modelColumn ) );
        }
    }

    /**
     * Delete the given column
     * @param modelColumn
     */
    public void deleteColumn( LimitedEntryBRLConditionColumn modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }

        int index = model.getExpandedColumns().indexOf( modelColumn );
        model.getConditions().remove( modelColumn );
        deleteColumn( index,
                      true );

        //Log deletion of column
        model.getAuditLog().add( new DeleteColumnAuditLogEntry( identity.getName(),
                                                                modelColumn ) );
    }

    /**
     * Delete the given column
     * @param modelColumn
     */
    public void deleteColumn( AttributeCol52 modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }

        int index = model.getExpandedColumns().indexOf( modelColumn );
        if ( index >= 0 ) {
            model.getAttributeCols().remove( modelColumn );
            deleteColumn( index,
                          true );

            //Log deletion of column
            model.getAuditLog().add( new DeleteColumnAuditLogEntry( identity.getName(),
                                                                    modelColumn ) );
        }
    }

    /**
     * Delete the given column
     * @param modelColumn
     */
    public void deleteColumn( MetadataCol52 modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }

        int index = model.getExpandedColumns().indexOf( modelColumn );
        model.getMetadataCols().remove( modelColumn );
        deleteColumn( index,
                      true );

        //Log deletion of column
        model.getAuditLog().add( new DeleteColumnAuditLogEntry( identity.getName(),
                                                                modelColumn ) );
    }

    /**
     * Delete the given column from the given pattern
     * @param modelColumn
     */
    public void deleteColumn( ConditionCol52 modelColumn ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null." );
        }

        int index = model.getExpandedColumns().indexOf( modelColumn );
        Pattern52 pattern = model.getPattern( modelColumn );
        pattern.getChildColumns().remove( modelColumn );

        //Remove pattern if it contains zero conditions
        if ( pattern.getChildColumns().size() == 0 ) {
            model.getConditions().remove( pattern );

            //Signal patterns changed event to Decision Table Widget
            BoundFactsChangedEvent pce = new BoundFactsChangedEvent( rm.getLHSBoundFacts() );
            eventBus.fireEvent( pce );
        }

        deleteColumn( index,
                      true );

        //Log deletion of column
        model.getAuditLog().add( new DeleteColumnAuditLogEntry( identity.getName(),
                                                                modelColumn ) );
    }

    // Delete the column at the given index with optional redraw
    private void deleteColumn( int index,
                               boolean redraw ) {
        DeleteColumnEvent dce = new DeleteColumnEvent( index,
                                                       redraw );
        eventBus.fireEvent( dce );
    }

    // Delete the column at the given index with optional redraw
    private void deleteColumns( int firstColumnIndex,
                                int numberOfColumns,
                                boolean redraw ) {
        DeleteColumnEvent dce = new DeleteColumnEvent( firstColumnIndex,
                                                       numberOfColumns,
                                                       redraw );
        eventBus.fireEvent( dce );
    }

    public void appendRow() {
        AppendRowEvent are = new AppendRowEvent();
        eventBus.fireEvent( are );
    }

    /**
     * Return the DataModelOracle associated with this Decision Table
     * @return
     */
    public AsyncPackageDataModelOracle getOracle() {
        return this.oracle;
    }

    /**
     * Mark a cell as containing the magical "otherwise" value. The magical
     * "otherwise" value has the meaning of all values other than those
     * explicitly defined for this column.
     */
    public void makeOtherwiseCell() {
        Set<CellStateOperation> operations = new HashSet<CellStateOperation>();
        operations.add( new CellStateOperation( CellValue.CellState.OTHERWISE,
                                                CellStateChangedEvent.Operation.ADD ) );
        CellStateChangedEvent csce = new CellStateChangedEvent( operations );
        eventBus.fireEvent( csce );
    }

    public void setColumnVisibility( DTColumnConfig52 modelColumn,
                                     boolean isVisible ) {
        if ( modelColumn == null ) {
            throw new IllegalArgumentException( "modelColumn cannot be null" );
        }
        int index = model.getExpandedColumns().indexOf( modelColumn );
        SetColumnVisibilityEvent scve = new SetColumnVisibilityEvent( index,
                                                                      isVisible );
        eventBus.fireEvent( scve );
    }

    /**
     * Ensure the wrapped DecoratedGridWidget's size is set too
     */
    @Override
    public void setPixelSize( int width,
                              int height ) {
        if ( width < 0 ) {
            throw new IllegalArgumentException( "width cannot be less than zero" );
        }
        if ( height < 0 ) {
            throw new IllegalArgumentException( "height cannot be less than zero" );
        }
        super.setPixelSize( width,
                            height );
        widget.setPixelSize( width,
                             height );
    }

    /**
     * Update an ActionSetFieldCol column
     * @param origColumn The existing column in the grid
     * @param editColumn A copy of the original column containing the modified values
     */
    public void updateColumn( final ActionInsertFactCol52 origColumn,
                              final ActionInsertFactCol52 editColumn ) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bUpdateColumnData = false;
        boolean bUpdateColumnDefinition = false;
        boolean isHideUpdated = false;
        boolean isBoundNameUpdated = false;
        boolean isFactTypeUpdated = false;
        boolean isFactFieldUpdated = false;
        boolean isValueListUpdated = false;
        int iCol = model.getExpandedColumns().indexOf( origColumn );

        List<BaseColumnFieldDiff> diffs = origColumn.diff( editColumn );
        if ( diffs != null && !diffs.isEmpty() ) {
            bUpdateColumnDefinition = true;
            isHideUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionInsertFactCol52.FIELD_HIDE_COLUMN, diffs );
            isBoundNameUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionInsertFactCol52.FIELD_BOUND_NAME, diffs );
            isFactTypeUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionInsertFactCol52.FIELD_FACT_TYPE, diffs );
            isFactFieldUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionInsertFactCol52.FIELD_FACT_FIELD, diffs );
            isValueListUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionInsertFactCol52.FIELD_VALUE_LIST, diffs );
        }

        // Update column's visibility
        if ( isHideUpdated ) {
            setColumnVisibility( origColumn,
                                 !editColumn.isHideColumn() );
        }

        // Change in column's binding forces an update and redraw if FactType or
        // FactField are different; otherwise only need to update and redraw if
        // the FactType or FieldType have changed
        if ( !isBoundNameUpdated && ( isFactTypeUpdated || isFactFieldUpdated ) ) {
            bUpdateColumnData = true;
        } else if ( isFactTypeUpdated || isFactFieldUpdated ) {
            bUpdateColumnData = true;
        }

        // Update column's cell content if the Optional Value list has changed
        if ( isValueListUpdated ) {
            bUpdateColumnData = updateCellsForOptionValueList( editColumn,
                                                               origColumn );
        }

        //Log change to column definition
        if ( bUpdateColumnDefinition ) {
            model.getAuditLog().add( new UpdateColumnAuditLogEntry( identity.getName(),
                                                                    origColumn,
                                                                    editColumn, diffs ) );
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        //First remove merging if column data is being changed. This is necessary before we potentially update
        //the column's cell type as removing merging causes a redraw that needs the column's cell to be
        //consistent with the column's data-type
        if ( bUpdateColumnData ) {
            ToggleMergingEvent tme = new ToggleMergingEvent( false );
            eventBus.fireEvent( tme );
        }

        //Update Column cell
        if ( bUpdateColumnDefinition ) {
            DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell = cellFactory.getCell( origColumn );
            UpdateColumnDefinitionEvent updateColumnDefinition = new UpdateColumnDefinitionEvent( cell,
                                                                                                  iCol );
            eventBus.fireEvent( updateColumnDefinition );
        }

        //Update Column data
        if ( bUpdateColumnData ) {
            UpdateColumnDataEvent updateColumnData = new UpdateColumnDataEvent( iCol,
                                                                                getColumnData( origColumn ) );
            eventBus.fireEvent( updateColumnData );
        }

    }

    /**
     * Update an ActionSetFieldCol column
     * @param origColumn The existing column in the grid
     * @param editColumn A copy of the original column containing the modified values
     */
    public void updateColumn( final ActionSetFieldCol52 origColumn,
                              final ActionSetFieldCol52 editColumn ) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bUpdateColumnData = false;
        boolean bUpdateColumnDefinition = false;
        int iCol = model.getExpandedColumns().indexOf( origColumn );

        boolean isHideUpdated = false;
        boolean isBoundNameUpdated = false;
        boolean isFactFieldUpdated = false;
        boolean isValueListUpdated = false;

        List<BaseColumnFieldDiff> diffs = origColumn.diff( editColumn );
        if ( diffs != null && !diffs.isEmpty() ) {
            bUpdateColumnDefinition = true;
            isHideUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionSetFieldCol52.FIELD_HIDE_COLUMN, diffs );
            isBoundNameUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionSetFieldCol52.FIELD_BOUND_NAME, diffs );
            isFactFieldUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionSetFieldCol52.FIELD_FACT_FIELD, diffs );
            isValueListUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionSetFieldCol52.FIELD_VALUE_LIST, diffs );

        }

        // Update column's visibility
        if ( isHideUpdated ) {
            setColumnVisibility( origColumn,
                                 !editColumn.isHideColumn() );
        }

        // Change in column's binding forces an update and redraw if FactField
        // is different; otherwise only need to update and redraw if the
        // FieldType has changed
        if ( isBoundNameUpdated && isFactFieldUpdated ) {
            bUpdateColumnData = true;
        } else if ( isFactFieldUpdated ) {
            bUpdateColumnData = true;
        }

        // Update column's cell content if the Optional Value list has changed
        if ( isValueListUpdated ) {
            bUpdateColumnData = updateCellsForOptionValueList( editColumn,
                                                               origColumn );
        }

        //Log change to column definition
        if ( bUpdateColumnDefinition ) {
            model.getAuditLog().add( new UpdateColumnAuditLogEntry( identity.getName(),
                                                                    origColumn,
                                                                    editColumn, diffs ) );
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        //First remove merging if column data is being changed. This is necessary before we potentially update
        //the column's cell type as removing merging causes a redraw that needs the column's cell to be
        //consistent with the column's data-type
        if ( bUpdateColumnData ) {
            ToggleMergingEvent tme = new ToggleMergingEvent( false );
            eventBus.fireEvent( tme );
        }

        //Update Column cell
        if ( bUpdateColumnDefinition ) {
            DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell = cellFactory.getCell( origColumn );
            UpdateColumnDefinitionEvent updateColumnDefinition = new UpdateColumnDefinitionEvent( cell,
                                                                                                  iCol );
            eventBus.fireEvent( updateColumnDefinition );
        }

        //Update Column data
        if ( bUpdateColumnData ) {
            UpdateColumnDataEvent updateColumnData = new UpdateColumnDataEvent( iCol,
                                                                                getColumnData( origColumn ) );
            eventBus.fireEvent( updateColumnData );
        }

    }

    /**
     * Update an ActionWorkItemSetFieldCol52 column
     * @param origColumn The existing column in the grid
     * @param editColumn A copy of the original column containing the modified values
     */
    public void updateColumn( final ActionWorkItemSetFieldCol52 origColumn,
                              final ActionWorkItemSetFieldCol52 editColumn ) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bUpdateColumnDefinition = false;
        int iCol = model.getExpandedColumns().indexOf( origColumn );

        boolean isHideUpdated = false;

        List<BaseColumnFieldDiff> diffs = origColumn.diff( editColumn );
        if ( diffs != null && !diffs.isEmpty() ) {
            bUpdateColumnDefinition = true;
            isHideUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionWorkItemSetFieldCol52.FIELD_HIDE_COLUMN, diffs );
        }

        // Update column's visibility
        if ( isHideUpdated ) {
            setColumnVisibility( origColumn,
                                 !editColumn.isHideColumn() );
        }

        //Log change to column definition
        if ( bUpdateColumnDefinition ) {
            model.getAuditLog().add( new UpdateColumnAuditLogEntry( identity.getName(),
                                                                    origColumn,
                                                                    editColumn, diffs ) );
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        //Update Column cell
        if ( bUpdateColumnDefinition ) {
            DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell = cellFactory.getCell( origColumn );
            UpdateColumnDefinitionEvent updateColumnDefinition = new UpdateColumnDefinitionEvent( cell,
                                                                                                  iCol );
            eventBus.fireEvent( updateColumnDefinition );
        }

    }

    /**
     * Update an ActionRetractFactCol52 column
     * @param origColumn The existing column in the grid
     * @param editColumn A copy of the original column containing the modified values
     */
    public void updateColumn( final ActionRetractFactCol52 origColumn,
                              final ActionRetractFactCol52 editColumn ) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bUpdateColumnDefinition = false;
        int iCol = model.getExpandedColumns().indexOf( origColumn );

        boolean isHideUpdated = false;

        List<BaseColumnFieldDiff> diffs = origColumn.diff( editColumn );
        if ( diffs != null && !diffs.isEmpty() ) {
            bUpdateColumnDefinition = true;
            isHideUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionRetractFactCol52.FIELD_HIDE_COLUMN, diffs );
        }

        // Update column's visibility
        if ( isHideUpdated ) {
            setColumnVisibility( origColumn,
                                 !editColumn.isHideColumn() );
        }

        //Log change to column definition
        if ( bUpdateColumnDefinition ) {
            model.getAuditLog().add( new UpdateColumnAuditLogEntry( identity.getName(),
                                                                    origColumn,
                                                                    editColumn, diffs ) );
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        //Update Column cell
        if ( bUpdateColumnDefinition ) {
            DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell = cellFactory.getCell( origColumn );
            UpdateColumnDefinitionEvent updateColumnDefinition = new UpdateColumnDefinitionEvent( cell,
                                                                                                  iCol );
            eventBus.fireEvent( updateColumnDefinition );
        }

    }

    /**
     * Update an ActionWorkItemCol52 column
     * @param origColumn The existing column in the grid
     * @param editColumn A copy of the original column containing the modified values
     */
    public void updateColumn( final ActionWorkItemCol52 origColumn,
                              final ActionWorkItemCol52 editColumn ) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bUpdateColumnDefinition = false;
        int iCol = model.getExpandedColumns().indexOf( origColumn );

        boolean isHideUpdated = false;

        List<BaseColumnFieldDiff> diffs = origColumn.diff( editColumn );
        if ( diffs != null && !diffs.isEmpty() ) {
            bUpdateColumnDefinition = true;
            isHideUpdated = BaseColumnFieldDiffImpl.hasChanged( ActionWorkItemCol52.FIELD_HIDE_COLUMN, diffs );
        }

        // Update column's visibility
        if ( isHideUpdated ) {
            setColumnVisibility( origColumn,
                                 !editColumn.isHideColumn() );
        }

        //Log change to column definition
        if ( bUpdateColumnDefinition ) {
            model.getAuditLog().add( new UpdateColumnAuditLogEntry( identity.getName(),
                                                                    origColumn,
                                                                    editColumn, diffs ) );
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        //Update Column cell
        if ( bUpdateColumnDefinition ) {
            DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell = cellFactory.getCell( origColumn );
            UpdateColumnDefinitionEvent updateColumnDefinition = new UpdateColumnDefinitionEvent( cell,
                                                                                                  iCol );
            eventBus.fireEvent( updateColumnDefinition );
        }

    }

    /**
     * Update a BRLActionColumn column
     * @param origColumn The existing column in the grid
     * @param editColumn A copy of the original column containing the modified values
     */
    public void updateColumn( final BRLActionColumn origColumn,
                              final BRLActionColumn editColumn ) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bUpdateColumnDefinition = false;

        List<BaseColumnFieldDiff> diffs = origColumn.diff( editColumn );
        if ( diffs != null && !diffs.isEmpty() ) {
            bUpdateColumnDefinition = true;
        }

        //Copy existing data for re-use if applicable
        Map<String, List<DTCellValue52>> origColumnVariables = new HashMap<String, List<DTCellValue52>>();
        for ( BRLActionVariableColumn variable : origColumn.getChildColumns() ) {
            int iCol = model.getExpandedColumns().indexOf( variable );
            List<DTCellValue52> columnData = new ArrayList<DTCellValue52>();
            String key = getUpdateBRLActionColumnKey( variable );
            for ( List<DTCellValue52> row : model.getData() ) {
                columnData.add( row.get( iCol ) );
            }
            origColumnVariables.put( key,
                                     columnData );
        }

        //Insert new variable columns setting data from that above, if applicable. Column visibility is handled here too.
        model.getActionCols().add( model.getActionCols().indexOf( origColumn ),
                                   editColumn );
        final int index = model.getExpandedColumns().indexOf( editColumn.getChildColumns().get( 0 ) );
        final List<BaseColumn> columns = new ArrayList<BaseColumn>();
        final List<List<DTCellValue52>> columnsData = new ArrayList<List<DTCellValue52>>();
        columns.addAll( editColumn.getChildColumns() );
        for ( BaseColumn column : columns ) {
            final BRLActionVariableColumn variable = (BRLActionVariableColumn) column;
            String key = getUpdateBRLActionColumnKey( variable );
            List<DTCellValue52> columnData = origColumnVariables.get( key );
            if ( columnData == null ) {
                columnData = cellValueFactory.makeColumnData( variable );
            }
            columnsData.add( columnData );
        }
        InsertDecisionTableColumnEvent dce = new InsertDecisionTableColumnEvent( columns,
                                                                                 columnsData,
                                                                                 index,
                                                                                 true );
        eventBus.fireEvent( dce );

        //Delete columns for the original definition
        BRLActionVariableColumn firstColumn = origColumn.getChildColumns().get( 0 );
        int firstColumnIndex = model.getExpandedColumns().indexOf( firstColumn );
        int numberOfColumns = origColumn.getChildColumns().size();
        deleteColumns( firstColumnIndex,
                       numberOfColumns,
                       true );
        model.getActionCols().remove( origColumn );

        //Log change to column definition
        if ( bUpdateColumnDefinition ) {
            model.getAuditLog().add( new UpdateColumnAuditLogEntry( identity.getName(),
                                                                    origColumn,
                                                                    editColumn, diffs ) );
        }

    }

    private String getUpdateBRLActionColumnKey( BRLActionVariableColumn variable ) {
        StringBuilder key = new StringBuilder( variable.getVarName() ).append( ":" ).append( variable.getFieldType() ).append( ":" ).append( variable.getFactField() ).append( ":" ).append( variable.getFactType() );
        return key.toString();
    }

    /**
     * Update a BRLConditionColumn column
     * @param origColumn The existing column in the grid
     * @param editColumn A copy of the original column containing the modified values
     */
    public void updateColumn( final BRLConditionColumn origColumn,
                              final BRLConditionColumn editColumn ) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bUpdateColumnDefinition = false;

        List<BaseColumnFieldDiff> diffs = origColumn.diff( editColumn );
        if ( diffs != null && !diffs.isEmpty() ) {
            bUpdateColumnDefinition = true;
        }

        //Copy existing data for re-use if applicable
        Map<String, List<DTCellValue52>> origColumnVariables = new HashMap<String, List<DTCellValue52>>();
        for ( BRLConditionVariableColumn variable : origColumn.getChildColumns() ) {
            int iCol = model.getExpandedColumns().indexOf( variable );
            List<DTCellValue52> columnData = new ArrayList<DTCellValue52>();
            String key = getUpdateBRLConditionColumnKey( variable );
            for ( List<DTCellValue52> row : model.getData() ) {
                columnData.add( row.get( iCol ) );
            }
            origColumnVariables.put( key,
                                     columnData );
        }

        //Insert new variable columns setting data from that above, if applicable. Column visibility is handled here too.
        model.getConditions().add( model.getConditions().indexOf( origColumn ),
                                   editColumn );
        final int index = model.getExpandedColumns().indexOf( editColumn.getChildColumns().get( 0 ) );
        final List<BaseColumn> columns = new ArrayList<BaseColumn>();
        final List<List<DTCellValue52>> columnsData = new ArrayList<List<DTCellValue52>>();
        columns.addAll( editColumn.getChildColumns() );
        for ( BaseColumn column : columns ) {
            final BRLConditionVariableColumn variable = (BRLConditionVariableColumn) column;
            String key = getUpdateBRLConditionColumnKey( variable );
            List<DTCellValue52> columnData = origColumnVariables.get( key );
            if ( columnData == null ) {
                columnData = cellValueFactory.makeColumnData( variable );
            }
            columnsData.add( columnData );
        }
        InsertDecisionTableColumnEvent dce = new InsertDecisionTableColumnEvent( columns,
                                                                                 columnsData,
                                                                                 index,
                                                                                 true );
        eventBus.fireEvent( dce );

        //Delete columns for the original definition
        BRLConditionVariableColumn firstColumn = origColumn.getChildColumns().get( 0 );
        int firstColumnIndex = model.getExpandedColumns().indexOf( firstColumn );
        int numberOfColumns = origColumn.getChildColumns().size();
        deleteColumns( firstColumnIndex,
                       numberOfColumns,
                       true );
        model.getConditions().remove( origColumn );

        //Log change to column definition
        if ( bUpdateColumnDefinition ) {
            model.getAuditLog().add( new UpdateColumnAuditLogEntry( identity.getName(),
                                                                    origColumn,
                                                                    editColumn, diffs ) );
        }

        //Signal patterns changed event to Decision Table Widget
        BoundFactsChangedEvent pce = new BoundFactsChangedEvent( rm.getLHSBoundFacts() );
        eventBus.fireEvent( pce );
    }

    private String getUpdateBRLConditionColumnKey( BRLConditionVariableColumn variable ) {
        StringBuilder key = new StringBuilder( variable.getVarName() ).append( ":" ).append( variable.getFieldType() ).append( ":" ).append( variable.getFactField() ).append( ":" ).append( variable.getFactType() );
        return key.toString();
    }

    /**
     * Update a LimitedEntryBRLConditionColumn column
     * @param origColumn The existing column in the grid
     * @param editColumn A copy of the original column containing the modified values
     */
    public void updateColumn( final LimitedEntryBRLConditionColumn origColumn,
                              final LimitedEntryBRLConditionColumn editColumn ) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bUpdateColumnDefinition = false;
        int iCol = model.getExpandedColumns().indexOf( origColumn );

        boolean isHideUpdated = false;

        List<BaseColumnFieldDiff> diffs = origColumn.diff( editColumn );
        if ( diffs != null && !diffs.isEmpty() ) {
            bUpdateColumnDefinition = true;
            isHideUpdated = BaseColumnFieldDiffImpl.hasChanged( LimitedEntryBRLConditionColumn.FIELD_HIDE_COLUMN, diffs );
        }

        // Update column's visibility
        if ( isHideUpdated ) {
            setColumnVisibility( origColumn,
                                 !editColumn.isHideColumn() );
        }

        //Log change to column definition
        if ( bUpdateColumnDefinition ) {
            model.getAuditLog().add( new UpdateColumnAuditLogEntry( identity.getName(),
                                                                    origColumn,
                                                                    editColumn ) );
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        //Update Column cell
        if ( bUpdateColumnDefinition ) {
            DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell = cellFactory.getCell( origColumn );
            UpdateColumnDefinitionEvent updateColumnDefinition = new UpdateColumnDefinitionEvent( cell,
                                                                                                  iCol );
            eventBus.fireEvent( updateColumnDefinition );
        }

    }

    /**
     * Update a LimitedEntryBRLActionColumn column
     * @param origColumn The existing column in the grid
     * @param editColumn A copy of the original column containing the modified values
     */
    public void updateColumn( final LimitedEntryBRLActionColumn origColumn,
                              final LimitedEntryBRLActionColumn editColumn ) {
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        boolean bUpdateColumnDefinition = false;
        int iCol = model.getExpandedColumns().indexOf( origColumn );

        boolean isHideUpdated = false;

        List<BaseColumnFieldDiff> diffs = origColumn.diff( editColumn );
        if ( diffs != null && !diffs.isEmpty() ) {
            bUpdateColumnDefinition = true;
            isHideUpdated = BaseColumnFieldDiffImpl.hasChanged( LimitedEntryBRLActionColumn.FIELD_HIDE_COLUMN, diffs );
        }

        // Update column's visibility
        if ( isHideUpdated ) {
            setColumnVisibility( origColumn,
                                 !editColumn.isHideColumn() );
        }

        //Log change to column definition
        if ( bUpdateColumnDefinition ) {
            model.getAuditLog().add( new UpdateColumnAuditLogEntry( identity.getName(),
                                                                    origColumn,
                                                                    editColumn, diffs ) );
        }

        // Copy new values into original column definition
        populateModelColumn( origColumn,
                             editColumn );

        //Update Column cell
        if ( bUpdateColumnDefinition ) {
            DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell = cellFactory.getCell( origColumn );
            UpdateColumnDefinitionEvent updateColumnDefinition = new UpdateColumnDefinitionEvent( cell,
                                                                                                  iCol );
            eventBus.fireEvent( updateColumnDefinition );
        }

    }

    /**
     * Update a Condition column
     * @param origPattern The existing pattern to which the column related
     * @param origColumn The existing column in the grid
     * @param editPattern The new pattern to which the column relates
     * @param editColumn A copy of the original column containing the modified values
     */
    public void updateColumn( Pattern52 origPattern,
                              ConditionCol52 origColumn,
                              Pattern52 editPattern,
                              ConditionCol52 editColumn ) {
        if ( origPattern == null ) {
            throw new IllegalArgumentException( "origPattern cannot be null" );
        }
        if ( origColumn == null ) {
            throw new IllegalArgumentException( "origColumn cannot be null" );
        }
        if ( editPattern == null ) {
            throw new IllegalArgumentException( "editPattern cannot be null" );
        }
        if ( editColumn == null ) {
            throw new IllegalArgumentException( "editColumn cannot be null" );
        }

        List<BaseColumnFieldDiff> patternDiff = null;

        boolean existPattern = false;
        if ( model.getConditions() != null && !model.getConditions().isEmpty() ) {
            for ( CompositeColumn<? extends BaseColumn> col : model.getConditions() ) {
                // In this case only check for Pattern52 class instances.
                try {
                    Pattern52 existingCol = (Pattern52) col;
                    if ( existingCol.getBoundName().equals( editPattern.getBoundName() ) ) {
                        existPattern = true;
                        break;
                    }
                } catch ( ClassCastException e ) {
                    // Do nothing.
                }
            }
        }

        // Calculate the diffs between original and edit pattern
        patternDiff = origPattern.diff( editPattern );

        //Add pattern to model, if applicable
        if ( !existPattern ) {
            model.getConditions().add( editPattern );

            //Signal patterns changed event
            BoundFactsChangedEvent pce = new BoundFactsChangedEvent( rm.getLHSBoundFacts() );
            eventBus.fireEvent( pce );
        } else {
            // Update the original pattern properties with new values.
            origPattern.update( editPattern );
        }

        boolean bUpdateColumnData = false;
        boolean bUpdateColumnDefinition = false;

        // Change in bound name requires column to be repositioned
        if ( !isEqualOrNull( origPattern.getBoundName(),
                             editPattern.getBoundName() ) ) {

            editPattern.getChildColumns().add( editColumn );
            List<DTCellValue52> columnData = cellValueFactory.makeColumnData( editColumn );
            int origColumnIndex = model.getExpandedColumns().indexOf( origColumn );

            // If the FactType, FieldType and ConstraintValueType are unchanged
            // we can copy cell values from the old column into the new
            if ( isEqualOrNull( origPattern.getFactType(),
                                editPattern.getFactType() )
                    && isEqualOrNull( origColumn.getFactField(),
                                      editColumn.getFactField() )
                    && origColumn.getConstraintValueType() == editColumn.getConstraintValueType() ) {

                columnData.clear();
                for ( int iRow = 0; iRow < model.getData().size(); iRow++ ) {
                    List<DTCellValue52> row = model.getData().get( iRow );
                    columnData.add( row.get( origColumnIndex ) );
                }
            }

            addColumn( editColumn,
                       columnData,
                       true );

            // Delete old column
            origPattern.getChildColumns().remove( origColumn );
            if ( origPattern.getChildColumns().size() == 0 ) {
                model.getConditions().remove( origPattern );

                //Signal patterns changed event to Decision Table Widget
                BRLRuleModel rm = new BRLRuleModel( model );
                BoundFactsChangedEvent pce = new BoundFactsChangedEvent( rm.getLHSBoundFacts() );
                eventBus.fireEvent( pce );
            }
            deleteColumn( origColumnIndex,
                          true );

            //Log change to column definition
            List<BaseColumnFieldDiff> diffs = origColumn.diff( editColumn );
            if ( patternDiff != null && !patternDiff.isEmpty() ) {
                if ( diffs == null ) {
                    diffs = new ArrayList<BaseColumnFieldDiff>( patternDiff.size() );
                }
                diffs.addAll( patternDiff );
            }
            model.getAuditLog().add( new UpdateColumnAuditLogEntry( identity.getName(),
                                                                    origColumn,
                                                                    editColumn, diffs ) );

        } else {

            boolean isHideUpdated = false;
            boolean isOperatorUpdated = false;
            boolean isFieldTypeUpdated = false;
            boolean isFactFieldUpdated = false;
            boolean isFactTypeUpdated = false;
            boolean isConstraintValueTypeUpdated = false;
            boolean isValueListUpdated = false;

            List<BaseColumnFieldDiff> diffs = origColumn.diff( editColumn );
            if ( diffs != null && !diffs.isEmpty() ) {
                bUpdateColumnDefinition = true;
                isHideUpdated = BaseColumnFieldDiffImpl.hasChanged( ConditionCol52.FIELD_HIDE_COLUMN, diffs );
                isOperatorUpdated = BaseColumnFieldDiffImpl.hasChanged( ConditionCol52.FIELD_OPERATOR, diffs );
                isFieldTypeUpdated = BaseColumnFieldDiffImpl.hasChanged( ConditionCol52.FIELD_FIELD_TYPE, diffs );
                isFactFieldUpdated = BaseColumnFieldDiffImpl.hasChanged( ConditionCol52.FIELD_FACT_FIELD, diffs );
                isFactTypeUpdated = BaseColumnFieldDiffImpl.hasChanged( Pattern52.FIELD_FACT_TYPE, diffs );
                isConstraintValueTypeUpdated = BaseColumnFieldDiffImpl.hasChanged( ConditionCol52.FIELD_VALUE_LIST, diffs );
                isValueListUpdated = BaseColumnFieldDiffImpl.hasChanged( ConditionCol52.FIELD_VALUE_LIST, diffs );
            }

            if ( patternDiff != null && !patternDiff.isEmpty() ) {
                if ( diffs == null ) {
                    diffs = new ArrayList<BaseColumnFieldDiff>( patternDiff.size() );
                }
                diffs.addAll( patternDiff );
                bUpdateColumnDefinition = true; // Force firing the audit log change event.
            }

            // Update column's visibility
            if ( isHideUpdated ) {
                setColumnVisibility( origColumn,
                                     !editColumn.isHideColumn() );
            }

            //Clear otherwise if column cannot accept them
            if ( isOperatorUpdated && !canAcceptOtherwiseValues( editColumn ) ) {
                removeOtherwiseStates( origColumn );
                bUpdateColumnData = true;
            }

            //Clear comma-separated values if column cannot accept them
            if ( isOperatorUpdated && !canAcceptCommaSeparatedValues( editColumn ) ) {
                cellUtils.removeCommaSeparatedValue( editColumn.getDefaultValue() );
                removeCommaSeparatedValues( origColumn );
                bUpdateColumnData = true;
            }

            // Update column's Cell type. Other than the obvious change in data-type if the
            // Operator changes to or from "not set" (possible for literal columns and formulae)
            // the column needs to be changed to or from Text.
            if ( isFactTypeUpdated || isFactFieldUpdated || isFieldTypeUpdated || isOperatorUpdated || isConstraintValueTypeUpdated ) {
                bUpdateColumnData = true;
            }

            // Update column's cell content if the Optional Value list has changed
            if ( isValueListUpdated ) {
                bUpdateColumnData = updateCellsForOptionValueList( editColumn,
                                                                   origColumn );
            }

            if ( origColumn.isBound() && editColumn.isBound() && !origColumn.getBinding().equals( editColumn.getBinding() ) ) {
                bUpdateColumnDefinition = true;
            }

            //Log change to column definition
            if ( bUpdateColumnDefinition ) {
                model.getAuditLog().add( new UpdateColumnAuditLogEntry( identity.getName(),
                                                                        origColumn,
                                                                        editColumn, diffs ) );
            }

            // Copy new values into original column definition
            populateModelColumn( origColumn,
                                 editColumn );
        }

        //First remove merging if column data is being changed. This is necessary before we potentially update
        //the column's cell type as removing merging causes a redraw that needs the column's cell to be
        //consistent with the column's data-type
        if ( bUpdateColumnData ) {
            ToggleMergingEvent tme = new ToggleMergingEvent( false );
            eventBus.fireEvent( tme );
        }

        //Update Column cell
        if ( bUpdateColumnDefinition ) {
            int iCol = model.getExpandedColumns().indexOf( origColumn );
            DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell = cellFactory.getCell( origColumn );
            UpdateColumnDefinitionEvent updateColumnDefinition = new UpdateColumnDefinitionEvent( cell,
                                                                                                  iCol );
            eventBus.fireEvent( updateColumnDefinition );
        }

        //Update Column data
        if ( bUpdateColumnData ) {
            int iCol = model.getExpandedColumns().indexOf( origColumn );
            UpdateColumnDataEvent updateColumnData = new UpdateColumnDataEvent( iCol,
                                                                                getColumnData( origColumn ) );
            eventBus.fireEvent( updateColumnData );
        }

    }

    /**
     * Update values controlled by the decision table itself
     */
    public void updateSystemControlledColumnValues() {

        for ( BaseColumn column : model.getExpandedColumns() ) {
            if ( column instanceof RowNumberCol52 ) {
                updateRowNumberColumnValues( column );

            } else if ( column instanceof AttributeCol52 ) {

                // Update Salience values
                AttributeCol52 attrCol = (AttributeCol52) column;
                if ( attrCol.getAttribute().equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                    updateSalienceColumnValues( attrCol );
                }
            }
        }
    }

    // Add column to table with optional redraw
    private void addColumn( MetadataCol52 modelColumn,
                            List<DTCellValue52> columnData,
                            boolean bRedraw ) {
        final int index = model.getExpandedColumns().indexOf( modelColumn );
        InsertDecisionTableColumnEvent dce = new InsertDecisionTableColumnEvent( modelColumn,
                                                                                 columnData,
                                                                                 index,
                                                                                 bRedraw );
        eventBus.fireEvent( dce );
    }

    // Add column to table with optional redraw
    private void addColumn( AttributeCol52 modelColumn,
                            List<DTCellValue52> columnData,
                            boolean bRedraw ) {
        final int index = model.getExpandedColumns().indexOf( modelColumn );
        InsertDecisionTableColumnEvent dce = new InsertDecisionTableColumnEvent( modelColumn,
                                                                                 columnData,
                                                                                 index,
                                                                                 bRedraw );
        eventBus.fireEvent( dce );
    }

    // Add column to table with optional redraw
    private void addColumn( ConditionCol52 modelColumn,
                            List<DTCellValue52> columnData,
                            boolean bRedraw ) {
        int index = findConditionColumnIndex( modelColumn );
        InsertDecisionTableColumnEvent dce = new InsertDecisionTableColumnEvent( modelColumn,
                                                                                 columnData,
                                                                                 index,
                                                                                 bRedraw );
        eventBus.fireEvent( dce );
    }

    // Add column to table with optional redraw
    private void addColumn( ActionCol52 modelColumn,
                            List<DTCellValue52> columnData,
                            boolean bRedraw ) {
        final int index = model.getExpandedColumns().indexOf( modelColumn );
        InsertDecisionTableColumnEvent dce = new InsertDecisionTableColumnEvent( modelColumn,
                                                                                 columnData,
                                                                                 index,
                                                                                 bRedraw );
        eventBus.fireEvent( dce );
    }

    // Add column to table with optional redraw
    private void addBRLActionVariableColumns( List<BRLActionVariableColumn> modelColumns,
                                              boolean bRedraw ) {
        final int index = model.getExpandedColumns().indexOf( modelColumns.get( 0 ) );
        final List<BaseColumn> columns = new ArrayList<BaseColumn>();
        final List<List<DTCellValue52>> columnsData = new ArrayList<List<DTCellValue52>>();
        columns.addAll( modelColumns );
        for ( BaseColumn column : columns ) {
            columnsData.add( cellValueFactory.makeColumnData( column ) );
        }
        InsertDecisionTableColumnEvent dce = new InsertDecisionTableColumnEvent( columns,
                                                                                 columnsData,
                                                                                 index,
                                                                                 bRedraw );
        eventBus.fireEvent( dce );
    }

    // Add column to table with optional redraw
    private void addBRLConditionVariableColumns( List<BRLConditionVariableColumn> modelColumns,
                                                 boolean bRedraw ) {
        final int index = model.getExpandedColumns().indexOf( modelColumns.get( 0 ) );
        final List<BaseColumn> columns = new ArrayList<BaseColumn>();
        final List<List<DTCellValue52>> columnsData = new ArrayList<List<DTCellValue52>>();
        columns.addAll( modelColumns );
        for ( BaseColumn column : columns ) {
            columnsData.add( cellValueFactory.makeColumnData( column ) );
        }
        InsertDecisionTableColumnEvent dce = new InsertDecisionTableColumnEvent( columns,
                                                                                 columnsData,
                                                                                 index,
                                                                                 bRedraw );
        eventBus.fireEvent( dce );
    }

    /**
     * Check whether the given column can accept "otherwise" values
     * @param column
     * @return true if the Column can accept "otherwise" values
     */
    private boolean canAcceptOtherwiseValues( BaseColumn column ) {

        //Check the column type is correct
        if ( !( column instanceof ConditionCol52 ) ) {
            return false;
        }
        ConditionCol52 cc = (ConditionCol52) column;

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

    /**
     * Check whether the given column can accept comma-separated values
     * @param column
     * @return true if the Column can accept comma-separated values
     */
    private boolean canAcceptCommaSeparatedValues( BaseColumn column ) {

        //Check the column type is correct
        if ( !( column instanceof ConditionCol52 ) ) {
            return false;
        }
        ConditionCol52 cc = (ConditionCol52) column;

        //Check column contains literal values and uses the equals operator
        if ( cc.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_LITERAL ) {
            return false;
        }

        //Check operator is supported
        final List<String> ops = Arrays.asList( OperatorsOracle.EXPLICIT_LIST_OPERATORS );
        return ops.contains( cc.getOperator() );
    }

    // Find the right-most index for a Condition column
    private int findConditionColumnIndex( ConditionCol52 col ) {
        int index = 0;
        boolean bMatched = false;
        List<BaseColumn> columns = model.getExpandedColumns();
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            BaseColumn column = columns.get( iCol );
            if ( column instanceof RowNumberCol52 ) {
                index = iCol;
            } else if ( column instanceof DescriptionCol52 ) {
                index = iCol;
            } else if ( column instanceof MetadataCol52 ) {
                index = iCol;
            } else if ( column instanceof AttributeCol52 ) {
                index = iCol;
            } else if ( column instanceof ConditionCol52 ) {
                if ( isEquivalentConditionColumn( (ConditionCol52) column,
                                                  col ) ) {
                    index = iCol;
                    bMatched = true;
                } else if ( !bMatched ) {
                    index = iCol;
                }
            }
        }
        return index;
    }

    // Retrieve the data for a particular column
    private List<CellValue<? extends Comparable<?>>> getColumnData( BaseColumn column ) {
        int iColIndex = model.getExpandedColumns().indexOf( column );
        List<CellValue<? extends Comparable<?>>> columnData = new ArrayList<CellValue<? extends Comparable<?>>>();
        for ( List<DTCellValue52> row : model.getData() ) {
            DTCellValue52 dcv = row.get( iColIndex );
            columnData.add( cellValueFactory.convertModelCellValue( column,
                                                                    dcv ) );
        }
        return columnData;
    }

    // Retrieve the data for the analysis column
    private List<CellValue<? extends Comparable<?>>> getAnalysisColumnData() {
        List<CellValue<? extends Comparable<?>>> columnData = new ArrayList<CellValue<? extends Comparable<?>>>();
        List<Analysis> analysisData = model.getAnalysisData();
        for ( int i = 0; i < analysisData.size(); i++ ) {
            Analysis analysis = analysisData.get( i );
            CellValue<Analysis> cell = new CellValue<Analysis>( analysis );
            columnData.add( cell );
        }
        return columnData;
    }

    // Check whether two Objects are equal or both null
    private boolean isEqualOrNull( Object s1,
                                   Object s2 ) {
        if ( s1 == null
                && s2 == null ) {
            return true;
        } else if ( s1 != null
                && s2 != null
                && s1.equals( s2 ) ) {
            return true;
        }
        return false;
    }

    // Check whether two ConditionCols are equivalent
    private boolean isEquivalentConditionColumn( ConditionCol52 c1,
                                                 ConditionCol52 c2 ) {

        Pattern52 c1Pattern = model.getPattern( c1 );
        Pattern52 c2Pattern = model.getPattern( c2 );

        if ( isEqualOrNull( c1Pattern.getFactType(),
                            c2Pattern.getFactType() )
                && isEqualOrNull( c1Pattern.getBoundName(),
                                  c2Pattern.getBoundName() ) ) {
            return true;
        }
        return false;
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn( final ActionInsertFactCol52 col,
                                      final ActionInsertFactCol52 editingCol ) {
        col.setBoundName( editingCol.getBoundName() );
        col.setType( editingCol.getType() );
        col.setFactField( editingCol.getFactField() );
        col.setHeader( editingCol.getHeader() );
        col.setValueList( editingCol.getValueList() );
        col.setDefaultValue( editingCol.getDefaultValue() );
        col.setHideColumn( editingCol.isHideColumn() );
        col.setFactType( editingCol.getFactType() );
        col.setInsertLogical( editingCol.isInsertLogical() );
        if ( col instanceof LimitedEntryCol && editingCol instanceof LimitedEntryCol ) {
            ( (LimitedEntryCol) col ).setValue( ( (LimitedEntryCol) editingCol ).getValue() );
        }
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn( final ActionSetFieldCol52 col,
                                      final ActionSetFieldCol52 editingCol ) {
        col.setBoundName( editingCol.getBoundName() );
        col.setType( editingCol.getType() );
        col.setFactField( editingCol.getFactField() );
        col.setHeader( editingCol.getHeader() );
        col.setValueList( editingCol.getValueList() );
        col.setDefaultValue( editingCol.getDefaultValue() );
        col.setHideColumn( editingCol.isHideColumn() );
        col.setUpdate( editingCol.isUpdate() );
        if ( col instanceof LimitedEntryCol && editingCol instanceof LimitedEntryCol ) {
            ( (LimitedEntryCol) col ).setValue( ( (LimitedEntryCol) editingCol ).getValue() );
        }
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn( final ActionRetractFactCol52 col,
                                      final ActionRetractFactCol52 editingCol ) {
        col.setHeader( editingCol.getHeader() );
        col.setDefaultValue( editingCol.getDefaultValue() );
        col.setHideColumn( editingCol.isHideColumn() );
        if ( col instanceof LimitedEntryCol && editingCol instanceof LimitedEntryCol ) {
            ( (LimitedEntryCol) col ).setValue( ( (LimitedEntryCol) editingCol ).getValue() );
        }
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn( final ActionWorkItemCol52 col,
                                      final ActionWorkItemCol52 editingCol ) {
        col.setHeader( editingCol.getHeader() );
        col.setDefaultValue( editingCol.getDefaultValue() );
        col.setHideColumn( editingCol.isHideColumn() );
        col.setWorkItemDefinition( editingCol.getWorkItemDefinition() );
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn( final ActionWorkItemSetFieldCol52 col,
                                      final ActionWorkItemSetFieldCol52 editingCol ) {
        col.setBoundName( editingCol.getBoundName() );
        col.setType( editingCol.getType() );
        col.setFactField( editingCol.getFactField() );
        col.setHeader( editingCol.getHeader() );
        col.setHideColumn( editingCol.isHideColumn() );
        col.setUpdate( editingCol.isUpdate() );
        col.setWorkItemName( editingCol.getWorkItemName() );
        col.setWorkItemResultParameterName( editingCol.getWorkItemResultParameterName() );
        col.setParameterClassName( editingCol.getParameterClassName() );
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn( final LimitedEntryBRLActionColumn col,
                                      final LimitedEntryBRLActionColumn editingCol ) {
        col.setHeader( editingCol.getHeader() );
        col.setDefaultValue( editingCol.getDefaultValue() );
        col.setHideColumn( editingCol.isHideColumn() );
        col.setDefinition( editingCol.getDefinition() );
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn( final ConditionCol52 col,
                                      final ConditionCol52 editingCol ) {
        col.setConstraintValueType( editingCol.getConstraintValueType() );
        col.setFactField( editingCol.getFactField() );
        col.setFieldType( editingCol.getFieldType() );
        col.setHeader( editingCol.getHeader() );
        col.setOperator( editingCol.getOperator() );
        col.setValueList( editingCol.getValueList() );
        col.setDefaultValue( editingCol.getDefaultValue() );
        col.setHideColumn( editingCol.isHideColumn() );
        col.setParameters( editingCol.getParameters() );
        col.setBinding( editingCol.getBinding() );
        if ( col instanceof LimitedEntryCol && editingCol instanceof LimitedEntryCol ) {
            ( (LimitedEntryCol) col ).setValue( ( (LimitedEntryCol) editingCol ).getValue() );
        }
    }

    // Copy values from one (transient) model column into another
    private void populateModelColumn( final LimitedEntryBRLConditionColumn col,
                                      final LimitedEntryBRLConditionColumn editingCol ) {
        col.setHeader( editingCol.getHeader() );
        col.setDefaultValue( editingCol.getDefaultValue() );
        col.setHideColumn( editingCol.isHideColumn() );
        col.setDefinition( editingCol.getDefinition() );
    }

    //Remove Otherwise state from column cells
    private void removeOtherwiseStates( DTColumnConfig52 column ) {
        int index = this.model.getExpandedColumns().indexOf( column );
        for ( List<DTCellValue52> row : this.model.getData() ) {
            DTCellValue52 dcv = row.get( index );
            dcv.setOtherwise( false );
        }
    }

    //Convert comma-separated values to the first in the list
    private void removeCommaSeparatedValues( DTColumnConfig52 column ) {
        int index = this.model.getExpandedColumns().indexOf( column );
        if ( index >= 0 ) {
            for ( List<DTCellValue52> row : this.model.getData() ) {
                DTCellValue52 dcv = row.get( index );
                cellUtils.removeCommaSeparatedValue( dcv );
            }
        }
    }

    // Ensure the values in a column are within the Value List
    private boolean updateCellsForOptionValueList( final DTColumnConfig52 editColumn,
                                                   final DTColumnConfig52 origColumn ) {

        //If the new column definition has no Value List the existing values remain valid
        List<String> vals = Arrays.asList( utils.getValueList( editColumn ) );
        final boolean clearExistingValues = vals.size() > 0;

        boolean bUpdateColumnData = false;
        int iCol = model.getExpandedColumns().indexOf( origColumn );
        if ( iCol >= 0 ) {
            for ( List<DTCellValue52> row : this.model.getData() ) {
                if ( !vals.contains( row.get( iCol ).getStringValue() ) ) {
                    bUpdateColumnData = true;
                }
                if ( clearExistingValues ) {
                    row.get( iCol ).clearValues();
                }
            }
        }
        return bUpdateColumnData;

    }

    // Update Row Number column values
    private void updateRowNumberColumnValues( BaseColumn column ) {
        int rowNumber = 1;
        int iColIndex = model.getExpandedColumns().indexOf( column );
        for ( List<DTCellValue52> row : model.getData() ) {
            row.get( iColIndex ).setNumericValue( rowNumber );
            rowNumber++;
        }

        //Raise event to the grid widget
        UpdateColumnDataEvent uce = new UpdateColumnDataEvent( iColIndex,
                                                               getColumnData( column ) );
        eventBus.fireEvent( uce );
    }

    // Update Salience column definition and values
    private void updateSalienceColumnValues( AttributeCol52 column ) {

        //Ensure Salience cells are rendered with the correct Cell
        int iColIndex = model.getExpandedColumns().indexOf( column );
        UpdateColumnDefinitionEvent updateColumnDefinition = new UpdateColumnDefinitionEvent( cellFactory.getCell( column ),
                                                                                              column.isUseRowNumber(),
                                                                                              !column.isUseRowNumber(),
                                                                                              iColIndex );
        eventBus.fireEvent( updateColumnDefinition );

        //If Salience values are-user defined, exit
        if ( !column.isUseRowNumber() ) {
            return;
        }

        //If Salience values are not reverse order use the Row Number values
        if ( !column.isReverseOrder() ) {
            updateRowNumberColumnValues( column );
        }

        //If Salience values are reverse order derive them and update column
        int salience = ( column.isReverseOrder() ? model.getData().size() : 1 );
        for ( List<DTCellValue52> row : model.getData() ) {
            row.get( iColIndex ).setNumericValue( salience );
            if ( column.isReverseOrder() ) {
                salience--;
            } else {
                salience++;
            }
        }
        UpdateColumnDataEvent updateColumnData = new UpdateColumnDataEvent( iColIndex,
                                                                            getColumnData( column ) );
        eventBus.fireEvent( updateColumnData );
    }

    public void analyze() {
        model.getAnalysisData().clear();
        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer( oracle );
        List<Analysis> analysisData = analyzer.analyze( model );
        model.getAnalysisData().addAll( analysisData );
        showAnalysis();
    }

    private void showAnalysis() {
        AnalysisCol52 analysisCol = model.getAnalysisCol();
        int analysisColumnIndex = model.getExpandedColumns().indexOf( analysisCol );

        UpdateColumnDataEvent updateColumnData = new UpdateColumnDataEvent( analysisColumnIndex,
                                                                            getAnalysisColumnData() );
        eventBus.fireEvent( updateColumnData );

        analysisCol.setHideColumn( false );
        setColumnVisibility( analysisCol,
                             !analysisCol.isHideColumn() );
    }

    /**
     * Move a Pattern to the given index in the model
     * @param pattern The Pattern to which the Condition relates
     * @param patternTargetIndex The index to which the pattern will be moved
     */
    public void movePattern( CompositeColumn<?> pattern,
                             int patternTargetIndex ) {

        //Sanity check
        if ( patternTargetIndex < 0 || patternTargetIndex > model.getConditions().size() - 1 ) {
            throw new IndexOutOfBoundsException();
        }

        //If target index is the Patterns current position exit
        int patternSourceIndex = model.getConditions().indexOf( pattern );
        if ( patternSourceIndex == patternTargetIndex ) {
            return;
        }

        //Update model
        if ( patternTargetIndex > patternSourceIndex ) {

            //Move down (after)
            CompositeColumn<?> patternBeingMovedAfter = model.getConditions().get( patternTargetIndex );
            int sourceColumnIndex = model.getExpandedColumns().indexOf( pattern.getChildColumns().get( 0 ) );
            int targetColumnIndex = model.getExpandedColumns().indexOf( patternBeingMovedAfter.getChildColumns().get( patternBeingMovedAfter.getChildColumns().size() - 1 ) );
            int numberOfColumns = pattern.getChildColumns().size();

            //Update model
            model.getConditions().remove( pattern );
            model.getConditions().add( patternTargetIndex,
                                       pattern );

            //Update data and UI
            MoveColumnsEvent mce = new MoveColumnsEvent( sourceColumnIndex,
                                                         targetColumnIndex,
                                                         numberOfColumns );
            eventBus.fireEvent( mce );

        } else {
            //Move up (before)
            CompositeColumn<?> patternBeingMovedBefore = model.getConditions().get( patternTargetIndex );
            int sourceColumnIndex = model.getExpandedColumns().indexOf( pattern.getChildColumns().get( 0 ) );
            int targetColumnIndex = model.getExpandedColumns().indexOf( patternBeingMovedBefore.getChildColumns().get( 0 ) );
            int numberOfColumns = pattern.getChildColumns().size();

            //Update model
            model.getConditions().remove( pattern );
            model.getConditions().add( patternTargetIndex,
                                       pattern );

            //Update data and UI
            MoveColumnsEvent mce = new MoveColumnsEvent( sourceColumnIndex,
                                                         targetColumnIndex,
                                                         numberOfColumns );
            eventBus.fireEvent( mce );
        }

    }

    /**
     * Move a Condition to the given index on a Pattern in the model
     * @param pattern The Pattern to which the Condition relates
     * @param condition The Condition being moved
     * @param conditionTargetIndex The index in the pattern to which the column will be moved
     */
    public void moveCondition( Pattern52 pattern,
                               ConditionCol52 condition,
                               int conditionTargetIndex ) {

        //Sanity check
        if ( conditionTargetIndex < 0 || conditionTargetIndex > pattern.getChildColumns().size() - 1 ) {
            throw new IndexOutOfBoundsException();
        }

        //If target index is the Conditions current position exit
        int conditionSourceIndex = pattern.getChildColumns().indexOf( condition );
        if ( conditionSourceIndex == conditionTargetIndex ) {
            return;
        }

        ConditionCol52 conditionTarget = pattern.getChildColumns().get( conditionTargetIndex );
        int conditionTargetColumnIndex = model.getExpandedColumns().indexOf( conditionTarget );
        int conditionSourceColumnIndex = model.getExpandedColumns().indexOf( condition );

        //Update model
        pattern.getChildColumns().remove( condition );
        pattern.getChildColumns().add( conditionTargetIndex,
                                       condition );

        //Update data and UI
        MoveColumnsEvent mce = new MoveColumnsEvent( conditionSourceColumnIndex,
                                                     conditionTargetColumnIndex,
                                                     1 );
        eventBus.fireEvent( mce );
    }

    /**
     * Move an action to the given index in the model
     * @param action The Action being moved
     * @param actionTargetIndex The index in the model to which the column will be moved
     */
    public void moveAction( ActionCol52 action,
                            int actionTargetIndex ) {

        //Sanity check
        if ( actionTargetIndex < 0 || actionTargetIndex > model.getActionCols().size() - 1 ) {
            throw new IndexOutOfBoundsException();
        }

        //If target index is the Actions current position exit
        int actionSourceIndex = model.getActionCols().indexOf( action );
        if ( actionSourceIndex == actionTargetIndex ) {
            return;
        }

        //Update model
        if ( actionTargetIndex > actionSourceIndex ) {

            //Move down (after)
            ActionCol52 actionBeingMovedAfter = model.getActionCols().get( actionTargetIndex );
            int sourceColumnIndex = -1;
            int targetColumnIndex = -1;
            int numberOfColumns = -1;

            if ( action instanceof BRLActionColumn ) {
                BRLActionColumn brlColumn = (BRLActionColumn) action;
                BRLActionVariableColumn variable = brlColumn.getChildColumns().get( 0 );
                sourceColumnIndex = model.getExpandedColumns().indexOf( variable );
                numberOfColumns = brlColumn.getChildColumns().size();
            } else {
                sourceColumnIndex = model.getExpandedColumns().indexOf( action );
                numberOfColumns = 1;
            }

            if ( actionBeingMovedAfter instanceof BRLActionColumn ) {
                BRLActionColumn brlColumn = (BRLActionColumn) actionBeingMovedAfter;
                BRLActionVariableColumn variable = brlColumn.getChildColumns().get( brlColumn.getChildColumns().size() - 1 );
                targetColumnIndex = model.getExpandedColumns().indexOf( variable );
            } else {
                targetColumnIndex = model.getExpandedColumns().indexOf( actionBeingMovedAfter );
            }

            //Update model
            model.getActionCols().remove( action );
            model.getActionCols().add( actionTargetIndex,
                                       action );

            //Update data and UI
            MoveColumnsEvent mce = new MoveColumnsEvent( sourceColumnIndex,
                                                         targetColumnIndex,
                                                         numberOfColumns );
            eventBus.fireEvent( mce );

        } else {
            //Move up (before)
            ActionCol52 actionBeingMovedBefore = model.getActionCols().get( actionTargetIndex );
            int sourceColumnIndex = -1;
            int targetColumnIndex = -1;
            int numberOfColumns = -1;

            if ( action instanceof BRLActionColumn ) {
                BRLActionColumn brlColumn = (BRLActionColumn) action;
                BRLActionVariableColumn variable = brlColumn.getChildColumns().get( 0 );
                sourceColumnIndex = model.getExpandedColumns().indexOf( variable );
                numberOfColumns = brlColumn.getChildColumns().size();
            } else {
                sourceColumnIndex = model.getExpandedColumns().indexOf( action );
                numberOfColumns = 1;
            }

            if ( actionBeingMovedBefore instanceof BRLActionColumn ) {
                BRLActionColumn brlColumn = (BRLActionColumn) actionBeingMovedBefore;
                BRLActionVariableColumn variable = brlColumn.getChildColumns().get( 0 );
                targetColumnIndex = model.getExpandedColumns().indexOf( variable );
            } else {
                targetColumnIndex = model.getExpandedColumns().indexOf( actionBeingMovedBefore );
            }

            //Update model
            model.getActionCols().remove( action );
            model.getActionCols().add( actionTargetIndex,
                                       action );

            //Update data and UI
            MoveColumnsEvent mce = new MoveColumnsEvent( sourceColumnIndex,
                                                         targetColumnIndex,
                                                         numberOfColumns );
            eventBus.fireEvent( mce );
        }
    }

    public void onDeleteRow( DeleteRowEvent event ) {
        model.getData().remove( event.getIndex() );
        model.getAnalysisData().remove( event.getIndex() );
        Scheduler.get().scheduleFinally( new Command() {

            public void execute() {
                updateSystemControlledColumnValues();
            }

        } );

        //Log deletion of row
        model.getAuditLog().add( new DeleteRowAuditLogEntry( identity.getName(),
                                                             event.getIndex() ) );
    }

    public void onInsertRow( InsertRowEvent event ) {
        List<DTCellValue52> data = cellValueFactory.makeRowData();
        model.getData().add( event.getIndex(),
                             data );
        model.getAnalysisData().add( event.getIndex(),
                                     new Analysis() );
        Scheduler.get().scheduleFinally( new Command() {

            public void execute() {
                updateSystemControlledColumnValues();
            }

        } );

        //Log insertion of row
        model.getAuditLog().add( new InsertRowAuditLogEntry( identity.getName(),
                                                             event.getIndex() ) );
    }

    public void onCopyRows( CopyRowsEvent event ) {
        copiedRows.clear();
        for ( Integer iRow : event.getRowIndexes() ) {
            copiedRows.add( model.getData().get( iRow ) );
        }
    }

    public void onPasteRows( PasteRowsEvent event ) {
        if ( copiedRows == null || copiedRows.size() == 0 ) {
            return;
        }
        int iRow = event.getTargetRowIndex();
        for ( List<DTCellValue52> sourceRowData : copiedRows ) {
            List<DTCellValue52> rowData = cellValueFactory.makeRowData();

            //Clone the row, other than RowNumber column
            for ( int iCol = 1; iCol < sourceRowData.size(); iCol++ ) {
                DTCellValue52 sourceCell = sourceRowData.get( iCol );
                DTCellValue52 targetCell = rowData.get( iCol );
                switch ( sourceCell.getDataType() ) {
                    case BOOLEAN:
                        targetCell.setBooleanValue( sourceCell.getBooleanValue() );
                        break;
                    case DATE:
                        targetCell.setDateValue( sourceCell.getDateValue() );
                        break;
                    case NUMERIC:
                        targetCell.setNumericValue( sourceCell.getNumericValue() );
                        break;
                    case NUMERIC_BIGDECIMAL:
                        targetCell.setNumericValue( (BigDecimal) sourceCell.getNumericValue() );
                        break;
                    case NUMERIC_BIGINTEGER:
                        targetCell.setNumericValue( (BigInteger) sourceCell.getNumericValue() );
                        break;
                    case NUMERIC_BYTE:
                        targetCell.setNumericValue( (Byte) sourceCell.getNumericValue() );
                        break;
                    case NUMERIC_DOUBLE:
                        targetCell.setNumericValue( (Double) sourceCell.getNumericValue() );
                        break;
                    case NUMERIC_FLOAT:
                        targetCell.setNumericValue( (Float) sourceCell.getNumericValue() );
                        break;
                    case NUMERIC_INTEGER:
                        targetCell.setNumericValue( (Integer) sourceCell.getNumericValue() );
                        break;
                    case NUMERIC_LONG:
                        targetCell.setNumericValue( (Long) sourceCell.getNumericValue() );
                        break;
                    case NUMERIC_SHORT:
                        targetCell.setNumericValue( (Short) sourceCell.getNumericValue() );
                        break;
                    default:
                        targetCell.setStringValue( sourceCell.getStringValue() );
                }
            }

            model.getData().add( iRow,
                                 rowData );
            model.getAnalysisData().add( iRow,
                                         new Analysis() );

            //Log insertion of row
            model.getAuditLog().add( new InsertRowAuditLogEntry( identity.getName(),
                                                                 iRow ) );

            iRow++;
        }
        Scheduler.get().scheduleFinally( new Command() {

            public void execute() {
                updateSystemControlledColumnValues();
            }

        } );

    }

    public void onAppendRow( AppendRowEvent event ) {
        List<DTCellValue52> data = cellValueFactory.makeRowData();
        model.getData().add( data );
        model.getAnalysisData().add( new Analysis() );
        Scheduler.get().scheduleFinally( new Command() {

            public void execute() {
                updateSystemControlledColumnValues();
            }

        } );

        //Log insertion of row
        model.getAuditLog().add( new InsertRowAuditLogEntry( identity.getName(),
                                                             model.getData().size() - 1 ) );
    }

    public void onDeleteColumn( DeleteColumnEvent event ) {
        int firstColumnIndex = event.getFirstColumnIndex();
        for ( int iCol = 0; iCol < event.getNumberOfColumns(); iCol++ ) {
            for ( List<DTCellValue52> row : model.getData() ) {
                row.remove( firstColumnIndex );
            }
        }
    }

    public void onInsertColumn( InsertColumnEvent<BaseColumn, DTCellValue52> event ) {
        int index = event.getIndex();
        List<List<DTCellValue52>> columnsData = event.getColumnsData();
        for ( int iCol = 0; iCol < columnsData.size(); iCol++ ) {
            List<DTCellValue52> columnData = columnsData.get( iCol );
            for ( int iRow = 0; iRow < columnData.size(); iRow++ ) {
                DTCellValue52 dcv = columnData.get( iRow );
                List<DTCellValue52> row = model.getData().get( iRow );
                row.add( index,
                         dcv );
            }
            index++;
        }
    }

    public void onSelectedCellChange( SelectedCellChangeEvent event ) {
        if ( event.getCellSelectionDetail() == null ) {
            setEnableOtherwiseButton( false );
        } else {
            Coordinate c = event.getCellSelectionDetail().getCoordinate();
            BaseColumn column = model.getExpandedColumns().get( c.getCol() );
            setEnableOtherwiseButton( canAcceptOtherwiseValues( column ) && !this.isReadOnly );
        }
    }

    protected abstract void setEnableOtherwiseButton( final boolean isEnabled );

    public void onMoveColumns( MoveColumnsEvent event ) {
        int sourceColumnIndex = event.getSourceColumnIndex();
        int targetColumnIndex = event.getTargetColumnIndex();
        int numberOfColumns = event.getNumberOfColumns();

        //Move source columns to destination
        if ( targetColumnIndex > sourceColumnIndex ) {
            for ( int iCol = 0; iCol < numberOfColumns; iCol++ ) {
                for ( int iRow = 0; iRow < model.getData().size(); iRow++ ) {
                    List<DTCellValue52> row = model.getData().get( iRow );
                    row.add( targetColumnIndex,
                             row.remove( sourceColumnIndex ) );
                }
            }
        } else if ( targetColumnIndex < sourceColumnIndex ) {
            for ( int iCol = 0; iCol < numberOfColumns; iCol++ ) {
                for ( int iRow = 0; iRow < model.getData().size(); iRow++ ) {
                    List<DTCellValue52> row = model.getData().get( iRow );
                    row.add( targetColumnIndex,
                             row.remove( sourceColumnIndex ) );
                }
                sourceColumnIndex++;
                targetColumnIndex++;
            }
        }

    }

    public void onUpdateModel( UpdateModelEvent event ) {

        //Copy data into the underlying model
        Map<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> updates = event.getUpdates();
        for ( Map.Entry<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> e : updates.entrySet() ) {

            //Coordinate of change
            Coordinate originCoordinate = e.getKey();
            int originRowIndex = originCoordinate.getRow();
            int originColumnIndex = originCoordinate.getCol();

            //Changed data
            List<List<CellValue<? extends Comparable<?>>>> data = e.getValue();

            for ( int iRow = 0; iRow < data.size(); iRow++ ) {
                List<CellValue<? extends Comparable<?>>> rowData = data.get( iRow );
                int targetRowIndex = originRowIndex + iRow;
                for ( int iCol = 0; iCol < rowData.size(); iCol++ ) {
                    int targetColumnIndex = originColumnIndex + iCol;
                    CellValue<? extends Comparable<?>> changedCell = rowData.get( iCol );
                    BaseColumn col = model.getExpandedColumns().get( targetColumnIndex );
                    DTCellValue52 dcv = cellValueFactory.convertToModelCell( col,
                                                                             changedCell );
                    model.getData().get( targetRowIndex ).set( targetColumnIndex,
                                                               dcv );
                }
            }
        }

        //Update system controlled columns
        Scheduler.get().scheduleFinally( new Command() {

            public void execute() {
                updateSystemControlledColumnValues();
            }

        } );
    }

}
