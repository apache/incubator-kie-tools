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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.session.command;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;

@Dependent
public class SaveDiagramSessionCommand extends AbstractClientSessionCommand<EditorSession> {

    public SaveDiagramSessionCommand() {
        super(false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void execute(final Callback<V> callback) {
        //Do not automatically export a SVG diagram for the DMN Designer
    }

    @Override
    public boolean accepts(final ClientSession session) {
        return false;
    }

    /**
     * This command is always disabled.
     * @param enable Ignored.
     */
    @Override
    protected void enable(final boolean enable) {
        super.enable(false);
    }

    /**
     * This command is always disabled.
     * @param enabled Ignored.
     */
    @Override
    protected void setEnabled(final boolean enabled) {
        super.setEnabled(false);
    }
}
