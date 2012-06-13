package org.drools.guvnor.client.editors.packageexplorer;

import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.PlaceRequest;

import javax.enterprise.context.Dependent;

@Dependent
public class PackageExplorerPlace extends PlaceRequest implements IPlaceRequest {

    public PackageExplorerPlace() {
        super("Package Explorer");
    }
}
