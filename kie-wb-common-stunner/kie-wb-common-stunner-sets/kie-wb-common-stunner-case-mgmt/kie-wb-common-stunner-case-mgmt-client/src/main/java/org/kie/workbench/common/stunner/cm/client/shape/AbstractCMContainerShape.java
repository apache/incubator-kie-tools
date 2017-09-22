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

package org.kie.workbench.common.stunner.cm.client.shape;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementShapeDef;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.shapes.client.BasicContainerShape;

public class AbstractCMContainerShape<W extends BPMNDefinition, D extends CaseManagementShapeDef<W>, V extends ShapeView<?>>
        extends BasicContainerShape<W, D, V> {

    private static final double ACTIVE_STROKE_WIDTH = 1d;

    public AbstractCMContainerShape(final D shapeDef,
                                    final V view) {
        super(shapeDef,
              view);
        getShape().getShapeStateHelper().setStrokeWidthForActiveState(ACTIVE_STROKE_WIDTH);
    }
}
