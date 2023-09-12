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


package org.kie.workbench.common.stunner.core.diagram;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.util.EqualsAndHashCodeTestUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MetadataImplTest {

    private static final String TITLE1 = "TITLE1";

    private static final String TITLE2 = "TITLE2";

    private static final String DEF_SET1 = "DEF_SET1";

    private static final String DEF_SET2 = "DEF_SET2";

    private static final String SHAPE_SET1 = "SHAPE_SET1";

    private static final String SHAPE_SET2 = "SHAPE_SET2";

    @Mock
    private DefinitionManager definitionManager1;

    @Mock
    private TypeDefinitionSetRegistry definitionSetRegistry1;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionSetAdapter definitionSetAdapter;

    @Mock
    private DefinitionSet definitionSet1;

    @Mock
    private Path path1;

    @Mock
    private Path path2;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(definitionManager1.definitionSets()).thenReturn(definitionSetRegistry1);
        when(definitionSetRegistry1.getDefinitionSetById(DEF_SET1)).thenReturn(definitionSet1);
        when(definitionManager1.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(definitionSetAdapter.getDescription(definitionSet1)).thenReturn(TITLE1);
    }

    @Test
    public void testMetadataBuilder1() {
        MetadataImpl metadata = new MetadataImpl.MetadataImplBuilder(DEF_SET1)
                .setPath(path1)
                .setTitle(TITLE1)
                .setShapeSetId(SHAPE_SET1)
                .build();

        assertEquals(DEF_SET1, metadata.getDefinitionSetId());
        assertEquals(TITLE1, metadata.getTitle());
        assertEquals(SHAPE_SET1, metadata.getShapeSetId());
        assertEquals(path1, metadata.getPath());
    }

    @Test
    public void testMetadataBuilder2() {
        MetadataImpl metadata = new MetadataImpl.MetadataImplBuilder(DEF_SET1, definitionManager1)
                .setPath(path1)
                .setShapeSetId(SHAPE_SET1)
                .build();

        assertEquals(DEF_SET1, metadata.getDefinitionSetId());
        assertEquals(TITLE1, metadata.getTitle());
        assertEquals(path1, metadata.getPath());
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsAndHashCodeTestUtils.TestCaseBuilder.newTestCase()
                .addTrueCase(new MetadataImpl.MetadataImplBuilder(DEF_SET1)
                                     .setPath(path1)
                                     .setTitle(TITLE1)
                                     .setShapeSetId(SHAPE_SET1)
                                     .build(),
                             new MetadataImpl.MetadataImplBuilder(DEF_SET1)
                                     .setPath(path1)
                                     .setTitle(TITLE1)
                                     .setShapeSetId(SHAPE_SET1)
                                     .build())
                .addFalseCase(new MetadataImpl.MetadataImplBuilder(DEF_SET1)
                                      .setPath(path1)
                                      .setTitle(TITLE1)
                                      .setShapeSetId(SHAPE_SET1)
                                      .build(),
                              new MetadataImpl.MetadataImplBuilder(DEF_SET2)
                                      .setPath(path1)
                                      .setTitle(TITLE1)
                                      .setShapeSetId(SHAPE_SET1)
                                      .build())

                .addFalseCase(new MetadataImpl.MetadataImplBuilder(DEF_SET1)
                                      .setPath(path1)
                                      .setTitle(TITLE1)
                                      .setShapeSetId(SHAPE_SET1)
                                      .build(),
                              new MetadataImpl.MetadataImplBuilder(DEF_SET1)
                                      .setPath(path2)
                                      .setTitle(TITLE1)
                                      .setShapeSetId(SHAPE_SET1)
                                      .build())
                .addFalseCase(new MetadataImpl.MetadataImplBuilder(DEF_SET1)
                                      .setPath(path1)
                                      .setTitle(TITLE1)
                                      .setShapeSetId(SHAPE_SET1)
                                      .build(),
                              new MetadataImpl.MetadataImplBuilder(DEF_SET1)
                                      .setPath(path1)
                                      .setTitle(TITLE2)
                                      .setShapeSetId(SHAPE_SET1)
                                      .build())
                .addFalseCase(new MetadataImpl.MetadataImplBuilder(DEF_SET1)
                                      .setPath(path1)
                                      .setTitle(TITLE1)
                                      .setShapeSetId(SHAPE_SET1)
                                      .build(),
                              new MetadataImpl.MetadataImplBuilder(DEF_SET1)
                                      .setPath(path1)
                                      .setTitle(TITLE1)
                                      .setShapeSetId(SHAPE_SET2)
                                      .build())
                .test();
    }
}
