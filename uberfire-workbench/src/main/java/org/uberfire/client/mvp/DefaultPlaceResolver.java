package org.uberfire.client.mvp;


import com.google.gwt.user.client.Window;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.backend.workbench.WorkbenchServices;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class DefaultPlaceResolver {

    @Inject
    private Caller<WorkbenchServices> wbServices;

    private Map<String, String> properties;

    @AfterInitialization
    public void init() {
        wbServices.call(
                new RemoteCallback<Map<String, String>>() {
                    @Override
                    public void callback(Map<String, String> properties) {
                       DefaultPlaceResolver.this.properties = properties;
                    }
                }
        ).loadDefaultEditorsMap();
    }

    public String getEditorId(String  key) {
        return properties.get(key);
    }

    public void saveDefaultEditor(String fullIdentifier, String signatureId) {
        properties.put(fullIdentifier, signatureId);

        wbServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {

            }
        }).saveDefaultEditors(properties);
    }
}
