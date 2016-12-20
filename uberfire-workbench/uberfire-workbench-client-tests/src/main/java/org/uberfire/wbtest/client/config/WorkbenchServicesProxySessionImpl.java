/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.jboss.errai.marshalling.client.Marshalling;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.SplashScreenFilter;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * Implementation of {@link WorkbenchServicesProxy} that stores perspective config in local memory, so it lasts for
 * the current session but not across page reloads.
 */
@Alternative
@ApplicationScoped
public class WorkbenchServicesProxySessionImpl implements WorkbenchServicesProxy {

    private final Map<String, PerspectiveDefinition> storedPerspectives = new HashMap<String, PerspectiveDefinition>();
    private final Map<String, SplashScreenFilter> storedSplashFilters = new HashMap<String, SplashScreenFilter>();

    @Override
    public void save( final String perspectiveId,
                      final PerspectiveDefinition activePerspective,
                      final Command callback ) {
        storedPerspectives.put( perspectiveId, copy( activePerspective ) );

        // scheduling as a deferred action to better simulate a real async request to the server
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {
            @Override
            public void execute() {
                callback.execute();
            }
        } );
    }

    @Override
    public void loadPerspective( final String name,
                                 final ParameterizedCommand<PerspectiveDefinition> parameterizedCommand ) {
        // scheduling as a deferred action to better simulate a real async request to the server
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {
            @Override
            public void execute() {
                parameterizedCommand.execute( storedPerspectives.get( name ) );
            }
        } );
    }

    @Override
    public void loadPerspectives( final ParameterizedCommand<Set<PerspectiveDefinition>> parameterizedCommand ) {
        // scheduling as a deferred action to better simulate a real async request to the server
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                parameterizedCommand.execute( new HashSet<PerspectiveDefinition>(storedPerspectives.values()) );
            }
        });
    }

    @Override
    public void removePerspectiveState( final String perspectiveId,
            final Command callback ) {
        storedPerspectives.remove( perspectiveId );

        // scheduling as a deferred action to better simulate a real async request to the server
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {
            @Override
            public void execute() {
                callback.execute();
            }
        } );
    }

    @Override
    public void removePerspectiveStates( final Command doWhenFinished ) {
        storedPerspectives.clear();

        // scheduling as a deferred action to better simulate a real async request to the server
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {
            @Override
            public void execute() {
                doWhenFinished.execute();
            }
        } );
    }

    @Override
    public void save( final SplashScreenFilter splashFilter ) {
        storedSplashFilters.put( splashFilter.getName(), copy( splashFilter ) );
    }

    @Override
    public void loadSplashScreenFilter( final String name,
                                        final ParameterizedCommand<SplashScreenFilter> parameterizedCommand ) {
        // scheduling as a deferred action to better simulate a real async request to the server
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {
            @Override
            public void execute() {
                parameterizedCommand.execute( storedSplashFilters.get( name ) );
            }
        } );
    }

    /**
     * Uses Errai Marshalling to make an independent copy of the given object and everything it references.
     */
    @SuppressWarnings("unchecked")
    private static <T> T copy( T original ) {
        return (T) Marshalling.fromJSON( Marshalling.toJSON( original ), original.getClass() );
    }
}
