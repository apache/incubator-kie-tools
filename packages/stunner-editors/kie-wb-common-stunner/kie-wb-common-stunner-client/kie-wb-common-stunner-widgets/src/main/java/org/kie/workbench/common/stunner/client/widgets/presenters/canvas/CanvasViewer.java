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


package org.kie.workbench.common.stunner.client.widgets.presenters.canvas;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.presenters.Viewer;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;

/**
 * A viewer type for generic instances based on any subtypes for <code>Canvas</code> view types and <code>CanvasHandler</code> types..
 * @param <T> The instance type supported.
 * @param <H> The handler type.
 * @param <V> The view type.
 * @param <K> The callback type.
 */
public interface CanvasViewer<T, H extends CanvasHandler, V extends IsWidget, K extends Viewer.Callback> extends Viewer<T, H, V, K> {

    /**
     * Returns a canvas mediators control enabled and available to use.
     */
    <C extends Canvas> MediatorsControl<C> getMediatorsControl();
}
