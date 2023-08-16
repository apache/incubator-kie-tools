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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.CorrelationKey;
import org.eclipse.bpmn2.CorrelationProperty;
import org.eclipse.bpmn2.CorrelationPropertyBinding;
import org.eclipse.bpmn2.CorrelationPropertyRetrievalExpression;
import org.eclipse.bpmn2.CorrelationSubscription;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;
import org.kie.workbench.common.stunner.core.util.UUID;

public class CollaborationPropertyWriterUtils {

    private final static String DEFAULT_COLLABORATION = "Default Collaboration";
    private final static String PROPERTY_ITEM_DEFINITION = "_Type";

    public static ItemDefinition createPropertyItemDefinition(Correlation correlation) {
        ItemDefinition itemDefinition = Bpmn2Factory.eINSTANCE.createItemDefinition();
        itemDefinition.setId(correlation.getPropertyId() + PROPERTY_ITEM_DEFINITION);
        itemDefinition.setStructureRef(correlation.getPropertyType());
        return itemDefinition;
    }

    public static CorrelationProperty createCorrelationProperty(Correlation correlation,
                                                                ItemDefinition itemDefinition) {
        CorrelationProperty correlationProperty = Bpmn2Factory.eINSTANCE.createCorrelationProperty();
        correlationProperty.setId(correlation.getPropertyId());
        correlationProperty.setName(correlation.getPropertyName());
        correlationProperty.setType(itemDefinition);
        return correlationProperty;
    }

    public static CorrelationPropertyRetrievalExpression createCorrelationPropertyRetrievalExpression() {
        CorrelationPropertyRetrievalExpression correlationPropertyRetrievalExpression =
                Bpmn2Factory.eINSTANCE.createCorrelationPropertyRetrievalExpression();
        correlationPropertyRetrievalExpression.setId(UUID.uuid());
        return correlationPropertyRetrievalExpression;
    }

    public static void addCorrelationPropertyRetrievalExpression(
            CorrelationProperty correlationProperty,
            CorrelationPropertyRetrievalExpression correlationPropertyRetrievalExpression) {
        correlationProperty.getCorrelationPropertyRetrievalExpression().add(correlationPropertyRetrievalExpression);
    }

    public static Collaboration createCollaboration(Participant participant) {
        Collaboration collaboration = Bpmn2Factory.eINSTANCE.createCollaboration();
        collaboration.setId(UUID.uuid());
        collaboration.setName(DEFAULT_COLLABORATION);
        collaboration.getParticipants().add(participant);

        return collaboration;
    }

    public static Participant createParticipant(Process process) {
        Participant participant = Bpmn2Factory.eINSTANCE.createParticipant();
        participant.setId(UUID.uuid());
        participant.setName("Pool Participant");
        participant.setProcessRef(process);
        return participant;
    }

    public static CorrelationKey createCorrelationKey(Correlation correlation,
                                                      Collaboration collaboration) {
        CorrelationKey correlationKey = Bpmn2Factory.eINSTANCE.createCorrelationKey();
        correlationKey.setId(correlation.getId());
        correlationKey.setName(correlation.getName());
        collaboration.getCorrelationKeys().add(correlationKey);
        return correlationKey;
    }

    public static void addCorrelationProperty(CorrelationKey correlationKey,
                                              CorrelationProperty correlationProperty) {
        correlationKey.getCorrelationPropertyRef().add(correlationProperty);
    }

    public static CorrelationSubscription createCorrelationSubscription(CorrelationKey correlationKey) {
        CorrelationSubscription correlationSubscription = Bpmn2Factory.eINSTANCE.createCorrelationSubscription();
        correlationSubscription.setId(UUID.uuid());
        correlationSubscription.setCorrelationKeyRef(correlationKey);
        return correlationSubscription;
    }

    public static CorrelationPropertyBinding createCorrelationPropertyBinding(CorrelationProperty correlationProperty) {
        CorrelationPropertyBinding correlationPropertyBinding = Bpmn2Factory.eINSTANCE.createCorrelationPropertyBinding();
        correlationPropertyBinding.setId(UUID.uuid());
        correlationPropertyBinding.setCorrelationPropertyRef(correlationProperty);
        return correlationPropertyBinding;
    }

    public static void addCorrelationPropertyBinding(CorrelationSubscription correlationSubscription,
                                                     CorrelationPropertyBinding correlationPropertyBinding) {
        correlationSubscription.getCorrelationPropertyBinding().add(correlationPropertyBinding);
    }
}
