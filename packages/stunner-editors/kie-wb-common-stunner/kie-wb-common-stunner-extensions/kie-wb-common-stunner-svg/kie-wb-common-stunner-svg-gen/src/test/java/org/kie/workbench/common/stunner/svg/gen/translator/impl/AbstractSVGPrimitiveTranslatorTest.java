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


package org.kie.workbench.common.stunner.svg.gen.translator.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.gen.model.impl.AbstractPrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Element;

import static org.kie.workbench.common.stunner.svg.gen.translator.SVGDocumentTranslator.STUNNER_ATTR_NS_TRANSFORM;
import static org.kie.workbench.common.stunner.svg.gen.translator.SVGDocumentTranslator.STUNNER_ATTR_TRANSFORM_NON_SCALABLE;
import static org.kie.workbench.common.stunner.svg.gen.translator.SVGDocumentTranslator.STUNNER_ATTR_TRANSFORM_SCALABLE;
import static org.kie.workbench.common.stunner.svg.gen.translator.SVGDocumentTranslator.STUNNER_URI;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractSVGPrimitiveTranslatorTest {

    private AbstractSVGPrimitiveTranslator tested;

    @Mock
    private Element element;

    @Mock
    private AbstractPrimitiveDefinition<?> def;

    @Before
    public void setUp() {
        tested = new AbstractSVGPrimitiveTranslator() {
            @Override
            protected AbstractPrimitiveDefinition<?> doTranslate(Element pathElement, SVGTranslatorContext context) {
                return null;
            }

            @Override
            public String getTagName() {
                return null;
            }

            @Override
            public Class getInputType() {
                return null;
            }
        };
    }

    @Test
    public void testScalableFalse() {
        when(element.getAttributeNS(STUNNER_URI, STUNNER_ATTR_NS_TRANSFORM))
                .thenReturn(STUNNER_ATTR_TRANSFORM_NON_SCALABLE);
        tested.translateTransformDefinition(element, def);
        verify(def).setScalable(false);
    }

    @Test
    public void testScalableTrue() {
        when(element.getAttributeNS(STUNNER_URI, STUNNER_ATTR_NS_TRANSFORM))
                .thenReturn(STUNNER_ATTR_TRANSFORM_SCALABLE);
        tested.translateTransformDefinition(element, def);
        verify(def).setScalable(true);
    }
}