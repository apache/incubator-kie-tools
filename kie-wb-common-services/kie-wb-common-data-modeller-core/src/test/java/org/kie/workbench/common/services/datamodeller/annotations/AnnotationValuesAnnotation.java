package org.kie.workbench.common.services.datamodeller.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@java.lang.annotation.Retention( RetentionPolicy.RUNTIME )
@java.lang.annotation.Target({ ElementType.TYPE, ElementType.FIELD })
public @interface AnnotationValuesAnnotation {

    PrimitivesAnnotation primitivesParam() default @PrimitivesAnnotation();

    PrimitivesAnnotation[] primitivesArrayParam() default {};

    EnumsAnnotation enumsParam() default @EnumsAnnotation;

    EnumsAnnotation[] enumsArrayParam() default {};

    ClassAnnotation classAnnotationParam() default @ClassAnnotation;

    ClassAnnotation[] classAnnotationArrayParam() default {};


}
