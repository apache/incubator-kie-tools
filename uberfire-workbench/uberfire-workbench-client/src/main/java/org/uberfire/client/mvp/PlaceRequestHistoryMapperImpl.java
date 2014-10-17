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
package org.uberfire.client.mvp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import com.google.gwt.http.client.URL;

public class PlaceRequestHistoryMapperImpl
        implements
        PlaceRequestHistoryMapper {

    @Override
    public PlaceRequest getPlaceRequest( String fullIdentifier ) {
        final String encodedIdentifier = fullIdentifier.contains( "?" ) ? fullIdentifier.substring( 0, fullIdentifier.indexOf( "?" ) ) : fullIdentifier;
        final String identifier = urlDecode( encodedIdentifier );
        final String query = fullIdentifier.contains( "?" ) ? fullIdentifier.substring( fullIdentifier.indexOf( "?" ) + 1 ) : "";
        final Map<String, String> parameters = getParameters( query );

        final PlaceRequest placeRequest;
        if ( parameters.containsKey( "path_uri" ) ) {
            if ( parameters.containsKey( "has_version_support" ) ) {
                placeRequest = new PathPlaceRequest( PathFactory.newPath( parameters.remove( "file_name" ), parameters.remove( "path_uri" ), new HashMap<String, Object>() {{
                    put( PathFactory.VERSION_PROPERTY, Boolean.valueOf( parameters.remove( "has_version_support" ) ) );
                }} ), identifier );
            } else {
                placeRequest = new PathPlaceRequest( PathFactory.newPath( parameters.remove( "file_name" ), parameters.remove( "path_uri" ) ), identifier );
            }
        } else {
            placeRequest = new DefaultPlaceRequest( identifier );
        }

        for ( String parameterName : parameters.keySet() ) {
            placeRequest.addParameter( parameterName, parameters.get( parameterName ) );
        }

        return placeRequest;
    }

    private Map<String, String> getParameters( String query ) {
        final Map<String, String> parameters = new HashMap<String, String>();

        if ( query != null && !"".equalsIgnoreCase( query ) ) {
            final List<String> parts = Arrays.asList( query.split( "&" ) );
            for ( String part : parts ) {
                int index = part.indexOf( '=' );
                String name = null;
                String value = null;
                if ( index == -1 ) {
                    name = part;
                    value = "";
                } else {
                    name = part.substring( 0, index );
                    value = index < part.length() ? part.substring( index + 1 ) : "";
                    value = urlDecode( value );
                }
                parameters.put( urlDecode( name ), value );
            }
        }

        return parameters;
    }

    String urlDecode( String value ) {
        return URL.decode( value );
    }
}
