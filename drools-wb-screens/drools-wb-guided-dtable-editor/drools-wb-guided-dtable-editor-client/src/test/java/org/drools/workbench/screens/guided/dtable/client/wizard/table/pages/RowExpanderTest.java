/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.wizard.table.pages;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.imports.HasImports;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ModuleDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static org.drools.workbench.screens.guided.dtable.TestUtil.populate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class RowExpanderTest {

    @Mock
    protected IncrementalDataModelService incrementalDataModelService;
    protected Caller<IncrementalDataModelService> incrementalDataModelServiceCaller;

    @Mock
    protected Instance<DynamicValidator> validatorInstance;

    @Before
    public void setup() {
        incrementalDataModelServiceCaller = new CallerMock<>(incrementalDataModelService);
    }

    private AsyncPackageDataModelOracle getOracle() {
        return new AsyncPackageDataModelOracleImpl(incrementalDataModelServiceCaller,
                                                   validatorInstance);
    }

    @Test
    @SuppressWarnings("serial")
    public void testExpansionNoExpansion() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Driver")
                .addField(new ModelField("age",
                                         Integer.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_NUMERIC_INTEGER))
                .addField(new ModelField("name",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("dateOfBirth",
                                         Boolean.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_DATE))
                .addField(new ModelField("approved",
                                         Boolean.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_BOOLEAN))
                .end()
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("name");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        Pattern52 p2 = new Pattern52();
        p2.setBoundName("c2");
        p2.setFactType("Driver");

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("age");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p2.getChildColumns().add(c2);
        model.getConditions().add(p2);

        Pattern52 p3 = new Pattern52();
        p3.setBoundName("c3");
        p3.setFactType("Driver");

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField("dateOfBirth");
        c3.setOperator("==");
        c3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p3.getChildColumns().add(c3);
        model.getConditions().add(p3);

        Pattern52 p4 = new Pattern52();
        p4.setBoundName("c4");
        p4.setFactType("Driver");

        ConditionCol52 c4 = new ConditionCol52();
        c4.setFactField("approved");
        c4.setOperator("==");
        c4.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p4.getChildColumns().add(c4);
        model.getConditions().add(p4);

        ActionSetFieldCol52 a1 = new ActionSetFieldCol52();
        a1.setBoundName("c1");
        a1.setFactField("name");
        model.getActionCols().add(a1);

        ActionInsertFactCol52 a2 = new ActionInsertFactCol52();
        a2.setBoundName("a2");
        a2.setFactType("Driver");
        a2.setFactField("name");
        model.getActionCols().add(a2);

        RowExpander re = new RowExpander(model,
                                         oracle);

        List<RowExpander.ColumnValues> columns = re.getColumns();
        assertEquals(9,
                     columns.size());
        assertEquals(1,
                     columns.get(0).values.size());
        assertEquals(1,
                     columns.get(1).values.size());
        assertEquals(1,
                     columns.get(2).values.size());
        assertEquals(1,
                     columns.get(3).values.size());
        assertEquals(1,
                     columns.get(4).values.size());
        assertEquals(1,
                     columns.get(5).values.size());
        assertEquals(1,
                     columns.get(6).values.size());
        assertEquals(1,
                     columns.get(7).values.size());
        assertEquals(1,
                     columns.get(8).values.size());

        RowExpander.RowIterator ri = re.iterator();
        assertFalse(ri.hasNext());
    }

    @Test
    @SuppressWarnings("serial")
    public void testExpansionWithValuesList() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Driver")
                .addField(new ModelField("age",
                                         Integer.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_NUMERIC_INTEGER))
                .addField(new ModelField("name",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("dateOfBirth",
                                         Boolean.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_DATE))
                .addField(new ModelField("approved",
                                         Boolean.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_BOOLEAN))
                .end()
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("name");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c1.setValueList("c1a,c1b");
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        Pattern52 p2 = new Pattern52();
        p2.setBoundName("c2");
        p2.setFactType("Driver");

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("age");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c2.setValueList("c2a,c2b");
        p2.getChildColumns().add(c2);
        model.getConditions().add(p2);

        Pattern52 p3 = new Pattern52();
        p3.setBoundName("c3");
        p3.setFactType("Driver");

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField("dateOfBirth");
        c3.setOperator("==");
        c3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c3.setValueList("c3a,c3b");
        p3.getChildColumns().add(c3);
        model.getConditions().add(p3);

        Pattern52 p4 = new Pattern52();
        p4.setBoundName("c4");
        p4.setFactType("Driver");

        ConditionCol52 c4 = new ConditionCol52();
        c4.setFactField("approved");
        c4.setOperator("==");
        c4.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c4.setValueList("c4a,c4b");
        p4.getChildColumns().add(c4);
        model.getConditions().add(p4);

        ActionSetFieldCol52 a1 = new ActionSetFieldCol52();
        a1.setBoundName("c1");
        a1.setFactField("name");
        a1.setValueList("a1a,a1b");
        model.getActionCols().add(a1);

        ActionInsertFactCol52 a2 = new ActionInsertFactCol52();
        a2.setBoundName("a2");
        a2.setFactType("Driver");
        a2.setFactField("name");
        a2.setValueList("a2a,a2b");
        model.getActionCols().add(a2);

        RowExpander re = new RowExpander(model,
                                         oracle);

        List<RowExpander.ColumnValues> columns = re.getColumns();
        assertEquals(9,
                     columns.size());

        assertEquals(1,
                     columns.get(0).values.size());
        assertEquals(1,
                     columns.get(1).values.size());
        assertEquals(1,
                     columns.get(2).values.size());
        assertEquals(2,
                     columns.get(3).values.size());
        assertEquals(2,
                     columns.get(4).values.size());
        assertEquals(2,
                     columns.get(5).values.size());
        assertEquals(2,
                     columns.get(6).values.size());
        assertEquals(1,
                     columns.get(7).values.size());
        assertEquals(1,
                     columns.get(8).values.size());

        assertEquals("c1a",
                     columns.get(3).values.get(0).getStringValue());
        assertEquals("c1b",
                     columns.get(3).values.get(1).getStringValue());

        assertEquals("c2a",
                     columns.get(4).values.get(0).getStringValue());
        assertEquals("c2b",
                     columns.get(4).values.get(1).getStringValue());

        assertEquals("c3a",
                     columns.get(5).values.get(0).getStringValue());
        assertEquals("c3b",
                     columns.get(5).values.get(1).getStringValue());

        assertEquals("c4a",
                     columns.get(6).values.get(0).getStringValue());
        assertEquals("c4b",
                     columns.get(6).values.get(1).getStringValue());

        assertNull(columns.get(7).values.get(0));

        assertNull(columns.get(8).values.get(0));

        RowExpander.RowIterator ri = re.iterator();
        assertTrue(ri.hasNext());
    }

    @Test
    public void testExpansionWithGuvnorEnums() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Driver")
                .addField(new ModelField("age",
                                         Integer.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_NUMERIC_INTEGER))
                .addField(new ModelField("name",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("dateOfBirth",
                                         Boolean.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_DATE))
                .addField(new ModelField("approved",
                                         Boolean.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_BOOLEAN))
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .setModuleOracle(projectLoader)
                .addEnum("'Driver.name' : ['f1a', 'f1b'], 'Driver.age' : ['f2a', 'f2b'], 'Driver.dateOfBirth' : ['f3a', 'f3b'], 'Driver.approved' : ['f4a', 'f4b']",
                         Thread.currentThread().getContextClassLoader())
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        dataModel.setWorkbenchEnumDefinitions(packageLoader.getPackageWorkbenchDefinitions());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("name");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        Pattern52 p2 = new Pattern52();
        p2.setBoundName("c2");
        p2.setFactType("Driver");

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("age");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p2.getChildColumns().add(c2);
        model.getConditions().add(p2);

        Pattern52 p3 = new Pattern52();
        p3.setBoundName("c3");
        p3.setFactType("Driver");

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField("dateOfBirth");
        c3.setOperator("==");
        c3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p3.getChildColumns().add(c3);
        model.getConditions().add(p3);

        Pattern52 p4 = new Pattern52();
        p4.setBoundName("c4");
        p4.setFactType("Driver");

        ConditionCol52 c4 = new ConditionCol52();
        c4.setFactField("approved");
        c4.setOperator("==");
        c4.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p4.getChildColumns().add(c4);
        model.getConditions().add(p4);

        ActionSetFieldCol52 a1 = new ActionSetFieldCol52();
        a1.setBoundName("c1");
        a1.setFactField("name");
        model.getActionCols().add(a1);

        ActionInsertFactCol52 a2 = new ActionInsertFactCol52();
        a2.setBoundName("a2");
        a2.setFactType("Driver");
        a2.setFactField("name");
        model.getActionCols().add(a2);

        RowExpander re = new RowExpander(model,
                                         oracle);

        List<RowExpander.ColumnValues> columns = re.getColumns();
        assertEquals(9,
                     columns.size());

        assertEquals(1,
                     columns.get(0).values.size());
        assertEquals(1,
                     columns.get(1).values.size());
        assertEquals(1,
                     columns.get(2).values.size());
        assertEquals(2,
                     columns.get(3).values.size());
        assertEquals(2,
                     columns.get(4).values.size());
        assertEquals(2,
                     columns.get(5).values.size());
        assertEquals(2,
                     columns.get(6).values.size());
        assertEquals(1,
                     columns.get(7).values.size());
        assertEquals(1,
                     columns.get(8).values.size());

        assertEquals("f1a",
                     columns.get(3).values.get(0).getStringValue());
        assertEquals("f1b",
                     columns.get(3).values.get(1).getStringValue());

        assertEquals("f2a",
                     columns.get(4).values.get(0).getStringValue());
        assertEquals("f2b",
                     columns.get(4).values.get(1).getStringValue());

        assertEquals("f3a",
                     columns.get(5).values.get(0).getStringValue());
        assertEquals("f3b",
                     columns.get(5).values.get(1).getStringValue());

        assertEquals("f4a",
                     columns.get(6).values.get(0).getStringValue());
        assertEquals("f4b",
                     columns.get(6).values.get(1).getStringValue());

        assertNull(columns.get(7).values.get(0));

        assertNull(columns.get(8).values.get(0));

        RowExpander.RowIterator ri = re.iterator();
        assertTrue(ri.hasNext());
    }

    @Test
    public void testExpansionWithGuvnorDependentEnums_2enum_x_3values() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final String enumDefinitions = "'Fact.field1' : ['f1a', 'f1b', 'f1c'], "
                + "'Fact.field2[field1=f1a]' : ['f1af2a', 'f1af2b', 'f1af2c'], "
                + "'Fact.field2[field1=f1b]' : ['f1bf2a', 'f1bf2b', 'f1bf2c'], "
                + "'Fact.field2[field1=f1c]' : ['f1cf2a', 'f1cf2b', 'f1cf2c']";

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Fact")
                .addField(new ModelField("field1",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("field2",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .setModuleOracle(projectLoader)
                .addEnum(enumDefinitions,
                         Thread.currentThread().getContextClassLoader())
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        dataModel.setWorkbenchEnumDefinitions(packageLoader.getPackageWorkbenchDefinitions());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("f1");
        p1.setFactType("Fact");
        model.getConditions().add(p1);

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("field1");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c1);

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("field2");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c2);

        ActionSetFieldCol52 a1 = new ActionSetFieldCol52();
        a1.setBoundName("f1");
        a1.setFactField("field1");
        model.getActionCols().add(a1);

        ActionInsertFactCol52 a2 = new ActionInsertFactCol52();
        a2.setBoundName("f2");
        a2.setFactType("Fact");
        a2.setFactField("field1");
        model.getActionCols().add(a2);

        RowExpander re = new RowExpander(model,
                                         oracle);

        List<RowExpander.ColumnValues> columns = re.getColumns();
        assertEquals(7,
                     columns.size());

        assertTrue(columns.get(0) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(1) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(2) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(3) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(4) instanceof RowExpander.ColumnDynamicValues);
        assertTrue(columns.get(5) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(6) instanceof RowExpander.ColumnValues);

        //Can't check size of values for ColumnDynamicValues as they depend on the other columns
        assertEquals(1,
                     columns.get(0).values.size());
        assertEquals(1,
                     columns.get(1).values.size());
        assertEquals(1,
                     columns.get(2).values.size());
        assertEquals(3,
                     columns.get(3).values.size());
        assertEquals(1,
                     columns.get(5).values.size());
        assertEquals(1,
                     columns.get(6).values.size());

        //Expected data
        // --> f1a, f1af2a
        // --> f1a, f1af2b
        // --> f1a, f1af2c
        // --> f1b, f1bf2a
        // --> f1b, f1bf2b
        // --> f1b, f1bf2c
        // --> f1c, f1cf2a
        // --> f1c, f1cf2b
        // --> f1c, f1cf2c

        RowExpander.RowIterator ri = re.iterator();
        assertTrue(ri.hasNext());
        List<DTCellValue52> row0 = ri.next();
        assertEquals(7,
                     row0.size());
        assertEquals("f1a",
                     row0.get(3).getStringValue());
        assertEquals("f1af2a",
                     row0.get(4).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row1 = ri.next();
        assertEquals(7,
                     row1.size());
        assertEquals("f1a",
                     row1.get(3).getStringValue());
        assertEquals("f1af2b",
                     row1.get(4).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row2 = ri.next();
        assertEquals(7,
                     row2.size());
        assertEquals("f1a",
                     row2.get(3).getStringValue());
        assertEquals("f1af2c",
                     row2.get(4).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row3 = ri.next();
        assertEquals(7,
                     row3.size());
        assertEquals("f1b",
                     row3.get(3).getStringValue());
        assertEquals("f1bf2a",
                     row3.get(4).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row4 = ri.next();
        assertEquals(7,
                     row4.size());
        assertEquals("f1b",
                     row4.get(3).getStringValue());
        assertEquals("f1bf2b",
                     row4.get(4).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row5 = ri.next();
        assertEquals(7,
                     row5.size());
        assertEquals("f1b",
                     row5.get(3).getStringValue());
        assertEquals("f1bf2c",
                     row5.get(4).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row6 = ri.next();
        assertEquals(7,
                     row6.size());
        assertEquals("f1c",
                     row6.get(3).getStringValue());
        assertEquals("f1cf2a",
                     row6.get(4).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row7 = ri.next();
        assertEquals(7,
                     row7.size());
        assertEquals("f1c",
                     row7.get(3).getStringValue());
        assertEquals("f1cf2b",
                     row7.get(4).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row8 = ri.next();
        assertEquals(7,
                     row8.size());
        assertEquals("f1c",
                     row8.get(3).getStringValue());
        assertEquals("f1cf2c",
                     row8.get(4).getStringValue());

        assertFalse(ri.hasNext());
    }

    @Test
    public void testExpansionWithGuvnorDependentEnumsExplicitExpansion1_2enum_x_2values() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final String enumDefinitions = "'Fact.field1' : ['f1a', 'f1b'], "
                + "'Fact.field2[field1=f1a]' : ['f1af2a', 'f1af2b'], "
                + "'Fact.field2[field1=f1b]' : ['f1bf2a', 'f1bf2b']";

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Fact")
                .addField(new ModelField("field1",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("field2",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .setModuleOracle(projectLoader)
                .addEnum(enumDefinitions,
                         Thread.currentThread().getContextClassLoader())
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        dataModel.setWorkbenchEnumDefinitions(packageLoader.getPackageWorkbenchDefinitions());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("f1");
        p1.setFactType("Fact");
        model.getConditions().add(p1);

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("field1");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c1);

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("field2");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c2);

        ActionSetFieldCol52 a1 = new ActionSetFieldCol52();
        a1.setBoundName("f1");
        a1.setFactField("field1");
        model.getActionCols().add(a1);

        ActionInsertFactCol52 a2 = new ActionInsertFactCol52();
        a2.setBoundName("f2");
        a2.setFactType("Fact");
        a2.setFactField("field1");
        model.getActionCols().add(a2);

        RowExpander re = new RowExpander(model,
                                         oracle);

        //Explicitly set which columns to expand
        for (ConditionCol52 c : p1.getChildColumns()) {
            re.setExpandColumn(c,
                               true);
        }

        List<RowExpander.ColumnValues> columns = re.getColumns();
        assertEquals(7,
                     columns.size());

        assertTrue(columns.get(0) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(1) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(2) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(3) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(4) instanceof RowExpander.ColumnDynamicValues);
        assertTrue(columns.get(5) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(6) instanceof RowExpander.ColumnValues);

        //Can't check size of values for ColumnDynamicValues as they depend on the other columns
        assertEquals(1,
                     columns.get(0).values.size());
        assertEquals(1,
                     columns.get(1).values.size());
        assertEquals(1,
                     columns.get(2).values.size());
        assertEquals(2,
                     columns.get(3).values.size());
        assertEquals(1,
                     columns.get(5).values.size());
        assertEquals(1,
                     columns.get(6).values.size());

        //Expected data
        // --> f1a, f1af2a
        // --> f1a, f1af2b
        // --> f1b, f1bf2a
        // --> f1b, f1bf2b

        RowExpander.RowIterator ri = re.iterator();
        assertTrue(ri.hasNext());
        List<DTCellValue52> row0 = ri.next();
        assertEquals(7,
                     row0.size());
        assertEquals("f1a",
                     row0.get(3).getStringValue());
        assertEquals("f1af2a",
                     row0.get(4).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row1 = ri.next();
        assertEquals(7,
                     row1.size());
        assertEquals("f1a",
                     row1.get(3).getStringValue());
        assertEquals("f1af2b",
                     row1.get(4).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row3 = ri.next();
        assertEquals(7,
                     row3.size());
        assertEquals("f1b",
                     row3.get(3).getStringValue());
        assertEquals("f1bf2a",
                     row3.get(4).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row4 = ri.next();
        assertEquals(7,
                     row4.size());
        assertEquals("f1b",
                     row4.get(3).getStringValue());
        assertEquals("f1bf2b",
                     row4.get(4).getStringValue());

        assertFalse(ri.hasNext());
    }

    @Test
    public void testExpansionWithGuvnorDependentEnumsExplicitExpansion2_2enum_x_2values() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final String enumDefinitions = "'Fact.field1' : ['f1a', 'f1b'], "
                + "'Fact.field2[field1=f1a]' : ['f1af2a', 'f1af2b'], "
                + "'Fact.field2[field1=f1b]' : ['f1bf2a', 'f1bf2b']";

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Fact")
                .addField(new ModelField("field1",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("field2",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .setModuleOracle(projectLoader)
                .addEnum(enumDefinitions,
                         Thread.currentThread().getContextClassLoader())
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        dataModel.setWorkbenchEnumDefinitions(packageLoader.getPackageWorkbenchDefinitions());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("f1");
        p1.setFactType("Fact");
        model.getConditions().add(p1);

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("field1");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c1);

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("field2");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c2);

        ActionSetFieldCol52 a1 = new ActionSetFieldCol52();
        a1.setBoundName("f1");
        a1.setFactField("field1");
        model.getActionCols().add(a1);

        ActionInsertFactCol52 a2 = new ActionInsertFactCol52();
        a2.setBoundName("f2");
        a2.setFactType("Fact");
        a2.setFactField("field1");
        model.getActionCols().add(a2);

        RowExpander re = new RowExpander(model,
                                         oracle);

        //Explicitly expand the first column, not the second
        re.setExpandColumn(c1,
                           true);
        re.setExpandColumn(c2,
                           false);

        List<RowExpander.ColumnValues> columns = re.getColumns();
        assertEquals(7,
                     columns.size());

        assertTrue(columns.get(0) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(1) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(2) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(3) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(4) instanceof RowExpander.ColumnDynamicValues);
        assertTrue(columns.get(5) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(6) instanceof RowExpander.ColumnValues);

        //Can't check size of values for ColumnDynamicValues as they depend on the other columns
        assertEquals(1,
                     columns.get(0).values.size());
        assertEquals(1,
                     columns.get(1).values.size());
        assertEquals(1,
                     columns.get(2).values.size());
        assertEquals(2,
                     columns.get(3).values.size());
        assertEquals(1,
                     columns.get(5).values.size());
        assertEquals(1,
                     columns.get(6).values.size());

        //Expected data
        // --> f1a, null
        // --> f1b, null

        RowExpander.RowIterator ri = re.iterator();
        assertTrue(ri.hasNext());
        List<DTCellValue52> row0 = ri.next();
        assertEquals(7,
                     row0.size());
        assertEquals("f1a",
                     row0.get(3).getStringValue());
        assertNull(row0.get(4));

        assertTrue(ri.hasNext());
        List<DTCellValue52> row1 = ri.next();
        assertEquals(7,
                     row1.size());
        assertEquals("f1b",
                     row1.get(3).getStringValue());
        assertNull(row1.get(4));

        assertFalse(ri.hasNext());
    }

    @Test
    public void testExpansionWithGuvnorDependentEnumsExplicitExpansion3_2enum_x_2values() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final String enumDefinitions = "'Fact.field1' : ['f1a', 'f1b'], "
                + "'Fact.field2[field1=f1a]' : ['f1af2a', 'f1af2b'], "
                + "'Fact.field2[field1=f1b]' : ['f1bf2a', 'f1bf2b']";

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Fact")
                .addField(new ModelField("field1",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("field2",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .setModuleOracle(projectLoader)
                .addEnum(enumDefinitions,
                         Thread.currentThread().getContextClassLoader())
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        dataModel.setWorkbenchEnumDefinitions(packageLoader.getPackageWorkbenchDefinitions());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("f1");
        p1.setFactType("Fact");
        model.getConditions().add(p1);

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("field1");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c1);

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("field2");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c2);

        ActionSetFieldCol52 a1 = new ActionSetFieldCol52();
        a1.setBoundName("f1");
        a1.setFactField("field1");
        model.getActionCols().add(a1);

        ActionInsertFactCol52 a2 = new ActionInsertFactCol52();
        a2.setBoundName("f2");
        a2.setFactType("Fact");
        a2.setFactField("field1");
        model.getActionCols().add(a2);

        RowExpander re = new RowExpander(model,
                                         oracle);

        //Explicitly expand the first column, not the second
        re.setExpandColumn(c1,
                           false);
        re.setExpandColumn(c2,
                           true);

        List<RowExpander.ColumnValues> columns = re.getColumns();
        assertEquals(7,
                     columns.size());

        assertTrue(columns.get(0) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(1) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(2) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(3) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(4) instanceof RowExpander.ColumnDynamicValues);
        assertTrue(columns.get(5) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(6) instanceof RowExpander.ColumnValues);

        //Can't check size of values for ColumnDynamicValues as they depend on the other columns
        assertEquals(1,
                     columns.get(0).values.size());
        assertEquals(1,
                     columns.get(1).values.size());
        assertEquals(1,
                     columns.get(2).values.size());
        assertEquals(1,
                     columns.get(3).values.size());
        assertEquals(1,
                     columns.get(5).values.size());
        assertEquals(1,
                     columns.get(6).values.size());

        //Expected data
        // --> null, null

        RowExpander.RowIterator ri = re.iterator();
        assertTrue(ri.hasNext());
        List<DTCellValue52> row0 = ri.next();
        assertEquals(7,
                     row0.size());
        assertNull(row0.get(3));
        assertNull(row0.get(4));

        assertFalse(ri.hasNext());
    }

    @Test
    public void testExpansionWithGuvnorDependentEnums_3enum_x_2values() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final String enumDefinitions = "'Fact.field1' : ['f1a', 'f1b'], "
                + "'Fact.field2[field1=f1a]' : ['f1af2a', 'f1af2b'], "
                + "'Fact.field2[field1=f1b]' : ['f1bf2a', 'f1bf2b'], "
                + "'Fact.field3[field2=f1af2a]' : ['f1af2af3a', 'f1af2af3b'], "
                + "'Fact.field3[field2=f1af2b]' : ['f1af2bf3a', 'f1af2bf3b'], "
                + "'Fact.field3[field2=f1bf2a]' : ['f1bf2af3a', 'f1bf2af3b'], "
                + "'Fact.field3[field2=f1bf2b]' : ['f1bf2bf3a', 'f1bf2bf3b']";

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Fact")
                .addField(new ModelField("field1",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("field2",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("field3",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .setModuleOracle(projectLoader)
                .addEnum(enumDefinitions,
                         Thread.currentThread().getContextClassLoader())
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        dataModel.setWorkbenchEnumDefinitions(packageLoader.getPackageWorkbenchDefinitions());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("f1");
        p1.setFactType("Fact");
        model.getConditions().add(p1);

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("field1");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c1);

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("field2");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c2);

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField("field3");
        c3.setOperator("==");
        c3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c3);

        ActionSetFieldCol52 a1 = new ActionSetFieldCol52();
        a1.setBoundName("f1");
        a1.setFactField("field1");
        model.getActionCols().add(a1);

        ActionInsertFactCol52 a2 = new ActionInsertFactCol52();
        a2.setBoundName("f2");
        a2.setFactType("Fact");
        a2.setFactField("field1");
        model.getActionCols().add(a2);

        RowExpander re = new RowExpander(model,
                                         oracle);

        List<RowExpander.ColumnValues> columns = re.getColumns();
        assertEquals(8,
                     columns.size());

        assertTrue(columns.get(0) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(1) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(2) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(3) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(4) instanceof RowExpander.ColumnDynamicValues);
        assertTrue(columns.get(5) instanceof RowExpander.ColumnDynamicValues);
        assertTrue(columns.get(6) instanceof RowExpander.ColumnValues);
        assertTrue(columns.get(7) instanceof RowExpander.ColumnValues);

        //Can't check size of values for ColumnDynamicValues as they depend on the other columns
        assertEquals(1,
                     columns.get(0).values.size());
        assertEquals(1,
                     columns.get(1).values.size());
        assertEquals(1,
                     columns.get(2).values.size());
        assertEquals(2,
                     columns.get(3).values.size());
        assertEquals(1,
                     columns.get(6).values.size());
        assertEquals(1,
                     columns.get(7).values.size());

        //Expected data
        // --> f1a, f1af2a, f1af2af3a
        // --> f1a, f1af2a, f1af2af3b
        // --> f1a, f1af2b, f1af2bf3a
        // --> f1a, f1af2b, f1af2bf3b
        // --> f1b, f1bf2a, f1bf2af3a
        // --> f1b, f1bf2a, f1bf2af3b
        // --> f1b, f1bf2b, f1bf2bf3a
        // --> f1b, f1bf2b, f1bf2bf3b

        RowExpander.RowIterator ri = re.iterator();
        assertTrue(ri.hasNext());
        List<DTCellValue52> row0 = ri.next();
        assertEquals(8,
                     row0.size());
        assertEquals("f1a",
                     row0.get(3).getStringValue());
        assertEquals("f1af2a",
                     row0.get(4).getStringValue());
        assertEquals("f1af2af3a",
                     row0.get(5).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row1 = ri.next();
        assertEquals(8,
                     row1.size());
        assertEquals("f1a",
                     row1.get(3).getStringValue());
        assertEquals("f1af2a",
                     row1.get(4).getStringValue());
        assertEquals("f1af2af3b",
                     row1.get(5).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row2 = ri.next();
        assertEquals(8,
                     row2.size());
        assertEquals("f1a",
                     row2.get(3).getStringValue());
        assertEquals("f1af2b",
                     row2.get(4).getStringValue());
        assertEquals("f1af2bf3a",
                     row2.get(5).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row3 = ri.next();
        assertEquals(8,
                     row3.size());
        assertEquals("f1a",
                     row3.get(3).getStringValue());
        assertEquals("f1af2b",
                     row3.get(4).getStringValue());
        assertEquals("f1af2bf3b",
                     row3.get(5).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row4 = ri.next();
        assertEquals(8,
                     row4.size());
        assertEquals("f1b",
                     row4.get(3).getStringValue());
        assertEquals("f1bf2a",
                     row4.get(4).getStringValue());
        assertEquals("f1bf2af3a",
                     row4.get(5).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row5 = ri.next();
        assertEquals(8,
                     row5.size());
        assertEquals("f1b",
                     row5.get(3).getStringValue());
        assertEquals("f1bf2a",
                     row5.get(4).getStringValue());
        assertEquals("f1bf2af3b",
                     row5.get(5).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row6 = ri.next();
        assertEquals(8,
                     row6.size());
        assertEquals("f1b",
                     row6.get(3).getStringValue());
        assertEquals("f1bf2b",
                     row6.get(4).getStringValue());
        assertEquals("f1bf2bf3a",
                     row6.get(5).getStringValue());

        assertTrue(ri.hasNext());
        List<DTCellValue52> row7 = ri.next();
        assertEquals(8,
                     row7.size());
        assertEquals("f1b",
                     row7.get(3).getStringValue());
        assertEquals("f1bf2b",
                     row7.get(4).getStringValue());
        assertEquals("f1bf2bf3b",
                     row7.get(5).getStringValue());

        assertFalse(ri.hasNext());
    }

    @SuppressWarnings("serial")
    @Test
    public void testColumnValues() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Driver")
                .addField(new ModelField("name",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .setModuleOracle(projectLoader)
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        dataModel.setWorkbenchEnumDefinitions(packageLoader.getPackageWorkbenchDefinitions());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("name");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c1.setValueList("c1a,c1b");
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        RowExpander re = new RowExpander(model,
                                         oracle);

        List<RowExpander.ColumnValues> columns = re.getColumns();
        assertEquals(4,
                     columns.size());

        assertEquals("",
                     columns.get(0).getCurrentValue().getStringValue());
        assertEquals("",
                     columns.get(1).getCurrentValue().getStringValue());
        assertEquals("",
                     columns.get(2).getCurrentValue().getStringValue());
        assertEquals("c1a",
                     columns.get(3).getCurrentValue().getStringValue());
        columns.get(3).advanceColumnValue();
        assertEquals("c1b",
                     columns.get(3).getCurrentValue().getStringValue());
        columns.get(3).advanceColumnValue();
        assertEquals("c1a",
                     columns.get(3).getCurrentValue().getStringValue());
        columns.get(3).advanceColumnValue();
        assertEquals("c1b",
                     columns.get(3).getCurrentValue().getStringValue());
    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesList1() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Driver")
                .addField(new ModelField("name",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .setModuleOracle(projectLoader)
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        dataModel.setWorkbenchEnumDefinitions(packageLoader.getPackageWorkbenchDefinitions());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("name");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c1.setValueList("c1a,c1b");
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        RowExpander re = new RowExpander(model,
                                         oracle);

        assertEquals(4,
                     re.getColumns().size());

        RowExpander.RowIterator i = re.iterator();
        List<List<DTCellValue52>> rows = new ArrayList<List<DTCellValue52>>();
        while (i.hasNext()) {
            List<DTCellValue52> row = i.next();
            rows.add(row);
        }

        assertEquals(2,
                     rows.size());

        assertEquals("",
                     rows.get(0).get(0).getStringValue());
        assertEquals("",
                     rows.get(0).get(1).getStringValue());
        assertEquals("",
                     rows.get(0).get(2).getStringValue());
        assertEquals("c1a",
                     rows.get(0).get(3).getStringValue());
        assertEquals("",
                     rows.get(1).get(0).getStringValue());
        assertEquals("",
                     rows.get(1).get(1).getStringValue());
        assertEquals("",
                     rows.get(1).get(2).getStringValue());
        assertEquals("c1b",
                     rows.get(1).get(3).getStringValue());
    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesList2() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Driver")
                .addField(new ModelField("age",
                                         Integer.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_NUMERIC_INTEGER))
                .addField(new ModelField("name",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .end()
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("name");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c1.setValueList("c1a,c1b");
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        Pattern52 p2 = new Pattern52();
        p2.setBoundName("c2");
        p2.setFactType("Driver");

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("age");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c2.setValueList("c2a,c2b");
        p2.getChildColumns().add(c2);
        model.getConditions().add(p2);

        RowExpander re = new RowExpander(model,
                                         oracle);

        assertEquals(5,
                     re.getColumns().size());

        RowExpander.RowIterator i = re.iterator();
        List<List<DTCellValue52>> rows = new ArrayList<List<DTCellValue52>>();
        while (i.hasNext()) {
            List<DTCellValue52> row = i.next();
            rows.add(row);
        }

        assertEquals(4,
                     rows.size());

        assertEquals("",
                     rows.get(0).get(0).getStringValue());
        assertEquals("",
                     rows.get(0).get(1).getStringValue());
        assertEquals("",
                     rows.get(0).get(2).getStringValue());
        assertEquals("c1a",
                     rows.get(0).get(3).getStringValue());
        assertEquals("c2a",
                     rows.get(0).get(4).getStringValue());
        assertEquals("",
                     rows.get(1).get(0).getStringValue());
        assertEquals("",
                     rows.get(1).get(1).getStringValue());
        assertEquals("",
                     rows.get(1).get(2).getStringValue());
        assertEquals("c1a",
                     rows.get(1).get(3).getStringValue());
        assertEquals("c2b",
                     rows.get(1).get(4).getStringValue());
        assertEquals("",
                     rows.get(2).get(0).getStringValue());
        assertEquals("",
                     rows.get(2).get(1).getStringValue());
        assertEquals("",
                     rows.get(2).get(2).getStringValue());
        assertEquals("c1b",
                     rows.get(2).get(3).getStringValue());
        assertEquals("c2a",
                     rows.get(2).get(4).getStringValue());
        assertEquals("",
                     rows.get(3).get(0).getStringValue());
        assertEquals("",
                     rows.get(3).get(1).getStringValue());
        assertEquals("",
                     rows.get(3).get(2).getStringValue());
        assertEquals("c1b",
                     rows.get(3).get(3).getStringValue());
        assertEquals("c2b",
                     rows.get(3).get(4).getStringValue());
    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesList3() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Driver")
                .addField(new ModelField("age",
                                         Integer.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_NUMERIC_INTEGER))
                .addField(new ModelField("name",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("dateOfBirth",
                                         Boolean.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_DATE))
                .end()
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("name");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c1.setValueList("c1a,c1b");
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        Pattern52 p2 = new Pattern52();
        p2.setBoundName("c2");
        p2.setFactType("Driver");

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("age");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c2.setValueList("c2a,c2b");
        p2.getChildColumns().add(c2);
        model.getConditions().add(p2);

        Pattern52 p3 = new Pattern52();
        p3.setBoundName("c3");
        p3.setFactType("Driver");

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField("dateOfBirth");
        c3.setOperator("==");
        c3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p3.getChildColumns().add(c3);
        model.getConditions().add(p3);

        RowExpander re = new RowExpander(model,
                                         oracle);

        assertEquals(6,
                     re.getColumns().size());

        RowExpander.RowIterator i = re.iterator();
        List<List<DTCellValue52>> rows = new ArrayList<List<DTCellValue52>>();
        while (i.hasNext()) {
            List<DTCellValue52> row = i.next();
            rows.add(row);
        }

        assertEquals(4,
                     rows.size());

        assertEquals("",
                     rows.get(0).get(0).getStringValue());
        assertEquals("",
                     rows.get(0).get(1).getStringValue());
        assertEquals("",
                     rows.get(0).get(2).getStringValue());
        assertEquals("c1a",
                     rows.get(0).get(3).getStringValue());
        assertEquals("c2a",
                     rows.get(0).get(4).getStringValue());
        assertNull(rows.get(0).get(5));

        assertEquals("",
                     rows.get(1).get(0).getStringValue());
        assertEquals("",
                     rows.get(1).get(1).getStringValue());
        assertEquals("",
                     rows.get(1).get(2).getStringValue());
        assertEquals("c1a",
                     rows.get(1).get(3).getStringValue());
        assertEquals("c2b",
                     rows.get(1).get(4).getStringValue());
        assertNull(rows.get(1).get(5));

        assertEquals("",
                     rows.get(2).get(0).getStringValue());
        assertEquals("",
                     rows.get(2).get(1).getStringValue());
        assertEquals("",
                     rows.get(2).get(2).getStringValue());
        assertEquals("c1b",
                     rows.get(2).get(3).getStringValue());
        assertEquals("c2a",
                     rows.get(2).get(4).getStringValue());
        assertNull(rows.get(2).get(5));

        assertEquals("",
                     rows.get(3).get(0).getStringValue());
        assertEquals("",
                     rows.get(3).get(1).getStringValue());
        assertEquals("",
                     rows.get(3).get(2).getStringValue());
        assertEquals("c1b",
                     rows.get(3).get(3).getStringValue());
        assertEquals("c2b",
                     rows.get(3).get(4).getStringValue());
        assertNull(rows.get(3).get(5));
    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesListAndDefaultValues() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Driver")
                .addField(new ModelField("age",
                                         Integer.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_NUMERIC_INTEGER))
                .addField(new ModelField("name",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("dateOfBirth",
                                         Boolean.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_DATE))
                .end()
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("name");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c1.setValueList("c1a,c1b");
        c1.setDefaultValue(new DTCellValue52("c1default"));
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        Pattern52 p2 = new Pattern52();
        p2.setBoundName("c2");
        p2.setFactType("Driver");

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("age");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c2.setValueList("c2a,c2b");
        c2.setDefaultValue(new DTCellValue52("c2default"));
        p2.getChildColumns().add(c2);
        model.getConditions().add(p2);

        Pattern52 p3 = new Pattern52();
        p3.setBoundName("c3");
        p3.setFactType("Driver");

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField("dateOfBirth");
        c3.setOperator("==");
        c3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c3.setDefaultValue(new DTCellValue52("c3default"));
        p3.getChildColumns().add(c3);
        model.getConditions().add(p3);

        RowExpander re = new RowExpander(model,
                                         oracle);

        assertEquals(6,
                     re.getColumns().size());

        RowExpander.RowIterator i = re.iterator();
        List<List<DTCellValue52>> rows = new ArrayList<List<DTCellValue52>>();
        while (i.hasNext()) {
            List<DTCellValue52> row = i.next();
            rows.add(row);
        }

        assertEquals(4,
                     rows.size());

        assertEquals("",
                     rows.get(0).get(0).getStringValue());
        assertEquals("",
                     rows.get(0).get(1).getStringValue());
        assertEquals("",
                     rows.get(0).get(2).getStringValue());
        assertEquals("c1a",
                     rows.get(0).get(3).getStringValue());
        assertEquals("c2a",
                     rows.get(0).get(4).getStringValue());
        assertEquals("c3default",
                     rows.get(0).get(5).getStringValue());

        assertEquals("",
                     rows.get(1).get(0).getStringValue());
        assertEquals("",
                     rows.get(1).get(1).getStringValue());
        assertEquals("",
                     rows.get(1).get(2).getStringValue());
        assertEquals("c1a",
                     rows.get(1).get(3).getStringValue());
        assertEquals("c2b",
                     rows.get(1).get(4).getStringValue());
        assertEquals("c3default",
                     rows.get(1).get(5).getStringValue());

        assertEquals("",
                     rows.get(2).get(0).getStringValue());
        assertEquals("",
                     rows.get(2).get(1).getStringValue());
        assertEquals("",
                     rows.get(2).get(2).getStringValue());
        assertEquals("c1b",
                     rows.get(2).get(3).getStringValue());
        assertEquals("c2a",
                     rows.get(2).get(4).getStringValue());
        assertEquals("c3default",
                     rows.get(2).get(5).getStringValue());

        assertEquals("",
                     rows.get(3).get(0).getStringValue());
        assertEquals("",
                     rows.get(3).get(1).getStringValue());
        assertEquals("",
                     rows.get(3).get(2).getStringValue());
        assertEquals("c1b",
                     rows.get(3).get(3).getStringValue());
        assertEquals("c2b",
                     rows.get(3).get(4).getStringValue());
        assertEquals("c3default",
                     rows.get(3).get(5).getStringValue());
    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesListAndColumnExpansionDisabled1() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Driver")
                .addField(new ModelField("age",
                                         Integer.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_NUMERIC_INTEGER))
                .addField(new ModelField("name",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("dateOfBirth",
                                         Boolean.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_DATE))
                .end()
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("name");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c1.setValueList("c1a,c1b");
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        Pattern52 p2 = new Pattern52();
        p2.setBoundName("c2");
        p2.setFactType("Driver");

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("age");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c2.setValueList("c2a,c2b");
        p2.getChildColumns().add(c2);
        model.getConditions().add(p2);

        Pattern52 p3 = new Pattern52();
        p3.setBoundName("c3");
        p3.setFactType("Driver");

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField("dateOfBirth");
        c3.setOperator("==");
        c3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c3.setValueList("c3a,c3b");
        p3.getChildColumns().add(c3);
        model.getConditions().add(p3);

        RowExpander re = new RowExpander(model,
                                         oracle);
        re.setExpandColumn(c1,
                           false);
        re.setExpandColumn(c2,
                           false);
        re.setExpandColumn(c3,
                           false);

        assertEquals(6,
                     re.getColumns().size());

        RowExpander.RowIterator i = re.iterator();
        assertFalse(i.hasNext());
    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesListAndColumnExpansionDisabled2() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Driver")
                .addField(new ModelField("age",
                                         Integer.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_NUMERIC_INTEGER))
                .addField(new ModelField("name",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("dateOfBirth",
                                         Boolean.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_DATE))
                .end()
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("name");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c1.setValueList("c1a,c1b");
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        Pattern52 p2 = new Pattern52();
        p2.setBoundName("c2");
        p2.setFactType("Driver");

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("age");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c2.setValueList("c2a,c2b");
        p2.getChildColumns().add(c2);
        model.getConditions().add(p2);

        Pattern52 p3 = new Pattern52();
        p3.setBoundName("c3");
        p3.setFactType("Driver");

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField("dateOfBirth");
        c3.setOperator("==");
        c3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c3.setValueList("c3a,c3b");
        p3.getChildColumns().add(c3);
        model.getConditions().add(p3);

        RowExpander re = new RowExpander(model,
                                         oracle);
        re.setExpandColumn(c1,
                           false);
        re.setExpandColumn(c2,
                           false);

        assertEquals(6,
                     re.getColumns().size());

        RowExpander.RowIterator i = re.iterator();
        List<List<DTCellValue52>> rows = new ArrayList<List<DTCellValue52>>();
        while (i.hasNext()) {
            List<DTCellValue52> row = i.next();
            rows.add(row);
        }

        assertEquals(2,
                     rows.size());

        assertEquals("",
                     rows.get(0).get(0).getStringValue());
        assertEquals("",
                     rows.get(0).get(1).getStringValue());
        assertEquals("",
                     rows.get(0).get(2).getStringValue());
        assertNull(rows.get(0).get(3));
        assertNull(rows.get(0).get(4));
        assertEquals("c3a",
                     rows.get(0).get(5).getStringValue());

        assertEquals("",
                     rows.get(1).get(0).getStringValue());
        assertEquals("",
                     rows.get(1).get(1).getStringValue());
        assertEquals("",
                     rows.get(1).get(2).getStringValue());
        assertNull(rows.get(1).get(3));
        assertNull(rows.get(1).get(4));
        assertEquals("c3b",
                     rows.get(1).get(5).getStringValue());
    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesListAndColumnExpansionDisabled3() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Driver")
                .addField(new ModelField("age",
                                         Integer.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_NUMERIC_INTEGER))
                .addField(new ModelField("name",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("dateOfBirth",
                                         Boolean.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_DATE))
                .end()
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("name");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c1.setValueList("c1a,c1b");
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        Pattern52 p2 = new Pattern52();
        p2.setBoundName("c2");
        p2.setFactType("Driver");

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("age");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c2.setValueList("c2a,c2b");
        p2.getChildColumns().add(c2);
        model.getConditions().add(p2);

        Pattern52 p3 = new Pattern52();
        p3.setBoundName("c3");
        p3.setFactType("Driver");

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField("dateOfBirth");
        c3.setOperator("==");
        c3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c3.setValueList("c3a,c3b");
        p3.getChildColumns().add(c3);
        model.getConditions().add(p3);

        RowExpander re = new RowExpander(model,
                                         oracle);
        re.setExpandColumn(c2,
                           false);

        assertEquals(6,
                     re.getColumns().size());

        RowExpander.RowIterator i = re.iterator();
        List<List<DTCellValue52>> rows = new ArrayList<List<DTCellValue52>>();
        while (i.hasNext()) {
            List<DTCellValue52> row = i.next();
            rows.add(row);
        }

        assertEquals(4,
                     rows.size());

        assertEquals("",
                     rows.get(0).get(0).getStringValue());
        assertEquals("",
                     rows.get(0).get(1).getStringValue());
        assertEquals("",
                     rows.get(0).get(2).getStringValue());
        assertEquals("c1a",
                     rows.get(0).get(3).getStringValue());
        assertNull(rows.get(0).get(4));
        assertEquals("c3a",
                     rows.get(0).get(5).getStringValue());

        assertEquals("",
                     rows.get(1).get(0).getStringValue());
        assertEquals("",
                     rows.get(1).get(1).getStringValue());
        assertEquals("",
                     rows.get(1).get(2).getStringValue());
        assertEquals("c1a",
                     rows.get(1).get(3).getStringValue());
        assertNull(rows.get(1).get(4));
        assertEquals("c3b",
                     rows.get(1).get(5).getStringValue());

        assertEquals("",
                     rows.get(2).get(0).getStringValue());
        assertEquals("",
                     rows.get(2).get(1).getStringValue());
        assertEquals("",
                     rows.get(2).get(2).getStringValue());
        assertEquals("c1b",
                     rows.get(2).get(3).getStringValue());
        assertNull(rows.get(2).get(4));
        assertEquals("c3a",
                     rows.get(2).get(5).getStringValue());

        assertEquals("",
                     rows.get(3).get(0).getStringValue());
        assertEquals("",
                     rows.get(3).get(1).getStringValue());
        assertEquals("",
                     rows.get(3).get(2).getStringValue());
        assertEquals("c1b",
                     rows.get(3).get(3).getStringValue());
        assertNull(rows.get(3).get(4));
        assertEquals("c3b",
                     rows.get(3).get(5).getStringValue());
    }

    @Test
    @SuppressWarnings("serial")
    public void testRowExpansionWithValuesListAndColumnExpansionDisabledAndDefaultValues() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Driver")
                .addField(new ModelField("age",
                                         Integer.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_NUMERIC_INTEGER))
                .addField(new ModelField("name",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("dateOfBirth",
                                         Boolean.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_DATE))
                .end()
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("name");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c1.setValueList("c1a,c1b");
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        Pattern52 p2 = new Pattern52();
        p2.setBoundName("c2");
        p2.setFactType("Driver");

        ConditionCol52 c2 = new ConditionCol52();
        c2.setFactField("age");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c2.setValueList("c2a,c2b");
        c2.setDefaultValue(new DTCellValue52("c2default"));
        p2.getChildColumns().add(c2);
        model.getConditions().add(p2);

        Pattern52 p3 = new Pattern52();
        p3.setBoundName("c3");
        p3.setFactType("Driver");

        ConditionCol52 c3 = new ConditionCol52();
        c3.setFactField("dateOfBirth");
        c3.setOperator("==");
        c3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c3.setValueList("c3a,c3b");
        p3.getChildColumns().add(c3);
        model.getConditions().add(p3);

        RowExpander re = new RowExpander(model,
                                         oracle);
        re.setExpandColumn(c2,
                           false);

        assertEquals(6,
                     re.getColumns().size());

        RowExpander.RowIterator i = re.iterator();
        List<List<DTCellValue52>> rows = new ArrayList<List<DTCellValue52>>();
        while (i.hasNext()) {
            List<DTCellValue52> row = i.next();
            rows.add(row);
        }

        assertEquals(4,
                     rows.size());

        assertEquals("",
                     rows.get(0).get(0).getStringValue());
        assertEquals("",
                     rows.get(0).get(1).getStringValue());
        assertEquals("",
                     rows.get(0).get(2).getStringValue());
        assertEquals("c1a",
                     rows.get(0).get(3).getStringValue());
        assertEquals("c2default",
                     rows.get(0).get(4).getStringValue());
        assertEquals("c3a",
                     rows.get(0).get(5).getStringValue());

        assertEquals("",
                     rows.get(1).get(0).getStringValue());
        assertEquals("",
                     rows.get(1).get(1).getStringValue());
        assertEquals("",
                     rows.get(1).get(2).getStringValue());
        assertEquals("c1a",
                     rows.get(1).get(3).getStringValue());
        assertEquals("c2default",
                     rows.get(1).get(4).getStringValue());
        assertEquals("c3b",
                     rows.get(1).get(5).getStringValue());

        assertEquals("",
                     rows.get(2).get(0).getStringValue());
        assertEquals("",
                     rows.get(2).get(1).getStringValue());
        assertEquals("",
                     rows.get(2).get(2).getStringValue());
        assertEquals("c1b",
                     rows.get(2).get(3).getStringValue());
        assertEquals("c2default",
                     rows.get(2).get(4).getStringValue());
        assertEquals("c3a",
                     rows.get(2).get(5).getStringValue());

        assertEquals("",
                     rows.get(3).get(0).getStringValue());
        assertEquals("",
                     rows.get(3).get(1).getStringValue());
        assertEquals("",
                     rows.get(3).get(2).getStringValue());
        assertEquals("c1b",
                     rows.get(3).get(3).getStringValue());
        assertEquals("c2default",
                     rows.get(3).get(4).getStringValue());
        assertEquals("c3b",
                     rows.get(3).get(5).getStringValue());
    }

    @Test
    @SuppressWarnings("serial")
    public void testExpansionWithLimitedEntry() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();
        model.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Driver")
                .addField(new ModelField("age",
                                         Integer.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_NUMERIC_INTEGER))
                .addField(new ModelField("name",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .addField(new ModelField("dateOfBirth",
                                         Boolean.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_DATE))
                .addField(new ModelField("approved",
                                         Boolean.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_BOOLEAN))
                .end()
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");

        LimitedEntryConditionCol52 c1 = new LimitedEntryConditionCol52();
        c1.setFactField("name");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c1.setValue(new DTCellValue52("Mike"));
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        Pattern52 p2 = new Pattern52();
        p2.setBoundName("c2");
        p2.setFactType("Driver");

        LimitedEntryConditionCol52 c2 = new LimitedEntryConditionCol52();
        c2.setFactField("age");
        c2.setOperator("==");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c1.setValue(new DTCellValue52(25));
        p2.getChildColumns().add(c2);
        model.getConditions().add(p2);

        LimitedEntryActionSetFieldCol52 a1 = new LimitedEntryActionSetFieldCol52();
        a1.setBoundName("c1");
        a1.setFactField("name");
        a1.setValue(new DTCellValue52("a1name"));
        model.getActionCols().add(a1);

        LimitedEntryActionInsertFactCol52 a2 = new LimitedEntryActionInsertFactCol52();
        a2.setBoundName("a2");
        a2.setFactType("Driver");
        a2.setFactField("name");
        a2.setValue(new DTCellValue52("a2name"));
        model.getActionCols().add(a2);

        RowExpander re = new RowExpander(model,
                                         oracle);

        List<RowExpander.ColumnValues> columns = re.getColumns();
        assertEquals(7,
                     columns.size());

        assertEquals(1,
                     columns.get(0).values.size());
        assertEquals(1,
                     columns.get(1).values.size());
        assertEquals(1,
                     columns.get(2).values.size());
        assertEquals(2,
                     columns.get(3).values.size());
        assertEquals(2,
                     columns.get(4).values.size());
        assertEquals(1,
                     columns.get(5).values.size());
        assertEquals(1,
                     columns.get(6).values.size());

        assertEquals(Boolean.TRUE,
                     columns.get(3).values.get(0).getBooleanValue());
        assertEquals(Boolean.FALSE,
                     columns.get(3).values.get(1).getBooleanValue());

        assertEquals(Boolean.TRUE,
                     columns.get(4).values.get(0).getBooleanValue());
        assertEquals(Boolean.FALSE,
                     columns.get(4).values.get(1).getBooleanValue());

        assertEquals(Boolean.FALSE,
                     columns.get(5).values.get(0).getBooleanValue());

        assertEquals(Boolean.FALSE,
                     columns.get(6).values.get(0).getBooleanValue());

        RowExpander.RowIterator i = re.iterator();
        List<List<DTCellValue52>> rows = new ArrayList<List<DTCellValue52>>();
        while (i.hasNext()) {
            List<DTCellValue52> row = i.next();
            rows.add(row);
        }

        assertEquals(4,
                     rows.size());

        assertEquals("",
                     rows.get(0).get(0).getStringValue());
        assertEquals("",
                     rows.get(0).get(1).getStringValue());
        assertEquals("",
                     rows.get(0).get(2).getStringValue());
        assertEquals(Boolean.TRUE,
                     rows.get(0).get(3).getBooleanValue());
        assertEquals(Boolean.TRUE,
                     rows.get(0).get(4).getBooleanValue());
        assertEquals(Boolean.FALSE,
                     rows.get(0).get(5).getBooleanValue());
        assertEquals(Boolean.FALSE,
                     rows.get(0).get(6).getBooleanValue());

        assertEquals("",
                     rows.get(1).get(0).getStringValue());
        assertEquals("",
                     rows.get(1).get(1).getStringValue());
        assertEquals("",
                     rows.get(1).get(2).getStringValue());
        assertEquals(Boolean.TRUE,
                     rows.get(1).get(3).getBooleanValue());
        assertEquals(Boolean.FALSE,
                     rows.get(1).get(4).getBooleanValue());
        assertEquals(Boolean.FALSE,
                     rows.get(1).get(5).getBooleanValue());
        assertEquals(Boolean.FALSE,
                     rows.get(1).get(6).getBooleanValue());

        assertEquals("",
                     rows.get(2).get(0).getStringValue());
        assertEquals("",
                     rows.get(2).get(1).getStringValue());
        assertEquals("",
                     rows.get(2).get(2).getStringValue());
        assertEquals(Boolean.FALSE,
                     rows.get(2).get(3).getBooleanValue());
        assertEquals(Boolean.TRUE,
                     rows.get(2).get(4).getBooleanValue());
        assertEquals(Boolean.FALSE,
                     rows.get(2).get(5).getBooleanValue());
        assertEquals(Boolean.FALSE,
                     rows.get(2).get(6).getBooleanValue());

        assertEquals("",
                     rows.get(3).get(0).getStringValue());
        assertEquals("",
                     rows.get(3).get(1).getStringValue());
        assertEquals("",
                     rows.get(3).get(2).getStringValue());
        assertEquals(Boolean.FALSE,
                     rows.get(3).get(3).getBooleanValue());
        assertEquals(Boolean.FALSE,
                     rows.get(3).get(4).getBooleanValue());
        assertEquals(Boolean.FALSE,
                     rows.get(3).get(5).getBooleanValue());
        assertEquals(Boolean.FALSE,
                     rows.get(3).get(6).getBooleanValue());
    }

    @Test
    //GUVNOR-1960
    public void testExpansionObjectUniqueness() {
        GuidedDecisionTable52 model = new GuidedDecisionTable52();

        final ModuleDataModelOracle projectLoader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
                .addFact("Driver")
                .addField(new ModelField("gender",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .setModuleOracle(projectLoader)
                .addEnum("'Driver.gender' : ['M', 'F']",
                         Thread.currentThread().getContextClassLoader())
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(projectLoader.getModuleModelFields());
        dataModel.setWorkbenchEnumDefinitions(packageLoader.getPackageWorkbenchDefinitions());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField("gender");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);
        RowExpander re = new RowExpander(model,
                                         oracle);

        List<RowExpander.ColumnValues> columns = re.getColumns();
        assertEquals(4,
                     columns.size());

        assertEquals(1,
                     columns.get(0).values.size());
        assertEquals(1,
                     columns.get(1).values.size());
        assertEquals(1,
                     columns.get(2).values.size());
        assertEquals(2,
                     columns.get(3).values.size());

        RowExpander.RowIterator ri = re.iterator();
        assertTrue(ri.hasNext());

        List<List<DTCellValue52>> rows = new ArrayList<List<DTCellValue52>>();
        while (ri.hasNext()) {
            List<DTCellValue52> row = ri.next();
            rows.add(row);
        }

        assertEquals(2,
                     rows.size());

        assertEquals("",
                     rows.get(0).get(0).getStringValue());
        assertEquals("",
                     rows.get(0).get(1).getStringValue());
        assertEquals("",
                     rows.get(0).get(2).getStringValue());
        assertEquals("M",
                     rows.get(0).get(3).getStringValue());

        assertEquals("",
                     rows.get(1).get(0).getStringValue());
        assertEquals("",
                     rows.get(1).get(1).getStringValue());
        assertEquals("",
                     rows.get(1).get(2).getStringValue());
        assertEquals("F",
                     rows.get(1).get(3).getStringValue());

        assertTrue(rows.get(0).get(0) != rows.get(1).get(0));
        assertTrue(rows.get(0).get(1) != rows.get(1).get(1));
    }

    private void populateDataModelOracle(final Path resourcePath,
                                         final HasImports hasImports,
                                         final AsyncPackageDataModelOracle oracle,
                                         final PackageDataModelOracleBaselinePayload payload) {
        populate(oracle,
                 payload);
        oracle.init(resourcePath);
        oracle.filter(hasImports.getImports());
    }
}
