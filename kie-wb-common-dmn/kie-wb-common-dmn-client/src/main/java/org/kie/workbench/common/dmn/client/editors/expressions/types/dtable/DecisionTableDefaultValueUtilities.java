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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.Optional;

import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.client.property.dmn.DefaultValueUtilities;

public class DecisionTableDefaultValueUtilities {

    public static final String INPUT_CLAUSE_PREFIX = "input-";

    public static final String INPUT_CLAUSE_UNARY_TEST_TEXT = "-";

    public static final String OUTPUT_CLAUSE_PREFIX = "output-";

    public static final String OUTPUT_CLAUSE_EXPRESSION_TEXT = "";

    public static final String RULE_DESCRIPTION = "";

    public static String getNewInputClauseName(final DecisionTable dtable) {
        return INPUT_CLAUSE_PREFIX + getMaxUnusedInputClauseIndex(dtable);
    }

    private static int getMaxUnusedInputClauseIndex(final DecisionTable dtable) {
        int maxIndex = 0;
        for (InputClause ic : dtable.getInput()) {
            final InputClauseLiteralExpression le = ic.getInputExpression();
            if (le != null) {
                final Optional<Integer> index = DefaultValueUtilities.extractIndex(le.getText().getValue(), INPUT_CLAUSE_PREFIX);
                if (index.isPresent()) {
                    maxIndex = Math.max(maxIndex, index.get());
                }
            }
        }
        return maxIndex + 1;
    }

    public static String getNewOutputClauseName(final DecisionTable dtable) {
        return OUTPUT_CLAUSE_PREFIX + getMaxUnusedOutputClauseIndex(dtable);
    }

    private static int getMaxUnusedOutputClauseIndex(final DecisionTable dtable) {
        int maxIndex = 0;
        for (OutputClause oc : dtable.getOutput()) {
            final Optional<Integer> index = DefaultValueUtilities.extractIndex(oc.getName(), OUTPUT_CLAUSE_PREFIX);
            if (index.isPresent()) {
                maxIndex = Math.max(maxIndex, index.get());
            }
        }
        return maxIndex + 1;
    }
}
