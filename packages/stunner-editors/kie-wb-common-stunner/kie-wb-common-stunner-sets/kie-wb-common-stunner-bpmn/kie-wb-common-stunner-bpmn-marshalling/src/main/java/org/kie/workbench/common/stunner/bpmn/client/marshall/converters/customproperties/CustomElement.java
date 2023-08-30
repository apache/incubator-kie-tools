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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.FlowElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.elements.BooleanElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.elements.DefaultImportsElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.elements.ElementDefinition;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.elements.GlobalVariablesElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.elements.MetaDataAttributesElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.elements.MetadataTypeDefinition;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.elements.StringElement;

public class CustomElement<T> {

    public static final MetadataTypeDefinition<Boolean> async = new BooleanElement("customAsync", false);
    public static final MetadataTypeDefinition<Boolean> autoStart = new BooleanElement("customAutoStart", false);
    public static final MetadataTypeDefinition<Boolean> autoConnectionSource = new BooleanElement("isAutoConnection.source", false);
    public static final MetadataTypeDefinition<Boolean> autoConnectionTarget = new BooleanElement("isAutoConnection.target", false);
    public static final MetadataTypeDefinition<String> customTags = new StringElement("customTags", "[]") {
        // Need to use an intermediary character since Variables depend on comma separators
        @Override
        public String getValue(BaseElement element) {
            return getStringValue(element)
                    .orElse(getDefaultValue()).replace(",", ";");
        }

        @Override
        public void setValue(BaseElement element, String value) {
            value = value.replace(";", ",");
            setStringValue(element, value);
        }
    };

    public static final MetadataTypeDefinition<String> description = new StringElement("customDescription", "");
    public static final MetadataTypeDefinition<String> scope = new StringElement("customScope", "");
    public static final MetadataTypeDefinition<String> name = new StringElement("elementname", "") {
        @Override
        public String getValue(BaseElement element) {
            String defaultValue =
                    element instanceof FlowElement ?
                            ((FlowElement) element).getName()
                            : this.getDefaultValue();

            return getStringValue(element).orElse(defaultValue);
        }
    };
    public static final MetadataTypeDefinition<String> caseIdPrefix = new StringElement("customCaseIdPrefix", "");
    public static final MetadataTypeDefinition<String> caseRole = new StringElement("customCaseRoles", "");
    public static final MetadataTypeDefinition<String> slaDueDate = new StringElement("customSLADueDate", "");
    public static final DefaultImportsElement defaultImports = new DefaultImportsElement("defaultImports");
    public static final GlobalVariablesElement globalVariables = new GlobalVariablesElement("customGlobalVariables");
    public static final MetaDataAttributesElement metaDataAttributes = new MetaDataAttributesElement("customMetaDataAttributes");
    public static final MetadataTypeDefinition<Boolean> isCase = new BooleanElement("case", Boolean.FALSE);
    public static final MetadataTypeDefinition<String> customActivationCondition = new StringElement("customActivationCondition", "");
    public static final MetadataTypeDefinition<Boolean> abortParent = new BooleanElement("customAbortParent", true);

    private final ElementDefinition<T> elementDefinition;
    private final BaseElement element;

    public CustomElement(ElementDefinition<T> elementDefinition, BaseElement element) {
        this.elementDefinition = elementDefinition;
        this.element = element;
    }

    public T get() {
        return elementDefinition.getValue(element);
    }

    public void set(T value) {
        if (value != null && !value.equals(elementDefinition.getDefaultValue())) {
            elementDefinition.setValue(element, value);
        }
    }
}
