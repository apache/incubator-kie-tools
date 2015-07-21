package org.uberfire.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.WindowConstants;

import com.google.gwt.user.client.Window;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.client.resources.WebAppResource;

@EntryPoint
public class
        DocksEntryPoint {


    @PostConstruct
    public void init() {
        WebAppResource.INSTANCE.CSS().ensureInjected();
    }

}
