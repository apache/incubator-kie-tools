package org.uberfire.ext.layout.editor.client.components.columns;

import com.google.gwt.event.dom.client.DropEvent;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.components.rows.RowDrop;
import org.uberfire.ext.layout.editor.client.infra.BeanHelper;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.DnDManager;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;
import org.uberfire.mvp.ParameterizedCommand;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Dependent
public class ColumnWithComponents implements Column {

    public interface View extends UberView<ColumnWithComponents> {

        void setSize( String size );

        void addRow( UberView<Row> view );

        void setCursor();

        void calculateSize();

        void clear();
    }

    private final View view;
    private Integer size;
    private int parentHashCode;
    private ParameterizedCommand<ColumnDrop> dropCommand;
    private ParameterizedCommand<ColumnDrop> removeComponentCommand;
    private ParameterizedCommand<Column> removeColumnCommand;
    private Row row;
    private Instance<Row> rowInstance;
    private DnDManager dndManager;
    private Position columnPosition;
    private LayoutDragComponentHelper layoutDragComponentHelper;

    @Inject
    public ColumnWithComponents( final View view, Instance<Row> rowInstance, DnDManager dndManager,
                                 LayoutDragComponentHelper layoutDragComponentHelper ) {
        this.view = view;
        this.rowInstance = rowInstance;
        this.dndManager = dndManager;
        this.layoutDragComponentHelper = layoutDragComponentHelper;
    }

    @PostConstruct
    public void post() {
        view.init( this );
    }


    @PreDestroy
    public void preDestroy() {
        destroy( row );
    }


    public void init( Integer parentHashCode, Position columnPosition,
                      Integer size,
                      ParameterizedCommand<ColumnDrop> dropCommand,
                      ParameterizedCommand<ColumnDrop> removeComponentCommand,
                      ParameterizedCommand<Column> removeCommand ) {
        this.columnPosition = columnPosition;
        this.size = size;
        this.parentHashCode = parentHashCode;
        this.dropCommand = dropCommand;
        this.removeComponentCommand = removeComponentCommand;
        this.removeColumnCommand = removeCommand;
        view.setSize( size.toString() );
        row = createInstanceRow();
        row.disableDrop();
        row.init( createDropCommand(), createRowRemoveCommand(), createComponentRemoveCommand(), this );
    }


    public void onDrop( ColumnDrop.Orientation orientation, DropEvent dropEvent ) {
        if ( dndManager.isOnComponentMove() ) {
            dndManager.endComponentMove();
            dropCommand.execute( new ColumnDrop( dndManager.getLayoutComponentMove(), hashCode(), orientation )
                                         .fromMove( dndManager.getDraggedColumn() ) );
        } else {
            dropCommand.execute(
                    new ColumnDrop( layoutDragComponentHelper.getLayoutComponentFromDrop( dropEvent ), hashCode(),
                                    orientation ) );
        }
    }


    public void onMouseDown( int xPosition ) {
        if ( canResize() ) {
            dndManager.beginColumnResize( hashCode(), xPosition );
        } else if ( canMove() ) {
            dndManager.beginRowMove( parentHashCode );
        }
    }

    public boolean hasComponent( Column targetColumn ) {
        return row.hasComponent( targetColumn ) != null;
    }

    public void remove( Column targetColumn ) {
        row.removeColumn( targetColumn );
    }

    boolean canResize() {
        return columnPosition == Position.MIDDLE;
    }

    boolean canMove() {
        return columnPosition == Position.FIRST_COLUMN;
    }

    public void onMouseUp( int xPosition ) {
        dndManager.endColumnResize( parentHashCode, xPosition );
    }

    protected Row createInstanceRow() {
        return rowInstance.get();
    }

    private ParameterizedCommand<ColumnDrop> createComponentRemoveCommand() {
        return drop -> removeComponentCommand.execute( drop );
    }

    private ParameterizedCommand<Row> createRowRemoveCommand() {
        return row -> removeColumnCommand.execute( this );
    }


    public void withComponents( ComponentColumn... _columns ) {

        row.addColumns( _columns );
    }

    ParameterizedCommand<RowDrop> createDropCommand() {
        return rowDrop -> {
        };
    }


    @Override
    public void setColumnPosition( Position columnPosition ) {
        this.columnPosition = columnPosition;
    }

    @Override
    public UberView<ColumnWithComponents> getView() {
        view.clear();
        if ( hasInnerRows() ) {
            view.addRow( row.getView() );
        }
        view.setCursor();
        view.calculateSize();
        return view;
    }

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public void reduzeSize() {
        final int newSize = this.size - 1;
        setSize( newSize );
    }

    @Override
    public void incrementSize() {
        final int newSize = this.size + 1;
        setSize( newSize );
    }

    @Override
    public void setSize( Integer size ) {
        this.size = size;
        view.setSize( size.toString() );
    }


    @Override
    public LayoutComponent getLayoutComponent() {
        return null;
    }

    @Override
    public boolean hasInnerRows() {
        return row != null;
    }

    public Row getRow() {
        return row;
    }

    @Override
    public int getParentHashCode() {
        return parentHashCode;
    }

    protected void destroy( Object o ) {
        BeanHelper.destroy( o );
    }
}
