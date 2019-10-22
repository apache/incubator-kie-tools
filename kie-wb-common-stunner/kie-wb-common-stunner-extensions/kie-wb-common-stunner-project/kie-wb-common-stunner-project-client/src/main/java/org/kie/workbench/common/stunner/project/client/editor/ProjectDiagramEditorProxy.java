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
package org.kie.workbench.common.stunner.project.client.editor;

import java.util.Optional;
import java.util.function.Consumer;

import org.kie.workbench.common.stunner.kogito.api.editor.KogitoDiagramResource;
import org.kie.workbench.common.stunner.kogito.client.editor.DiagramEditorProxy;
import org.uberfire.mvp.Command;

public class ProjectDiagramEditorProxy<RESOURCE extends KogitoDiagramResource> extends DiagramEditorProxy<RESOURCE> {

    private Optional<Consumer<Command>> saveAfterValidationConsumer;
    private Optional<Consumer<String>> saveAfterUserConfirmationConsumer;
    private Optional<Consumer<String>> showNoChangesSinceLastSaveMessageConsumer;

    public ProjectDiagramEditorProxy() {
        super();
        saveAfterValidationConsumer = Optional.empty();
        saveAfterUserConfirmationConsumer = Optional.empty();
        showNoChangesSinceLastSaveMessageConsumer = Optional.empty();
    }

    public void setSaveAfterValidationConsumer(final Consumer<Command> saveAfterValidationConsumer) {
        this.saveAfterValidationConsumer = Optional.ofNullable(saveAfterValidationConsumer);
    }

    public void setSaveAfterUserConfirmationConsumer(final Consumer<String> saveAfterUserConfirmationConsumer) {
        this.saveAfterUserConfirmationConsumer = Optional.ofNullable(saveAfterUserConfirmationConsumer);
    }

    public void setShowNoChangesSinceLastSaveMessageConsumer(final Consumer<String> showNoChangesSinceLastSaveMessageConsumer) {
        this.showNoChangesSinceLastSaveMessageConsumer = Optional.ofNullable(showNoChangesSinceLastSaveMessageConsumer);
    }

    public void saveAfterValidation(final Command command) {
        saveAfterValidationConsumer.orElse((c) -> {/*NOP*/}).accept(command);
    }

    public void saveAfterUserConfirmation(final String commitMessage) {
        saveAfterUserConfirmationConsumer.orElse((s) -> {/*NOP*/}).accept(commitMessage);
    }

    public void showNoChangesSinceLastSaveMessage(final String message) {
        showNoChangesSinceLastSaveMessageConsumer.orElse((s) -> {/*NOP*/}).accept(message);
    }
}
