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

package org.kie.workbench.common.dmn.client.session.presenters.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.DMNCommand;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPreviewImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

@DMNEditor
@Dependent
public class DMNSessionPreview implements SessionDiagramPreview<AbstractSession> {

    private final SessionDiagramPreview<AbstractSession> delegate;

    @Inject
    public DMNSessionPreview(final @Default SessionDiagramPreview<AbstractSession> delegate) {
        this.delegate = delegate;
    }

    @PostConstruct
    public void init() {
        ((SessionPreviewImpl) delegate).setCommandAllowed(command -> !(command instanceof DMNCommand));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends Canvas> ZoomControl<C> getZoomControl() {
        return (ZoomControl<C>) delegate.getZoomControl();
    }

    @Override
    public void open(final AbstractSession item,
                     final SessionViewerCallback<Diagram> callback) {
        delegate.open(item,
                      callback);
    }

    @Override
    public void open(final AbstractSession item,
                     final int width,
                     final int height,
                     final SessionViewerCallback<Diagram> callback) {
        delegate.open(item,
                      width,
                      height,
                      callback);
    }

    @Override
    public void scale(final int width,
                      final int height) {
        delegate.scale(width,
                       height);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public void destroy() {
        delegate.destroy();
    }

    @Override
    public AbstractSession getInstance() {
        return delegate.getInstance();
    }

    @Override
    public AbstractCanvasHandler getHandler() {
        return delegate.getHandler();
    }

    @Override
    public IsWidget getView() {
        return delegate.getView();
    }
}
