package org.uberfire.client.docks;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.resources.WebAppResource;
import org.uberfire.mvp.ParameterizedCommand;

public class DocksItem
        extends Composite {

    private final String identifier;
    private boolean selected;

    interface ViewBinder
            extends
            UiBinder<Widget, DocksItem> {

    }

    @UiField
    Label itemLabel;

    @UiField
    FlowPanel dockItem;

    private ViewBinder uiBinder = GWT.create( ViewBinder.class );

    private static WebAppResource CSS = GWT.create( WebAppResource.class );

    public DocksItem( final String identifier,
                      final ParameterizedCommand<String> selectCommand,
                      final ParameterizedCommand<String> deselectCommand ) {
        this.identifier = identifier;
        this.selected = false;
        initWidget( uiBinder.createAndBindUi( this ) );
        itemLabel.setText( identifier );
        itemLabel.addStyleName( CSS.CSS().dockLabel() );
        itemLabel.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if ( !selected ) {
                    selectCommand.execute( identifier );
                } else {
                    deselectCommand.execute( identifier );
                }
            }
        } );
        setDndSupport();
    }

    private void setDndSupport() {
        dockItem.addDomHandler( new DragStartHandler() {
            @Override
            public void onDragStart( DragStartEvent event ) {
                createDragStart( event );
            }
        }, DragStartEvent.getType() );

        dockItem.getElement().setDraggable( Element.DRAGGABLE_TRUE );
    }

    private void createDragStart( DragStartEvent event ) {
        event.setData( DocksItem.class.getSimpleName(), itemLabel.getText() );
        Icon icon = new Icon( IconType.MOVE );
        icon.setIconSize(IconSize.DEFAULT);
        event.getDataTransfer().setDragImage(icon.getElement(), 10, 10);
    }

    public void selected() {
        selected = true;
        itemLabel.removeStyleName( CSS.CSS().dockLabel() );
        itemLabel.addStyleName( CSS.CSS().dockLabelSelected() );
        dockItem.removeStyleName( CSS.CSS().dockItem() );
        dockItem.addStyleName( CSS.CSS().dockItemSelected() );
    }

    public void deselect() {
        selected = false;
        itemLabel.addStyleName( CSS.CSS().dockLabel() );
        itemLabel.removeStyleName( CSS.CSS().dockLabelSelected() );
        dockItem.removeStyleName( CSS.CSS().dockItemSelected() );
        dockItem.addStyleName( CSS.CSS().dockItem() );
    }

    public String getIdentifier() {
        return identifier;
    }

}
