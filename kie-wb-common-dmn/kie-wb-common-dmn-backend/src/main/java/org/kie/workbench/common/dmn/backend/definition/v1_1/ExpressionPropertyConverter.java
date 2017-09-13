/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;

public class ExpressionPropertyConverter {

    public static Expression wbFromDMN(final org.kie.dmn.model.v1_1.Expression dmn) {
        if (dmn instanceof org.kie.dmn.model.v1_1.LiteralExpression) {
            return LiteralExpressionPropertyConverter.wbFromDMN((org.kie.dmn.model.v1_1.LiteralExpression) dmn);
        } else if (dmn instanceof org.kie.dmn.model.v1_1.Context) {
            return ContextPropertyConverter.wbFromDMN((org.kie.dmn.model.v1_1.Context) dmn);
        }
        return null;
    }

    public static org.kie.dmn.model.v1_1.Expression dmnFromWB(final Expression wb) {
        if (wb instanceof LiteralExpression) {
            return LiteralExpressionPropertyConverter.dmnFromWB((LiteralExpression) wb);
        } else if (wb instanceof Context) {
            return ContextPropertyConverter.dmnFromWB((Context) wb);
        }
        return null;
    }
}
