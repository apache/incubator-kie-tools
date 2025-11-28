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


package org.kie.workbench.common.stunner.bpmn.client.session;

import java.util.Collection;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.bpmn.client.dataproviders.CalledElementFormProvider;
import org.kie.workbench.common.stunner.bpmn.client.dataproviders.RuleFlowGroupFormProvider;
import org.kie.workbench.common.stunner.bpmn.client.diagram.DiagramTypeClientService;
import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientService;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.core.client.session.impl.SessionInitializer;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.mvp.Command;

@BPMN
@ApplicationScoped
public class BPMNSessionInitializer implements SessionInitializer {

    private static Logger LOGGER = Logger.getLogger(BPMNSessionInitializer.class.getName());

    private final WorkItemDefinitionClientService workItemDefinitionService;
    private final DiagramTypeClientService diagramTypeService;

    // CDI proxy.
    protected BPMNSessionInitializer() {
        this(null, null);
    }

    @Inject
    public BPMNSessionInitializer(final WorkItemDefinitionClientService workItemDefinitionService,
                                  final DiagramTypeClientService diagramTypeService) {
        this.workItemDefinitionService = workItemDefinitionService;
        this.diagramTypeService = diagramTypeService;
    }

    @Override
    public void init(final Metadata metadata,
                     final Command completeCallback) {
        diagramTypeService.loadDiagramType(metadata);
        CalledElementFormProvider.initServerData();
        RuleFlowGroupFormProvider.initServerData();
        workItemDefinitionService
                .call(metadata)
                .then(workItemDefinitions -> {
                    completeCallback.execute();
                    return null;
                })
                .catch_((Promise.CatchOnRejectedCallbackFn<Collection<WorkItemDefinition>>) error -> {
                    LOGGER.severe("Error obtaining the work item definitions [error=" + error + "]");
                    completeCallback.execute();
                    return null;
                });
    }
}
