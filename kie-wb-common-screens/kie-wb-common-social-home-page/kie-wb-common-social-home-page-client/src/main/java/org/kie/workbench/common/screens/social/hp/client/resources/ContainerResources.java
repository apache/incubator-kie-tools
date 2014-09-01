package org.kie.workbench.common.screens.social.hp.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface ContainerResources extends
                                    ClientBundle {

    ContainerResources INSTANCE = GWT.create( ContainerResources.class );

    @Source("css/container.css")
    ContainerCss CSS();

}
