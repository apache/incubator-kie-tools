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

package org.uberfire.shared.mvp.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Default implementation of PlaceRequest
 */
@Portable
public class DefaultPlaceRequest
    implements
    PlaceRequest {

    public static final PlaceRequest  NOWHERE    = new DefaultPlaceRequest( "NOWHERE" );

    private final String              identifier;

    private final Map<String, String> parameters = new HashMap<String, String>();

    public DefaultPlaceRequest() {
        this.identifier = "";
    }

    public DefaultPlaceRequest(final String identifier) {
        this.identifier = identifier;
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
            fullIdentifier.append( name ).append( "=" ).append( this.getParameter( name,
                                                                                   null ) );
            fullIdentifier.append( "&" );
        }

        if ( fullIdentifier.length() != 0 && fullIdentifier.lastIndexOf( "&" ) + 1 == fullIdentifier.length() ) {
            fullIdentifier.deleteCharAt( fullIdentifier.length() - 1 );
        }

        return fullIdentifier.toString();
    }

    @Override
    public String getParameter(final String key,
                               final String defaultValue) {
        String value = null;

        if ( parameters != null ) {
            value = parameters.get( key );
        }

        if ( value == null ) {
            value = defaultValue;
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
    public PlaceRequest addParameter(final String name,
                                     final String value) {
        this.parameters.put( name,
                             value );
        return this;
    }

    @Override
    public PlaceRequest getPlace() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null ) return false;
        if ( !(o instanceof PlaceRequest) ) return false;

        PlaceRequest that = (PlaceRequest) o;
        return this.getFullIdentifier().equals( that.getFullIdentifier() );
    }

    @Override
    public int hashCode() {
        final String fullIdentifier = getFullIdentifier();
        return fullIdentifier != null ? fullIdentifier.hashCode() : 0;
    }

}
