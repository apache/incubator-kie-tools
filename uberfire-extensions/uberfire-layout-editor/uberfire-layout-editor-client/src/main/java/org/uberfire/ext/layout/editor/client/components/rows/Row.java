package org.uberfire.ext.layout.editor.client.components.rows;

import com.google.gwt.event.dom.client.DropEvent;
import org.uberfire.client.mvp.UberView;
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

    public interface View extends UberView<Row> {

        void addColumn( UberView<ComponentColumn> view );

        void clear();

    }

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
    }

    private void extractColumns( LayoutRow layoutRow ) {
        int currentColumnIndex = 0;
        for ( LayoutColumn layoutColumn : layoutRow.getLayoutColumns() ) {
            if ( isColumnWithComponents( layoutColumn ) ) {
                extractColumnWithComponents( currentColumnIndex, layoutColumn );

            } else {
                extractComponentColumn( currentColumnIndex, layoutColumn );
            }
            currentColumnIndex = currentColumnIndex + 1;
        }
    }


    private void extractComponentColumn( int currentColumnIndex, LayoutColumn layoutColumn ) {
        ComponentColumn newComponentColumn = getComponentColumn(
                currentColumnIndex, layoutColumn );
        this.columns.add( newComponentColumn );
    }

    private void extractColumnWithComponents( int currentColumnIndex, LayoutColumn layoutColumn ) {
        for ( LayoutRow row : layoutColumn.getRows() ) {
            Integer size = new Integer( layoutColumn.getSpan() );
            final ColumnWithComponents columnWithComponents = createColumnWithComponentsInstance();

            columnWithComponents
                    .init( hashCode(), getColumnPosition( currentColumnIndex ), size,
                           dropCommand(), removeComponentCommand,
                           removeColumnCommand() );

            for ( LayoutColumn column : row.getLayoutColumns() ) {
                ComponentColumn newComponentColumn = getComponentColumn( 0, column );
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
        dndManager.beginRowMove( hashCode() );
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

    private ComponentColumn getComponentColumn( int currentColumnIndex, LayoutColumn column ) {
        LayoutComponent layoutComponent = column.getLayoutComponents().get( 0 );
        return createNewComponentColumn( layoutComponent,
                                         currentColumnIndex,
                                         new Integer(
                                                 column.getSpan() ),
                                         false );
    }

    public void addColumns( ComponentColumn... _columns ) {
        for ( ComponentColumn column : _columns ) {
            column.setParentHashCode( hashCode() );
            column.setDropCommand( dropCommand() );
            columns.add( column );
        }
    }

    public void withOneColumn( LayoutComponent layoutComponent, boolean newComponent ) {
        final ComponentColumn column = createComponentColumnInstance();


        column.init( hashCode(), Column.Position.FIRST_COLUMN, COLUMN_DEFAULT_SIZE,
                     layoutComponent,
                     dropCommand(), removeColumnCommand(), newComponent );
        columns.add( column );
    }

    protected ComponentColumn createComponentColumnInstance() {
        final ComponentColumn column = columnInstance.get();
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

    private void removeChildColumn( Column targetColumn ) {
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


    private boolean rowIsEmpty() {
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
            if ( hasRightSibling() ) {
                final Column sibling = columns.get( 1 );
                sibling.setSize( sibling.getSize() + columnToRemove.getSize() );
            }
        } else {
            final Column sibling = columns.get( removeIndex - 1 );
            sibling.setSize( sibling.getSize() + columnToRemove.getSize() );
        }
    }

    private boolean hasRightSibling() {
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
        return column;
    }

    public void drop( DropEvent dropEvent, RowDrop.Orientation orientation ) {
        if ( dndManager.isOnRowMove() ) {
            dndManager.endRowMove( hashCode(), orientation );
        } else if ( dndManager.isOnComponentMove() ) {
            dndManager.endComponentMove();
            dropOnRowCommand
                    .execute( new RowDrop( dndManager.getLayoutComponentMove(), hashCode(), orientation )
                                      .fromMove( dndManager.getRowHashCode(),
                                                 dndManager.getDraggedColumn() ) );
        } else {
            dropOnRowCommand
                    .execute(
                            new RowDrop( layoutDragComponentHelper.getLayoutComponentFromDrop( dropEvent ), hashCode(),
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


        newColumn.init( currentColumn.getParentHashCode(), getColumnPosition( 0 ), COLUMN_DEFAULT_SIZE,
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
                    .init( hashCode(), getColumnPosition( columnIndex ), size, dropCommand(), removeComponentCommand,
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
        currentColumn.setColumnPosition( getColumnPosition( 0 ) );
        currentColumn.setInnerColumn();
        return currentColumn;
    }

    private ComponentColumn createComponentColumn( LayoutComponent layoutComponent,
                                                   boolean newComponent ) {
        return createNewComponentColumn( layoutComponent, 0, 12, newComponent );
    }

    private ComponentColumn createNewComponentColumn( LayoutComponent layoutComponent,
                                                      int columnIndex, Integer columnSize, boolean newComponent ) {
        final ComponentColumn newColumn = createComponentColumnInstance();

        newColumn.init( hashCode(), getColumnPosition( columnIndex ), columnSize,
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
            final ComponentColumn newColumn = createNewComponentColumn( drop.getComponent(), columnIndex,
                                                                        currentColumn.getSize() / 2,
                                                                        drop.newComponent() );
            currentColumn = updateCurrentColumn( currentColumn, ( columnIndex + 1 ) );

            columns.add( newColumn );
            columns.add( currentColumn );

        } else {
            final ComponentColumn newColumn = createNewComponentColumn( drop.getComponent(),
                                                                        columnIndex + 1,
                                                                        currentColumn.getSize() / 2,
                                                                        drop.newComponent() );
            currentColumn = updateCurrentColumn( currentColumn, columnIndex );

            columns.add( currentColumn );
            columns.add( newColumn );
        }
    }

    private Column updateCurrentColumn( Column currentColumn, int columnIndex ) {
        currentColumn.setColumnPosition( getColumnPosition( columnIndex + 1 ) );
        setupColumnSize( currentColumn );
        return currentColumn;
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
        return drop.getEndHash() == column.hashCode();
    }


    private Column.Position getColumnPosition( int i ) {
        Column.Position Position;
        if ( i == 0 ) {
            Position = Column.Position.FIRST_COLUMN;
        } else {
            Position = Column.Position.MIDDLE;
        }
        return Position;
    }

    public void resizeColumns( @Observes ColumnResizeEvent resize ) {
        if ( resizeEventIsinThisRow( resize ) ) {

            Column resizedColumn = getColumn( resize );

            if ( resizedColumn != null ) {
                Column affectedColumn = lookUpForLeftNeighbor( resizedColumn );
                if ( resize.isLeft() ) {
                    resizedColumn.incrementSize();
                    affectedColumn.reduzeSize();
                } else {
                    affectedColumn.incrementSize();
                    resizedColumn.reduzeSize();
                }
            }
            updateView();
        }
    }

    private Column lookUpForLeftNeighbor( Column resizedColumn ) {
        return columns
                .get( getColumnIndex( resizedColumn ) - 1 );
    }

    private boolean resizeEventIsinThisRow( @Observes ColumnResizeEvent resize ) {
        return resize.getRowHashCode() == hashCode();
    }


    private Column getColumn( ColumnResizeEvent resize ) {
        for ( Column column : columns ) {
            if ( resize.getColumnHashCode() == column.hashCode() ) {
                return column;
            }
        }
        return null;
    }

    public void updateView() {
        view.clear();
        for ( Column column : columns ) {
            view.addColumn( column.getView() );
        }
    }

    public UberView<Row> getView() {
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
}
