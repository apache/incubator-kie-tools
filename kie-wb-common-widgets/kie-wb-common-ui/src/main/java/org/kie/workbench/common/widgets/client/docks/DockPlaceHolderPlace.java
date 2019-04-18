package org.kie.workbench.common.widgets.client.docks;

import org.uberfire.mvp.impl.DefaultPlaceRequest;

public class DockPlaceHolderPlace
        extends DefaultPlaceRequest {

    public DockPlaceHolderPlace(final String name) {
        super("org.docks.PlaceHolder");
        addParameter("name", name);
    }
}
