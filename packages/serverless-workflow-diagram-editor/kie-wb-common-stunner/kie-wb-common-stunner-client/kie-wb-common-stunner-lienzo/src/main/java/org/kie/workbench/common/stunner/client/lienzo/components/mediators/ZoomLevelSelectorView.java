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


package org.kie.workbench.common.stunner.client.lienzo.components.mediators;

import elemental2.dom.Event;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import elemental2.dom.KeyboardEvent;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.j2cl.tools.di.ui.templates.client.annotation.DataField;
import org.kie.j2cl.tools.di.ui.templates.client.annotation.EventHandler;
import org.kie.j2cl.tools.di.ui.templates.client.annotation.ForEvent;
import org.kie.j2cl.tools.di.ui.templates.client.annotation.Templated;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class ZoomLevelSelectorView
        implements ZoomLevelSelector.View {

    static final String CSS_DROP_UP = "dropup";

    @Inject
    @DataField
    HTMLButtonElement previewButton;

    @Inject
    @DataField
    HTMLButtonElement decreaseButton;

    @Inject
    @DataField
    HTMLButtonElement increaseButton;

    @Inject
    @DataField
    HTMLButtonElement resetButton;

    @Inject
    @DataField
    HTMLDivElement dropDownPanelGroup;

    @Inject
    @DataField
    HTMLDivElement dropDownPanel;

    @Inject
    @DataField
    HTMLButtonElement dropDownButton;

    @Inject
    @DataField
    @Named("span")
    HTMLElement dropDownText;

    @Inject
    @DataField
    HTMLUListElement dropDownMenu;

    @Inject
    @Any
    ManagedInstance<ZoomLevelSelectorItem> items;

    @Inject
    ClientTranslationService translationService;

    private ZoomLevelSelector presenter;

    @Override
    public void init(final ZoomLevelSelector presenter) {
        this.presenter = presenter;
        setTooltip(previewButton, translationService.getNotNullValue(CoreTranslationMessages.PREVIEW));
        setTooltip(resetButton, translationService.getNotNullValue(CoreTranslationMessages.RESET));
        setTooltip(increaseButton, translationService.getNotNullValue(CoreTranslationMessages.INCREASE));
        setTooltip(decreaseButton, translationService.getNotNullValue(CoreTranslationMessages.DECREASE));
    }

    @Override
    public void add(final String text,
                    final Command onClick) {
        final ZoomLevelSelectorItem item = items.get();
        item.setText(text);
        item.setOnClick(onClick);
        dropDownMenu.appendChild(item.getElement());
    }

    @Override
    public void clear() {
        DOMUtil.removeAllChildren(dropDownMenu);
    }

    public void setSelectedValue(String selectedItem) {
        dropDownText.textContent = (selectedItem);
    }

    @Override
    public void setText(String text) {
        dropDownText.textContent = (text);
    }

    @Override
    public void setEnabled(boolean enabled) {
        resetButton.disabled = (!enabled);
        increaseButton.disabled = (!enabled);
        dropDownButton.disabled = (!enabled);
        decreaseButton.disabled = (!enabled);
    }

    @Override
    public void dropUp() {
        dropDownPanelGroup.className = (dropDownPanelGroup.className + " " + CSS_DROP_UP);
    }

    @Override
    public void updatePreviewButton(boolean enabled) {
        previewButton.disabled = (!enabled);
    }

    @EventHandler("previewButton")
    void onPreview(@ForEvent("click") Event event) {
        presenter.onPreview();
    }

    @EventHandler("increaseButton")
    void onIncreaseLevel(@ForEvent("click") Event event) {
        presenter.onIncreaseLevel();
    }

    @EventHandler("decreaseButton")
    void onDecreaseLevel(@ForEvent("click") Event event) {
        presenter.onDecreaseLevel();
    }

    @EventHandler("resetButton")
    void onReset(@ForEvent("click") Event event) {
        presenter.onScaleToFitSize();
    }

    @EventHandler("dropDownButton")
    void onDropDownKeyDown(@ForEvent("keydown") KeyboardEvent event) {
        onDropDownKeyEvent(event);
    }

    @EventHandler("dropDownButton")
    void onDropDownKeyUp(@ForEvent("keyup") KeyboardEvent event) {
        onDropDownKeyEvent(event);
    }

    @EventHandler("dropDownButton")
    void onDropDownKeyPress(@ForEvent("keypress") KeyboardEvent event) {
        onDropDownKeyEvent(event);
    }

    private void onDropDownKeyEvent(KeyboardEvent event) {
        event.preventDefault();
        event.stopPropagation();
    }

    @PreDestroy
    public void destroy() {
        clear();
        items.destroyAll();
        presenter = null;
    }

    private static void setTooltip(final HTMLButtonElement button,
                                   final String text) {
        button.setAttribute("data-placement", "top");
        button.title = (text);
    }
}
