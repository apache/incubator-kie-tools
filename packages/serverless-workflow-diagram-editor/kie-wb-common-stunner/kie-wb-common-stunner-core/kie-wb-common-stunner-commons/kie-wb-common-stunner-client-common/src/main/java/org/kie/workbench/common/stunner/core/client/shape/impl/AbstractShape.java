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


package org.kie.workbench.common.stunner.core.client.shape.impl;

import org.kie.workbench.common.stunner.core.client.shape.Lifecycle;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public abstract class AbstractShape<V extends ShapeView>
        implements
        Shape<V>,
        Lifecycle {

    public abstract ShapeStateHandler getShapeStateHandler();

    @Override
    public void beforeDraw() {
    }

    @Override
    public void afterDraw() {
        if (getShapeView() instanceof HasTitle) {
            ((HasTitle) getShapeView()).moveTitleToTop();
        }
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        getShapeStateHandler()
                .applyState(shapeState);
    }
}
