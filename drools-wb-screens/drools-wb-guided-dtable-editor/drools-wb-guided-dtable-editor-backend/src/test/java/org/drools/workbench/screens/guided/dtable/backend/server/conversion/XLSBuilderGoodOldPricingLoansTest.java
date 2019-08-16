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

public class XLSBuilderGoodOldPricingLoansTest
        extends TestBase {

    @BeforeClass
    public static void setUp() throws Exception {
        final String xml = loadResource(XLSBuilderGoodOldPricingLoansTest.class.getResourceAsStream("Pricing loans.gdst"));

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

        assertEquals("RuleTable Pricing loans", cell(4, 1).getStringCellValue());
    }

    @Test
    public void patterns() {

        assertEquals("application : LoanApplication", cell(6, 1).getStringCellValue());
        assertEquals("application : LoanApplication", cell(6, 2).getStringCellValue());
        assertEquals("application : LoanApplication", cell(6, 3).getStringCellValue());
        assertEquals("application : LoanApplication", cell(6, 4).getStringCellValue());
        assertEquals("income : IncomeSource", cell(6, 5).getStringCellValue());
        assertNullCell(6, 6);
        assertNullCell(6, 7);
        assertNullCell(6, 8);
    }

    @Test
    public void constraints() {

        assertEquals("amount > $param", cell(7, 1).getStringCellValue());
        assertEquals("amount <= $param", cell(7, 2).getStringCellValue());
        assertEquals("lengthYears == $param", cell(7, 3).getStringCellValue());
        assertEquals("deposit < $param", cell(7, 4).getStringCellValue());
        assertEquals("type == $param", cell(7, 5).getStringCellValue());
        assertEquals("application.setApproved( $param );", cell(7, 6).getStringCellValue());
        assertEquals("application.setInsuranceCost( $param );", cell(7, 7).getStringCellValue());
        assertEquals("application.setApprovedRate( $param );", cell(7, 8).getStringCellValue());
    }

    @Test
    public void columnTitles() {

        assertEquals("amount min", cell(8, 1).getStringCellValue());
        assertEquals("amount max", cell(8, 2).getStringCellValue());
        assertEquals("period", cell(8, 3).getStringCellValue());
        assertEquals("deposit max", cell(8, 4).getStringCellValue());
        assertEquals("income", cell(8, 5).getStringCellValue());
        assertEquals("Loan approved", cell(8, 6).getStringCellValue());
        assertEquals("LMI", cell(8, 7).getStringCellValue());
        assertEquals("rate", cell(8, 8).getStringCellValue());
    }

    @Test
    public void content() {

        assertEquals("131000", cell(9, 1).getStringCellValue());
        assertEquals("200000", cell(9, 2).getStringCellValue());
        assertEquals("30", cell(9, 3).getStringCellValue());
        assertEquals("20000", cell(9, 4).getStringCellValue());
        assertEquals("\"Asset\"", cell(9, 5).getStringCellValue());
        assertEquals("true", cell(9, 6).getStringCellValue());
        assertEquals("0", cell(9, 7).getStringCellValue());
        assertEquals("2", cell(9, 8).getStringCellValue());
    }
}