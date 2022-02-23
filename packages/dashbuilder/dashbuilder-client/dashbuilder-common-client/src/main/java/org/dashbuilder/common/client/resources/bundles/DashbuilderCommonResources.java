package org.dashbuilder.common.client.resources.bundles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface DashbuilderCommonResources extends ClientBundle {

    DashbuilderCommonResources INSTANCE = GWT.create(DashbuilderCommonResources.class);
    
    DashbuilderCommonImages IMAGES = GWT.create(DashbuilderCommonImages.class);
    
}
