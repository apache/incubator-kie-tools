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

package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripRegistry;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoImageStripLoaderTest {

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ImageStripRegistry stripRegistry;

    @Mock
    private BiConsumer<com.ait.lienzo.client.core.image.ImageStrip[], Runnable> lienzoStripRegistration;

    @Mock
    private Consumer<String> lienzoStripRemoval;

    @Mock
    private ImageResource imageResource;

    @Mock
    private SafeUri safeUri;

    @Mock
    private CssResource cssResource;

    private ImageStrip.StripCssResource STRIP_CSS = new ImageStrip.StripCssResource() {
        @Override
        public CssResource getCssResource() {
            return cssResource;
        }

        @Override
        public String getClassName() {
            return "testClass";
        }
    };

    private ImageStrip STRIP = new ImageStrip() {
        @Override
        public ImageResource getImage() {
            return imageResource;
        }

        @Override
        public StripCssResource getCss() {
            return STRIP_CSS;
        }

        @Override
        public int getWide() {
            return 16;
        }

        @Override
        public int getHigh() {
            return 16;
        }

        @Override
        public int getPadding() {
            return 5;
        }

        @Override
        public Orientation getOrientation() {
            return Orientation.HORIZONTAL;
        }
    };

    private ImageStrip[] STRIPS = new ImageStrip[]{STRIP};

    private LienzoImageStripLoader tested;

    @Before
    public void setUp() {
        doAnswer(invocation -> {
            ((Runnable) invocation.getArguments()[1]).run();
            return null;
        }).when(lienzoStripRegistration).accept(any(com.ait.lienzo.client.core.image.ImageStrip[].class),
                                                any(Runnable.class));
        when(imageResource.getSafeUri()).thenReturn(safeUri);
        when(stripRegistry.get(any(Annotation.class))).thenReturn(STRIPS);
        when(stripRegistry.get((Annotation[]) anyVararg())).thenReturn(STRIPS);
        tested = new LienzoImageStripLoader(definitionUtils,
                                            stripRegistry,
                                            lienzoStripRegistration,
                                            lienzoStripRemoval);
    }

    @Test
    public void testInit() {
        when(safeUri.asString()).thenReturn("someUri");
        Metadata metadata = mock(Metadata.class);
        Command callback = mock(Command.class);
        tested.init(metadata,
                    callback);
        ArgumentCaptor<com.ait.lienzo.client.core.image.ImageStrip[]> stripsCaptor =
                ArgumentCaptor.forClass(com.ait.lienzo.client.core.image.ImageStrip[].class);
        verify(lienzoStripRegistration, times(1)).accept(stripsCaptor.capture(),
                                                         any(Runnable.class));
        final com.ait.lienzo.client.core.image.ImageStrip[] strips = stripsCaptor.getValue();
        assertNotNull(strips);
        assertEquals(1, strips.length);
        assertEquals("someUri", strips[0].getUrl());
        assertEquals(16, strips[0].getWide());
        assertEquals(16, strips[0].getHigh());
        assertEquals(5, strips[0].getPadding());
        assertEquals(com.ait.lienzo.client.core.image.ImageStrip.Orientation.HORIZONTAL, strips[0].getOrientation());
        assertFalse(tested.getRegistered().isEmpty());
    }

    @Test
    public void testDestroy() {
        Metadata metadata = mock(Metadata.class);
        Command callback = mock(Command.class);
        tested.init(metadata,
                    callback);
        tested.destroy();
        assertTrue(tested.getRegistered().isEmpty());
        verify(lienzoStripRemoval, times(1)).accept(anyString());
    }
}
