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

import java.util.Optional;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.cm.client.wires.AbstractCaseManagementShape;
import org.kie.workbench.common.stunner.cm.client.wires.VerticalStackLayoutManager;

/**
 * The Lienzo view implementation for the Stage shape.
 */
public class StageView extends AbstractCaseManagementShape<StageView> {

    private final double voffset;

    public StageView(final double width,
                     final double height,
                     final double voffset) {
        this(create(new MultiPath(),
                    width,
                    height,
                    voffset),
             width,
             height,
             voffset);
    }

    private StageView(final MultiPath path,
                      final double width,
                      final double height,
                      final double voffset) {
        super(CM_SHAPE_VIEW_EVENT_TYPES,
              path,
              width,
              height);
        this.voffset = voffset;
        setLayoutHandler(new VerticalStackLayoutManager());
        setResizable(false);
        setDraggable(true);
    }

    /**
     * Append the path parts for a stage.
     * @param path The source multipath
     * @param w The stage width
     * @param h The stage height
     * @param voffset The chevron "V" offset
     */
    private static MultiPath create(final MultiPath path,
                                    final double w,
                                    final double h,
                                    final double voffset) {
        path.M(0,
               0).L(w,
                    0).L(w + voffset,
                         h / 2).L(w,
                                  h).L(0,
                                       h).L(voffset,
                                            h / 2).L(0,
                                                     0).Z().close();
        return path
                .setFillColor(ColorName.LIGHTGOLDENRODYELLOW)
                .setStrokeColor(ColorName.BLACK);
    }

    @Override
    public StageView setSize(final double width,
                             final double height) {
        create(getPath().clear(),
               width,
               height,
               voffset);
        updateFillGradient(width,
                           height);
        refresh();
        return this;
    }

    @Override
    public Optional<MultiPath> makeDropZone() {
        return Optional.of(new MultiPath().rect(0d,
                                                0d,
                                                getWidth(),
                                                getMinHeight())
                                   .setStrokeWidth(1));
    }

    @Override
    protected AbstractCaseManagementShape createGhost() {
        final StageView ghost = new StageView(getWidth(),
                                              getHeight(),
                                              voffset);
        for (WiresShape ws : getChildShapes()) {
            final AbstractCaseManagementShape wsg = ((AbstractCaseManagementShape) ws).getGhost();
            ghost.add(wsg);
        }

        return ghost;
    }
}
