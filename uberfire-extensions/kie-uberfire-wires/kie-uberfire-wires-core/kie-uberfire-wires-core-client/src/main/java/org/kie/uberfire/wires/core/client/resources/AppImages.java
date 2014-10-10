package org.kie.uberfire.wires.core.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * GWT managed images for Workbench
 */
public interface AppImages extends ClientBundle {

    @Source("org/kie/uberfire/wires/core/public/images/wires-user-logo.png")
    ImageResource wiresUserLogo();
    
    @Source("org/kie/uberfire/wires/core/public/images/layerPanel/delete.png")
    public ImageResource delete();
    
    @Source("org/kie/uberfire/wires/core/public/images/layerPanel/view.png")
    public ImageResource view();
    
    @Source("org/kie/uberfire/wires/core/public/images/layerPanel/clear.png")
    public ImageResource clear();

}
