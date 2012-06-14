package org.drools.guvnor.client.mvp;

import org.drools.guvnor.vfs.Path;

/**
 * Hack interface because Errai CDI does not currently support this for classes:
 * Collection<IOCBeanDef> beans = manager.lookupBeans(PlaceRequest.class); How
 * ever, it works for interfaces like this.
 */
public interface IPlaceRequestFactory<P extends IPlaceRequest> {

    String getFactoryName();
    
    P makePlace(Path path);

    P makePlace();

}
