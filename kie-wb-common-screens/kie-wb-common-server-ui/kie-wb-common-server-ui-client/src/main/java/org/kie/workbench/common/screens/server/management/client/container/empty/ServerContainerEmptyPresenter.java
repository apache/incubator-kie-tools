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

package org.kie.workbench.common.screens.server.management.client.container.empty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.client.events.AddNewContainer;
import org.uberfire.client.mvp.UberView;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class ServerContainerEmptyPresenter {

    public interface View extends UberView<ServerContainerEmptyPresenter> {

        void setTemplateName( String templateName );
    }

    private final View view;

    private final Event<AddNewContainer> addNewContainerEvent;

    private ServerTemplate serverTemplate;

    @Inject
    public ServerContainerEmptyPresenter( final View view,
                                          final Event<AddNewContainer> addNewContainerEvent ) {
        this.view = view;
        this.addNewContainerEvent = addNewContainerEvent;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public View getView() {
        return view;
    }

    public void setTemplate( final ServerTemplate serverTemplate ) {
        this.serverTemplate = checkNotNull( "serverTemplate", serverTemplate );
        view.setTemplateName( serverTemplate.getName() );
    }

    public void addContainer() {
        addNewContainerEvent.fire( new AddNewContainer( serverTemplate ) );
    }

}
