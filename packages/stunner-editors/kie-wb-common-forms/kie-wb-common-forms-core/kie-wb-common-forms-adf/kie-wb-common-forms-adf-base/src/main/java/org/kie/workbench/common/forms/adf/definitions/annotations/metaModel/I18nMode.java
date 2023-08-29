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

import org.kie.workbench.common.forms.adf.service.building.FieldStatusModifier;

/**
 * Specifies how a {@link FieldDefinition} i18n has to be handled
 */
public enum I18nMode {

    /**
     * Doesn't override the default field label generated for the field.
     */
    DONT_OVERRIDE,

    /**
     * Overrides the field i18n by calculating the I18n key for the label using the Full Qualified Name of the
     * annotated class and the name property annotated with {@link FieldLabel} or {@link FieldHelp}
     */
    OVERRIDE_I18N_KEY,

    /**
     * Overrides the field i18n by generating a {@link FieldStatusModifier} that dynamically modifies the field
     * label using the contents of the property annotated with {@link FieldLabel} or {@link FieldHelp}
     */
    OVERRIDE
}
