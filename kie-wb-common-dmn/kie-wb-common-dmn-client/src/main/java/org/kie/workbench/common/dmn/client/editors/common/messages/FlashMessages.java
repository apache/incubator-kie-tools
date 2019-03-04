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

package org.kie.workbench.common.dmn.client.editors.common.messages;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.Command;

@Dependent
public class FlashMessages {

    private final View view;

    private Command warningSuccessCallback;

    private Command warningErrorCallback;

    @Inject
    public FlashMessages(final View view) {
        this.view = view;
    }

    @PostConstruct
    void init() {
        view.init(this);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void onFlashMessageEvent(final @Observes FlashMessage flashMessage) {
        final boolean isElementPresent = view.isElementPresent(flashMessage.getElementSelector());
        if (isElementPresent) {
            registerFlashMessageCallback(flashMessage);
            showFlashMessage(flashMessage);
            highlighElement(flashMessage);
        }
    }

    void showFlashMessage(final FlashMessage flashMessage) {
        switch (flashMessage.getType()) {
            case ERROR:
                view.showErrorMessage(flashMessage.getStrongMessage(), flashMessage.getRegularMessage());
                break;
            case WARNING:
                view.showWarningMessage(flashMessage.getStrongMessage(), flashMessage.getRegularMessage());
                break;
        }
    }

    void highlighElement(final FlashMessage flashMessage) {
        switch (flashMessage.getType()) {
            case ERROR:
                view.showErrorHighlight(flashMessage.getElementSelector());
                break;
            case WARNING:
                view.showWarningHighlight(flashMessage.getElementSelector());
                break;
        }
    }

    void registerFlashMessageCallback(final FlashMessage flashMessage) {
        switch (flashMessage.getType()) {
            case ERROR:
                // 'Error' FlashMessage does not have callbacks.
                break;
            case WARNING:
                warningSuccessCallback = flashMessage.getOnSuccess();
                warningErrorCallback = flashMessage.getOnError();
                break;
        }
    }

    void executeSuccessWarningCallback() {
        getWarningSuccessCallback().ifPresent(Command::execute);
    }

    void executeErrorWarningCallback() {
        getWarningErrorCallback().ifPresent(Command::execute);
    }

    private Optional<Command> getWarningErrorCallback() {
        return Optional.ofNullable(warningErrorCallback);
    }

    private Optional<Command> getWarningSuccessCallback() {
        return Optional.ofNullable(warningSuccessCallback);
    }

    public void hideMessages() {
        view.hideErrorContainer();
        view.hideWarningContainer();
    }

    public interface View extends UberElemental<FlashMessages>,
                                  IsElement {

        void showErrorMessage(final String strongMessage, final String regularMessage);

        void showWarningMessage(final String strongMessage, final String regularMessage);

        void showErrorHighlight(final String errorElementSelector);

        void showWarningHighlight(final String warningElementSelector);

        boolean isElementPresent(final String elementSelector);

        void hideWarningContainer();

        void hideErrorContainer();
    }
}
