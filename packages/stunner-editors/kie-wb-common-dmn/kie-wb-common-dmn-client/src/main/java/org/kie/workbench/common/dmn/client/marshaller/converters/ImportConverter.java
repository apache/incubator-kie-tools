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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import javax.xml.XMLConstants;

import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.definition.model.ImportDMN;
import org.kie.workbench.common.dmn.api.definition.model.ImportPMML;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.marshaller.common.NameSpaceUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;

import static org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes.determineImportType;

public final class ImportConverter {

    public static Import wbFromDMN(final JSITImport dmn,
                                   final JSITDefinitions definitions,
                                   final PMMLDocumentMetadata pmmlDocument) {
        final Import result = createWBImport(dmn, definitions, pmmlDocument);
        final Map<QName, String> additionalAttributes = new HashMap<>();
        final Map<javax.xml.namespace.QName, String> otherAttributes = JSITUnaryTests.getOtherAttributesMap(dmn);
        for (Map.Entry<javax.xml.namespace.QName, String> entry : otherAttributes.entrySet()) {
            additionalAttributes.put(QNamePropertyConverter.wbFromDMN(entry.getKey().toString()), entry.getValue());
        }
        result.setAdditionalAttributes(additionalAttributes);
        final String name = dmn.getName();
        final String description = dmn.getDescription();
        result.setId(IdPropertyConverter.wbFromDMN(dmn.getId()));
        result.setName(new Name(name));
        result.setDescription(DescriptionPropertyConverter.wbFromDMN(description));

        NameSpaceUtils.extractNamespacesKeyedByPrefix(dmn).forEach((key, value) -> result.getNsContext().put(key, value));

        return result;
    }

    private static Import createWBImport(final JSITImport dmn,
                                         final JSITDefinitions definitions,
                                         final PMMLDocumentMetadata pmmlDocument) {
        final LocationURI locationURI = new LocationURI(dmn.getLocationURI());
        if (Objects.equals(DMNImportTypes.DMN, determineImportType(dmn.getImportType()))) {
            final ImportDMN result = new ImportDMN(dmn.getNamespace(), locationURI, dmn.getImportType());
            result.setDrgElementsCount(countDefinitionElement(definitions, d -> d.getDrgElement().size()));
            result.setItemDefinitionsCount(countDefinitionElement(definitions, d -> d.getItemDefinition().size()));
            return result;
        } else if (Objects.equals(DMNImportTypes.PMML, determineImportType(dmn.getImportType()))) {
            final ImportPMML result = new ImportPMML(dmn.getNamespace(), locationURI, dmn.getImportType());
            result.setModelCount(countDefinitionElement(pmmlDocument, document -> document.getModels().size()));
            return result;
        } else {
            return new Import(dmn.getNamespace(), locationURI, dmn.getImportType());
        }
    }

    static JSITImport dmnFromWb(final Import wb) {
        final JSITImport result = JSITImport.newInstance();
        result.setImportType(wb.getImportType());
        result.setLocationURI(wb.getLocationURI().getValue());
        result.setNamespace(wb.getNamespace());
        final Map<javax.xml.namespace.QName, String> otherAttributes = new HashMap<>();
        for (Map.Entry<QName, String> entry : wb.getAdditionalAttributes().entrySet()) {
            QNamePropertyConverter.dmnFromWB(entry.getKey())
                    .ifPresent(qName -> otherAttributes.put(qName, entry.getValue()));
        }
        wb.getNsContext().forEach((k, v) -> {
            // jsonix does not like marshalling xmlns="a url" so remove the default namespace.
            // The default namespace is now set when jsonix is invoked in MainJs.marshall(dmn12)
            // See https://github.com/highsource/jsonix/issues/227
            if (!Objects.equals(k, DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix())) {
                otherAttributes.put(new javax.xml.namespace.QName(XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
                                                                  k,
                                                                  XMLConstants.DEFAULT_NS_PREFIX),
                                    v);
            }
        });
        otherAttributes.remove(new javax.xml.namespace.QName(XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
                                                             DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix(),
                                                             XMLConstants.DEFAULT_NS_PREFIX));

        result.setId(wb.getId().getValue());
        result.setName(wb.getName().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        description.ifPresent(result::setDescription);
        result.setOtherAttributes(otherAttributes);

        return result;
    }

    private static <T> Integer countDefinitionElement(final T definition,
                                                      final Function<T, Integer> countFunction) {
        final Integer none = 0;
        return Optional
                .ofNullable(definition)
                .map(countFunction)
                .orElse(none);
    }
}