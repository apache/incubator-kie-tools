/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.project;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.backend.server.AbstractDeleteModuleObserverBridge;
import org.guvnor.common.services.project.events.DeleteModuleEvent;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;

/**
 * CDI implementation for KIE Workbenches
 */
@ApplicationScoped
public class DeleteKieModuleObserverBridge
        extends AbstractDeleteModuleObserverBridge<KieModule> {

    private KieModuleFactory moduleFactory;

    public DeleteKieModuleObserverBridge() {
        //Zero-arg constructor for CDI proxying
    }

    @Inject
    public DeleteKieModuleObserverBridge(final @Named("ioStrategy") IOService ioService,
                                         final Event<DeleteModuleEvent> deleteModuleEvent,
                                         final KieModuleFactory moduleFactory) {
        super(ioService,
              deleteModuleEvent);
        this.moduleFactory = PortablePreconditions.checkNotNull("moduleFactory",
                                                                moduleFactory);
    }

    public void onBatchResourceChanges(final @Observes ResourceDeletedEvent event) {
        super.onBatchResourceChanges(event);
    }

    public void onBatchResourceChanges(final @Observes ResourceBatchChangesEvent resourceBatchChangesEvent) {
        super.onBatchResourceChanges(resourceBatchChangesEvent);
    }

    @Override
    protected KieModule getModule(final Path path) {
        return moduleFactory.simpleModuleInstance(path);
    }
}
