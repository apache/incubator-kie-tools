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
package org.kie.workbench.common.stunner.project.client.editor;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource;
import org.uberfire.mvp.Command;

public class ProjectDiagramEditorProxy {

    static final ProjectDiagramEditorProxy NULL_EDITOR = new ProjectDiagramEditorProxy();

    private Optional<Consumer<Command>> saveAfterValidationConsumer;
    private Optional<Consumer<String>> saveAfterUserConfirmationConsumer;
    private Optional<Consumer<String>> showNoChangesSinceLastSaveMessageConsumer;
    private Optional<Supplier<Integer>> hashCodeSupplier;
    private Supplier<ProjectDiagramResource> contentSupplier = () -> null;

    public ProjectDiagramEditorProxy() {
        saveAfterValidationConsumer = Optional.empty();
        saveAfterUserConfirmationConsumer = Optional.empty();
        showNoChangesSinceLastSaveMessageConsumer = Optional.empty();
        hashCodeSupplier = Optional.empty();
    }

    void setSaveAfterValidationConsumer(final Consumer<Command> saveAfterValidationConsumer) {
        this.saveAfterValidationConsumer = Optional.ofNullable(saveAfterValidationConsumer);
    }

    void setSaveAfterUserConfirmationConsumer(final Consumer<String> saveAfterUserConfirmationConsumer) {
        this.saveAfterUserConfirmationConsumer = Optional.ofNullable(saveAfterUserConfirmationConsumer);
    }

    void setShowNoChangesSinceLastSaveMessageConsumer(final Consumer<String> showNoChangesSinceLastSaveMessageConsumer) {
        this.showNoChangesSinceLastSaveMessageConsumer = Optional.ofNullable(showNoChangesSinceLastSaveMessageConsumer);
    }

    void setHashCodeSupplier(final Supplier<Integer> hashCodeSupplier) {
        this.hashCodeSupplier = Optional.ofNullable(hashCodeSupplier);
    }

    void saveAfterValidation(final Command command) {
        saveAfterValidationConsumer.orElse((c) -> {/*NOP*/}).accept(command);
    }

    void saveAfterUserConfirmation(final String commitMessage) {
        saveAfterUserConfirmationConsumer.orElse((s) -> {/*NOP*/}).accept(commitMessage);
    }

    void showNoChangesSinceLastSaveMessage(final String message) {
        showNoChangesSinceLastSaveMessageConsumer.orElse((s) -> {/*NOP*/}).accept(message);
    }

    int getEditorHashCode() {
        return hashCodeSupplier.orElse(() -> 0).get();
    }

    public Supplier<ProjectDiagramResource> getContentSupplier() {
        return contentSupplier;
    }

    public void setContentSupplier(final Supplier<ProjectDiagramResource> contentSupplier) {
        this.contentSupplier = contentSupplier;
    }
}
