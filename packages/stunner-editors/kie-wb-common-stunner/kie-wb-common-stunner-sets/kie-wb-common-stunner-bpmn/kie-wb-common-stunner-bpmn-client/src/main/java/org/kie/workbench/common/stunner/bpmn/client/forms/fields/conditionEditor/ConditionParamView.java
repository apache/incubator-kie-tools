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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Templated
@Dependent
public class ConditionParamView
        implements IsElement,
                   ConditionParamPresenter.View {

    private static final String DATA_CONTENT_ATTR = "data-content";

    @Inject
    @DataField("param-group")
    private HTMLDivElement paramGroup;

    @Inject
    @DataField("param-label")
    private HTMLLabelElement paramLabel;

    @Inject
    @DataField("param-input")
    private HTMLInputElement paramInput;

    @Inject
    @DataField("param-input-help")
    private HTMLAnchorElement paramInputHelp;

    @Inject
    private JQueryProducer.JQuery<Popover> paramInputHelpPopover;

    @Inject
    @DataField("param-error")
    @Named("span")
    private HTMLElement paramError;

    private ConditionParamPresenter presenter;

    @PostConstruct
    public void init() {
        paramInput.type = "text";
        paramInputHelpPopover.wrap(paramInputHelp).popover();
    }

    @Override
    public void init(ConditionParamPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setName(String name) {
        paramLabel.textContent = (name);
    }

    @Override
    public String getName() {
        return paramLabel.textContent;
    }

    @Override
    public void setHelp(String help) {
        if (!isEmpty(help)) {
            paramInputHelp.setAttribute(DATA_CONTENT_ATTR, help);
            paramInputHelp.style.removeProperty("display");
        }
    }

    @Override
    public void clear() {
        setValue(null);
        clearError();
    }

    @Override
    public String getValue() {
        return paramInput.value;
    }

    @Override
    public void setValue(String value) {
        paramInput.value = (value);
    }

    @Override
    public void clearError() {
        DOMUtil.removeCSSClass(paramGroup, "has-error");
        paramError.textContent = (null);
    }

    @Override
    public void setError(String error) {
        DOMUtil.addCSSClass(paramGroup, "has-error");
        paramError.textContent = (error);
    }

    @Override
    public void setReadonly(boolean readonly) {
        paramInput.readOnly = (readonly);
    }

    @EventHandler("param-input")
    public void onValueChange(@ForEvent("change") final elemental2.dom.Event event) {
        presenter.onValueChange();
    }
}
