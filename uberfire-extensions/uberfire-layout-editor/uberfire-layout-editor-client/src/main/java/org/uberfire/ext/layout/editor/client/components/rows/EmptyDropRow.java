package org.uberfire.ext.layout.editor.client.components.rows;

import com.google.gwt.event.dom.client.DropEvent;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.infra.DndDataJSONConverter;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;
import org.uberfire.mvp.ParameterizedCommand;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class EmptyDropRow {

    public interface View extends UberView<EmptyDropRow> {

    }

    private final View view;

    private DndDataJSONConverter converter = new DndDataJSONConverter();


    private ParameterizedCommand<RowDrop> dropCommand;
    private LayoutDragComponentHelper layoutDragComponentHelper;

    @Inject
    public EmptyDropRow( final View view, LayoutDragComponentHelper layoutDragComponentHelper ) {
        this.view = view;
        this.layoutDragComponentHelper = layoutDragComponentHelper;
    }

    @PostConstruct
    public void post() {
        view.init( this );
    }

    public void init( ParameterizedCommand<RowDrop> dropCommand ) {
        this.dropCommand = dropCommand;
    }

    public void drop( DropEvent dropEvent ) {
        LayoutDragComponent component = extractComponent( dropEvent );
        if ( thereIsAComponent( component ) ) {
            dropCommand.execute( new RowDrop( layoutDragComponentHelper.getLayoutComponent( component ), hashCode(),
                                              RowDrop.Orientation.AFTER ) );
        }
    }

    private LayoutDragComponent extractComponent( DropEvent dropEvent ) {
        return converter
                .readJSONDragComponent( dropEvent.getData( LayoutDragComponent.FORMAT ) );
    }


    private boolean thereIsAComponent( LayoutDragComponent component ) {
        return component != null;
    }


    public UberView<EmptyDropRow> getView() {
        return view;
    }

}