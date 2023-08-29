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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.gen.exception.TranslatorException;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SVGViewBoxTranslatorTest {

    @Test
    public void testTranslate() throws Exception {
        ViewDefinition.ViewBoxDefinition definition = SVGViewBoxTranslator.translate("0 0 0 0");
        assertDefinition(definition,
                         0,
                         0,
                         0,
                         0);
        definition = SVGViewBoxTranslator.translate("0px 0px 0px 0px"); // Shoult not happen this, but better considering it...
        assertDefinition(definition,
                         0,
                         0,
                         0,
                         0);
        definition = SVGViewBoxTranslator.translate("123.4 23 567.45 568.70");
        assertDefinition(definition,
                         123.4,
                         23,
                         567.45,
                         568.70);
        definition = SVGViewBoxTranslator.translate("-123.4 -23.5 567.45 568.70");
        assertDefinition(definition,
                         -123.4,
                         -23.5,
                         567.45,
                         568.70);
    }

    @Test(expected = TranslatorException.class)
    public void testTranslateException() throws Exception {
        ViewDefinition.ViewBoxDefinition definition = SVGViewBoxTranslator.translate("0 0 0");
        assertDefinition(definition,
                         0,
                         0,
                         0,
                         0);
    }

    private void assertDefinition(final ViewDefinition.ViewBoxDefinition viewBoxDefinition,
                                  final double minX,
                                  final double minY,
                                  final double width,
                                  final double height) {
        assertEquals(minX,
                     viewBoxDefinition.getMinX(),
                     0d);
        assertEquals(minY,
                     viewBoxDefinition.getMinY(),
                     0d);
        assertEquals(width,
                     viewBoxDefinition.getWidth(),
                     0d);
        assertEquals(height,
                     viewBoxDefinition.getHeight(),
                     0d);
    }
}
