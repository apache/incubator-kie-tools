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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.adf.definitions.annotations.layout.Layout;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.forms.adf.definitions.settings.LabelPosition;
import org.kie.workbench.common.forms.adf.service.definitions.FormDefinitionSettings;

/**
 * Indicates to the ADF engine to generate the {@link FormDefinitionSettings} for the annotated class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FormDefinition {

    /**
     * Determines how labesl are going to be aligned on the form
     */
    LabelPosition labelPosition() default LabelPosition.DEFAULT;

    /**
     * Indicates to the ADF engine to add fields from all the superclasses of the annotated class
     */
    boolean allowInheritance() default true;

    /**
     * Determines the policy to add form elements.
     */
    FieldPolicy policy() default FieldPolicy.ALL;

    /**
     * Defines the i18nSettings to get the field labels
     */
    I18nSettings i18n() default @I18nSettings;

    /**
     * Defines the form layout
     */
    Layout layout() default @Layout;

    /**
     * Determines what's the element name of first form element
     */
    String startElement() default "";

    /**
     * List of default settings to configure the fields
     */
    FieldParam[] defaultFieldSettings() default {};
}
