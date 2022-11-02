/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client.shapes;

import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.AbstractShape;
import org.kie.workbench.common.stunner.core.client.shape.impl.NodeShapeImpl;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateHandler;

public class StartShape extends NodeShapeImpl {

    public StartShape() {
        super(new AbstractShape<StartShapeView>() {
            final StartShapeView shape = new StartShapeView();

            @Override
            public ShapeStateHandler getShapeStateHandler() {
                return shape.getShapeStateHandler();
            }

            @Override
            public void setUUID(String uuid) {
                shape.setUUID(uuid);
            }

            @Override
            public String getUUID() {
                return shape.getUUID();
            }

            @Override
            public StartShapeView getShapeView() {
                return shape;
            }
        });
    }

    @Override
    public void applyState(ShapeState shapeState) {
        super.applyState(shapeState);
        if (ShapeState.SELECTED == shapeState) {
            getShapeView().setFillColor("#E7F1FA");
        } else {
            getShapeView().setFillColor("#fff");
        }
    }
}
