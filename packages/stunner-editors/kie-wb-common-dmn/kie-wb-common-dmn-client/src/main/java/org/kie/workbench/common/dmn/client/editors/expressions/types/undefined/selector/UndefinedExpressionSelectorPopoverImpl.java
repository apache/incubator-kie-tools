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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverImpl;

@ApplicationScoped
public class UndefinedExpressionSelectorPopoverImpl extends AbstractPopoverImpl<UndefinedExpressionSelectorPopoverView, UndefinedExpressionGrid> implements UndefinedExpressionSelectorPopoverView.Presenter {

    private TranslationService translationService;

    public UndefinedExpressionSelectorPopoverImpl() {
        //CDI proxy
    }

    @Inject
    public UndefinedExpressionSelectorPopoverImpl(final UndefinedExpressionSelectorPopoverView view,
                                                  final TranslationService translationService,
                                                  final @DMNEditor Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier) {
        super(view);
        this.translationService = translationService;

        view.init(this);
        view.setExpressionEditorDefinitions(expressionEditorDefinitionsSupplier
                                                    .get()
                                                    .stream()
                                                    .filter(ExpressionEditorDefinition::isUserSelectable)
                                                    .filter(definition -> definition.getModelClass().isPresent())
                                                    .collect(Collectors.toList()));
    }

    @Override
    public void onExpressionEditorDefinitionSelected(final ExpressionEditorDefinition definition) {
        binding.ifPresent(b -> {
            b.onExpressionTypeChanged(definition.getType());
            view.hide();
        });
    }

    @Override
    public String getPopoverTitle() {
        return translationService.getTranslation(DMNEditorConstants.UndefinedExpressionEditor_SelectorTitle);
    }
}
