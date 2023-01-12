package org.kie.workbench.common.stunner.sw.definition.custom;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.JsonbDeserializer;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.CallbackState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.EventState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.ForEachState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.InjectState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.OperationState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.ParallelState;
import org.kie.workbench.common.stunner.sw.definition.ParallelState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.SleepState;
import org.kie.workbench.common.stunner.sw.definition.SleepState_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.State_JsonDeserializerImpl;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.SwitchState_JsonDeserializerImpl;


public class StateJsonDeserializer extends JsonbDeserializer<State> {

    @Override
    public State deserialize(JsonValue value, DeserializationContext ctx) {
        JsonObject jsonObject = value.asJsonObject();
        String type = jsonObject.getString("type");

        switch (type) {
            case CallbackState.TYPE_CALLBACK:
                return CallbackState_JsonDeserializerImpl.INSTANCE.deserialize(jsonObject, ctx);
            case EventState.TYPE_EVENT:
                return EventState_JsonDeserializerImpl.INSTANCE.deserialize(jsonObject, ctx);
            case ForEachState.TYPE_FOR_EACH:
                return ForEachState_JsonDeserializerImpl.INSTANCE.deserialize(jsonObject, ctx);
            case InjectState.TYPE_INJECT:
                return InjectState_JsonDeserializerImpl.INSTANCE.deserialize(jsonObject, ctx);
            case OperationState.TYPE_OPERATION:
                return OperationState_JsonDeserializerImpl.INSTANCE.deserialize(jsonObject, ctx);
            case ParallelState.TYPE_PARALLEL:
                return ParallelState_JsonDeserializerImpl.INSTANCE.deserialize(jsonObject, ctx);
            case SleepState.TYPE_SLEEP:
                return SleepState_JsonDeserializerImpl.INSTANCE.deserialize(jsonObject, ctx);
            case SwitchState.TYPE_SWITCH:
                return SwitchState_JsonDeserializerImpl.INSTANCE.deserialize(jsonObject, ctx);
            default:
                return State_JsonDeserializerImpl.INSTANCE.deserialize(jsonObject, ctx);
        }
    }
}
