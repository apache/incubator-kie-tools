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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public abstract class TestBase {

    protected static Sheet sheet;

    public static PackageDataModelOracle makeDMO() {
        final PackageDataModelOracle dmo = mock(PackageDataModelOracle.class);

        final HashMap<String, ModelField[]> map = new HashMap<>();

        map.put("mortgages.mortgages.LoanApplication", makeModelFieldsLoanApplication());
        map.put("mortgages.mortgages.IncomeSource", makeModelFieldsIncomeSource());
        map.put("mortgages.mortgages.Applicant", makeModelFieldsApplicantSource());

        doReturn(map).when(dmo).getModuleModelFields();

        return dmo;
    }

    private static ModelField[] makeModelFieldsApplicantSource() {
        final List<ModelField> modelFields = Arrays.asList(new ModelField("age",
                                                                          "java.lang.Integer",
                                                                          ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                                          FieldAccessorsAndMutators.BOTH,
                                                                          "Integer"),
                                                           new ModelField("name",
                                                                          "java.lang.String",
                                                                          ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                                          FieldAccessorsAndMutators.BOTH,
                                                                          "String"));

        return modelFields.toArray(new ModelField[modelFields.size()]);
    }

    private static ModelField[] makeModelFieldsLoanApplication() {
        final List<ModelField> modelFields = Arrays.asList(new ModelField("insuranceCost",
                                                                          "java.lang.Integer",
                                                                          ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                                          FieldAccessorsAndMutators.BOTH,
                                                                          "Integer"),
                                                           new ModelField("approved",
                                                                          "java.lang.Boolean",
                                                                          ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                                          FieldAccessorsAndMutators.BOTH,
                                                                          "Boolean"),
                                                           new ModelField("amount",
                                                                          "java.lang.Integer",
                                                                          ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                                          FieldAccessorsAndMutators.BOTH,
                                                                          "Integer"),
                                                           new ModelField("lengthYears",
                                                                          "java.lang.Integer",
                                                                          ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                                          FieldAccessorsAndMutators.BOTH,
                                                                          "Integer"),
                                                           new ModelField("deposit",
                                                                          "java.lang.Integer",
                                                                          ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                                          FieldAccessorsAndMutators.BOTH,
                                                                          "Integer"),
                                                           new ModelField("approvedRate",
                                                                          "java.lang.Integer",
                                                                          ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                                          FieldAccessorsAndMutators.BOTH,
                                                                          "Integer"),
                                                           new ModelField("explanation",
                                                                          "java.lang.String",
                                                                          ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                                          FieldAccessorsAndMutators.BOTH,
                                                                          "String"));

        return modelFields.toArray(new ModelField[modelFields.size()]);
    }

    private static ModelField[] makeModelFieldsIncomeSource() {
        final List<ModelField> modelFields = Arrays.asList(new ModelField("amount",
                                                                          "java.lang.Integer",
                                                                          ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                                          FieldAccessorsAndMutators.BOTH,
                                                                          "Integer"),
                                                           new ModelField("type",
                                                                          "java.lang.String",
                                                                          ModelField.FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS,
                                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                                          FieldAccessorsAndMutators.BOTH,
                                                                          "String"));

        return modelFields.toArray(new ModelField[modelFields.size()]);
    }

    protected void assertNullCell(final int y,
                                  final int x) {
        assertNull(sheet.getRow(y).getCell(x));
    }

    protected Cell cell(final int y,
                        final int x) {
        final Row row = sheet.getRow(y);
        return row.getCell(x);
    }
}
