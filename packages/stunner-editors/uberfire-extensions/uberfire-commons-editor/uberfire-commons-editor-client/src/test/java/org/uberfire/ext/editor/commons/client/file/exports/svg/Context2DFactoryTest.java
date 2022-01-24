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

package org.uberfire.ext.editor.commons.client.file.exports.svg;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.CanvasRenderingContext2D;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.editor.commons.client.file.exports.jso.svg.C2SContext2D;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(GwtMockitoTestRunner.class)
public class Context2DFactoryTest {

    private Context2DFactory context2DFactory;

    @Before
    public void setUp() throws Exception {
        context2DFactory = new Context2DFactory();
    }

    @Test
    public void testCreate() {
        final CanvasRenderingContext2D nativeContext = mock(CanvasRenderingContext2D.class);
        final SvgExportSettings settings = new SvgExportSettings(100, 100, nativeContext);
        final IContext2D context2D = context2DFactory.create(settings);
        assertTrue(C2SContext2D.class.isInstance(context2D));
    }
}