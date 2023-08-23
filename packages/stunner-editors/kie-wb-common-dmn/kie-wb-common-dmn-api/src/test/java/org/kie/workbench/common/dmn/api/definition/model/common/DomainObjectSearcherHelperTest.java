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

package org.kie.workbench.common.dmn.api.definition.model.common;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.HasDomainObject;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DomainObjectSearcherHelperTest {

    private static final String UUID = "the uuid";

    @Test
    public void testFind() {

        final DomainObject domainObject = mock(DomainObject.class);
        final HasDomainObject hasDo1 = mock(HasDomainObject.class);
        final HasDomainObject hasDo2 = mock(HasDomainObject.class);
        final HasDomainObject hasDo3 = mock(HasDomainObject.class);
        final HasDomainObject hasDo4 = mock(HasDomainObject.class);
        when(hasDo4.findDomainObject(UUID)).thenReturn(Optional.of(domainObject));

        final List<HasDomainObject> list = Arrays.asList(hasDo1,
                                                         hasDo2,
                                                         hasDo3,
                                                         hasDo4);
        final Optional<DomainObject> result = DomainObjectSearcherHelper.find(list, UUID);

        assertTrue(result.isPresent());
        assertEquals(domainObject, result.get());

        verify(hasDo1).findDomainObject(UUID);
        verify(hasDo2).findDomainObject(UUID);
        verify(hasDo3).findDomainObject(UUID);
        verify(hasDo4).findDomainObject(UUID);
    }

    @Test
    public void testGetDomainObject() {

        final String uuid1 = "uuid1";
        final String uuid2 = "uuid2";
        final String uuid3 = "uuid3";
        final DomainObject domainObject1 = createDomainObject(uuid1);
        final DomainObject domainObject2 = createDomainObject(uuid2);
        final DomainObject domainObject3 = createDomainObject(uuid3);

        final List<DomainObject> list = Arrays.asList(domainObject1,
                                                      domainObject2,
                                                      domainObject3);

        assertEquals(domainObject3, DomainObjectSearcherHelper.getDomainObject(list, uuid3).get());
        assertEquals(domainObject2, DomainObjectSearcherHelper.getDomainObject(list, uuid2).get());
        assertEquals(domainObject1, DomainObjectSearcherHelper.getDomainObject(list, uuid1).get());
    }

    @Test
    public void testMatches() {

        final DomainObject domainObject1 = createDomainObject(UUID);

        assertTrue(DomainObjectSearcherHelper.matches(domainObject1, UUID));
        assertFalse(DomainObjectSearcherHelper.matches(domainObject1, "another"));
    }

    @Test
    public void testMatches_WhenItIsNull() {

        assertFalse(DomainObjectSearcherHelper.matches(null, UUID));
    }

    private DomainObject createDomainObject(final String uuid) {

        final DomainObject domainObject = mock(DomainObject.class);
        when(domainObject.getDomainObjectUUID()).thenReturn(uuid);
        return domainObject;
    }
}
