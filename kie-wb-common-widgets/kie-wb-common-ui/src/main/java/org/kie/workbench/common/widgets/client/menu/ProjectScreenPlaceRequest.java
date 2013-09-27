package org.kie.workbench.common.widgets.client.menu;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Portable
public class ProjectScreenPlaceRequest
        extends DefaultPlaceRequest {

    public ProjectScreenPlaceRequest() {
        super("projectScreen");
    }

}
