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


package org.kie.workbench.common.stunner.bpmn.documentation.model.general;

import java.util.ArrayList;

import org.junit.Test;
import org.kie.workbench.common.stunner.core.client.util.js.KeyValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ProcessOverviewTest {

    @Test
    public void create() {
        final Object processOverview = ProcessOverview.create(null, null, null);

        assertNotNull(processOverview);
        assertTrue(processOverview instanceof ProcessOverview);
    }

    @Test
    public void getGeneral() {
        final General general = new General.Builder().build();
        final ProcessOverview processOverview = ProcessOverview.create(general, null, null);

        assertNotNull(processOverview.getGeneral());
        assertEquals(general, processOverview.getGeneral());
    }

    @Test
    public void getImports() {
        final Imports imports = Imports.create(new ArrayList<>(), new ArrayList<>());
        final ProcessOverview processOverview = ProcessOverview.create(null, imports, null);

        assertNotNull(processOverview.getImports());
        assertEquals(imports, processOverview.getImports());
    }

    @Test
    public void getDataTotal() {
        final KeyValue[] variables = new KeyValue[0];
        final ProcessVariablesTotal dataTotal = ProcessVariablesTotal.create(0, 0, variables);
        final ProcessOverview processOverview = ProcessOverview.create(null, null, dataTotal);

        assertNotNull(processOverview.getDataTotal());
        assertEquals(dataTotal, processOverview.getDataTotal());
    }
}