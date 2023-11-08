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


package org.uberfire.ext.editor.commons.client.file.exports.svg;

import elemental2.dom.CanvasRenderingContext2D;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.ext.editor.commons.client.file.exports.jso.svg.C2S;
import org.uberfire.ext.editor.commons.client.file.exports.jso.svg.C2SContext2D;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(C2S.class)
public class Context2DFactoryTest {

    private Context2DFactory context2DFactory;

    @Before
    public void setUp() throws Exception {
        context2DFactory = new Context2DFactory();
    }

    @Test
    public void testCreate() {
        final CanvasRenderingContext2D nativeContext = mock(CanvasRenderingContext2D.class);

        PowerMockito.mockStatic(C2S.class);

        when(C2S.create(anyDouble(), anyDouble(), any(CanvasRenderingContext2D.class))).thenReturn(new C2S(null));

        final SvgExportSettings settings = spy(new SvgExportSettings(100, 100, nativeContext));
        doReturn(100d).when(settings).getWidth();
        doReturn(100d).when(settings).getHeight();
        doReturn(nativeContext).when(settings).getContext();
        final IContext2D context2D = context2DFactory.create(settings);
        assertTrue(C2SContext2D.class.isInstance(context2D));
    }
}
