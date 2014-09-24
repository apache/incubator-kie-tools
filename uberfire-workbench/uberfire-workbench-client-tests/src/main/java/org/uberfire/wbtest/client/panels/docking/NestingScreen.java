package org.uberfire.wbtest.client.panels.docking;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

@Dependent
@Named("org.uberfire.wbtest.client.panels.docking.NestingScreen")
public class NestingScreen extends AbstractTestScreenActivity {

    /**
     * Gets incremented every time a new instance of this class is created. Tests that want to assert on how many
     * instances have been created by a specific operation are free to reset this to 0.
     */
    public static int instanceCount;

    @Inject Logger log;

    @Inject
    public NestingScreen( PlaceManager placeManager ) {
        super( placeManager );
        instanceCount++;
    }

    @Inject PlaceManager placeManager;
    @Inject PanelManager panelManager;

    Panel panel = new FlowPanel();
    Button addNorthPanelButton = new Button( "Add North Child" );
    Button addSouthPanelButton = new Button( "Add South Child" );
    Button addEastPanelButton = new Button( "Add East Child" );
    Button addWestPanelButton = new Button( "Add West Child" );
    Button closeButton = new Button( "Close" );
    Button dumpLayout = new Button( "Dump Layout Info" );

    Map<CompassPosition, Integer> childCounts = new HashMap<CompassPosition, Integer>();

    /**
     * The value of the "place" parameter from the PlaceRequest that launched us. Gets used for building predictable but
     * unique IDs for each nested panel.
     */
    private String positionTag;

    @Override
    public IsWidget getWidget() {
        return panel;
    }


    @PostConstruct
    private void setup() {
        addNorthPanelButton.addStyleName( "north" );
        addNorthPanelButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                addNestedPanel( CompassPosition.NORTH );
            }
        } );
        addSouthPanelButton.addStyleName( "south" );
        addSouthPanelButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                addNestedPanel( CompassPosition.SOUTH );
            }
        } );
        addEastPanelButton.addStyleName( "east" );
        addEastPanelButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                addNestedPanel( CompassPosition.EAST );
            }
        } );
        addWestPanelButton.addStyleName( "west" );
        addWestPanelButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                addNestedPanel( CompassPosition.WEST );
            }
        } );
        closeButton.addStyleName( "close" );
        closeButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                placeManager.closePlace( getPlace() );
            }
        } );
        dumpLayout.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                log.info( Layouts.getContainmentHierarchy( panel ) );
            }
        } );

        panel.add( addNorthPanelButton );
        panel.add( addSouthPanelButton );
        panel.add( addEastPanelButton );
        panel.add( addWestPanelButton );
        panel.add( closeButton );
        panel.add( dumpLayout );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        positionTag = place.getParameter( "place", "root" );
        panel.ensureDebugId( "NestingScreen-" + positionTag );
        panel.add( new Label( positionTag ) );
    }

    @Override
    public String getTitle() {
        if ( positionTag != null ) {
            return positionTag;
        }
        return "Not Started";
    }

    void addNestedPanel( CompassPosition position ) {
        PanelDefinition myParentPanel = findParentPanel( panelManager.getRoot() );
        if ( myParentPanel == null ) {
            Window.alert( "Could not find my parent panel!" );
            return;
        }

        Integer childCount = childCounts.get(position);
        if ( childCount == null ) {
            childCount = 0;
        }
        childCounts.put( position, childCount + 1 );
        final String childPositionTag = positionTag + position.name().charAt( 0 ) + childCount;

        PanelDefinition childPanel = new PanelDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        childPanel.setElementId( "NestingScreenPanel-" + childPositionTag );
        childPanel.setWidth( 100 );
        childPanel.setHeight( 100 );
        panelManager.addWorkbenchPanel( myParentPanel, childPanel, position );

        PlaceRequest childScreen = new DefaultPlaceRequest( getClass().getName() );
        childScreen.addParameter( "place", childPositionTag );
        placeManager.goTo( childScreen, childPanel );
    }

    private PanelDefinition findParentPanel( PanelDefinition startAt ) {
        for ( PartDefinition part : startAt.getParts() ) {
            if ( part.getPlace().equals( place ) ) {
                return startAt;
            }
        }
        for ( PanelDefinition child : startAt.getChildren() ) {
            PanelDefinition found = findParentPanel( child );
            if ( found != null ) {
                return found;
            }
        }
        return null;
    }
}
