/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.bpmn2.TextAnnotation;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;

public class TextAnnotationPropertyWriter extends PropertyWriter {

    private final TextAnnotation element;

    public TextAnnotationPropertyWriter(TextAnnotation element, VariableScope variableScope) {
        super(element, variableScope);
        this.element = element;
    }

    public void setName(String value) {
        final String escaped = StringEscapeUtils.escapeXml10(value.trim());
        element.setText(escaped);
        element.setName(escaped);
        CustomElement.name.of(element).set(value);
    }

    @Override
    public TextAnnotation getElement() {
        return (TextAnnotation) super.getElement();
    }
}
