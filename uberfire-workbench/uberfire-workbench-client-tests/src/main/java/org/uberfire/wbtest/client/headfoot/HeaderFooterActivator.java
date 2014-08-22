package org.uberfire.wbtest.client.headfoot;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.container.BeanActivator;

import com.google.gwt.user.client.Window.Location;

/**
 * Bean activator that disables the header and footer beans if the request contains the parameter
 * disableHeadersAndFooters=true.
 */
@ApplicationScoped
public class HeaderFooterActivator implements BeanActivator {

    public static final String DISABLE_PARAM = "disableHeadersAndFooters";

    @Override
    public boolean isActivated() {
        String disabled = Location.getParameter( DISABLE_PARAM );
        if ( disabled == null ) {
            return true;
        }
        return !Boolean.valueOf( disabled );
    }

}
