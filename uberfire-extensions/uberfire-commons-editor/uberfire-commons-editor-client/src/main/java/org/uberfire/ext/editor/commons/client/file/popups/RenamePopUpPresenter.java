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
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.validation.ValidationErrorReason;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class RenamePopUpPresenter {

    private static final boolean DEFAULT_DIRTY_STATUS = false;

    private static final String DEFAULT_FILE_NAME = "";

    private static final CommandWithFileNameAndCommitMessage DEFAULT_COMMAND = (c) -> {
    };

    private final View view;

    private final ToggleCommentPresenter toggleCommentPresenter;

    private Path path;

    private Validator validator;

    private CommandWithFileNameAndCommitMessage renameCommand;

    private CommandWithFileNameAndCommitMessage saveAndRenameCommand;

    private String originalFileName;

    private boolean isDirty;

    @Inject
    public RenamePopUpPresenter(final View view,
                                final ToggleCommentPresenter toggleCommentPresenter) {
        this.view = view;
        this.toggleCommentPresenter = toggleCommentPresenter;
    }

    public void show(final Path path,
                     final Validator validator,
                     final CommandWithFileNameAndCommitMessage renameCommand,
                     final CommandWithFileNameAndCommitMessage saveAndRenameCommand,
                     final boolean isDirty,
                     final String originalFileName) {

        this.validator = checkNotNull("validator", validator);
        this.path = checkNotNull("path", path);
        this.renameCommand = checkNotNull("renameCommand", renameCommand);
        this.saveAndRenameCommand = checkNotNull("saveAndRenameCommand", saveAndRenameCommand);
        this.originalFileName = checkNotNull("originalFileName", originalFileName);
        this.isDirty = checkNotNull("isDirty", isDirty);

        setupView();
        showView();
    }

    void setupView() {
        enablePrimaryButton();
        hideSaveAndRenameIfAssetIsNotDirty();
    }

    void showView() {
        view.setOriginalFileName(getOriginalFileName());
        view.show();
    }

    public void show(final Path path,
                     final CommandWithFileNameAndCommitMessage renameCommand,
                     final String originalFileName) {

        final Validator validator = defaultValidator();

        show(path, validator, renameCommand, DEFAULT_COMMAND, DEFAULT_DIRTY_STATUS, originalFileName);
    }

    public void show(final Path path,
                     final Validator validator,
                     final CommandWithFileNameAndCommitMessage renameCommand) {

        show(path, validator, renameCommand, DEFAULT_COMMAND, DEFAULT_DIRTY_STATUS, DEFAULT_FILE_NAME);
    }

    public void show(final Path path,
                     final Validator validator,
                     final boolean isDirty,
                     final CommandWithFileNameAndCommitMessage renameCommand,
                     final CommandWithFileNameAndCommitMessage saveAndRenameCommand) {

        show(path, validator, renameCommand, saveAndRenameCommand, isDirty, DEFAULT_FILE_NAME);
    }

    public void show(final Path path,
                     final CommandWithFileNameAndCommitMessage renameCommand) {

        final Validator validator = defaultValidator();

        show(path, validator, renameCommand, DEFAULT_COMMAND, DEFAULT_DIRTY_STATUS, DEFAULT_FILE_NAME);
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void rename(final String newName) {

        final String fileName = newFileName(newName);
        final String comment = toggleCommentPresenter.getComment();
        final ValidatorWithReasonCallback callback = validatorCallback(comment, newName, renameCommand);

        validator.validate(fileName, callback);
    }

    public void saveAndRename(final String newName) {

        final String fileName = newFileName(newName);
        final String comment = toggleCommentPresenter.getComment();
        final ValidatorWithReasonCallback callback = validatorCallback(comment, newName, saveAndRenameCommand);

        validator.validate(fileName, callback);
    }

    private String newFileName(final String newName) {

        final String extension = extension(path.getFileName());

        return newName + extension;
    }

    private String extension(final String fileName) {
        return fileName.lastIndexOf(".") > 0 ? fileName.substring(fileName.lastIndexOf(".")) : "";
    }

    private ValidatorWithReasonCallback validatorCallback(final String comment,
                                                          final String baseFileName,
                                                          final CommandWithFileNameAndCommitMessage onSuccess) {

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

            public void onSuccess() {
                onSuccess.execute(new FileNameAndCommitMessage(baseFileName, comment));
            }

            public void onFailure() {
                view.handleInvalidFileName();
            }
        };
    }

    boolean isDirty() {
        return isDirty;
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
        return (value, callback) -> callback.onSuccess();
    }

    Path getPath() {
        return path;
    }

    Validator getValidator() {
        return validator;
    }

    CommandWithFileNameAndCommitMessage getRenameCommand() {
        return renameCommand;
    }

    String getOriginalFileName() {
        return originalFileName;
    }

    void hideSaveAndRenameIfAssetIsNotDirty() {
        view.hideSaveAndRename(!isDirty());
    }

    void enablePrimaryButton() {
        if (isDirty()) {
            view.saveAndRenameAsPrimary();
        } else {
            view.renameAsPrimary();
        }
    }

    public interface View extends UberElemental<RenamePopUpPresenter> {

        void show();

        void hide();

        void handleDuplicatedFileName();

        void handleInvalidFileName();

        void setOriginalFileName(final String fileName);

        void handleRenameNotAllowed();

        void renameAsPrimary();

        void saveAndRenameAsPrimary();

        void hideSaveAndRename(final boolean hidden);
    }
}
