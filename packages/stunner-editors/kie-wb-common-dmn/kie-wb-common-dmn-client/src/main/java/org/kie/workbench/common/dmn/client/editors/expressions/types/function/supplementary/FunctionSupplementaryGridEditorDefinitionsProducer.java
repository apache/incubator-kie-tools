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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary;

import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionGridSupplementaryEditor;

@ApplicationScoped
@FunctionGridSupplementaryEditor
public class FunctionSupplementaryGridEditorDefinitionsProducer implements Supplier<ExpressionEditorDefinitions> {

    private ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();

    public FunctionSupplementaryGridEditorDefinitionsProducer() {
        //CDI proxy
    }

    @Inject
    public FunctionSupplementaryGridEditorDefinitionsProducer(final @FunctionGridSupplementaryEditor Instance<ExpressionEditorDefinition> expressionEditorDefinitionBeans) {
        expressionEditorDefinitionBeans.forEach(t -> expressionEditorDefinitions.add(t));
        expressionEditorDefinitions.sort((o1, o2) -> o1.getType().ordinal() - o2.getType().ordinal());
    }

    @Produces
    @Override
    public ExpressionEditorDefinitions get() {
        return expressionEditorDefinitions;
    }
}
