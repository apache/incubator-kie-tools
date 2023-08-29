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


package org.kie.workbench.common.stunner.core.client.shape;

import java.lang.annotation.Annotation;

import com.google.gwt.resources.client.ImageResource;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ImageStripRegistryTest {

    @Mock
    private ImageResource imageResource;

    @Mock
    private ImageStrip.StripCssResource cssResource;

    private ImageStripTestInstanceManaged strip;
    private ManagedInstance<ImageStrip> stripInstances;
    private ImageStripRegistry tested;

    @Before
    public void setUp() {
        strip = new ImageStripTestInstanceManaged();
        stripInstances = spy(new ManagedInstanceStub<>(strip));
        tested = new ImageStripRegistry(stripInstances);
    }

    @Test
    public void testGetByName() {
        final ImageStrip result = tested.get(ImageStripRegistry.getName(ImageStripTestInstance.class));
        assertEquals(strip, result);
    }

    @Test
    public void testGetByType() {
        final ImageStrip result = tested.get(ImageStripTestInstance.class);
        assertEquals(strip, result);
        verify(stripInstances, times(1)).select(eq(ImageStripTestInstance.class));
    }

    @Test
    public void testGetByQualifier() {
        Annotation qualifier = mock(Annotation.class);
        final ImageStrip[] result = tested.get(qualifier);
        assertEquals(1, result.length);
        assertEquals(strip, result[0]);
        verify(stripInstances, times(1)).select(eq(qualifier));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(stripInstances, times(1)).destroyAll();
    }

    private class ImageStripTestInstanceManaged extends ImageStripTestInstance {

    }

    private class ImageStripTestInstance implements ImageStrip {

        @Override
        public ImageResource getImage() {
            return imageResource;
        }

        @Override
        public StripCssResource getCss() {
            return cssResource;
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
            return 3;
        }

        @Override
        public Orientation getOrientation() {
            return Orientation.HORIZONTAL;
        }
    }
}
