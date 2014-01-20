/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.datamodeller.codegen;


import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class GenerationToolsTest {

    @Test
    public void testNamesGeneration() {

        GenerationTools genTools = new GenerationTools();

        assertEquals("getA", genTools.toJavaGetter("a"));
        assertEquals("setA", genTools.toJavaSetter("a"));

        assertEquals("getaB", genTools.toJavaGetter("aB"));
        assertEquals("setaB", genTools.toJavaSetter("aB"));

        assertEquals("getaBC", genTools.toJavaGetter("aBC"));
        assertEquals("setaBC", genTools.toJavaSetter("aBC"));

        assertEquals("getaBa", genTools.toJavaGetter("aBa"));
        assertEquals("setaBa", genTools.toJavaSetter("aBa"));

        assertEquals("getAa", genTools.toJavaGetter("aa"));
        assertEquals("setAa", genTools.toJavaSetter("aa"));


        assertEquals("getAaB", genTools.toJavaGetter("aaB"));
        assertEquals("setAaB", genTools.toJavaSetter("aaB"));

        assertEquals("getAaa", genTools.toJavaGetter("aaa"));
        assertEquals("setAaa", genTools.toJavaSetter("aaa"));

        assertEquals("getB", genTools.toJavaGetter("b"));
        assertEquals("setB", genTools.toJavaSetter("b"));

        assertEquals("getC", genTools.toJavaGetter("C"));
        assertEquals("setC", genTools.toJavaSetter("C"));

        assertEquals("getCB", genTools.toJavaGetter("CB"));
        assertEquals("setCB", genTools.toJavaSetter("CB"));


        assertEquals("getCBC", genTools.toJavaGetter("CBC"));
        assertEquals("setCBC", genTools.toJavaSetter("CBC"));

        assertEquals("getCBa", genTools.toJavaGetter("CBa"));
        assertEquals("setCBa", genTools.toJavaSetter("CBa"));

        assertEquals("getCa", genTools.toJavaGetter("Ca"));
        assertEquals("setCa", genTools.toJavaSetter("Ca"));

        assertEquals("getCaB", genTools.toJavaGetter("CaB"));
        assertEquals("setCaB", genTools.toJavaSetter("CaB"));

        assertEquals("getCaa", genTools.toJavaGetter("Caa"));
        assertEquals("setCaa", genTools.toJavaSetter("Caa"));

        assertEquals("get_C", genTools.toJavaGetter("_C"));
        assertEquals("set_C", genTools.toJavaSetter("_C"));


        assertEquals("getÁ", genTools.toJavaGetter("á"));
        assertEquals("setÁ", genTools.toJavaSetter("á"));

        assertEquals("get_", genTools.toJavaGetter("_"));
        assertEquals("set_", genTools.toJavaSetter("_"));


    }
}
