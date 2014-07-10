package org.uberfire.client.mvp;

import static org.uberfire.commons.validation.PortablePreconditions.*;

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
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;

@ApplicationScoped
public class PerspectiveManagerImpl implements PerspectiveManager {

    @Inject
    private PanelManager panelManager;

    // FIXME this is a circular dependency!
    // would be better modularity if the PerspectiveManager would return a list of activities to launch
    // once the panels have been arranged, instead of calling PlaceManager.goTo() explicitly
    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkbenchServicesProxy wbServices;

    @Inject
    private Event<PerspectiveChange> perspectiveChangeEvent;

    private PerspectiveActivity currentPerspective;

    private PerspectiveDefinition livePerspectiveDef;

    @Override
    public void switchToPerspective( final PerspectiveActivity perspective, final Command doWhenFinished ) {

        // switching perspectives is a chain of async operations. they're declared here
        // in reverse order (last to first):

        NotifyOthersOfPerspectiveChangeCommand fourthOperation = new NotifyOthersOfPerspectiveChangeCommand( doWhenFinished );

        BuildPerspectiveFromDefinitionCommand thirdOperation = new BuildPerspectiveFromDefinitionCommand( fourthOperation );

        FetchPerspectiveCommand secondOperation = new FetchPerspectiveCommand( perspective, thirdOperation );

        if ( currentPerspective != null && !currentPerspective.isTransient() ) {
            wbServices.save( livePerspectiveDef, secondOperation );
        } else {
            secondOperation.execute();
        }
    }

    @Override
    public PerspectiveActivity getCurrentPerspective() {
        return currentPerspective;
    }

    @Override
    public PerspectiveDefinition getLivePerspectiveDefinition() {
        return livePerspectiveDef;
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
     * Builds up a perspective in both the PanelManager and PlaceManager based on the structure described in a given
     * {@link PerspectiveDefinition}.
     */
    class BuildPerspectiveFromDefinitionCommand implements ParameterizedCommand<PerspectiveDefinition> {

        private final ParameterizedCommand<PerspectiveDefinition> doWhenFinished;

        public BuildPerspectiveFromDefinitionCommand( ParameterizedCommand<PerspectiveDefinition> doWhenFinished ) {
            this.doWhenFinished = checkNotNull( "doWhenFinished", doWhenFinished );
        }

        @Override
        public void execute( PerspectiveDefinition perspectiveDef ) {
            livePerspectiveDef = perspectiveDef;
            panelManager.setRoot( perspectiveDef.getRoot() );
            setupPanelRecursively( perspectiveDef.getRoot() );
            doWhenFinished.execute( perspectiveDef );
        }

        private void setupPanelRecursively( final PanelDefinition panel ) {
            for ( PartDefinition part : panel.getParts() ) {
                final PlaceRequest place = clonePlaceAndMergeParameters( part.getPlace() );
                part.setPlace( place );
                placeManager.goTo( part, panel );
            }
            for ( PanelDefinition child : panel.getChildren() ) {
                final PanelDefinition target = panelManager.addWorkbenchPanel( panel,
                                                                               child,
                                                                               child.getPosition() );
                setupPanelRecursively( target );
            }
        }
        // TODO (UF-88) when PlaceRequest is an immutable value type, cloning will no longer be a sensible operation
        private PlaceRequest clonePlaceAndMergeParameters( final PlaceRequest _place ) {
            return _place.clone();
        }
    }

    class NotifyOthersOfPerspectiveChangeCommand implements ParameterizedCommand<PerspectiveDefinition> {

        private final Command doWhenFinished;

        public NotifyOthersOfPerspectiveChangeCommand( Command doWhenFinished ) {
            this.doWhenFinished = checkNotNull( "doWhenFinished", doWhenFinished );
        }

        @Override
        public void execute( PerspectiveDefinition perspectiveDef ) {
            perspectiveChangeEvent.fire( new PerspectiveChange( perspectiveDef, currentPerspective.getMenus(), currentPerspective.getIdentifier() ) );
            doWhenFinished.execute();
        }
    }
}
