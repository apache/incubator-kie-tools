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
package org.kie.workbench.common.dmn.api.definition.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;

public abstract class DMNElement extends DMNModelInstrumentedBase {

    @Property
    @FormField(readonly = true)
    @Valid
    protected Id id;

    @Property
    @FormField(afterElement = "id", type = TextAreaFieldType.class)
    @Valid
    protected Description description;

    private DMNElement.ExtensionElements extensionElements;

    public DMNElement() {
    }

    public DMNElement(final Id id,
                      final Description description) {
        this.id = id;
        this.description = description;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    public Id getId() {
        return id;
    }

    public void setId(final Id id) {
        this.id = id;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(final Description description) {
        this.description = description;
    }

    public ExtensionElements getExtensionElements() {
        return extensionElements;
    }

    public void setExtensionElements(final ExtensionElements extensionElements) {
        this.extensionElements = extensionElements;
    }

    @Portable
    public static class ExtensionElements extends DMNModelInstrumentedBase {

        protected List<Object> any;

        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<>();
            }
            return this.any;
        }
    }
}
