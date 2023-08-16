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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.CorrelationKey;
import org.eclipse.bpmn2.CorrelationProperty;
import org.eclipse.bpmn2.CorrelationPropertyBinding;
import org.eclipse.bpmn2.CorrelationPropertyRetrievalExpression;
import org.eclipse.bpmn2.CorrelationSubscription;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CorrelationWriterData;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CollaborationPropertyWriterUtils.addCorrelationProperty;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CollaborationPropertyWriterUtils.addCorrelationPropertyBinding;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CollaborationPropertyWriterUtils.addCorrelationPropertyRetrievalExpression;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CollaborationPropertyWriterUtils.createCollaboration;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CollaborationPropertyWriterUtils.createCorrelationKey;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CollaborationPropertyWriterUtils.createCorrelationProperty;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CollaborationPropertyWriterUtils.createCorrelationPropertyBinding;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CollaborationPropertyWriterUtils.createCorrelationPropertyRetrievalExpression;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CollaborationPropertyWriterUtils.createCorrelationSubscription;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CollaborationPropertyWriterUtils.createParticipant;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.CollaborationPropertyWriterUtils.createPropertyItemDefinition;

public class CollaborationPropertyWriter {

    private final Definitions definitions;
    private final Process process;

    private Collaboration collaboration;
    private final Set<ItemDefinition> itemDefinitions;
    private final Set<CorrelationProperty> correlationProperties;
    private final Set<CorrelationKey> correlationKeys;
    private final Set<CorrelationSubscription> correlationSubscriptions;
    private final List<CorrelationWriterData> correlationWriterDataList;

    public CollaborationPropertyWriter(final Definitions definitions,
                                       final Process process,
                                       final List<CorrelationWriterData> correlationWriterDataList) {
        this.definitions = definitions;
        this.process = process;

        this.itemDefinitions = new HashSet<>();
        this.correlationProperties = new HashSet<>();
        this.correlationKeys = new HashSet<>();
        this.correlationSubscriptions = new HashSet<>();

        this.correlationWriterDataList = correlationWriterDataList;
    }

    public void setCorrelations(final List<Correlation> correlations) {
        Participant participant = createParticipant(process);
        collaboration = createCollaboration(participant);

        correlations.stream()
                .forEach(correlation -> createCollaborationData(correlation));

        definitions.getRootElements().addAll(itemDefinitions);
        definitions.getRootElements().addAll(correlationProperties);
        definitions.getRootElements().add(collaboration);
        process.getCorrelationSubscriptions().addAll(correlationSubscriptions);
    }

    protected void createCollaborationData(final Correlation correlation) {
        ItemDefinition itemDefinition = createPropertyItemDefinition(correlation);
        itemDefinitions.add(itemDefinition);

        CorrelationProperty correlationProperty = createCorrelationProperty(correlation, itemDefinition);
        correlationProperties.add(correlationProperty);
        CorrelationPropertyRetrievalExpression correlationPropertyRetrievalExpression =
                createCorrelationPropertyRetrievalExpression();
        addCorrelationPropertyRetrievalExpression(correlationProperty,
                                                  correlationPropertyRetrievalExpression);

        CorrelationKey correlationKey = fetchCorrelationKey(correlation, collaboration);
        addCorrelationProperty(correlationKey, correlationProperty);

        CorrelationSubscription correlationSubscription = fetchCorrelationSubscription(correlationKey);

        CorrelationPropertyBinding correlationPropertyBinding = createCorrelationPropertyBinding(correlationProperty);
        addCorrelationPropertyBinding(correlationSubscription, correlationPropertyBinding);

        correlationWriterDataList.stream()
                .filter(correlationData -> correlationData.getCorrelationPropertyID() == correlationProperty.getId())
                .forEach(correlationData -> {
                    correlationPropertyRetrievalExpression.setMessageRef(correlationData.getMessage());
                    correlationPropertyRetrievalExpression.setMessagePath(correlationData.getMessagePath());
                    correlationPropertyBinding.setDataPath(correlationData.getDataPath());
                    correlationWriterDataList.remove(correlationData);
                });
    }

    private CorrelationKey fetchCorrelationKey(final Correlation correlation, final Collaboration collaboration) {
        Optional<CorrelationKey> correlationKey = correlationKeys.stream()
                .filter(ck -> Objects.equals(ck.getId(), correlation.getId()))
                .findFirst();

        return correlationKey.orElseGet(() -> {
            CorrelationKey newCorrelationKey = createCorrelationKey(correlation, collaboration);
            correlationKeys.add(newCorrelationKey);
            return newCorrelationKey;
        });
    }

    private CorrelationSubscription fetchCorrelationSubscription(final CorrelationKey correlationKey) {
        Optional<CorrelationSubscription> correlationSubscription = correlationSubscriptions.stream()
                .filter(cs -> Objects.equals(cs.getCorrelationKeyRef(), correlationKey))
                .findFirst();

        return correlationSubscription.orElseGet(() -> {
            CorrelationSubscription newCorrelationSubscription = createCorrelationSubscription(correlationKey);
            correlationSubscriptions.add(newCorrelationSubscription);
            return newCorrelationSubscription;
        });
    }
}
