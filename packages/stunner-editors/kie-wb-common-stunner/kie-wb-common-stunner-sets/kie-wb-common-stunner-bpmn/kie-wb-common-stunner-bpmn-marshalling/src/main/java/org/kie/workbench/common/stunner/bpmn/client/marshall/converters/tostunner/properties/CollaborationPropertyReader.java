/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.util.CorrelationReaderData;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.util.CollaborationPropertyReaderUtils.processCorrelationPropertyBinding;

public class CollaborationPropertyReader {

    protected final Definitions definitions;
    protected final Process process;
    protected final List<CorrelationReaderData> correlationReaderDataList;

    public CollaborationPropertyReader(final Definitions definitions,
                                       final Process process,
                                       final List<CorrelationReaderData> correlationReaderDataList) {
        this.definitions = definitions;
        this.process = process;
        this.correlationReaderDataList = correlationReaderDataList;
    }

    public List<Correlation> getCorrelations() {
        return process.getCorrelationSubscriptions().stream()
                .flatMap(correlationSubscription -> correlationSubscription.getCorrelationPropertyBinding().stream()
                        .map(correlationPropertyBinding -> processCorrelationPropertyBinding(correlationSubscription,
                                                                                             correlationPropertyBinding,
                                                                                             correlationReaderDataList))
                )
                .collect(Collectors.toList());
    }
}
