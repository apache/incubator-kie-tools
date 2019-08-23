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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;

import static org.drools.workbench.screens.guided.dtable.backend.server.util.TestUtil.loadResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class XLSBuilderCarsInCityTest
        extends TestBase {

    private String oldDateFormatValue;
    private String oldLanguageValue;

    @BeforeClass
    public static void setUpBefore() throws Exception {
        final String xml = loadResource(XLSBuilderCarsInCityTest.class.getResourceAsStream("cars in city.gdst"));

        final GuidedDecisionTable52 dtable = GuidedDTXMLPersistence.getInstance().unmarshal(xml);

        final XLSBuilder.BuildResult buildResult = new XLSBuilder(dtable, makeDMO()).build();
        final Workbook workbook = buildResult.getWorkbook();

        assertEquals(1, workbook.getNumberOfSheets());
        sheet = workbook.iterator().next();
    }

    public static PackageDataModelOracle makeDMO() {
        final PackageDataModelOracle dmo = mock(PackageDataModelOracle.class);

        final HashMap<String, ModelField[]> map = new HashMap<>();

        map.put("org.kie.example.traffic.Route", makeModelFieldsRoute());
        map.put("org.kie.example.traffic.Violation", makeModelFieldsViolation());

        doReturn(map).when(dmo).getModuleModelFields();

        return dmo;
    }

    private static ModelField[] makeModelFieldsRoute() {
        final List<ModelField> modelFields = Arrays.asList(new ModelField("passengers",
                                                                          "java.lang.Integer",
                                                                          ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                                          FieldAccessorsAndMutators.BOTH,
                                                                          "Integer"));

        return modelFields.toArray(new ModelField[modelFields.size()]);
    }

    private static ModelField[] makeModelFieldsViolation() {
        final List<ModelField> modelFields = Arrays.asList(new ModelField("when",
                                                                          "java.util.Date",
                                                                          ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                                          FieldAccessorsAndMutators.BOTH,
                                                                          "Date"),
                                                           new ModelField("type",
                                                                          "java.lang.String",
                                                                          ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                                          FieldAccessorsAndMutators.BOTH,
                                                                          "String"));

        return modelFields.toArray(new ModelField[modelFields.size()]);
    }

    @Before
    public void setUp() throws Exception {

        oldDateFormatValue = System.getProperty("drools.dateformat");
        oldLanguageValue = System.getProperty("drools.defaultlanguage");

        System.setProperty("drools.dateformat",
                           "dd-MMM-yyyy");
        System.setProperty("drools.defaultlanguage",
                           "fr_FR");
    }

    @After
    public void tearDown() throws Exception {

        if (oldDateFormatValue == null) {
            System.clearProperty("drools.dateformat");
        } else {
            System.setProperty("drools.dateformat",
                               oldDateFormatValue);
        }

        if (oldLanguageValue == null) {
            System.clearProperty("drools.defaultlanguage");
        } else {
            System.setProperty("drools.defaultlanguage",
                               oldLanguageValue);
        }
    }

    @Test
    public void content() {

        assertEquals("4", cell(9, 1).getStringCellValue());
        assertEquals("X", cell(9, 2).getStringCellValue());
        assertEquals("\"it is fine\"", cell(9, 3).getStringCellValue());
        assertEquals("\"09-Aug-2019\"", cell(9, 4).getStringCellValue());
    }
}