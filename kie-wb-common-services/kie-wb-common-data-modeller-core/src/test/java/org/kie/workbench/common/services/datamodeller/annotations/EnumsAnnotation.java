package org.kie.workbench.common.services.datamodeller.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@java.lang.annotation.Retention( RetentionPolicy.RUNTIME )
@java.lang.annotation.Target({ ElementType.TYPE, ElementType.FIELD })
public @interface EnumsAnnotation {

    TestEnums.ENUM1 enum1Param() default TestEnums.ENUM1.VALUE1;

    TestEnums.ENUM1[] enum1ArrayParam() default {};

    TestEnums.ENUM2 enum2Param() default TestEnums.ENUM2.VALUE1;

    TestEnums.ENUM2[] enum2ArrayParam() default {};

    ENUM3 enum3Param() default ENUM3.VALUE1;

    ENUM3[] enum3ArrayParam() default {};

}
