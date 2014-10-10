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

public class DragGridElement extends Composite {

    @UiField
    InputAddOn move;

    TextBox textBox;

    public DragGridElement( DragType type,
                            final String dragText ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        if ( type == DragType.GRID ) {
            createTextBox( dragText );
        } else {
            createComponentWidget( dragText );
        }
        createMoveIcon( type );
    }

    private void createMoveIcon( final DragType type ) {
        move.addDomHandler( new DragStartHandler() {
            @Override
            public void onDragStart( DragStartEvent event ) {
                String text = extractText();

                event.setData( type.name(), text );
                event.getDataTransfer().setDragImage( move.getElement(), 10, 10 );
            }
        }, DragStartEvent.getType() );

        move.getElement().setDraggable( Element.DRAGGABLE_TRUE );
    }

    private String extractText() {
        String EMPTY_SCREEN = " ";
        return textBox.getText().isEmpty() ? EMPTY_SCREEN : textBox.getText();
    }

    private void createComponentWidget( String label ) {
        textBox = new TextBox();
        textBox.setPlaceholder( label );
        textBox.setReadOnly( true );
        textBox.setAlternateSize( AlternateSize.MEDIUM );
        move.add( textBox );
    }

    private void createTextBox( String label ) {
        textBox = new TextBox();
        textBox.setText( label );
        textBox.setAlternateSize( AlternateSize.SMALL );
        move.add( textBox );
    }

    interface MyUiBinder extends UiBinder<Widget, DragGridElement> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}
