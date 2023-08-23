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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverImpl;

@ApplicationScoped
public class KindPopoverImpl extends AbstractPopoverImpl<KindPopoverView, HasKindSelectControl> implements KindPopoverView.Presenter {

    private TranslationService translationService;

    public KindPopoverImpl() {
        //CDI proxy
    }

    @Inject
    public KindPopoverImpl(final KindPopoverView view,
                           final TranslationService translationService) {
        super(view);
        this.translationService = translationService;

        view.init(this);
        view.setFunctionKinds(FunctionDefinition.Kind.values());
    }

    @Override
    public String getPopoverTitle() {
        return translationService.getTranslation(DMNEditorConstants.FunctionEditor_SelectFunctionKind);
    }

    @Override
    public void onFunctionKindSelected(final FunctionDefinition.Kind kind) {
        binding.ifPresent(b -> {
            b.setFunctionKind(kind);
            view.hide();
        });
    }
}