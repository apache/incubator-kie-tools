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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.mvp.ParameterizedCommand;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class DeletePopUpPresenter {

    private ParameterizedCommand<String> command;
    private View view;
    private ToggleCommentPresenter toggleCommentPresenter;

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
        this.command = command;
        view.show();
    }

    public void cancel() {
        view.hide();
    }

    public ParameterizedCommand<String> getCommand() {
        return command;
    }

    public void delete() {
        checkNotNull("command",
                     command);
        command.execute(toggleCommentPresenter.getComment());
        view.hide();
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
    }
}
