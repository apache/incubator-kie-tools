/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.backend.service;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class XMLEncoderDiagramMetadataMarshallerTest {

    private static final String TEST1 = "org/kie/workbench/common/stunner/core/backend/test1.meta";

    private XMLEncoderDiagramMetadataMarshaller tested;

    @Before
    public void setup() {
        tested = new XMLEncoderDiagramMetadataMarshaller();
    }

    @Test
    public void testEncode() throws Exception {
        MetadataImpl metadata = new MetadataImpl.MetadataImplBuilder("defSet1").build();
        metadata.setTitle("title1");
        metadata.setCanvasRootUUID("root1");
        metadata.setShapeSetId("ss1");
        metadata.setThumbData("thumbData1");
        metadata.setTitle("title1");
        String result = tested.marshall(metadata);
        assertNotNull(result);
    }

    @Test
    public void testDecodeTest1() throws Exception {
        InputStream is = loadStream(TEST1);
        Metadata metadata = tested.unmarshall(is);
        assertNotNull(metadata);
        assertEquals("defSet1",
                     metadata.getDefinitionSetId());
        assertEquals("ss1",
                     metadata.getShapeSetId());
        assertEquals("thumbData1",
                     metadata.getThumbData());
        assertEquals("title1",
                     metadata.getTitle());
        assertEquals("root1",
                     metadata.getCanvasRootUUID());
    }

    private InputStream loadStream(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }
}
