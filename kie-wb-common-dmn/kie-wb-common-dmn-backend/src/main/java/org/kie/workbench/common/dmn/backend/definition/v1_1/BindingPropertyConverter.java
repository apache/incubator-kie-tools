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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.Binding;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;

public class BindingPropertyConverter {

    public static Binding wbFromDMN(final org.kie.dmn.model.api.Binding dmn) {
        if (dmn == null) {
            return null;
        }
        InformationItem convertedParameter = InformationItemPropertyConverter.wbFromDMN(dmn.getParameter());
        Expression convertedExpression = ExpressionPropertyConverter.wbFromDMN(dmn.getExpression());

        Binding result = new Binding();
        if (convertedParameter != null) {
            convertedParameter.setParent(result);
        }
        result.setParameter(convertedParameter);
        if (convertedExpression != null) {
            convertedExpression.setParent(result);
        }
        result.setExpression(convertedExpression);
        return result;
    }

    public static org.kie.dmn.model.api.Binding dmnFromWB(final Binding wb) {
        if (wb == null) {
            return null;
        }
        org.kie.dmn.model.api.Binding result = new org.kie.dmn.model.v1_1.TBinding();
        result.setParameter(InformationItemPropertyConverter.dmnFromWB(wb.getParameter()));
        result.setExpression(ExpressionPropertyConverter.dmnFromWB(wb.getExpression()));

        return result;
    }
}
