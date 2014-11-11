package org.uberfire.ext.apps.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface WebAppResource
        extends
        ClientBundle {

    WebAppResource INSTANCE = GWT.create( WebAppResource.class );

    @Source("css/apps.css")
    AppsCss CSS();

}
