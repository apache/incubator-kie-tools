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

import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.api.Assertions;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.shared.XLSConversionResultMessage;
import org.drools.workbench.screens.guided.dtable.shared.XLSConversionResultMessageType;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.drools.workbench.screens.guided.dtable.backend.server.util.TestUtil.loadResource;
import static org.junit.Assert.assertEquals;

public class XLSBuilderTypesTableTest
        extends TestBase {

    @BeforeClass
    public static void setUp() throws Exception {
        final String xml = loadResource(XLSBuilderAttributesNegateTest.class.getResourceAsStream("TypesTable.gdst"));

        final GuidedDecisionTable52 dtable = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final XLSBuilder.BuildResult buildResult = new XLSBuilder(dtable, makeDMO()).build();
        final Workbook workbook = buildResult.getWorkbook();

        final Set<XLSConversionResultMessage> infoMessages = buildResult.getConversionResult().getInfoMessages();

        assertEquals(2, infoMessages.size());
        Assertions.assertThat(infoMessages)
                .hasSize(2)
                .containsOnlyOnce(new XLSConversionResultMessage(XLSConversionResultMessageType.DIALECT_NOT_CONVERTED,
                                                                 "Dialect is not a supported column type in XLS Decision tables. Conversion ignored this column."))
                .containsOnlyOnce(new XLSConversionResultMessage(XLSConversionResultMessageType.RULE_NAME_NOT_CONVERTED,
                                                                 "Rule Name column conversion is not supported yet. Conversion ignored this column."));

        assertEquals(1, workbook.getNumberOfSheets());
        sheet = workbook.iterator().next();
    }

    @Test
    public void headers() {

        assertEquals("RuleSet", cell(1, 1).getStringCellValue());
        assertEquals("com.myspace.test", cell(1, 2).getStringCellValue());

        assertEquals("Import", cell(2, 1).getStringCellValue());
        assertEquals("", sheet.getRow(2).getCell(2).getStringCellValue());

        assertEquals("RuleTable TypesTable", cell(4, 1).getStringCellValue());
    }

    @Test
    public void columnTypes() {

        assertEquals("CONDITION", cell(5, 1).getStringCellValue());
        assertEquals("CONDITION", cell(5, 2).getStringCellValue());
        assertEquals("CONDITION", cell(5, 3).getStringCellValue());
        assertEquals("CONDITION", cell(5, 4).getStringCellValue());
        assertEquals("CONDITION", cell(5, 5).getStringCellValue());
        assertEquals("CONDITION", cell(5, 6).getStringCellValue());
        assertEquals("CONDITION", cell(5, 7).getStringCellValue());
        assertEquals("CONDITION", cell(5, 8).getStringCellValue());
        assertEquals("ACTION", cell(5, 9).getStringCellValue());
        assertEquals("ACTION", cell(5, 10).getStringCellValue());
        assertEquals("ACTION", cell(5, 11).getStringCellValue());
        assertEquals("ACTION", cell(5, 12).getStringCellValue());
        assertEquals("ACTION", cell(5, 13).getStringCellValue());
        assertEquals("ACTION", cell(5, 14).getStringCellValue());
        assertEquals("ACTION", cell(5, 15).getStringCellValue());
        assertEquals("ACTION", cell(5, 16).getStringCellValue());
        assertEquals("ACTION", cell(5, 17).getStringCellValue());
        assertNullCell(5, 18);
    }

    @Test
    public void patterns() {

        assertEquals("tf2 : TypesFact", cell(6, 1).getStringCellValue());
        assertEquals("tf2 : TypesFact", cell(6, 2).getStringCellValue());
        assertEquals("tf2 : TypesFact", cell(6, 3).getStringCellValue());
        assertEquals("tf2 : TypesFact", cell(6, 4).getStringCellValue());
        assertEquals("tf2 : TypesFact", cell(6, 5).getStringCellValue());
        assertEquals("tf2 : TypesFact", cell(6, 6).getStringCellValue());
        assertEquals("tf2 : TypesFact", cell(6, 7).getStringCellValue());
        assertEquals("tf2 : TypesFact", cell(6, 8).getStringCellValue());
        assertNullCell(6, 9);
        assertNullCell(6, 10);
    }

    @Test
    public void constraints() {

        assertEquals("theBigDecimal == $paramB", cell(7, 1).getStringCellValue().trim());
        assertEquals("theBigInteger == $paramI", cell(7, 2).getStringCellValue().trim());
        assertEquals("thedouble == $param", cell(7, 3).getStringCellValue().trim());
        assertEquals("thedoubleobject == $param", cell(7, 4).getStringCellValue().trim());
        assertEquals("thefloat == $param", cell(7, 5).getStringCellValue().trim());
        assertEquals("thefloatobject == $param", cell(7, 6).getStringCellValue().trim());
        assertEquals("thelong == $param", cell(7, 7).getStringCellValue().trim());
        assertEquals("thelongobject == $param", cell(7, 8).getStringCellValue().trim());
        assertEquals("TypesFact tf = new TypesFact(); insert( tf );", cell(7, 9).getStringCellValue().trim());
        assertEquals("tf.setTheBigDecimal( new java.math.BigDecimal(\"$param\") );", cell(7, 10).getStringCellValue().trim());
        assertEquals("tf.setTheBigInteger( new java.math.BigInteger(\"$param\") );", cell(7, 11).getStringCellValue().trim());
        assertEquals("tf.setThedouble( $paramd );", cell(7, 12).getStringCellValue().trim());
        assertEquals("tf.setThedoubleobject( $paramd );", cell(7, 13).getStringCellValue().trim());
        assertEquals("tf.setThefloat( $paramf );", cell(7, 14).getStringCellValue().trim());
        assertEquals("tf.setThefloatobject( $paramf );", cell(7, 15).getStringCellValue().trim());
        assertEquals("tf.setThelong( $paramL );", cell(7, 16).getStringCellValue().trim());
        assertEquals("tf.setThelongobject( $paramL );", cell(7, 17).getStringCellValue().trim());
        assertNullCell(7, 18);
    }

    @Test
    public void columnTitles() {

        assertEquals("a", cell(8, 1).getStringCellValue());
        assertEquals("BigInteger", cell(8, 2).getStringCellValue());
        assertEquals("double", cell(8, 3).getStringCellValue());
        assertEquals("Double Object", cell(8, 4).getStringCellValue());
        assertEquals("Float", cell(8, 5).getStringCellValue());
        assertEquals("Float Object", cell(8, 6).getStringCellValue());
        assertEquals("Long", cell(8, 7).getStringCellValue());
        assertEquals("Long Object", cell(8, 8).getStringCellValue());

        assertEquals("", cell(8, 9).getStringCellValue());
        assertEquals("decimal", cell(8, 10).getStringCellValue());
        assertEquals("integer", cell(8, 11).getStringCellValue());
        assertEquals("double", cell(8, 12).getStringCellValue());
        assertEquals("double object", cell(8, 13).getStringCellValue());
        assertEquals("float", cell(8, 14).getStringCellValue());
        assertEquals("float object", cell(8, 15).getStringCellValue());
        assertEquals("long", cell(8, 16).getStringCellValue());
        assertEquals("long object", cell(8, 17).getStringCellValue());
        assertNullCell(8, 18);
    }

    @Test
    public void content() {

        assertEquals("1", cell(9, 1).getStringCellValue());
        assertEquals("2", cell(9, 2).getStringCellValue());
        assertEquals("3.0", cell(9, 3).getStringCellValue());
        assertEquals("4.0", cell(9, 4).getStringCellValue());
        assertEquals("5.0", cell(9, 5).getStringCellValue());
        assertEquals("6.0", cell(9, 6).getStringCellValue());
        assertEquals("7", cell(9, 7).getStringCellValue());
        assertEquals("8", cell(9, 8).getStringCellValue());
        assertEquals("X", cell(9, 9).getStringCellValue());
        assertEquals("1", cell(9, 10).getStringCellValue());
        assertEquals("2", cell(9, 11).getStringCellValue());
        assertEquals("3.0", cell(9, 12).getStringCellValue());
        assertEquals("4.0", cell(9, 13).getStringCellValue());
        assertEquals("5.0", cell(9, 14).getStringCellValue());
        assertEquals("6.0", cell(9, 15).getStringCellValue());
        assertEquals("7", cell(9, 16).getStringCellValue());
        assertEquals("8", cell(9, 17).getStringCellValue());
        assertNullCell(9, 18);
    }
}