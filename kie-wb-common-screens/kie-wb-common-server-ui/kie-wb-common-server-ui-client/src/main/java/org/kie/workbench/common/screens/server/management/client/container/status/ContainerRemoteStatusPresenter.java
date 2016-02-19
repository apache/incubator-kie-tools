/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
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

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.server.controller.api.model.events.ServerInstanceDeleted;
import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.workbench.common.screens.server.management.client.container.status.card.ContainerCardPresenter;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class ContainerRemoteStatusPresenter {

    public interface View extends IsWidget {

        void addCard( final IsWidget content );

        void clear();
    }

    private final View view;

    private final Map<String, Map<String, ContainerCardPresenter>> index = new HashMap<String, Map<String, ContainerCardPresenter>>();

    private ContainerSpec containerSpec;

    @Inject
    public ContainerRemoteStatusPresenter( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
    }

    public View getView() {
        return view;
    }

    public void onServerInstanceUpdated( @Observes final ServerInstanceUpdated serverInstanceUpdated ) {
        checkNotNull( "serverInstanceUpdated", serverInstanceUpdated );
        final String updatedServerInstanceKey = serverInstanceUpdated.getServerInstance().getServerInstanceId();
        if ( index.containsKey( updatedServerInstanceKey ) ) {
            final Map<String, ContainerCardPresenter> oldIndex = new HashMap<String, ContainerCardPresenter>( index.remove( updatedServerInstanceKey ) );
            final Map<String, ContainerCardPresenter> newIndexIndex = new HashMap<String, ContainerCardPresenter>( serverInstanceUpdated.getServerInstance().getContainers().size() );
            index.put( updatedServerInstanceKey, newIndexIndex );
            for ( final Container container : serverInstanceUpdated.getServerInstance().getContainers() ) {
                final ContainerCardPresenter presenter = oldIndex.remove( container.getContainerName() );
                if ( presenter != null ) {
                    presenter.updateContent( serverInstanceUpdated.getServerInstance(), container );
                }
                newIndexIndex.put( container.getContainerName(), presenter );
            }
            for ( final ContainerCardPresenter presenter : oldIndex.values() ) {
                presenter.delete();
            }
        } else {
            for ( final Container container : serverInstanceUpdated.getServerInstance().getContainers() ) {
                if ( container.getServerTemplateId().equals( containerSpec.getServerTemplateKey().getId() ) &&
                        container.getContainerSpecId().equals( containerSpec.getId() ) ) {
                    buildContainer( container );
                }
            }
        }
    }

    public void onDelete( @Observes final ServerInstanceDeleted serverInstanceDeleted ) {
        checkNotNull( "serverInstanceDeleted", serverInstanceDeleted );
        final String deletedServerInstanceId = serverInstanceDeleted.getServerInstanceId();
        if ( index.containsKey( deletedServerInstanceId ) ) {
            final Map<String, ContainerCardPresenter> oldIndex = index.remove( deletedServerInstanceId );
            if ( oldIndex != null ) {
                for ( final ContainerCardPresenter presenter : oldIndex.values() ) {
                    presenter.delete();
                }
            }
        }
    }

    public void setup( final ContainerSpec containerSpec,
                       final Collection<Container> containers ) {
        this.containerSpec = containerSpec;
        this.view.clear();
        for ( Container container : containers ) {
            buildContainer( container );
        }
    }

    private void buildContainer( final Container container ) {
        final ContainerCardPresenter cardPresenter = newCard();
        index( container, cardPresenter );
        cardPresenter.setup( container.getServerInstanceKey(), container );
        view.addCard( cardPresenter.getView().asWidget() );
    }

    private void index( final Container container,
                        final ContainerCardPresenter cardPresenter ) {
        if ( !index.containsKey( container.getServerInstanceKey().getServerInstanceId() ) ) {
            index.put( container.getServerInstanceKey().getServerInstanceId(), new HashMap<String, ContainerCardPresenter>() );
        }
        index.get( container.getServerInstanceKey().getServerInstanceId() ).put( container.getContainerName(), cardPresenter );
    }

    ContainerCardPresenter newCard() {
        return IOC.getBeanManager().lookupBean( ContainerCardPresenter.class ).getInstance();
    }

}