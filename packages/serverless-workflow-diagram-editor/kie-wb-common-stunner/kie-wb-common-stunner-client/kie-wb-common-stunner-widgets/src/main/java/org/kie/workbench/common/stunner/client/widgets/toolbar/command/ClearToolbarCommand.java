/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.toolbar.command;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
public class ClearToolbarCommand extends AbstractToolbarCommand<EditorSession, ClearSessionCommand> {

    @Inject
    public ClearToolbarCommand(final DefinitionUtils definitionUtils,
                               final @Any ManagedInstance<ClearSessionCommand> clearSessionCommand,
                               final ClientTranslationService translationService) {
        super(definitionUtils,
              clearSessionCommand,
              translationService);
    }

    @Override
    public IconType getIcon() {
        return IconType.ERASER;
    }

    @Override
    public String getCaption() {
        return translationService.getValue(CoreTranslationMessages.CLEAR_DIAGRAM);
    }

    @Override
    public String getTooltip() {
        return translationService.getValue(CoreTranslationMessages.CLEAR_DIAGRAM);
    }

    @Override
    protected boolean requiresConfirm() {
        return true;
    }

    /**
     * Added alert message - the operation cannot be reverted.
     * See <a>org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand</a>
     */
    @Override
    protected String getConfirmMessage() {
        return super.getConfirmMessage() + " This operation cannot be reverted.";
    }
}
