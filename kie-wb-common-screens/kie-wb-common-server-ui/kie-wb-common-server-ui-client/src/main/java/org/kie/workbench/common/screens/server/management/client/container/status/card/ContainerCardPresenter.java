package org.kie.workbench.common.screens.server.management.client.container.status.card;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.server.api.model.Message;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.workbench.common.screens.server.management.client.events.ServerInstanceSelected;
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

    private final Event<ServerInstanceSelected> remoteServerSelectedEvent;

    private LinkTitlePresenter linkTitlePresenter;
    private BodyPresenter bodyPresenter;
    private FooterPresenter footerPresenter;

    @Inject
    public ContainerCardPresenter( final View view,
                                   final Event<ServerInstanceSelected> remoteServerSelectedEvent ) {
        this.view = view;
        this.remoteServerSelectedEvent = remoteServerSelectedEvent;
    }

    public View getView() {
        return view;
    }

    public void setup( final ServerInstanceKey serverInstanceKey,
                       final Container container ) {
        linkTitlePresenter = newTitle();
        bodyPresenter = newBody();
        footerPresenter = newFooter();

        updateContent( serverInstanceKey, container );

        CardPresenter card = newCard();
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
        final List<Message> collection = new ArrayList<Message>( container.getMessages() );
        if( collection.size() > 0 ) {
            bodyPresenter.setup( collection.get( collection.size() - 1));
        }
        footerPresenter.setup( container.getUrl(), container.getResolvedReleasedId().getVersion() );
    }

    CardPresenter newCard() {
        return IOC.getBeanManager().lookupBean( CardPresenter.class ).getInstance();
    }

    LinkTitlePresenter newTitle() {
        return IOC.getBeanManager().lookupBean( LinkTitlePresenter.class ).getInstance();
    }

    BodyPresenter newBody() {
        return IOC.getBeanManager().lookupBean( BodyPresenter.class ).getInstance();
    }

    FooterPresenter newFooter() {
        return IOC.getBeanManager().lookupBean( FooterPresenter.class ).getInstance();
    }

}
