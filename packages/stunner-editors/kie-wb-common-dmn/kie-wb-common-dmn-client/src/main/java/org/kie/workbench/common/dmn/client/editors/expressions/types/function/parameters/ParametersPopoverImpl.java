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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverImpl;

@ApplicationScoped
public class ParametersPopoverImpl extends AbstractPopoverImpl<ParametersPopoverView, HasParametersControl> implements ParametersPopoverView.Presenter {

    private TranslationService translationService;

    public ParametersPopoverImpl() {
        //CDI proxy
    }

    @Inject
    public ParametersPopoverImpl(final ParametersPopoverView view,
                                 final TranslationService translationService) {
        super(view);
        this.translationService = translationService;

        view.init(this);
    }

    @Override
    public String getPopoverTitle() {
        return translationService.getTranslation(DMNEditorConstants.FunctionEditor_EditParametersTitle);
    }

    @Override
    public void bind(final HasParametersControl bound,
                     final int uiRowIndex,
                     final int uiColumnIndex) {
        super.bind(bound, uiRowIndex, uiColumnIndex);
        binding.ifPresent(b -> view.setParameters(b.getParameters()));
    }

    private void refresh() {
        binding.ifPresent(b -> {
            view.setParameters(b.getParameters());
            focusLastParameter(b);
        });
    }

    @Override
    public void show() {
        binding.ifPresent(b -> {
            view.show(Optional.ofNullable(getPopoverTitle()));
            focusLastParameter(b);
        });
    }

    private void focusLastParameter(final HasParametersControl hasParameters) {
        final List<InformationItem> parameters = hasParameters.getParameters();
        if (!parameters.isEmpty()) {
            view.focusParameter(parameters.size() - 1);
        }
    }

    @Override
    public void addParameter() {
        binding.ifPresent(b -> b.addParameter(this::refresh));
    }

    @Override
    public void removeParameter(final InformationItem parameter) {
        binding.ifPresent(b -> b.removeParameter(parameter,
                                                 this::refresh));
    }

    @Override
    public void updateParameterName(final InformationItem parameter,
                                    final String name) {
        binding.ifPresent(b -> {
            // See https://issues.redhat.com/browse/DROOLS-4907
            final String trimmedName = Objects.nonNull(name) ? name.trim() : "";
            if (!Objects.equals(parameter.getName().getValue(), trimmedName)) {
                b.updateParameterName(parameter,
                                      trimmedName,
                                      () -> updateViewParameterName(b, parameter, trimmedName));
            } else if (!Objects.equals(name, trimmedName)) {
                updateViewParameterName(b, parameter, trimmedName);
            }
        });
    }

    private void updateViewParameterName(final HasParametersControl binding,
                                         final InformationItem parameter,
                                         final String name) {
        final int index = binding.getParameters().indexOf(parameter);
        view.updateParameterName(index, name);
    }

    @Override
    public void updateParameterTypeRef(final InformationItem parameter,
                                       final QName typeRef) {
        binding.ifPresent(b -> b.updateParameterTypeRef(parameter,
                                                        typeRef));
    }
}
