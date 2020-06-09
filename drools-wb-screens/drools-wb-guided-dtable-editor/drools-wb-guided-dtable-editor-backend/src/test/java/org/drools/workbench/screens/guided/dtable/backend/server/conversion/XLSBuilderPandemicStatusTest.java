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

public class XLSBuilderPandemicStatusTest
        extends TestBase {

    @BeforeClass
    public static void setUp() throws Exception {
        final String xml = loadResource(XLSBuilderAttributesNegateTest.class.getResourceAsStream("PandemicStatus.gdst"));

        final GuidedDecisionTable52 dtable = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final XLSBuilder.BuildResult buildResult = new XLSBuilder(dtable, makeDMO()).build();
        final Workbook workbook = buildResult.getWorkbook();

        assertEquals(1, workbook.getNumberOfSheets());
        sheet = workbook.iterator().next();
    }

    @Test
    public void columnTypes() {

        assertEquals("CONDITION", cell(5, 1).getStringCellValue());
        assertEquals("CONDITION", cell(5, 2).getStringCellValue());
        assertEquals("CONDITION", cell(5, 3).getStringCellValue());
        assertEquals("CONDITION", cell(5, 4).getStringCellValue());
        assertEquals("ACTION", cell(5, 5).getStringCellValue());
        assertEquals("ACTION", cell(5, 6).getStringCellValue());
        assertNullCell(5, 7);
    }

    @Test
    public void patterns() {

        assertEquals("$v : Virus", cell(6, 1).getStringCellValue().trim());
        assertEquals("$v : Virus", cell(6, 2).getStringCellValue().trim());
        assertEquals("$v : Virus", cell(6, 3).getStringCellValue().trim());
        assertEquals("$v : Virus", cell(6, 4).getStringCellValue().trim());
        assertNullCell(6, 5);
        assertNullCell(6, 6);
    }

    @Test
    public void constraints() {
        assertEquals("infectedPeople > $param", cell(7, 1).getStringCellValue().trim());
        assertEquals("infectedPeople < $param", cell(7, 2).getStringCellValue().trim());
        assertEquals("severity == $param", cell(7, 3).getStringCellValue().trim());
        assertEquals("stillActive == $param", cell(7, 4).getStringCellValue().trim());
        assertEquals("Message brlColumnFact0 = new Message(); insert( brlColumnFact0 );",
                     cell(7, 5).getStringCellValue().trim());
        assertEquals("brlColumnFact0.setResult( $param );",
                     cell(7, 6).getStringCellValue().trim());
    }

    @Test
    public void columnTitles() {

        assertEquals("virus is like", cell(8, 1).getStringCellValue());
        assertEquals("virus is like", cell(8, 2).getStringCellValue());
        assertEquals("virus is like", cell(8, 3).getStringCellValue());
        assertEquals("virus is like", cell(8, 4).getStringCellValue());
        assertEquals("", cell(8, 5).getStringCellValue());
        assertEquals("then the state is", cell(8, 6).getStringCellValue());
    }

    @Test
    public void content() {

        assertEquals("0.0", cell(9, 1).getStringCellValue());
        assertEquals("0.0", cell(10, 1).getStringCellValue());

        assertEquals("10000.0", cell(9, 2).getStringCellValue());
        assertEquals("10000.0", cell(10, 2).getStringCellValue());

        assertEquals("\"low\"", cell(9, 3).getStringCellValue());
        assertEquals("\"high\"", cell(10, 3).getStringCellValue());

        assertEquals("true", cell(9, 4).getStringCellValue());
        assertEquals("true", cell(10, 4).getStringCellValue());

        assertEquals("X", cell(9, 5).getStringCellValue());
        assertEquals("X", cell(10, 5).getStringCellValue());

        assertEquals("\"safe\"", cell(9, 6).getStringCellValue());
        assertEquals("\"dangerous\"", cell(10, 6).getStringCellValue());
    }
}