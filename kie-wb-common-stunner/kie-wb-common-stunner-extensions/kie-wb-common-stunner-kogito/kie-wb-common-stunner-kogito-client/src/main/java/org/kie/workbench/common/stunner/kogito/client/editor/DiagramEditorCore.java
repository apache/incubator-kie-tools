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

package org.kie.workbench.common.stunner.kogito.client.editor;

import java.lang.annotation.Annotation;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.editor.commons.client.BaseEditorView;

public interface DiagramEditorCore<M extends Metadata, D extends Diagram> {

    interface View extends UberView<DiagramEditorCore>,
                           BaseEditorView,
                           RequiresResize,
                           ProvidesResize,
                           IsWidget {

        void setWidget(final IsWidget widget);
    }

    void open(final D diagram);

    Annotation[] getDockQualifiers();

    void initialiseKieEditorForSession(final D diagram);

    SessionEditorPresenter<EditorSession> newSessionEditorPresenter();

    SessionViewerPresenter<ViewerSession> newSessionViewerPresenter();

    String getEditorIdentifier();

    int getCurrentDiagramHash();

    CanvasHandler getCanvasHandler();

    void onSaveError(final ClientRuntimeError error);

    SessionPresenter<? extends ClientSession, ?, Diagram> getSessionPresenter();

    void doFocus();

    void doLostFocus();
}
