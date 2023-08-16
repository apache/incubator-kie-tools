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

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.uberfire.backend.vfs.Path;

public abstract class AbstractExportSessionCommand extends AbstractClientSessionCommand<AbstractSession<AbstractCanvas, AbstractCanvasHandler>> {

    public AbstractExportSessionCommand(final boolean enabled) {
        super(enabled);
    }

    protected abstract void export(final String fileName);

    @Override
    public boolean accepts(final ClientSession session) {
        return true;
    }

    @Override
    public <T> void execute(final Callback<T> callback) {
        final String fileName = getFileName();
        export(fileName);
        callback.onSuccess();
    }

    private String getFileName() {
        final Path path = getSession().getCanvasHandler().getDiagram().getMetadata().getPath();
        return null != path ? path.getFileName() : getSession().getCanvasHandler().getDiagram().getGraph().getUUID();
    }
}