package org.kie.workbench.common.screens.server.management.client.widget.card.body;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.server.api.model.Message;
import org.kie.workbench.common.screens.server.management.client.widget.card.body.notification.NotificationPresenter;
import org.uberfire.client.mvp.UberView;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class BodyPresenter {

    public interface View extends UberView<BodyPresenter> {

        void addNotification( IsWidget view );

        void clear();
    }

    private final View view;

    @Inject
    public BodyPresenter( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public View getView() {
        return view;
    }

    public void setup( final Collection<Message> messages ) {
        checkNotNull( "messages", messages );

        if ( messages.isEmpty() ) {
            view.addNotification( setupNotification( null ).getView() );
        } else {
            for ( final Message message : messages ) {
                view.addNotification( setupNotification( message ).getView() );
            }
        }
    }

    NotificationPresenter setupNotification( final Message message ) {
        final NotificationPresenter presenter = newNotification();
        if (message == null){
            presenter.setupOk();
        } else {
            presenter.setup( message );
        }
        return presenter;
    }

    NotificationPresenter newNotification() {
        return IOC.getBeanManager().lookupBean( NotificationPresenter.class ).getInstance();
    }

}
