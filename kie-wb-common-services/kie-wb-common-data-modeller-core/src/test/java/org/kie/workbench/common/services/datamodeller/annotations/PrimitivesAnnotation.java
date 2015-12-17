/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodeller.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is for testing purposes and is used in some test .java files.
 */

@java.lang.annotation.Retention( RetentionPolicy.RUNTIME )
@java.lang.annotation.Target({ ElementType.TYPE, ElementType.FIELD })
public @interface PrimitivesAnnotation {

    byte byteParam() default 0;

    byte[] byteArrayParam() default {};

    short shortParam() default 0;

    short[] shortArrayParam() default {};

    int intParam() default 0;

    int[] intArrayParam() default {};

    long longParam() default 0;

    long[] longArrayParam() default {};

    float floatParam() default 0.0f;

    float[] floatArrayParam() default {};

    double doubleParam() default 0.0;

    double[] doubleArrayParam() default {};

    boolean booleanParam() default false;

    boolean[] booleanArrayParam() default {};

    char charParam() default '0';

    char[] charArrayParam() default {};

    String stringParam() default "";

    String[] stringArrayParam() default {};
}
