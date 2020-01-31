/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.ParameterizedCommand;

public class SquashChangeRequestPopUpPresenter {

    public interface View extends UberElemental<SquashChangeRequestPopUpPresenter> {
        void show(String messages);
        void hide();
        void showMessageInputError();
        void clearMessageInputError();
    }

    private View view;
    private ParameterizedCommand<String> command;

    @Inject
    public SquashChangeRequestPopUpPresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void setup() {
        view.init(SquashChangeRequestPopUpPresenter.this);
    }

    public void show(final String messages,
                     final ParameterizedCommand<String> command) {
        this.command = command;
        view.clearMessageInputError();
        view.show(messages);
    }

    public void squash(final String message) {
        if (message == null || message.trim().isEmpty()) {
            view.showMessageInputError();
        } else {
            view.hide();
            command.execute(message);
        }
    }

    public void cancel() {
        view.hide();
    }
}