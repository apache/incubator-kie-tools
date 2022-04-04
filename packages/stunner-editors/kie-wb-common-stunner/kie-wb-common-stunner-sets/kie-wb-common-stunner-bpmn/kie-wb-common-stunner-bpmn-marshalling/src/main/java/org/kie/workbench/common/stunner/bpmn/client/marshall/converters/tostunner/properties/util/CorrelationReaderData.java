package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.util;

import org.eclipse.bpmn2.CorrelationProperty;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Message;

public class CorrelationReaderData {

    protected final CorrelationProperty correlationProperty;
    protected final Message message;
    protected final FormalExpression messagePath;
    protected final FormalExpression dataPath;

    public CorrelationReaderData(CorrelationProperty correlationProperty,
                                 Message message,
                                 FormalExpression messagePath,
                                 FormalExpression dataPath) {
        this.correlationProperty = correlationProperty;
        this.message = message;
        this.messagePath = messagePath;
        this.dataPath = dataPath;
    }

    public CorrelationProperty getCorrelationProperty() {
        return correlationProperty;
    }

    public Message getMessage() {
        return message;
    }

    public FormalExpression getMessagePath() {
        return messagePath;
    }

    public FormalExpression getDataPath() {
        return dataPath;
    }
}
