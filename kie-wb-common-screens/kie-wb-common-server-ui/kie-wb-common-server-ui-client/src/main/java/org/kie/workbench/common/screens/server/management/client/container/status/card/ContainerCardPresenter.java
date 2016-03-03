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

package org.kie.workbench.common.screens.server.management.client.container.status.card;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.workbench.common.screens.server.management.client.events.ServerInstanceSelected;
import org.kie.workbench.common.screens.server.management.client.util.IOCUtil;
import org.kie.workbench.common.screens.server.management.client.widget.card.CardPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.body.BodyPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.footer.FooterPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.title.LinkTitlePresenter;
import org.uberfire.mvp.Command;

@Dependent
public class ContainerCardPresenter {

    public interface View extends IsWidget {

        void setCard( CardPresenter.View card );

        void delete();
    }

    private final View view;
    private final IOCUtil iocUtil;

    private final Event<ServerInstanceSelected> remoteServerSelectedEvent;

    private LinkTitlePresenter linkTitlePresenter;
    private BodyPresenter bodyPresenter;
    private FooterPresenter footerPresenter;

    @Inject
    public ContainerCardPresenter( final View view,
                                   final IOCUtil iocUtil,
                                   final Event<ServerInstanceSelected> remoteServerSelectedEvent ) {
        this.view = view;
        this.iocUtil = iocUtil;
        this.remoteServerSelectedEvent = remoteServerSelectedEvent;
    }

    public View getView() {
        return view;
    }

    public void setup( final ServerInstanceKey serverInstanceKey,
                       final Container container ) {
        linkTitlePresenter = iocUtil.newInstance( this, LinkTitlePresenter.class );
        bodyPresenter = iocUtil.newInstance( this, BodyPresenter.class );
        footerPresenter = iocUtil.newInstance( this, FooterPresenter.class );

        updateContent( serverInstanceKey, container );

        final CardPresenter card = iocUtil.newInstance( this, CardPresenter.class );
        card.addTitle( linkTitlePresenter );
        card.addBody( bodyPresenter );
        card.addFooter( footerPresenter );

        view.setCard( card.getView() );
    }

    public void delete() {
        view.delete();
    }

    public void updateContent( final ServerInstanceKey serverInstanceKey,
                               final Container container ) {
        linkTitlePresenter.setup( serverInstanceKey.getServerName(),
                                  new Command() {
                                      @Override
                                      public void execute() {
                                          remoteServerSelectedEvent.fire( new ServerInstanceSelected( serverInstanceKey ) );
                                      }
                                  } );
        bodyPresenter.setup( container.getMessages() );
        footerPresenter.setup( container.getUrl(), container.getResolvedReleasedId().getVersion() );
    }

    @PreDestroy
    public void destroy() {
        iocUtil.cleanup( this );
    }

}
