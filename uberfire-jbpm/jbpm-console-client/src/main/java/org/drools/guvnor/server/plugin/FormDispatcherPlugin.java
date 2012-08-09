package org.drools.guvnor.server.plugin;

import javax.activation.DataHandler;
import java.net.URL;

public interface FormDispatcherPlugin {
    URL getDispatchUrl(FormAuthorityRef ref);

    DataHandler provideForm(FormAuthorityRef ref);
}
