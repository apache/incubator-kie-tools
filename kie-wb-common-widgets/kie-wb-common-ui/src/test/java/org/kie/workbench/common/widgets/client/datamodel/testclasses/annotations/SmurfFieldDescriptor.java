package org.kie.workbench.common.widgets.client.datamodel.testclasses.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SmurfFieldDescriptor {

    String gender();

    String colour() default "blue";

    String description();
}
