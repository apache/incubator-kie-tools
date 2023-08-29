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

package org.kie.workbench.common.dmn.client.marshaller.converters;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition.Kind;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITContext;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITContextEntry;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITFunctionDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;

public class ContextPropertyConverter {

    public static Context wbFromDMN(final JSITContext dmn,
                                    final JSITExpression parent,
                                    final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());
        final Context result = new Context(id,
                                           description,
                                           typeRef);
        final List<JSITContextEntry> jsiContextEntries = dmn.getContextEntry();
        for (int i = 0; i < jsiContextEntries.size(); i++) {
            final JSITContextEntry jsiContextentry = Js.uncheckedCast(jsiContextEntries.get(i));
            final ContextEntry ceConverted = ContextEntryPropertyConverter.wbFromDMN(jsiContextentry, hasComponentWidthsConsumer);
            if (Objects.nonNull(ceConverted)) {
                ceConverted.setParent(result);
                result.getContextEntry().add(ceConverted);
            }
        }

        //No need to append a _default_ row if the Context is part of a JAVA or PMML FunctionDefinition
        if (JSITFunctionDefinition.instanceOf(parent)) {
            final JSITFunctionDefinition functionDefinition = Js.uncheckedCast(parent);
            final String sKind = Js.uncheckedCast(functionDefinition.getKind());
            final Kind kind = Kind.fromValue(sKind);
            if (!Objects.equals(Kind.FEEL, kind)) {
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

    public static JSITContext dmnFromWB(final Context wb,
                                        final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        final JSITContext result = JSITContext.newInstance();
        result.setId(wb.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        description.ifPresent(result::setDescription);
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);
        for (ContextEntry ce : wb.getContextEntry()) {
            final JSITContextEntry ceConverted = ContextEntryPropertyConverter.dmnFromWB(ce, componentWidthsConsumer);
            result.addContextEntry(ceConverted);
        }

        //The UI appends a ContextEntry for the _default_ result that may contain an undefined Expression.
        //If this is the case then DMN does not require the ContextEntry to be written out to the XML.
        //Conversion of ContextEntries will always create a _mock_ LiteralExpression if no Expression has
        //been defined therefore remove the last entry from the org.kie.dmn.model if the WB had no Expression.
        final int contextEntriesCount = result.getContextEntry().size();
        if (contextEntriesCount > 0) {
            if (Objects.isNull(wb.getContextEntry().get(contextEntriesCount - 1).getExpression())) {
                result.removeContextEntry(contextEntriesCount - 1);
            }
        }

        return result;
    }
}
