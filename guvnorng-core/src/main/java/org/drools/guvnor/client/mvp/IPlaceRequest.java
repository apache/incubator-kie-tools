package org.drools.guvnor.client.mvp;

import java.util.Map;
import java.util.Set;

/**
 * Hack interface because Errai CDI does not currently support this for classes:
 * Collection<IOCBeanDef> beans = manager.lookupBeans(PlaceRequest.class); How
 * ever, it works for interfaces like this.
 */
public interface IPlaceRequest {

    String getNameToken();

    PlaceRequest getPlace();

    String getParameter(String key,
                        String defaultValue);

    Set<String> getParameterNames();

    Map<String, String> getParameters();

    IPlaceRequest addParameter(String name,
                               String value);

}
