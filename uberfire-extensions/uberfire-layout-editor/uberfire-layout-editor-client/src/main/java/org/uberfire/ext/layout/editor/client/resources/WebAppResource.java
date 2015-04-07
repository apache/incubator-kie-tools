package org.uberfire.ext.layout.editor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface WebAppResource
        extends
        ClientBundle {

    WebAppResource INSTANCE = GWT.create( WebAppResource.class );

    @Source("css/layout-editor.css")
    LayoutEditorCss CSS();

}
