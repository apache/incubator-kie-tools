/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props;

import jsinterop.annotations.JsType;

import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.DECISION_TABLE;

@JsType
public class DecisionTableProps extends ExpressionProps {
    public final String hitPolicy;
    public final String aggregation;
    public final Annotation[] annotations;
    public final InputClauseProps[] input;
    public final OutputClauseProps[] output;
    public final DecisionTableRule[] rules;

    public DecisionTableProps(final String id,
                              final String name,
                              final String dataType,
                              final String hitPolicy,
                              final String aggregation,
                              final Annotation[] annotations,
                              final InputClauseProps[] input,
                              final OutputClauseProps[] output,
                              final DecisionTableRule[] rules) {
        super(id, name, dataType, DECISION_TABLE.getText());
        this.hitPolicy = hitPolicy;
        this.aggregation = aggregation;
        this.annotations = annotations;
        this.input = input;
        this.output = output;
        this.rules = rules;
    }
}
