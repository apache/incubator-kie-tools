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


package org.kie.workbench.common.forms.adf.definitions.annotations.i18n;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;

/**
 * Defines the I18n settings on a {@link FormDefinition}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nSettings {

    /**
     * The bundle source of the i18n translations (only for backend). If no bundle is provided the engine will take the
     * Full Qualified Name of the class annotated as {@link FormDefinition}
     */
    String bundle() default "";

    /**
     * Preffix to add before any i18n-key on the form.
     */
    String keyPreffix() default "";

    /**
     * Separator between the preffix and the i18n-key
     */
    String separator() default ".";
}
