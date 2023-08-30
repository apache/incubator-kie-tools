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


package org.kie.workbench.common.stunner.bpmn;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.kie.workbench.common.stunner.bpmn.client.forms.changeHandlers.businessRuleTask.BusinessRuleTaskChangeHandler;
import org.kie.workbench.common.stunner.bpmn.client.forms.filters.AssociationFilterProvider;
import org.kie.workbench.common.stunner.bpmn.client.forms.filters.CatchingIntermediateEventFilterProvider;
import org.kie.workbench.common.stunner.bpmn.client.forms.filters.StartEventFilterProvider;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.forms.client.formFilters.FormFiltersProviderFactory;
import org.kie.workbench.common.stunner.forms.client.formFilters.StunnerFormElementFilterProvider;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.domainChangeHandlers.DomainObjectFieldChangeHandlerRegistry;
import org.uberfire.client.views.pfly.sys.PatternFlyBootstrapper;

@EntryPoint
@Bundle("resources/i18n/StunnerBPMNConstants.properties")
public class StunnerBPMNEntryPoint {

    private SessionManager sessionManager;

    private ManagedInstance<StunnerFormElementFilterProvider> managedFilters;

    private DomainObjectFieldChangeHandlerRegistry changeHandlerRegistry;

    private DomainObjectFieldChangeHandlerRegistry domainObjectFieldChangeHandlerRegistry;

    @Inject
    public StunnerBPMNEntryPoint(SessionManager sessionManager, ManagedInstance<StunnerFormElementFilterProvider> managedFilters, DomainObjectFieldChangeHandlerRegistry changeHandlerRegistry) {
        this.sessionManager = sessionManager;
        this.managedFilters = managedFilters;
        this.changeHandlerRegistry = changeHandlerRegistry;
    }

    @PostConstruct
    public void init() {
        PatternFlyBootstrapper.ensureMonacoEditorLoaderIsAvailable();

        FormFiltersProviderFactory.registerProvider(new StartEventFilterProvider(sessionManager, StartNoneEvent.class));
        FormFiltersProviderFactory.registerProvider(new StartEventFilterProvider(sessionManager, StartCompensationEvent.class));
        FormFiltersProviderFactory.registerProvider(new StartEventFilterProvider(sessionManager, StartSignalEvent.class));
        FormFiltersProviderFactory.registerProvider(new StartEventFilterProvider(sessionManager, StartMessageEvent.class));
        FormFiltersProviderFactory.registerProvider(new StartEventFilterProvider(sessionManager, StartErrorEvent.class));
        FormFiltersProviderFactory.registerProvider(new StartEventFilterProvider(sessionManager, StartTimerEvent.class));
        FormFiltersProviderFactory.registerProvider(new StartEventFilterProvider(sessionManager, StartConditionalEvent.class));
        FormFiltersProviderFactory.registerProvider(new StartEventFilterProvider(sessionManager, StartEscalationEvent.class));
        FormFiltersProviderFactory.registerProvider(new CatchingIntermediateEventFilterProvider(sessionManager, IntermediateTimerEvent.class));
        FormFiltersProviderFactory.registerProvider(new CatchingIntermediateEventFilterProvider(sessionManager, IntermediateErrorEventCatching.class));
        FormFiltersProviderFactory.registerProvider(new CatchingIntermediateEventFilterProvider(sessionManager, IntermediateConditionalEvent.class));
        FormFiltersProviderFactory.registerProvider(new CatchingIntermediateEventFilterProvider(sessionManager, IntermediateCompensationEvent.class));
        FormFiltersProviderFactory.registerProvider(new CatchingIntermediateEventFilterProvider(sessionManager, IntermediateSignalEventCatching.class));
        FormFiltersProviderFactory.registerProvider(new CatchingIntermediateEventFilterProvider(sessionManager, IntermediateLinkEventCatching.class));
        FormFiltersProviderFactory.registerProvider(new CatchingIntermediateEventFilterProvider(sessionManager, IntermediateEscalationEvent.class));
        FormFiltersProviderFactory.registerProvider(new CatchingIntermediateEventFilterProvider(sessionManager, IntermediateMessageEventCatching.class));
        FormFiltersProviderFactory.registerProvider(new AssociationFilterProvider());

        //registering managed filters instances
        managedFilters.forEach(FormFiltersProviderFactory::registerProvider);

        changeHandlerRegistry.register(BusinessRuleTask.class, BusinessRuleTaskChangeHandler.class);
    }
}
