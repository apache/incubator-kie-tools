package org.drools.guvnor.client.editors.packageexplorer;

import javax.enterprise.context.Dependent;

import org.drools.guvnor.client.mvp.PlaceRequest;

@Dependent
public class PackageExplorerPlace extends PlaceRequest {

    private static final String PLACE_NAME = "Package Explorer";

    public PackageExplorerPlace() {
        super( PLACE_NAME );
    }
}
