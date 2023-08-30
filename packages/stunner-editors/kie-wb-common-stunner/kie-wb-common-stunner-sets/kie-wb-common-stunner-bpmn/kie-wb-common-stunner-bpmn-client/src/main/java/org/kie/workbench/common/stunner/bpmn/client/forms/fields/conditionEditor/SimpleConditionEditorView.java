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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;

@Templated
public class SimpleConditionEditorView
        implements IsElement,
                   SimpleConditionEditorPresenter.View {

    private static final String VARIABLE_SELECTOR_HELP = "SimpleConditionEditorView.VariableSelectorHelp";
    private static final String CONDITION_SELECTOR_HELP = "SimpleConditionEditorView.ConditionSelectorHelp";
    private static final String DATA_CONTENT_ATTR = "data-content";

    private SimpleConditionEditorPresenter presenter;

    @Inject
    @DataField("variable-selector-form")
    private Div variableSelectorForm;

    @Inject
    @DataField("variable-selector")
    private LiveSearchDropDown<String> variableSelectorDropDown;

    @Inject
    @DataField("variable-selector-help")
    private Anchor variableSelectorHelp;

    @Inject
    private JQueryProducer.JQuery<Popover> variableSelectorHelpPopover;

    @Inject
    @DataField("variable-selector-error")
    private Span variableSelectorError;

    @Inject
    @DataField("condition-selector-form")
    private Div conditionSelectorForm;

    @Inject
    @DataField("condition-selector")
    private LiveSearchDropDown<String> conditionSelectorDropDown;

    @Inject
    @DataField("condition-selector-error")
    private Span conditionSelectorError;

    @Inject
    @DataField("condition-selector-help")
    private Anchor conditionSelectorHelp;

    @Inject
    private JQueryProducer.JQuery<Popover> conditionSelectorHelpPopover;

    @Inject
    @DataField("condition-params")
    private Div conditionParams;

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
        variableSelectorError.setTextContent(error);
    }

    @Override
    public void clearVariableError() {
        DOMUtil.removeCSSClass(variableSelectorForm, "has-error");
        variableSelectorError.setTextContent(null);
    }

    @Override
    public void setConditionError(String error) {
        DOMUtil.addCSSClass(conditionSelectorForm, "has-error");
        conditionSelectorError.setTextContent(error);
    }

    @Override
    public void clearConditionError() {
        DOMUtil.removeCSSClass(conditionSelectorForm, "has-error");
        conditionSelectorError.setTextContent(null);
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
