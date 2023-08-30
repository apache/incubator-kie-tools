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

package org.kie.workbench.common.dmn.client.editors.expressions.types;

import java.util.ArrayList;
import java.util.Optional;

import org.kie.workbench.common.dmn.api.definition.model.Expression;

public class ExpressionEditorDefinitions extends ArrayList<ExpressionEditorDefinition<Expression>> {

    public Optional<ExpressionEditorDefinition<Expression>> getExpressionEditorDefinition(final Optional<Expression> expression) {
        if (!expression.isPresent()) {
            return this.stream()
                    .filter(ed -> !ed.getModelClass().isPresent())
                    .findFirst();
        } else {
            return this.stream()
                    .filter(ed -> ed.getModelClass().isPresent())
                    .filter(ed -> ed.getModelClass().get().getClass().equals(expression.get().getClass()))
                    .findFirst();
        }
    }

    public Optional<ExpressionEditorDefinition<Expression>> getExpressionEditorDefinition(final ExpressionType type) {
        return this.stream()
                .filter(ed -> ed.getType().equals(type))
                .findFirst();
    }
}
