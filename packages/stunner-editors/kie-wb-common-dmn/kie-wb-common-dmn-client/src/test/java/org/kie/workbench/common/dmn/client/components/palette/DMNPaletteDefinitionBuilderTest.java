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

package org.kie.workbench.common.dmn.client.components.palette;

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Categories;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.palette.CollapsedPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinition;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.profile.DomainProfileManager;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DMNPaletteDefinitionBuilderTest {

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private DomainProfileManager profileFunctions;

    @Mock
    private DefinitionsCacheRegistry definitionsRegistry;

    @Mock
    private StunnerTranslationService translationService;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private DefaultPaletteDefinition paletteDefinition;

    private DMNPaletteDefinitionBuilder tested;
    private CollapsedPaletteDefinitionBuilder collapsedPaletteBuilder;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        collapsedPaletteBuilder = spy(new CollapsedPaletteDefinitionBuilder(definitionUtils,
                                                                            profileFunctions,
                                                                            definitionsRegistry,
                                                                            translationService));
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Consumer<DefaultPaletteDefinition> c = (Consumer<DefaultPaletteDefinition>) invocationOnMock.getArguments()[1];
                c.accept(paletteDefinition);
                return null;
            }
        }).when(collapsedPaletteBuilder).build(eq(canvasHandler),
                                               any(Consumer.class));
        tested = new DMNPaletteDefinitionBuilder(collapsedPaletteBuilder);
        tested.init();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFilters() {
        assertTrue(tested.getPaletteDefinitionBuilder().getCategoryFilter().test(Categories.NODES));
        assertFalse(tested.getPaletteDefinitionBuilder().getCategoryFilter().test(Categories.DIAGRAM));
        assertFalse(tested.getPaletteDefinitionBuilder().getCategoryFilter().test(Categories.CONNECTORS));
        assertFalse(tested.getPaletteDefinitionBuilder().getCategoryFilter().test(Categories.MISCELLANEOUS));
        assertFalse(tested.getPaletteDefinitionBuilder().getCategoryFilter().test(Categories.DOMAIN_OBJECTS));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuild() {
        Consumer<DefaultPaletteDefinition> paletteConsumer = mock(Consumer.class);
        tested.build(canvasHandler,
                     paletteConsumer);
        verify(collapsedPaletteBuilder, times(1)).build(eq(canvasHandler),
                                                        any(Consumer.class));
        verify(paletteConsumer, times(1)).accept(eq(paletteDefinition));
    }
}
