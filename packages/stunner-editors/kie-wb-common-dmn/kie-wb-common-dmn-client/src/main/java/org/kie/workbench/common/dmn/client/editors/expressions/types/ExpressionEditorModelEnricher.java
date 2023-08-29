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

import java.util.Optional;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Expression;

public interface ExpressionEditorModelEnricher<T extends Expression> {

    default void enrich(final Optional<String> nodeUUID,
                        final HasExpression hasExpression,
                        final Optional<T> expression) {
    }

    /**
     * It enriches a ROOT expression, typically when managing the new React based Boxed Expression.
     * In this scenario, additional info are required to correctly enrich the Expression. To customize its
     * behavior, you need to explicitly override this method, otherwise it will be redirected to the "old"
     * enricher currently used by the old GWT based editor.
     * @param nodeUUID
     * @param rootExpression
     * @param enrichedExpression
     * @param dataType
     */
    default void enrichRootExpression(final String nodeUUID,
                                      final HasExpression rootExpression,
                                      final T enrichedExpression,
                                      final String dataType) {
        enrich(nodeUUID != null ? Optional.ofNullable(nodeUUID) : Optional.empty(),
                rootExpression,
                enrichedExpression != null ? Optional.of(enrichedExpression) : Optional.empty());
    }
}
