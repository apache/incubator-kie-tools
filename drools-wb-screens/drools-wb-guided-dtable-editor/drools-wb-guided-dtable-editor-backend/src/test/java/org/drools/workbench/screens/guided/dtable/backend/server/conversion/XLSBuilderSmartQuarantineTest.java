/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

public class XLSBuilderSmartQuarantineTest
        extends TestBase {

    @BeforeClass
    public static void setUp() throws Exception {
        final String xml = loadResource(XLSBuilderAttributesNegateTest.class.getResourceAsStream("SmartQuarantine.gdst"));

        final GuidedDecisionTable52 dtable = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final XLSBuilder.BuildResult buildResult = new XLSBuilder(dtable, makeDMO()).build();
        final Workbook workbook = buildResult.getWorkbook();

        assertEquals(1, workbook.getNumberOfSheets());
        sheet = workbook.iterator().next();
    }

    @Test
    public void columnTypes() {

        assertEquals("RULEFLOW-GROUP", cell(5, 1).getStringCellValue());
        assertEquals("CONDITION", cell(5, 2).getStringCellValue());
        assertEquals("CONDITION", cell(5, 3).getStringCellValue());
        assertEquals("ACTION", cell(5, 4).getStringCellValue());
        assertEquals("ACTION", cell(5, 5).getStringCellValue());
        assertNullCell(5, 6);
    }

    @Test
    public void patterns() {

        assertNullCell(6, 1);
        assertNullCell(6, 2);
        assertNullCell(6, 3);
        assertNullCell(6, 4);
        assertNullCell(6, 5);
    }

    @Test
    public void constraints() {

        assertNullCell(7, 1);
        assertEquals("$r : Repatriant( age < $1 , canCrossBorders == true , homeDistrict == \"$2\" )", cell(7, 2).getStringCellValue().trim());
        assertEquals("$pt : PoliceTransport( personalId == $r.personalId )", cell(7, 3).getStringCellValue().trim());
        assertEquals("$r.setSmartCarantene( $param );", cell(7, 4).getStringCellValue().trim());
        assertEquals("retract( $pt );", cell(7, 5).getStringCellValue().trim());
        assertNullCell(7, 6);
    }

    @Test
    public void columnTitles() {

        assertNullCell(8, 1);
        assertEquals("repatriant is like", cell(8, 2).getStringCellValue());
        assertEquals("police transport ordered", cell(8, 3).getStringCellValue());
        assertEquals("enable smart quarantine", cell(8, 4).getStringCellValue());
        assertEquals("cancel ordered transport", cell(8, 5).getStringCellValue());
        assertNullCell(8, 6);
    }

    @Test
    public void content() {

        assertEquals("transition to smart", cell(9, 1).getStringCellValue());
        assertEquals("50, west", cell(9, 2).getStringCellValue());
        assertEquals("X", cell(9, 3).getStringCellValue());
        assertEquals("true", cell(9, 4).getStringCellValue());
        assertEquals("X", cell(9, 5).getStringCellValue());
        assertNullCell(9, 6);
    }
}