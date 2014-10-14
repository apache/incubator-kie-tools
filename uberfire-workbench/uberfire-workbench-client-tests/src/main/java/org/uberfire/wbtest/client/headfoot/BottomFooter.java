package org.uberfire.wbtest.client.headfoot;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ActivatedBy;
import org.uberfire.client.workbench.Footer;
import org.uberfire.wbtest.client.api.UncaughtExceptionAlerter;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

@ApplicationScoped
@ActivatedBy( HeaderFooterActivator.class )
public class BottomFooter implements Footer {

    private final HorizontalPanel panel = new HorizontalPanel();
    private final Label label = new Label( "This is the bottom footer (order=5)" );

    @Inject
    private UncaughtExceptionAlerter uncaughtExceptionAlerter;

    @PostConstruct
    private void setup() {
        panel.add( label );
        panel.add( uncaughtExceptionAlerter );
    }

    @Override
    public String getId() {
        return getClass().getName();
    }

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

}
