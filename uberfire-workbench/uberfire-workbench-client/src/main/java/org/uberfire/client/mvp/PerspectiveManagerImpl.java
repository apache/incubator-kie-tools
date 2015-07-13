package org.uberfire.client.mvp;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@ApplicationScoped
public class PerspectiveManagerImpl implements PerspectiveManager {

    @Inject
    private PanelManager panelManager;

    @Inject
    private WorkbenchServicesProxy wbServices;

    @Inject
    private Event<PerspectiveChange> perspectiveChangeEvent;

    private PerspectiveActivity currentPerspective;

    private PerspectiveDefinition livePerspectiveDef;

    @Override
    public void switchToPerspective( final PlaceRequest placeRequest,
                                     final PerspectiveActivity activity,
                                     final ParameterizedCommand<PerspectiveDefinition> doWhenFinished ) {

        // switching perspectives is a chain of async operations. they're declared here
        // in reverse order (last to first):

        NotifyOthersOfPerspectiveChangeCommand fourthOperation = new NotifyOthersOfPerspectiveChangeCommand( placeRequest, doWhenFinished );

        BuildPerspectiveFromDefinitionCommand thirdOperation = new BuildPerspectiveFromDefinitionCommand( activity, fourthOperation );

        FetchPerspectiveCommand secondOperation = new FetchPerspectiveCommand( activity, thirdOperation );

        secondOperation.execute();
    }

    @Override
    public PerspectiveActivity getCurrentPerspective() {
        return currentPerspective;
    }

    @Override
    public PerspectiveDefinition getLivePerspectiveDefinition() {
        return livePerspectiveDef;
    }

    @Override
    public void savePerspectiveState( Command doWhenFinished ) {
        if ( currentPerspective != null && !currentPerspective.isTransient() ) {
            wbServices.save( currentPerspective.getIdentifier(), livePerspectiveDef, doWhenFinished );
        } else {
            doWhenFinished.execute();
        }
    }

    @Override
    public void loadPerspectiveStates( final ParameterizedCommand<Set<PerspectiveDefinition>> doWhenFinished ) {
        wbServices.loadPerspectives( doWhenFinished );
    }

    @Override
    public void removePerspectiveState( final String perspectiveId,
                                        final Command doWhenFinished ) {
        wbServices.removePerspectiveState( perspectiveId, doWhenFinished );
    }

    @Override
    public void removePerspectiveStates( final Command doWhenFinished ) {
        wbServices.removePerspectiveStates( doWhenFinished );
    }

    /**
     * Fetches the given perspective's definition either from the server (if non-transient) or from the activity itself
     * (if transient or if the fetch call fails).
     */
    class FetchPerspectiveCommand implements Command {

        private final PerspectiveActivity perspective;
        private final ParameterizedCommand<PerspectiveDefinition> doAfterFetch;

        public FetchPerspectiveCommand( PerspectiveActivity perspective,
                                        ParameterizedCommand<PerspectiveDefinition> doAfterFetch ) {
            this.perspective = checkNotNull( "perspective", perspective );
            this.doAfterFetch = checkNotNull( "doAfterFetch", doAfterFetch );
        }

        @Override
        public void execute() {
            currentPerspective = perspective;
            if ( perspective.isTransient() ) {
                //Transient Perspectives are not saved and hence cannot be loaded
                doAfterFetch.execute( perspective.getDefaultPerspectiveLayout() );

            } else {

                wbServices.loadPerspective( perspective.getIdentifier(), new ParameterizedCommand<PerspectiveDefinition>() {
                    @Override
                    public void execute( final PerspectiveDefinition response ) {
                        if ( response == null ) {
                            doAfterFetch.execute( perspective.getDefaultPerspectiveLayout() );
                        } else {
                            doAfterFetch.execute( response );
                        }
                    }
                } );
            }
        }
    }

    /**
     * Builds up the panels of a perspective based on the structure described in a given {@link PerspectiveDefinition}.
     */
    class BuildPerspectiveFromDefinitionCommand implements ParameterizedCommand<PerspectiveDefinition> {

        private final ParameterizedCommand<PerspectiveDefinition> doWhenFinished;
        private final PerspectiveActivity activity;

        public BuildPerspectiveFromDefinitionCommand( PerspectiveActivity activity,
                                                      ParameterizedCommand<PerspectiveDefinition> doWhenFinished ) {
            this.activity = checkNotNull( "activity", activity );
            this.doWhenFinished = checkNotNull( "doWhenFinished", doWhenFinished );
        }

        @Override
        public void execute( PerspectiveDefinition perspectiveDef ) {
            if ( livePerspectiveDef != null ) {
                tearDownChildPanelsRecursively( livePerspectiveDef.getRoot() );
            }
            livePerspectiveDef = perspectiveDef;
            panelManager.setRoot( activity, perspectiveDef.getRoot() );
            setupPanelRecursively( perspectiveDef.getRoot() );
            doWhenFinished.execute( perspectiveDef );
        }

        private void tearDownChildPanelsRecursively( final PanelDefinition panel ) {
            for ( PanelDefinition child : panel.getChildren() ) {
                tearDownChildPanelsRecursively( child );
                panelManager.removeWorkbenchPanel( child );
            }
        }

        private void setupPanelRecursively( final PanelDefinition panel ) {
            for ( PanelDefinition child : panel.getChildren() ) {
                final PanelDefinition target = panelManager.addWorkbenchPanel( panel,
                                                                               child,
                                                                               child.getPosition() );
                setupPanelRecursively( target );
            }
        }
    }

    class NotifyOthersOfPerspectiveChangeCommand implements ParameterizedCommand<PerspectiveDefinition> {

        private final PlaceRequest placeRequest;
        private final ParameterizedCommand<PerspectiveDefinition> doWhenFinished;

        public NotifyOthersOfPerspectiveChangeCommand( final PlaceRequest placeRequest,
                                                       final ParameterizedCommand<PerspectiveDefinition> doWhenFinished ) {
            this.placeRequest = checkNotNull( "placeRequest", placeRequest );
            ;
            this.doWhenFinished = checkNotNull( "doWhenFinished", doWhenFinished );
        }

        @Override
        public void execute( PerspectiveDefinition perspectiveDef ) {
            perspectiveChangeEvent.fire( new PerspectiveChange( placeRequest, perspectiveDef, currentPerspective.getMenus(), currentPerspective.getIdentifier() ) );
            doWhenFinished.execute( perspectiveDef );
        }
    }
}
