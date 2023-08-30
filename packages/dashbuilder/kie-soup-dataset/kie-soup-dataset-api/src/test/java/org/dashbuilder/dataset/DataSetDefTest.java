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

package org.dashbuilder.dataset;

import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataSetDefTest {

    ExternalDataSetDef externalDef = (ExternalDataSetDef) DataSetDefFactory.newExternalDataSetDef()
            .uuid("external")
            .name("external dataset")
            .url("http://datasets.com/dataset")
            .buildDef();
    
    ExternalDataSetDef externalDef2 = (ExternalDataSetDef) DataSetDefFactory.newExternalDataSetDef()
            .uuid("external")
            .name("external dataset")
            .url("http://datasets.com/dataset")
            .buildDef();

    @Test
    public void testExternalHashCode() throws Exception {
        assertEquals(externalDef.hashCode(), externalDef2.clone().hashCode());
        externalDef.setUrl("http://otherurl.com");
        assertNotEquals(externalDef.hashCode(), externalDef2.clone().hashCode());
    }
}
