package org.uberfire.ext.layout.editor.client.components.columns;

import com.google.gwt.event.dom.client.DropEvent;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.DnDManager;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class ComponentColumn implements Column {

    public interface View extends UberView<ComponentColumn> {

        void setCursor();

        void setSize( String size );

        void calculateSize();

        void clearContent();

        void setContent();

        void showConfigComponentModal( Command configurationFinish, Command configurationCanceled );

        boolean hasModalConfiguration();

        void setup( LayoutComponent layoutComponent );
    }

    private DnDManager dndManager;
    private final View view;
    private int parentHashCode;
    private Position columnPosition;
    private Integer size;
    private ParameterizedCommand<ColumnDrop> dropCommand;
    private boolean innerColumn = false;
    private LayoutComponent layoutComponent;
    private boolean componentReady;
    private ParameterizedCommand<Column> removeCommand;
    private LayoutDragComponentHelper layoutDragComponentHelper;

    @Inject
    public ComponentColumn( final View view, DnDManager dndManager, LayoutDragComponentHelper layoutDragComponentHelper ) {
        this.view = view;
        this.dndManager = dndManager;
        this.layoutDragComponentHelper = layoutDragComponentHelper;
    }

    @PostConstruct
    public void post() {
        view.init( this );
    }

    public void init( int parentHashCode, Position columnPosition, Integer size, LayoutComponent layoutComponent,
                      ParameterizedCommand<ColumnDrop> dropCommand,
                      ParameterizedCommand<Column> removeCommand,
                      boolean newComponent ) {
        this.layoutComponent = layoutComponent;
        view.setup(layoutComponent);
        this.parentHashCode = parentHashCode;
        this.columnPosition = columnPosition;
        this.size = size;
        this.dropCommand = dropCommand;
        this.removeCommand = removeCommand;
        view.setSize( size.toString() );
        view.setCursor();
        if ( newComponent && hasConfiguration() ) {
            configComponent( newComponent );
        } else {
            componentReady = true;
        }
    }

    protected boolean hasConfiguration() {
        return view.hasModalConfiguration();
    }

    public void setParentHashCode( int parentHashCode ) {
        this.parentHashCode = parentHashCode;
    }

    public void setDropCommand(
            ParameterizedCommand<ColumnDrop> dropCommand ) {
        this.dropCommand = dropCommand;
    }

    void configComponent( boolean newComponent ) {

        if ( hasModalConfiguration( newComponent ) ) {
            view.showConfigComponentModal( this::configurationFinish,
                                           this::configurationCanceled );
        } else {
            configurationFinish();
        }
    }

    private boolean hasModalConfiguration( boolean newComponent ) {
        return newComponent && view.hasModalConfiguration();
    }

    private void configurationFinish() {
        this.componentReady = true;
        updateView();
    }

    private void configurationCanceled() {
        if ( !componentReady ) {
            remove();
        }
    }

    public void remove() {
        removeCommand.execute( this );
    }

    public void edit() {
        configComponent( true );
    }

    public boolean shouldPreviewDrop() {
        return !dndManager.isOnRowMove();
    }

    public void dragStartComponent() {
        dndManager.dragComponent( layoutComponent, parentHashCode, this );
    }

    public void dragEndComponent() {
        dndManager.dragEndComponent();
    }

    @Override
    public LayoutComponent getLayoutComponent() {
        return layoutComponent;
    }

    @Override
    public boolean hasInnerRows() {
        return false;
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

    public void onResize( int xPosition ) {
        if ( canResize() ) {
            dndManager.beginColumnResize( hashCode(), xPosition );
        }
    }


    boolean canResize() {
        return columnPosition == Position.MIDDLE;
    }

    public void endColumnResize( int xPosition ) {
        dndManager.endColumnResize( parentHashCode, xPosition );
    }


    public void recalculateSize() {
        view.calculateSize();
    }

    @Override
    public int getParentHashCode() {
        return parentHashCode;
    }

    @Override
    public Integer getSize() {
        return size;
    }

    public void onDrop( ColumnDrop.Orientation orientation, DropEvent dropEvent ) {
        if ( dndManager.isOnComponentMove() ) {
            moveDrop( orientation );
        } else {
            newComponentDrop( orientation, dropEvent );
        }
    }

    private void newComponentDrop( ColumnDrop.Orientation orientation, DropEvent dropEvent ) {
        dropCommand.execute(
                new ColumnDrop( layoutDragComponentHelper.getLayoutComponentFromDrop( dropEvent ), hashCode(), orientation ) );
    }

    private void moveDrop( ColumnDrop.Orientation orientation ) {
        dndManager.endComponentMove();
        dropCommand.execute( new ColumnDrop( dndManager.getLayoutComponentMove(), hashCode(), orientation )
                                     .fromMove(
                                             dndManager.getDraggedColumn() ) );
    }


    public void setColumnPosition( Position columnPosition ) {
        this.columnPosition = columnPosition;
    }

    protected boolean isInnerColumn() {
        return innerColumn;
    }

    public void setInnerColumn() {
        this.innerColumn = true;
    }

    public void updateView() {
        if ( componentReady ) {
            view.clearContent();
            view.setContent();
            view.setCursor();
            view.calculateSize();
        }
    }

    public UberView<ComponentColumn> getView() {
        updateView();
        return view;
    }

    public LayoutDragComponentHelper getLayoutDragComponentHelper() {
        return layoutDragComponentHelper;
    }
}
