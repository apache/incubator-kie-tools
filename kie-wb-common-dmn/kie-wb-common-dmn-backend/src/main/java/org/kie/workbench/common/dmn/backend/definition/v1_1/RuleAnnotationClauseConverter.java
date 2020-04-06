/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.dmn.model.v1_2.TRuleAnnotationClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

public class RuleAnnotationClauseConverter {

    public static RuleAnnotationClause wbFromDMN(final org.kie.dmn.model.api.RuleAnnotationClause ruleAnnotationClause) {
        if (ruleAnnotationClause == null) {
            return null;
        }
        final RuleAnnotationClause rule = new RuleAnnotationClause();
        rule.setName(new Name(ruleAnnotationClause.getName()));
        return rule;
    }

    public static org.kie.dmn.model.api.RuleAnnotationClause dmnFromWB(final RuleAnnotationClause wb) {
        if (wb == null) {
            return null;
        }
        final org.kie.dmn.model.api.RuleAnnotationClause rule = new TRuleAnnotationClause();
        rule.setName(wb.getName().getValue());
        return rule;
    }
}
