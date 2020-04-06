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

import org.kie.dmn.model.api.RuleAnnotation;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClauseText;
import org.kie.workbench.common.dmn.api.property.dmn.Text;

public class RuleAnnotationClauseTextConverter {

    public static RuleAnnotationClauseText wbFromDMN(final RuleAnnotation ruleAnnotation) {
        if (ruleAnnotation == null) {
            return null;
        }
        final RuleAnnotationClauseText text = new RuleAnnotationClauseText();
        text.setText(new Text(ruleAnnotation.getText()));
        return text;
    }

    public static RuleAnnotation dmnFromWB(final RuleAnnotationClauseText wb) {
        if (wb == null) {
            return null;
        }
        final RuleAnnotation ruleAnnotation = new org.kie.dmn.model.v1_2.TRuleAnnotation();
        ruleAnnotation.setText(wb.getText().getValue());
        return ruleAnnotation;
    }
}
