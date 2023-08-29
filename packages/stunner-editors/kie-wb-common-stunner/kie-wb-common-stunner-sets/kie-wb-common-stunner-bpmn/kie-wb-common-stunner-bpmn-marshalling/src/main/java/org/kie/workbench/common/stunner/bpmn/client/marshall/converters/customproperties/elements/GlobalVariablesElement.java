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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsFactory;
import org.jboss.drools.GlobalType;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;

import static org.jboss.drools.DroolsPackage.Literals.DOCUMENT_ROOT__GLOBAL;

public class GlobalVariablesElement extends ElementDefinition<String> {

    public GlobalVariablesElement(String name) {
        super(name, "");
    }

    @Override
    public String getValue(BaseElement element) {
        return getStringValue(element)
                .orElse(getDefaultValue());
    }

    @Override
    public void setValue(BaseElement element, String value) {
        setStringValue(element, value);
    }

    @Override
    protected Optional<String> getStringValue(BaseElement element) {
        List<ExtensionAttributeValue> extValues = element.getExtensionValues();

        List<FeatureMap> extElementsList = extValues.stream()
                .map(extAttrVal -> extAttrVal.getValue())
                .collect(Collectors.toList());

        List<GlobalType> globalExtensions = extElementsList.stream()
                .map(extAttrVal -> (List<GlobalType>) extAttrVal.get(DOCUMENT_ROOT__GLOBAL, true))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        String globalVariables = globalExtensions.stream()
                .map(globalType -> globalType.getIdentifier() + ":" + globalType.getType())
                .collect(Collectors.joining(","));

        return Optional.ofNullable(globalVariables);
    }

    @Override
    protected void setStringValue(BaseElement element, String value) {
        value = StringUtils.preFilterVariablesTwoSemicolonForGenerics(value);
        Stream.of(value.split(","))
                .map(GlobalVariablesElement::extensionOf)
                .forEach(getExtensionElements(element)::add);
    }

    static FeatureMap.Entry extensionOf(String variable) {
        return new EStructuralFeatureImpl.SimpleFeatureMapEntry(
                (EStructuralFeature.Internal) DOCUMENT_ROOT__GLOBAL,
                globalTypeDataOf(variable));
    }

    static GlobalType globalTypeDataOf(String variable) {
        variable = StringUtils.postFilterForGenerics(variable);
        GlobalType globalType = DroolsFactory.eINSTANCE.createGlobalType();
        String[] properties = variable.split(":", -1);
        globalType.setIdentifier(properties[0]);
        globalType.setType(properties[1]);

        return globalType;
    }
}