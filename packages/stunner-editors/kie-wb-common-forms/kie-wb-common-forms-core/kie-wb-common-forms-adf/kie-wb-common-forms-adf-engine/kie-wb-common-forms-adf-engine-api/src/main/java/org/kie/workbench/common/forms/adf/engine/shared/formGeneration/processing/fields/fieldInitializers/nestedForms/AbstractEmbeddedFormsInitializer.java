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


package org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms;

import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.FieldInitializer;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.Container;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.EmbedsForm;
import org.kie.workbench.common.forms.model.FieldDefinition;

public abstract class AbstractEmbeddedFormsInitializer<FIELD extends FieldDefinition & EmbedsForm> implements FieldInitializer<FIELD> {

    public static final String FIELD_CONTAINER_PARAM = "nestedFormContainer";

    public static final String COLLAPSIBLE_CONTAINER = "COLLAPSIBLE";

    protected void initializeContainer(FIELD field, FieldElement fieldElement) {
        String containerSetting = fieldElement.getParams().get(FIELD_CONTAINER_PARAM);

        if (containerSetting != null) {
            try {
                Container container = Container.valueOf(containerSetting);
                field.setContainer(container);
            } catch (Exception ex) {
                // Swallow
            }
        }
    }
}
