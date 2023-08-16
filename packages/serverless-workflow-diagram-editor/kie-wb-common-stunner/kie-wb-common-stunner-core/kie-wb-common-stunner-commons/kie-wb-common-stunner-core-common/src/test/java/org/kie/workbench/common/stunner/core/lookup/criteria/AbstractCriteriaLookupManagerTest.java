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

package org.kie.workbench.common.stunner.core.lookup.criteria;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("unchecked")
public class AbstractCriteriaLookupManagerTest {

    private AbstractCriteriaLookupManager manager;

    @Before
    public void setup() {
        manager = new AbstractCriteriaLookupManager() {
            @Override
            protected boolean matches(final String key,
                                      final String value,
                                      final Object item) {
                return false;
            }

            @Override
            protected List getItems(final LookupRequest request) {
                return Collections.emptyList();
            }

            @Override
            protected Object buildResult(final Object item) {
                return null;
            }
        };
    }

    @Test
    public void checkToSet_NullSet() {
        assertEquals(Collections.emptySet(),
                     manager.toCollection(null));
    }

    @Test
    public void checkToSet_EmptySet() {
        assertEquals(Collections.emptySet(),
                     manager.toCollection(""));
    }

    @Test
    public void checkToSet_NoCollectionDelimiters() {
        assertEquals(Collections.emptySet(),
                     manager.toCollection("label"));
    }

    @Test
    public void checkToSet_NoCollectionStartDelimiter() {
        assertEquals(Collections.emptySet(),
                     manager.toCollection("label]"));
    }

    @Test
    public void checkToSet_NoCollectionEndDelimiter() {
        assertEquals(Collections.emptySet(),
                     manager.toCollection("[label"));
    }

    @Test
    public void checkToSet_SingleValue() {
        final Collection<String> set = manager.toCollection("[label1]");
        assertEquals(1,
                     set.size());
        assertTrue(set.stream().anyMatch(s -> s.equals("label1")));
    }

    @Test
    public void checkToSet_MultipleValues() {
        final Collection<String> set = manager.toCollection("[label1,label2]");
        assertEquals(2,
                     set.size());
        assertTrue(set.stream().anyMatch(s -> s.equals("label1")));
        assertTrue(set.stream().anyMatch(s -> s.equals("label2")));
    }
}
