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


package org.kie.workbench.common.stunner.forms.client.session;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;

@ApplicationScoped
public class StunnerFormsHandler {

    private final SessionManager sessionManager;
    private final Event<RefreshFormPropertiesEvent> refreshFormsEvent;

    // CDI proxy.
    public StunnerFormsHandler() {
        this(null, null);
    }

    @Inject
    public StunnerFormsHandler(final SessionManager sessionManager,
                               final Event<RefreshFormPropertiesEvent> refreshFormsEvent) {
        this.sessionManager = sessionManager;
        this.refreshFormsEvent = refreshFormsEvent;
    }

    public void refreshCurrentSessionForms() {
        refreshCurrentSessionForms(null);
    }

    public void refreshCurrentSessionForms(final Class<?> defSetType) {
        final ClientSession session = sessionManager.getCurrentSession();
        boolean fireEvent = false;
        if (null != session) {
            if (null != defSetType) {
                final CanvasHandler canvasHandler = session.getCanvasHandler();
                if (null != canvasHandler) {
                    final String id = canvasHandler.getDiagram().getMetadata().getDefinitionSetId();
                    final String expected = BindableAdapterUtils.getDefinitionSetId(defSetType);
                    if (id.equals(expected)) {
                        fireEvent = true;
                    }
                }
            } else {
                fireEvent = true;
            }
            if (fireEvent) {
                refreshFormsEvent.fire(new RefreshFormPropertiesEvent(session));
            }
        }
    }
}
