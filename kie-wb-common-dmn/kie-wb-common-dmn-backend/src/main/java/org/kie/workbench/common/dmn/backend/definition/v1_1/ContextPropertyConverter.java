/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.FunctionKind;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;

public class ContextPropertyConverter {

    public static Context wbFromDMN(final org.kie.dmn.model.api.Context dmn,
                                    final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);
        Context result = new Context(id,
                                     description,
                                     typeRef);
        for (org.kie.dmn.model.api.ContextEntry ce : dmn.getContextEntry()) {
            ContextEntry ceConverted = ContextEntryPropertyConverter.wbFromDMN(ce,
                                                                               hasComponentWidthsConsumer);
            if (ceConverted != null) {
                ceConverted.setParent(result);
            }
            result.getContextEntry().add(ceConverted);
        }

        //No need to append a _default_ row if the Context is part of a JAVA or PMML FunctionDefinition
        if (dmn.getParent() instanceof FunctionDefinition) {
            final FunctionDefinition functionDefinition = (FunctionDefinition) dmn.getParent();
            if (!functionDefinition.getKind().equals(FunctionKind.FEEL)) {
                return result;
            }
        }

        //The UI requires a ContextEntry for the _default_ result even if none has been defined
        final int contextEntriesCount = result.getContextEntry().size();
        if (contextEntriesCount == 0) {
            result.getContextEntry().add(new ContextEntry());
        } else if (!Objects.isNull(result.getContextEntry().get(contextEntriesCount - 1).getVariable())) {
            result.getContextEntry().add(new ContextEntry());
        }

        return result;
    }

    public static org.kie.dmn.model.api.Context dmnFromWB(final Context wb,
                                                          final Consumer<ComponentWidths> componentWidthsConsumer) {
        org.kie.dmn.model.api.Context result = new org.kie.dmn.model.v1_2.TContext();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);
        for (ContextEntry ce : wb.getContextEntry()) {
            org.kie.dmn.model.api.ContextEntry ceConverted = ContextEntryPropertyConverter.dmnFromWB(ce,
                                                                                                     componentWidthsConsumer);
            if (ceConverted != null) {
                ceConverted.setParent(result);
            }
            result.getContextEntry().add(ceConverted);
        }

        //The UI appends a ContextEntry for the _default_ result that may contain an undefined Expression.
        //If this is the case then DMN does not require the ContextEntry to be written out to the XML.
        //Conversion of ContextEntries will always create a _mock_ LiteralExpression if no Expression has
        //been defined therefore remove the last entry from the org.kie.dmn.model if the WB had no Expression.
        final int contextEntriesCount = result.getContextEntry().size();
        if (contextEntriesCount > 0) {
            if (Objects.isNull(wb.getContextEntry().get(contextEntriesCount - 1).getExpression())) {
                result.getContextEntry().remove(contextEntriesCount - 1);
            }
        }

        return result;
    }
}
