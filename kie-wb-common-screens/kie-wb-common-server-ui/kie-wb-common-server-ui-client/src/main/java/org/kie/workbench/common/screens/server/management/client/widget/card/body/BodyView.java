package org.kie.workbench.common.screens.server.management.client.widget.card.body;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class BodyView extends Composite
        implements BodyPresenter.View {

    private BodyPresenter presenter;

    @Inject
    @DataField("notifications")
    Paragraph notifications;

    @Override
    public void init( final BodyPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void addNotification( final IsWidget widget ) {
        notifications.add( widget );
    }

    @Override
    public void clear() {
        notifications.clear();
    }

}
