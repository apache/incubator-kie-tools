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


package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.diagram.Correlations;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.diagram.CorrelationsValue;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.SafeComparator;

public class CorrelationsProvider
        implements SelectorDataProvider {

    protected SessionManager sessionManager;

    protected ClientTranslationService translationService;

    @Inject
    public CorrelationsProvider(final SessionManager sessionManager,
                                final ClientTranslationService translationService) {
        this.sessionManager = sessionManager;
        this.translationService = translationService;
    }

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SelectorData getSelectorData(final FormRenderingContext context) {
        Map<Object, String> values = new TreeMap<>(SafeComparator.TO_STRING_COMPARATOR);
        values.put("", "");

        String elementUUID = sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getMetadata().getCanvasRootUUID();
        if (elementUUID != null) {
            Node node = sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getGraph().getNode(elementUUID);
            BPMNDiagram bpmnDiagram = (BPMNDiagram) ((View) node.getContent()).getDefinition();
            if (bpmnDiagram != null) {
                Correlations correlations = bpmnDiagram.getCollaborationSet().getCorrelations();
                if (correlations != null) {
                    CorrelationsValue correlationsValue = correlations.getValue();
                    correlationsValue.getCorrelations()
                            .forEach(correlation -> {
                                if (!correlation.getPropertyId().isEmpty()) {
                                    values.put(correlation.getPropertyId(), createDisplayName(correlation));
                                }
                            });
                }
            }
        }

        SelectorData selectorData = new SelectorData<>();
        selectorData.setValues(values);
        selectorData.setSelectedValue("");
        return selectorData;
    }

    private String createDisplayName(Correlation correlation) {
        return correlation.getName() + " - " + correlation.getPropertyName();
    }
}