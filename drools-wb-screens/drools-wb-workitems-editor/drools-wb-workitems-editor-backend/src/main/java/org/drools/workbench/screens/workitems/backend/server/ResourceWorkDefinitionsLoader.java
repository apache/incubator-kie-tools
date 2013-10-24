/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.workitems.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.core.process.core.WorkDefinition;
import org.guvnor.common.services.backend.file.FileDiscoveryService;
import org.guvnor.common.services.backend.file.FileExtensionFilter;
import org.uberfire.io.IOService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * Class to load Work Definitions from VFS
 */
public class ResourceWorkDefinitionsLoader {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private FileDiscoveryService fileDiscoveryService;

    private FileExtensionFilter widFilter = new FileExtensionFilter( ".wid" );

    public Map<String, WorkDefinition> loadWorkDefinitions( final Path projectRoot ) {
        //Find all WID files in the project
        final Map<String, WorkDefinition> workDefinitions = new HashMap<String, WorkDefinition>();
        final org.uberfire.java.nio.file.Path nioProjectRoot = Paths.convert( projectRoot );
        final Collection<org.uberfire.java.nio.file.Path> widPaths = fileDiscoveryService.discoverFiles( nioProjectRoot,
                                                                                                            widFilter,
                                                                                                            true );
        if ( widPaths == null || widPaths.isEmpty() ) {
            return workDefinitions;
        }

        //Load WID files
        final List<String> definitions = new ArrayList<String>();
        for ( org.uberfire.java.nio.file.Path widPath : widPaths ) {
            final String definition = ioService.readAllString( widPath );
            definitions.add( definition );
        }

        //Parse MVEL expressions into model
        workDefinitions.putAll( WorkDefinitionsParser.parse( definitions ) );

        return workDefinitions;
    }

}
