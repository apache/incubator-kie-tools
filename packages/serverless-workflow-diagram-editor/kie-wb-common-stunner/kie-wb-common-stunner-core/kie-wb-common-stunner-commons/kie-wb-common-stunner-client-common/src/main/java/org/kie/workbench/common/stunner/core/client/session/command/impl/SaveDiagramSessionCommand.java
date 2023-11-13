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


package org.kie.workbench.common.stunner.core.client.session.command.impl;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;

/**
 * This session commands saves the current Diagram on {@link CanvasHandler#getDiagram()}.
 * This is responsible to generate the diagram SVG and save it as well.
 * Does not support undo operation.
 */
@Dependent
public class SaveDiagramSessionCommand extends AbstractClientSessionCommand<EditorSession> {

    @Inject
    public SaveDiagramSessionCommand() {
        super(true);
    }

    @Override
    public <V> void execute(final Callback<V> callback) {
        //TODO (kogito): call the diagram client and remove the logic from Editor Screens
    }

    @Override
    public boolean accepts(final ClientSession session) {
        return session instanceof EditorSession;
    }
}
