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

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;

@Dependent
public class ExpressionEditorControlImpl extends AbstractCanvasControl<AbstractCanvas> implements ExpressionEditorControl {

    private ExpressionEditorView view;
    private DecisionNavigatorPresenter decisionNavigator;
    private Optional<ExpressionEditorView.Presenter> expressionEditor = Optional.empty();

    @Inject
    public ExpressionEditorControlImpl(final ExpressionEditorView view,
                                       final DecisionNavigatorPresenter decisionNavigator) {
        this.view = view;
        this.decisionNavigator = decisionNavigator;
    }

    @Override
    public void bind(final DMNSession session) {
        final ExpressionEditorView.Presenter editor = makeExpressionEditor(view,
                                                                           decisionNavigator);
        editor.bind(session);
        expressionEditor = Optional.of(editor);
    }

    ExpressionEditorView.Presenter makeExpressionEditor(final ExpressionEditorView view,
                                                        final DecisionNavigatorPresenter decisionNavigator) {
        return new ExpressionEditor(view,
                                    decisionNavigator);
    }

    @Override
    protected void doInit() {
    }

    @Override
    protected void doDestroy() {
        view = null;
        decisionNavigator = null;
        expressionEditor = Optional.empty();
    }

    @Override
    public ExpressionEditorView.Presenter getExpressionEditor() {
        if (expressionEditor.isPresent()) {
            return expressionEditor.get();
        }
        return null;
    }

    @SuppressWarnings("unused")
    public void onCanvasFocusedSelectionEvent(final @Observes CanvasSelectionEvent event) {
        expressionEditor.ifPresent(ExpressionEditorView.Presenter::exit);
    }

    public void onCanvasElementUpdated(final @Observes CanvasElementUpdatedEvent event) {
        expressionEditor.ifPresent(editor -> editor.handleCanvasElementUpdated(event));
    }
}
