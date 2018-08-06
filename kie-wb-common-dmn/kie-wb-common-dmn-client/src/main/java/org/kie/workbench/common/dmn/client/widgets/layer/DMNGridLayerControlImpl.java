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

package org.kie.workbench.common.dmn.client.widgets.layer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;

@Dependent
public class DMNGridLayerControlImpl extends AbstractCanvasControl<AbstractCanvas> implements DMNGridLayerControl {

    private DMNGridLayer gridLayer;

    public DMNGridLayerControlImpl() {
        this.gridLayer = makeGridLayer();
    }

    DMNGridLayer makeGridLayer() {
        return new DMNGridLayer();
    }

    @Override
    protected void doInit() {
    }

    @Override
    protected void doDestroy() {
        gridLayer = null;
    }

    @Override
    public DMNGridLayer getGridLayer() {
        return gridLayer;
    }

    @SuppressWarnings("unused")
    public void onCanvasElementUpdatedEvent(final @Observes CanvasElementUpdatedEvent event) {
        gridLayer.batch();
    }
}
