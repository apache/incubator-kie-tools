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

public class XLSBuilderWorkItemTest
        extends TestBase {

    @BeforeClass
    public static void setUp() throws Exception {
        final String xml = loadResource(XLSBuilderWorkItemTest.class.getResourceAsStream("WorkItem.gdst"));

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

        assertEquals("RuleTable WorkItem", cell(4, 1).getStringCellValue());
    }

    @Test
    public void patterns() {

        assertEquals("a : Applicant", cell(6, 1).getStringCellValue());
        assertNullCell(6, 2);
        assertNullCell(6, 3);
        assertNullCell(6, 4);
        assertNullCell(6, 5);
    }

    @Test
    public void constraints() {

        assertEquals("approved == $param", cell(7, 1).getStringCellValue());
        assertEquals("org.drools.core.process.instance.WorkItemManager wiMyTaskManager = (org.drools.core.process.instance.WorkItemManager) drools.getWorkingMemory().getWorkItemManager();\n" +
                             "org.drools.core.process.instance.impl.WorkItemImpl wiMyTaskParameter = new org.drools.core.process.instance.impl.WorkItemImpl();\n" +
                             "wiMyTaskParameter.setName( \"MyTask\" );\n" +
                             "wiMyTaskParameter.getParameters().put(\"MyThirdParam\", a);\n" +
                             "wiMyTaskParameter.getParameters().put(\"MySecondParam\", \"SecondParam\");\n" +
                             "wiMyTaskParameter.getParameters().put(\"MyFirstParam\", \"FirstParam\");\n" +
                             "wiMyTaskManager.internalExecuteWorkItem( wiMyTaskParameter );", cell(7, 2).getStringCellValue());
        assertEquals("a.setName( (String) wiMyTaskParameter.getResult( \"Result\" ) );", cell(7, 3).getStringCellValue());
        assertEquals("org.drools.core.process.instance.WorkItemManager wiMyOtherTaskManager = (org.drools.core.process.instance.WorkItemManager) drools.getWorkingMemory().getWorkItemManager();\n" +
                             "org.drools.core.process.instance.impl.WorkItemImpl wiMyOtherTaskParameter = new org.drools.core.process.instance.impl.WorkItemImpl();\n" +
                             "wiMyOtherTaskParameter.setName( \"MyOtherTask\" );\n" +
                             "wiMyOtherTaskParameter.getParameters().put(\"MyOtherFirstParam\", \"theParamValue\");\n" +
                             "wiMyOtherTaskManager.internalExecuteWorkItem( wiMyOtherTaskParameter );", cell(7, 4).getStringCellValue());
        assertNullCell(7, 5);
    }

    @Test
    public void columnTitles() {

        assertEquals("Applicant Header", cell(8, 1).getStringCellValue());
        assertEquals("WorkItem Header", cell(8, 2).getStringCellValue());
        assertEquals("Use Work Item Result", cell(8, 3).getStringCellValue());
        assertEquals("Other WI", cell(8, 4).getStringCellValue());
        assertNullCell(8, 5);
    }

    @Test
    public void content() {

        assertEquals("true", cell(9, 1).getStringCellValue());
        assertEquals("X", cell(9, 2).getStringCellValue());
        assertEquals("X", cell(9, 3).getStringCellValue());
        assertEquals("X", cell(9, 4).getStringCellValue());
        assertNullCell(9, 5);

        assertEquals("true", cell(10, 1).getStringCellValue());
        assertNullCell(10, 2);
        assertNullCell(10, 3);
        assertNullCell(10, 4);
        assertNullCell(10, 5);
    }
}