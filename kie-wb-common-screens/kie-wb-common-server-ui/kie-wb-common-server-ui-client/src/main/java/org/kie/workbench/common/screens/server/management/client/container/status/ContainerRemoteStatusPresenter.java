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

package org.kie.workbench.common.screens.server.management.client.container.status;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.controller.api.model.events.ServerInstanceDeleted;
import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.workbench.common.screens.server.management.client.container.status.card.ContainerCardPresenter;
import org.slf4j.Logger;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
public class ContainerRemoteStatusPresenter {

    public interface View extends IsWidget {

        void addCard( final IsWidget content );

        void clear();
    }

    private final Logger logger;
    private final View view;
    private final ManagedInstance<ContainerCardPresenter> cardPresenterProvider;

    private final Map<String, Map<String, ContainerCardPresenter>> index = new HashMap<String, Map<String, ContainerCardPresenter>>();

    private ContainerSpec containerSpec;

    @Inject
    public ContainerRemoteStatusPresenter( final Logger logger,
                                           final View view,
                                           final ManagedInstance<ContainerCardPresenter> cardPresenterProvider ) {
        this.logger = logger;
        this.view = view;
        this.cardPresenterProvider = cardPresenterProvider;
    }

    @PostConstruct
    public void init() {
    }

    public View getView() {
        return view;
    }

    public void onServerInstanceUpdated( @Observes final ServerInstanceUpdated serverInstanceUpdated ) {
        if ( serverInstanceUpdated != null &&
                serverInstanceUpdated.getServerInstance() != null ) {
            final String updatedServerInstanceKey = serverInstanceUpdated.getServerInstance().getServerInstanceId();
            if ( index.containsKey( updatedServerInstanceKey ) || index.isEmpty() ) {
                final Map<String, ContainerCardPresenter> oldIndex = index.isEmpty() ?
                        new HashMap<String, ContainerCardPresenter>() :
                        new HashMap<String, ContainerCardPresenter>( index.remove( updatedServerInstanceKey ) );
                final Map<String, ContainerCardPresenter> newIndexIndex = new HashMap<String, ContainerCardPresenter>( serverInstanceUpdated.getServerInstance().getContainers().size() );
                index.put( updatedServerInstanceKey, newIndexIndex );
                for ( final Container container : serverInstanceUpdated.getServerInstance().getContainers() ) {
                    ContainerCardPresenter presenter = oldIndex.remove( container.getContainerSpecId() );
                    if ( !container.getStatus().equals( KieContainerStatus.STOPPED ) ) {
                        if ( presenter != null ) {
                            presenter.updateContent(serverInstanceUpdated.getServerInstance(),
                                                    container );
                        } else {
                            presenter = buildContainer( container, false );
                        }
                        newIndexIndex.put( container.getContainerName(), presenter );
                    }
                }
                for ( final ContainerCardPresenter presenter : oldIndex.values() ) {
                    presenter.delete();
                }
            } else {
                for ( final Container container : serverInstanceUpdated.getServerInstance().getContainers() ) {
                    if ( container.getServerTemplateId().equals( containerSpec.getServerTemplateKey().getId() ) &&
                            container.getContainerSpecId().equals( containerSpec.getId() ) ) {
                        buildAndIndexContainer( container );
                    }
                }
            }
        } else {
            logger.warn( "Illegal event argument." );
        }
    }

    public void onDelete( @Observes final ServerInstanceDeleted serverInstanceDeleted ) {
        if ( serverInstanceDeleted != null &&
                serverInstanceDeleted.getServerInstanceId() != null ) {
            final String deletedServerInstanceId = serverInstanceDeleted.getServerInstanceId();
            if ( index.containsKey( deletedServerInstanceId ) ) {
                final Map<String, ContainerCardPresenter> oldIndex = index.remove( deletedServerInstanceId );
                if ( oldIndex != null ) {
                    for ( final ContainerCardPresenter presenter : oldIndex.values() ) {
                        presenter.delete();
                    }
                }
            }
        } else {
            logger.warn( "Illegal event argument." );
        }
    }

    public void setup( final ContainerSpec containerSpec,
                       final Collection<Container> containers ) {
        this.containerSpec = containerSpec;
        this.view.clear();
        for ( Container container : containers ) {
            if ( !container.getStatus().equals( KieContainerStatus.STOPPED ) ) {
                buildAndIndexContainer( container );
            }
        }
    }

    private void buildAndIndexContainer( final Container container ) {
        index( container, buildContainer( container, true ) );
    }

    private ContainerCardPresenter buildContainer( final Container container, boolean addCard ) {
        final ContainerCardPresenter cardPresenter = cardPresenterProvider.get();
        cardPresenter.setup( container.getServerInstanceKey(), container );
        if(addCard) {
            view.addCard( cardPresenter.getView().asWidget() );
        }
        return cardPresenter;
    }

    private void index( final Container container,
                        final ContainerCardPresenter cardPresenter ) {
        if ( !index.containsKey( container.getServerInstanceKey().getServerInstanceId() ) ) {
            index.put( container.getServerInstanceKey().getServerInstanceId(), new HashMap<String, ContainerCardPresenter>() );
        }
        index.get( container.getServerInstanceKey().getServerInstanceId() ).put( container.getContainerSpecId(), cardPresenter );
    }

}