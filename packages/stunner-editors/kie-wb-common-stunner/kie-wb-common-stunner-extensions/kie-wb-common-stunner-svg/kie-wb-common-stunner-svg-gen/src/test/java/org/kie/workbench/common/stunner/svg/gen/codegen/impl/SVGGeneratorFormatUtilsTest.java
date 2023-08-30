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


package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * Tests that check the formatting results are the always the same ones, no matter the locale.
 */
@RunWith(MockitoJUnitRunner.class)
public class SVGGeneratorFormatUtilsTest {

    private Locale locale;

    @Before
    public void init() {
        locale = Locale.getDefault();
    }

    @After
    public void end() {
        Locale.setDefault(locale);
    }

    @Test
    public void testFormatDoubleES() {
        Locale.setDefault(new Locale("es",
                                     "ES"));
        final String result = SVGGeneratorFormatUtils.format(45.675d);
        assertEquals("45.67",
                     result);
    }

    @Test
    public void testFormatDoubleRU() {
        Locale.setDefault(new Locale("ru",
                                     "RU"));
        final String result = SVGGeneratorFormatUtils.format(45.675d);
        assertEquals("45.67",
                     result);
    }

    @Test
    public void testFormatDoubleUS() {
        Locale.setDefault(new Locale("en",
                                     "US"));
        final String result = SVGGeneratorFormatUtils.format(45.675d);
        assertEquals("45.67",
                     result);
    }

    @Test
    public void testFormatMessageES() {
        Locale.setDefault(new Locale("es",
                                     "ES"));
        final String result = SVGGeneratorFormatUtils.format("[%1s,%2s]",
                                                             45.675d,
                                                             23.4563d);
        assertEquals("[45.67,23.46]",
                     result);
    }

    @Test
    public void testFormatMessageRU() {
        Locale.setDefault(new Locale("ru",
                                     "RU"));
        final String result = SVGGeneratorFormatUtils.format("[%1s,%2s]",
                                                             45.675d,
                                                             23.4563d);
        assertEquals("[45.67,23.46]",
                     result);
    }

    @Test
    public void testFormatMessageUS() {
        Locale.setDefault(new Locale("en",
                                     "US"));
        final String result = SVGGeneratorFormatUtils.format("[%1s,%2s]",
                                                             45.675d,
                                                             23.4563d);
        assertEquals("[45.67,23.46]",
                     result);
    }
}
