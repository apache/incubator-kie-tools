package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.QualifierAnnotation;
import org.uberfire.client.mvp.RegularAnnotation;
import org.uberfire.workbench.model.PerspectiveDefinition;

@QualifierAnnotation( classField = String.class, stringField = "someText", booleanField = true, intField = 13 )
@RegularAnnotation( classField = String.class, stringField = "someText", booleanField = true, intField = 13 )
@WorkbenchPerspective(identifier = "PerspectiveTest25")
public class PerspectiveTest25 {

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return null;
    }

}
