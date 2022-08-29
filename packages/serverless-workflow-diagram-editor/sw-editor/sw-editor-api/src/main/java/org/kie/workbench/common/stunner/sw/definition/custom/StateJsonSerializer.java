/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.definition.custom;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.ParallelState;
import org.kie.workbench.common.stunner.sw.definition.SleepState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;

public class StateJsonSerializer implements JsonbSerializer<State> {

    @Override
    public void serialize(State obj, JsonGenerator generator, SerializationContext ctx) {
        if(obj instanceof CallbackState) {
            org.kie.workbench.common.stunner.sw.definition.CallbackState_JsonSerializerImpl.INSTANCE.serialize((CallbackState) obj, generator, ctx);
        } else if(obj instanceof EventState) {
            org.kie.workbench.common.stunner.sw.definition.EventState_JsonSerializerImpl.INSTANCE.serialize((EventState) obj, generator, ctx);
        } else if(obj instanceof ForEachState) {
            org.kie.workbench.common.stunner.sw.definition.ForEachState_JsonSerializerImpl.INSTANCE.serialize((ForEachState) obj, generator, ctx);
        } else if (obj instanceof InjectState) {
            org.kie.workbench.common.stunner.sw.definition.InjectState_JsonSerializerImpl.INSTANCE.serialize((InjectState) obj, generator, ctx);
        } else if (obj instanceof OperationState) {
            org.kie.workbench.common.stunner.sw.definition.OperationState_JsonSerializerImpl.INSTANCE.serialize((OperationState) obj, generator, ctx);
        } else if (obj instanceof ParallelState) {
            org.kie.workbench.common.stunner.sw.definition.ParallelState_JsonSerializerImpl.INSTANCE.serialize((ParallelState) obj, generator, ctx);
        } else if (obj instanceof SleepState) {
            org.kie.workbench.common.stunner.sw.definition.SleepState_JsonSerializerImpl.INSTANCE.serialize((SleepState) obj, generator, ctx);
        } else if (obj instanceof SwitchState) {
            org.kie.workbench.common.stunner.sw.definition.SwitchState_JsonSerializerImpl.INSTANCE.serialize((SwitchState) obj, generator, ctx);
        } else {
            org.kie.workbench.common.stunner.sw.definition.State_JsonSerializerImpl.INSTANCE.serialize(obj, generator, ctx);
        }
    }
}
