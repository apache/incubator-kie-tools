package org.kie.workbench.common.screens.home.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.kie.workbench.common.screens.home.client.resources.css.HomeCss;

/**
 * Resources for the Home Page
 */
public interface HomeResources extends ClientBundle {

    public static final HomeResources INSTANCE = GWT.create( HomeResources.class );

    @Source("css/styles.css")
    HomeCss CSS();

}
