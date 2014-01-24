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

import com.google.gwt.http.client.URL;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

public class PlaceRequestHistoryMapperImpl
        implements
        PlaceRequestHistoryMapper {

    @Override
    public PlaceRequest getPlaceRequest( String fullIdentifier ) {
        String identifier = fullIdentifier.indexOf( "?" ) != -1 ? fullIdentifier.substring( 0, fullIdentifier.indexOf( "?" ) ) : fullIdentifier;
        String query = fullIdentifier.indexOf( "?" ) != -1 ? fullIdentifier.substring( fullIdentifier.indexOf( "?" ) + 1 ) : "";
        Map<String, String> parameters = getParameters( query );

        PlaceRequest placeRequest = new DefaultPlaceRequest( identifier );
        for ( String parameterName : parameters.keySet() ) {
            placeRequest.addParameter( parameterName, parameters.get( parameterName ) );
        }

        return placeRequest;
    }

    private Map<String, String> getParameters( String query ) {
        Map<String, String> parameters = new HashMap<String, String>();

        if ( query != null && !"".equalsIgnoreCase( query ) ) {
            List<String> parts = Arrays.asList( query.split( "&" ) );
            for ( String part : parts ) {
                int index = part.indexOf( '=' );
                String name = null;
                String value = null;
                if ( index == -1 ) {
                    name = part;
                    value = "";
                } else {
                    name = part.substring( 0,
                                           index );
                    value = index < part.length() ? part.substring( index + 1 ) : "";
                    value = this.urlDecode( value );
                }
                parameters.put( urlDecode( name ),
                                value );
            }
        }

        return parameters;
    }

    String urlDecode( String value ) {
        return URL.decode( value );
    }
}
