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

package org.uberfire.ext.preferences.backend;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;

import org.uberfire.ext.preferences.shared.PreferenceScope;
import org.uberfire.ext.preferences.shared.PreferenceScopeTypes;
import org.uberfire.ext.preferences.shared.impl.DefaultScopes;
import org.uberfire.ext.preferences.shared.impl.exception.InvalidPreferenceScopeException;
import org.uberfire.rpc.SessionInfo;

@Vetoed
public class DefaultPreferenceScopeTypes implements PreferenceScopeTypes {

    private Map<String, DefaultKey> defaultKeyByType;

    private SessionInfo sessionInfo;

    protected DefaultPreferenceScopeTypes() {
    }

    @Inject
    public DefaultPreferenceScopeTypes( final SessionInfo sessionInfo ) {
        this.sessionInfo = sessionInfo;

        defaultKeyByType = new HashMap<>();
        defaultKeyByType.put( DefaultScopes.GLOBAL.type(), DefaultScopes.GLOBAL::type );
        defaultKeyByType.put( DefaultScopes.USER.type(), this.sessionInfo.getIdentity()::getIdentifier );
    }

    @Override
    public void validate( final PreferenceScope scope ) throws InvalidPreferenceScopeException {
        final String type = scope.type();

        validateType( type );

        final String key = scope.key();
        if ( typeRequiresKey( type ) && isEmpty( key ) ) {
            throw new InvalidPreferenceScopeException( "For this type, the key must be a non empty string." );
        }
    }

    @Override
    public boolean typeRequiresKey( final String type ) throws InvalidPreferenceScopeException {
        validateType( type );

        return defaultKeyByType.get( type ) == null;
    }

    @Override
    public String getDefaultKeyFor( final String type ) throws InvalidPreferenceScopeException {
        validateType( type );

        return defaultKeyByType.get( type ).get();
    }

    protected void validateType( final String type ) throws InvalidPreferenceScopeException {
        if ( isEmpty( type ) ) {
            throw new InvalidPreferenceScopeException( "Type must be a non empty string." );
        }

        if ( !defaultKeyByType.containsKey( type ) ) {
            throw new InvalidPreferenceScopeException( "Invalid preference scope type." );
        }
    }

    protected boolean isEmpty( String str ) {
        return str == null || str.isEmpty();
    }

    protected interface DefaultKey {
        String get();
    }
}