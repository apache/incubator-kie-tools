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

package org.kie.workbench.common.screens.server.management.client.navigation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.server.controller.api.model.events.ServerTemplateUpdated;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.server.management.client.events.AddNewServerTemplate;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateListRefresh;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateSelected;
import org.uberfire.client.mvp.UberView;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@ApplicationScoped
public class ServerNavigationPresenter {

    public interface View extends UberView<ServerNavigationPresenter> {

        void addTemplate( final String id,
                          final String name );

        void select( final String id );

        void clean();
    }

    private final View view;

    private final Event<AddNewServerTemplate> addNewServerTemplateEvent;
    private final Event<ServerTemplateListRefresh> serverTemplateListRefreshEvent;
    private final Event<ServerTemplateSelected> serverTemplateSelectedEvent;

    private Set<String> serverTemplates = new HashSet<String>();

    @Inject
    public ServerNavigationPresenter( final View view,
                                      final Event<AddNewServerTemplate> addNewServerTemplateEvent,
                                      final Event<ServerTemplateListRefresh> serverTemplateListRefreshEvent,
                                      final Event<ServerTemplateSelected> serverTemplateSelectedEvent ) {
        this.view = view;
        this.addNewServerTemplateEvent = addNewServerTemplateEvent;
        this.serverTemplateListRefreshEvent = serverTemplateListRefreshEvent;
        this.serverTemplateSelectedEvent = serverTemplateSelectedEvent;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public View getView() {
        return view;
    }

    public void setup( final ServerTemplateKey firstTemplate,
                       final Collection<ServerTemplateKey> serverTemplateKeys ) {
        view.clean();
        serverTemplates.clear();
        addTemplate( checkNotNull( "serverTemplate2BeSelected", firstTemplate ) );
        for ( final ServerTemplateKey serverTemplateKey : serverTemplateKeys ) {
            if ( !serverTemplateKey.equals( firstTemplate ) ) {
                addTemplate( serverTemplateKey );
            }
        }
    }

    private void addTemplate( final ServerTemplateKey serverTemplateKey ) {
        checkNotNull( "serverTemplateKey", serverTemplateKey );
        serverTemplates.add( serverTemplateKey.getId() );
        this.view.addTemplate( serverTemplateKey.getId(), serverTemplateKey.getName() );
    }

    public void onSelect( @Observes final ServerTemplateSelected serverTemplateSelected ) {
        checkNotNull( "serverTemplateSelected", serverTemplateSelected );
        view.select( serverTemplateSelected.getServerTemplateKey().getId() );
    }

    public void onServerTemplateUpdated( @Observes final ServerTemplateUpdated serverTemplateUpdated ) {
        final ServerTemplate serverTemplate = checkNotNull( "serverTemplateUpdated", serverTemplateUpdated ).getServerTemplate();
        if ( !serverTemplates.contains( serverTemplate.getId() ) ) {
            addTemplate( serverTemplate );
        }
    }

    public void select( final String id ) {
        serverTemplateSelectedEvent.fire( new ServerTemplateSelected( new ServerTemplateKey( id, "" ) ) );
    }

    public void clear() {
        view.clean();
    }

    public void refresh() {
        serverTemplateListRefreshEvent.fire( new ServerTemplateListRefresh() );
    }

    public void newTemplate() {
        addNewServerTemplateEvent.fire( new AddNewServerTemplate() );
    }

}
