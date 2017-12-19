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

package org.kie.workbench.common.stunner.cm.client.wires;

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeLocationControlImpl;
import com.ait.lienzo.client.core.types.Point2D;

public class CaseManagementShapeLocationControl extends WiresShapeLocationControlImpl {

    public CaseManagementShapeLocationControl(WiresShape m_shape) {
        super(m_shape);
    }

    @Override
    public void setShapeLocation(Point2D location) {
    }

    public void execute() {
    }
}
