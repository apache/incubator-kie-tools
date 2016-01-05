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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.mvp.PlaceRequest;

@Portable
public class DefaultPlaceRequest implements PlaceRequest {

    protected String identifier;

    protected final Map<String, String> parameters = new HashMap<String, String>();

    private final boolean updateLocationBar;

    public DefaultPlaceRequest() {
        this( "" );
    }

    public DefaultPlaceRequest( final String identifier ) {
        this( identifier, Collections.<String, String>emptyMap(), true );
    }

    public DefaultPlaceRequest( final String identifier,
                                final Map<String, String> parameters ) {
        this( identifier, parameters, true );
    }

    /**
     * Creates a place request for the given place ID, with the given state parameters for that place, and the given
     * preference of whether or not the browser's location bar should be updated.
     *
     * @param identifier
     *            The place ID, or an empty string for the default place.
     * @param parameters
     *            Place-specific parameters to pass to the place. Must not be null.
     * @param updateLocationBar
     *            If true, the browser's history will be updated with this place request. If false, the location bar
     *            will not be modified as a result of this place request.
     */
    public DefaultPlaceRequest( final String identifier,
                                final Map<String, String> parameters,
                                final boolean updateLocationBar ) {
        this.identifier = identifier;
        this.parameters.putAll( parameters );
        this.updateLocationBar = updateLocationBar;
    }

    /**
     * Creates a new place request from a string that encodes a place ID and optional parameters in standard URL query
     * syntax.
     * <p>
     * For example, the following returns a PlaceRequest with identifier {@code MyPlaceID} and two parameters,
     * {@code param1} and {@code param2}.
     * <pre>
     *   DefaultPlaceRequest.parse("MyPlaceID?param1=val1&amp;param2=val2")
     * </pre>
     *
     * @param partNameAndParams
     *            specification of the place ID and optional parameter map. Special characters in the identifier, key
     *            name, or key value can be escaped using URL encoding: for '%' use '%25'; for '&amp;' use '%26'; for
     *            '=' use '%3d'; for '?' use '%3f'.
     * @return a new PlaceRequest configured according to the given string.
     */
    public static PlaceRequest parse( CharSequence partNameAndParams ) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();

        StringBuilder nextToken = new StringBuilder( 50 );
        String foundPartName = null;
        String key = null;
        for ( int i = 0; i < partNameAndParams.length(); i++ ) {
            char ch = partNameAndParams.charAt( i );
            switch ( ch ) {
                case '%':
                    StringBuilder hexVal = new StringBuilder( 2 );
                    hexVal.append( partNameAndParams.charAt( i + 1 ) );
                    hexVal.append( partNameAndParams.charAt( i + 2 ) );
                    nextToken.append( (char) Integer.parseInt( hexVal.toString(), 16 ) );
                    i += 2;
                    break;

                case '?':
                    if ( foundPartName == null ) {
                        foundPartName = nextToken.toString();
                        nextToken = new StringBuilder( 50 );
                    } else {
                        nextToken.append( '?' );
                    }
                    break;

                case '=':
                    if ( foundPartName == null ) {
                        nextToken.append( '=' );
                    } else {
                        key = nextToken.toString();
                        nextToken = new StringBuilder( 50 );
                    }
                    break;

                case '&':
                    parameters.put( key, nextToken.toString() );
                    nextToken = new StringBuilder( 50 );
                    key = null;
                    break;

                default:
                    nextToken.append( ch );
            }
        }

        if ( foundPartName == null ) {
            foundPartName = nextToken.toString();
        } else if ( key != null ) {
            parameters.put( key, nextToken.toString() );
        } else if ( nextToken.length() > 0 ) {
            parameters.put( nextToken.toString(), "" );
        }

        return new DefaultPlaceRequest( foundPartName, parameters );
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier( String identifier ) {
        this.identifier = identifier;
    }

    @Override
    public String getFullIdentifier() {
        StringBuilder fullIdentifier = new StringBuilder();
        fullIdentifier.append( this.getIdentifier() );

        if ( this.getParameterNames().size() > 0 ) {
            fullIdentifier.append( "?" );
        }
        for ( String name : this.getParameterNames() ) {
            fullIdentifier.append( name ).append( "=" ).append( this.getParameter( name, null ).toString() );
            fullIdentifier.append( "&" );
        }

        if ( fullIdentifier.length() != 0 && fullIdentifier.lastIndexOf( "&" ) + 1 == fullIdentifier.length() ) {
            fullIdentifier.deleteCharAt( fullIdentifier.length() - 1 );
        }

        return fullIdentifier.toString();
    }

    //TODO: Throw ValueFormatException if conversion to a String is not possible
    @Override
    public String getParameter( final String key,
                                final String defaultValue ) {

        final String value = parameters.get( key );

        if ( value == null ) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public Set<String> getParameterNames() {
        return parameters.keySet();
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public PlaceRequest addParameter( final String name,
                                      final String value ) {
        this.parameters.put( name, value );
        return this;
    }

    @Override
    public PlaceRequest clone() {
        return new DefaultPlaceRequest( identifier, parameters );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DefaultPlaceRequest ) ) {
            return false;
        }

        final DefaultPlaceRequest that = (DefaultPlaceRequest) o;

        if ( !identifier.equals( that.identifier ) ) {
            return false;
        }

        return parameters.equals( that.parameters );
    }

    @Override
    public boolean isUpdateLocationBarAllowed() {
        return updateLocationBar;
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + parameters.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PlaceRequest[\"" + identifier + "\" " + parameters + "]";
    }

}
