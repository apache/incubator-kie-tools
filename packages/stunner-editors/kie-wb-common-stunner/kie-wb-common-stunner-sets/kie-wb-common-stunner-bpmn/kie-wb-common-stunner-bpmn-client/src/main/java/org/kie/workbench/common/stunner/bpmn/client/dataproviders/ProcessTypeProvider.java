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

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.uberfire.commons.Pair;

public class ProcessTypeProvider extends AbstractProcessFilteredNodeProvider {

    private final static String[] TYPES = new String[]{"Public", "Private"};

    @Inject
    public ProcessTypeProvider(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    public Predicate<Node> getFilter() {
        //not used in this implementation.
        return node -> true;
    }

    @Override
    public Function<Node, Pair<Object, String>> getMapper() {
        //not used in this implementation.
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SelectorData getSelectorData(final FormRenderingContext context) {
        return new SelectorData(Arrays.asList(TYPES)
                                        .stream()
                                        .collect(Collectors.toMap(p -> p, p -> p)),
                                "Public");
    }
}
