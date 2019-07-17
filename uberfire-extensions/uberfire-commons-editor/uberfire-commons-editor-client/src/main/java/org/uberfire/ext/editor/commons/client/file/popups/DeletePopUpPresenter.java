/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client.file.popups;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.validation.ValidationErrorReason;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.mvp.ParameterizedCommand;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class DeletePopUpPresenter {

    private Validator validator;
    private ParameterizedCommand<String> command;
    private View view;
    private ToggleCommentPresenter toggleCommentPresenter;
    private boolean opened = false;

    @Inject
    public DeletePopUpPresenter(View view,
                                ToggleCommentPresenter toggleCommentPresenter) {
        this.view = view;
        this.toggleCommentPresenter = toggleCommentPresenter;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void show(final ParameterizedCommand<String> command) {
        show(null,
             command);
    }

    public void show(final Validator validator,
                     final ParameterizedCommand<String> command) {
        this.validator = validator == null ? defaultValidator() : validator;
        this.command = command;
        this.opened = true;
        view.show();
    }

    public void cancel() {
        this.opened = false;
        view.hide();
    }

    Validator getValidator() {
        return validator;
    }

    public ParameterizedCommand<String> getCommand() {
        return command;
    }

    public boolean isOpened() {
        return opened;
    }

    public void delete() {
        checkNotNull("command",
                     command);

        validator.validate(null,
                           validatorCallback(toggleCommentPresenter.getComment()));
    }

    private ValidatorWithReasonCallback validatorCallback(final String comment) {
        return new ValidatorWithReasonCallback() {
            @Override
            public void onFailure(final String reason) {
                if (ValidationErrorReason.NOT_ALLOWED.name().equals(reason)) {
                    view.handleDeleteNotAllowed();
                } else {
                    view.handleUnexpectedError();
                }
            }

            @Override
            public void onSuccess() {
                command.execute(comment);
                view.hide();
                opened = false;
            }

            @Override
            public void onFailure() {
                view.handleUnexpectedError();
            }
        };
    }

    private Validator defaultValidator() {
        return (value, callback) -> callback.onSuccess();
    }

    public void setPrompt(final String prompt) {
        view.setPrompt(prompt);
    }

    public void setCommentIsHidden(final boolean hidden) {
        toggleCommentPresenter.setHidden(hidden);
    }

    public ToggleCommentPresenter getToggleCommentPresenter() {
        return toggleCommentPresenter;
    }

    public interface View extends UberElement<DeletePopUpPresenter> {

        void show();

        void hide();

        void setPrompt(final String prompt);

        void handleDeleteNotAllowed();

        void handleUnexpectedError();
    }
}
