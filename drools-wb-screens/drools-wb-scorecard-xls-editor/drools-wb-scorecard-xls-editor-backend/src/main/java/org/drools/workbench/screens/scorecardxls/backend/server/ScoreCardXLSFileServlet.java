/*
 * Copyright 2005 JBoss Inc
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

package org.drools.workbench.screens.scorecardxls.backend.server;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.guvnor.services.backend.file.upload.AbstractFileServlet;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * This is for dealing with XLS Score Cards
 */
public class ScoreCardXLSFileServlet extends AbstractFileServlet {

    private static final long serialVersionUID = 510l;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private ExtendedScoreCardXLSService scoreCardXLSService;

    @Override
    protected InputStream doLoad( final Path path ) {
        return scoreCardXLSService.load( path );
    }

    @Override
    protected void doCreate( final Path path,
                             final InputStream data,
                             final String comment ) {
        scoreCardXLSService.create( path,
                                    data,
                                    comment );
    }

    @Override
    protected void doUpdate( final Path path,
                             final InputStream data,
                             final String comment ) {
        scoreCardXLSService.save( path,
                                  data,
                                  comment );
    }

    @Override
    protected Path convertPath( final String fileName,
                                final String contextPath ) throws URISyntaxException {
        final org.kie.commons.java.nio.file.Path path = ioService.get( new URI( contextPath ) );
        return paths.convert( path.resolve( fileName ),
                              false );
    }

    @Override
    protected Path convertPath( final String fullPath ) throws URISyntaxException {
        final org.kie.commons.java.nio.file.Path path = ioService.get( new URI( fullPath ) );
        return paths.convert( path,
                              false );
    }
}
