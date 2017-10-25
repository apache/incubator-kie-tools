/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.common.services.project.backend.server.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;

import org.guvnor.common.services.project.model.POM;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class POMContentHandlerOneToOneTest {

    private String fileName;

    @Test
    public void testNoChanges() throws Exception {

        String pomxml = fromStream(POMContentHandlerOneToOneTest.class.getResourceAsStream(fileName));

        POM model = new POMContentHandler().toModel(pomxml);

        assertContainsIgnoreWhitespace(pomxml,
                                       new POMContentHandler().toString(model,
                                                                        pomxml));
    }

    public POMContentHandlerOneToOneTest(String fileName) {
        this.fileName = fileName;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getData() {
        return Arrays.asList(new Object[][]{
                {"pom1.xml"},
                {"pom2.xml"},
                {"pom3.xml"}
        });
    }

    public static String fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line).append("\n");
        }
        return out.toString();
    }

    private void assertContainsIgnoreWhitespace(final String expected,
                                                final String xml) {
        final String cleanExpected = expected.replaceAll("\\s+",
                                                         "");
        final String cleanActual = xml.replaceAll("\\s+",
                                                  "");

        assertEquals("Failure with pom file: " + fileName,
                     cleanExpected,
                     cleanActual);
    }
}
