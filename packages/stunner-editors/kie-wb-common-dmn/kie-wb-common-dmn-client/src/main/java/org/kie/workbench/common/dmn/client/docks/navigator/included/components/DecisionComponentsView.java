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

package org.kie.workbench.common.dmn.client.docks.navigator.included.components;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyUpEvent;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.common.RemoveHelper;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker.CallbackFunction;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionComponentsView_EnterText;
import static org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker.$;

@Dependent
@Templated
public class DecisionComponentsView implements DecisionComponents.View {

    @DataField("drg-element-filter")
    private final HTMLSelectElement drgElementFilter;

    @DataField("term-filter")
    private final HTMLInputElement termFilter;

    @DataField("list")
    private final HTMLDivElement list;

    @DataField("empty-state")
    private final HTMLDivElement emptyState;

    @DataField("loading")
    private final HTMLDivElement loading;

    @DataField("components-counter")
    private final HTMLDivElement componentsCounter;

    private final TranslationService translationService;

    private DecisionComponents presenter;

    @Inject
    public DecisionComponentsView(final HTMLSelectElement drgElementFilter,
                                  final HTMLInputElement termFilter,
                                  final HTMLDivElement list,
                                  final HTMLDivElement emptyState,
                                  final HTMLDivElement loading,
                                  final HTMLDivElement componentsCounter,
                                  final TranslationService translationService) {
        this.drgElementFilter = drgElementFilter;
        this.termFilter = termFilter;
        this.list = list;
        this.emptyState = emptyState;
        this.loading = loading;
        this.componentsCounter = componentsCounter;
        this.translationService = translationService;
    }

    @PostConstruct
    public void init() {
        getDrgElementFilter().selectpicker("refresh");
        getDrgElementFilter().on("hidden.bs.select", onDrgElementFilterChange());
        setupTermFilterPlaceholder();
    }

    @Override
    public void init(final DecisionComponents presenter) {
        this.presenter = presenter;
    }

    private void setupTermFilterPlaceholder() {
        termFilter.placeholder = translationService.format(DecisionComponentsView_EnterText);
    }

    @EventHandler("term-filter")
    public void onTermFilterChange(final KeyUpEvent e) {
        presenter.applyTermFilter(termFilter.value);
    }

    CallbackFunction onDrgElementFilterChange() {
        return event -> presenter.applyDrgElementFilterFilter(event.target.value);
    }

    @Override
    public void clear() {
        RemoveHelper.removeChildren(list);
        hide(emptyState);
    }

    @Override
    public void addListItem(final HTMLElement htmlElement) {
        list.appendChild(htmlElement);
    }

    @Override
    public void showEmptyState() {
        show(emptyState);
    }

    @Override
    public void showLoading() {
        show(loading);
    }

    @Override
    public void hideLoading() {
        hide(loading);
    }

    @Override
    public void disableFilterInputs() {
        termFilter.value = "";
        getDrgElementFilter().selectpicker("val", "");
        disableFilterInputs(true);
    }

    @Override
    public void enableFilterInputs() {
        disableFilterInputs(false);
    }

    @Override
    public void setComponentsCounter(final Integer count) {
        componentsCounter.textContent = count.toString();
    }

    private void disableFilterInputs(final boolean disabled) {
        termFilter.disabled = disabled;
        drgElementFilter.disabled = disabled;
        getDrgElementFilter().selectpicker("refresh");
    }

    JQuerySelectPicker getDrgElementFilter() {
        return $(drgElementFilter);
    }
}
