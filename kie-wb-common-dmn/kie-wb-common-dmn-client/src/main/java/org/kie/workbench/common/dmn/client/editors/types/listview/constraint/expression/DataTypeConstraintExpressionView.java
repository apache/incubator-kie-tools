/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLTextAreaElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeConstraintExpressionView_Placeholder;

@Templated
@Dependent
public class DataTypeConstraintExpressionView implements DataTypeConstraintExpression.View {

    @DataField("expression")
    private final HTMLTextAreaElement expression;

    private final TranslationService translationService;

    private DataTypeConstraintExpression presenter;

    @Inject
    public DataTypeConstraintExpressionView(final HTMLTextAreaElement expression,
                                            final TranslationService translationService) {
        this.expression = expression;
        this.translationService = translationService;
    }

    @Override
    public void init(final DataTypeConstraintExpression presenter) {
        this.presenter = presenter;
        setupPlaceholder();
    }

    private void setupPlaceholder() {
        expression.setAttribute("placeholder", translationService.format(DataTypeConstraintExpressionView_Placeholder));
    }

    @Override
    public String getExpressionValue() {
        return expression.value;
    }

    @Override
    public void setExpressionValue(final String value) {
        expression.value = value;
    }
}
