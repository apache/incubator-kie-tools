package org.kie.workbench.common.screens.server.management.client.container.status.card;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.html.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.widget.card.CardPresenter;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Templated
@Dependent
public class ContainerCardView extends Composite
        implements ContainerCardPresenter.View {

    @Inject
    @DataField("container")
    Div container;

    @Override
    public void setCard( final CardPresenter.View cardView ) {
        container.add( checkNotNull( "cardView", cardView ).asWidget() );
    }

    @Override
    public void delete() {
        removeFromParent();
    }

    @Override
    public Widget asWidget() {
        return container;
    }
}
