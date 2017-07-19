/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.shape.view;

import com.ait.lienzo.client.core.shape.MultiPath;
import org.kie.workbench.common.stunner.cm.client.wires.AbstractCaseManagementShape;
import org.kie.workbench.common.stunner.cm.client.wires.HorizontalStackLayoutManager;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;

/**
 * The Lienzo view implementation for the Diagram shape.
 */
public class DiagramView extends AbstractCaseManagementShape<DiagramView> {

    public DiagramView(final double width,
                       final double height) {
        super(new ViewEventType[]{ViewEventType.MOUSE_CLICK},
              create(new MultiPath(),
                     width,
                     height),
              width,
              height);
        setLayoutHandler(new HorizontalStackLayoutManager());
        setResizable(false);
        setDraggable(false);
    }

    /**
     * Append the path parts for a diagram.
     * @param path The source multipath
     * @param w The diagram width
     * @param h The diagram height
     */
    private static MultiPath create(final MultiPath path,
                                    final double w,
                                    final double h) {
        path.rect(0,
                  0,
                  w,
                  h);
        return path;
    }

    @Override
    public DiagramView setSize(final double width,
                               final double height) {
        create(getPath().clear(),
               width,
               height);
        updateFillGradient(width,
                           height);
        refresh();
        return this;
    }

    @Override
    public AbstractCaseManagementShape getGhost() {
        return null;
    }
}
