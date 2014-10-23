package org.kie.uberfire.perspective.editor.client.panels.dnd;

import com.github.gwtbootstrap.client.ui.InputAddOn;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlternateSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.perspective.editor.client.util.DragType;
import org.kie.uberfire.perspective.editor.client.api.ExternalPerspectiveEditorComponent;

public class DragGridElement extends Composite {

    private ExternalPerspectiveEditorComponent externalComponent;
    private DragType type;
    private String dragText;

    @UiField
    InputAddOn move;

    TextBox textBox;

    public DragGridElement( DragType type,
                            final String dragText ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.dragText = dragText;
        this.type = type;
        build();
    }

    public DragGridElement( DragType type,
                            String dragText,
                            ExternalPerspectiveEditorComponent externalPerspectiveEditorComponent ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.dragText = dragText;
        this.type = type;
        this.externalComponent = externalPerspectiveEditorComponent;
        build();
    }

    private void build() {
        if ( type == DragType.GRID ) {
            createTextBox();
        } else {
            createComponentWidget();
        }
        createMoveIcon( type );
    }

    private void createMoveIcon( final DragType type ) {
        move.addDomHandler( new DragStartHandler() {
            @Override
            public void onDragStart( DragStartEvent event ) {
                String text = extractText();
                event.setData( type.name(), text );
                if ( isAExternalComponent( type ) ) {
                    event.setData( type.name(), externalComponent.getClass().getName()  );
                }
                event.getDataTransfer().setDragImage( move.getElement(), 10, 10 );
            }
        }, DragStartEvent.getType() );

        move.getElement().setDraggable( Element.DRAGGABLE_TRUE );
    }

    private boolean isAExternalComponent( DragType type ) {
        return type == DragType.EXTERNAL;
    }

    private String extractText() {
        return textBox.getText().isEmpty() ? type.name() : textBox.getText();
    }

    private void createComponentWidget() {
        textBox = new TextBox();
        textBox.setPlaceholder( dragText );
        textBox.setReadOnly( true );
        textBox.setAlternateSize( AlternateSize.MEDIUM );
        move.add( textBox );
    }

    private void createTextBox() {
        textBox = new TextBox();
        textBox.setText( dragText );
        textBox.setAlternateSize( AlternateSize.SMALL );
        move.add( textBox );
    }

    interface MyUiBinder extends UiBinder<Widget, DragGridElement> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}
