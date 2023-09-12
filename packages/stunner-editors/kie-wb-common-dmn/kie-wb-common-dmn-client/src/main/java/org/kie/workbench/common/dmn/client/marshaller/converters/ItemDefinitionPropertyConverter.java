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

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;

import static java.util.Optional.ofNullable;

public class ItemDefinitionPropertyConverter {

    public static ItemDefinition wbFromDMN(final JSITItemDefinition dmn) {

        if (Objects.isNull(dmn)) {
            return null;
        }

        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Name name = new Name(dmn.getName());

        final Description description = wbDescriptionFromDMN(dmn);
        final QName typeRef = wbTypeRefFromDMN(dmn);

        final String typeLanguage = dmn.getTypeLanguage();
        final boolean isCollection = dmn.getIsCollection();

        final ItemDefinition wb = new ItemDefinition(id,
                                                     description,
                                                     name,
                                                     typeRef,
                                                     null,
                                                     null,
                                                     typeLanguage,
                                                     isCollection,
                                                     false);

        setUnaryTests(wb, dmn);
        setItemComponent(wb, dmn);

        return wb;
    }

    static void setUnaryTests(final ItemDefinition wb,
                              final JSITItemDefinition dmn) {

        final JSITUnaryTests dmnAllowedValues = dmn.getAllowedValues();
        final Optional<UnaryTests> wbUnaryTests = ofNullable(UnaryTestsPropertyConverter.wbFromDMN(dmnAllowedValues));

        wbUnaryTests.ifPresent(unaryTests -> {
            wb.setAllowedValues(unaryTests);
            unaryTests.setParent(wb);
        });
    }

    static void setItemComponent(final ItemDefinition wb,
                                 final JSITItemDefinition dmn) {
        final List<JSITItemDefinition> jsiItemDefinitions = dmn.getItemComponent();
        if (Objects.nonNull(jsiItemDefinitions)) {
            for (int i = 0; i < jsiItemDefinitions.size(); i++) {
                final JSITItemDefinition jsiItemDefinition = Js.uncheckedCast(jsiItemDefinitions.get(i));
                wb.getItemComponent().add(wbChildFromDMN(wb, jsiItemDefinition));
            }
        }
    }

    static ItemDefinition wbChildFromDMN(final ItemDefinition wbParent,
                                         final JSITItemDefinition dmnChild) {

        final ItemDefinition wbChild = wbFromDMN(dmnChild);

        if (Objects.nonNull(wbChild)) {
            wbChild.setParent(wbParent);
        }

        return wbChild;
    }

    static Description wbDescriptionFromDMN(final JSITItemDefinition dmn) {
        return DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
    }

    static QName wbTypeRefFromDMN(final JSITItemDefinition dmn) {
        final QName wbQName = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());
        final QName undefinedQName = BuiltInType.UNDEFINED.asQName();

        return Objects.equals(wbQName, undefinedQName) ? null : wbQName;
    }

    public static JSITItemDefinition dmnFromWB(final ItemDefinition wb) {
        if (Objects.isNull(wb)) {
            return null;
        }
        final JSITItemDefinition result = JSITItemDefinition.newInstance();
        result.setId(wb.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        description.ifPresent(result::setDescription);
        result.setName(wb.getName().getValue());
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);

        result.setTypeLanguage(wb.getTypeLanguage());
        result.setIsCollection(wb.isIsCollection());

        final JSITUnaryTests utConverted = UnaryTestsPropertyConverter.dmnFromWB(wb.getAllowedValues());
        result.setAllowedValues(utConverted);

        for (ItemDefinition child : wb.getItemComponent()) {
            final JSITItemDefinition convertedChild = ItemDefinitionPropertyConverter.dmnFromWB(child);
            result.addItemComponent(convertedChild);
        }

        return result;
    }
}
