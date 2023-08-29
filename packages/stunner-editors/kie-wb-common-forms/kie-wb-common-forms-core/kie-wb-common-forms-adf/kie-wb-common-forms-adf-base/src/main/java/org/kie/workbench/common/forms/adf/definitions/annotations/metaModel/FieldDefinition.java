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


package org.kie.workbench.common.forms.adf.definitions.annotations.metaModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;

/**
 * Indicates to the ADF engine that the annotated class must be used as a meta-fieldDefinition
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FieldDefinition {

    String LABEL = "label";
    String HELP_MESSAGE = "helpMessage";

    /**
     * Specifies how the i18n for the field should be calculated. By default let's the engine calculate it using the
     * {@link I18nSettings} specified on the {@link FormDefinition}
     */
    I18nMode i18nMode() default I18nMode.DONT_OVERRIDE;

    String labelKeySuffix() default LABEL;

    String helpMessageKeySuffix() default HELP_MESSAGE;
}
