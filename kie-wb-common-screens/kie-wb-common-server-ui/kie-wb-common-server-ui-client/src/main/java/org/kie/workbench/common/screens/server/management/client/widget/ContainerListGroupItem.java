package org.kie.workbench.common.screens.server.management.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.LinkedGroupItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.mvp.ParameterizedCommand;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class ContainerListGroupItem extends LinkedGroupItem {

    public ContainerListGroupItem( final String containerName,
                                   final ParameterizedCommand<String> command ) {
        super();
        insert( new Icon( IconType.FOLDER_O ), 0 );
        setText( checkNotEmpty( "containerName", containerName ) );
        addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                command.execute( containerName );
            }
        } );
    }
}
