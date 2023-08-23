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

package org.kie.workbench.common.dmn.api.editors.included;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DMNIncludedModelTest {

    private static final String MODEL_NAME = "modelName";

    private static final String MODEL_PACKAGE = "modelPackage";

    private static final String PATH = "path";

    private static final String NAMESPACE = "namespace";

    private static final String IMPORT_TYPE = "importType";

    private static final Integer ELEMENTS_COUNT = 1;

    private static final Integer DEFINITIONS_COUNT = 2;

    @Test
    public void testGetters() {
        final DMNIncludedModel model = new DMNIncludedModel(MODEL_NAME,
                                                            MODEL_PACKAGE,
                                                            PATH,
                                                            NAMESPACE,
                                                            IMPORT_TYPE,
                                                            ELEMENTS_COUNT,
                                                            DEFINITIONS_COUNT);

        assertEquals(MODEL_NAME, model.getModelName());
        assertEquals(MODEL_PACKAGE, model.getModelPackage());
        assertEquals(PATH, model.getPath());
        assertEquals(NAMESPACE, model.getNamespace());
        assertEquals(IMPORT_TYPE, model.getImportType());
        assertEquals(ELEMENTS_COUNT, model.getDrgElementsCount());
        assertEquals(DEFINITIONS_COUNT, model.getItemDefinitionsCount());
    }
}
