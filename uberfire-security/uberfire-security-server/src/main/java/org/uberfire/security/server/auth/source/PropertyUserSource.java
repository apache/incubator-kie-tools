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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.uberfire.security.Role;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationSource;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.auth.RoleProvider;
import org.uberfire.security.impl.RoleImpl;
import org.uberfire.security.impl.auth.UsernamePasswordCredential;
import org.uberfire.security.server.SecurityConstants;

import static org.uberfire.commons.validation.Preconditions.*;

public class PropertyUserSource implements AuthenticationSource,
                                           RoleProvider {

    private boolean alreadyInit = false;
    private Map<String, Object> credentials = new HashMap<String, Object>();
    private Map<String, List<Role>> roles = new HashMap<String, List<Role>>();

    @Override
    public synchronized void initialize( Map<String, ?> options ) {
        if ( !alreadyInit ) {
            InputStream is = null;
            try {
                if ( options.containsKey( "usersPropertyFile" ) ) {
                    is = new FileInputStream( new File( (String) options.get( "usersPropertyFile" ) ) );
                } else {
                    is = Thread.currentThread().getContextClassLoader().getResourceAsStream( SecurityConstants.CONFIG_USERS_PROPERTIES );
                }

                if ( is == null ) {
                    throw new RuntimeException( "Uname to find properties file." );
                }
                final Properties properties = new Properties();
                properties.load( is );

                for ( Map.Entry<Object, Object> contentEntry : properties.entrySet() ) {
                    final String content = contentEntry.getValue().toString();
                    final String[] result = content.split( "," );
                    credentials.put( contentEntry.getKey().toString(), result[ 0 ] );
                    final List<Role> roles = new ArrayList<Role>();
                    if ( result.length > 1 ) {
                        for ( int i = 1; i < result.length; i++ ) {
                            final String currentRole = result[ i ];
                            roles.add( new RoleImpl( currentRole ) );
                        }
                        this.roles.put( contentEntry.getKey().toString(), roles );
                    }
                }

            } catch ( FileNotFoundException e ) {
                throw new RuntimeException( e );
            } catch ( IOException e ) {
                throw new RuntimeException( e );
            } finally {
                alreadyInit = true;
                if ( is != null ) {
                    try {
                        is.close();
                    } catch ( Exception e ) {
                    }
                }
            }
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
    public boolean authenticate( final Credential credential,
                                 final SecurityContext securityContext ) {
        final UsernamePasswordCredential usernamePasswd = checkInstanceOf( "credential", credential, UsernamePasswordCredential.class );

        final Object pass = credentials.get( usernamePasswd.getUserName() );
        if ( pass != null && pass.equals( usernamePasswd.getPassword() ) ) {
            return true;
        }
        return false;
    }

    @Override
    public List<Role> loadRoles( final Principal principal,
                                 final SecurityContext securityContext ) {
        return roles.get( principal.getName() );
    }
}
