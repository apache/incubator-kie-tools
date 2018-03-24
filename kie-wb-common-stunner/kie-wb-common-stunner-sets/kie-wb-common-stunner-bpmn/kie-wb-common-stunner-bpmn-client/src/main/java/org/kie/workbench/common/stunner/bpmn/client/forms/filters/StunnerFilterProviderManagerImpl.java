/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.filters;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.forms.client.formFilters.StunnerFilterProviderManager;
import org.kie.workbench.common.stunner.forms.client.formFilters.StunnerFormElementFilterProvider;

@ApplicationScoped
public class StunnerFilterProviderManagerImpl implements StunnerFilterProviderManager {

    private Map<Class<?>, StunnerFormElementFilterProvider> filters = new HashMap<>();
    private StartEventFilterProviderFactory factory;

    protected StunnerFilterProviderManagerImpl() {
    }

    @Inject
    public StunnerFilterProviderManagerImpl(SessionManager sessionManager, GraphUtils graphUtils) {
        this.factory = new StartEventFilterProviderFactory(sessionManager, graphUtils);
    }

    @PostConstruct
    public void init() {
        factory.getFilterProviders().forEach(f -> filters.put(f.getDefinitionType(), f));
    }

    @Override
    public Collection<FormElementFilter> getFilterForDefinition(String elementUUID, Element<? extends Definition<?>> element, Object definition) {
        StunnerFormElementFilterProvider provider = filters.get(definition.getClass());

        if (provider != null) {
            return provider.provideFilters(elementUUID, element, definition);
        }

        return Collections.emptyList();
    }
}
