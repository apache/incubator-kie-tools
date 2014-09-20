package org.kie.uberfire.plugin.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface WebAppResource
        extends
        ClientBundle {

    WebAppResource INSTANCE = GWT.create( WebAppResource.class );

    @Source("css/plugin.css")
    PluginCss CSS();

    @Source("images/handle-v.png")
    DataResource handleVertical();

    @Source("images/handle-h.png")
    DataResource handleHorizontal();

}
