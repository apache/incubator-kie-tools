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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;

@Templated
@ApplicationScoped
public class ParametersPopoverViewImpl extends AbstractPopoverViewImpl implements ParametersPopoverView {

    @DataField("parametersContainer")
    private Div parametersContainer;

    @DataField("addParameter")
    private Div addParameter;

    private ManagedInstance<ParameterView> parameterViews;
    private final List<ParameterView> parameterViewInstances = new ArrayList<>();

    private Presenter presenter;

    public ParametersPopoverViewImpl() {
        //CDI proxy
    }

    @Inject
    public ParametersPopoverViewImpl(final Div parametersContainer,
                                     final Div addParameter,
                                     final ManagedInstance<ParameterView> parameterViews,
                                     final Div popoverElement,
                                     final Div popoverContentElement,
                                     final JQueryProducer.JQuery<Popover> jQueryPopover) {
        super(popoverElement,
              popoverContentElement,
              jQueryPopover);

        this.parametersContainer = parametersContainer;
        this.addParameter = addParameter;
        this.parameterViews = parameterViews;

        this.popoverElement = popoverElement;
        this.popoverContentElement = popoverContentElement;
        this.jQueryPopover = jQueryPopover;
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setParameters(final List<InformationItem> parameters) {
        parameterViewInstances.clear();
        parameterViewInstances.addAll(parameters
                                              .stream()
                                              .map(this::makeParameterView)
                                              .collect(Collectors.toList()));

        DOMUtil.removeAllChildren(parametersContainer);
        parameterViewInstances.forEach(parameterView -> parametersContainer.appendChild(parameterView.getElement()));
    }

    private ParameterView makeParameterView(final InformationItem parameter) {
        final ParameterView parameterView = parameterViews.get();
        parameterView.setName(parameter.getName().getValue());
        parameterView.setTypeRef(parameter);
        parameterView.addRemoveClickHandler(() -> presenter.removeParameter(parameter));
        parameterView.addParameterNameChangeHandler((name) -> presenter.updateParameterName(parameter, name));
        parameterView.addParameterTypeRefChangeHandler((typeRef) -> presenter.updateParameterTypeRef(parameter, typeRef));
        return parameterView;
    }

    @Override
    public void focusParameter(final int index) {
        if (index < 0 || index >= parameterViewInstances.size()) {
            return;
        }
        parameterViewInstances.get(index).focus();
    }

    @EventHandler("addParameter")
    @SinkNative(Event.ONCLICK)
    @SuppressWarnings("unused")
    public void onClickAddParameter(final Event event) {
        presenter.addParameter();
    }
}
