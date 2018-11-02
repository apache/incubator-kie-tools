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
import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class InvocationPropertyConverter {

    public static Invocation wbFromDMN(final org.kie.dmn.model.api.Invocation dmn) {
        if (dmn == null) {
            return null;
        }
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);

        Invocation result = new Invocation();
        result.setId(id);
        result.setDescription(description);
        result.setTypeRef(typeRef);

        Expression convertedExpression = ExpressionPropertyConverter.wbFromDMN(dmn.getExpression());
        result.setExpression(convertedExpression);
        if (convertedExpression != null) {
            convertedExpression.setParent(result);
        }

        for (org.kie.dmn.model.api.Binding b : dmn.getBinding()) {
            Binding bConverted = BindingPropertyConverter.wbFromDMN(b);
            if (bConverted != null) {
                bConverted.setParent(result);
            }
            result.getBinding().add(bConverted);
        }

        return result;
    }

    public static org.kie.dmn.model.api.Invocation dmnFromWB(final Invocation wb) {
        if (wb == null) {
            return null;
        }
        org.kie.dmn.model.api.Invocation result = new org.kie.dmn.model.v1_2.TInvocation();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);

        org.kie.dmn.model.api.Expression convertedExpression = ExpressionPropertyConverter.dmnFromWB(wb.getExpression());
        if (convertedExpression != null) {
            convertedExpression.setParent(result);
        }
        result.setExpression(convertedExpression);

        for (Binding b : wb.getBinding()) {
            org.kie.dmn.model.api.Binding bConverted = BindingPropertyConverter.dmnFromWB(b);
            if (bConverted != null) {
                bConverted.setParent(result);
            }
            result.getBinding().add(bConverted);
        }

        return result;
    }
}