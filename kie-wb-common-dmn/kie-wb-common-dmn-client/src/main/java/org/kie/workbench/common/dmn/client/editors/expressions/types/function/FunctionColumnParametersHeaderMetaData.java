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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

public class FunctionColumnParametersHeaderMetaData implements GridColumn.HeaderMetaData {

    private static final String PARAMETER_COLUMN_GROUP = "FunctionColumnParametersHeaderMetaData$Parameters";

    private final Supplier<FunctionDefinition.Kind> expressionLanguageSupplier;
    private final Supplier<List<InformationItem>> formalParametersSupplier;

    public FunctionColumnParametersHeaderMetaData(final Supplier<FunctionDefinition.Kind> expressionLanguageSupplier,
                                                  final Supplier<List<InformationItem>> formalParametersSupplier) {
        this.expressionLanguageSupplier = expressionLanguageSupplier;
        this.formalParametersSupplier = formalParametersSupplier;
    }

    @Override
    public String getColumnGroup() {
        return PARAMETER_COLUMN_GROUP;
    }

    @Override
    public void setColumnGroup(final String columnGroup) {
        throw new UnsupportedOperationException("Group cannot be set.");
    }

    @Override
    public String getTitle() {
        //TODO {manstis} We need the FunctionGridRendered to render the two sections as different cells
        final StringBuffer sb = new StringBuffer(getExpressionLanguageTitle());
        sb.append(" : ");
        sb.append(getFormalParametersTitle());
        return sb.toString();
    }

    public String getExpressionLanguageTitle() {
        return expressionLanguageSupplier.get().code();
    }

    public String getFormalParametersTitle() {
        final List<InformationItem> formalParameters = formalParametersSupplier.get();
        final StringBuffer sb = new StringBuffer();
        sb.append("(");
        if (!formalParameters.isEmpty()) {
            sb.append(formalParameters.stream().map(ii -> ii.getName().getValue()).collect(Collectors.joining(", ")));
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void setTitle(final String title) {
        throw new UnsupportedOperationException("Title is derived from the Decision Table Hit Policy and cannot be set on the HeaderMetaData.");
    }
}