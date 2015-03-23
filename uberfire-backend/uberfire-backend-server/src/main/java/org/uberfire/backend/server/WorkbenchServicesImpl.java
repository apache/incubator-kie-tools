/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.backend.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.SplashScreenFilter;
import org.uberfire.workbench.services.WorkbenchServices;

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.java.nio.file.Files.*;

@Service
@ApplicationScoped
public class WorkbenchServicesImpl
        implements
        WorkbenchServices {

    public static final String PERSPECTIVE_EXTENSION = ".perspective";

    @Inject
    @Named("configIO")
    private IOService ioService;

    @Inject
    private UserServicesImpl userServices;

    private final XStream xs = new XStream();

    @Override
    public void save( final String perspectiveId,
                      final PerspectiveDefinition perspective ) {
        final String xml = xs.toXML( perspective );
        final Path perspectivePath = userServices.buildPath( "perspectives",
                                                             perspectiveId + PERSPECTIVE_EXTENSION );
        try {
            ioService.startBatch( perspectivePath.getFileSystem() );
            ioService.write( perspectivePath, xml );
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public void save( SplashScreenFilter splashFilter ) {
        final String xml = xs.toXML( splashFilter );
        final Path splashFilterPath = userServices.buildPath( "splash",
                                                              splashFilter.getName() + ".filter" );
        try {
            ioService.startBatch( splashFilterPath.getFileSystem() );
            ioService.write( splashFilterPath, xml );
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public PerspectiveDefinition loadPerspective( final String perspectiveName ) {
        final Path perspectivePath = userServices.buildPath( "perspectives",
                                                             perspectiveName + PERSPECTIVE_EXTENSION );
        if ( ioService.exists( perspectivePath ) ) {
            final String xml = ioService.readAllString( perspectivePath );
            return (PerspectiveDefinition) xs.fromXML( xml );
        }

        return null;
    }

    @Override
    public Set<PerspectiveDefinition> loadPerspectives() {
        final Set<PerspectiveDefinition> result = new HashSet<PerspectiveDefinition>();
        final Path perspectivesPath = userServices.buildPath( "perspectives" );
        if ( ioService.exists( perspectivesPath ) ) {

            walkFileTree( perspectivesPath, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile( final Path file,
                                                  final BasicFileAttributes attrs ) throws IOException {
                    try {
                        checkNotNull( "file", file );
                        checkNotNull( "attrs", attrs );
                        String fileName = file.getFileName().toString();
                        if ( fileName.endsWith( PERSPECTIVE_EXTENSION ) && attrs.isRegularFile() ) {
                            String perspectiveName = fileName.substring( 0, fileName.indexOf( PERSPECTIVE_EXTENSION ) );
                            PerspectiveDefinition def = loadPerspective( perspectiveName );
                            if ( def != null ) {
                                result.add( def );
                            }
                        }
                    } catch ( final Exception ex ) {
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            } );
        }
        return result;
    }

    @Override
    public void removePerspectiveState( final String perspectiveId ) {
        final Path perspectivePath = userServices.buildPath( "perspectives", perspectiveId + ".perspective" );
        if ( ioService.exists( perspectivePath ) ) {
            ioService.delete( perspectivePath );
        }
    }

    @Override
    public void removePerspectiveStates() {
        final Path perspectivesPath = userServices.buildPath( "perspectives" );
        if ( ioService.exists( perspectivesPath ) ) {
            try {
                ioService.startBatch( perspectivesPath.getFileSystem() );
                ioService.delete( perspectivesPath,
                                  StandardDeleteOption.NON_EMPTY_DIRECTORIES );
            } finally {
                ioService.endBatch();
            }
        }
    }

    @Override
    public SplashScreenFilter loadSplashScreenFilter( String filterName ) {
        final Path splashFilterPath = userServices.buildPath( "splash",
                                                              filterName + ".filter" );

        if ( ioService.exists( splashFilterPath ) ) {
            final String xml = ioService.readAllString( splashFilterPath );
            return (SplashScreenFilter) xs.fromXML( xml );
        }

        return null;
    }

    @Override
    public Map<String, String> loadDefaultEditorsMap() {
        final Map<String, String> map = new HashMap<String, String>();
        try {
            final Path path = getPathToDefaultEditors();
            if ( ioService.exists( path ) ) {
                for ( String line : ioService.readAllLines( path ) ) {
                    if ( !line.trim().startsWith( "#" ) ) {
                        String[] split = line.split( "=" );
                        map.put( split[ 0 ], split[ 1 ] );
                    }
                }
            }

            return map;

        } catch ( final NoSuchFileException e ) {
            e.printStackTrace();
            return map;
        }
    }

    @Override
    public void saveDefaultEditors( final Map<String, String> properties ) {
        final StringBuilder text = new StringBuilder();
        for ( String key : properties.keySet() ) {
            text.append( String.format( "%s=%s", key, properties.get( key ) ) );
        }
        final Path path = getPathToDefaultEditors();
        try {
            ioService.startBatch( path.getFileSystem() );
            ioService.write( path,
                             text.toString() );
        } finally {
            ioService.endBatch();
        }
    }

    private Path getPathToDefaultEditors() {
        return userServices.buildPath( "defaultEditors", null );
    }
}
