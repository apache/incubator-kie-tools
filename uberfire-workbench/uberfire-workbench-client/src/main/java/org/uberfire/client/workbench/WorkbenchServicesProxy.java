/*
 * Copyright 2014 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.workbench;

import java.util.Set;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.SplashScreenFilter;

public interface WorkbenchServicesProxy {

    void save( final String perspectiveId,
               final PerspectiveDefinition activePerspective,
               final Command callback );

    void loadPerspective( final String name,
                          final ParameterizedCommand<PerspectiveDefinition> parameterizedCommand );

    public void loadPerspectives( final ParameterizedCommand<Set<PerspectiveDefinition>> parameterizedCommand );

    void removePerspectiveState( final String perspectiveId,
            final Command callback );

    void removePerspectiveStates( final Command doWhenFinished );

    void save( final SplashScreenFilter splashFilter );

    void loadSplashScreenFilter( final String name,
                                 final ParameterizedCommand<SplashScreenFilter> parameterizedCommand );
}
