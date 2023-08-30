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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.util;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.CorrelationKey;
import org.eclipse.bpmn2.CorrelationProperty;
import org.eclipse.bpmn2.CorrelationPropertyBinding;
import org.eclipse.bpmn2.CorrelationSubscription;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;

public class CollaborationPropertyReaderUtils {

    public static Correlation createCorrelation(CorrelationKey correlationKey,
                                                CorrelationPropertyBinding correlationPropertyBinding) {
        CorrelationProperty correlationProperty = correlationPropertyBinding.getCorrelationPropertyRef();
        Correlation correlation = new Correlation(correlationKey.getId(),
                                                  correlationKey.getName(),
                                                  correlationProperty.getId(),
                                                  correlationProperty.getName(),
                                                  correlationProperty.getType().getStructureRef());
        return correlation;
    }

    public static List<CorrelationReaderData> createCorrelationReaderData(
            CorrelationPropertyBinding correlationPropertyBinding) {
        CorrelationProperty correlationProperty = correlationPropertyBinding.getCorrelationPropertyRef();

        return correlationProperty.getCorrelationPropertyRetrievalExpression().stream()
                .map(correlationPropertyRetrievalExpression -> new CorrelationReaderData(
                        correlationProperty,
                        correlationPropertyRetrievalExpression.getMessageRef(),
                        correlationPropertyRetrievalExpression.getMessagePath(),
                        correlationPropertyBinding.getDataPath()
                ))
                .collect(Collectors.toList());
    }

    public static Correlation processCorrelationPropertyBinding(
            final CorrelationSubscription correlationSubscription,
            final CorrelationPropertyBinding correlationPropertyBinding,
            final List<CorrelationReaderData> correlationReaderDataList) {
        correlationReaderDataList.addAll(createCorrelationReaderData(correlationPropertyBinding));
        return createCorrelation(correlationSubscription.getCorrelationKeyRef(), correlationPropertyBinding);
    }
}
