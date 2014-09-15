package org.uberfire.wbtest.client.headfoot;

import org.jboss.errai.databinding.client.api.Bindable;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;

@Bindable
public class NewPanelBuilder {

    private String partPlace;
    private String type;
    private String position;

    public String getPartPlace() {
        return partPlace;
    }

    public void setPartPlace( String partPlace ) {
        this.partPlace = partPlace;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition( String position ) {
        this.position = position;
    }

    public void makePanel( PlaceManager placeManager, PanelManager panelManager ) {
        PlaceRequest place = DefaultPlaceRequest.parse( partPlace );
        PanelDefinition panel = new PanelDefinitionImpl( type );
        panelManager.addWorkbenchPanel( panelManager.getRoot(),
                                        panel,
                                        CompassPosition.valueOf( position.toUpperCase() ) );
        placeManager.goTo( place, panel );
    }
}
