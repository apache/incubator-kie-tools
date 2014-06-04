package org.uberfire.client;

import java.util.Collection;
import java.util.Set;

import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

public class JSWorkbenchPerspectiveActivity implements PerspectiveActivity {

    private PlaceRequest place;

    private Command callback;

    private JSNativePerspective nativePerspective;

    public JSWorkbenchPerspectiveActivity( final JSNativePerspective nativePerspective ) {
        this.nativePerspective = nativePerspective;
    }

    @Override
    public void launch( final PlaceRequest place,
                        final Command callback ) {
        this.place = place;
        this.callback = callback;
        loadState();
    }

    @Override
    public void onStartup() {
        nativePerspective.onStartup();
    }

    @Override
    public void onStartup( final PlaceRequest place ) {
        nativePerspective.onStartup( place );
    }

    @Override
    public void onClose() {
        nativePerspective.onClose();
    }

    @Override
    public void onShutdown() {
        nativePerspective.onShutdown();
    }

    @Override
    public PerspectiveDefinition getPerspective() {
        return nativePerspective.buildPerspective();
    }

    @Override
    public String getIdentifier() {
        return nativePerspective.getId();
    }

    @Override
    public boolean isDefault() {
        return nativePerspective.isDefault();
    }

    @Override
    public Menus getMenus() {
        return null;
    }

    @Override
    public ToolBar getToolBar() {
        return null;
    }

    @Override
    public void onOpen() {
        nativePerspective.onOpen();
    }

    @Override
    public String getSignatureId() {
        return nativePerspective.getId();
    }

    @Override
    public Collection<String> getRoles() {
        return nativePerspective.getRoles();
    }

    @Override
    public Collection<String> getTraits() {
        return nativePerspective.getTraits();
    }

//    //Save the current state of the Workbench
//    private void saveState() {
//
//        onClose();
//
//        final PerspectiveDefinition perspective = nativePerspective.getPanelManager().getPerspective();
//
//        if ( perspective == null ) {
//            //On startup the Workbench has not been set to contain a perspective
//            loadState();
//
//        } else if ( perspective.isTransient() ) {
//            //Transient Perspectives are not saved
////            nativePerspective.getPlaceManager().closeAllPlaces();
//            loadState();
//
//        } else {
//            //Save first, then close all places before loading persisted state
//            nativePerspective.getWbServices().call( new RemoteCallback<Void>() {
//                @Override
//                public void callback( Void response ) {
////                    nativePerspective.getPlaceManager().closeAllPlaces();
//                    loadState();
//                }
//            } ).save( perspective );
//        }
//    }

    //Load the persisted state of the Workbench or use the default Perspective definition if no saved state found
    private void loadState() {
        final PerspectiveDefinition perspective = getPerspective();

        if ( perspective.isTransient() ) {
            //Transient Perspectives are not saved and hence cannot be loaded
            initialisePerspective( perspective );

        } else {

            nativePerspective.getWbServices().loadPerspective( perspective.getName(), new ParameterizedCommand<PerspectiveDefinition>() {
                @Override
                public void execute( final PerspectiveDefinition response ) {
                    if ( response == null ) {
                        initialisePerspective( perspective );
                    } else {
                        initialisePerspective( response );
                    }
                }
            } );
        }
    }

    //Initialise Workbench state to that of the provided perspective
    private void initialisePerspective( final PerspectiveDefinition perspective ) {

        onStartup( place );

        nativePerspective.getPanelManager().setPerspective( perspective );

        Set<PartDefinition> parts = nativePerspective.getPanelManager().getRoot().getParts();
        for ( PartDefinition part : parts ) {
            final PlaceRequest place = clonePlaceAndMergeParameters( part.getPlace() );
            part.setPlace( place );
            nativePerspective.getPlaceManager().goTo( part, nativePerspective.getPanelManager().getRoot() );
        }
        buildPerspective( nativePerspective.getPanelManager().getRoot() );

        onOpen();
    }

    private void buildPerspective( final PanelDefinition panel ) {
        for ( final PanelDefinition child : panel.getChildren() ) {

            if ( child.getHeight() == null || child.getWidth() == null ) {
                buildSize( child );
            }

            final PanelDefinition target = nativePerspective.getPanelManager().addWorkbenchPanel( panel,
                                                                                                  child,
                                                                                                  child.getPosition() );
            addChildren( target );
        }
    }

    private void buildSize( final PanelDefinition panel ) {
        if ( panel.getParts().isEmpty() ) {
            return;
        }
        for ( final PartDefinition partDefinition : panel.getParts() ) {
            final Activity currentActivity = nativePerspective.getActivityManager().getActivity( partDefinition.getPlace() );
            if ( currentActivity instanceof WorkbenchActivity ) {
                final Integer width = ( (WorkbenchActivity) currentActivity ).preferredWidth();
                final Integer height = ( (WorkbenchActivity) currentActivity ).preferredHeight();
                if ( width != null || height != null ) {
                    panel.setHeight( height );
                    panel.setWidth( width );
                    break;
                }
            }
        }
    }

    private void addChildren( final PanelDefinition panel ) {
        final Set<PartDefinition> parts = panel.getParts();
        for ( final PartDefinition part : parts ) {
            final PlaceRequest place = clonePlaceAndMergeParameters( part.getPlace() );
            part.setPlace( place );
            nativePerspective.getPlaceManager().goTo( part, panel );
        }
        buildPerspective( panel );
    }

    private PlaceRequest clonePlaceAndMergeParameters( final PlaceRequest _place ) {
        return _place.clone();
    }
}
