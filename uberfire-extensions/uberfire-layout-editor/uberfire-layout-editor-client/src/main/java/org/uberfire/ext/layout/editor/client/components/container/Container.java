package org.uberfire.ext.layout.editor.client.components.container;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.ComponentDropEvent;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRow;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent;
import org.uberfire.ext.layout.editor.client.components.rows.RowDrop;
import org.uberfire.ext.layout.editor.client.infra.BeanHelper;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.LayoutTemplateAdapter;
import org.uberfire.ext.layout.editor.client.infra.UniqueIDGenerator;
import org.uberfire.mvp.ParameterizedCommand;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Dependent
public class Container {

    private LayoutTemplate layoutTemplate;

    public interface View extends UberElement<Container> {

        void addRow( UberElement<Row> view );

        void clear();

        void addEmptyRow( UberElement<EmptyDropRow> emptyDropRow );

    }

    private String id;
    private UniqueIDGenerator idGenerator = new UniqueIDGenerator();
    private final Instance<Row> rowInstance;
    private final Instance<EmptyDropRow> emptyDropRowInstance;
    private final View view;
    private List<Row> rows = new ArrayList<>();
    private EmptyDropRow emptyDropRow;
    private String layoutName;
    private String emptyTitleText;
    private String emptySubTitleText;
    private Map<String, String> properties = new HashMap<>();
    private Event<ComponentDropEvent> componentDropEvent;

    @Inject
    public Container( final View view, Instance<Row> rowInstance, Instance<EmptyDropRow> emptyDropRowInstance,
                      Event<ComponentDropEvent> componentDropEvent ) {
        this.rowInstance = rowInstance;
        this.emptyDropRowInstance = emptyDropRowInstance;
        this.view = view;
        this.componentDropEvent = componentDropEvent;
        this.id = idGenerator.createContainerID();
    }

    @PostConstruct
    public void setup() {
        view.init( this );
        init();
    }

    @PreDestroy
    public void preDestroy() {
        for ( Row row : rows ) {
            destroy( row );
        }
        destroy( emptyDropRow );
    }


    private void init() {
        view.clear();
        for ( Row row : rows ) {
            destroy( row );
        }
        rows = new ArrayList<>();
    }

    private void createEmptyDropRow() {
        emptyDropRow = createEmptyRow();
        view.addEmptyRow( emptyDropRow.getView() );
    }


    public void loadEmptyLayout( String layoutName, String emptyTitleText, String emptySubTitleText ) {
        this.layoutName = layoutName;
        this.emptyTitleText = emptyTitleText;
        this.emptySubTitleText = emptySubTitleText;
        createEmptyDropRow();
    }

    public void load( LayoutTemplate layoutTemplate, String emptyTitleText, String emptySubTitleText ) {
        this.layoutTemplate = layoutTemplate;
        this.emptyTitleText = emptyTitleText;
        this.emptySubTitleText = emptySubTitleText;
        if ( !layoutTemplate.isEmpty() ) {
            this.layoutName = layoutTemplate.getName();
            this.properties = layoutTemplate.getLayoutProperties();
            for ( LayoutRow layoutRow : layoutTemplate.getRows() ) {
                rows.add( load( layoutRow ) );
            }
            updateView();
        } else {
            createEmptyDropRow();
        }
    }

    private EmptyDropRow createEmptyRow() {
        emptyDropRow = createInstanceEmptyDropRow();
        emptyDropRow.init( createEmptyDropCommand(), emptyTitleText, emptySubTitleText );
        return emptyDropRow;
    }

    protected EmptyDropRow createInstanceEmptyDropRow() {
        EmptyDropRow emptyDropRow = emptyDropRowInstance.get();
        emptyDropRow.setId( idGenerator.createRowID( id ) );
        return emptyDropRow;
    }

    public ParameterizedCommand<RowDrop> createEmptyDropCommand() {
        return ( drop ) -> {
            destroy( emptyDropRow );
            notifyDrop( drop.getComponent() );
            rows.add( createRow( drop ) );
            updateView();
        };
    }

    private void notifyDrop( LayoutComponent component ) {
        componentDropEvent.fire( new ComponentDropEvent( component ) );
    }

    private Row createRow( RowDrop drop ) {
        final Row row = createInstanceRow();
        row.init( createRowDropCommand(), createRemoveRowCommand(), createRemoveComponentCommand() );
        row.withOneColumn( drop.getComponent(), drop.newComponent() );
        view.addRow( row.getView() );
        return row;
    }

    private ParameterizedCommand<Row> createRemoveRowCommand() {
        return ( row ) -> {
            this.rows.remove( row );
            destroy( row );
            if ( layoutIsEmpty() ) {
                init();
                createEmptyDropRow();
            } else {
                updateView();
            }
        };
    }

    private ParameterizedCommand<ColumnDrop> createRemoveComponentCommand() {
        return drop -> removeOldComponent( drop.getOldColumn() );
    }

    private boolean layoutIsEmpty() {
        return rows.isEmpty();
    }

    public ParameterizedCommand<RowDrop> createRowDropCommand() {
        return ( dropRow ) -> {
            List<Row> updatedRows = new ArrayList<>();
            for ( Row row : rows ) {
                handleDrop( dropRow, updatedRows, row );
            }
            rows = updatedRows;
            getView();
        };
    }

    private void handleDrop( RowDrop dropRow, List<Row> updatedRows, Row row ) {
        if ( dropIsInthisRow( row, dropRow ) ) {
            if ( dropRow.newComponent() ) {
                addNewRow( row, dropRow, updatedRows );
            } else {
                handleMoveComponent( dropRow, updatedRows, row );
            }
        } else {
            updatedRows.add( row );
        }
    }

    private void handleMoveComponent( RowDrop dropRow, List<Row> updatedRows, Row row ) {
        removeOldComponent( dropRow.getOldColumn() );
        addNewRow( row, dropRow, updatedRows );
    }

    private void removeOldComponent( Column column ) {
        for ( Row row : rows ) {
            row.removeChildColumn( column );
        }
    }

    private void addNewRow( Row row, RowDrop dropRow, List<Row> newRows ) {
        if ( newRowIsBeforeThisRow( dropRow ) ) {
            newRows.add( createRow( dropRow ) );
            if ( !row.rowIsEmpty() ) {
                newRows.add( row );
            }
        } else {
            if ( !row.rowIsEmpty() ) {
                newRows.add( row );
            }
            newRows.add( createRow( dropRow ) );
        }
    }

    private boolean newRowIsBeforeThisRow( RowDrop dropRow ) {
        return dropRow.getOrientation() == RowDrop.Orientation.BEFORE;
    }

    private boolean dropIsInthisRow( Row row, RowDrop dropRow ) {
        return dropRow.getRowId() == row.getId();
    }

    private void clearView() {
        view.clear();
    }


    protected void swapRows( @Observes RowDnDEvent rowDndEvent ) {
        List<Row> newRows = new ArrayList<>();
        Row beginRow = lookForBeginningRow( rowDndEvent );

        if ( beginRow != null ) {
            for ( Row row : rows ) {
                if ( row.getId() == rowDndEvent.getRowIdEnd() ) {
                    if ( rowDndEvent.getOrientation() == RowDrop.Orientation.AFTER ) {
                        newRows.add( row );
                        newRows.add( beginRow );
                    } else {
                        newRows.add( beginRow );
                        newRows.add( row );
                    }
                } else {
                    if ( row.getId() != beginRow.getId() ) {
                        newRows.add( row );
                    }
                }
            }
            this.rows = newRows;
        }

        updateView();
    }

    private Row lookForBeginningRow( @Observes RowDnDEvent rowDndEvent ) {
        Row beginRow = null;

        for ( Row row : rows ) {
            if ( row.getId() == rowDndEvent.getRowIdBegin() ) {
                beginRow = row;
            }
        }
        return beginRow;
    }

    public String getLayoutName() {
        return layoutName;
    }


    private Row load( LayoutRow layoutRow ) {
        final Row row = createInstanceRow();
        row.load( createRowDropCommand(), layoutRow, createRemoveRowCommand(), createRemoveComponentCommand() );
        return row;
    }

    protected Row createInstanceRow() {
        Row row = rowInstance.get();
        row.setId( idGenerator.createRowID( id ) );
        return row;
    }

    public void addProperty( String key, String value ) {
        properties.put( key, value );
    }

    public String getProperty( String key ) {
        return properties.get( key );
    }

    public Map<String, String> getProperties() {
        return properties;
    }


    public LayoutTemplate toLayoutTemplate() {
        LayoutTemplate convert = LayoutTemplateAdapter.convert( this );
        return convert;
    }

    public List<Row> getRows() {
        return rows;
    }


    void updateView() {
        if ( !rows.isEmpty() ) {
            clearView();
            for ( Row row : rows ) {
                view.addRow( row.getView() );
            }
        }
    }

    public UberElement<Container> getView() {
        updateView();
        return view;
    }

    EmptyDropRow getEmptyDropRow() {
        return emptyDropRow;
    }

    protected void destroy( Object o ) {
        BeanHelper.destroy( o );
    }

}
