/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.backend;

import java.util.concurrent.Executor;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.server.api.model.KieContainerResource;
import org.kie.server.controller.api.model.KieServerInstance;
import org.kie.server.controller.api.storage.KieServerControllerStorage;
import org.kie.server.controller.rest.RestKieServerControllerAdminImpl;
import org.uberfire.commons.async.SimpleAsyncExecutorService;

@ApplicationScoped
public class KieServerWBControllerAdminImpl extends RestKieServerControllerAdminImpl {

    private SimpleAsyncExecutorService executor;

    public KieServerWBControllerAdminImpl() {
        this.executor = SimpleAsyncExecutorService.getDefaultInstance();
    }

    @Inject
    @Override
    public void setStorage(KieServerControllerStorage storage) {
        super.setStorage(storage);
    }

    protected void doDoNotifyKieServersOnCreateContainer(final KieServerInstance kieServerInstance, final KieContainerResource container) {
        super.notifyKieServersOnCreateContainer(kieServerInstance, container);
    }

    protected void doNotifyKieServersOnDeleteContainer(KieServerInstance kieServerInstance, String containerId) {
        super.notifyKieServersOnDeleteContainer(kieServerInstance, containerId);
    }

    @Override
    public void notifyKieServersOnCreateContainer(final KieServerInstance kieServerInstance, final KieContainerResource container) {

        executor.execute( new Runnable() {
            @Override
            public void run() {
                try {
                    doDoNotifyKieServersOnCreateContainer(kieServerInstance, container);
                } catch ( final Exception ex ) {

                }
            }
        } );
    }

    @Override
    public void notifyKieServersOnDeleteContainer(final KieServerInstance kieServerInstance, final String containerId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    doNotifyKieServersOnDeleteContainer(kieServerInstance, containerId);
                } catch (final Exception ex) {

                }
            }
        });
    }
}
