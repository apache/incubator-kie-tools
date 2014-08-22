package org.uberfire.wbtest.client.headfoot;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.api.ActivatedBy;
import org.uberfire.client.workbench.Footer;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

@ApplicationScoped
@ActivatedBy( HeaderFooterActivator.class )
public class TopFooter implements Footer {

    private final Label label = new Label( "This is the top footer (order=10)" );

    @Override
    public String getId() {
        return getClass().getName();
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public Widget asWidget() {
        return label;
    }

}
