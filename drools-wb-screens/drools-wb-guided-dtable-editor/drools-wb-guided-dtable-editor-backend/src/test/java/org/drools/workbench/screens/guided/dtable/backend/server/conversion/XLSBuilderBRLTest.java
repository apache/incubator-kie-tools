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

public class XLSBuilderBRLTest
        extends TestBase {

    @BeforeClass
    public static void setUp() throws Exception {
        final String xml = loadResource(XLSBuilderAttributesNegateTest.class.getResourceAsStream("BRL.gdst"));

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

        assertEquals("RuleTable BRL", cell(4, 1).getStringCellValue());
    }

    @Test
    public void columnTypes() {

        assertEquals("CONDITION", cell(5, 1).getStringCellValue());
        assertEquals("CONDITION", cell(5, 2).getStringCellValue());
        assertEquals("CONDITION", cell(5, 3).getStringCellValue());
        assertEquals("CONDITION", cell(5, 4).getStringCellValue());
        assertEquals("CONDITION", cell(5, 5).getStringCellValue());
        assertEquals("CONDITION", cell(5, 6).getStringCellValue());
        assertEquals("ACTION", cell(5, 7).getStringCellValue());
        assertEquals("ACTION", cell(5, 8).getStringCellValue());
        assertEquals("ACTION", cell(5, 9).getStringCellValue());
        assertEquals("ACTION", cell(5, 10).getStringCellValue());
        assertEquals("ACTION", cell(5, 11).getStringCellValue());
        assertEquals("ACTION", cell(5, 12).getStringCellValue());
        assertEquals("ACTION", cell(5, 12).getStringCellValue());
        assertEquals("ACTION", cell(5, 13).getStringCellValue());
        assertNullCell(5, 14);
    }

    @Test
    public void patterns() {

        assertNullCell(6, 1);
        assertNullCell(6, 2);
        assertEquals("Applicant", cell(6, 3).getStringCellValue().trim());
        assertEquals("Applicant", cell(6, 4).getStringCellValue().trim());
        assertEquals("Applicant", cell(6, 5).getStringCellValue().trim());
        assertEquals("Applicant", cell(6, 6).getStringCellValue().trim());
        assertNullCell(6, 7);
    }

    @Test
    public void constraints() {

        assertEquals("LoanApplication( explanation != null )", cell(7, 1).getStringCellValue().trim());
        assertEquals("Applicant( age == $1 , approved == $2 )", cell(7, 2).getStringCellValue().trim());
        assertEquals("age == $param", cell(7, 3).getStringCellValue().trim());
        assertEquals("approved == $param", cell(7, 4).getStringCellValue().trim());
        assertEquals("age == $param", cell(7, 5).getStringCellValue().trim());
        assertEquals("approved == $param", cell(7, 6).getStringCellValue().trim());
        assertEquals("Applicant brlColumnFact0 = new Applicant(); insert( brlColumnFact0 );", cell(7, 7).getStringCellValue().trim());
        assertEquals("brlColumnFact0.setAge( $param );", cell(7, 8).getStringCellValue().trim());
        assertEquals("brlColumnFact0.setApproved( $param );", cell(7, 9).getStringCellValue().trim());
        assertEquals("LoanApplication brlColumnFact1 = new LoanApplication();\n" +
                             "\t\tbrlColumnFact1.setApproved( true );\n" +
                             "\t\tinsert( brlColumnFact1 );", cell(7, 10).getStringCellValue().trim());
        assertEquals("log($1 + \" \" + $2);", cell(7, 11).getStringCellValue().trim());
        assertEquals("Bankruptcy brlColumnFact2 = new Bankruptcy(); insert( brlColumnFact2 );", cell(7, 12).getStringCellValue().trim());
        assertEquals("brlColumnFact2.setAmountOwed( $param );", cell(7, 13).getStringCellValue().trim());
        assertNullCell(7, 14);
    }

    @Test
    public void columnTitles() {

        assertEquals("Not null", cell(8, 1).getStringCellValue());
        assertEquals("Free form LHS", cell(8, 2).getStringCellValue());
        assertEquals("Order mixed on purpose", cell(8, 3).getStringCellValue());
        assertEquals("Order mixed on purpose", cell(8, 4).getStringCellValue());
        assertEquals("Order mixed on purpose", cell(8, 5).getStringCellValue());
        assertEquals("Order mixed on purpose", cell(8, 6).getStringCellValue());
        assertEquals("", cell(8, 7).getStringCellValue());
        assertEquals("Something", cell(8, 8).getStringCellValue());
        assertEquals("Something", cell(8, 9).getStringCellValue());
        assertEquals("No variable BRL action", cell(8, 10).getStringCellValue());
        assertEquals("Free form RHS", cell(8, 11).getStringCellValue());
        assertEquals("", cell(8, 12).getStringCellValue());
        assertEquals("Amount", cell(8, 13).getStringCellValue());
        assertNullCell(8, 14);
    }

    @Test
    public void content() {

        assertEquals("X", cell(9, 1).getStringCellValue());
        assertEquals("12, false", cell(9, 2).getStringCellValue());
        assertNullCell(9, 3);
        assertEquals("true", cell(9, 4).getStringCellValue());
        assertNullCell(9, 5);
        assertEquals("false", cell(9, 6).getStringCellValue());
        assertEquals("X", cell(9, 7).getStringCellValue());
        assertEquals("0", cell(9, 8).getStringCellValue());
        assertEquals("true", cell(9, 9).getStringCellValue());
        assertNullCell(9, 10);
        assertNullCell(9, 11);
        assertNullCell(9, 12);
        assertNullCell(9, 13);
        assertNullCell(9, 14);

        assertNullCell(10, 1);
        assertEquals(", true", cell(10, 2).getStringCellValue());
        assertEquals("4444", cell(10, 3).getStringCellValue());
        assertEquals("false", cell(10, 4).getStringCellValue());
        assertEquals("4444", cell(10, 5).getStringCellValue());
        assertEquals("true", cell(10, 6).getStringCellValue());
        assertEquals("X", cell(10, 7).getStringCellValue());
        assertEquals("121", cell(10, 8).getStringCellValue());
        assertEquals("true", cell(10, 9).getStringCellValue());
        assertEquals("X", cell(10, 10).getStringCellValue());
        assertEquals("\"log this\", \"and this\"", cell(10, 11).getStringCellValue());
        assertEquals("X", cell(10, 12).getStringCellValue());
        assertEquals("123", cell(10, 13).getStringCellValue());
        assertNullCell(10, 14);
    }
}