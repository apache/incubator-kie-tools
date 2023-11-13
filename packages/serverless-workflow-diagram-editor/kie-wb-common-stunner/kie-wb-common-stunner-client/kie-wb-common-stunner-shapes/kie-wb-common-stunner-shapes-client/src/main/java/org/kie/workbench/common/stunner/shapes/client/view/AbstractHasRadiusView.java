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


package org.kie.workbench.common.stunner.shapes.client.view;

import com.ait.lienzo.client.core.shape.MultiPath;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresContainerShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.HasRadius;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;

public abstract class AbstractHasRadiusView<T extends AbstractHasRadiusView> extends WiresContainerShapeView<T>
        implements HasRadius<T> {

    public AbstractHasRadiusView(final ViewEventType[] supportedEventTypes,
                                 final MultiPath path) {
        super(supportedEventTypes, path);
    }

    @Override
    public T setMinRadius(Double minRadius) {
        double minDiameter = minRadius * 2;

        getPath().setMinWidth(minDiameter)
                .setMinHeight(minDiameter);

        return cast();
    }

    @Override
    public T setMaxRadius(Double maxRadius) {
        double maxDiameter = maxRadius * 2;

        getPath().setMaxWidth(maxDiameter)
                .setMaxHeight(maxDiameter);

        return cast();
    }
}
