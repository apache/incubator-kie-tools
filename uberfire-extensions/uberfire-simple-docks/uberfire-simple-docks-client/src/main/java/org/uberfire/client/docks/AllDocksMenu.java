package org.uberfire.client.docks;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.mvp.ParameterizedCommand;

public class AllDocksMenu extends PopupPanel {

    interface AllDocksMenuBinder
            extends
            UiBinder<Widget, AllDocksMenu> {

    }

    private static AllDocksMenuBinder uiBinder = GWT.create( AllDocksMenuBinder.class );

    @UiField
    FlowPanel popup;

    @UiField
    NavList navList;

    private List<DocksMenu> currentDocks;

    public AllDocksMenu() {
        super( true );
        add( uiBinder.createAndBindUi( this ) );
        onMouseOutHidePopup();
    }

    private void onMouseOutHidePopup() {
        this.addDomHandler( new MouseOutHandler() {
            public void onMouseOut( MouseOutEvent event ) {
                hide();
            }
        }, MouseOutEvent.getType() );
    }

    public void show( final Widget widgetToPosition ) {
        this.setPopupPositionAndShow( new PositionCallback() {
            public void setPosition( int offsetWidth,
                                     int offsetHeight ) {
                int top = widgetToPosition.getElement().getAbsoluteTop() - getElement().getClientHeight();
                setPopupPosition( widgetToPosition.getAbsoluteLeft(), top );
            }
        } );
    }

    public void createDocksMenu( final String label,
                            final ParameterizedCommand<String> openDockLink ) {

        DocksMenu docksMenu = new DocksMenu( label, openDockLink );

        if(!alreadyHasThisDock( docksMenu ) ){
            currentDocks.add( docksMenu );

            final NavLink navLink = GWT.create( NavLink.class );
            navLink.setText( label );
            navLink.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    openDockLink.execute( label );
                }
            } );
            navList.add( navLink );
        }

    }

    private boolean alreadyHasThisDock( DocksMenu docksMenu ) {
        return currentDocks.contains( docksMenu );
    }

    public void clearDocksMenu() {
        navList.clear();
        currentDocks = new ArrayList<DocksMenu>(  );
    }

    private class DocksMenu {

        private final String label;
        private final ParameterizedCommand<String> command;

        DocksMenu( String label,
                   ParameterizedCommand<String> command ) {
            this.label = label;
            this.command = command;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( !( o instanceof DocksMenu ) ) {
                return false;
            }

            DocksMenu docksMenu = (DocksMenu) o;

            return !( label != null ? !label.equals( docksMenu.label ) : docksMenu.label != null );

        }

        @Override
        public int hashCode() {
            return label != null ? label.hashCode() : 0;
        }
    }

    public List<DocksMenu> getCurrentDocks() {
        return currentDocks;
    }
}
