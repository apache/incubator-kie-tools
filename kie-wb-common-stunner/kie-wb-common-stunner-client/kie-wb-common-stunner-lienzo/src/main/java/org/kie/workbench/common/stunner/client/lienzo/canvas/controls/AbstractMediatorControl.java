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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import com.ait.lienzo.client.core.mediator.IMediator;
import com.ait.lienzo.client.core.mediator.Mediators;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;

public abstract class AbstractMediatorControl<M extends IMediator, C extends AbstractCanvas> extends AbstractCanvasControl<C> {

    protected abstract M buildMediator();

    protected M mediator;

    @Override
    protected void doInit() {
        this.mediator = buildMediator();
        getMediators().push(getMediator());
    }

    private Mediators getMediators() {
        return getLayer().getLienzoLayer().getViewport().getMediators();
    }

    private LienzoLayer getLayer() {
        return (LienzoLayer) canvas.getLayer();
    }

    @Override
    protected void doDestroy() {
        if (null != mediator) {
            mediator.cancel();
            getMediators().remove(mediator);
            mediator = null;
        }
    }

    public M getMediator() {
        return mediator;
    }
}
