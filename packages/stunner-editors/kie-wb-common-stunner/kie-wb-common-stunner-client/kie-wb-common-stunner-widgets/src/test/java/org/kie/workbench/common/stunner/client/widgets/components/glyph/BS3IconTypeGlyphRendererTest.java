/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.components.glyph;

import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.components.views.WidgetElementRendererView;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BS3IconTypeGlyphRendererTest {

    @Mock
    private WidgetElementRendererView view;

    @Mock
    private Supplier<WidgetElementRendererView> viewSupplier;

    @Mock
    private Command viewDestroyer;

    private BS3IconTypeGlyphRenderer tested;

    @Before
    public void setup() throws Exception {
        when(viewSupplier.get()).thenReturn(view);
        this.tested = new BS3IconTypeGlyphRenderer(viewSupplier,
                                                   viewDestroyer);
    }

    @Test
    public void testType() {
        assertEquals(BS3IconTypeGlyph.class,
                     tested.getGlyphType());
    }

    @Test
    public void testRender() {
        final BS3IconTypeGlyph glyph = BS3IconTypeGlyph.create(IconType.ADN);
        tested.render(glyph,
                      100,
                      200);
        verify(viewSupplier,
               times(1)).get();
        verify(view,
               times(1)).setWidget(any(Icon.class));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(viewDestroyer, times(1)).execute();
    }
}
