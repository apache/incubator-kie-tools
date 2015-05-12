package org.kie.workbench.common.services.datamodeller.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@java.lang.annotation.Retention( RetentionPolicy.RUNTIME )
@java.lang.annotation.Target({ ElementType.TYPE, ElementType.FIELD })
public @interface ClassAnnotation {

    Class classParam() default Object.class;

    Class[] classArrayParam() default {};

}
