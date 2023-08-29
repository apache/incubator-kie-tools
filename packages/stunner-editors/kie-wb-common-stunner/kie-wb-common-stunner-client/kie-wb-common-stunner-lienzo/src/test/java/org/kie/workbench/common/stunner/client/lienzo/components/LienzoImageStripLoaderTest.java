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


package org.kie.workbench.common.stunner.client.lienzo.components;

import java.lang.annotation.Annotation;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripRegistry;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyVararg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoImageStripLoaderTest {

    private static final ImageStrip[] STRIPS = new ImageStrip[0];

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ImageStripRegistry stripRegistry;

    @Mock
    private LienzoImageStrips lienzoImageStrips;

    @Mock
    private Metadata metadata;

    @Mock
    private Annotation qualifier;

    private LienzoImageStripLoader tested;

    @Before
    public void setUp() {
        doAnswer(invocation -> {
            ((Command) invocation.getArguments()[1]).execute();
            return null;
        }).when(lienzoImageStrips).register(any(ImageStrip[].class),
                                            any(Command.class));
        when(stripRegistry.get(any(Annotation.class))).thenReturn(STRIPS);
        when(stripRegistry.get((Annotation[]) anyVararg())).thenReturn(STRIPS);
        when(metadata.getDefinitionSetId()).thenReturn("mds1");
        when(definitionUtils.getQualifier(eq("mds1"))).thenReturn(qualifier);
        tested = new LienzoImageStripLoader(definitionUtils,
                                            stripRegistry,
                                            lienzoImageStrips);
    }

    @Test
    public void testInit() {
        Command callback = mock(Command.class);
        tested.init(metadata, callback);
        verify(stripRegistry, times(1)).get(eq(DefinitionManager.DEFAULT_QUALIFIER), eq(qualifier));
        verify(lienzoImageStrips, times(1)).register(eq(STRIPS), eq(callback));
    }

    @Test
    public void testDestroy() {
        tested.init(metadata, mock(Command.class));
        tested.destroy();
        verify(lienzoImageStrips, times(1)).remove(eq(STRIPS));
    }
}