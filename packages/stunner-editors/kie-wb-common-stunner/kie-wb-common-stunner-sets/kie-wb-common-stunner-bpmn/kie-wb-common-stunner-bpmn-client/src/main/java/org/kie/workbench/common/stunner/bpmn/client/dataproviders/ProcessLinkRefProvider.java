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

import java.util.function.Function;
import java.util.function.Predicate;

import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.link.LinkRef;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.commons.Pair;

public class ProcessLinkRefProvider
        extends AbstractProcessFilteredNodeProvider {

    private static final Predicate<Node> intermediateCatchingLinkEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof IntermediateLinkEventCatching;
    private static final Predicate<Node> intermediateThrowingLinkEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof IntermediateLinkEventThrowing;
    private static final Predicate<Node> allLinkEventsFilter = intermediateCatchingLinkEventsFilter.or(intermediateThrowingLinkEventsFilter);

    @Inject
    public ProcessLinkRefProvider(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public Predicate<Node> getFilter() {
        return allLinkEventsFilter;
    }

    @Override
    public Function<Node, Pair<Object, String>> getMapper() {
        return node -> {
            LinkRef linkRef = null;
            if (intermediateCatchingLinkEventsFilter.test(node)) {
                linkRef = ((IntermediateLinkEventCatching) ((View) node.getContent()).getDefinition()).getExecutionSet().getLinkRef();
            } else if (intermediateThrowingLinkEventsFilter.test(node)) {
                linkRef = ((IntermediateLinkEventThrowing) ((View) node.getContent()).getDefinition()).getExecutionSet().getLinkRef();
            }

            if (linkRef != null && linkRef.getValue() != null && !linkRef.getValue().isEmpty()) {
                return new Pair<>(linkRef.getValue(),
                                  linkRef.getValue(),
                                  Pair.PairEqualsMode.K1);
            } else {
                return null;
            }
        };
    }
}