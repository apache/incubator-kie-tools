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
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CloneManagerImplTest {

    private CloneManagerImpl cloneManager;

    private Object def1;

    private Object def2;

    @Mock
    private DeepCloneProcess deepCloneProcess;

    @Mock
    private DefaultCloneProcess defaultCloneProcess;

    @Mock
    private NoneCloneProcess noneCloneProcess;

    @Before
    public void setUp() {
        cloneManager = new CloneManagerImpl(deepCloneProcess, defaultCloneProcess, noneCloneProcess);
    }

    @Test
    public void testClone() throws Exception {
        cloneManager.clone(def1, ClonePolicy.ALL);
        verify(deepCloneProcess, times(1)).clone(def1);

        cloneManager.clone(def1, ClonePolicy.DEFAULT);
        verify(defaultCloneProcess, times(1)).clone(def1);

        cloneManager.clone(def1, ClonePolicy.NONE);
        verify(noneCloneProcess, times(1)).clone(def1);
    }

    @Test(expected = NullPointerException.class)
    public void testCloneNullPolicy() {
        cloneManager.clone(def1, null);
    }

    @Test
    public void testCloneParam() throws Exception {
        cloneManager.clone(def1, def2, ClonePolicy.ALL);
        verify(deepCloneProcess, times(1)).clone(def1, def2);

        cloneManager.clone(def1, def2, ClonePolicy.DEFAULT);
        verify(defaultCloneProcess, times(1)).clone(def1, def2);

        cloneManager.clone(def1, def2, ClonePolicy.NONE);
        verify(noneCloneProcess, times(1)).clone(def1, def2);
    }
}