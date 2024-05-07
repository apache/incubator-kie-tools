package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;

@WorkbenchPerspective(identifier = "PerspectiveTest2")
public class PerspectiveTest2 {

    @Perspective
    public void getPerspective() {
    }

}
