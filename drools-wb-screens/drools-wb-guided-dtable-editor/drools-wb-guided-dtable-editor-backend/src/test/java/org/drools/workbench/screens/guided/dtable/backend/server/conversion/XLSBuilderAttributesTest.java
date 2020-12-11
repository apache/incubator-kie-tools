/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.backend.server.conversion;

import org.apache.poi.ss.usermodel.Workbook;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.drools.workbench.screens.guided.dtable.backend.server.util.TestUtil.loadResource;
import static org.junit.Assert.assertEquals;

public class XLSBuilderAttributesTest
        extends TestBase {

    private String oldValue;

    @BeforeClass
    public static void setBeforeClass() throws Exception {
        final String xml = loadResource(XLSBuilderAttributesTest.class.getResourceAsStream("Attributes.gdst"));

        final GuidedDecisionTable52 dtable = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final XLSBuilder.BuildResult buildResult = new XLSBuilder(dtable, makeDMO()).build();
        final Workbook workbook = buildResult.getWorkbook();

        assertEquals(1, workbook.getNumberOfSheets());
        sheet = workbook.iterator().next();
    }

    @Before
    public void setUp() throws Exception {

        oldValue = System.getProperty("drools.dateformat");

        System.setProperty("drools.dateformat",
                           "dd-MMM-yyyy");
    }

    @After
    public void tearDown() throws Exception {

        if (oldValue == null) {
            System.clearProperty("drools.dateformat");
        } else {
            System.setProperty("drools.dateformat",
                               oldValue);
        }
    }

    @Test
    public void headers() {

        assertEquals("RuleSet", cell(1, 1).getStringCellValue());
        assertEquals("mortgages.mortgages", cell(1, 2).getStringCellValue());

        assertEquals("Import", cell(2, 1).getStringCellValue());
        assertEquals("", sheet.getRow(2).getCell(2).getStringCellValue());

        assertEquals("RuleTable Attributes", cell(4, 1).getStringCellValue());
    }

    @Test
    public void correctAttributeHeaders() {

        assertEquals("PRIORITY", cell(5, 1).getStringCellValue());
        assertEquals("DATE-EFFECTIVE", cell(5, 2).getStringCellValue());
        assertEquals("NO-LOOP", cell(5, 3).getStringCellValue());
        assertEquals("ENABLED", cell(5, 4).getStringCellValue());
        assertEquals("DATE-EXPIRES", cell(5, 5).getStringCellValue());
        assertEquals("AGENDA-GROUP", cell(5, 6).getStringCellValue());
        assertEquals("ACTIVATION-GROUP", cell(5, 7).getStringCellValue());
        assertEquals("TIMER", cell(5, 8).getStringCellValue());
        assertEquals("CALENDARS", cell(5, 9).getStringCellValue());
        assertEquals("AUTO-FOCUS", cell(5, 10).getStringCellValue());
        assertEquals("LOCK-ON-ACTIVE", cell(5, 11).getStringCellValue());
        assertEquals("RULEFLOW-GROUP", cell(5, 12).getStringCellValue());
        assertEquals("DURATION", cell(5, 13).getStringCellValue());
    }

    @Test
    public void patterns() {

        assertNullCell(6, 1);
        assertNullCell(6, 2);
        assertNullCell(6, 3);
    }

    @Test
    public void constraints() {

        assertNullCell(7, 1);
        assertNullCell(7, 2);
        assertNullCell(7, 3);
        assertNullCell(7, 4);
        assertNullCell(7, 5);
        assertNullCell(7, 6);
        assertNullCell(7, 7);
        assertNullCell(7, 8);
        assertNullCell(7, 9);
        assertNullCell(7, 10);
        assertNullCell(7, 11);
        assertNullCell(7, 12);
        assertNullCell(7, 13);
        assertNullCell(7, 14);
    }

    @Test
    public void columnTitles() {

        assertNullCell(8, 1);
        assertNullCell(8, 2);
        assertNullCell(8, 3);
        assertNullCell(8, 4);
        assertNullCell(8, 5);
        assertNullCell(8, 6);
    }

    @Test
    public void content() {

        assertEquals("0", cell(9, 1).getStringCellValue());
        assertEquals("\"09-Aug-2019\"", cell(9, 2).getStringCellValue());
        assertEquals("true", cell(9, 3).getStringCellValue());
        assertEquals("true", cell(9, 4).getStringCellValue());
        assertEquals("\"09-Aug-2019\"", cell(9, 5).getStringCellValue());
        assertEquals("agenda group cell", cell(9, 6).getStringCellValue());
        assertEquals("activation group cell", cell(9, 7).getStringCellValue());
        assertEquals("", cell(9, 8).getStringCellValue());
        assertEquals("", cell(9, 9).getStringCellValue());
        assertEquals("true", cell(9, 10).getStringCellValue());
        assertEquals("true", cell(9, 11).getStringCellValue());
        assertEquals("rule flow group value", cell(9, 12).getStringCellValue());
        assertEquals("0", cell(9, 13).getStringCellValue());
    }
}