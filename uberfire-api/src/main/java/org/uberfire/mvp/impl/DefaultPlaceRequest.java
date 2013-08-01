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

package org.uberfire.mvp.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.mvp.PlaceRequest;

/**
 * Default implementation of PlaceRequest
 */
@Portable
public class DefaultPlaceRequest
        implements
        PlaceRequest {

    protected final String identifier;

    protected final Map<String, String> parameters = new HashMap<String, String>();

    public DefaultPlaceRequest() {
        this.identifier = "";
    }

    public DefaultPlaceRequest( final String identifier ) {
        this.identifier = identifier;
    }

    public DefaultPlaceRequest( final String identifier,
                                final Map<String, String> parameters ) {
        this( identifier );
        this.parameters.putAll( parameters );
    }

    @Override
    public String getIdentifier() {
        return identifier;
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
    public PlaceRequest getPlace() {
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

        DefaultPlaceRequest that = (DefaultPlaceRequest) o;

        if ( !identifier.equals( that.identifier ) ) {
            return false;
        }
        if ( !parameters.equals( that.parameters ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + parameters.hashCode();
        return result;
    }
}
