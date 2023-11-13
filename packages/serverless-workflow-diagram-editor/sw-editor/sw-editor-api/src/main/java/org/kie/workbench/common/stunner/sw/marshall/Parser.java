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


package org.kie.workbench.common.stunner.sw.marshall;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.ParallelState;
import org.kie.workbench.common.stunner.sw.definition.SleepState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

import static org.kie.workbench.common.stunner.sw.definition.CallbackState.TYPE_CALLBACK;
import static org.kie.workbench.common.stunner.sw.definition.EventState.TYPE_EVENT;
import static org.kie.workbench.common.stunner.sw.definition.ForEachState.TYPE_FOR_EACH;
import static org.kie.workbench.common.stunner.sw.definition.InjectState.TYPE_INJECT;
import static org.kie.workbench.common.stunner.sw.definition.OperationState.TYPE_OPERATION;
import static org.kie.workbench.common.stunner.sw.definition.ParallelState.TYPE_PARALLEL;
import static org.kie.workbench.common.stunner.sw.definition.SleepState.TYPE_SLEEP;
import static org.kie.workbench.common.stunner.sw.definition.SwitchState.TYPE_SWITCH;

@ApplicationScoped
public class Parser {

    @Inject
    private FactoryManager factoryManager;

    public Workflow parse(Workflow jso) {
        Workflow workflow = parse(Workflow.class, jso);
        workflow.setStates(new State[jso.getStates().length]);

        State[] states = jso.getStates();
        for (int i = 0; i < states.length; i++) {
            State s = states[i];
            State state = parseState(s);
            workflow.getStates()[i] = state;
        }
        return workflow;
    }

    private State parseState(State jso) {
        switch (jso.getType()) {
            case TYPE_INJECT:
                return parse(InjectState.class, jso);
            case TYPE_EVENT:
                return parse(EventState.class, jso);
            case TYPE_SWITCH:
                return parse(SwitchState.class, jso);
            case TYPE_OPERATION:
                return parse(OperationState.class, jso);
            case TYPE_SLEEP:
                return parse(SleepState.class, jso);
            case TYPE_PARALLEL:
                return parse(ParallelState.class, jso);
            case TYPE_FOR_EACH:
                return parse(ForEachState.class, jso);
            case TYPE_CALLBACK:
                return parse(CallbackState.class, jso);
            default:
                return null;
        }
    }

    private <T> T parse(Class<? extends T> type, T jso) {
        return MarshallerUtils.parse(factoryManager, type, jso);
    }
}
