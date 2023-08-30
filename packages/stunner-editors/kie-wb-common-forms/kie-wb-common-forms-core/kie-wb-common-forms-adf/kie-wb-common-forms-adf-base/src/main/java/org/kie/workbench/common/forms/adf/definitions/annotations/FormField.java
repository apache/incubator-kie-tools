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


package org.kie.workbench.common.forms.adf.definitions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kie.workbench.common.forms.adf.definitions.annotations.layout.LayoutSettings;
import org.kie.workbench.common.forms.model.FieldType;

/**
 * Indicates to the ADF engine the form field settings for the annotated property.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FormField {

    /**
     * The preferred field type to use with the property. If the property type doesn't match the field type supported
     * types the ADF engine will find the default matching field type.
     */
    Class<? extends FieldType> type() default FieldType.class;

    /**
     * I18n key for the field label
     */
    String labelKey() default "";

    /**
     * Indicates that the field is required
     */
    boolean required() default false;

    /**
     * Indicates that the field is read-only
     */
    boolean readonly() default false;

    /**
     * I18n key for the field help message
     */
    String helpMessageKey() default "";

    /**
     * Define how the field has to be added to the form layout
     */
    LayoutSettings layoutSettings() default @LayoutSettings;

    /**
     * List of extra settings to configure the field
     */
    FieldParam[] settings() default {};

    /**
     * Indicates the name of the form element that goes the annotated property is placed after on the layout
     */
    String afterElement() default "";
}
