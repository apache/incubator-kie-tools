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


package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.util.HashMap;
import java.util.Map;

import io.crysknife.client.ManagedInstance;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.lookup.domain.CommonDomainLookups;

@ApplicationScoped
public class ToolboxDomainLookups {

    private final ManagedInstance<CommonDomainLookups> domainLookupInstances;
    private final Map<String, CommonDomainLookups> domainLookups;

    // CDI proxy.
    protected ToolboxDomainLookups() {
        this(null);
    }

    @Inject
    public ToolboxDomainLookups(final ManagedInstance<CommonDomainLookups> domainLookupInstances) {
        this.domainLookupInstances = domainLookupInstances;
        this.domainLookups = new HashMap<>(4);
    }

    public CommonDomainLookups get(final String definitionSetId) {
        CommonDomainLookups lookup = domainLookups.get(definitionSetId);
        if (null == lookup) {
            lookup = domainLookupInstances.get();
            lookup.setDomain(definitionSetId);
            domainLookups.put(definitionSetId, lookup);
        }
        return lookup;
    }

    @PreDestroy
    public void destroy() {
        domainLookups.clear();
        domainLookupInstances.destroyAll();
    }
}
