package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.mvp.PlaceRequest;

@WorkbenchPerspective(identifier = "PerspectiveTest7", isDefault = true)
public class PerspectiveTest7 {

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return null;
    }

    @OnStart
    public void onStart(final PlaceRequest place) {
    }

    @OnClose
    public void onClose() {
    }

    @OnReveal
    public void onReveal() {
    }

}
