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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PMMLModelMetadataTest {

    private static final String MODEL_NAME = "modelName";

    private static final Set<PMMLParameterMetadata> PARAMETERS = Stream.of(mock(PMMLParameterMetadata.class))
            .collect(Collectors.toSet());

    @Test
    public void testGetters() {
        final PMMLModelMetadata metadata = new PMMLModelMetadata(MODEL_NAME,
                                                                 PARAMETERS);

        assertEquals(MODEL_NAME, metadata.getName());
        assertEquals(PARAMETERS, metadata.getInputParameters());
    }
}
