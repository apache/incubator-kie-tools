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

package org.kie.workbench.common.dmn.client.editors.common.messages;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.DomGlobal.SetTimeoutCallbackFn;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.NodeList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class FlashMessagesView implements FlashMessages.View {

    static final String ERROR_CSS_CLASS = "kie-flash-message-error-element";

    static final String WARNING_CSS_CLASS = "kie-flash-message-warning-element";

    static final String OPENED_CONTAINER_CSS_CLASS = "opened";

    @DataField("error-container")
    private final HTMLDivElement errorContainer;

    @DataField("warning-container")
    private final HTMLDivElement warningContainer;

    @DataField("success-container")
    private final HTMLDivElement successContainer;

    @DataField("strong-error-message")
    private final HTMLElement strongErrorMessage;

    @DataField("regular-error-message")
    private final HTMLElement regularErrorMessage;

    @DataField("strong-warning-message")
    private final HTMLElement strongWarningMessage;

    @DataField("regular-warning-message")
    private final HTMLElement regularWarningMessage;

    @DataField("strong-success-message")
    private final HTMLElement strongSuccessMessage;

    @DataField("regular-success-message")
    private final HTMLElement regularSuccessMessage;

    @DataField("ok-warning-button")
    private final HTMLButtonElement okWarningButton;

    @DataField("cancel-warning-button")
    private final HTMLButtonElement cancelWarningButton;

    @DataField("close-success-message-button")
    private final HTMLButtonElement closeSuccessButton;

    private FlashMessages presenter;

    @Inject
    public FlashMessagesView(final HTMLDivElement errorContainer,
                             final HTMLDivElement warningContainer,
                             final @Named("strong") HTMLElement strongErrorMessage,
                             final @Named("span") HTMLElement regularErrorMessage,
                             final @Named("strong") HTMLElement strongWarningMessage,
                             final @Named("span") HTMLElement regularWarningMessage,
                             final HTMLButtonElement okWarningButton,
                             final HTMLButtonElement cancelWarningButton,
                             final HTMLButtonElement closeSuccessButton,
                             final HTMLDivElement successContainer,
                             final @Named("strong") HTMLElement strongSuccessMessage,
                             final @Named("span") HTMLElement regularSuccessMessage) {
        this.errorContainer = errorContainer;
        this.warningContainer = warningContainer;
        this.strongErrorMessage = strongErrorMessage;
        this.regularErrorMessage = regularErrorMessage;
        this.strongWarningMessage = strongWarningMessage;
        this.regularWarningMessage = regularWarningMessage;
        this.okWarningButton = okWarningButton;
        this.cancelWarningButton = cancelWarningButton;
        this.closeSuccessButton = closeSuccessButton;
        this.successContainer = successContainer;
        this.strongSuccessMessage = strongSuccessMessage;
        this.regularSuccessMessage = regularSuccessMessage;
    }

    @Override
    public void init(final FlashMessages presenter) {
        this.presenter = presenter;
    }

    @EventHandler("close-success-message-button")
    public void onCloseSuccessButtonClick(final ClickEvent e) {
        hideSuccessContainer();
    }

    @EventHandler("ok-warning-button")
    public void onOkWarningButtonClick(final ClickEvent e) {
        presenter.executeSuccessWarningCallback();
        hideWarningContainer();
        disableWarningHighlight();
    }

    @EventHandler("cancel-warning-button")
    public void onCancelWarningButtonClick(final ClickEvent e) {
        presenter.executeErrorWarningCallback();
        hideWarningContainer();
        disableWarningHighlight();
    }

    @Override
    public void showErrorMessage(final String strongMessage,
                                 final String regularMessage) {
        show(errorContainer);
        strongErrorMessage.textContent = strongMessage;
        regularErrorMessage.textContent = regularMessage;
    }

    @Override
    public void showWarningMessage(final String strongMessage,
                                   final String regularMessage) {
        show(warningContainer);
        strongWarningMessage.textContent = strongMessage;
        regularWarningMessage.textContent = regularMessage;
    }

    @Override
    public void showSuccessMessage(final String strongMessage,
                                   final String regularMessage) {
        show(successContainer);
        strongSuccessMessage.textContent = strongMessage;
        regularSuccessMessage.textContent = regularMessage;
    }

    @Override
    public void showErrorHighlight(final String errorElementSelector) {
        querySelector(errorElementSelector).ifPresent(element -> {

            enableErrorHighlight(element);
            setupDisableErrorHighlightCallbacks(element);

            element.focus();
        });
    }

    @Override
    public void showWarningHighlight(final String warningElementSelector) {
        disableWarningHighlight();
        querySelector(warningElementSelector).ifPresent(this::enableWarningHighlight);
    }

    @Override
    public boolean isElementPresent(final String elementSelector) {
        return querySelector(elementSelector).isPresent();
    }

    private Optional<Element> querySelector(final String selector) {
        return Optional.ofNullable(getElement().parentNode).map(node -> node.querySelector(selector));
    }

    @Override
    public void hideWarningContainer() {
        hide(warningContainer);
    }

    @Override
    public void hideSuccessContainer() {
        hide(successContainer);
    }

    @Override
    public void hideErrorContainer() {
        hide(errorContainer);
    }

    void setupDisableErrorHighlightCallbacks(final Element element) {

        element.onkeypress = (e) -> {
            disableErrorHighlight(element);
            return true;
        };

        element.onblur = (e) -> {
            disableErrorHighlight(element);
            return true;
        };
    }

    void enableErrorHighlight(final Element element) {
        element.classList.add(ERROR_CSS_CLASS);
    }

    void enableWarningHighlight(final Element element) {
        element.classList.add(WARNING_CSS_CLASS);
    }

    void disableWarningHighlight() {

        final NodeList<Element> warningElements = getElement().parentNode.querySelectorAll("." + WARNING_CSS_CLASS);

        for (int i = 0; i < warningElements.length; i++) {
            warningElements.getAt(i).classList.remove(WARNING_CSS_CLASS);
        }
    }

    void disableErrorHighlight(final Element element) {
        element.classList.remove(ERROR_CSS_CLASS);

        setTimeout((type) -> {
            final boolean isErrorEnabled = element.classList.contains(ERROR_CSS_CLASS);

            if (!isErrorEnabled) {
                hideErrorContainer();
                teardownDisableErrorHighlightCallbacks(element);
            }
        }, 500);
    }

    void teardownDisableErrorHighlightCallbacks(final Element element) {
        element.onkeypress = (e) -> true;
        element.onblur = (e) -> true;
    }

    void setTimeout(final SetTimeoutCallbackFn callback,
                    final double delay) {
        DomGlobal.setTimeout(callback, delay);
    }

    private void hide(final Element element) {
        element.classList.remove(OPENED_CONTAINER_CSS_CLASS);
    }

    private void show(final Element element) {
        element.classList.add(OPENED_CONTAINER_CSS_CLASS);
    }
}
