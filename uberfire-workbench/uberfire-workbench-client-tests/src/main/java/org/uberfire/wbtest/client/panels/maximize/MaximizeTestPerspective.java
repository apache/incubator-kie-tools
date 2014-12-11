package org.uberfire.wbtest.client.panels.maximize;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleDnDWorkbenchPanelPresenter;
import org.uberfire.wbtest.client.api.AbstractTestPerspectiveActivity;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * A perspective to assist in testing the maximize panel feature.
 */
@Dependent
@Named("org.uberfire.wbtest.client.panels.maximize.MaximizeTestPerspective")
public class MaximizeTestPerspective extends AbstractTestPerspectiveActivity {

    public static final String LIST_PANEL_ID = "MaximizeTestPerspective-list";
    public static final String LIST_PANEL_SCREEN_1_ID = "1";
    public static final String LIST_PANEL_SCREEN_2_ID = "2";

    public static final String TAB_PANEL_ID = "MaximizeTestPerspective-tab";
    public static final String TAB_PANEL_SCREEN_3_ID = "3";
    public static final String TAB_PANEL_SCREEN_4_ID = "4";

    public static final String SIMPLE_PANEL_ID = "MaximizeTestPerspective-simple";
    public static final String SIMPLE_PANEL_SCREEN_5_ID = "5";

    @Inject
    public MaximizeTestPerspective( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        PerspectiveDefinition pd = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        pd.getRoot().setElementId( LIST_PANEL_ID );
        pd.getRoot().addPart( MaximizeTestScreen.class.getName() + "?debugId=1" );
        pd.getRoot().addPart( MaximizeTestScreen.class.getName() + "?debugId=2" );

        PanelDefinitionImpl simplePanel = new PanelDefinitionImpl( SimpleDnDWorkbenchPanelPresenter.class.getName() );
        simplePanel.setHeight( 200 );
        simplePanel.setElementId( SIMPLE_PANEL_ID );
        simplePanel.addPart( MaximizeTestScreen.class.getName() + "?debugId=5" );
        pd.getRoot().appendChild( CompassPosition.SOUTH, simplePanel );

        PanelDefinitionImpl tabPanel = new PanelDefinitionImpl( MultiTabWorkbenchPanelPresenter.class.getName() );
        tabPanel.setElementId( TAB_PANEL_ID );
        tabPanel.setWidth( 300 );
        tabPanel.addPart( MaximizeTestScreen.class.getName() + "?debugId=3" );
        tabPanel.addPart( MaximizeTestScreen.class.getName() + "?debugId=4" );
        pd.getRoot().appendChild( CompassPosition.WEST, tabPanel );

        return pd;
    }

}
