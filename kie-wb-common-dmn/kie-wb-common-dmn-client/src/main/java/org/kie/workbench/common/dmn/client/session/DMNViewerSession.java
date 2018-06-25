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

package org.kie.workbench.common.dmn.client.session;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultViewerSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.mvp.Command;

@Dependent
@DMNEditor
public class DMNViewerSession extends DefaultViewerSession implements DMNSession {

    @Inject
    public DMNViewerSession(final ManagedSession session,
                            final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager) {
        super(session,
              canvasCommandManager);
    }

    @Override
    public void init(final Metadata metadata,
                     final Command callback) {
        init(s -> s.registerCanvasControl(ZoomControl.class)
                     .registerCanvasControl(PanControl.class)
                     .registerCanvasHandlerControl(SelectionControl.class,
                                                   MultipleSelection.class)
                     .registerCanvasControl(ExpressionGridCache.class),
             metadata,
             callback);
    }

    @Override
    public ExpressionGridCache getExpressionGridCache() {
        return (ExpressionGridCache) getSession().getCanvasControl(ExpressionGridCache.class);
    }
}
