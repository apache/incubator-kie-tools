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


package org.kie.workbench.common.stunner.sw.marshall.json;

import java.lang.reflect.Type;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.CallbackState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.CallbackState_JsonSerializerImpl;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.EventState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.EventState_JsonSerializerImpl;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.ForEachState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.ForEachState_JsonSerializerImpl;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.InjectState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.InjectState_JsonSerializerImpl;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.OperationState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.OperationState_JsonSerializerImpl;
import org.kie.workbench.common.stunner.sw.definition.ParallelState;
import org.kie.workbench.common.stunner.sw.definition.ParallelState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.ParallelState_JsonSerializerImpl;
import org.kie.workbench.common.stunner.sw.definition.SleepState;
import org.kie.workbench.common.stunner.sw.definition.SleepState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.SleepState_JsonSerializerImpl;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.State_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.State_JsonSerializerImpl;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.SwitchState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.SwitchState_JsonSerializerImpl;

public class StateJsonSerializer implements JsonbDeserializer<State>, JsonbSerializer<State> {

    @Override
    public void serialize(State obj, JsonGenerator generator, SerializationContext ctx) {
        if(obj instanceof CallbackState) {
            CallbackState_JsonSerializerImpl.INSTANCE.serialize((CallbackState) obj, generator, ctx);
        } else if(obj instanceof EventState) {
            EventState_JsonSerializerImpl.INSTANCE.serialize((EventState) obj, generator, ctx);
        } else if(obj instanceof ForEachState) {
            ForEachState_JsonSerializerImpl.INSTANCE.serialize((ForEachState) obj, generator, ctx);
        } else if (obj instanceof InjectState) {
            InjectState_JsonSerializerImpl.INSTANCE.serialize((InjectState) obj, generator, ctx);
        } else if (obj instanceof OperationState) {
            OperationState_JsonSerializerImpl.INSTANCE.serialize((OperationState) obj, generator, ctx);
        } else if (obj instanceof ParallelState) {
            ParallelState_JsonSerializerImpl.INSTANCE.serialize((ParallelState) obj, generator, ctx);
        } else if (obj instanceof SleepState) {
            SleepState_JsonSerializerImpl.INSTANCE.serialize((SleepState) obj, generator, ctx);
        } else if (obj instanceof SwitchState) {
            SwitchState_JsonSerializerImpl.INSTANCE.serialize((SwitchState) obj, generator, ctx);
        } else {
            State_JsonSerializerImpl.INSTANCE.serialize(obj, generator, ctx);
        }
    }

    @Override
    public State deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonValue value = parser.getValue();
        if(value != null) {
            JsonObject jsonObject = value.asJsonObject();
            String type = jsonObject.getString("type");
            switch (type) {
                case CallbackState.TYPE_CALLBACK:
                    return CallbackState_JsonDeserializerImpl.INSTANCE.deserialize(parser, ctx, rtType);
                case EventState.TYPE_EVENT:
                    return EventState_JsonDeserializerImpl.INSTANCE.deserialize(parser, ctx, rtType);
                case ForEachState.TYPE_FOR_EACH:
                    return ForEachState_JsonDeserializerImpl.INSTANCE.deserialize(parser, ctx, rtType);
                case InjectState.TYPE_INJECT:
                    return InjectState_JsonDeserializerImpl.INSTANCE.deserialize(parser, ctx, rtType);
                case OperationState.TYPE_OPERATION:
                    return OperationState_JsonDeserializerImpl.INSTANCE.deserialize(parser, ctx, rtType);
                case ParallelState.TYPE_PARALLEL:
                    return ParallelState_JsonDeserializerImpl.INSTANCE.deserialize(parser, ctx, rtType);
                case SleepState.TYPE_SLEEP:
                    return SleepState_JsonDeserializerImpl.INSTANCE.deserialize(parser, ctx, rtType);
                case SwitchState.TYPE_SWITCH:
                    return SwitchState_JsonDeserializerImpl.INSTANCE.deserialize(parser, ctx, rtType);
                default:
                    return State_JsonDeserializerImpl.INSTANCE.deserialize(parser, ctx, rtType);
            }
        }
        return null;
    }
}
