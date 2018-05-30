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

package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.lookup.domain.CommonDomainLookups;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ToolboxDomainLookupsTest {

    @Mock
    private CommonDomainLookups domainLookups;
    private ManagedInstance<CommonDomainLookups> domainLookupsManagedInstances;

    private ToolboxDomainLookups tested;

    @Before
    public void setUp() {
        domainLookupsManagedInstances = spy(new ManagedInstanceStub<>(domainLookups));
        tested = new ToolboxDomainLookups(domainLookupsManagedInstances);
    }

    @Test
    public void testGetCached() {
        String dsId = "ds1";
        CommonDomainLookups lookups = tested.get(dsId);
        CommonDomainLookups lookups1 = tested.get(dsId);
        assertEquals(domainLookups, lookups);
        verify(domainLookups, times(1)).setDomain(eq(dsId));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(domainLookupsManagedInstances, times(1)).destroyAll();
    }
}
