/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.jbpm.client.rendering.documents;

import javax.enterprise.context.Dependent;

import org.jboss.errai.databinding.client.PropertyType;
import org.kie.workbench.common.forms.dynamic.client.helper.PropertyGenerator;
import org.kie.workbench.common.forms.jbpm.model.authoring.documents.definition.DocumentListFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentData;

@Dependent
public class DocumentListPropertyGenerator implements PropertyGenerator<DocumentListFieldDefinition> {

    @Override
    public Class<DocumentListFieldDefinition> getType() {
        return DocumentListFieldDefinition.class;
    }

    @Override
    public PropertyType generatePropertyType(DocumentListFieldDefinition field) {
        return new PropertyType(DocumentData.class, false, true);
    }
}
