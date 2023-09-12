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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.List;

import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Message;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CorrelationWriterData;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CorrelationPropertyWriterUtils.createFormalExpression;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CorrelationPropertyWriterUtils.createItemDefinition;

public class CorrelationPropertyWriter {

    private final BasePropertyWriter basePropertyWriter;
    protected final List<CorrelationWriterData> correlationWriterDataList;

    public CorrelationPropertyWriter(final BasePropertyWriter basePropertyWriter,
                                     final List<CorrelationWriterData> correlationWriterDataList) {
        this.basePropertyWriter = basePropertyWriter;
        this.correlationWriterDataList = correlationWriterDataList;
    }

    public void setCorrelationData(final String propertyID,
                                   final Message message,
                                   final String messageExpression,
                                   final String messageExpressionLanguage,
                                   final String messageExpressionType,
                                   final String dataExpression,
                                   final String dataExpressionLanguage,
                                   final String dataExpressionType) {
        FormalExpression messagePath = createMessageExpression(messageExpression,
                                                               messageExpressionLanguage,
                                                               messageExpressionType);
        FormalExpression dataPath = createDataExpression(dataExpression,
                                                         dataExpressionLanguage,
                                                         dataExpressionType);
        CorrelationWriterData correlationData = new CorrelationWriterData(propertyID,
                                                                          message,
                                                                          messagePath,
                                                                          dataPath);
        correlationWriterDataList.add(correlationData);
    }

    protected FormalExpression createMessageExpression(final String messageExpression,
                                                       final String messageExpressionLanguage,
                                                       final String messageExpressionType) {
        ItemDefinition itemDefinition = createItemDefinition(messageExpressionType);
        basePropertyWriter.addItemDefinition(itemDefinition);

        FormalExpression formalExpression = createFormalExpression(itemDefinition,
                                                                   messageExpressionLanguage,
                                                                   messageExpression);
        return formalExpression;
    }

    protected FormalExpression createDataExpression(final String dataExpression,
                                                    final String dataExpressionLanguage,
                                                    final String dataExpressionType) {
        ItemDefinition itemDefinition = createItemDefinition(dataExpressionType);
        basePropertyWriter.addItemDefinition(itemDefinition);

        FormalExpression formalExpression = createFormalExpression(itemDefinition,
                                                                   dataExpressionLanguage,
                                                                   dataExpression);
        return formalExpression;
    }
}
