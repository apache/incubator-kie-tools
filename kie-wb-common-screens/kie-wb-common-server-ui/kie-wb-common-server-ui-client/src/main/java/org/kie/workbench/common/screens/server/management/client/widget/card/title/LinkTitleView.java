package org.kie.workbench.common.screens.server.management.client.widget.card.title;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Anchor;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Templated
@Dependent
public class LinkTitleView extends Composite
        implements LinkTitlePresenter.View {

    private LinkTitlePresenter presenter;

    @Inject
    @DataField("link")
    Anchor link;

    @Override
    public void init( final LinkTitlePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setText( final String value ) {
        link.setText( checkNotNull( "value", value ) );
    }

    @EventHandler("link")
    public void onClick( final ClickEvent event ) {
        presenter.onSelect();
    }

}
