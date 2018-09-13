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

package org.kie.workbench.common.screens.server.management.client.empty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.screens.server.management.client.events.AddNewServerTemplate;
import org.uberfire.client.mvp.UberView;

@Dependent
public class ServerEmptyPresenter {

    public interface View extends UberView<ServerEmptyPresenter> {

    }

    private final View view;

    private final Event<AddNewServerTemplate> addNewServerTemplateEvent;

    @Inject
    public ServerEmptyPresenter( final View view,
                                 final Event<AddNewServerTemplate> addNewServerTemplateEvent ) {
        this.view = view;
        this.addNewServerTemplateEvent = addNewServerTemplateEvent;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public View getView() {
        return view;
    }

    public void addTemplate() {
        addNewServerTemplateEvent.fire( new AddNewServerTemplate() );
    }

}
