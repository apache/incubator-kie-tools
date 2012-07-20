package org.drools.guvnor.client.mvp;

import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Hack interface because Errai CDI does not currently support this for classes:
 * Collection<IOCBeanDef> beans = manager.lookupBeans(PlaceRequest.class); How
 * ever, it works for interfaces like this.
 */
public interface IPlaceRequest {

    String getIdentifier();

    IPlaceRequest getPlace();

    String getParameter(String key,
                        String defaultValue);

    Set<String> getParameterNames();

    Map<String, String> getParameters();

    IPlaceRequest addParameter(String name,
                               String value);

}
