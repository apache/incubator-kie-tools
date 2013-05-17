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

package org.uberfire.security.server.auth.source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.security.Role;
import org.uberfire.security.auth.AuthenticationSource;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.auth.RoleProvider;
import org.uberfire.security.impl.RoleImpl;
import org.uberfire.security.impl.auth.UsernamePasswordCredential;

import static org.kie.commons.validation.Preconditions.*;

public abstract class AbstractDatabaseAuthSource implements AuthenticationSource,
                                                            RoleProvider {

    private static final Logger LOG = LoggerFactory.getLogger( AbstractDatabaseAuthSource.class );

    private boolean alreadyInit = false;
    private String userQuery;
    private String rolesQuery;

    public abstract Connection getConnection();

    public synchronized void initialize( final Map<String, ?> options ) {
        if ( !alreadyInit ) {
            userQuery = "select 1 from " + options.get( "userTable" ) + " where " + options.get( "userField" ) + "=? and " + options.get( "passwordField" ) + "=?";

            if ( options.containsKey( "userQuery" ) ) {
                userQuery = (String) options.get( "userQuery" );
            }

            LOG.debug( "userQuery = " + userQuery );

            rolesQuery = "select " + options.get( "userRoleRoleField" ) + " from " + options.get( "userRoleTable" ) + " where " + options.get( "userRoleUserField" ) + "=?";

            if ( options.containsKey( "rolesQuery" ) ) {
                rolesQuery = (String) options.get( "rolesQuery" );
            }

            LOG.debug( "rolesQuery = " + rolesQuery );

            alreadyInit = true;
        }
    }

    @Override
    public boolean supportsCredential( final Credential credential ) {
        if ( credential == null ) {
            return false;
        }
        return credential instanceof UsernamePasswordCredential;
    }

    @Override
    public boolean authenticate( final Credential credential ) {
        final UsernamePasswordCredential usernamePasswd = checkInstanceOf( "credential", credential, UsernamePasswordCredential.class );

        Connection connection = null;

        try {
            connection = getConnection();
            final PreparedStatement statement = connection.prepareStatement( userQuery );
            statement.setString( 1, usernamePasswd.getUserName() );
            statement.setObject( 2, usernamePasswd.getPassword() );
            final ResultSet queryResult = statement.executeQuery();
            final boolean result;
            if ( queryResult.next() ) {
                result = true;
            } else {
                result = false;
            }
            queryResult.close();
            statement.close();

            return result;
        } catch ( Exception ex ) {
            throw new IllegalStateException( ex );
        } finally {
            if ( connection != null ) {
                try {
                    connection.close();
                } catch ( SQLException e ) {
                    throw new IllegalStateException( e );
                }
            }
        }
    }

    @Override
    public List<Role> loadRoles( final Principal principal ) {
        Connection connection = null;
        try {
            connection = getConnection();
            final PreparedStatement statement = connection.prepareStatement( rolesQuery );
            statement.setString( 1, principal.getName() );
            final ResultSet queryResult = statement.executeQuery();
            final List<Role> roles = new ArrayList<Role>();
            while ( queryResult.next() ) {
                final String roleName = queryResult.getString( 1 );
                roles.add( new RoleImpl( roleName ) );
            }

            queryResult.close();
            statement.close();

            return roles;
        } catch ( Exception ex ) {
            throw new IllegalStateException( ex );
        } finally {
            if ( connection != null ) {
                try {
                    connection.close();
                } catch ( SQLException e ) {
                    throw new IllegalStateException( e );
                }
            }
        }
    }

}
