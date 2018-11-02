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

import org.kie.dmn.model.api.FunctionKind;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition.Kind;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class FunctionDefinitionPropertyConverter {

    public static FunctionDefinition wbFromDMN(final org.kie.dmn.model.api.FunctionDefinition dmn) {
        if (dmn == null) {
            return null;
        }
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);
        Expression expression = ExpressionPropertyConverter.wbFromDMN(dmn.getExpression());
        FunctionDefinition result = new FunctionDefinition(id,
                                                           description,
                                                           typeRef,
                                                           expression);
        if (expression != null) {
            expression.setParent(result);
        }

        FunctionKind kind = dmn.getKind();
        switch (kind) {
            case FEEL:
                result.setKind(Kind.FEEL);
                break;
            case JAVA:
                result.setKind(Kind.JAVA);
                break;
            case PMML:
                result.setKind(Kind.PMML);
                break;
            default:
                result.setKind(Kind.FEEL);
                break;
        }

        for (org.kie.dmn.model.api.InformationItem ii : dmn.getFormalParameter()) {
            InformationItem iiConverted = InformationItemPropertyConverter.wbFromDMN(ii);
            if (iiConverted != null) {
                iiConverted.setParent(result);
            }
            result.getFormalParameter().add(iiConverted);
        }

        return result;
    }

    public static org.kie.dmn.model.api.FunctionDefinition dmnFromWB(final FunctionDefinition wb) {
        if (wb == null) {
            return null;
        }
        org.kie.dmn.model.api.FunctionDefinition result = new org.kie.dmn.model.v1_2.TFunctionDefinition();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);
        result.setExpression(ExpressionPropertyConverter.dmnFromWB(wb.getExpression()));

        Kind kind = wb.getKind();
        switch (kind) {
            case FEEL:
                result.setKind(FunctionKind.FEEL);
                break;
            case JAVA:
                result.setKind(FunctionKind.JAVA);
                break;
            case PMML:
                result.setKind(FunctionKind.PMML);
                break;
            default:
                result.setKind(FunctionKind.FEEL);
                break;
        }

        for (InformationItem ii : wb.getFormalParameter()) {
            org.kie.dmn.model.api.InformationItem iiConverted = InformationItemPropertyConverter.dmnFromWB(ii);
            if (iiConverted != null) {
                iiConverted.setParent(result);
            }
            result.getFormalParameter().add(iiConverted);
        }

        return result;
    }
}