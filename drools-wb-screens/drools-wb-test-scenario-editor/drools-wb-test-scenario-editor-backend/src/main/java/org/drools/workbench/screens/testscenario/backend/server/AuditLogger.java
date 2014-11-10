/*
 * Copyright 2014 JBoss Inc
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

import org.kie.api.definition.rule.Rule;
import org.kie.api.event.process.*;
import org.kie.api.event.rule.*;
import org.kie.api.runtime.KieSession;

import java.util.HashSet;
import java.util.Set;

public class AuditLogger {

    private final Set<String> logs = new HashSet<String>();
    private final KieSession ksession;


    public AuditLogger(KieSession ksession) {

        this.ksession = ksession;

        addRuleRuntimeEventListener();

        addAgendaEventListener();

        addProcessEventListener();

    }

    public Set<String> getLog() {
        return logs;
    }

    private void addProcessEventListener() {
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

    private void addAgendaEventListener() {
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

    private void addRuleRuntimeEventListener() {
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
                logs.add("Object " + objectUpdatedEvent.getObject().getClass().getName() + " updated in rule " + objectUpdatedEvent.getRule().getName()
                        + ". Old fact[ " + objectUpdatedEvent.getOldObject().toString()
                        + " ]. New fact[ " + objectUpdatedEvent.getObject().toString() + " ].");
                logs.add("test");
            }

            @Override
            public void objectDeleted(ObjectDeletedEvent objectDeletedEvent) {
                logs.add(
                        "Object " + objectDeletedEvent.getOldObject().getClass().getName() + " deleted in rule " + objectDeletedEvent.getRule().getName() + ". Fact[ " + objectDeletedEvent.getOldObject().toString() + " ].");
            }
        });
    }

    private void log(Object o) {
        logs.add(o.toString());
    }
}
