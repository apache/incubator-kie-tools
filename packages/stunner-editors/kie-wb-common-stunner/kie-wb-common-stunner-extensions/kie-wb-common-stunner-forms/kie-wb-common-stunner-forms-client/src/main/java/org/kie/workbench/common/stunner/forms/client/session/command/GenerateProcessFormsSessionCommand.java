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

package org.kie.workbench.common.stunner.forms.client.session.command;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.forms.client.gen.ClientFormGenerationManager;
import org.kie.workbench.common.stunner.forms.service.FormGenerationService;

@Dependent
@Default
public class GenerateProcessFormsSessionCommand extends AbstractClientSessionCommand<EditorSession> {

    private final ClientFormGenerationManager formGenerationManager;

    protected GenerateProcessFormsSessionCommand() {
        this(null);
    }

    @Inject
    public GenerateProcessFormsSessionCommand(final ClientFormGenerationManager formGenerationManager) {
        super(true);
        this.formGenerationManager = formGenerationManager;
    }

    @Override
    public <V> void execute(Callback<V> callback) {
        formGenerationManager.call(this::call);
        callback.onSuccess();
    }

    @Override
    public boolean accepts(final ClientSession session) {
        return session instanceof EditorSession;
    }

    private void call(final FormGenerationService service) {
        service.generateProcessForm(getCanvasHandler().getDiagram());
    }
}
