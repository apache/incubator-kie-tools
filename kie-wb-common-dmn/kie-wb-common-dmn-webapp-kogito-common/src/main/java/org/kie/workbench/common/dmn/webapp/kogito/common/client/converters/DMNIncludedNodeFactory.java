/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters;

import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.IsInformationItem;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.util.FileUtils;

import static org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils.isBuiltInType;

public class DMNIncludedNodeFactory {

    DMNIncludedNode makeDMNIncludeNode(final String path,
                                       final IncludedModel includeModel,
                                       final DRGElement drgElement) {
        return new DMNIncludedNode(FileUtils.getFileName(path), drgElementWithNamespace(drgElement, includeModel));
    }

    DRGElement drgElementWithNamespace(final DRGElement drgElement,
                                       final IncludedModel includeModel) {

        final String modelName = includeModel.getModelName();

        drgElement.setName(createName(drgElement, modelName));
        drgElement.setAllowOnlyVisualChange(true);

        if (drgElement instanceof HasVariable) {
            final HasVariable hasVariable = (HasVariable) drgElement;
            final IsInformationItem variable = hasVariable.getVariable();
            final QName qName = variable.getTypeRef();

            if (qName != null && !isBuiltInType(qName.getLocalPart())) {
                final QName typeRef = createTypeRef(modelName, qName);
                setVariable(hasVariable, variable, typeRef);
            }
        }

        return drgElement;
    }

    QName createTypeRef(final String modelName,
                        final QName qName) {
        return new QName(qName.getNamespaceURI(),
                         modelName + "." + qName.getLocalPart(),
                         qName.getPrefix());
    }

    Name createName(final DRGElement drgElement,
                    final String modelName) {
        return new Name(modelName + "." + drgElement.getName().getValue());
    }

    @SuppressWarnings("unchecked")
    void setVariable(final HasVariable hasVariable,
                     final IsInformationItem variable,
                     final QName typeRef) {
        if (variable instanceof InformationItemPrimary) {
            hasVariable.setVariable(new InformationItemPrimary(variable.getId(), variable.getName(), typeRef));
        }
    }
}
