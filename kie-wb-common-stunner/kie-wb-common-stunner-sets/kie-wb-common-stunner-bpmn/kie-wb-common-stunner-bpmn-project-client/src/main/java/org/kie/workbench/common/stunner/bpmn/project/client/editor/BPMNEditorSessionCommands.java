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

package org.kie.workbench.common.stunner.bpmn.project.client.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.client.forms.util.ContextUtils;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ManagedClientSessionCommands;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateDiagramFormsSessionCommand;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateProcessFormsSessionCommand;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateSelectedFormsSessionCommand;
import org.kie.workbench.common.stunner.project.client.session.EditorSessionCommands;

@Dependent
@Typed(BPMNEditorSessionCommands.class)
public class BPMNEditorSessionCommands extends EditorSessionCommands {

    @Inject
    public BPMNEditorSessionCommands(final ManagedClientSessionCommands commands) {
        super(commands);
    }

    @Override
    public void init() {
        super.init();
        getCommands()
                .register(GenerateProcessFormsSessionCommand.class)
                .register(GenerateDiagramFormsSessionCommand.class)
                .register(GenerateSelectedFormsSessionCommand.class);
    }

    @Override
    public EditorSessionCommands bind(final ClientSession session) {
        super.bind(session);
        getGenerateSelectedFormsSessionCommand()
                .setElementAcceptor(ContextUtils::isFormGenerationSupported);
        return this;
    }

    public GenerateProcessFormsSessionCommand getGenerateProcessFormsSessionCommand() {
        return get(15);
    }

    public GenerateDiagramFormsSessionCommand getGenerateDiagramFormsSessionCommand() {
        return get(16);
    }

    public GenerateSelectedFormsSessionCommand getGenerateSelectedFormsSessionCommand() {
        return get(17);
    }
}
