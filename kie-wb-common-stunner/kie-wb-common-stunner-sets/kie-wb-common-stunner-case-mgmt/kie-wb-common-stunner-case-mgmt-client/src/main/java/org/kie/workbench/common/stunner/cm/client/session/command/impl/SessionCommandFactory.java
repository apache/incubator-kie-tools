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

package org.kie.workbench.common.stunner.cm.client.session.command.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearStatesSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CopySelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.DeleteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToBpmnSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToJpgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPdfSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPngSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToSvgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.UndoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.VisitGraphSessionCommand;

@CaseManagementEditor
@ApplicationScoped
public class SessionCommandFactory extends org.kie.workbench.common.stunner.core.client.session.command.impl.SessionCommandFactory {

    public SessionCommandFactory() {
        //CDI proxy
    }

    @Inject
    public SessionCommandFactory(final ManagedInstance<ClearStatesSessionCommand> clearStatesCommand,
                                 final ManagedInstance<VisitGraphSessionCommand> visitGraphCommand,
                                 final ManagedInstance<SwitchGridSessionCommand> switchGridCommand,
                                 final ManagedInstance<ClearSessionCommand> clearCommand,
                                 final ManagedInstance<DeleteSelectionSessionCommand> deleteSelectionCommand,
                                 final ManagedInstance<UndoSessionCommand> undoCommand,
                                 final ManagedInstance<RedoSessionCommand> redoCommand,
                                 final ManagedInstance<ValidateSessionCommand> validateCommand,
                                 final ManagedInstance<ExportToPngSessionCommand> exportImageSessionCommand,
                                 final ManagedInstance<ExportToJpgSessionCommand> exportImageJPGSessionCommand,
                                 final ManagedInstance<ExportToPdfSessionCommand> exportPDFSessionCommand,
                                 final ManagedInstance<ExportToSvgSessionCommand> exportSVGSessionCommand,
                                 final ManagedInstance<ExportToBpmnSessionCommand> exportBPMNSessionCommand,
                                 final ManagedInstance<CopySelectionSessionCommand> copySelectionSessionCommand,
                                 final @CaseManagementEditor ManagedInstance<PasteSelectionSessionCommand> pasteSelectionSessionCommand,
                                 final ManagedInstance<CutSelectionSessionCommand> cutSelectionSessionCommand) {
        super(clearStatesCommand,
              visitGraphCommand,
              switchGridCommand,
              clearCommand,
              deleteSelectionCommand,
              undoCommand,
              redoCommand,
              validateCommand,
              exportImageSessionCommand,
              exportImageJPGSessionCommand,
              exportPDFSessionCommand,
              exportSVGSessionCommand,
              exportBPMNSessionCommand,
              copySelectionSessionCommand,
              pasteSelectionSessionCommand,
              cutSelectionSessionCommand);
    }
}
