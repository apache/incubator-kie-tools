package org.uberfire.client.resources;

import com.github.gwtbootstrap.client.ui.config.Configurator;
import com.github.gwtbootstrap.client.ui.resources.Resources;
import com.google.gwt.core.client.GWT;

public class UberFireConfigurator implements Configurator {

    public Resources getResources() {
        return GWT.create( FontAwesomeResources.class );
    }

    public boolean hasResponsiveDesign() {
        return false;
    }
}
