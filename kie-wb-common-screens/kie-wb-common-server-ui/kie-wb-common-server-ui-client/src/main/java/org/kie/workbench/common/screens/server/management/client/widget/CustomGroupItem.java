package org.kie.workbench.common.screens.server.management.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.uberfire.mvp.Command;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class CustomGroupItem extends Anchor {

    public CustomGroupItem( final String text,
                            final IconType icon,
                            final Command command ) {
        super();
        addStyleName( Styles.LIST_GROUP_ITEM );
        setText( checkNotEmpty( "text", text ) );
        setIcon( checkNotNull( "icon", icon ) );
        addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                command.execute();
            }
        } );
    }

    public void setActive( boolean active ) {
        if ( active ) {
            addStyleName( "active" );
        } else {
            removeStyleName( "active" );
        }
    }
}
