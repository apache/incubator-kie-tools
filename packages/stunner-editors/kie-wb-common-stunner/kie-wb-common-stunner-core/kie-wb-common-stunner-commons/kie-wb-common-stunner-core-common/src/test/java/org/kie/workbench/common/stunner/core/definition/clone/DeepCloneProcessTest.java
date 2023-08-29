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


package org.kie.workbench.common.stunner.core.definition.clone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.util.ClassUtils;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DeepCloneProcessTest extends AbstractCloneProcessTest {

    private DeepCloneProcess deepCloneProcess;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        deepCloneProcess = new DeepCloneProcess(factoryManager, adapterManager, new ClassUtils());
    }

    @Test
    public void testClone() throws Exception {
        Object clone = deepCloneProcess.clone(def1);
        testPropertySet(clone, def1, nameProperty2, nameValue);
        testPropertySet(clone, def1, textProperty2, textValue);
        testPropertySet(clone, def1, booleanProperty2, booleanValue);
    }
}