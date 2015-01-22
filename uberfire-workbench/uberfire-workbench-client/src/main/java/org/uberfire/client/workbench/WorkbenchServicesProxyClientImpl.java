package org.uberfire.client.workbench;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.SplashScreenFilter;

@Alternative
@Dependent
public class WorkbenchServicesProxyClientImpl implements WorkbenchServicesProxy {

    @Override
    public void save( final String perspectiveId,
                      final PerspectiveDefinition activePerspective,
                      final Command callback ) {
        callback.execute();
    }

    @Override
    public void loadPerspective( final String name,
                                 final ParameterizedCommand<PerspectiveDefinition> parameterizedCommand ) {
        parameterizedCommand.execute( null );
    }

    @Override
    public void removePerspectiveStates( final Command doWhenFinished ) {
        doWhenFinished.execute();
    }

    @Override
    public void save( final SplashScreenFilter splashFilter ) {

    }

    @Override
    public void loadSplashScreenFilter( final String name,
                                        final ParameterizedCommand<SplashScreenFilter> parameterizedCommand ) {
        parameterizedCommand.execute( null );
    }
}
