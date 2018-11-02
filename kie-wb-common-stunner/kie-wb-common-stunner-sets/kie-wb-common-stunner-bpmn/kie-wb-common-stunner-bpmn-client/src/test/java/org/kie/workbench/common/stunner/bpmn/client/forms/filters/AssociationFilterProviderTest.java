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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AssociationFilterProviderTest {

    private AssociationFilterProvider filterProvider;

    @Before
    public void setUp() {
        filterProvider = new AssociationFilterProvider();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProvideFilters() {
        String arbitraryElementUUID = "arbitraryElementUUID";
        Object arbitraryObject = mock(Object.class);
        Collection<FormElementFilter> result = filterProvider.provideFilters(arbitraryElementUUID, arbitraryObject);
        assertEquals(1, result.size());
        FormElementFilter filter = result.iterator().next();
        assertEquals("general.name", filter.getElementName());
        Object arbitraryValue = mock(Object.class);
        assertFalse(filter.getPredicate().test(arbitraryValue));
    }
}
