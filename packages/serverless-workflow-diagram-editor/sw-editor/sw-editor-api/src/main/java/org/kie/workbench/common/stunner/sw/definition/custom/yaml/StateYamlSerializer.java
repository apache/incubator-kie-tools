package org.kie.workbench.common.stunner.sw.definition.custom.yaml;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.CallbackState_YamlMapperImpl;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.EventState_YamlMapperImpl;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.ForEachState_YamlMapperImpl;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.InjectState_YamlMapperImpl;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.OperationState_YamlMapperImpl;
import org.kie.workbench.common.stunner.sw.definition.ParallelState;
import org.kie.workbench.common.stunner.sw.definition.ParallelState_YamlMapperImpl;
import org.kie.workbench.common.stunner.sw.definition.SleepState;
import org.kie.workbench.common.stunner.sw.definition.SleepState_YamlMapperImpl;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.State_YamlMapperImpl;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.SwitchState_YamlMapperImpl;


public class StateYamlSerializer implements YAMLDeserializer<State>, YAMLSerializer<State> {

    @Override
    public State deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) throws YAMLDeserializationException {
        YamlMapping value = yaml.getMappingNode(key);
        if(value != null) {
            String type = value.<String>getScalarNode("type").value();
            switch (type) {
                case CallbackState.TYPE_CALLBACK:
                    return CallbackState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, key, ctx);
                case EventState.TYPE_EVENT:
                    return EventState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, key, ctx);
                case ForEachState.TYPE_FOR_EACH:
                    return ForEachState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, key, ctx);
                case InjectState.TYPE_INJECT:
                    return InjectState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, key, ctx);
                case OperationState.TYPE_OPERATION:
                    return OperationState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, key, ctx);
                case ParallelState.TYPE_PARALLEL:
                    return ParallelState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, key, ctx);
                case SleepState.TYPE_SLEEP:
                    return SleepState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, key, ctx);
                case SwitchState.TYPE_SWITCH:
                    return SwitchState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, key, ctx);
                default:
                    return State_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, key, ctx);
            }
        }
        return null;
    }

    @Override
    public State deserialize(YamlNode node, YAMLDeserializationContext ctx) {
        if(node != null) {
            YamlMapping value = node.asMapping();
            String type = value.<String>getScalarNode("type").value();
            switch (type) {
                case CallbackState.TYPE_CALLBACK:
                    return CallbackState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, ctx);
                case EventState.TYPE_EVENT:
                    return EventState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, ctx);
                case ForEachState.TYPE_FOR_EACH:
                    return ForEachState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, ctx);
                case InjectState.TYPE_INJECT:
                    return InjectState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, ctx);
                case OperationState.TYPE_OPERATION:
                    return OperationState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, ctx);
                case ParallelState.TYPE_PARALLEL:
                    return ParallelState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, ctx);
                case SleepState.TYPE_SLEEP:
                    return SleepState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, ctx);
                case SwitchState.TYPE_SWITCH:
                    return SwitchState_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, ctx);
                default:
                    return State_YamlMapperImpl.INSTANCE.getDeserializer().deserialize(value, ctx);
            }
        }
        return null;
    }

    @Override
    public void serialize(YamlMapping writer, String propertyName, State obj, YAMLSerializationContext ctx) {
        if(obj instanceof CallbackState) {
            CallbackState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, propertyName, (CallbackState) obj, ctx);
        } else if(obj instanceof EventState) {
            EventState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, propertyName, (EventState) obj, ctx);
        } else if(obj instanceof ForEachState) {
            ForEachState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, propertyName, (ForEachState) obj, ctx);
        } else if (obj instanceof InjectState) {
            InjectState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, propertyName, (InjectState) obj, ctx);
        } else if (obj instanceof OperationState) {
            OperationState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, propertyName, (OperationState) obj, ctx);
        } else if (obj instanceof ParallelState) {
            ParallelState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, propertyName, (ParallelState) obj, ctx);
        } else if (obj instanceof SleepState) {
            SleepState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, propertyName, (SleepState) obj, ctx);
        } else if (obj instanceof SwitchState) {
            SwitchState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, propertyName, (SwitchState) obj, ctx);
        } else {
            State_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, propertyName, obj, ctx);
        }
    }

    @Override
    public void serialize(YamlSequence writer, State obj, YAMLSerializationContext ctx) {
        if(obj instanceof CallbackState) {
            CallbackState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, (CallbackState) obj, ctx);
        } else if(obj instanceof EventState) {
            EventState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, (EventState) obj, ctx);
        } else if(obj instanceof ForEachState) {
            ForEachState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, (ForEachState) obj, ctx);
        } else if (obj instanceof InjectState) {
            InjectState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, (InjectState) obj, ctx);
        } else if (obj instanceof OperationState) {
            OperationState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, (OperationState) obj, ctx);
        } else if (obj instanceof ParallelState) {
            ParallelState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, (ParallelState) obj, ctx);
        } else if (obj instanceof SleepState) {
            SleepState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, (SleepState) obj, ctx);
        } else if (obj instanceof SwitchState) {
            SwitchState_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, (SwitchState) obj, ctx);
        } else {
            State_YamlMapperImpl.INSTANCE.getSerializer().serialize(writer, obj, ctx);
        }
    }
}
