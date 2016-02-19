package org.kie.workbench.common.screens.server.management.client.remote.card;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOC;
import org.kie.server.api.model.Message;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.server.management.client.events.ContainerSpecSelected;
import org.kie.workbench.common.screens.server.management.client.widget.card.CardPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.body.BodyPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.footer.FooterPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.title.InfoTitlePresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.title.LinkTitlePresenter;
import org.uberfire.mvp.Command;

@Dependent
public class ContainerCardPresenter {

    private final org.kie.workbench.common.screens.server.management.client.container.status.card.ContainerCardPresenter.View view;

    private final Event<ContainerSpecSelected> containerSpecSelectedEvent;

    @Inject
    public ContainerCardPresenter( final org.kie.workbench.common.screens.server.management.client.container.status.card.ContainerCardPresenter.View view,
                                   final Event<ContainerSpecSelected> containerSpecSelectedEvent ) {
        this.view = view;
        this.containerSpecSelectedEvent = containerSpecSelectedEvent;
    }

    public org.kie.workbench.common.screens.server.management.client.container.status.card.ContainerCardPresenter.View getView() {
        return view;
    }

    public void setup( final Container container ) {
        final LinkTitlePresenter linkTitlePresenter = newTitle();
        linkTitlePresenter.setup( container.getContainerName(),
                                  new Command() {
                                      @Override
                                      public void execute() {
                                          containerSpecSelectedEvent.fire( new ContainerSpecSelected( buildContainerSpecKey( container ) ) );
                                      }
                                  } );

        final InfoTitlePresenter infoTitlePresenter = newInfoTitle();
        infoTitlePresenter.setup( container.getResolvedReleasedId() );

        final BodyPresenter bodyPresenter = newBody();
        final List<Message> collection = new ArrayList<Message>( container.getMessages() );
        bodyPresenter.setup( collection.get( collection.size() - 1 ) );

        final FooterPresenter footerPresenter = newFooter();
        footerPresenter.setup( container.getUrl(), container.getResolvedReleasedId().getVersion() );

        CardPresenter card = newCard();
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

    CardPresenter newCard() {
        return IOC.getBeanManager().lookupBean( CardPresenter.class ).getInstance();
    }

    LinkTitlePresenter newTitle() {
        return IOC.getBeanManager().lookupBean( LinkTitlePresenter.class ).getInstance();
    }

    InfoTitlePresenter newInfoTitle() {
        return IOC.getBeanManager().lookupBean( InfoTitlePresenter.class ).getInstance();
    }

    BodyPresenter newBody() {
        return IOC.getBeanManager().lookupBean( BodyPresenter.class ).getInstance();
    }

    FooterPresenter newFooter() {
        return IOC.getBeanManager().lookupBean( FooterPresenter.class ).getInstance();
    }

}
