package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.model.PerspectiveDefinition;

@WorkbenchPerspective(identifier = "PerspectiveTest6", isDefault = true)
public class PerspectiveTest6 {

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return null;
    }

    @OnStart
    public void onStart() {
    }

    @OnClose
    public void onClose() {
    }

    @OnReveal
    public void onReveal() {
    }

}
