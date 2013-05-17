package org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SmurfFieldPositionDescriptor {

    int value();
}
