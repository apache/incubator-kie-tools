/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.services.builder;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.java.nio.file.Path;
import org.kie.workbench.common.services.backend.SourceService;
import org.kie.workbench.common.services.backend.SourceServices;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@ApplicationScoped
public class SourceServicesImpl
        implements SourceServices {

    private Set<SourceService> sourceServices = new HashSet<SourceService>();

    public SourceServicesImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public SourceServicesImpl( @Any Instance<SourceService> sourceServiceList ) {
        for ( SourceService sourceService : sourceServiceList ) {
            sourceServices.add( sourceService );
        }
    }

    @Override
    public boolean hasServiceFor( final Path path ) {
        final SourceService sourceService = getMatchingSourceService( path );
        return sourceService != null;
    }

    @Override
    public SourceService getServiceFor( final Path path ) {
        final SourceService sourceService = getMatchingSourceService( path );
        if ( sourceService == null ) {
            throw new IllegalArgumentException( "No SourceService found for '" + path + "'." );
        }
        return sourceService;
    }

    private SourceService getMatchingSourceService( final Path path ) {
        //Find all matching services
        final List<SourceService> matchingServices = new ArrayList<SourceService>();
        for ( final SourceService service : sourceServices ) {
            if ( service.accepts( path ) ) {
                matchingServices.add( service );
            }
        }
        if ( matchingServices.isEmpty() ) {
            return null;
        }

        //Find the service that matches the longest pattern. This will be the more specific service
        SourceService specificMatchingService = null;
        for ( final SourceService service : matchingServices ) {
            if ( specificMatchingService == null ) {
                specificMatchingService = service;
            } else {
                if ( service.getPattern().length() > specificMatchingService.getPattern().length() ) {
                    specificMatchingService = service;
                }
            }
        }
        return specificMatchingService;
    }

}
