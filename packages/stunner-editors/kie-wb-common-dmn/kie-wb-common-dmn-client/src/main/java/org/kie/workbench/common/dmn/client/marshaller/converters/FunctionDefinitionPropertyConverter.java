/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.marshaller.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import jsinterop.base.Js;
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
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITFunctionDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITFunctionKind;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;

public class FunctionDefinitionPropertyConverter {

    public static FunctionDefinition wbFromDMN(final JSITFunctionDefinition dmn,
                                               final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        if (Objects.isNull(dmn)) {
            return null;
        }
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());

        Expression expression = null;
        final JSITExpression jsiWrapped = dmn.getExpression();
        if (Objects.nonNull(jsiWrapped)) {
            final JSITExpression jsiExpression = Js.uncheckedCast(JsUtils.getUnwrappedElement(jsiWrapped));
            expression = ExpressionPropertyConverter.wbFromDMN(jsiExpression,
                                                               Js.uncheckedCast(dmn),
                                                               hasComponentWidthsConsumer);
        }

        final FunctionDefinition result = new FunctionDefinition(id,
                                                                 description,
                                                                 typeRef,
                                                                 expression);
        if (Objects.nonNull(expression)) {
            expression.setParent(result);
        }

        //JSITFunctionKind is a String JSO so convert into the real type
        final String sKind = Js.uncheckedCast(dmn.getKind());
        final Kind kind = Kind.fromValue(sKind);
        switch (kind) {
            case FEEL:
                result.setKind(Kind.FEEL);
                break;
            case JAVA:
                result.setKind(Kind.JAVA);
                break;
            case PMML:
                result.setKind(Kind.PMML);
                convertPMMLFunctionExpression(result, hasComponentWidthsConsumer);
                break;
            default:
                result.setKind(Kind.FEEL);
                break;
        }

        final List<JSITInformationItem> jsiInformationItems = dmn.getFormalParameter();
        for (int i = 0; i < jsiInformationItems.size(); i++) {
            final JSITInformationItem jsiInformationItem = Js.uncheckedCast(jsiInformationItems.get(i));
            final InformationItem iiConverted = InformationItemPropertyConverter.wbFromDMN(jsiInformationItem);
            if (Objects.nonNull(iiConverted)) {
                iiConverted.setParent(result);
            }
            result.getFormalParameter().add(iiConverted);
        }

        return result;
    }

    private static void convertPMMLFunctionExpression(final FunctionDefinition function,
                                                      final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        final Expression expression = function.getExpression();
        if (expression instanceof Context) {
            final Context context = (Context) expression;
            context.getContextEntry().forEach(ce -> convertContextEntryExpression(ce, hasComponentWidthsConsumer));
        }
    }

    private static void convertContextEntryExpression(final ContextEntry contextEntry,
                                                      final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        final Expression expression = contextEntry.getExpression();
        if (expression instanceof LiteralExpression) {
            final LiteralExpression le = (LiteralExpression) expression;
            final String variableName = contextEntry.getVariable().getName().getValue();
            if (Objects.equals(LiteralExpressionPMMLDocument.VARIABLE_DOCUMENT,
                               variableName)) {
                final LiteralExpressionPMMLDocument e = convertLiteralExpressionToPMMLDocument(le);
                //Ensure ComponentWidths are updated for the converted LiteralExpression
                hasComponentWidthsConsumer.accept(e.getId().getValue(), e);
                contextEntry.setExpression(e);
            } else if (Objects.equals(LiteralExpressionPMMLDocumentModel.VARIABLE_MODEL,
                                      variableName)) {
                final LiteralExpressionPMMLDocumentModel e = convertLiteralExpressionToPMMLDocumentModel(le);
                //Ensure ComponentWidths are updated for the converted LiteralExpression
                hasComponentWidthsConsumer.accept(e.getId().getValue(), e);
                contextEntry.setExpression(e);
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

    public static JSITFunctionDefinition dmnFromWB(final FunctionDefinition wb,
                                                   final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        if (Objects.isNull(wb)) {
            return null;
        }
        final JSITFunctionDefinition result = JSITFunctionDefinition.newInstance();
        result.setId(wb.getId().getValue());
        // TODO {gcardosi} add because  present in original json
        if (Objects.isNull(result.getFormalParameter())) {
            result.setFormalParameter(new ArrayList<>());
        }
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        description.ifPresent(result::setDescription);
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);
        result.setExpression(ExpressionPropertyConverter.dmnFromWB(wb.getExpression(),
                                                                   componentWidthsConsumer));

        final Kind kind = wb.getKind();
        switch (kind) {
            case FEEL:
                result.setKind(Js.uncheckedCast(JSITFunctionKind.FEEL.value()));
                break;
            case JAVA:
                result.setKind(Js.uncheckedCast(JSITFunctionKind.JAVA.value()));
                break;
            case PMML:
                result.setKind(Js.uncheckedCast(JSITFunctionKind.PMML.value()));
                break;
            default:
                result.setKind(Js.uncheckedCast(JSITFunctionKind.FEEL.value()));
                break;
        }

        for (InformationItem ii : wb.getFormalParameter()) {
            final JSITInformationItem iiConverted = InformationItemPropertyConverter.dmnFromWB(ii);
            result.addFormalParameter(iiConverted);
        }

        return result;
    }
}