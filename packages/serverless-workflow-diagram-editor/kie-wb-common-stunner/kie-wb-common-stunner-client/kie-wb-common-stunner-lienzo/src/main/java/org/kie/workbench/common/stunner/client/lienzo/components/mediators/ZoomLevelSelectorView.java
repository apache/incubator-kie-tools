/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.components.mediators;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.Composite;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class ZoomLevelSelectorView
        extends Composite
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
    void onPreview(ClickEvent event) {
        presenter.onPreview();
    }

    @EventHandler("increaseButton")
    void onIncreaseLevel(ClickEvent event) {
        presenter.onIncreaseLevel();
    }

    @EventHandler("decreaseButton")
    void onDecreaseLevel(ClickEvent event) {
        presenter.onDecreaseLevel();
    }

    @EventHandler("resetButton")
    void onReset(ClickEvent event) {
        presenter.onScaleToFitSize();
    }

    @EventHandler("dropDownButton")
    void onDropDownKeyDown(KeyDownEvent event) {
        onDropDownKeyEvent(event);
    }

    @EventHandler("dropDownButton")
    void onDropDownKeyUp(KeyUpEvent event) {
        onDropDownKeyEvent(event);
    }

    @EventHandler("dropDownButton")
    void onDropDownKeyPress(KeyPressEvent event) {
        onDropDownKeyEvent(event);
    }

    private void onDropDownKeyEvent(DomEvent event) {
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
