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

package org.kie.workbench.common.stunner.bpmn.client.session;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.Disposer;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.Request;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.impl.ClientFullSessionImpl;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;

@Dependent
@BPMN
public class BPMNClientFullSession extends ClientFullSessionImpl {

    @Inject
    public BPMNClientFullSession(final @BPMN CanvasFactory<AbstractCanvas, AbstractCanvasHandler> factory,
                                 final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                 final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                 final @Request SessionCommandManager<AbstractCanvasHandler> requestCommandManager,
                                 final RegistryFactory registryFactory,
                                 final Disposer<CanvasControl> canvasControlDisposer) {
        super(factory,
              canvasCommandManager,
              sessionCommandManager,
              requestCommandManager,
              registryFactory,
              canvasControlDisposer);
    }
}
