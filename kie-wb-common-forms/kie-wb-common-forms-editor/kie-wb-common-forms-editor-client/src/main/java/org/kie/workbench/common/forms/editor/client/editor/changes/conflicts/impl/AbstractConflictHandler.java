/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.editor.changes.conflicts.impl;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.PreDestroy;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.editor.client.editor.changes.conflicts.ConflictsHandler;
import org.kie.workbench.common.forms.editor.client.editor.changes.conflicts.element.ConflictElement;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.model.FieldDefinition;

public abstract class AbstractConflictHandler implements ConflictsHandler {

    protected ManagedInstance<ConflictElement> conflictElementManagedInstance;
    protected TranslationService translationService;

    protected FormModelerContent content;
    protected Consumer<ConflictElement> conflictElementConsumer;

    public AbstractConflictHandler(ManagedInstance<ConflictElement> conflictElementManagedInstance,
                                   TranslationService translationService) {
        this.conflictElementManagedInstance = conflictElementManagedInstance;
        this.translationService = translationService;
    }

    void init(FormModelerContent content,
              Consumer<ConflictElement> conflictElementConsumer) {
        this.content = content;
        this.conflictElementConsumer = conflictElementConsumer;
    }

    protected void consumeConflict(String target,
                                   String firstMessagePart,
                                   String secondMessagePart) {
        ConflictElement conflictElement = conflictElementManagedInstance.get();

        conflictElement.showConflict(target,
                                     firstMessagePart,
                                     secondMessagePart);
        conflictElementConsumer.accept(conflictElement);
    }

    protected String getFieldText(FieldDefinition fieldDefinition) {
        return Optional.ofNullable(fieldDefinition.getLabel()).orElse(fieldDefinition.getName());
    }

    @PreDestroy
    public void destroy() {
        conflictElementManagedInstance.destroyAll();
    }
}
