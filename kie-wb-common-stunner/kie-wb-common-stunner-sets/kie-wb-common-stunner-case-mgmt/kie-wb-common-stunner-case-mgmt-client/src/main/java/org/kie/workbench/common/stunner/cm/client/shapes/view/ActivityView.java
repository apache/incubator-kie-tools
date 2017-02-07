/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.client.shapes.view;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.cm.client.wires.AbstractCaseModellerShape;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ShapeViewSupportedEvents;
import org.kie.workbench.common.stunner.lienzo.util.LienzoPaths;

/**
 * The Lienzo view implementation for the Activity shape.
 */
public class ActivityView extends AbstractCaseModellerShape<ActivityView> {

    public ActivityView(final double width,
                        final double height) {
        super(ShapeViewSupportedEvents.DESKTOP_NO_RESIZE_EVENT_TYPES,
              create(new MultiPath(),
                     width,
                     height),
              width,
              height);
        setResizable(false);
        setZIndex(1);
    }

    @Override
    public ActivityView setSize(final double width,
                                final double height) {
        create(getPath().clear(),
               width,
               height);
        updateFillGradient(width,
                           height);
        refresh();
        return this;
    }

    /**
     * Append the path parts for a Activity.
     * @param path The source multipath
     * @param w The activity width
     * @param h The activity height
     */
    private static MultiPath create(final MultiPath path,
                                    final double w,
                                    final double h) {
        LienzoPaths.rectangle(path,
                              w,
                              h,
                              0.0);
        path.setFillColor(ColorName.LIME);
        return path;
    }

    @Override
    public AbstractCaseModellerShape getGhost() {
        final ActivityView ghost = new ActivityView(getWidth(),
                                                    getHeight());
        for (WiresShape ws : getChildShapes()) {
            final AbstractCaseModellerShape wsg = ((AbstractCaseModellerShape) ws).getGhost();
            ghost.add(wsg);
        }

        return ghost;
    }
}
