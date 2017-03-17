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
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.DragControlImpl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Element;

@Dependent
@CaseManagementEditor
public class CaseManagementDragControlImpl extends DragControlImpl {

    @Inject
    public CaseManagementDragControlImpl(final @CaseManagementEditor CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        super(canvasCommandFactory);
    }

    @Override
    protected void doDragStart(final Element element) {
        super.doDragStart(element);
    }

    @Override
    protected void doDragUpdate(final Element element) {
        //Case Modeller doesn't constraint Shapes to the boundary of their container
    }

    @Override
    protected void doDragEnd(final Element element) {
        //Case Modeller does not update Node's positions
    }

    @Override
    protected void ensureDragConstraints(final ShapeView<?> shapeView) {
        //Case Modeller does not update Node's positions
    }

    double[] getDragShapeSize() {
        return dragShapeSize;
    }
}
