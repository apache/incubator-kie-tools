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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.marshaller.common.NameSpaceUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDI;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.core.util.UUID;

import static org.kie.workbench.common.dmn.client.marshaller.common.JsInteropUtils.forEach;

public class DefinitionsConverter {

    public static Definitions wbFromDMN(final JSITDefinitions dmn,
                                        final Map<JSITImport, JSITDefinitions> importDefinitions,
                                        final Map<JSITImport, PMMLDocumentMetadata> pmmlDocuments) {
        if (Objects.isNull(dmn)) {
            return null;
        }
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Name name = new Name(dmn.getName());
        final String namespace = dmn.getNamespace();
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final Definitions result = new Definitions();

        result.setId(id);
        result.setName(name);
        result.setNamespace(new Text(namespace));
        result.getNsContext().putIfAbsent(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix(), namespace);
        result.setExpressionLanguage(ExpressionLanguagePropertyConverter.wbFromDMN(dmn.getExpressionLanguage()));
        result.setTypeLanguage(dmn.getTypeLanguage());
        result.setDmnDiagramElements(getDMNDiagramElements(dmn));
        result.setDescription(description);

        final Map<String, String> namespaces = NameSpaceUtils.extractNamespacesKeyedByPrefix(dmn);
        for (Entry<String, String> kv : namespaces.entrySet()) {
            String mappedURI = kv.getValue();
            switch (mappedURI) {
                case org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_DMN:
                    mappedURI = org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_DMN;
                    break;
                case org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_FEEL:
                    mappedURI = org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_FEEL;
                    break;
                case org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_KIE:
                    mappedURI = org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_KIE;
                    break;
            }
            if (kv.getKey().equalsIgnoreCase(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix())) {
                result.getNsContext().putIfAbsent(kv.getKey(), mappedURI);
            } else {
                result.getNsContext().put(kv.getKey(), mappedURI);
            }
        }

        final List<JSITItemDefinition> jsiItemDefinitions = dmn.getItemDefinition();
        for (int i = 0; i < jsiItemDefinitions.size(); i++) {
            final JSITItemDefinition jsiItemDefinition = Js.uncheckedCast(jsiItemDefinitions.get(i));
            final ItemDefinition itemDefConverted = ItemDefinitionPropertyConverter.wbFromDMN(jsiItemDefinition);
            if (Objects.nonNull(itemDefConverted)) {
                itemDefConverted.setParent(result);
                result.getItemDefinition().add(itemDefConverted);
            }
        }

        final List<JSITImport> jsiImports = dmn.getImport();
        for (int i = 0; i < jsiImports.size(); i++) {
            final JSITImport jsiImport = Js.uncheckedCast(jsiImports.get(i));
            final JSITDefinitions definitions = Js.uncheckedCast(importDefinitions.get(jsiImport));
            final PMMLDocumentMetadata pmmlDocument = pmmlDocuments.get(jsiImport);
            final Import importConverted = ImportConverter.wbFromDMN(jsiImport, definitions, pmmlDocument);
            if (Objects.nonNull(importConverted)) {
                importConverted.setParent(result);
                result.getImport().add(importConverted);
            }
        }

        return result;
    }

    public static JSITDefinitions dmnFromWB(final Definitions wb, final boolean ignoreImportedItemDefinition) {
        if (Objects.isNull(wb)) {
            return null;
        }
        final JSITDefinitions result = JSITDefinitions.newInstance();

        // TODO currently DMN wb UI does not offer feature to set these required DMN properties, setting some hardcoded defaults for now.
        final String defaultId = Objects.nonNull(wb.getId()) ? wb.getId().getValue() : UUID.uuid();
        final String defaultName = Objects.nonNull(wb.getName()) ? wb.getName().getValue() : UUID.uuid(8);
        final JSIDMNDI dmnDMNDI = JSIDMNDI.newInstance();
        final String defaultNamespace = !StringUtils.isEmpty(wb.getNamespace().getValue())
                ? wb.getNamespace().getValue()
                : DMNModelInstrumentedBase.Namespace.DEFAULT.getUri() + UUID.uuid();

        result.setDMNDI(dmnDMNDI);
        result.setId(defaultId);
        result.setName(defaultName);
        result.setNamespace(defaultNamespace);

        final List<DMNDiagramElement> dmnDiagramElements = wb.getDiagramElements();
        final List<JSIDMNDiagram> jsidmnDiagrams = getJSIDMNDiagrams(dmnDiagramElements);
        dmnDMNDI.setDMNDiagram(jsidmnDiagrams);

        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        description.ifPresent(result::setDescription);
        final String typeLanguage = wb.getTypeLanguage();
        final String expressionLanguage = ExpressionLanguagePropertyConverter.dmnFromWB(wb.getExpressionLanguage());
        if (!StringUtils.isEmpty(typeLanguage)) {
            result.setTypeLanguage(typeLanguage);
        }
        if (!StringUtils.isEmpty(expressionLanguage)) {
            result.setExpressionLanguage(expressionLanguage);
        }
        final Map<QName, String> otherAttributes = new HashMap<>();
        wb.getNsContext().forEach((k, v) -> {
            // jsonix does not like marshalling xmlns="a url" so remove the default namespace.
            // The default namespace is now set when jsonix is invoked in MainJs.marshall(dmn12)
            // See https://github.com/highsource/jsonix/issues/227
            if (!Objects.equals(k, DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix())) {
                otherAttributes.put(new QName(XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
                                              k,
                                              XMLConstants.DEFAULT_NS_PREFIX),
                                    v);
            }
        });
        result.setOtherAttributes(otherAttributes);

        // Add because it is present in the original JSON when unmarshalling
        if (Objects.isNull(result.getItemDefinition())) {
            result.setItemDefinition(new ArrayList<>());
        }
        for (ItemDefinition itemDef : wb.getItemDefinition()) {
            if (ignoreImportedItemDefinition) {
                if (itemDef.isAllowOnlyVisualChange()) {
                    continue;
                }
            }
            final JSITItemDefinition itemDefConverted = ItemDefinitionPropertyConverter.dmnFromWB(itemDef);
            result.addItemDefinition(itemDefConverted);
        }
        // Add because it is present in the original JSON when unmarshalling
        if (Objects.isNull(result.getImport())) {
            result.setImport(new ArrayList<>());
        }
        // Add because it is present in the original JSON when unmarshalling
        if (Objects.isNull(result.getDrgElement())) {
            result.setDrgElement(new ArrayList<>());
        }
        // Add because it is present in the original JSON when unmarshalling
        if (Objects.isNull(result.getArtifact())) {
            result.setArtifact(new ArrayList<>());
        }
        for (Import i : wb.getImport()) {
            final JSITImport importConverted = ImportConverter.dmnFromWb(i);
            result.addImport(importConverted);
        }

        return result;
    }

    private static List<JSIDMNDiagram> getJSIDMNDiagrams(final List<DMNDiagramElement> dmnDiagramElements) {
        return dmnDiagramElements
                .stream()
                .map(dmnDiagramElement -> {
                    final JSIDMNDiagram diagram = JSIDMNDiagram.newInstance();
                    diagram.setId(dmnDiagramElement.getId().getValue());
                    diagram.setName(dmnDiagramElement.getName().getValue());
                    return diagram;
                })
                .collect(Collectors.toList());
    }

    private static List<DMNDiagramElement> getDMNDiagramElements(final JSITDefinitions definitions) {

        final List<DMNDiagramElement> dmnDiagramElements = new ArrayList<>();
        final List<JSIDMNDiagram> dmnDiagrams = definitions.getDMNDI().getDMNDiagram();

        forEach(dmnDiagrams, dmnDiagram -> {
            final Id id = new Id(dmnDiagram.getId());
            dmnDiagramElements.add(new DMNDiagramElement(id,
                                                         new Name(dmnDiagram.getName())));
        });

        return dmnDiagramElements;
    }
}
