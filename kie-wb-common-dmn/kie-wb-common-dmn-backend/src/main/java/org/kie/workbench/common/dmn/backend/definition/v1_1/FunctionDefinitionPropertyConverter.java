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

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.kie.dmn.model.api.FunctionKind;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition.Kind;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocumentModel;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;

public class FunctionDefinitionPropertyConverter {

    public static FunctionDefinition wbFromDMN(final org.kie.dmn.model.api.FunctionDefinition dmn,
                                               final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        if (dmn == null) {
            return null;
        }
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);
        final Expression expression = ExpressionPropertyConverter.wbFromDMN(dmn.getExpression(),
                                                                            hasComponentWidthsConsumer);
        final FunctionDefinition result = new FunctionDefinition(id,
                                                                 description,
                                                                 typeRef,
                                                                 expression);
        if (expression != null) {
            expression.setParent(result);
        }

        final FunctionKind kind = dmn.getKind();
        switch (kind) {
            case FEEL:
                result.setKind(Kind.FEEL);
                break;
            case JAVA:
                result.setKind(Kind.JAVA);
                break;
            case PMML:
                result.setKind(Kind.PMML);
                convertPMMLFunctionExpression(result);
                break;
            default:
                result.setKind(Kind.FEEL);
                break;
        }

        for (org.kie.dmn.model.api.InformationItem ii : dmn.getFormalParameter()) {
            final InformationItem iiConverted = InformationItemPropertyConverter.wbFromDMN(ii);
            if (iiConverted != null) {
                iiConverted.setParent(result);
            }
            result.getFormalParameter().add(iiConverted);
        }

        return result;
    }

    private static void convertPMMLFunctionExpression(final FunctionDefinition function) {
        final Expression expression = function.getExpression();
        if (expression instanceof Context) {
            final Context context = (Context) expression;
            context.getContextEntry().forEach(FunctionDefinitionPropertyConverter::convertContextEntryExpression);
        }
    }

    private static void convertContextEntryExpression(final ContextEntry contextEntry) {
        final Expression expression = contextEntry.getExpression();
        if (expression instanceof LiteralExpression) {
            final LiteralExpression le = (LiteralExpression) expression;
            final String variableName = contextEntry.getVariable().getName().getValue();
            if (Objects.equals(LiteralExpressionPMMLDocument.VARIABLE_DOCUMENT,
                               variableName)) {
                contextEntry.setExpression(convertLiteralExpressionToPMMLDocument(le));
            } else if (Objects.equals(LiteralExpressionPMMLDocumentModel.VARIABLE_MODEL,
                                      variableName)) {
                contextEntry.setExpression(convertLiteralExpressionToPMMLDocumentModel(le));
            }
        }
    }

    private static LiteralExpressionPMMLDocument convertLiteralExpressionToPMMLDocument(final LiteralExpression le) {
        return new LiteralExpressionPMMLDocument(le.getId(),
                                                 le.getDescription(),
                                                 le.getTypeRef(),
                                                 le.getText(),
                                                 le.getImportedValues(),
                                                 le.getExpressionLanguage());
    }

    private static LiteralExpressionPMMLDocumentModel convertLiteralExpressionToPMMLDocumentModel(final LiteralExpression le) {
        return new LiteralExpressionPMMLDocumentModel(le.getId(),
                                                      le.getDescription(),
                                                      le.getTypeRef(),
                                                      le.getText(),
                                                      le.getImportedValues(),
                                                      le.getExpressionLanguage());
    }

    public static org.kie.dmn.model.api.FunctionDefinition dmnFromWB(final FunctionDefinition wb,
                                                                     final Consumer<ComponentWidths> componentWidthsConsumer) {
        if (wb == null) {
            return null;
        }
        final org.kie.dmn.model.api.FunctionDefinition result = new org.kie.dmn.model.v1_2.TFunctionDefinition();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);
        result.setExpression(ExpressionPropertyConverter.dmnFromWB(wb.getExpression(),
                                                                   componentWidthsConsumer));

        final Kind kind = wb.getKind();
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
            final org.kie.dmn.model.api.InformationItem iiConverted = InformationItemPropertyConverter.dmnFromWB(ii);
            if (iiConverted != null) {
                iiConverted.setParent(result);
            }
            result.getFormalParameter().add(iiConverted);
        }

        return result;
    }
}