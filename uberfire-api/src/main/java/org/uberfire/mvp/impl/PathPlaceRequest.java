/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.mvp.impl;

import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.backend.vfs.IsVersioned;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.PlaceRequest;

import static org.uberfire.util.URIUtil.*;

/**
 *
 */
@Portable
public class PathPlaceRequest extends DefaultPlaceRequest {

    public static String NULL = "[null]";

    private ObservablePath path;

    public PathPlaceRequest() {
    }

    public PathPlaceRequest( final Path path ) {
        super( NULL );
        this.path = createObservablePath( path );
    }

    protected ObservablePath createObservablePath( Path path ) {
        return IOC.getBeanManager().lookupBean( ObservablePath.class ).getInstance().wrap( path );
    }

    public PathPlaceRequest( final Path path,
                             final Map<String, String> parameters ) {
        this( path );
        this.parameters.putAll( parameters );
    }

    public PathPlaceRequest( final Path path,
                             final String id ) {
        super( id );
        this.path = createObservablePath( path );
    }

    public PathPlaceRequest( final Path path,
                             final String id,
                             final Map<String, String> parameters ) {
        this( path, id );
        this.parameters.putAll( parameters );
    }

    public ObservablePath getPath() {
        return path;
    }

    @Override
    public String getFullIdentifier() {
        final StringBuilder fullIdentifier = new StringBuilder();
        if ( getIdentifier() != null ) {
            fullIdentifier.append( this.getIdentifier() );
        } else {
            fullIdentifier.append( NULL );
        }

        fullIdentifier.append( "?" ).append( "path_uri" ).append( "=" ).append( encode( path.toURI() ) ).append( "&" )
                .append( "file_name" ).append( "=" ).append( encode( path.getFileName() ) ).append( "&" );

        if ( path instanceof IsVersioned ) {
            fullIdentifier.append( "has_version_support" ).append( "=" ).append( ( (IsVersioned) path ).hasVersionSupport() ).append( "&" );
        }

        for ( String name : this.getParameterNames() ) {
            fullIdentifier.append( name ).append( "=" ).append( this.getParameter( name, null ) );
            fullIdentifier.append( "&" );
        }

        if ( fullIdentifier.length() != 0 && fullIdentifier.lastIndexOf( "&" ) + 1 == fullIdentifier.length() ) {
            fullIdentifier.deleteCharAt( fullIdentifier.length() - 1 );
        }

        return fullIdentifier.toString();
    }

    @Override
    public PlaceRequest clone() {
        return new PathPlaceRequest( path, identifier, parameters );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        final PathPlaceRequest that = (PathPlaceRequest) o;

        return getPath().equals( that.getPath() );
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getPath().hashCode();
        return result;
    }
}
