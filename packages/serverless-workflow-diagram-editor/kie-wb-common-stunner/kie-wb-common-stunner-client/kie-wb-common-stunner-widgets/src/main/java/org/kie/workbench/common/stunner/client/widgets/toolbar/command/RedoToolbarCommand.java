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
package org.kie.workbench.common.stunner.client.widgets.toolbar.command;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.constants.IconRotate;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
public class RedoToolbarCommand extends AbstractToolbarCommand<EditorSession, RedoSessionCommand> {

    @Inject
    public RedoToolbarCommand(final DefinitionUtils definitionUtils,
                              final @Any ManagedInstance<RedoSessionCommand> command,
                              final ClientTranslationService translationService) {
        super(definitionUtils,
              command,
              translationService);
    }

    @Override
    public IconType getIcon() {
        return IconType.UNDO;
    }

    @Override
    public IconRotate getIconRotate() {
        return IconRotate.ROTATE_180;
    }

    @Override
    public String getCaption() {
        return translationService.getValue(CoreTranslationMessages.REDO);
    }

    @Override
    public String getTooltip() {
        return translationService.getValue(CoreTranslationMessages.REDO);
    }

    @Override
    protected boolean requiresConfirm() {
        return false;
    }
}
