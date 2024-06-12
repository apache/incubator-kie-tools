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

package org.kie.workbench.common.stunner.client.lienzo.components.mediators.preview;

import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import com.ait.lienzo.tools.client.event.MouseEventUtil;

public class TogglePreviewUtils {

    static final double PREVIEW_MARGIN = 25d;

    public static TogglePreviewEvent buildEvent(LienzoBoundsPanel lienzoBoundsPanel,
                                                TogglePreviewEvent.EventType eventType) {
        final int absoluteLeft = MouseEventUtil.getAbsoluteLeft(lienzoBoundsPanel.getElement());
        final int absoluteTop = MouseEventUtil.getAbsoluteTop(lienzoBoundsPanel.getElement());
        final int width = lienzoBoundsPanel.getWidePx();
        final int height = lienzoBoundsPanel.getHighPx() - (int) (PREVIEW_MARGIN * 2);

        return new TogglePreviewEvent(absoluteLeft,
                                      absoluteTop,
                                      width,
                                      height,
                                      eventType);
    }

    public static boolean IsPreviewAvailable(ScrollablePanel scrollablePanel) {
        final double internalWidth = scrollablePanel.calculateInternalScrollPanelWidth();
        final double internalHeight = scrollablePanel.calculateInternalScrollPanelHeight();

        return internalWidth != 1 || internalHeight != 1;
    }
}
