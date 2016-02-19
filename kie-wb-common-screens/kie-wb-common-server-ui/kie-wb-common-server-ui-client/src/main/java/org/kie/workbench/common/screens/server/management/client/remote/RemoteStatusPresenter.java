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

package org.kie.workbench.common.screens.server.management.client.remote;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.workbench.common.screens.server.management.client.remote.card.ContainerCardPresenter;

@Dependent
public class RemoteStatusPresenter {

    public interface View extends IsWidget {

        void addCard( final IsWidget content );

        void clear();
    }

    private final View view;

    @Inject
    public RemoteStatusPresenter( final View view ) {
        this.view = view;
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
            ContainerCardPresenter newCard = newCard();
            newCard.setup( container );
            view.addCard( newCard.getView().asWidget() );
        }

    }

    ContainerCardPresenter newCard() {
        return IOC.getBeanManager().lookupBean( ContainerCardPresenter.class ).getInstance();
    }
}
