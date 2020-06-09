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

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.workbench.screens.guided.dtable.backend.server.util.TestUtil.loadResource;
import static org.junit.Assert.assertEquals;

public class XLSBuilderVirusStateTest
        extends TestBase {

    @BeforeClass
    public static void setUp() throws Exception {
        final String xml = loadResource(XLSBuilderAttributesNegateTest.class.getResourceAsStream("VirusState.gdst"));

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
        assertNullCell(5, 3);
    }

    @Test
    public void patterns() {

        assertNullCell(6, 1);
        assertNullCell(6, 2);
        assertNullCell(6, 3);
    }

    @Test
    public void constraints() {

        assertEquals("$v : Virus(name == \"$1\", infectedPeople < $2)\n" +
                             "\t\tVirus( name == ( $v.name ) , found < \"$3\" )",
                     cell(7, 1).getStringCellValue().trim());
        assertEquals("$v.setSeverity(\"$1\")",
                     cell(7, 2).getStringCellValue().trim());
        assertNullCell(7, 5);
    }

    @Test
    public void columnTitles() {

        assertEquals("what is the virus like", cell(8, 1).getStringCellValue());
        assertEquals("then set its severity", cell(8, 2).getStringCellValue());
    }

    @Test
    public void content() {

        assertThat(cell(9, 1).getStringCellValue())
                .startsWith("European, 10000.0, 0")
                .endsWith("-Jun-2020");
        assertThat(cell(10, 1).getStringCellValue())
                .startsWith("Asian, 100000.0, 0")
                .endsWith("-Jun-2020");
        assertThat(cell(11, 1).getStringCellValue())
                .startsWith("Asian, 10000.0, 0")
                .endsWith("-Jun-2020");

        assertEquals("low", cell(9, 2).getStringCellValue());
        assertEquals("medium", cell(10, 2).getStringCellValue());
        assertEquals("low", cell(11, 2).getStringCellValue());
    }
}