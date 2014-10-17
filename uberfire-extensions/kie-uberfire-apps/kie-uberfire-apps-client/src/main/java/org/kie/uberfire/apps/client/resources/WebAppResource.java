package org.kie.uberfire.apps.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface WebAppResource
        extends
        ClientBundle {

    WebAppResource INSTANCE = GWT.create( WebAppResource.class );

    @Source("css/apps.css")
    AppsCss CSS();

}
