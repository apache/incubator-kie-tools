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

package org.kie.workbench.common.screens.server.management.client.remote.card;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.server.management.client.events.ContainerSpecSelected;
import org.kie.workbench.common.screens.server.management.client.util.IOCUtil;
import org.kie.workbench.common.screens.server.management.client.widget.card.CardPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.body.BodyPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.footer.FooterPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.title.InfoTitlePresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.title.LinkTitlePresenter;
import org.uberfire.mvp.Command;

@Dependent
public class ContainerCardPresenter {

    private final IOCUtil iocUtil;
    private final org.kie.workbench.common.screens.server.management.client.container.status.card.ContainerCardPresenter.View view;
    private final Event<ContainerSpecSelected> containerSpecSelectedEvent;

    @Inject
    public ContainerCardPresenter( final IOCUtil iocUtil,
                                   final org.kie.workbench.common.screens.server.management.client.container.status.card.ContainerCardPresenter.View view,
                                   final Event<ContainerSpecSelected> containerSpecSelectedEvent ) {
        this.iocUtil = iocUtil;
        this.view = view;
        this.containerSpecSelectedEvent = containerSpecSelectedEvent;
    }

    public org.kie.workbench.common.screens.server.management.client.container.status.card.ContainerCardPresenter.View getView() {
        return view;
    }

    public void setup( final Container container ) {
        final LinkTitlePresenter linkTitlePresenter = iocUtil.newInstance( this, LinkTitlePresenter.class );
        linkTitlePresenter.setup( container.getContainerName(),
                                  new Command() {
                                      @Override
                                      public void execute() {
                                          containerSpecSelectedEvent.fire( new ContainerSpecSelected( buildContainerSpecKey( container ) ) );
                                      }
                                  } );

        final InfoTitlePresenter infoTitlePresenter = iocUtil.newInstance( this, InfoTitlePresenter.class );
        infoTitlePresenter.setup( container.getResolvedReleasedId() );

        final BodyPresenter bodyPresenter = iocUtil.newInstance( this, BodyPresenter.class );
        bodyPresenter.setup( container.getMessages() );

        final FooterPresenter footerPresenter = iocUtil.newInstance( this, FooterPresenter.class );
        footerPresenter.setup( container.getUrl(), container.getResolvedReleasedId().getVersion() );

        CardPresenter card = iocUtil.newInstance( this, CardPresenter.class );
        card.addTitle( linkTitlePresenter );
        card.addTitle( infoTitlePresenter );
        card.addBody( bodyPresenter );
        card.addFooter( footerPresenter );

        view.setCard( card.getView() );
    }

    private ContainerSpecKey buildContainerSpecKey( final Container container ) {
        return new ContainerSpecKey( container.getContainerSpecId(),
                                     container.getContainerName(),
                                     new ServerTemplateKey( container.getServerInstanceKey().getServerTemplateId(), "" ) );

    }

    @PreDestroy
    public void destroy() {
        iocUtil.cleanup( this );
    }
}
