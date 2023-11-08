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


package org.kie.workbench.common.stunner.core.rule.ext.impl;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionMultiHandler;

/**
 * A rule handler that checks if both source and target nodes for a given connector
 * have the same parent instance for a certain type.
 * <p>
 * This handler applies for both Graph connection and containment evaluation contexts.
 * - For a connection context - evaluates parent instance match for both source / target nodes
 * - For a containment context - evaluates parent instance match for both source / target nodes
 * for all candidate connectors and connectors on its children, if any.
 * <p>
 * The RuleExtension instance used by this handler needs the following arguments:
 * - RuleExtension#getTypeArguments()[0] - The parent type
 * - RuleExtension#getArguments()[0] - The rule violation's message
 * <p>
 * Example:
 * <code>
 * @RuleExtension( handler = ConnectorParentsMatchHandler.class,
 * typeArguments = {TheParentType.class}
 * arguments = {"My violation's message"}
 * )
 * public class MyConnectorBean {
 * }
 * </code>
 */
@ApplicationScoped
public class ConnectorParentsMatchHandler
        extends RuleExtensionHandler<ConnectorParentsMatchHandler, RuleEvaluationContext> {

    private final ConnectorParentsMatchConnectionHandler connectionHandler;
    private final ConnectorParentsMatchContainmentHandler containmentHandler;
    private final RuleExtensionMultiHandler multiHandler;

    protected ConnectorParentsMatchHandler() {
        this(null,
             null,
             null);
    }

    @Inject
    public ConnectorParentsMatchHandler(final ConnectorParentsMatchConnectionHandler connectionHandler,
                                        final ConnectorParentsMatchContainmentHandler containmentHandler,
                                        final RuleExtensionMultiHandler multiHandler) {
        this.connectionHandler = connectionHandler;
        this.containmentHandler = containmentHandler;
        this.multiHandler = multiHandler;
    }

    @PostConstruct
    public void init() {
        multiHandler.addHandler(connectionHandler);
        multiHandler.addHandler(containmentHandler);
    }

    @Override
    public Class<ConnectorParentsMatchHandler> getExtensionType() {
        return ConnectorParentsMatchHandler.class;
    }

    @Override
    public Class<RuleEvaluationContext> getContextType() {
        return RuleEvaluationContext.class;
    }

    @Override
    public boolean accepts(final RuleExtension rule,
                           final RuleEvaluationContext context) {
        return multiHandler.accepts(rule,
                                    context);
    }

    @Override
    public RuleViolations evaluate(final RuleExtension rule,
                                   final RuleEvaluationContext context) {
        return multiHandler.evaluate(rule,
                                     context);
    }
}
