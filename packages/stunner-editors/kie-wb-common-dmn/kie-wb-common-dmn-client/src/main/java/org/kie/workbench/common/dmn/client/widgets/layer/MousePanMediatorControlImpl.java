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

package org.kie.workbench.common.dmn.client.widgets.layer;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

@Dependent
public class MousePanMediatorControlImpl extends AbstractCanvasControl<AbstractCanvas> implements MousePanMediatorControl {

    private RestrictedMousePanMediator mousePanMediator;

    @Override
    public void bind(final DMNSession session) {
        final DMNGridLayer gridLayer = session.getGridLayer();
        this.mousePanMediator = new RestrictedMousePanMediator(gridLayer);
    }

    @Override
    protected void doInit() {
    }

    @Override
    protected void doDestroy() {
        mousePanMediator = null;
    }

    @Override
    public RestrictedMousePanMediator getMousePanMediator() {
        return mousePanMediator;
    }
}
