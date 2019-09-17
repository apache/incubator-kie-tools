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
import org.junit.BeforeClass;
import org.junit.Test;

import static org.drools.workbench.screens.guided.dtable.backend.server.util.TestUtil.loadResource;
import static org.junit.Assert.assertEquals;

public class XLSBuilderFormulaAndEvalTest
        extends TestBase {

    @BeforeClass
    public static void setUp() throws Exception {
        final String xml = loadResource(XLSBuilderFormulaAndEvalTest.class.getResourceAsStream("FormulaAndEval.gdst"));

        final GuidedDecisionTable52 dtable = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final XLSBuilder.BuildResult buildResult = new XLSBuilder(dtable, makeDMO()).build();
        final Workbook workbook = buildResult.getWorkbook();

        assertEquals(1, workbook.getNumberOfSheets());
        sheet = workbook.iterator().next();
    }

    @Test
    public void headers() {

        assertEquals("RuleSet", cell(1, 1).getStringCellValue());
        assertEquals("mortgages.mortgages", cell(1, 2).getStringCellValue());

        assertEquals("Import", cell(2, 1).getStringCellValue());
        assertEquals("", sheet.getRow(2).getCell(2).getStringCellValue());

        assertEquals("RuleTable FormulaAndEval", cell(4, 1).getStringCellValue());
    }

    @Test
    public void correctAttributeHeaders() {

        assertEquals("METADATA", cell(5, 1).getStringCellValue());
        assertEquals("CONDITION", cell(5, 2).getStringCellValue());
        assertEquals("CONDITION", cell(5, 3).getStringCellValue());
        assertEquals("CONDITION", cell(5, 4).getStringCellValue());
        assertEquals("CONDITION", cell(5, 5).getStringCellValue());
        assertNullCell(5, 6);
    }

    @Test
    public void patterns() {

        assertNullCell(6, 1);
        assertEquals("a : Applicant", cell(6, 2).getStringCellValue());
        assertEquals("a : Applicant", cell(6, 3).getStringCellValue());
        assertEquals("a : Applicant", cell(6, 4).getStringCellValue());
        assertEquals("a : Applicant", cell(6, 5).getStringCellValue());
        assertNullCell(6, 6);
    }

    @Test
    public void constraints() {

        assertEquals("thisIsMetadata( $param )", cell(7, 1).getStringCellValue());
        assertEquals("age == ( $param )", cell(7, 2).getStringCellValue());
        assertEquals("eval( this.callMethod($param) )", cell(7, 3).getStringCellValue());
        assertEquals("eval( $param )", cell(7, 4).getStringCellValue());
        assertEquals("ageBind : age > ( $param )", cell(7, 5).getStringCellValue());
        assertNullCell(7, 6);
    }

    @Test
    public void columnTitles() {

        assertEquals("", cell(8, 1).getStringCellValue());
        assertEquals("Formula", cell(8, 2).getStringCellValue());
        assertEquals("With Parameter", cell(8, 3).getStringCellValue());
        assertEquals("No Parameter", cell(8, 4).getStringCellValue());
        assertEquals("Field uses bind", cell(8, 5).getStringCellValue());
        assertNullCell(8, 6);
    }

    @Test
    public void content() {

        assertEquals("hello", cell(9, 1).getStringCellValue());
        assertEquals("1+2+3", cell(9, 2).getStringCellValue());
        assertEquals("\"hello\"", cell(9, 3).getStringCellValue());
        assertEquals("run()", cell(9, 4).getStringCellValue());
        assertEquals("3+1", cell(9, 5).getStringCellValue());
        assertNullCell(9, 6);
    }
}