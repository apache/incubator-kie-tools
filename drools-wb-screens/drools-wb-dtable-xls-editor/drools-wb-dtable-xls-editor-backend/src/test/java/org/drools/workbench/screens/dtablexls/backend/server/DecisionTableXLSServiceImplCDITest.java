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

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionMessage;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.test.CDITestSetup;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

public class DecisionTableXLSServiceImplCDITest extends CDITestSetup {

    private DecisionTableXLSService xlsService;

    private static String droolsDateFormat;

    @BeforeClass
    public static void setUpDateTimeFormat() {
        droolsDateFormat = System.setProperty(ApplicationPreferences.DATE_FORMAT, "dd-MM-yyyy");
    }

    @AfterClass
    public static void clearDateTimeFormat() {
        if (droolsDateFormat != null) {
            System.setProperty(ApplicationPreferences.DATE_FORMAT, droolsDateFormat);
        } else {
            System.clearProperty(ApplicationPreferences.DATE_FORMAT);
        }
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        xlsService = getReference(DecisionTableXLSService.class);
    }

    @After
    public void tearDown() throws Exception {
        super.cleanup();
    }

    /**
     * Covers RHBA-48 - BigDecimal fields in table
     */
    @Test
    public void testConvertFunctionAndBigDecimal() throws Exception {
        final String resourcePath = "dtables/src/main/resources/guvnor/feature/dtables/FunctionAndBigDecimalTable.xls";
        final ConversionResult conversionResult = convertResource(resourcePath);
        final List<ConversionMessage> messages = conversionResult.getMessages();
        Assertions.assertThat(messages).hasSize(1);
        final String message = messages.get(0).getMessage();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(message).startsWith("Created Guided Decision Table 'FunctionAndBigDecimalTable (converted on");
            softly.assertThat(message).endsWith(").gdst'");
        });
    }

    /**
     * Covers RHBRMS-609 - Validation for spreadsheet fails because it cannot find functions in the same package
     */
    @Test
    public void testValidateFunctionAndBigDecimal() throws Exception {
        final String resourcePath = "dtables/src/main/resources/guvnor/feature/dtables/FunctionAndBigDecimalTable.xls";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).hasSize(0);
    }

    @Test
    public void testConvertStaticFields() throws Exception {
        final String resourcePath = "dtables/src/main/resources/guvnor/feature/dtables/StaticFieldsTable.xls";
        final ConversionResult conversionResult = convertResource(resourcePath);
        final List<ConversionMessage> messages = conversionResult.getMessages();
        Assertions.assertThat(messages).hasSize(2);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(messages.get(0).getMessage())
                    .isEqualTo("Unable to convert value 'Message.GOODBYE' to NUMERIC_INTEGER. Cell (F11)");
            softly.assertThat(messages.get(1).getMessage())
                    .startsWith("Created Guided Decision Table 'StaticFieldsTable (converted on");
            softly.assertThat(messages.get(1).getMessage())
                    .endsWith(").gdst'");
        });
    }

    @Test
    public void testConvertFunctionInSpreadhseet() throws Exception {
        final String resourcePath = "dtables/src/main/resources/guvnor/feature/dtables/test_functions.xls";
        final ConversionResult conversionResult = convertResource(resourcePath);
        final List<ConversionMessage> messages = conversionResult.getMessages();
        Assertions.assertThat(messages).hasSize(2);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(messages.get(0).getMessage())
                    .startsWith("Created Function 'Function 1")
                    .endsWith(".drl'");
            softly.assertThat(messages.get(1).getMessage())
                    .startsWith("Created Guided Decision Table 'Hello RuleTable")
                    .endsWith(").gdst'");
        });
    }

    @Test
    public void testValidateMultiplePatterns() throws Exception {
        final String resourcePath = "dtables/src/main/resources/guvnor/feature/dtables/SampleDTExt1.xls";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).hasSize(0);
    }

    @Test
    public void testValidateColumnsNotInStandardOrder() throws Exception {
        final String resourcePath = "dtables/src/main/resources/guvnor/feature/dtables/SampleDTExt2.xls";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).hasSize(0);
    }

    /**
     * Covers RHDM-216 - From accumulate causes validation errors
     */
    @Test
    public void testFromAccumulate() throws Exception {
        final String resourcePath = "forest/src/main/resources/com/redhat/sample/ForestHealth.xls";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).hasSize(0);
    }

    private ConversionResult convertResource(final String resource) throws Exception {
        final Path resourcePath = getPath(resource);
        return xlsService.convert(resourcePath);
    }

    private List<ValidationMessage> validateResource(final String resource) throws Exception {
        final Path resourcePath = getPath(resource);
        return xlsService.validate(resourcePath, resourcePath);
    }

    private Path getPath(String resource) throws URISyntaxException {
        final URL resourceURL = getClass().getResource(resource);
        final org.uberfire.java.nio.file.Path resourceNioPath = fileSystemProvider.getPath(resourceURL.toURI());
        return Paths.convert(resourceNioPath);
    }
}
