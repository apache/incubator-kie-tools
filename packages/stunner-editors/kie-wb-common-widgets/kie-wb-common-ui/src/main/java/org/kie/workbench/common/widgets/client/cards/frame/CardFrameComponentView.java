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


package org.kie.workbench.common.widgets.client.cards.frame;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class CardFrameComponentView implements CardFrameComponent.View {

    public static final String CARD_UUID_ATTR = "data-card-uuid";

    @DataField("view")
    private final HTMLDivElement view;

    @DataField("icon")
    private final HTMLElement icon;

    @DataField("title-text")
    private final HTMLHeadingElement titleText;

    @DataField("title-input")
    private final HTMLInputElement titleInput;

    @DataField("input-close-button")
    private final HTMLButtonElement inputCloseButton;

    @DataField("edit-mode")
    private final HTMLDivElement editMode;

    @DataField("ok-button")
    private final HTMLButtonElement okButton;

    @DataField("close-button")
    private final HTMLButtonElement closeButton;

    @DataField("content")
    private final HTMLDivElement content;

    private CardFrameComponent presenter;

    @Inject
    public CardFrameComponentView(final HTMLDivElement view,
                                  final @Named("span") HTMLElement icon,
                                  final @Named("h2") HTMLHeadingElement titleText,
                                  final HTMLInputElement titleInput,
                                  final HTMLButtonElement inputCloseButton,
                                  final HTMLDivElement editMode,
                                  final HTMLButtonElement okButton,
                                  final HTMLButtonElement closeButton,
                                  final HTMLDivElement content) {
        this.view = view;
        this.icon = icon;
        this.titleText = titleText;
        this.titleInput = titleInput;
        this.inputCloseButton = inputCloseButton;
        this.editMode = editMode;
        this.okButton = okButton;
        this.closeButton = closeButton;
        this.content = content;
    }

    @Override
    public void init(final CardFrameComponent presenter) {
        this.presenter = presenter;
    }

    @EventHandler("title-text")
    public void onTitleTextClick(final ClickEvent e) {
        presenter.enableEditMode();
    }

    @EventHandler("ok-button")
    public void onOkButtonClick(final ClickEvent e) {
        presenter.changeTitle();
    }

    @EventHandler("close-button")
    public void onCloseButtonClick(final ClickEvent e) {
        presenter.refreshView();
    }

    @EventHandler("input-close-button")
    public void onInputCloseButtonClick(final ClickEvent e) {
        titleInput.value = "";
    }

    @EventHandler("title-input")
    public void onTitleInputKeyDownEvent(final KeyDownEvent event) {
        if (isEscape(event)) {
            event.preventDefault();
            presenter.refreshView();
        }
        if (isEnter(event)) {
            event.preventDefault();
            presenter.changeTitle();
        }
    }

    @Override
    public void setUUID(final String uuid) {
        view.setAttribute(CARD_UUID_ATTR, uuid);
    }

    @Override
    public void setIcon(final String cssClassName) {
        icon.classList.add(cssClassName);
    }

    @Override
    public void setTitle(final String title) {
        titleText.textContent = title;
        titleInput.value = title;
    }

    @Override
    public String getTitle() {
        return titleInput.value;
    }

    @Override
    public void setContent(final HTMLElement content) {
        this.content.appendChild(content);
    }

    @Override
    public void enableReadOnlyMode() {
        titleText.hidden = false;
        editMode.hidden = true;
    }

    @Override
    public void enableEditMode() {
        titleText.hidden = true;
        editMode.hidden = false;
        titleInput.focus();
    }

    @Override
    public void setupToggleTitle(final boolean enabled) {
        final String readOnlyCSSClass = "read-only";
        titleText.classList.toggle(readOnlyCSSClass, !enabled);
    }

    private boolean isEscape(final KeyDownEvent event) {
        return event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE;
    }

    private boolean isEnter(final KeyDownEvent event) {
        return event.getNativeKeyCode() == KeyCodes.KEY_ENTER;
    }
}
