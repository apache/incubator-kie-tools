/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.canvas.controls.keyboard.shortcut;

import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public enum DmnNode {
    DECISION(new Decision()),
    TEXT_ANNOTATION(new TextAnnotation()),
    BUSINESS_KNOWLEDGE_MODEL(new BusinessKnowledgeModel()),
    KNOWLEDGE_SOURCE(new KnowledgeSource()),
    INPUT_DATA(new InputData());

    final Object definition;

    final Element element;

    DmnNode(final Object definition) {
        this.definition = definition;
        this.element = mock(Element.class);

        final Definition elementDefinition = mock(Definition.class);
        doReturn(elementDefinition).when(element).getContent();
        doReturn(definition).when(elementDefinition).getDefinition();
    }

    public Object getDefinition() {
        return definition;
    }

    public Element getElement() {
        return element;
    }
}
