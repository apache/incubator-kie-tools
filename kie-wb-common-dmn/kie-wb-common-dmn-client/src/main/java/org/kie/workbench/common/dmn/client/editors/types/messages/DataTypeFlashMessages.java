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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.uberfire.client.mvp.UberElemental;

@ApplicationScoped
public class DataTypeFlashMessages {

    private final View view;

    @Inject
    public DataTypeFlashMessages(final View view) {
        this.view = view;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void onNameIsBlankErrorMessage(final @Observes DataTypeFlashMessage flashMessage) {
        showFlashMessage(flashMessage);
        highlightDataField(flashMessage);
    }

    void showFlashMessage(final DataTypeFlashMessage flashMessage) {
        switch (flashMessage.getType()) {
            case ERROR:
                view.showErrorMessage(flashMessage.getStrongMessage(), flashMessage.getRegularMessage());
                break;
            case WARNING:
                view.showWarningMessage(flashMessage.getStrongMessage(), flashMessage.getRegularMessage());
                break;
        }
    }

    void highlightDataField(final DataTypeFlashMessage flashMessage) {
        switch (flashMessage.getType()) {
            case ERROR:
                view.showErrorHighlight(flashMessage.getErrorElementSelector());
                break;
            case WARNING:
                view.showWarningHighlight(flashMessage.getErrorElementSelector());
                break;
        }
    }

    public interface View extends UberElemental<DataTypeFlashMessages>,
                                  IsElement {

        void showErrorMessage(final String strongMessage, final String regularMessage);

        void showWarningMessage(final String strongMessage, final String regularMessage);

        void showErrorHighlight(final String errorElementSelector);

        void showWarningHighlight(final String errorElementSelector);
    }
}
