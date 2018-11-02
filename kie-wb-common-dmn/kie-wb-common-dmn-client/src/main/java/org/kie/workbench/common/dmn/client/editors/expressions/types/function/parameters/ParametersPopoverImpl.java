/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

@ApplicationScoped
public class ParametersPopoverImpl implements ParametersPopoverView.Presenter {

    private ParametersPopoverView view;
    private Optional<HasParametersControl> binding = Optional.empty();

    public ParametersPopoverImpl() {
        //CDI proxy
    }

    @Inject
    public ParametersPopoverImpl(final ParametersPopoverView view) {
        this.view = view;
        view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void bind(final HasParametersControl bound,
                     final int uiRowIndex,
                     final int uiColumnIndex) {
        binding = Optional.ofNullable(bound);
        binding.ifPresent(b -> {
            view.setParameters(b.getParameters());
        });
    }

    private void refresh() {
        binding.ifPresent(b -> {
            view.setParameters(b.getParameters());
            focusLastParameter(b);
        });
    }

    @Override
    public void show(final Optional<String> editorTitle) {
        binding.ifPresent(b -> {
            view.show(editorTitle);
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
    public void hide() {
        binding.ifPresent(b -> view.hide());
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
        binding.ifPresent(b -> b.updateParameterName(parameter,
                                                     name));
    }

    @Override
    public void updateParameterTypeRef(final InformationItem parameter,
                                       final QName typeRef) {
        binding.ifPresent(b -> b.updateParameterTypeRef(parameter,
                                                        typeRef));
    }
}
