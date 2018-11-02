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

package org.kie.workbench.common.stunner.svg.gen.impl;

import javax.annotation.processing.Messager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.gen.SVGGeneratorRequest;
import org.kie.workbench.common.stunner.svg.gen.codegen.impl.SVGViewFactoryGenerator;
import org.kie.workbench.common.stunner.svg.gen.model.ViewFactory;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewDefinitionImpl;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGDocumentTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SVGGeneratorImplTest {

    private static final String SVG_NAME = "svg-test";
    private static final String SVG_PKG = "org.kie.workbench.common.stunner.svg.gen.test";
    private static final String SVG_FQCN = "org.kie.workbench.common.stunner.svg.gen.test.SVGViewFactory";
    private static final String SVG_CANCEL_NAME = "svg-cancel";
    private static final String SVG_CANCEL_PATH = "org/kie/workbench/common/stunner/svg/gen/cancel.svg";

    @Mock
    SVGDocumentTranslator translator;

    @Mock
    ViewDefinitionImpl viewDefinition;

    @Mock
    SVGViewFactoryGenerator viewFactoryGenerator;

    private SVGGeneratorImpl tested;

    @Before
    public void setup() throws Exception {
        when(translator.translate(any(SVGTranslatorContext.class))).thenReturn(viewDefinition);
        tested = new SVGGeneratorImpl(translator,
                                      viewFactoryGenerator);
    }

    @Test
    public void testGenerate() throws Exception {
        when(viewDefinition.getId()).thenReturn("svg-cancel");
        doAnswer(new Answer<StringBuffer>() {
            @Override
            public StringBuffer answer(final InvocationOnMock invocationOnMock) throws Throwable {
                final ViewFactory factoryArgument = (ViewFactory) invocationOnMock.getArguments()[0];
                assertEquals(SVG_NAME,
                             factoryArgument.getSimpleName());
                assertEquals(SVG_PKG,
                             factoryArgument.getPackage());
                assertEquals(SVG_FQCN,
                             factoryArgument.getImplementedType());
                assertTrue(factoryArgument.getViewDefinitions().size() == 1);
                assertEquals(viewDefinition,
                             factoryArgument.getViewDefinitions().get(0));
                return new StringBuffer("done");
            }
        }).when(viewFactoryGenerator).generate(any(ViewFactory.class));
        final SVGGeneratorRequest request = new SVGGeneratorRequest(SVG_NAME,
                                                                    SVG_PKG,
                                                                    SVG_FQCN,
                                                                    "",
                                                                    "MyViewBuilderType.class",
                                                                    mock(Messager.class));
        request.getViewSources().put(SVG_CANCEL_NAME,
                                     SVG_CANCEL_PATH);
        tested.generate(request);
        verify(viewDefinition,
               times(1)).setFactoryMethodName(eq(SVG_CANCEL_NAME));
        verify(viewFactoryGenerator,
               times(1)).generate(any(ViewFactory.class));
    }
}
