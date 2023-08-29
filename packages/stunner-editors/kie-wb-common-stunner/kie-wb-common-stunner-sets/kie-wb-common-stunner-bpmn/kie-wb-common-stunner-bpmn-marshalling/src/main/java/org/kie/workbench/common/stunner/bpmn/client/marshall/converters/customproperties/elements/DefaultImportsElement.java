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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsFactory;
import org.jboss.drools.ImportType;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;

import static org.jboss.drools.DroolsPackage.Literals.DOCUMENT_ROOT__IMPORT;

public class DefaultImportsElement extends ElementDefinition<List<DefaultImport>> {

    private static Logger LOGGER = Logger.getLogger(DefaultImportsElement.class.getName());

    public DefaultImportsElement(String name) {
        super(name, new ArrayList<>());
    }

    @Override
    public List<DefaultImport> getValue(BaseElement element) {
        List<ExtensionAttributeValue> extValues = element.getExtensionValues();

        List<DefaultImport> defaultImports = extValues.stream()
                .map(ExtensionAttributeValue::getValue)
                .flatMap((Function<FeatureMap, Stream<?>>) extensionElements -> {
                    List o = (List) extensionElements.get(DOCUMENT_ROOT__IMPORT, true);
                    return o.stream();
                })
                .map(m -> new DefaultImport(((ImportType) m).getName()))
                .collect(Collectors.toList());

        return defaultImports;
    }

    @Override
    public void setValue(BaseElement element, List<DefaultImport> value) {
        value.stream()
                .map(DefaultImportsElement::extensionOf)
                .forEach(getExtensionElements(element)::add);
    }

    @Override
    protected void setStringValue(BaseElement element, String value) {
        Stream.of(value.split(","))
                .map(DefaultImportsElement::parseImport)
                .map(DefaultImportsElement::extensionOf)
                .forEach(getExtensionElements(element)::add);
    }

    static DefaultImport parseImport(String importValue) {
        try {
            return DefaultImport.fromString(importValue);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            return new DefaultImport();
        }
    }

    public static FeatureMap.Entry extensionOf(DefaultImport defaultImport) {
        return new EStructuralFeatureImpl.SimpleFeatureMapEntry(
                (EStructuralFeature.Internal) DOCUMENT_ROOT__IMPORT,
                importTypeDataOf(defaultImport));
    }

    public static ImportType importTypeDataOf(DefaultImport defaultImport) {
        ImportType importType = DroolsFactory.eINSTANCE.createImportType();
        importType.setName(defaultImport.getClassName());
        return importType;
    }
}
