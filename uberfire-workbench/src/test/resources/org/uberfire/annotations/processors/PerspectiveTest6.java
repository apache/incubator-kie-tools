package org.uberfire.annotations.processors;

import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;

@WorkbenchPerspective(identifier = "PerspectiveTest6", isDefault = true)
public class PerspectiveTest6 {

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return null;
    }

    @OnStartup
    public void onStartup() {
    }

    @OnClose
    public void onClose() {
    }

    @OnOpen
    public void onOpen() {
    }

}
