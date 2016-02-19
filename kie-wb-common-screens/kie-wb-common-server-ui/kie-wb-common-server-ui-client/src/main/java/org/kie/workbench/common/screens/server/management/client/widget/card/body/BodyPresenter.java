package org.kie.workbench.common.screens.server.management.client.widget.card.body;

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

    public void setup( final Message message ) {
        checkNotNull( "message", message );

        configNotification( message );
    }

    private void configNotification( final Message message ) {
        final NotificationPresenter presenter = newNotification();
        presenter.setup( message );

        view.addNotification( presenter.getView() );
    }

    NotificationPresenter newNotification() {
        return IOC.getBeanManager().lookupBean( NotificationPresenter.class ).getInstance();
    }

}
