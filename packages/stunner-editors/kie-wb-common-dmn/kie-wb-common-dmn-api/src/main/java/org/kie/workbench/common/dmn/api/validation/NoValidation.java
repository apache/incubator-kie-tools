/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package org.kie.workbench.common.dmn.api.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * JSR303, or probably more specifically Errai's use of GWT's Bean Validation, looks for a validator implementation
 * for any class that reference a JSR303 annotation. Classes annotated with either {@code @Valid} or {@code @Constraint}
 * have a validator class generated. However all of DMN's connectors extend classes that use {@code @Valid} but do not
 * themselves use JSR303 annotations and hence do not have validators generated. This causes problems when Errai
 * attempts to validate the whole graph. This annotation is therefore used to fool Errai into generating a
 * validator that does nothing.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = NoValidationValidator.class)
public @interface NoValidation {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
