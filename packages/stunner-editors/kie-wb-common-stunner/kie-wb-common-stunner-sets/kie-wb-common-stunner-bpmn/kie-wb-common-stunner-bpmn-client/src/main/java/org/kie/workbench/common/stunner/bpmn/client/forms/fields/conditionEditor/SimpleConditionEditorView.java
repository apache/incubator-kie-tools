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
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;

@Templated
@Dependent
public class SimpleConditionEditorView
        implements IsElement,
                   SimpleConditionEditorPresenter.View {

    private static final String VARIABLE_SELECTOR_HELP = "SimpleConditionEditorView.VariableSelectorHelp";
    private static final String CONDITION_SELECTOR_HELP = "SimpleConditionEditorView.ConditionSelectorHelp";
    private static final String DATA_CONTENT_ATTR = "data-content";

    private SimpleConditionEditorPresenter presenter;

    @Inject
    @DataField("variable-selector-form")
    private HTMLDivElement variableSelectorForm;

    @Inject
    @DataField("variable-selector")
    private LiveSearchDropDown<String> variableSelectorDropDown;

    @Inject
    @DataField("variable-selector-help")
    private HTMLAnchorElement variableSelectorHelp;

    @Inject
    private JQueryProducer.JQuery<Popover> variableSelectorHelpPopover;

    @Inject
    @DataField("variable-selector-error")
    @Named("span")
    private HTMLElement variableSelectorError;

    @Inject
    @DataField("condition-selector-form")
    private HTMLDivElement conditionSelectorForm;

    @Inject
    @DataField("condition-selector")
    private LiveSearchDropDown<String> conditionSelectorDropDown;

    @Inject
    @DataField("condition-selector-error")
    @Named("span")
    private HTMLElement conditionSelectorError;

    @Inject
    @DataField("condition-selector-help")
    private HTMLAnchorElement conditionSelectorHelp;

    @Inject
    private JQueryProducer.JQuery<Popover> conditionSelectorHelpPopover;

    @Inject
    @DataField("condition-params")
    private HTMLDivElement conditionParams;

    @Inject
    private ClientTranslationService translationService;

    @Override
    public void init(SimpleConditionEditorPresenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        variableSelectorHelp.setAttribute(DATA_CONTENT_ATTR, translationService.getValue(VARIABLE_SELECTOR_HELP));
        variableSelectorHelpPopover.wrap(variableSelectorHelp).popover();
        conditionSelectorHelp.setAttribute(DATA_CONTENT_ATTR, translationService.getValue(CONDITION_SELECTOR_HELP));
        conditionSelectorHelpPopover.wrap(conditionSelectorHelp).popover();
    }

    @Override
    public LiveSearchDropDown<String> getVariableSelectorDropDown() {
        return variableSelectorDropDown;
    }

    @Override
    public LiveSearchDropDown<String> getConditionSelectorDropDown() {
        return conditionSelectorDropDown;
    }

    @Override
    public void setVariableError(String error) {
        DOMUtil.addCSSClass(variableSelectorForm, "has-error");
        variableSelectorError.textContent = (error);
    }

    @Override
    public void clearVariableError() {
        DOMUtil.removeCSSClass(variableSelectorForm, "has-error");
        variableSelectorError.textContent = (null);
    }

    @Override
    public void setConditionError(String error) {
        DOMUtil.addCSSClass(conditionSelectorForm, "has-error");
        conditionSelectorError.textContent = (error);
    }

    @Override
    public void clearConditionError() {
        DOMUtil.removeCSSClass(conditionSelectorForm, "has-error");
        conditionSelectorError.textContent = (null);
    }

    @Override
    public void removeParams() {
        DOMUtil.removeAllChildren(conditionParams);
    }

    @Override
    public void addParam(HTMLElement param) {
        conditionParams.appendChild(param);
    }
}
