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

package org.kie.workbench.common.screens.datasource.management.backend.integration.wildfly;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.OperationBuilder;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.as.controller.client.helpers.ClientConstants.*;
import static org.kie.workbench.common.screens.datasource.management.util.ServiceUtil.*;

/**
 * Base class for Wildfly/EAP based clients.
 */
public abstract class WildflyBaseClient {

    private static final Logger logger = LoggerFactory.getLogger( WildflyBaseClient.class );

    private static final String PREFIX = "datasource.management.wildfly";

    public static final String HTTP_REMOTING_PROTOCOL = "http-remoting";

    public static final String HTTPS_REMOTING_PROTOCOL = "https-remoting";

    public static final String PROTOCOL = PREFIX + ".protocol";
    public static final String HOST = PREFIX + ".host";
    public static final String PORT = PREFIX + ".port";
    public static final String ADMIN = PREFIX + ".admin";
    public static final String PASSWORD = PREFIX + ".password";
    public static final String REALM = PREFIX + ".realm";
    public static final String PROFILE = PREFIX + ".profile";
    public static final String SERVER_GROUP = PREFIX + ".serverGroup";
    public static final String REFERENCE_SERVER_HOST = PREFIX + ".referenceServerHost";
    public static final String REFERENCE_SERVER_NAME = PREFIX + ".referenceServerName";
    public static final String JBOSS_SERVER_NAME = "jboss.server.name";

    protected static final String DEFAULT_HOST = "localhost";
    protected static final int DEFAULT_PORT = 9990;
    protected static final String DEFAULT_ADMIN = null;
    protected static final String DEFAULT_ADMIN_PASSWORD = null;
    protected static final String DEFAULT_REALM = "ApplicationRealm";

    protected String protocol;
    protected String host;
    protected int port;
    protected String admin;
    protected String password;
    protected String realm;
    protected String profile;
    protected String serverGroup;
    protected String referenceServerHost;
    protected String referenceServerName;

    public void loadConfig( Properties properties ) {
        protocol = getManagedProperty( properties, PROTOCOL, HTTP_REMOTING_PROTOCOL );
        host = getManagedProperty( properties, HOST, DEFAULT_HOST );
        String currentPort = null;
        try {
            currentPort = getManagedProperty( properties, PORT, String.valueOf( DEFAULT_PORT ) );
            port = Integer.parseInt( currentPort );
        } catch ( Exception e ) {
            logger.error( "It was not possible to parse port configuration from: " + currentPort +
                    " default port: " + DEFAULT_PORT + " will be used instead." );
            port = DEFAULT_PORT;
        }
        admin = getManagedProperty( properties, ADMIN, DEFAULT_ADMIN );
        password = getManagedProperty( properties, PASSWORD, DEFAULT_ADMIN_PASSWORD );
        realm = getManagedProperty( properties, REALM, DEFAULT_REALM );
        profile = getManagedProperty( properties, PROFILE, null );
        serverGroup = getManagedProperty( properties, SERVER_GROUP, null );
        referenceServerHost = getManagedProperty( properties, REFERENCE_SERVER_HOST, null );
        referenceServerName = getManagedProperty( properties, REFERENCE_SERVER_NAME, null );
    }

    public ModelControllerClient createControllerClient( ) throws Exception {
        return createControllerClient( true );
    }

    public ModelControllerClient createControllerClient( boolean checkConnection ) throws Exception {

        ModelControllerClient client = ModelControllerClient.Factory.create( protocol, InetAddress.getByName( host ), port,
                new CallbackHandler( ) {
                    public void handle( Callback[] callbacks )
                            throws IOException, UnsupportedCallbackException {
                        for ( Callback current : callbacks ) {
                            if ( current instanceof NameCallback ) {
                                NameCallback ncb = ( NameCallback ) current;
                                ncb.setName( admin );
                            } else if ( current instanceof PasswordCallback ) {
                                PasswordCallback pcb = ( PasswordCallback ) current;
                                pcb.setPassword( password.toCharArray( ) );
                            } else if ( current instanceof RealmCallback ) {
                                RealmCallback rcb = ( RealmCallback ) current;
                                rcb.setText( realm );
                            } else {
                                throw new UnsupportedCallbackException( current );
                            }
                        }
                    }
                } );

        if ( checkConnection ) {
            testConnection( client );
        }
        return client;
    }

    /**
     * Tests the connection with the server.
     * @return If the connection was successfully established returns a String with the server version information.
     * @throws Exception If it was not possible to connect to server.
     */
    public String testConnection( ) throws Exception {
        ModelControllerClient client = null;
        try {
            client = createControllerClient( false );
            return testConnection( client );
        } finally {
            safeClose( client );
        }
    }

    private String testConnection( final ModelControllerClient client ) throws Exception {
        try {
            final ModelNode op = new ModelNode( );
            op.get( ClientConstants.OP ).set( "read-resource" );

            final ModelNode returnVal = client.execute( new OperationBuilder( op ).build( ) );
            final String productName = returnVal.get( "result" ).get( "product-name" ).asString( );
            final String productVersion = returnVal.get( "result" ).get( "product-version" ).asString( );
            final String releaseVersion = returnVal.get( "result" ).get( "release-version" ).asString( );
            final String releaseCodeName = returnVal.get( "result" ).get( "release-codename" ).asString( );
            final StringBuilder stringBuilder = new StringBuilder( );
            stringBuilder.append( productName + ", " + productVersion );
            stringBuilder.append( " (" + releaseCodeName + ", " + releaseVersion + ")" );
            return stringBuilder.toString( );
        } catch ( Exception e ) {
            logger.error( "It was not possible to open connection to Wildfly/EAP server.", e );
            throw new Exception( "It was not possible to open connection to server. " + e.getMessage( ) );
        }
    }

    /**
     * Checks the outcome returned by server when an operation was executed.
     *
     * @param response ModelNode returned by server as response.
     *
     * @throws Exception
     */
    public void checkResponse( ModelNode response ) throws Exception {

        final String outcome = response.get( OUTCOME ) != null ? response.get( OUTCOME ).asString() : "";

        if ( outcome.contains( "failed" ) ) {
            throw new Exception( "operation execution failed. :" + getErrorDescription( response ) );
        } else if ( outcome.contains( "canceled" ) ) {
            throw new Exception( "operation execution was canceled by server: " + getErrorDescription( response ) );
        } else if ( outcome.contains( SUCCESS ) ) {
            //great!!!
        }
    }

    public boolean isFailure( ModelNode response ) {
        final String outcome = response.get( OUTCOME ) != null ? response.get( OUTCOME ).asString() : "";
        return outcome.contains( "failed" );
    }

    public void safeClose( final Closeable closeable ) {
        if ( closeable != null ) {
            try {
                if ( logger.isDebugEnabled() ) {
                    logger.debug( "starting ModelControllerClient connection close" );
                }
                boolean disableClose = Boolean.valueOf( System.getProperty( "disableClose" ) );
                if ( disableClose ) {
                    logger.warn( "ModelControllerClient connection closing was disabled" );
                } else {
                    closeable.close();
                }
                if ( logger.isDebugEnabled() ) {
                    logger.debug( "ModelControllerClient connection was closed successfully" );
                }
            } catch ( Exception e ) {
                logger.error( "An error was produced during ModelControllerClient closing: ", e );
            }
        }
    }

    public boolean isStandalone( ) {
        return profile == null;
    }

    public String getReferenceServerName( ) {
        if ( referenceServerName == null ) {
            referenceServerName = System.getProperty( JBOSS_SERVER_NAME );
        }
        return referenceServerName;
    }

    public String getReferenceServerHost( ) {
        if ( referenceServerHost == null ) {
            String currentServer = getReferenceServerName( );
            try {
                List< String > hosts = getHosts( );
                for ( String host : hosts ) {
                    List< String > servers = getServers( host );
                    if ( servers.contains( currentServer ) ) {
                        referenceServerHost = host;
                        break;
                    }
                }
            } catch ( Exception e ) {
                logger.error( "It was not possible to calculate the referenceServerHost for currentServer: "
                        + currentServer, e );
            }
        }
        return referenceServerHost;
    }

    public List< String > getHosts( ) throws Exception {
        ArrayList< String > result = new ArrayList<>( );
        ModelControllerClient client = null;
        ModelNode response = null;

        try {
            // /:read-children-names(child-type=host)
            ModelNode operation = new ModelNode( );
            operation.get( OP ).set( "read-children-names" );
            operation.get( "child-type" ).set( "host" );

            client = createControllerClient( );
            response = client.execute( operation );

            if ( !isFailure( response ) ) {
                if ( response.hasDefined( RESULT ) ) {
                    List< ModelNode > nodes = response.get( RESULT ).asList( );
                    for ( ModelNode node : nodes ) {
                        result.add( node.asString( ) );
                    }
                }
            }
        } finally {
            safeClose( client );
            checkResponse( response );
        }
        return result;
    }

    public List< String > getServers( String host ) throws Exception {
        ArrayList< String > result = new ArrayList<>( );
        ModelControllerClient client = null;
        ModelNode response = null;

        try {
            // /host=host1:read-children-names(child-type=server)
            ModelNode operation = new ModelNode( );
            operation.get( OP ).set( "read-children-names" );
            operation.get( "child-type" ).set( "server" );
            operation.get( OP_ADDR ).add( "host", host );

            client = createControllerClient( );
            response = client.execute( operation );

            if ( !isFailure( response ) ) {
                if ( response.hasDefined( RESULT ) ) {
                    List< ModelNode > nodes = response.get( RESULT ).asList( );
                    for ( ModelNode node : nodes ) {
                        result.add( node.asString( ) );
                    }
                }
            }
        } finally {
            safeClose( client );
            checkResponse( response );
        }
        return result;
    }

    private String getErrorDescription( ModelNode response ) {
        if ( response.hasDefined( FAILURE_DESCRIPTION ) ) {
            return response.get( FAILURE_DESCRIPTION ).asString();
        } else {
            return response.asString();
        }
    }
}