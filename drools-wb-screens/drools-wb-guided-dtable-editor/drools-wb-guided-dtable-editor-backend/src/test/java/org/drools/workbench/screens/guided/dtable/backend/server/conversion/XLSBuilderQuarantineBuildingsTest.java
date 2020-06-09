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
import static org.junit.Assert.assertNull;

public class XLSBuilderQuarantineBuildingsTest
        extends TestBase {

    @BeforeClass
    public static void setUp() throws Exception {
        final String xml = loadResource(XLSBuilderAttributesNegateTest.class.getResourceAsStream("QuarantineBuildings.gdst"));

        final GuidedDecisionTable52 dtable = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final XLSBuilder.BuildResult buildResult = new XLSBuilder(dtable, makeDMO()).build();
        final Workbook workbook = buildResult.getWorkbook();

        assertEquals(1, workbook.getNumberOfSheets());
        sheet = workbook.iterator().next();
    }

    @Test
    public void columnTypes() {

        assertEquals("CONDITION", cell(5, 1).getStringCellValue());
        assertEquals("ACTION", cell(5, 2).getStringCellValue());
        assertEquals("ACTION", cell(5, 3).getStringCellValue());
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

        assertEquals("$westBuilding : StateCaranteneBuilding( district == \"west\" , isFull == $1 )\n" +
                             "\t\t$eastBuilding : StateCaranteneBuilding( district == \"east\" , isFull == $2 )",
                     cell(7, 1).getStringCellValue().trim());
        assertEquals("PoliceTransport brlColumnFact0 = new PoliceTransport(); insert( brlColumnFact0 );",
                     cell(7, 2).getStringCellValue().trim());
        assertEquals("brlColumnFact0.setWhere( $param );",
                     cell(7, 3).getStringCellValue().trim());
        assertEquals("modify( $westBuilding ) {\n" +
                             "\t\t\t\tsetRoomPricePerNight( $westBuilding.roomPricePerNight / 2 )\n" +
                             "\t\t}",
                     cell(7, 4).getStringCellValue().trim());
        assertEquals("modify( $eastBuilding ) {\n" +
                             "\t\t\t\tsetRoomPricePerNight( $eastBuilding.roomPricePerNight / 2 )\n" +
                             "\t\t}",
                     cell(7, 5).getStringCellValue().trim());
    }

    @Test
    public void columnTitles() {

        assertEquals("capacities of buildings", cell(8, 1).getStringCellValue());
        assertEquals("", cell(8, 2).getStringCellValue());
        assertEquals("transport from full", cell(8, 3).getStringCellValue());
        assertEquals("make discount for west", cell(8, 4).getStringCellValue());
        assertEquals("make discount for east", cell(8, 5).getStringCellValue());
    }

    @Test
    public void content() {

        assertEquals("true, false", cell(9, 1).getStringCellValue());
        assertEquals("false, true", cell(10, 1).getStringCellValue());

        assertEquals("X", cell(9, 2).getStringCellValue());
        assertEquals("X", cell(10, 2).getStringCellValue());

        assertEquals("\"east\"", cell(9, 3).getStringCellValue());
        assertEquals("\"west\"", cell(10, 3).getStringCellValue());

        assertEquals("X", cell(9, 4).getStringCellValue());
        assertNull(cell(10, 4));

        assertNull(cell(9, 5));
        assertEquals("X", cell(10, 5).getStringCellValue());
    }
}