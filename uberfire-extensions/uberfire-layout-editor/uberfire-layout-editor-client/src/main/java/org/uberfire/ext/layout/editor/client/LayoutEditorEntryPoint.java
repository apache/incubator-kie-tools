package org.uberfire.ext.layout.editor.client;

import javax.annotation.PostConstruct;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.ext.layout.editor.client.resources.WebAppResource;

@EntryPoint
public class LayoutEditorEntryPoint {

    @PostConstruct
    public void init() {
        WebAppResource.INSTANCE.CSS().ensureInjected();
    }

}
