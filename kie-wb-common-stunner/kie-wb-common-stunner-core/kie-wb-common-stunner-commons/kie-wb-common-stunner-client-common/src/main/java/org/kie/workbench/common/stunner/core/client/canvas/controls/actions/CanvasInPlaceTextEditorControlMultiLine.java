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

package org.kie.workbench.common.stunner.core.client.canvas.controls.actions;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.graph.Element;

@Dependent
@MultiLineTextEditorBox
@Default
public class CanvasInPlaceTextEditorControlMultiLine
        extends AbstractCanvasInPlaceTextEditorControl {

    private final FloatingView<IsWidget> floatingView;
    private final TextEditorBox<AbstractCanvasHandler, Element> textEditorBox;
    private final Event<CanvasSelectionEvent> canvasSelectionEvent;

    @Inject
    public CanvasInPlaceTextEditorControlMultiLine(final FloatingView<IsWidget> floatingView,
                                                   final @MultiLineTextEditorBox TextEditorBox<AbstractCanvasHandler, Element> textEditorBox,
                                                   final Event<CanvasSelectionEvent> canvasSelectionEvent) {
        this.floatingView = floatingView;
        this.textEditorBox = textEditorBox;
        this.canvasSelectionEvent = canvasSelectionEvent;
    }

    @Override
    protected FloatingView<IsWidget> getFloatingView() {
        return floatingView;
    }

    @Override
    protected TextEditorBox<AbstractCanvasHandler, Element> getTextEditorBox() {
        return textEditorBox;
    }

    @Override
    protected Event<CanvasSelectionEvent> getCanvasSelectionEvent() {
        return canvasSelectionEvent;
    }
}
