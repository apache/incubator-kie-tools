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

import java.util.Collection;
import java.util.logging.Level;

import javax.enterprise.event.Event;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.kie.workbench.common.stunner.core.validation.impl.ValidationUtils;
import org.kie.workbench.common.stunner.kogito.api.editor.KogitoDiagramResource;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorCore;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorMenuSessionItems;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class AbstractProjectDiagramEditorCore<M extends Metadata, D extends Diagram<Graph, M>,
        C extends KogitoDiagramResource<D>, P extends ProjectDiagramEditorProxy<C>> extends AbstractDiagramEditorCore<M,
        D, C, P> {

    public AbstractProjectDiagramEditorCore(final View baseEditorView,
                                            final TextEditorView xmlEditorView,
                                            final Event<NotificationEvent> notificationEvent,
                                            final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                            final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                            final AbstractDiagramEditorMenuSessionItems<?> menuSessionItems,
                                            final ErrorPopupPresenter errorPopupPresenter,
                                            final DiagramClientErrorHandler diagramClientErrorHandler,
                                            final ClientTranslationService translationService) {
        super(baseEditorView,
              xmlEditorView,
              notificationEvent,
              editorSessionPresenterInstances,
              viewerSessionPresenterInstances,
              menuSessionItems,
              errorPopupPresenter,
              diagramClientErrorHandler,
              translationService);
    }

    protected abstract void saveOrUpdate(final String commitMessage);

    protected abstract void saveAsXML(final String commitMessage);

    @Override
    @SuppressWarnings("unchecked")
    public P makeStunnerEditorProxy() {
        final P proxy = super.makeStunnerEditorProxy();
        proxy.setSaveAfterValidationConsumer((continueSaveOnceValid) -> {
            getMenuSessionItems()
                    .getCommands()
                    .getValidateSessionCommand()
                    .execute(getSaveAfterValidationCallback(continueSaveOnceValid));
        });
        proxy.setSaveAfterUserConfirmationConsumer(AbstractProjectDiagramEditorCore.this::saveOrUpdate);
        proxy.setShowNoChangesSinceLastSaveMessageConsumer((message) -> getSessionPresenter().getView().showMessage(message));

        return proxy;
    }

    protected ClientSessionCommand.Callback<Collection<DiagramElementViolation<RuleViolation>>> getSaveAfterValidationCallback(final Command continueSaveOnceValid) {
        return new ClientSessionCommand.Callback<Collection<DiagramElementViolation<RuleViolation>>>() {
            @Override
            public void onSuccess() {
                continueSaveOnceValid.execute();
            }

            @Override
            public void onError(final Collection<DiagramElementViolation<RuleViolation>> violations) {
                final Violation.Type maxSeverity = ValidationUtils.getMaxSeverity(violations);
                if (maxSeverity.equals(Violation.Type.ERROR)) {
                    onValidationFailed(violations);
                } else {
                    // Allow saving when only warnings founds.
                    continueSaveOnceValid.execute();
                }
            }
        };
    }

    @Override
    public P makeXmlEditorProxy() {
        final P proxy = super.makeXmlEditorProxy();
        proxy.setSaveAfterValidationConsumer(Command::execute);
        proxy.setSaveAfterUserConfirmationConsumer(AbstractProjectDiagramEditorCore.this::saveAsXML);
        proxy.setShowNoChangesSinceLastSaveMessageConsumer((message) -> getNotificationEvent().fire(new NotificationEvent(message)));
        return proxy;
    }

    public void doShowNoChangesSinceLastSaveMessage(final String message) {
        getEditorProxy().showNoChangesSinceLastSaveMessage(message);
    }

    void onValidationFailed(final Collection<DiagramElementViolation<RuleViolation>> violations) {
        log(Level.WARNING, "Validation FAILED [violations=" + violations.toString() + "]");
        getBaseEditorView().hideBusyIndicator();
    }
}
