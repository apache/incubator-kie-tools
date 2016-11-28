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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.OperationBuilder;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;

import static org.jboss.as.controller.client.helpers.ClientConstants.*;

/**
 * Helper client for creating/modifying/deletion of data sources on a Wildfly server.
 */
public class WildflyDataSourceManagementClient
        extends WildflyBaseClient {

    /**
     * Creates a data source.
     */
    public void createDataSource( WildflyDataSourceDef dataSourceDef ) throws Exception {

        ModelNode operation = new ModelNode();
        operation.get( OP ).set( ADD );
        if ( profile != null ) {
            operation.get( OP_ADDR ).add( "profile", profile );
        }
        operation.get( OP_ADDR ).add( "subsystem", "datasources" );

        if ( dataSourceDef.getName() != null ) {
            operation.get( OP_ADDR ).add( "data-source", dataSourceDef.getName() );
        }
        if ( dataSourceDef.getJndi() != null ) {
            operation.get( WildflyDataSourceAttributes.JNDI_NAME ).set( dataSourceDef.getJndi() );
        }
        if ( dataSourceDef.getConnectionURL() != null ) {
            operation.get( WildflyDataSourceAttributes.CONNECTION_URL ).set( dataSourceDef.getConnectionURL() );
        }
        if ( dataSourceDef.getDriverName() != null ) {
            operation.get( WildflyDataSourceAttributes.DRIVER_NAME ).set( dataSourceDef.getDriverName() );
        }
        if ( dataSourceDef.getDriverClass() != null ) {
            operation.get( WildflyDataSourceAttributes.DRIVER_CLASS ).set( dataSourceDef.getDriverClass() );
        }
        if ( dataSourceDef.getDataSourceClass() != null ) {
            operation.get( WildflyDataSourceAttributes.DATASOURCE_CLASS ).set( dataSourceDef.getDataSourceClass() );
        }
        if ( dataSourceDef.getUser() != null ) {
            operation.get( WildflyDataSourceAttributes.USER_NAME ).set( dataSourceDef.getUser() );
        }
        if ( dataSourceDef.getPassword() != null ) {
            operation.get( WildflyDataSourceAttributes.PASSWORD ).set( dataSourceDef.getPassword() );
        }

        operation.get( WildflyDataSourceAttributes.JTA ).set( dataSourceDef.isUseJTA() );
        operation.get( WildflyDataSourceAttributes.USE_CCM ).set( dataSourceDef.isUseCCM() );

        ModelControllerClient client = createControllerClient();
        ModelNode response = client.execute( new OperationBuilder( operation ).build() );

        safeClose( client );
        checkResponse( response );
    }

    /**
     * Updates an existing data source.
     */
    public void updateDataSource( WildflyDataSourceDef dataSourceDef ) throws Exception {

        Map<String, Object> changeSet = new HashMap<>(  );

        changeSet.put( WildflyDataSourceAttributes.JNDI_NAME, dataSourceDef.getJndi() );
        changeSet.put( WildflyDataSourceAttributes.CONNECTION_URL, dataSourceDef.getConnectionURL() );
        changeSet.put( WildflyDataSourceAttributes.DRIVER_CLASS, dataSourceDef.getDriverClass() );
        changeSet.put( WildflyDataSourceAttributes.DATASOURCE_CLASS, dataSourceDef.getDataSourceClass() );
        changeSet.put( WildflyDataSourceAttributes.DRIVER_NAME, dataSourceDef.getDriverName() );
        changeSet.put( WildflyDataSourceAttributes.USER_NAME, dataSourceDef.getUser() );
        changeSet.put( WildflyDataSourceAttributes.PASSWORD, dataSourceDef.getPassword() );

        updateDatasource( dataSourceDef.getName(), changeSet );
    }

    private void updateDatasource( String name, Map<String, Object> changeSet ) throws Exception {

        //note: in order to update a datasource it should first be disabled.

        //The operation is a composite operation of multiple attribute changes.
        ModelNode operation = new ModelNode();
        operation.get( OP ).set( COMPOSITE );
        operation.get( OP_ADDR ).setEmptyList();

        //Use a template for copying the datasource address
        ModelNode stepTemplate = new ModelNode();
        stepTemplate.get( OP ).set( "write-attribute" );
        if ( profile != null ) {
            stepTemplate.get( OP_ADDR ).add( "profile", profile );
        }
        stepTemplate.get( OP_ADDR ).add( "subsystem", "datasources" );
        stepTemplate.get( OP_ADDR ).add( "data-source", name );

        ModelNode step;
        ModelNode stepValue;
        List<ModelNode> steps = new ArrayList<>();
        Object value;

        for ( String attrName : changeSet.keySet() ) {

            value = changeSet.get( attrName );
            if ( value == null ) {
                continue;
            }
            step = stepTemplate.clone();
            step.get( NAME ).set( attrName );
            stepValue = step.get( "value" );
            stepValue.set( value.toString() );

            steps.add( step );
        }

        operation.get( STEPS ).set( steps );

        ModelControllerClient client = createControllerClient();
        ModelNode response = client.execute( new OperationBuilder( operation ).build() );

        safeClose( client );
        checkResponse( response );
    }

    /**
     * Gets the definitions of the currently available data sources.
     */
    public List<WildflyDataSourceDef> getDataSources() throws Exception {

        List<WildflyDataSourceDef> dataSources = new ArrayList<>( );
        WildflyDataSourceDef dataSource;
        ModelNode response = null;
        ModelControllerClient client = null;

        try {
            client = createControllerClient();
            ModelNode operation = new ModelNode();

            ////profile=full/subsystem=datasources:read-children-resources(child-type=data-source)
            ///subsystem=datasources:read-children-resources(child-type=data-source)
            operation.get( OP ).set( "read-children-resources" );
            operation.get( "child-type" ).set( "data-source" );
            if ( profile != null ) {
                operation.get( OP_ADDR ).add( "profile", profile );
            }
            operation.get( OP_ADDR ).add( "subsystem", "datasources" );

            response = client.execute( new OperationBuilder( operation ).build() );
            if ( !isFailure( response ) ) {
                if ( response.hasDefined( RESULT ) ) {
                    List<ModelNode> nodes = response.get( RESULT ).asList();
                    Property property;
                    ModelNode node;
                    for ( ModelNode resultNode : nodes ) {
                        property = resultNode.asProperty();
                        node = property.getValue();
                        dataSource = new WildflyDataSourceDef();

                        dataSource.setName( property.getName() );
                        dataSource.setJndi( node.get( WildflyDataSourceAttributes.JNDI_NAME ).asString() );
                        dataSource.setConnectionURL( node.get( WildflyDataSourceAttributes.CONNECTION_URL ).asString() );
                        dataSource.setDriverName( node.get( WildflyDataSourceAttributes.DRIVER_NAME ).asString() );
                        dataSource.setDriverClass( node.get( WildflyDataSourceAttributes.DRIVER_CLASS ).asString() );
                        dataSource.setDataSourceClass( node.get( WildflyDataSourceAttributes.DATASOURCE_CLASS ).asString() );
                        dataSource.setUser( node.get( WildflyDataSourceAttributes.USER_NAME ).asString() );
                        dataSource.setPassword( node.get( WildflyDataSourceAttributes.PASSWORD ).asString() );
                        dataSource.setUseJTA( node.get( WildflyDataSourceAttributes.JTA ).asBoolean() );
                        dataSource.setUseCCM( node.get( WildflyDataSourceAttributes.USE_CCM ).asBoolean() );

                        dataSources.add( dataSource );
                    }
                }
            }
        } finally {
            safeClose( client );
            checkResponse( response );
        }

        return dataSources;
    }

    /**
     * Enables/Disables a data source.
     *
     * @param name The data source name.
     *
     * @param enable true: enables the data source, false: disables the data source.
     * @throws Exception
     */
    public void enableDataSource( String name, boolean enable ) throws Exception {

        final String opName = enable ? "enable" : "disable";

        ModelNode operation = new ModelNode( );
        operation.get( OP ).set( opName );
        if ( profile != null ) {
            operation.get( OP_ADDR ).add( "profile", profile );
        }
        operation.get( OP_ADDR ).add( "subsystem", "datasources");
        operation.get( OP_ADDR ).add( "data-source", name );

        if ( ! enable  ) {
            operation.get( OPERATION_HEADERS ).get( "allow-resource-service-restart" ).set( true );
        }

        ModelControllerClient client = createControllerClient();
        ModelNode result = client.execute( operation );
        safeClose( client );
        checkResponse( result );

    }

    /**
     * Deletes an existing data source.
     *
     * @param name the data source name.
     *
     * @throws Exception when the operation fails.
     */
    public void deleteDataSource( String name ) throws Exception {

        ModelNode operation = new ModelNode( );
        operation.get( OP ).set( "remove" );
        if ( profile != null ) {
            operation.get( OP_ADDR ).add( "profile", profile );
        }
        operation.get( OP_ADDR ).add( "subsystem", "datasources" );
        operation.get( OP_ADDR ).add( "data-source", name );

        ModelControllerClient client = createControllerClient();
        ModelNode result = client.execute( operation );
        safeClose( client );
        checkResponse( result );
    }
}