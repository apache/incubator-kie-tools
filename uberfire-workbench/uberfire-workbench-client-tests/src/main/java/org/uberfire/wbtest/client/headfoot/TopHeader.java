package org.uberfire.wbtest.client.headfoot;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.api.ActivatedBy;
import org.uberfire.client.workbench.Header;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

@ApplicationScoped
@ActivatedBy( HeaderFooterActivator.class )
public class TopHeader implements Header {

    private final Label label = new Label( "This is the top header (order=10)" );

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
