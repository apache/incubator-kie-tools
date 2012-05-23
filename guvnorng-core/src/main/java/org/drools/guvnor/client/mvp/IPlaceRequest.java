package org.drools.guvnor.client.mvp;

/**
 * Hack interface because Errai CDI does not currently support this for classes:
 * Collection<IOCBeanDef> beans = manager.lookupBeans(PlaceRequest.class);
 * How ever, it works for interfaces like this.
 */
public interface IPlaceRequest {
    String getNameToken();

    PlaceRequest getPlace();
}
