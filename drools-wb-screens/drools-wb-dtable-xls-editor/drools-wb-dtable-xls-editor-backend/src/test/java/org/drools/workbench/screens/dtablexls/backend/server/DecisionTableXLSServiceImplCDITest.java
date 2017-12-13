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

package org.drools.workbench.screens.dtablexls.backend.server;

import java.net.URL;

import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSService;
import org.guvnor.test.CDITestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DecisionTableXLSServiceImplCDITest extends CDITestSetup {

    private ConversionResult conversionResult;
    private DecisionTableXLSService xlsService;

    private String droolsDateFormat;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        xlsService = getReference(DecisionTableXLSService.class);

        droolsDateFormat = System.getProperty(ApplicationPreferences.DATE_FORMAT);
        System.setProperty(ApplicationPreferences.DATE_FORMAT, "dd-MM-yyyy");
    }

    @After
    public void tearDown() throws Exception {
        super.cleanup();
        if (droolsDateFormat != null) {
            System.setProperty(ApplicationPreferences.DATE_FORMAT, droolsDateFormat);
        } else {
            System.clearProperty(ApplicationPreferences.DATE_FORMAT);
        }
    }

    /**
     * Covers RHBA-48 - BigDecimal fields in table
     */
    @Test
    public void testConvertFunctionAndBigDecimal() throws Exception {
        convertResource("dtables/src/main/resources/guvnor/feature/dtables/FunctionAndBigDecimalTable.xls");
        assertEquals(1, conversionResult.getMessages().size());
        assertTrue(conversionResult.getMessages().get(0).getMessage().startsWith("Created Guided Decision Table 'FunctionAndBigDecimalTable (converted on"));
        assertTrue(conversionResult.getMessages().get(0).getMessage().endsWith(").gdst'"));
    }

    @Test
    public void testConvertStaticFields() throws Exception {
        convertResource("dtables/src/main/resources/guvnor/feature/dtables/StaticFieldsTable.xls");
        assertEquals(2, conversionResult.getMessages().size());
        assertEquals("Unable to convert value 'Message.GOODBYE' to NUMERIC_INTEGER. Cell (F11)",
                     conversionResult.getMessages().get(0).getMessage());
        assertTrue(conversionResult.getMessages().get(1).getMessage().startsWith("Created Guided Decision Table 'StaticFieldsTable (converted on"));
        assertTrue(conversionResult.getMessages().get(1).getMessage().endsWith(").gdst'"));
    }

    private void convertResource(final String resource) throws Exception {
        final URL resourceURL = getClass().getResource(resource);
        final org.uberfire.java.nio.file.Path resourceNioPath = fileSystemProvider.getPath(resourceURL.toURI());
        final Path resourcePath = Paths.convert(resourceNioPath);

        conversionResult = xlsService.convert(resourcePath);
    }
}
