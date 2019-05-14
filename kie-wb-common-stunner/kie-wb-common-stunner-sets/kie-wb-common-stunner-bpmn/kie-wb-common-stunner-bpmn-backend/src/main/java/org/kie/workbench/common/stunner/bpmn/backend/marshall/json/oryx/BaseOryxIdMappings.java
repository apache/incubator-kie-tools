/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.kie.soup.commons.util.Maps;
import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.StartConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.common.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.DefaultRoute;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.DistributionType;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.StandardDeviation;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.TimeUnit;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.UnitCost;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.WorkingHours;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocOrdering;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CalledElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CreatedBy;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Description;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Skippable;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Subject;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.WaitForCompletion;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;

/**
 * This class contains the mappings for the different stencil identifiers that are different from
 * the patterns used in this tool.
 */
public abstract class BaseOryxIdMappings implements OryxIdMappings {

    private final DefinitionManager definitionManager;

    private final Map<Class<?>, String> defMappings = new HashMap<>();

    private final Map<Class<?>, String> globalMappings = getGlobalMappings();

    private final Map<Class<?>, String> customMappings = getCustomMappings();

    private final Map<Class<?>, Set<String>> skippedProperties = getSkippedProperties();

    private final Map<Class<?>, Map<Class<?>, String>> definitionMappings = getDefinitionMappings();

    protected BaseOryxIdMappings() {
        this(null);
    }

    public BaseOryxIdMappings(final DefinitionManager definitionManager) {
        this.definitionManager = definitionManager;
    }

    protected abstract Class<? extends BPMNDiagram> getDiagramType();

    @Override
    public void init(final List<Class<?>> definitions) {
        // Load default & custom mappings for BPMN definitions.
        for (final Class<?> defClass : definitions) {
            String customMapping = customMappings.get(defClass);
            customMapping = customMapping != null ? customMapping : globalMappings.get(defClass);
            final String oryxId = customMapping != null ? customMapping : getDefaultOryxDefinitionId(defClass);
            addOryxDefinitionId(defClass,
                                oryxId);
        }
    }

    public void addOryxDefinitionId(final Class<?> type,
                                    final String oryxId) {
        defMappings.put(type,
                        oryxId);
    }

    @Override
    public Map<Class<?>, String> getGlobalMappings() {

        return new Maps.Builder<Class<?>, String>()
                .put(getDiagramType(), "BPMNDiagram")
                // Add here global class <-> oryxId mappings, if any.
                .put(Name.class, "name")
                .put(TaskType.class, "tasktype")
                .put(NoneTask.class, "Task")
                .put(UserTask.class, "Task")
                .put(ScriptTask.class, "Task")
                .put(BusinessRuleTask.class, "Task")
                .put(RuleFlowGroup.class, "ruleflowgroup")
                .put(CalledElement.class, "calledelement")
                .put(ConditionExpression.class, "conditionexpression")
                .put(Priority.class, "priority")
                .put(ExclusiveGateway.class, "Exclusive_Databased_Gateway")
                .put(TimerSettings.class, "timersettings")
                .put(EmbeddedSubprocess.class, "Subprocess")
                .put(AdHocSubprocess.class, "AdHocSubprocess")
                .put(AdHoc.class, "adhocprocess")
                .put(ProcessInstanceDescription.class, "customdescription")
                .put(WaitForCompletion.class, "waitforcompletion")
                .put(IsAsync.class, "isasync")
                .put(NotificationsInfo.class, "notificationsinfo")
                .put(ReassignmentsInfo.class, "reassignmentsinfo")
                .put(Skippable.class, "skippable")
                .put(Subject.class, "subject")
                .put(Description.class, "description")
                .put(CreatedBy.class, "createdby")
                .put(AdHocAutostart.class, "customautostart")
                .put(OnEntryAction.class, "onentryactions")
                .put(OnExitAction.class, "onexitactions")
                .put(IsInterrupting.class, "isinterrupting")
                .put(SignalRef.class, "signalref")
                .put(MessageRef.class, "messageref")
                .put(EscalationRef.class, "escalationcode")
                .put(CancelActivity.class, "boundarycancelactivity")
                .put(SignalScope.class, "signalscope")
                .put(ErrorRef.class, "errorref")
                .put(IntermediateErrorEventCatching.class, "IntermediateErrorEvent")
                .put(AdHocOrdering.class, "adhocordering")
                .put(AdHocCompletionCondition.class, "adhoccompletioncondition")

                .put(IsMultipleInstance.class, "mitrigger")

                .put(MultipleInstanceCollectionInput.class, "multipleinstancecollectioninput")
                .put(MultipleInstanceCollectionOutput.class, "multipleinstancecollectionoutput")
                .put(MultipleInstanceDataInput.class, "multipleinstancedatainput")
                .put(MultipleInstanceDataOutput.class, "multipleinstancedataoutput")

                .put(MultipleInstanceCompletionCondition.class, "multipleinstancecompletioncondition")

                // Simulation properties
                .put(TimeUnit.class, "timeunit")
                .put(StandardDeviation.class, "standarddeviation")
                .put(DistributionType.class, "distributiontype")
                .put(WorkingHours.class, "workinghours")
                .put(UnitCost.class, "unitcost")
                .build();
    }

    @Override
    public Map<Class<?>, String> getCustomMappings() {
        // No custom mappings, for now.
        return Collections.emptyMap();
    }

    @Override
    public Map<Class<?>, Set<String>> getSkippedProperties() {
        return new Maps.Builder<Class<?>, Set<String>>()
                // Add here global class <-> collection oryx property identifiers to skip processing, if any.
                .put(getDiagramType(), new Sets.Builder<String>()
                        .add("name").build()
                ).build();
    }

    @Override
    public Map<Class<?>, Map<Class<?>, String>> getDefinitionMappings() {
        return new Maps.Builder<Class<?>, Map<Class<?>, String>>()

                // Add here class <-> oryxId mappings just for a concrete definition (stencil), if any.
                .put(getDiagramType(), new Maps.Builder<Class<?>, String>()
                        // The name property in the diagram stencil is "processn".
                        .put(Name.class, "processn")
                        // The process variables property in the diagram stencil is "vardefs".
                        .put(ProcessVariables.class, "vardefs").build())

                .put(UserTask.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo")
                        .put(TaskName.class, "taskname").build())

                .put(BusinessRuleTask.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(ServiceTask.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo")
                        .put(TaskName.class, "taskname").build())

                .put(StartNoneEvent.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(EndSignalEvent.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(StartSignalEvent.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(StartTimerEvent.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(StartMessageEvent.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(StartErrorEvent.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(StartConditionalEvent.class, new Maps.Builder<Class<?>, String>()
                        .build())

                .put(StartEscalationEvent.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(IntermediateTimerEvent.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(IntermediateConditionalEvent.class, new Maps.Builder<Class<?>, String>()
                        .build())

                .put(IntermediateSignalEventCatching.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(IntermediateErrorEventCatching.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(IntermediateMessageEventCatching.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(IntermediateEscalationEvent.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(IntermediateSignalEventThrowing.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(IntermediateMessageEventThrowing.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(IntermediateEscalationEventThrowing.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(EndNoneEvent.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(EndTerminateEvent.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(EndErrorEvent.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(EndMessageEvent.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(EndEscalationEvent.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(ReusableSubprocess.class, new Maps.Builder<Class<?>, String>()
                        .put(AssignmentsInfo.class, "assignmentsinfo").build())

                .put(EmbeddedSubprocess.class, new Maps.Builder<Class<?>, String>()
                        .put(ProcessVariables.class, "vardefs").build())

                .put(EventSubprocess.class, new Maps.Builder<Class<?>, String>()
                        .put(ProcessVariables.class, "vardefs").build())

                .put(AdHocSubprocess.class, new Maps.Builder<Class<?>, String>()
                        .put(ProcessVariables.class, "vardefs").build())

                .put(ExclusiveGateway.class, new Maps.Builder<Class<?>, String>()
                        .put(DefaultRoute.class, "defaultgate").build())

                .put(InclusiveGateway.class, new Maps.Builder<Class<?>, String>()
                        .put(DefaultRoute.class, "defaultgate").build())

                .put(MultipleInstanceSubprocess.class, new Maps.Builder<Class<?>, String>()
                        .put(ProcessVariables.class, "vardefs").build())
                .build();
    }

    @Override
    public String getOryxDefinitionId(final Object def) {
        return defMappings.get(def.getClass());
    }

    @Override
    public String getOryxPropertyId(final Class<?> clazz) {
        String mapping = customMappings.get(clazz);
        mapping = mapping != null ? mapping : globalMappings.get(clazz);
        return mapping != null ? mapping : getDefaultOryxPropertyId(clazz);
    }

    @Override
    public String getOryxPropertyId(final Class<?> definitionClass,
                                    final Class<?> clazz) {
        Map<Class<?>, String> mappings = definitionMappings.get(definitionClass);
        if (null != mappings) {
            String r = mappings.get(clazz);
            if (null != r) {
                return r;
            }
        }
        return getOryxPropertyId(clazz);
    }

    @Override
    public boolean isSkipProperty(final Class<?> definitionClass,
                                  final String oryxPropertyId) {
        Set<String> toSkip = skippedProperties.get(definitionClass);
        return toSkip != null && toSkip.contains(oryxPropertyId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<?> getProperty(final T definition,
                                    final String oryxId) {
        Class<?> clazz = getKey(oryxId,
                                customMappings);
        if (null != clazz) {
            return clazz;
        }
        clazz = getKey(oryxId,
                       globalMappings);
        if (null != clazz) {
            return clazz;
        }

        Set<Object> properties = (Set<Object>) definitionManager.adapters().forDefinition().getProperties(definition);
        if (null != properties && !properties.isEmpty()) {
            for (Object property : properties) {
                Class<?> pClass = property.getClass();
                String pId = getDefaultOryxPropertyId(pClass);
                if (oryxId.equals(pId)) {
                    return pClass;
                }
            }
        }
        return null;
    }

    @Override
    public Class<?> getDefinition(final String oryxId) {
        return getKey(oryxId, defMappings);
    }

    @Override
    public <T> String getPropertyId(final T definition,
                                    final String oryxId) {
        Class<?> definitionClass = definition.getClass();
        Map<Class<?>, String> mappings = definitionMappings.get(definitionClass);
        if (null != mappings) {
            Class<?> p = getKey(oryxId, mappings);
            if (null != p) {
                return getPropertyId(p);
            }
        }
        Class<?> c = getProperty(definition,
                                 oryxId);
        return null != c ? getPropertyId(c) : null;
    }

    @Override
    public String getDefinitionId(final String oryxId) {
        Class<?> c = getDefinition(oryxId);
        return null != c ? getDefinitionId(c) : null;
    }

    @Override
    public String getPropertyId(final Class<?> clazz) {
        return BindableAdapterUtils.getPropertyId(clazz);
    }

    @Override
    public String getDefinitionId(final Class<?> clazz) {
        return BindableAdapterUtils.getDefinitionId(clazz);
    }

    private Class<?> getKey(final String value,
                            final Map<Class<?>, String> map) {
        Set<Map.Entry<Class<?>, String>> entrySet = map.entrySet();
        for (Map.Entry<Class<?>, String> entry : entrySet) {
            String oId = entry.getValue();
            if (oId.equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String getDefaultOryxDefinitionId(final Class<?> clazz) {
        return clazz.getSimpleName();
    }

    private String getDefaultOryxPropertyId(final Class<?> clazz) {
        return StringUtils.uncapitalize(clazz.getSimpleName());
    }
}
