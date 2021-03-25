/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.client.toolbox;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.project.client.service.ClientProjectOpenReusableSubprocessService;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.resources.StunnerCommonIconsGlyphFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.isEmpty;
import static org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNClientConstants.OpenSubprocessToolBoxAction;
import static org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNClientConstants.SubprocessIdNotSpecified;
import static org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNClientConstants.SubprocessNotFound;

@Dependent
public class OpenSubprocessToolboxAction implements ToolboxAction<AbstractCanvasHandler> {

    private final ClientTranslationService translationService;
    private final ClientProjectOpenReusableSubprocessService openSubprocessService;

    @Inject
    public OpenSubprocessToolboxAction(final ClientTranslationService translationService,
                                       final ClientProjectOpenReusableSubprocessService openSubprocessService) {
        this.translationService = translationService;
        this.openSubprocessService = openSubprocessService;
    }

    @Override
    public Glyph getGlyph(final AbstractCanvasHandler canvasHandler,
                          final String uuid) {
        return StunnerCommonIconsGlyphFactory.SUBPROCESS;
    }

    @Override
    public String getTitle(final AbstractCanvasHandler canvasHandler,
                           final String uuid) {
        return translationService.getValue(OpenSubprocessToolBoxAction);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ToolboxAction<AbstractCanvasHandler> onMouseClick(final AbstractCanvasHandler canvasHandler,
                                                             final String uuid,
                                                             final MouseClickEvent event) {
        String processId = getProcessId(canvasHandler, uuid);
        if (isEmpty(processId)) {
            showNotification(translationService.getValue(SubprocessIdNotSpecified));
            return this;
        }

        openSubprocessService
                .call(processId)
                .then(serverData -> {
                    openSubprocess(serverData, processId);
                    return null;
                })
                .catch_(exception -> {
                    showNotification(translationService.getValue(SubprocessNotFound, processId));
                    return null;
                });

        return this;
    }

    @SuppressWarnings("unchecked")
    public String getProcessId(final AbstractCanvasHandler canvasHandler,
                        final String uuid) {
        Node<View<ReusableSubprocess>, ?> node = canvasHandler.getDiagram().getGraph().getNode(uuid);
        ReusableSubprocess subprocess = node.getContent().getDefinition();
        return subprocess.getExecutionSet().getCalledElement().getValue();
    }

    public void openSubprocess(final List<String> serverData,
                        final String processId) {
        if (serverData.size() == 2) {
            openSubprocessService.openReusableSubprocess(serverData);
        } else {
            showNotification(translationService.getValue(SubprocessNotFound, processId));
        }
    }

    public void showNotification(final String message) {
        Notify.notify("",
                      buildHtmlEscapedText(message),
                      IconType.EXCLAMATION);
    }

    private static String buildHtmlEscapedText(final String message) {
        return new SafeHtmlBuilder().appendEscapedLines(message).toSafeHtml().asString();
    }
}
