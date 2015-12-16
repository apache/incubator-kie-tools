/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.backend.server;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kie.api.definition.rule.Rule;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;

public class AuditLogger {

    private final Set<String> logs = new HashSet<String>();
    private final Map<String, KieSession> ksessions;

    public AuditLogger(Map<String, KieSession> ksessions) {

        this.ksessions = ksessions;

        for (KieSession ksession : ksessions.values()) {
            if (ksession != null) {
                addRuleRuntimeEventListener(ksession);
                addAgendaEventListener(ksession);
                addProcessEventListener(ksession);
            }
        }
    }

    public Set<String> getLog() {
        return logs;
    }

    private void addProcessEventListener(KieSession ksession) {
        ksession.addEventListener(new ProcessEventListener() {
            @Override
            public void beforeProcessStarted(ProcessStartedEvent processStartedEvent) {
                log(processStartedEvent);
            }

            @Override
            public void afterProcessStarted(ProcessStartedEvent processStartedEvent) {
                log(processStartedEvent);
            }

            @Override
            public void beforeProcessCompleted(ProcessCompletedEvent processCompletedEvent) {
                log(processCompletedEvent);
            }

            @Override
            public void afterProcessCompleted(ProcessCompletedEvent processCompletedEvent) {
                log(processCompletedEvent);
            }

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent processNodeTriggeredEvent) {
                log(processNodeTriggeredEvent);
            }

            @Override
            public void afterNodeTriggered(ProcessNodeTriggeredEvent processNodeTriggeredEvent) {
                log(processNodeTriggeredEvent);
            }

            @Override
            public void beforeNodeLeft(ProcessNodeLeftEvent processNodeLeftEvent) {
                log(processNodeLeftEvent);
            }

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent processNodeLeftEvent) {
                log(processNodeLeftEvent);
            }

            @Override
            public void beforeVariableChanged(ProcessVariableChangedEvent processVariableChangedEvent) {
                log(processVariableChangedEvent);
            }

            @Override
            public void afterVariableChanged(ProcessVariableChangedEvent processVariableChangedEvent) {
                log(processVariableChangedEvent);
            }
        });
    }

    private void addAgendaEventListener(KieSession ksession) {
        ksession.addEventListener(new AgendaEventListener() {
            @Override
            public void matchCreated(MatchCreatedEvent matchCreatedEvent) {
                log(matchCreatedEvent);
            }

            @Override
            public void matchCancelled(MatchCancelledEvent matchCancelledEvent) {
                log(matchCancelledEvent);
            }

            @Override
            public void beforeMatchFired(BeforeMatchFiredEvent beforeMatchFiredEvent) {
//                log(beforeMatchFiredEvent);
            }

            @Override
            public void afterMatchFired(AfterMatchFiredEvent afterMatchFiredEvent) {
                logs.add("Rule " + afterMatchFiredEvent.getMatch().getRule() + " fired.");
            }

            @Override
            public void agendaGroupPopped(AgendaGroupPoppedEvent agendaGroupPoppedEvent) {
                log(agendaGroupPoppedEvent);
            }

            @Override
            public void agendaGroupPushed(AgendaGroupPushedEvent agendaGroupPushedEvent) {
                log(agendaGroupPushedEvent);
            }

            @Override
            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent ruleFlowGroupActivatedEvent) {
                log(ruleFlowGroupActivatedEvent);
            }

            @Override
            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent ruleFlowGroupActivatedEvent) {
                log(ruleFlowGroupActivatedEvent);
            }

            @Override
            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent ruleFlowGroupDeactivatedEvent) {
                log(ruleFlowGroupDeactivatedEvent);
            }

            @Override
            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent ruleFlowGroupDeactivatedEvent) {
                log(ruleFlowGroupDeactivatedEvent);
            }
        });
    }

    private void addRuleRuntimeEventListener(KieSession ksession) {
        ksession.addEventListener(new RuleRuntimeEventListener() {
            @Override
            public void objectInserted(ObjectInsertedEvent objectInsertedEvent) {
                Object object = objectInsertedEvent.getObject();
                Rule rule = objectInsertedEvent.getRule();
                if (rule == null) {
                    logs.add("Fact " + object.getClass().getName() + " inserted.");
                } else {
                    logs.add("Fact " + object.getClass().getName() + " inserted in rule " + rule.getName() + ". Fact[ " + object.toString() + " ].");
                }
            }

            @Override
            public void objectUpdated(ObjectUpdatedEvent objectUpdatedEvent) {
                Object object = objectUpdatedEvent.getObject();
                Rule rule = objectUpdatedEvent.getRule();
                Object oldObject = objectUpdatedEvent.getOldObject();

                if (rule == null) {

                    logs.add("Object " + object.getClass().getName() + " updated. Old fact[ " + oldObject.toString()
                             + " ]. New fact[ " + object.toString() + " ].");
                } else {
                    logs.add("Object " + object.getClass().getName() + " updated in rule " + rule.getName()
                             + ". Old fact[ " + oldObject.toString()
                             + " ]. New fact[ " + object.toString() + " ].");
                }
            }

            @Override
            public void objectDeleted(ObjectDeletedEvent objectDeletedEvent) {
                Object oldObject = objectDeletedEvent.getOldObject();
                Rule rule = objectDeletedEvent.getRule();
                if (rule == null) {
                    logs.add("Object " + oldObject.getClass().getName() + " deleted. Fact[ " + oldObject.toString() + " ].");
                } else {
                    logs.add("Object " + oldObject.getClass().getName() + " deleted in rule " + rule.getName() + ". Fact[ " + oldObject.toString() + " ].");
                }
            }
        });
    }

    private void log(Object o) {
        logs.add(o.toString());
    }
}
