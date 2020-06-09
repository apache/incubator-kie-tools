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

public class XLSBuilderCovidBRLTest
        extends TestBase {

    @BeforeClass
    public static void setUp() throws Exception {
        final String xml = loadResource(XLSBuilderAttributesNegateTest.class.getResourceAsStream("CovidBRL.gdst"));

        final GuidedDecisionTable52 dtable = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final XLSBuilder.BuildResult buildResult = new XLSBuilder(dtable, makeDMO()).build();
        final Workbook workbook = buildResult.getWorkbook();

        assertEquals(1, workbook.getNumberOfSheets());
        sheet = workbook.iterator().next();
    }

    @Test
    public void headers() {

        assertEquals("RuleSet", cell(1, 1).getStringCellValue());
        assertEquals("com.myspace.covid19", cell(1, 2).getStringCellValue());

        assertEquals("Import", cell(2, 1).getStringCellValue());
        assertEquals("", sheet.getRow(2).getCell(2).getStringCellValue());
        assertEquals("Declare", cell(3, 1).getStringCellValue());
        assertEquals("dialect \"mvel\";", sheet.getRow(3).getCell(2).getStringCellValue());

        assertEquals("RuleTable repatriation", cell(4, 1).getStringCellValue());
    }

    @Test
    public void columnTypes() {

        assertEquals("CONDITION", cell(5, 1).getStringCellValue());
        assertEquals("CONDITION", cell(5, 2).getStringCellValue());
        assertEquals("CONDITION", cell(5, 3).getStringCellValue());
        assertEquals("ACTION", cell(5, 4).getStringCellValue());
        assertEquals("ACTION", cell(5, 5).getStringCellValue());
        assertNullCell(5, 6);
    }

    @Test
    public void patterns() {

        assertEquals("$r : Repatriant", cell(6, 1).getStringCellValue().trim());
        assertEquals("$r : Repatriant", cell(6, 2).getStringCellValue().trim());
        assertNullCell(6, 3);
        assertNullCell(6, 4);
        assertNullCell(6, 5);
        assertNullCell(6, 6);
    }

    @Test
    public void constraints() {

        assertEquals("age > $param", cell(7, 1).getStringCellValue().trim());
        assertEquals("fromCountry == $param", cell(7, 2).getStringCellValue().trim());
        assertEquals("Covid19Test( personId == $r.personalId , positiveResult == $1 )", cell(7, 3).getStringCellValue().trim());
        assertEquals("$r.setCanCrossBorders( $param );", cell(7, 4).getStringCellValue().trim());
        assertEquals("PoliceTransport brlColumnFact0 = new PoliceTransport();\n" +
                             "\t\tbrlColumnFact0.setPersonalId( $r.personalId );\n" +
                             "\t\tbrlColumnFact0.setWhere( \"$1\" );\n" +
                             "\t\tinsert( brlColumnFact0 );\n" +
                             "\t\tStateCaranteneBuilding brlColumnFact1 = new StateCaranteneBuilding();\n" +
                             "\t\tbrlColumnFact1.setRoomPricePerNight( $2 );\n" +
                             "\t\tinsert( brlColumnFact1 );", cell(7, 5).getStringCellValue().trim());
         assertNullCell(7, 6);
    }

    @Test
    public void columnTitles() {

        assertEquals("repatriant is", cell(8, 1).getStringCellValue());
        assertEquals("repatriant is", cell(8, 2).getStringCellValue());
        assertEquals("has test", cell(8, 3).getStringCellValue());
        assertEquals("can cross borders", cell(8, 4).getStringCellValue());
        assertEquals("transport it", cell(8, 5).getStringCellValue());
        assertNullCell(8, 6);
    }

    @Test
    public void content() {

        assertEquals("3", cell(9, 1).getStringCellValue());
        assertEquals("\"UK\"", cell(9, 2).getStringCellValue());
        assertEquals("false", cell(9, 3).getStringCellValue());
        assertEquals("true", cell(9, 4).getStringCellValue());
        assertNullCell(9, 5);
        assertNullCell(9, 6);

        assertNullCell(10, 1);
        assertEquals("\"UK\"", cell(10, 2).getStringCellValue());
        assertEquals("true", cell(10, 3).getStringCellValue());
        assertEquals("false", cell(10, 4).getStringCellValue());
        assertEquals("BA, 10", cell(10, 5).getStringCellValue());
        assertNullCell(10, 6);
    }
}