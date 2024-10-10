package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.util;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.bpmn2.FormalExpression;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.events.CorrelationProperty;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.events.CorrelationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.events.DataExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.events.DataExpressionType;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.events.MessageExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.events.MessageExpressionType;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;

public class CorrelationPropertyReaderUtils {

    public static CorrelationSet createCorrelationSet(CorrelationReaderData correlationReaderData) {
        CorrelationSet correlationSet = new CorrelationSet();
        correlationSet.setCorrelationProperty(createCorrelationProperty(correlationReaderData.getCorrelationProperty()));
        correlationSet.setMessageExpression(createMessageExpression(correlationReaderData.getMessagePath()));
        correlationSet.setMessageExpressionType(createMessageExpressionType(correlationReaderData.getMessagePath()));
        correlationSet.setDataExpression(createDataExpression(correlationReaderData.getDataPath()));
        correlationSet.setDataExpressionType(createDataExpressionType(correlationReaderData.getDataPath()));
        return correlationSet;
    }

    public static Optional<CorrelationReaderData> findCorrelationReaderData(
            final MessageRef messageRef,
            final List<CorrelationReaderData> correlationReaderDataList) {
        return correlationReaderDataList.stream()
                .filter(correlationReaderData -> correlationReaderData.message != null)
                .filter(correlationReaderData -> Objects.equals(correlationReaderData.message.getName(),
                                                                messageRef.getValue()))

                .findAny();
    }

    private static CorrelationProperty createCorrelationProperty(org.eclipse.bpmn2.CorrelationProperty cp) {
        return new CorrelationProperty(cp.getId());
    }

    private static MessageExpression createMessageExpression(FormalExpression messagePath) {
        ScriptTypeValue scriptTypeValue = new ScriptTypeValue();
        scriptTypeValue.setScript(FormalExpressionBodyHandler.of(messagePath).getBody());
        scriptTypeValue.setLanguage(messagePath.getLanguage());
        return new MessageExpression(scriptTypeValue);
    }

    private static MessageExpressionType createMessageExpressionType(FormalExpression messagePath) {
        return new MessageExpressionType(messagePath.getEvaluatesToTypeRef().getStructureRef());
    }

    private static DataExpression createDataExpression(FormalExpression dataPath) {
        ScriptTypeValue scriptTypeValue = new ScriptTypeValue();
        scriptTypeValue.setScript(FormalExpressionBodyHandler.of(dataPath).getBody());
        scriptTypeValue.setLanguage(dataPath.getLanguage());
        return new DataExpression(scriptTypeValue);
    }

    private static DataExpressionType createDataExpressionType(FormalExpression dataPath) {
        return new DataExpressionType(dataPath.getEvaluatesToTypeRef().getStructureRef());
    }
}
