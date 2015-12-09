/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.whitelist;

import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.file.SupportsUpdate;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

public class PackageNameWhiteListSaver
        implements SupportsUpdate<WhiteList> {

    private MetadataServerSideService metadataService;

    private IOService ioService;

    private CommentedOptionFactory commentedOptionFactory;

    public PackageNameWhiteListSaver() {
    }

    @Inject
    public PackageNameWhiteListSaver( final @Named( "ioStrategy" ) IOService ioService,
                                      final MetadataServerSideService metadataService,
                                      final CommentedOptionFactory commentedOptionFactory ) {
        this.ioService = ioService;
        this.metadataService = metadataService;
        this.commentedOptionFactory = commentedOptionFactory;
    }

    @Override
    public Path save( final Path path,
                      final WhiteList content,
                      final Metadata metadata,
                      final String comment ) {


        try {
            ioService.write( Paths.convert( path ),
                             toString( content ),
                             metadataService.setUpAttributes( path,
                                                              metadata ),
                             commentedOptionFactory.makeCommentedOption( comment ) );

            return path;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }

    }

    private String toString( final WhiteList content ) {

        final StringBuilder builder = new StringBuilder();

        for ( String line : content ) {
            builder.append( line );
            builder.append( '\n' );
        }

        return builder.toString();
    }
}
