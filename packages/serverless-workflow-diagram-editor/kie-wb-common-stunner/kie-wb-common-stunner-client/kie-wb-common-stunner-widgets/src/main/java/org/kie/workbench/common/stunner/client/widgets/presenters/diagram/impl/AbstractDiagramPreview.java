/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.mvp.Command;

public abstract class AbstractDiagramPreview<D extends Diagram, H extends AbstractCanvasHandler>
        implements DiagramViewer<D, H> {

    public abstract AbstractDiagramViewer<D, H> getViewer();

    @Override
    public void open(final D item,
                     final DiagramViewerCallback<D> callback) {
        getViewerOrNothing(() -> getViewer().open(item,
                                                  callback));
    }

    @Override
    public void scale(final int width,
                      final int height) {
        getViewerOrNothing(() -> getViewer().scale(width,
                                                   height,
                                                   false));
    }

    @Override
    public void clear() {
        getViewerOrNothing(() -> getViewer().clear());
    }

    @Override
    public void destroy() {
        getViewerOrNothing(() -> getViewer().destroy());
    }

    @Override
    public D getInstance() {
        return null != getViewer() ? getViewer().getInstance() : null;
    }

    @Override
    public H getHandler() {
        return null != getViewer() ? getViewer().getHandler() : null;
    }

    @Override
    public WidgetWrapperView getView() {
        return getViewer().getView();
    }

    private void getViewerOrNothing(final Command callback) {
        if (null != getViewer()) {
            callback.execute();
        }
    }
}
