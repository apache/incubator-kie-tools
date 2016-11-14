/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.service;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.service.FactoryService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Provides the client side and remote caller for the factory manager and services.
 * If the requested factory is present on client side, it will create the object using it.
 * If the requested factory is not present on client side, whether it's not implemented or the object
 * cannot be created on client, it performs the service calls to the factory service.
 */
@ApplicationScoped
public class ClientFactoryService {

    ClientFactoryManager clientFactoryManager;
    Caller<FactoryService> factoryServiceCaller;

    protected ClientFactoryService() {
        super();
    }

    @Inject
    public ClientFactoryService( final ClientFactoryManager clientFactoryManager,
                                 final Caller<FactoryService> factoryServiceCaller ) {
        this.clientFactoryManager = clientFactoryManager;
        this.factoryServiceCaller = factoryServiceCaller;
    }

    public <T> void newDefinition( final String definitionId,
                                   final ServiceCallback<T> callback ) {
        final T def = clientFactoryManager.newDefinition( definitionId );
        if ( null != def ) {
            callback.onSuccess( def );
        } else {
            factoryServiceCaller.call( new RemoteCallback<T>() {
                @Override
                public void callback( T t ) {
                    callback.onSuccess( t );
                }
            }, ( message, throwable ) -> {
                callback.onError( new ClientRuntimeError( throwable ) );
                return false;
            } ).newDefinition( definitionId );
        }
    }

    public <T> void newDefinition( final Class<T> type,
                                   final ServiceCallback<T> callback ) {
        final T def = clientFactoryManager.newDefinition( type );
        if ( null != def ) {
            callback.onSuccess( def );
        } else {
            factoryServiceCaller.call( new RemoteCallback<T>() {
                @Override
                public void callback( T t ) {
                    callback.onSuccess( t );
                }
            }, ( message, throwable ) -> {
                callback.onError( new ClientRuntimeError( throwable ) );
                return false;
            } ).newDefinition( type );
        }
    }

    public <T> void newElement( final String uuid,
                                final String definitionId,
                                final ServiceCallback<Element> callback ) {
        final Element element = clientFactoryManager.newElement( uuid, definitionId );
        if ( null != element ) {
            callback.onSuccess( element );
        } else {
            factoryServiceCaller.call( new RemoteCallback<Element>() {
                @Override
                public void callback( final Element t ) {
                    callback.onSuccess( t );
                }
            }, ( message, throwable ) -> {
                callback.onError( new ClientRuntimeError( throwable ) );
                return false;
            } ).newElement( uuid, definitionId );
        }

    }

    public <T> void newElement( final String uuid,
                                final Class<T> type,
                                final ServiceCallback<Element> callback ) {
        final Element element = clientFactoryManager.newElement( uuid, type );
        if ( null != element ) {
            callback.onSuccess( element );
        } else {
            factoryServiceCaller.call( new RemoteCallback<Element>() {
                @Override
                public void callback( Element t ) {
                    callback.onSuccess( t );
                }
            }, ( message, throwable ) -> {
                callback.onError( new ClientRuntimeError( throwable ) );
                return false;
            } ).newElement( uuid, type );
        }

    }

    public <M extends Metadata, D extends Diagram> void newDiagram( final String uuid,
                                                                   final String id,
                                                                    final M metadata,
                                                                   final ServiceCallback<D> callback ) {
        final D diagram = clientFactoryManager.newDiagram( uuid, id, metadata );
        if ( null != diagram ) {
            callback.onSuccess( diagram );
        } else {
            factoryServiceCaller.call( new RemoteCallback<D>() {
                @Override
                public void callback( final D d ) {
                    callback.onSuccess( d );
                }
            }, ( message, throwable ) -> {
                callback.onError( new ClientRuntimeError( throwable ) );
                return false;
            } ).newDiagram( uuid, id, metadata );
        }

    }

    public <M extends Metadata, D extends Diagram> void newDiagram( final String uuid,
                                                final Class<?> type,
                                                                    final M metadata,
                                                final ServiceCallback<D> callback ) {
        final D diagram = clientFactoryManager.newDiagram( uuid, type, metadata );
        if ( null != diagram ) {
            callback.onSuccess( diagram );
        } else {
            factoryServiceCaller.call( new RemoteCallback<D>() {
                @Override
                public void callback( final D d ) {
                    callback.onSuccess( d );
                }
            }, ( message, throwable ) -> {
                callback.onError( new ClientRuntimeError( throwable ) );
                return false;
            } ).newDiagram( uuid, type, metadata );
        }
    }

    public ClientFactoryManager getClientFactoryManager() {
        return clientFactoryManager;
    }

}
