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

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.validation.ValidationErrorReason;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class RenamePopUpPresenter {

    private String originalFileName;
    private Path path;
    private Validator validator;
    private CommandWithFileNameAndCommitMessage command;
    private ToggleCommentPresenter toggleCommentPresenter;
    private View view;

    @Inject
    public RenamePopUpPresenter(View view,
                                ToggleCommentPresenter toggleCommentPresenter) {
        this.view = view;
        this.toggleCommentPresenter = toggleCommentPresenter;
    }

    public void show(final Path path,
                     final Validator validator,
                     final CommandWithFileNameAndCommitMessage command,
                     final String originalFileName) {
        this.validator = checkNotNull("validator",
                                      validator);
        this.path = checkNotNull("path",
                                 path);
        this.command = checkNotNull("command",
                                    command);
        this.originalFileName = originalFileName;

        view.setOriginalFileName(originalFileName);
        view.show();
    }

    public void show(final Path path,
                     final CommandWithFileNameAndCommitMessage copyPopupCommand,
                     final String originalFileName) {
        show(path,
             defaultValidator(),
             copyPopupCommand,
             originalFileName);
    }

    public void show(final Path path,
                     final Validator validator,
                     final CommandWithFileNameAndCommitMessage command) {
        show(path,
             validator,
             command,
             "");
    }

    public void show(final Path path,
                     final CommandWithFileNameAndCommitMessage renamePopupCommand) {
        show(path,
             defaultValidator(),
             renamePopupCommand,
             "");
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void rename(final String newName) {
        final String extension = extension(path.getFileName());
        final String fileName = newName + extension;

        validator.validate(fileName,
                           validatorCallback(toggleCommentPresenter.getComment(),
                                             newName));
    }

    private String extension(final String fileName) {
        return fileName.lastIndexOf(".") > 0 ? fileName.substring(fileName.lastIndexOf(".")) : "";
    }

    private ValidatorWithReasonCallback validatorCallback(final String comment,
                                                          final String baseFileName) {
        return new ValidatorWithReasonCallback() {
            @Override
            public void onFailure(final String reason) {
                if (ValidationErrorReason.DUPLICATED_NAME.name().equals(reason)) {
                    view.handleDuplicatedFileName();
                } else if (ValidationErrorReason.NOT_ALLOWED.name().equals(reason)) {
                    view.handleRenameNotAllowed();
                } else {
                    view.handleInvalidFileName();
                }
            }

            @Override
            public void onSuccess() {
                command.execute(new FileNameAndCommitMessage(baseFileName,
                                                             comment));
            }

            @Override
            public void onFailure() {
                view.handleInvalidFileName();
            }
        };
    }

    public void cancel() {
        view.hide();
    }

    public View getView() {
        return view;
    }

    public ToggleCommentPresenter getToggleCommentPresenter() {
        return toggleCommentPresenter;
    }

    private Validator defaultValidator() {
        return new Validator() {
            @Override
            public void validate(final String value,
                                 final ValidatorCallback callback) {
                callback.onSuccess();
            }
        };
    }

    Path getPath() {
        return path;
    }

    Validator getValidator() {
        return validator;
    }

    CommandWithFileNameAndCommitMessage getCommand() {
        return command;
    }

    public interface View extends UberElement<RenamePopUpPresenter> {

        void show();

        void hide();

        void handleDuplicatedFileName();

        void handleInvalidFileName();

        void setOriginalFileName(String fileName);

        void handleRenameNotAllowed();
    }
}
