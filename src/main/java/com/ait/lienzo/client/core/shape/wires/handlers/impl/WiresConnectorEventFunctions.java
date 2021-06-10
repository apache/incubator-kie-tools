/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.wires.SelectionManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorHandlerImpl.Event;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class WiresConnectorEventFunctions
{
    public static Predicate<WiresConnector> canShowControlPoints()
    {
        return connector -> !connector.getControl().areControlPointsVisible();
    }

    public static Predicate<WiresConnector> canHideControlPoints(final WiresManager wiresManager)
    {
        return connector -> null == wiresManager.getSelectionManager() ||
               !wiresManager.getSelectionManager().getSelectedItems().getConnectors().contains(connector);
    }

    public static Consumer<Event> select(final WiresManager wiresManager,
                                         final WiresConnector connector)
    {
        return new Consumer<WiresConnectorHandlerImpl.Event>()
        {
            @Override
            public void accept(WiresConnectorHandlerImpl.Event event)
            {
                if (null != wiresManager.getSelectionManager()) {
                    wiresManager.getSelectionManager().selected(connector, false);
                }
                connector.getControl().showControlPoints();
            }
        };
    }

    public static Consumer<WiresConnectorHandlerImpl.Event> addControlPoint(final WiresConnector connector)
    {
        return event -> connector.getControl().addControlPoint(event.x, event.y);
    }
}
