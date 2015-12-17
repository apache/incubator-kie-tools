/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.context;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;

@ApplicationScoped
public class DataModelerWorkbenchContext {

    @Inject
    private Event<DataModelerWorkbenchContextChangeEvent> dataModelerWBContextEvent;

    private DataModelerContext activeContext;

    public DataModelerWorkbenchContext() {
    }

    public void setActiveContext( DataModelerContext activeContext ) {
        this.activeContext = activeContext;
        dataModelerWBContextEvent.fire( new DataModelerWorkbenchContextChangeEvent() );
    }

    public DataModelerContext getActiveContext() {
        return activeContext;
    }

    public void clearContext() {
        this.activeContext = null;
        dataModelerWBContextEvent.fire( new DataModelerWorkbenchContextChangeEvent()  );
    }
}
