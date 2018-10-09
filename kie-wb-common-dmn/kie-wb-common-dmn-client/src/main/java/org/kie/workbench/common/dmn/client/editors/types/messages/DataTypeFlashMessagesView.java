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

package org.kie.workbench.common.dmn.client.editors.types.messages;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.DomGlobal;
import elemental2.dom.DomGlobal.SetTimeoutCallbackFn;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@ApplicationScoped
public class DataTypeFlashMessagesView implements DataTypeFlashMessages.View {

    static final String ERROR_CSS_CLASS = "kie-data-types-error-element";

    static final String OPENED_CONTAINER_CSS_CLASS = "opened";

    @DataField("error-container")
    private final HTMLDivElement errorContainer;

    @DataField("warning-container")
    private final HTMLDivElement warningContainer;

    @DataField("strong-error-message")
    private final HTMLElement strongErrorMessage;

    @DataField("regular-error-message")
    private final HTMLElement regularErrorMessage;

    @DataField("strong-warning-message")
    private final HTMLElement strongWarningMessage;

    @DataField("regular-warning-message")
    private final HTMLElement regularWarningMessage;

    private DataTypeFlashMessages presenter;

    @Inject
    public DataTypeFlashMessagesView(final HTMLDivElement errorContainer,
                                     final HTMLDivElement warningContainer,
                                     final @Named("strong") HTMLElement strongErrorMessage,
                                     final @Named("span") HTMLElement regularErrorMessage,
                                     final @Named("strong") HTMLElement strongWarningMessage,
                                     final @Named("span") HTMLElement regularWarningMessage) {
        this.errorContainer = errorContainer;
        this.warningContainer = warningContainer;
        this.strongErrorMessage = strongErrorMessage;
        this.regularErrorMessage = regularErrorMessage;
        this.strongWarningMessage = strongWarningMessage;
        this.regularWarningMessage = regularWarningMessage;
    }

    @Override
    public void init(final DataTypeFlashMessages presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showErrorMessage(final String strongMessage,
                                 final String regularMessage) {
        show(errorContainer);
        strongErrorMessage.textContent = strongMessage;
        regularErrorMessage.textContent = regularMessage;
    }

    @Override
    public void showErrorHighlight(final String errorElementSelector) {

        final Element element = getElement().parentNode.querySelector(errorElementSelector);

        enableErrorHighlight(element);
        setupDisableErrorHighlightCallbacks(element);

        element.focus();
    }

    @Override
    public void showWarningMessage(final String strongMessage, final String regularMessage) {
        // TODO: https://issues.jboss.org/browse/DROOLS-3023
    }

    @Override
    public void showWarningHighlight(final String errorElementSelector) {
        // TODO: https://issues.jboss.org/browse/DROOLS-3023
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

    void disableErrorHighlight(final Element element) {
        element.classList.remove(ERROR_CSS_CLASS);

        setTimeout((type) -> {
            final boolean isErrorEnabled = element.classList.contains(ERROR_CSS_CLASS);

            if (!isErrorEnabled) {
                hide(errorContainer);
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
