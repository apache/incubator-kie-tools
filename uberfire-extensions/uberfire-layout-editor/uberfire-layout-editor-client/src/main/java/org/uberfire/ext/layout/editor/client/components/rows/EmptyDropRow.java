package org.uberfire.ext.layout.editor.client.components.rows;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.infra.DndDataJSONConverter;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;
import org.uberfire.mvp.ParameterizedCommand;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class EmptyDropRow {


    private String id;

    public interface View extends UberElement<EmptyDropRow> {

        void setupText( String titleText, String subTitleText );
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

    public void init( ParameterizedCommand<RowDrop> dropCommand, String titleText, String subTitleText ) {
        this.dropCommand = dropCommand;
        view.setupText( titleText, subTitleText );
    }

    public void drop( String dropData ) {
        LayoutDragComponent component = extractComponent( dropData );
        if ( thereIsAComponent( component ) ) {
            dropCommand.execute( new RowDrop( layoutDragComponentHelper.getLayoutComponent( component ), id,
                                              RowDrop.Orientation.AFTER ) );
        }
    }

    private LayoutDragComponent extractComponent( String dropData ) {
        return converter
                .readJSONDragComponent( dropData );
    }


    private boolean thereIsAComponent( LayoutDragComponent component ) {
        return component != null;
    }

    public UberElement<EmptyDropRow> getView() {
        return view;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}