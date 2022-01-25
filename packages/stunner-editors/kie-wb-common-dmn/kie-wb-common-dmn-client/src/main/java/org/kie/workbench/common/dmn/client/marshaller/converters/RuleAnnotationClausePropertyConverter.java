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

package org.kie.workbench.common.dmn.client.marshaller.converters;

import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITRuleAnnotationClause;

public class RuleAnnotationClausePropertyConverter {

    public static RuleAnnotationClause wbFromDMN(final JSITRuleAnnotationClause dmn) {
        final RuleAnnotationClause result = new RuleAnnotationClause();
        result.setName(new Name(dmn.getName()));
        return result;
    }

    public static JSITRuleAnnotationClause dmnFromWB(final RuleAnnotationClause wb) {
        final JSITRuleAnnotationClause result = JSITRuleAnnotationClause.newInstance();
        result.setName(wb.getName().getValue());
        return result;
    }
}
