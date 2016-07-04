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

package org.kie.workbench.common.screens.server.management.client.remote;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.workbench.common.screens.server.management.client.remote.card.ContainerCardPresenter;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
public class RemoteStatusPresenter {

    public interface View extends IsWidget {

        void addCard( final IsWidget content );

        void clear();
    }

    private final View view;
    private final ManagedInstance<ContainerCardPresenter> presenterProvider;

    @Inject
    public RemoteStatusPresenter( final View view,
                                  final ManagedInstance<ContainerCardPresenter> presenterProvider ) {
        this.view = view;
        this.presenterProvider = presenterProvider;
    }

    @PostConstruct
    public void init() {
    }

    public View getView() {
        return view;
    }

    public void setup( final Collection<Container> containers ) {
        view.clear();
        for ( final Container container : containers ) {
            final ContainerCardPresenter newCard = presenterProvider.get();
            newCard.setup( container );
            view.addCard( newCard.getView().asWidget() );
        }
    }

}
