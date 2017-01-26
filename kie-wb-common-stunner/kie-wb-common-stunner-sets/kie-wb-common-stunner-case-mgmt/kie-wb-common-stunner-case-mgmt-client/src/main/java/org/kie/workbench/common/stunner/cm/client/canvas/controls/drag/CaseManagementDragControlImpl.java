/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.client.canvas.controls.drag;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasGrid;
import org.kie.workbench.common.stunner.core.client.canvas.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.DragControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.DragControlImpl;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

@Dependent
@CaseManagementEditor
public class CaseManagementDragControlImpl extends DragControlImpl {

    @Inject
    public CaseManagementDragControlImpl(final @CaseManagementEditor CanvasCommandFactory canvasCommandFactory) {
        super(canvasCommandFactory);
    }

    @Override
    public DragControl<AbstractCanvasHandler, Element> setDragGrid(final CanvasGrid grid) {
        //Case Management doesn't show a "Drag Grid" when shapes are moved
        return this;
    }

    @Override
    protected void doDragStart(final Element element) {
        final double[] size = GraphUtils.getNodeSize((View) element.getContent());
        dragShapeSize[0] = size[0];
        dragShapeSize[1] = size[1];
    }

    @Override
    protected void doDragEnd(final Element element) {
        //Case Modeller does not update Node's positions
    }
}
