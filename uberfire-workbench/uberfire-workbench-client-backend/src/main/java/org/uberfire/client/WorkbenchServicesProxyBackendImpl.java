package org.uberfire.client;

import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.SplashScreenFilter;
import org.uberfire.workbench.services.WorkbenchServices;

@Alternative
@Dependent
public class WorkbenchServicesProxyBackendImpl implements WorkbenchServicesProxy {

    @Inject
    private Caller<WorkbenchServices> workbenchServices;

    @Override
    public void save( final String perspectiveId,
                      final PerspectiveDefinition activePerspective,
                      final Command callback ) {
        workbenchServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void o ) {
                callback.execute();
            }
        } ).save( perspectiveId, activePerspective );
    }

    @Override
    public void loadPerspective( final String name,
                                 final ParameterizedCommand<PerspectiveDefinition> parameterizedCommand ) {
        workbenchServices.call( new RemoteCallback<PerspectiveDefinition>() {
            @Override
            public void callback( final PerspectiveDefinition result ) {
                parameterizedCommand.execute( result );
            }
        } ).loadPerspective(name);
    }

    @Override
    public void loadPerspectives( final ParameterizedCommand<Set<PerspectiveDefinition>> parameterizedCommand ) {
        workbenchServices.call( new RemoteCallback<Set<PerspectiveDefinition>>() {
            @Override
            public void callback( final Set<PerspectiveDefinition> result ) {
                parameterizedCommand.execute( result );
            }
        } ).loadPerspectives();
    }

    @Override
    public void removePerspectiveState( final String perspectiveId,
            final Command callback ) {
        workbenchServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void o ) {
                callback.execute();
            }
        } ).removePerspectiveState(perspectiveId);
    }

    @Override
    public void removePerspectiveStates( final Command doWhenFinished ) {
        workbenchServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void o ) {
                doWhenFinished.execute();
            }
        } ).removePerspectiveStates();
    }

    @Override
    public void save( final SplashScreenFilter splashFilter ) {
        workbenchServices.call().save( splashFilter );
    }

    @Override
    public void loadSplashScreenFilter( final String name,
                                        final ParameterizedCommand<SplashScreenFilter> parameterizedCommand ) {
        workbenchServices.call( new RemoteCallback<SplashScreenFilter>() {
            @Override
            public void callback( final SplashScreenFilter result ) {
                parameterizedCommand.execute( result );
            }
        } ).loadSplashScreenFilter( name );
    }
}
