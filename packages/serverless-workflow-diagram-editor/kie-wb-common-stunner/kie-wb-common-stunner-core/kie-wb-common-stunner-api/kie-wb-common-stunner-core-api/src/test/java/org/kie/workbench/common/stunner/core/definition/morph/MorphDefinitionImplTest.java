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


package org.kie.workbench.common.stunner.core.definition.morph;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.core.definition.clone.ClonePolicy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MorphDefinitionImplTest {

    private static final String DEFINITION_ID = "def1";
    private static final String BASE = "base1";
    private static final String DEF_DEFINITION_ID = "default1";
    private static final List<String> TARGETS = Arrays.asList("target1",
                                                              "target2");
    private static final ClonePolicy POLICY = ClonePolicy.ALL;

    private MorphDefinitionImpl tested;

    @Before
    public void setup() {
        this.tested = new MorphDefinitionImpl(DEFINITION_ID,
                                              BASE,
                                              DEF_DEFINITION_ID,
                                              TARGETS,
                                              POLICY);
    }

    @Test
    public void testAccepts() {
        assertTrue(tested.accepts(DEFINITION_ID));
        assertFalse(tested.accepts("def2"));
    }

    @Test
    public void testGetters() {
        assertEquals(BASE,
                     tested.getBase());
        assertEquals(DEF_DEFINITION_ID,
                     tested.getDefault());
        assertEquals(TARGETS,
                     tested.getTargets(DEFINITION_ID));
        assertFalse(tested.getTargets("def2").iterator().hasNext());
        assertEquals(POLICY,
                     tested.getPolicy());
    }
}
