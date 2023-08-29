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

import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasDomainObjectListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Element;

@Dependent
public class DMNGridLayerControlImpl extends AbstractCanvasControl<AbstractCanvas> implements DMNGridLayerControl {

    private DMNGridLayer gridLayer;
    private Optional<ClientSession> session = Optional.empty();

    private CanvasElementListener redrawElementListener = new CanvasElementListener() {
        @Override
        public void update(final Element item) {
            gridLayer.batch();
        }
    };

    private CanvasDomainObjectListener redrawDomainObjectListener = new CanvasDomainObjectListener() {
        @Override
        public void update(final DomainObject domainObject) {
            gridLayer.batch();
        }
    };

    public DMNGridLayerControlImpl() {
        this.gridLayer = makeGridLayer();
    }

    DMNGridLayer makeGridLayer() {
        return new DMNGridLayer();
    }

    @Override
    public void bind(final ClientSession session) {
        this.session = Optional.ofNullable(session);
    }

    @Override
    protected void doInit() {
        withCanvasHandler(abstractCanvasHandler -> {
            abstractCanvasHandler.addRegistrationListener(redrawElementListener);
            abstractCanvasHandler.addDomainObjectListener(redrawDomainObjectListener);
        });
    }

    @Override
    protected void doDestroy() {
        withCanvasHandler(abstractCanvasHandler -> {
            abstractCanvasHandler.removeRegistrationListener(redrawElementListener);
            abstractCanvasHandler.removeDomainObjectListener(redrawDomainObjectListener);
        });

        session = Optional.empty();
        gridLayer = null;
    }

    private void withCanvasHandler(final Consumer<AbstractCanvasHandler> consumer) {
        session.ifPresent(s -> {

            final CanvasHandler canvasHandler = s.getCanvasHandler();

            if (canvasHandler instanceof AbstractCanvasHandler) {
                final AbstractCanvasHandler abstractCanvasHandler = (AbstractCanvasHandler) canvasHandler;
                consumer.accept(abstractCanvasHandler);
            }
        });
    }

    @Override
    public DMNGridLayer getGridLayer() {
        return gridLayer;
    }
}
