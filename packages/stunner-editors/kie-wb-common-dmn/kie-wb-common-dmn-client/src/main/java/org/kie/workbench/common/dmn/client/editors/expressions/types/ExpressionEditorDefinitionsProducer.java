/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types;

import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;

@DMNEditor
@ApplicationScoped
public class ExpressionEditorDefinitionsProducer implements Supplier<ExpressionEditorDefinitions> {

    private ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();

    public ExpressionEditorDefinitionsProducer() {
        //CDI proxy
    }

    @Inject
    public ExpressionEditorDefinitionsProducer(final Instance<ExpressionEditorDefinition> expressionEditorDefinitionBeans) {
        expressionEditorDefinitionBeans.forEach(t -> expressionEditorDefinitions.add(t));
        expressionEditorDefinitions.sort((o1, o2) -> o1.getType().ordinal() - o2.getType().ordinal());
    }

    @Produces
    @Override
    public ExpressionEditorDefinitions get() {
        return expressionEditorDefinitions;
    }
}
