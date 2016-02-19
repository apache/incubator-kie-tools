package org.kie.workbench.common.screens.server.management.client.widget.card.body.notification;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Popover;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.gwtbootstrap3.client.ui.constants.Trigger;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Templated
@Dependent
public class NotificationView extends Composite
        implements NotificationPresenter.View {

    private TranslationService translationService;

    @Inject
    @DataField
    Anchor size;

    @Inject
    @DataField
    Span icon;

    @Inject
    public NotificationView( final TranslationService translationService ) {
        super();
        this.translationService = translationService;
    }

    @Override
    public void setupOk() {
        icon.addStyleName( NotificationType.OK.getStyleName() );
        size.setVisible( false );
    }

    @Override
    public void setup( final NotificationType type,
                       final String size ) {
        icon.addStyleName( checkNotNull( "type", type ).getStyleName() );
        this.size.setVisible( true );
        this.size.setText( size );
    }

    @Override
    public void setup( final NotificationType type,
                       final String size,
                       final String popOverMessage ) {
        setup( type, size );
        final Widget parent = this.size.getParent();
        final Popover popover = new Popover( this.size );
        popover.setTrigger( Trigger.CLICK );
        popover.setPlacement( Placement.RIGHT );
        popover.setTitle( getTitleText() );
        popover.setContent( popOverMessage );
        parent.getElement().insertAfter( popover.asWidget().getElement(), icon.getElement() );
        popover.reconfigure();
    }

    private String getTitleText() {
        return translationService.format( Constants.NotificationView_TitleText );
    }
}
