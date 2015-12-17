/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

        String className = "java.lang.Object";
        assertEquals("getA", genTools.toJavaGetter("a", className));
        assertEquals("isA", genTools.toJavaGetter("a", "boolean"));
        assertEquals("setA", genTools.toJavaSetter("a"));

        assertEquals("getaB", genTools.toJavaGetter("aB", className));
        assertEquals("isaB", genTools.toJavaGetter("aB", "boolean"));
        assertEquals("setaB", genTools.toJavaSetter("aB"));

        assertEquals("getaBC", genTools.toJavaGetter("aBC", className));
        assertEquals("isaBC", genTools.toJavaGetter("aBC", "boolean"));
        assertEquals("setaBC", genTools.toJavaSetter("aBC"));

        assertEquals("getaBa", genTools.toJavaGetter("aBa", className));
        assertEquals("isaBa", genTools.toJavaGetter("aBa", "boolean"));
        assertEquals("setaBa", genTools.toJavaSetter("aBa"));

        assertEquals("getAa", genTools.toJavaGetter("aa", className));
        assertEquals("isAa", genTools.toJavaGetter("aa", "boolean"));
        assertEquals("setAa", genTools.toJavaSetter("aa"));


        assertEquals("getAaB", genTools.toJavaGetter("aaB", className));
        assertEquals("isAaB", genTools.toJavaGetter("aaB", "boolean"));
        assertEquals("setAaB", genTools.toJavaSetter("aaB"));

        assertEquals("getAaa", genTools.toJavaGetter("aaa", className));
        assertEquals("isAaa", genTools.toJavaGetter("aaa", "boolean"));
        assertEquals("setAaa", genTools.toJavaSetter("aaa"));

        assertEquals("getB", genTools.toJavaGetter("b", className));
        assertEquals("isB", genTools.toJavaGetter("b", "boolean"));
        assertEquals("setB", genTools.toJavaSetter("b"));

        assertEquals("getC", genTools.toJavaGetter("C", className));
        assertEquals("isC", genTools.toJavaGetter("C", "boolean"));
        assertEquals("setC", genTools.toJavaSetter("C"));

        assertEquals("getCB", genTools.toJavaGetter("CB", className));
        assertEquals("isCB", genTools.toJavaGetter("CB", "boolean"));
        assertEquals("setCB", genTools.toJavaSetter("CB"));


        assertEquals("getCBC", genTools.toJavaGetter("CBC", className));
        assertEquals("isCBC", genTools.toJavaGetter("CBC", "boolean"));
        assertEquals("setCBC", genTools.toJavaSetter("CBC"));

        assertEquals("getCBa", genTools.toJavaGetter("CBa", className));
        assertEquals("isCBa", genTools.toJavaGetter("CBa", "boolean"));
        assertEquals("setCBa", genTools.toJavaSetter("CBa"));

        assertEquals("getCa", genTools.toJavaGetter("Ca", className));
        assertEquals("isCa", genTools.toJavaGetter("Ca", "boolean"));
        assertEquals("setCa", genTools.toJavaSetter("Ca"));

        assertEquals("getCaB", genTools.toJavaGetter("CaB", className));
        assertEquals("isCaB", genTools.toJavaGetter("CaB", "boolean"));
        assertEquals("setCaB", genTools.toJavaSetter("CaB"));

        assertEquals("getCaa", genTools.toJavaGetter("Caa", className));
        assertEquals("isCaa", genTools.toJavaGetter("Caa", "boolean"));
        assertEquals("setCaa", genTools.toJavaSetter("Caa"));

        assertEquals("get_C", genTools.toJavaGetter("_C", className));
        assertEquals("is_C", genTools.toJavaGetter("_C", "boolean"));
        assertEquals("set_C", genTools.toJavaSetter("_C"));


        assertEquals("getÁ", genTools.toJavaGetter("á", className));
        assertEquals("isÁ", genTools.toJavaGetter("á", "boolean"));
        assertEquals("setÁ", genTools.toJavaSetter("á"));

        assertEquals("get_", genTools.toJavaGetter("_", className));
        assertEquals("is_", genTools.toJavaGetter("_", "boolean"));
        assertEquals("set_", genTools.toJavaSetter("_"));


    }
}
