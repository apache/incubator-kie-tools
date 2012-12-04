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

package org.uberfire.backend.server.util;

import java.net.URI;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.kie.commons.io.IOService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

import static org.uberfire.backend.vfs.PathFactory.newPath;

@ApplicationScoped
public final class Paths {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    public Path convert( final org.kie.commons.java.nio.file.Path path ) {
        final Map<String, Object> attributes = ioService.readAttributes( path, "*" );

        return newPath( path.getFileName().toString(), path.toUri().toString(), attributes );
    }

    public org.kie.commons.java.nio.file.Path convert( final Path path ) {
        try {
            return ioService.get( URI.create( path.toURI() ) );
        } catch ( IllegalArgumentException e ) {
            try {
                return ioService.get( URI.create( URIUtil.encodePath( path.toURI() ) ) );
            } catch ( URIException ex ) {
                return null;
            }
        }
    }

}
