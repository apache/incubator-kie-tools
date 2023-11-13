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


package org.kie.workbench.common.stunner.forms.client.widgets;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.forms.client.event.FormPropertiesOpened;
import org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainer;

@Singleton
public class FormsFlushManager {

    protected String formElementUUID;
    protected FormsContainer container;

    void setCurrentContainer(FormsContainer container) {
        this.container = container;
    }

    public void flush(ClientSession session) {
        if (container != null) {
            container.flush(session.getCanvasHandler().getDiagram().getGraph().getUUID(), formElementUUID);
        }
    }

    void onFormsOpenedEvent(@Observes FormPropertiesOpened event) {
        formElementUUID = event.getUuid();
    }

    @PreDestroy
    public void destroy() {
        this.container = null;
    }
}
