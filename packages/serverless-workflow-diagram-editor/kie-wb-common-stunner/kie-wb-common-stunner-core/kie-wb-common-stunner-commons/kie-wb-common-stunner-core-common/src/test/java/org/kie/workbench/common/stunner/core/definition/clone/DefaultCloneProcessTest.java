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
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultCloneProcessTest extends AbstractCloneProcessTest {

    private DefaultCloneProcess defaultCloneProcess;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        defaultCloneProcess = new DefaultCloneProcess(factoryManager, adapterManager);
    }

    @Test(expected = NullPointerException.class)
    public void testCloneNull() throws Exception {
        defaultCloneProcess.clone(null);
    }

    @Test
    public void testClone() throws Exception {
        Object clone = defaultCloneProcess.clone(def1);
        testPropertySet(clone, def1, nameProperty2, nameValue);
    }

    @Test
    public void testCloneParam() throws Exception {
        Object clone = defaultCloneProcess.clone(def1, def3);
        testPropertySet(clone, def1, nameProperty3, nameValue);
    }
}