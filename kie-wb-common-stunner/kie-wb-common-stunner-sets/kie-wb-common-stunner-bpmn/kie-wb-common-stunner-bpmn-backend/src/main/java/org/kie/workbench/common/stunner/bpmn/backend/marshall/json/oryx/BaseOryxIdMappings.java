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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.DefaultRoute;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
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
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MITrigger;
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
        final Map<Class<?>, String> globalMappings = new HashMap<Class<?>, String>() {{
            put(getDiagramType(),
                "BPMNDiagram");
            // Add here global class <-> oryxId mappings, if any.
            put(Name.class,
                "name");
            put(TaskType.class,
                "tasktype");
            put(NoneTask.class,
                "Task");
            put(UserTask.class,
                "Task");
            put(ScriptTask.class,
                "Task");
            put(BusinessRuleTask.class,
                "Task");
            put(RuleFlowGroup.class,
                "ruleflowgroup");
            put(CalledElement.class,
                "calledelement");
            put(ConditionExpression.class,
                "conditionexpression");
            put(Priority.class,
                "priority");
            put(ExclusiveGateway.class,
                "Exclusive_Databased_Gateway");
            put(TimerSettings.class,
                "timersettings");
            put(EmbeddedSubprocess.class,
                "Subprocess");
            put(AdHocSubprocess.class,
                "AdHocSubprocess");
            put(AdHoc.class,
                "adhocprocess");
            put(ProcessInstanceDescription.class,
                "customdescription");
            put(WaitForCompletion.class,
                "waitforcompletion");
            put(IsAsync.class,
                "isasync");
            put(Skippable.class,
                "skippable");
            put(Subject.class,
                "subject");
            put(Description.class,
                "description");
            put(CreatedBy.class,
                "createdby");
            put(AdHocAutostart.class,
                "customautostart");
            put(OnEntryAction.class,
                "onentryactions");
            put(OnExitAction.class,
                "onexitactions");
            put(IsInterrupting.class,
                "isinterrupting");
            put(SignalRef.class,
                "signalref");
            put(MessageRef.class,
                "messageref");
            put(CancelActivity.class,
                "boundarycancelactivity");
            put(SignalScope.class,
                "signalscope");
            put(ErrorRef.class,
                "errorref");
            put(IntermediateErrorEventCatching.class,
                "IntermediateErrorEvent");
            put(AdHocOrdering.class,
                "adhocordering");
            put(AdHocCompletionCondition.class,
                "adhoccompletioncondition");

            put(MITrigger.class,
                "mitrigger");

            put(MultipleInstanceCollectionInput.class,
                "multipleinstancecollectioninput");
            put(MultipleInstanceCollectionOutput.class,
                "multipleinstancecollectionoutput");
            put(MultipleInstanceDataInput.class,
                "multipleinstancedatainput");
            put(MultipleInstanceDataOutput.class,
                "multipleinstancedataoutput");

            put(MultipleInstanceCompletionCondition.class,
                "multipleinstancecompletioncondition");

            // Simulation properties
            put(TimeUnit.class,
                "timeunit");
            put(StandardDeviation.class,
                "standarddeviation");
            put(DistributionType.class,
                "distributiontype");
            put(WorkingHours.class,
                "workinghours");
            put(UnitCost.class,
                "unitcost");
        }};

        return globalMappings;
    }

    @Override
    public Map<Class<?>, String> getCustomMappings() {
        // No custom mappings, for now.
        return Collections.emptyMap();
    }

    @Override
    public Map<Class<?>, Set<String>> getSkippedProperties() {
        final Map<Class<?>, Set<String>> skippedProperties = new HashMap<Class<?>, Set<String>>() {{
            // Add here global class <-> collection oryx property identifiers to skip processing, if any.
            put(getDiagramType(),
                new HashSet<String>() {{
                    add("name");
                }});
        }};

        return skippedProperties;
    }

    @Override
    public Map<Class<?>, Map<Class<?>, String>> getDefinitionMappings() {
        final Map<Class<?>, Map<Class<?>, String>> definitionMappings = new HashMap<Class<?>, Map<Class<?>, String>>() {{
            // Add here class <-> oryxId mappings just for a concrete definition (stencil), if any.
            Map<Class<?>, String> diagramPropertiesMap = new HashMap<Class<?>, String>();
            put(getDiagramType(),
                diagramPropertiesMap);
            // The name property in the diagram stencil is "processn".
            diagramPropertiesMap.put(Name.class,
                                     "processn");
            // The process variables property in the diagram stencil is "vardefs".
            diagramPropertiesMap.put(ProcessVariables.class,
                                     "vardefs");

            Map<Class<?>, String> userTaskPropertiesMap = new HashMap<Class<?>, String>();
            put(UserTask.class,
                userTaskPropertiesMap);
            userTaskPropertiesMap.put(AssignmentsInfo.class,
                                      "assignmentsinfo");
            userTaskPropertiesMap.put(TaskName.class,
                                      "taskname");

            Map<Class<?>, String> businesRuleTaskPropertiesMap = new HashMap<Class<?>, String>();
            put(BusinessRuleTask.class,
                businesRuleTaskPropertiesMap);
            businesRuleTaskPropertiesMap.put(AssignmentsInfo.class,
                                             "assignmentsinfo");

            Map<Class<?>, String> serviceTaskPropertiesMap = new HashMap<Class<?>, String>();
            put(ServiceTask.class,
                serviceTaskPropertiesMap);
            serviceTaskPropertiesMap.put(AssignmentsInfo.class,
                                         "assignmentsinfo");
            serviceTaskPropertiesMap.put(TaskName.class,
                                         "taskname");

            Map<Class<?>, String> startNoneEventPropertiesMap = new HashMap<Class<?>, String>();
            put(StartNoneEvent.class,
                startNoneEventPropertiesMap);
            startNoneEventPropertiesMap.put(AssignmentsInfo.class,
                                            "assignmentsinfo");

            Map<Class<?>, String> endSignalEventPropertiesMap = new HashMap<Class<?>, String>();
            put(EndSignalEvent.class,
                endSignalEventPropertiesMap);
            endSignalEventPropertiesMap.put(AssignmentsInfo.class,
                                            "assignmentsinfo");

            Map<Class<?>, String> startSignalEventPropertiesMap = new HashMap<Class<?>, String>();
            put(StartSignalEvent.class,
                startSignalEventPropertiesMap);
            startSignalEventPropertiesMap.put(AssignmentsInfo.class,
                                              "assignmentsinfo");

            Map<Class<?>, String> startTimerEventPropertiesMap = new HashMap<Class<?>, String>();
            put(StartTimerEvent.class,
                startTimerEventPropertiesMap);
            startTimerEventPropertiesMap.put(AssignmentsInfo.class,
                                             "assignmentsinfo");

            Map<Class<?>, String> startMessageEventPropertiesMap = new HashMap<Class<?>, String>();
            put(StartMessageEvent.class,
                startMessageEventPropertiesMap);
            startMessageEventPropertiesMap.put(AssignmentsInfo.class,
                                               "assignmentsinfo");

            Map<Class<?>, String> startErrorEventPropertiesMap = new HashMap<Class<?>, String>();
            put(StartErrorEvent.class,
                startErrorEventPropertiesMap);
            startErrorEventPropertiesMap.put(AssignmentsInfo.class,
                                             "assignmentsinfo");

            Map<Class<?>, String> intermediateTimerEventPropertiesMap = new HashMap<Class<?>, String>();
            put(IntermediateTimerEvent.class,
                intermediateTimerEventPropertiesMap);
            intermediateTimerEventPropertiesMap.put(AssignmentsInfo.class,
                                                    "assignmentsinfo");

            Map<Class<?>, String> intermediateSignalEventCatchingPropertiesMap = new HashMap<Class<?>, String>();
            put(IntermediateSignalEventCatching.class,
                intermediateSignalEventCatchingPropertiesMap);
            intermediateSignalEventCatchingPropertiesMap.put(AssignmentsInfo.class,
                                                             "assignmentsinfo");

            Map<Class<?>, String> intermediateErrorEventCatchingPropertiesMap = new HashMap<Class<?>, String>();
            put(IntermediateErrorEventCatching.class,
                intermediateErrorEventCatchingPropertiesMap);
            intermediateErrorEventCatchingPropertiesMap.put(AssignmentsInfo.class,
                                                            "assignmentsinfo");
            Map<Class<?>, String> intermediateMessageEventCatchingPropertiesMap = new HashMap<Class<?>, String>();
            put(IntermediateMessageEventCatching.class,
                intermediateMessageEventCatchingPropertiesMap);
            intermediateMessageEventCatchingPropertiesMap.put(AssignmentsInfo.class,
                                                              "assignmentsinfo");

            Map<Class<?>, String> intermediateSignalEventThrowingPropertiesMap = new HashMap<Class<?>, String>();
            put(IntermediateSignalEventThrowing.class,
                intermediateSignalEventThrowingPropertiesMap);
            intermediateSignalEventThrowingPropertiesMap.put(AssignmentsInfo.class,
                                                             "assignmentsinfo");

            Map<Class<?>, String> intermediateMessageEventThrowingPropertiesMap = new HashMap<Class<?>, String>();
            put(IntermediateMessageEventThrowing.class,
                intermediateMessageEventThrowingPropertiesMap);
            intermediateMessageEventThrowingPropertiesMap.put(AssignmentsInfo.class,
                                                              "assignmentsinfo");

            Map<Class<?>, String> endEventPropertiesMap = new HashMap<Class<?>, String>();
            put(EndNoneEvent.class,
                endEventPropertiesMap);
            put(EndTerminateEvent.class,
                endEventPropertiesMap);
            put(EndErrorEvent.class,
                endEventPropertiesMap);
            endEventPropertiesMap.put(AssignmentsInfo.class,
                                      "assignmentsinfo");

            Map<Class<?>, String> endMessageEventPropertiesMap = new HashMap<Class<?>, String>();
            put(EndMessageEvent.class,
                endMessageEventPropertiesMap);
            endMessageEventPropertiesMap.put(AssignmentsInfo.class,
                                             "assignmentsinfo");

            Map<Class<?>, String> reusableSubprocessPropertiesMap = new HashMap<Class<?>, String>();
            put(ReusableSubprocess.class,
                reusableSubprocessPropertiesMap);
            reusableSubprocessPropertiesMap.put(AssignmentsInfo.class,
                                                "assignmentsinfo");

            Map<Class<?>, String> embeddedSubprocessPropertiesMap = new HashMap<Class<?>, String>();
            put(EmbeddedSubprocess.class,
                embeddedSubprocessPropertiesMap);
            embeddedSubprocessPropertiesMap.put(ProcessVariables.class,
                                                "vardefs");

            Map<Class<?>, String> eventSubprocessPropertiesMap = new HashMap<Class<?>, String>();
            put(EventSubprocess.class,
                eventSubprocessPropertiesMap);
            eventSubprocessPropertiesMap.put(ProcessVariables.class,
                                             "vardefs");

            Map<Class<?>, String> adHocSubprocessPropertiesMap = new HashMap<>();
            put(AdHocSubprocess.class,
                adHocSubprocessPropertiesMap);
            adHocSubprocessPropertiesMap.put(ProcessVariables.class,
                                             "vardefs");

            Map<Class<?>, String> exclusiveGatewayPropertiesMap = new HashMap<Class<?>, String>();
            put(ExclusiveGateway.class,
                exclusiveGatewayPropertiesMap);
            exclusiveGatewayPropertiesMap.put(DefaultRoute.class,
                                              "defaultgate");

            Map<Class<?>, String> inclusiveGatewayPropertiesMap = new HashMap<>();
            put(InclusiveGateway.class,
                inclusiveGatewayPropertiesMap);
            inclusiveGatewayPropertiesMap.put(DefaultRoute.class,
                                              "defaultgate");
            Map<Class<?>, String> multipleInstanceSubprocessPropertiesMap = new HashMap<Class<?>, String>();
            put(MultipleInstanceSubprocess.class,
                multipleInstanceSubprocessPropertiesMap);
            multipleInstanceSubprocessPropertiesMap.put(ProcessVariables.class,
                                                        "vardefs");
        }};

        return definitionMappings;
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
        return get(oryxId,
                   defMappings);
    }

    @Override
    public <T> String getPropertyId(final T definition,
                                    final String oryxId) {
        Class<?> definitionClass = definition.getClass();
        Map<Class<?>, String> mappings = definitionMappings.get(definitionClass);
        if (null != mappings) {
            Class<?> p = get(oryxId,
                             mappings);
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

    private Class<?> get(final String oryxId,
                         final Map<Class<?>, String> map) {
        Class<?> r = getKey(oryxId,
                            map);
        if (null != r) {
            return r;
        }
        return null;
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
