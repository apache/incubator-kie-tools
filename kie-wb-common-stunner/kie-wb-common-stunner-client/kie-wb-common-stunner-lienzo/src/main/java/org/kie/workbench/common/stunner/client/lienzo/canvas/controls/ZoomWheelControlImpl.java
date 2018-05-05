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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;

import com.ait.lienzo.client.core.mediator.EventFilter;
import com.ait.lienzo.client.core.mediator.IEventFilter;
import com.ait.lienzo.client.core.mediator.MouseWheelZoomMediator;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;

@Dependent
@Default
public class ZoomWheelControlImpl<C extends AbstractCanvas> extends AbstractMediatorControl<MouseWheelZoomMediator, C> implements ZoomControl<C> {

    private static final double MIN_SCALE = 1;
    private static final double MAX_SCALE = 2;

    private final IEventFilter[] filters = new IEventFilter[]{EventFilter.CONTROL};

    @Override
    protected MouseWheelZoomMediator buildMediator() {
        return new MouseWheelZoomMediator(filters) {{
            setMinScale(MIN_SCALE);
            setMaxScale(MAX_SCALE);
        }};
    }

    @Override
    public ZoomControl<C> setMinScale(final double minScale) {
        getMediator().setMinScale(minScale);
        return this;
    }

    @Override
    public ZoomControl<C> setMaxScale(final double maxScale) {
        getMediator().setMaxScale(maxScale);
        return this;
    }

    @Override
    public ZoomControl<C> setZoomFactory(final double factor) {
        getMediator().setZoomFactor(factor);
        return this;
    }

    @Override
    public ZoomControl<C> scale(final double factor) {
        getLienzoLayer().scale(factor);
        return this;
    }

    @Override
    public ZoomControl<C> scale(double sx,
                                double sy) {
        getLienzoLayer().scale(sx,
                               sy);
        return this;
    }

    private LienzoLayer getLienzoLayer() {
        return (LienzoLayer) canvas.getLayer();
    }
}
