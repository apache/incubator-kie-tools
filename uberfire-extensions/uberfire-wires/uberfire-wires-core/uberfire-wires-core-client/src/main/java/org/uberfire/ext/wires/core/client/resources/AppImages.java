package org.uberfire.ext.wires.core.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * GWT managed images for Workbench
 */
public interface AppImages extends ClientBundle {

    @Source("wires-user-logo.png")
    ImageResource wiresUserLogo();

    @Source("layerPanel/delete.png")
    ImageResource delete();

    @Source("layerPanel/view.png")
    ImageResource view();

    @Source("layerPanel/clear.png")
    ImageResource clear();

}
