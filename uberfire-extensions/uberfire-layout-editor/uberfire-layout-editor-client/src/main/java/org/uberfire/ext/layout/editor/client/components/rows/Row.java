package org.uberfire.ext.layout.editor.client.components.rows;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.client.api.ComponentDropEvent;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponents;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn;
import org.uberfire.ext.layout.editor.client.infra.*;
import org.uberfire.mvp.ParameterizedCommand;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class Row {

    public static final int COLUMN_DEFAULT_SIZE = 12;

    public interface View extends UberElement<Row> {

        void addColumn( UberElement<ComponentColumn> view );

        void clear();

    }

    private UniqueIDGenerator idGenerator = new UniqueIDGenerator();

    private String id;

    private View view;

    private List<Column> columns = new ArrayList<Column>();

    private Instance<ComponentColumn> columnInstance;

    private Instance<ColumnWithComponents> columnWithComponentsInstance;

    private ParameterizedCommand<RowDrop> dropOnRowCommand;

    private ParameterizedCommand<Row> removeRowCommand;

    private ParameterizedCommand<ColumnDrop> removeComponentCommand;

    private ColumnWithComponents parentColumnWithComponents;

    private DnDManager dndManager;

    private boolean dropEnable = true;

    private final LayoutDragComponentHelper layoutDragComponentHelper;

    private Event<ComponentDropEvent> componentDropEvent;

    private Event<ComponentRemovedEvent> componentRemovedEvent;

    @Inject
    public Row( View view, Instance<ComponentColumn> columnInstance,
                Instance<ColumnWithComponents> columnWithComponentsInstance,
                DnDManager dndManager,
                LayoutDragComponentHelper layoutDragComponentHelper,
                Event<ComponentDropEvent> componentDropEvent,
                Event<ComponentRemovedEvent> componentRemovedEvent ) {

        this.view = view;
        this.columnInstance = columnInstance;
        this.columnWithComponentsInstance = columnWithComponentsInstance;
        this.dndManager = dndManager;
        this.layoutDragComponentHelper = layoutDragComponentHelper;
        this.componentDropEvent = componentDropEvent;
        this.componentRemovedEvent = componentRemovedEvent;
    }

    public void init( ParameterizedCommand<RowDrop> dropOnRowCommand,
                      ParameterizedCommand<Row> removeCommand,
                      ParameterizedCommand<ColumnDrop> removeComponentCommand ) {
        this.dropOnRowCommand = dropOnRowCommand;
        this.removeRowCommand = removeCommand;
        this.removeComponentCommand = removeComponentCommand;
        this.parentColumnWithComponents = null;
    }

    public void init( ParameterizedCommand<RowDrop> dropOnRowCommand,
                      ParameterizedCommand<Row> removeCommand,
                      ParameterizedCommand<ColumnDrop> removeComponentCommand,
                      ColumnWithComponents parentColumnWithComponents ) {
        this.dropOnRowCommand = dropOnRowCommand;
        this.removeRowCommand = removeCommand;
        this.removeComponentCommand = removeComponentCommand;
        this.parentColumnWithComponents = parentColumnWithComponents;
    }

    public void load( ParameterizedCommand<RowDrop> dropOnRowCommand, LayoutRow layoutRow,
                      ParameterizedCommand<Row> removeCommand,
                      ParameterizedCommand<ColumnDrop> removeComponentCommand ) {
        this.dropOnRowCommand = dropOnRowCommand;
        this.removeRowCommand = removeCommand;
        this.removeComponentCommand = removeComponentCommand;
        extractColumns( layoutRow );
        setupColumnResizeActions();
    }

    private void extractColumns( LayoutRow layoutRow ) {
        for ( LayoutColumn layoutColumn : layoutRow.getLayoutColumns() ) {
            if ( isColumnWithComponents( layoutColumn ) ) {
                extractColumnWithComponents( layoutColumn );

            } else {
                extractComponentColumn( layoutColumn );
            }
        }
    }


    private void extractComponentColumn( LayoutColumn layoutColumn ) {
        ComponentColumn newComponentColumn = getComponentColumn( layoutColumn );
        this.columns.add( newComponentColumn );
    }

    private void extractColumnWithComponents( LayoutColumn layoutColumn ) {
        for ( LayoutRow row : layoutColumn.getRows() ) {
            Integer size = new Integer( layoutColumn.getSpan() );
            final ColumnWithComponents columnWithComponents = createColumnWithComponentsInstance();

            columnWithComponents
                    .init( id, size,
                           dropCommand(), removeComponentCommand,
                           removeColumnCommand() );

            for ( LayoutColumn column : row.getLayoutColumns() ) {
                ComponentColumn newComponentColumn = getComponentColumn( column );
                newComponentColumn.setInnerColumn();
                columnWithComponents.withComponents( newComponentColumn );
            }

            this.columns.add( columnWithComponents );
        }
    }

    public Column hasComponent( Column targetColumn ) {
        for ( Column column : columns ) {
            if ( targetColumn.hashCode() == column.hashCode() ) {
                return column;
            }
        }
        return null;
    }

    public void dragStart() {
        dndManager.beginRowMove( id );
    }

    public boolean canDrag() {
        return dndManager.canMoveRow();
    }

    public void dragEndMove() {
        dndManager.dragEndMove();
    }


    private boolean isColumnWithComponents( LayoutColumn layoutColumn ) {
        return layoutColumn.hasRows();
    }

    private ComponentColumn getComponentColumn( LayoutColumn column ) {
        LayoutComponent layoutComponent = column.getLayoutComponents().get( 0 );
        return createNewComponentColumn( layoutComponent,
                                         new Integer(
                                                 column.getSpan() ),
                                         false );
    }

    public void addColumns( ComponentColumn... _columns ) {
        for ( ComponentColumn column : _columns ) {
            column.setParentId( id );
            column.setDropCommand( dropCommand() );
            columns.add( column );
        }
    }

    public void withOneColumn( LayoutComponent layoutComponent, boolean newComponent ) {
        final ComponentColumn column = createComponentColumnInstance();


        column.init( id, COLUMN_DEFAULT_SIZE,
                     layoutComponent,
                     dropCommand(), removeColumnCommand(), newComponent );
        columns.add( column );
        setupColumnResizeActions();
    }

    protected ComponentColumn createComponentColumnInstance() {
        final ComponentColumn column = columnInstance.get();
        column.setId( idGenerator.createColumnID( id ) );
        return column;
    }

    public ParameterizedCommand<ColumnDrop> dropCommand() {
        return ( drop ) -> {
            if ( dropFromMoveComponent( drop ) ) {
                removeOldComponent( drop );
            }
            notifyDrop( drop.getComponent() );
            Row.this.columns = updateColumns( drop, Row.this.columns );
            updateView();
        };
    }

    private void notifyDrop( LayoutComponent component ) {
        componentDropEvent.fire( new ComponentDropEvent( component ) );
    }

    private void removeOldComponent( ColumnDrop drop ) {
        removeComponentCommand.execute( drop );
    }

    private boolean dropFromMoveComponent( ColumnDrop drop ) {
        return !drop.newComponent();
    }

    ParameterizedCommand<Column> removeColumnCommand() {
        return ( targetCol ) -> {
            removeColumn( targetCol );
        };
    }

    public void removeColumn( Column targetColumn ) {
        removeChildColumn( targetColumn );
        if ( rowIsEmpty() ) {
            removeRowCommand.execute( this );
        }
    }

    public void removeChildColumn( Column targetColumn ) {
        if ( isAChildColumn( targetColumn ) ) {
            removeChildComponentColumn( targetColumn );
        } else {
            lookupAndRemoveFromColumnsWithComponents( targetColumn );
        }
    }

    private void removeChildComponentColumn( Column targetColumn ) {
        if ( needToUpdateSizeOfMySiblings( targetColumn ) ) {
            updateSizeOfSibilinColumn( targetColumn );
        }
        columns.remove( targetColumn );
        destroy( targetColumn );
        notifyRemoval( targetColumn.getLayoutComponent() );
        updateView();
    }

    private void notifyRemoval( LayoutComponent layoutComponent ) {
        componentRemovedEvent.fire( new ComponentRemovedEvent( layoutComponent ) );
    }

    private boolean isAChildColumn( Column targetColumn ) {
        return columns.contains( targetColumn );
    }


    public boolean rowIsEmpty() {
        return columns.isEmpty();
    }

    private void lookupAndRemoveFromColumnsWithComponents( Column targetColumn ) {
        for ( Column column : columns ) {
            if ( column instanceof ColumnWithComponents ) {
                ColumnWithComponents c = ( ColumnWithComponents ) column;
                if ( c.hasComponent( targetColumn ) ) {
                    c.remove( targetColumn );
                    destroy( targetColumn );
                }
            }
        }
    }

    private boolean needToUpdateSizeOfMySiblings( Column targetColumn ) {
        return targetColumn.getSize() != 12;
    }

    private void updateSizeOfSibilinColumn( Column columnToRemove ) {
        final int removeIndex = getColumnIndex( columnToRemove );
        if ( isFirstColumn( removeIndex ) ) {
            if ( firstColumnHasRightSibling() ) {
                final Column sibling = columns.get( 1 );
                sibling.setSize( sibling.getSize() + columnToRemove.getSize() );
            }
        } else {
            final Column sibling = columns.get( removeIndex - 1 );
            sibling.setSize( sibling.getSize() + columnToRemove.getSize() );
        }
    }

    private boolean firstColumnHasRightSibling() {
        return columns.size() >= 2;
    }


    private int getColumnIndex( Column columnToRemove ) {
        return columns.indexOf( columnToRemove );
    }

    private boolean isFirstColumn( int columnIndex ) {
        return columnIndex == 0;
    }

    public void disableDrop() {
        this.dropEnable = false;
    }

    protected ColumnWithComponents createColumnWithComponentsInstance() {
        final ColumnWithComponents column = columnWithComponentsInstance.get();
        column.setId( idGenerator.createColumnID( id ) );
        return column;
    }

    public void drop( String dropData, RowDrop.Orientation orientation ) {
        if ( dndManager.isOnRowMove() ) {
            dndManager.endRowMove( id, orientation );
        } else if ( dndManager.isOnComponentMove() ) {
            dndManager.endComponentMove();
            dropOnRowCommand
                    .execute( new RowDrop( dndManager.getLayoutComponentMove(), id, orientation )
                                      .fromMove( dndManager.getRowId(),
                                                 dndManager.getDraggedColumn() ) );
        } else {
            dropOnRowCommand
                    .execute(
                            new RowDrop( layoutDragComponentHelper.getLayoutComponentFromDrop( dropData ), id,
                                         orientation ) );
        }
    }

    @PostConstruct
    public void post() {
        view.init( this );
    }

    @PreDestroy
    public void preDestroy() {
        for ( Column column : columns ) {
            destroy( column );
        }
    }


    private List<Column> updateColumns( ColumnDrop drop, List<Column> originalColumns ) {
        List<Column> columns = new ArrayList<>();
        for ( int i = 0; i < originalColumns.size(); i++ ) {
            final Column currentColumn = originalColumns.get( i );
            if ( dropIsOn( drop, currentColumn ) && columnCanBeSplitted( currentColumn ) ) {
                if ( isComponentColumn( currentColumn ) ) {
                    handleDropOnComponentColumn( drop, columns, i, currentColumn );
                } else {
                    handleDropOnColumnWithComponents( drop, columns, i
                            , currentColumn );

                }
            } else {
                columns.add( currentColumn );
            }
        }
        return columns;
    }

    private void handleDropOnColumnWithComponents( ColumnDrop drop, List<Column> columns, int columnIndex,
                                                   Column currentColumn ) {
        ColumnWithComponents column = ( ColumnWithComponents ) currentColumn;

        if ( drop.isASideDrop() ) {
            handleSideDrop( drop, columns, columnIndex, currentColumn );
        } else {
            if ( column.hasInnerRows() ) {
                Row innerRow = column.getRow();
                innerRow.columns = updateInnerColumns( drop, innerRow.getColumns() );
            }
            columns.add( column );
        }
    }

    private List<Column> updateInnerColumns( ColumnDrop drop, List<Column> originalColumns ) {
        List<Column> columns = new ArrayList<>();

        for ( int i = 0; i < originalColumns.size(); i++ ) {
            final Column currentColumn = originalColumns.get( i );
            if ( isComponentColumn( currentColumn ) ) {
                handleDropInnerColumn( drop, columns, i, currentColumn );
            }
        }
        return columns;
    }


    private void handleDropInnerColumn( ColumnDrop drop, List<Column> columns, int columnIndex, Column column ) {
        ComponentColumn currentColumn = ( ComponentColumn ) column;
        if ( dropIsOn( drop, currentColumn ) && columnCanBeSplitted( currentColumn ) ) {
            if ( drop.isASideDrop() ) {
                handleSideDrop( drop, columns, columnIndex, currentColumn );
            } else {
                handleInnerDrop( drop, columns, currentColumn );
            }
        } else {
            columns.add( currentColumn );
        }
    }

    private void handleInnerDrop( ColumnDrop drop, List<Column> columns, ComponentColumn currentColumn ) {
        final ComponentColumn newColumn = createNewInnerColumn( drop, currentColumn );
        currentColumn.setInnerColumn();
        addColumnsInTheRightPosition( drop, columns, currentColumn, newColumn );
    }

    private void addColumnsInTheRightPosition( ColumnDrop drop, List<Column> columns, ComponentColumn currentColumn,
                                               ComponentColumn newColumn ) {
        if ( drop.isADownDrop() ) {
            columns.add( currentColumn );
            columns.add( newColumn );
        } else {
            columns.add( newColumn );
            columns.add( currentColumn );
        }
    }

    private ComponentColumn createNewInnerColumn( ColumnDrop drop, ComponentColumn currentColumn ) {
        final ComponentColumn newColumn = createComponentColumnInstance();


        newColumn.init( currentColumn.getParentId(), COLUMN_DEFAULT_SIZE,
                        drop.getComponent(),
                        dropCommand(), removeColumnCommand(), drop.newComponent() );
        newColumn.setInnerColumn();
        return newColumn;
    }

    private void handleInnerComponentDrop( ColumnDrop drop, int columnIndex, List<Column> columns,
                                           ComponentColumn currentColumn ) {
        if ( parentColumnWithComponents == null ) {
            Integer size = currentColumn.getSize();
            final ColumnWithComponents columnWithComponents = createColumnWithComponentsInstance();
            columnWithComponents
                    .init( id, size, dropCommand(), removeComponentCommand,
                           removeColumnCommand() );

            final ComponentColumn newColumn = createComponentColumn(
                    drop.getComponent(), drop.newComponent() );
            newColumn.setInnerColumn();
            currentColumn = updateCurrentColumn( currentColumn );

            if ( drop.isADownDrop() ) {
                columnWithComponents.withComponents( currentColumn, newColumn );
            } else {
                columnWithComponents.withComponents( newColumn, currentColumn );
            }

            columns.add( columnWithComponents );
        } else {
            final ComponentColumn newColumn = createComponentColumn(
                    drop.getComponent(), drop.newComponent() );
            newColumn.setInnerColumn();
            addColumnsInTheRightPosition( drop, columns, currentColumn, newColumn );

        }

    }

    private ComponentColumn updateCurrentColumn( ComponentColumn currentColumn ) {
        currentColumn.setSize( 12 );
        currentColumn.recalculateSize();
        currentColumn.setInnerColumn();
        return currentColumn;
    }

    private ComponentColumn createComponentColumn( LayoutComponent layoutComponent,
                                                   boolean newComponent ) {
        return createNewComponentColumn( layoutComponent, 12, newComponent );
    }

    private ComponentColumn createNewComponentColumn( LayoutComponent layoutComponent, Integer columnSize,
                                                      boolean newComponent ) {
        final ComponentColumn newColumn = createComponentColumnInstance();

        newColumn.init( id, columnSize,
                        layoutComponent, dropCommand()
                , removeColumnCommand(), newComponent );
        return newColumn;
    }

    private void handleDropOnComponentColumn( ColumnDrop drop, List<Column> columns, int columnIndex, Column column ) {
        ComponentColumn componentColumn = ( ComponentColumn ) column;
        if ( drop.isASideDrop() ) {
            handleSideDrop( drop, columns, columnIndex, componentColumn );
        } else {
            handleInnerComponentDrop( drop, columnIndex, columns, componentColumn );
        }
    }

    private boolean columnCanBeSplitted( Column column ) {
        return column.getSize() != 1;
    }


    private boolean isComponentColumn( Column currentColumn ) {
        return currentColumn instanceof ComponentColumn;
    }

    private void handleSideDrop( ColumnDrop drop, List<Column> columns, int columnIndex,
                                 Column currentColumn ) {

        if ( drop.isALeftDrop() ) {
            final ComponentColumn newColumn = createNewComponentColumn( drop.getComponent(),
                                                                        currentColumn.getSize() / 2,
                                                                        drop.newComponent() );
            setupColumnSize( currentColumn );

            columns.add( newColumn );
            columns.add( currentColumn );
        } else {
            final ComponentColumn newColumn = createNewComponentColumn( drop.getComponent(),
                                                                        currentColumn.getSize() / 2,
                                                                        drop.newComponent() );
            setupColumnSize( currentColumn );

            columns.add( currentColumn );
            columns.add( newColumn );
        }
    }

    private Integer setupColumnSize( Column column ) {
        Integer originalSize = column.getSize();
        Integer newColumnSize = originalSize / 2;
        if ( originalSize % 2 == 0 ) {
            column.setSize( newColumnSize );
        } else {
            column.setSize( newColumnSize + 1 );
        }
        return newColumnSize;
    }


    private boolean dropIsOn( ColumnDrop drop, Column column ) {
        return drop.getEndId() == column.getId();
    }


    public void resizeColumns( @Observes ColumnResizeEvent resize ) {
        if ( resizeEventIsinThisRow( resize ) ) {

            Column resizedColumn = getColumn( resize );

            if ( resizedColumn != null ) {
                Column affectedColumn = null;
                if ( resize.isLeft() ) {
                    affectedColumn = lookUpForLeftNeighbor( resizedColumn );
                } else {
                    affectedColumn = lookUpForRightNeighbor( resizedColumn );

                }
                if ( affectedColumn != null ) {
                    resizedColumn.incrementSize();
                    affectedColumn.reduzeSize();
                }
            }
            updateView();
        }
    }

    private Column lookUpForLeftNeighbor( Column resizedColumn ) {
        return columns
                .get( getColumnIndex( resizedColumn ) - 1 );
    }

    private Column lookUpForRightNeighbor( Column resizedColumn ) {
        return columns
                .get( getColumnIndex( resizedColumn ) + 1 );
    }

    private boolean resizeEventIsinThisRow( @Observes ColumnResizeEvent resize ) {
        return resize.getRowID() == id;
    }


    private Column getColumn( ColumnResizeEvent resize ) {
        for ( Column column : columns ) {
            if ( resize.getColumnID() == column.getId() ) {
                return column;
            }
        }
        return null;
    }

    public void updateView() {
        view.clear();
        setupColumnResizeActions();
        for ( Column column : columns ) {
            view.addColumn( column.getView() );
        }
    }

    private void setupColumnResizeActions() {
        for ( int i = 0; i < columns.size(); i++ ) {
            Column column = columns.get( i );
            setupColumnResizeActions( columns, column, i );
        }
    }

    private void setupColumnResizeActions( List<Column> columns, Column currentColumn, int index ) {
        if ( firstColumn( index ) ) {
            boolean canResizeRight = canResizeRight( index, columns );
            currentColumn.setupResize( false, canResizeRight );
        } else {
            currentColumn.setupResize( canResizeLeft( index, columns ), canResizeRight( index, columns ) );
        }
    }

    private boolean canResizeLeft( int index, List<Column> columns ) {
        Column rightSibling = columns.get( index - 1 );
        return rightSibling.getSize() > 1;
    }

    private boolean canResizeRight( int index, List<Column> columns ) {
        if ( hasRightSibling( index, columns ) ) {
            Column rightSibling = columns.get( index + 1 );
            return rightSibling.getSize() > 1;
        }
        return false;
    }

    private boolean hasRightSibling( int index, List<Column> columns ) {
        return columns.size() > ( index + 1 );
    }

    private boolean firstColumn( int index ) {
        return index == 0;
    }

    public UberElement<Row> getView() {
        updateView();
        return view;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public boolean isDropEnable() {
        return dropEnable;
    }

    protected void destroy( Object o ) {
        BeanHelper.destroy( o );
    }

    public void calculateSizeChilds() {
        for ( Column column : columns ) {
            column.calculateSize();
        }
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }
}
