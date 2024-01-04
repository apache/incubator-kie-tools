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


package org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationMessages;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

/**
 * Build element on the canvas, either a node or an edge.
 */
@Default
@Element
@Dependent
public class ElementBuilderControlImpl extends AbstractElementBuilderControl {

    @Inject
    public ElementBuilderControlImpl(final ClientDefinitionManager clientDefinitionManager,
                                     final ClientFactoryService clientFactoryServices,
                                     final RuleManager ruleManager,
                                     final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                     final ClientTranslationMessages translationMessages,
                                     final GraphBoundsIndexer graphBoundsIndexer) {
        super(clientDefinitionManager,
              clientFactoryServices,
              ruleManager,
              canvasCommandFactory,
              translationMessages,
              graphBoundsIndexer);
    }
}
