/*
 * Copyright 2013 JBoss Inc
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

package org.uberfire.ext.editor.commons.backend.version;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.ext.editor.commons.version.impl.PortableVersionRecord;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.rpc.SessionInfo;

import static org.uberfire.backend.server.util.Paths.*;
import static org.uberfire.java.nio.file.StandardCopyOption.*;

@Service
@ApplicationScoped
public class VersionServiceImpl
        implements VersionService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private SessionInfo sessionInfo;

    @Override
    public List<VersionRecord> getVersions( final Path path ) {
        try {
            final List<VersionRecord> records = ioService.getFileAttributeView( convert( path ),
                                                                                VersionAttributeView.class ).readAttributes().history().records();

            final List<VersionRecord> result = new ArrayList<VersionRecord>( records.size() );

            for ( final VersionRecord record : records ) {
                result.add( new PortableVersionRecord( record.id(), record.author(), record.email(), record.comment(),
                                                       record.date(), record.uri() ) );
            }

            return result;

        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Path getPathToPreviousVersion( String uri ) {
        return convert( ioService.get( URI.create( uri ) ) );
    }

    @Override
    public Path restore( final Path _path,
                         final String comment ) {
        try {
            final org.uberfire.java.nio.file.Path path = convert( _path );
            final org.uberfire.java.nio.file.Path target = path.getFileSystem().getPath( path.toString() );

            return convert( ioService.copy( path, target, REPLACE_EXISTING,
                                            new CommentedOption(
                                                    sessionInfo != null ? sessionInfo.getId() : "--",
                                                    sessionInfo != null ? sessionInfo.getIdentity().getIdentifier() : "system",
                                                    null,
                                                    comment ) ) );

        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }
}
