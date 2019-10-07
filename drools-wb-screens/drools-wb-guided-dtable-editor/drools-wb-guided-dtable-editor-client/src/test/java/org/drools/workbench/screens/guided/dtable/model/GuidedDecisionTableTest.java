/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Instance;

import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class GuidedDecisionTableTest {

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
    public void testValueLists() {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final PackageDataModelOracle loader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .addEnum("Driver",
                         "name",
                         new String[]{"bob", "michael"})
                .addEnum("Person",
                         "rating",
                         new String[]{"1", "2"})
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(loader.getModuleModelFields());
        dataModel.setWorkbenchEnumDefinitions(loader.getPackageWorkbenchDefinitions());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);
        final ColumnUtilities utils = new ColumnUtilities(model,
                                                          oracle);

        final Map<String, String> currentValueMap = new HashMap<>();

        // add cols for LHS
        final ConditionCol52 c1 = new ConditionCol52();
        final Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");
        c1.setFactField("name");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        final ConditionCol52 c1_ = new ConditionCol52();
        final Pattern52 p1_ = new Pattern52();
        p1_.setBoundName("c1");
        p1_.setFactType("Driver");
        c1_.setFactField("name");
        p1_.getChildColumns().add(c1_);
        c1_.setConstraintValueType(BaseSingleFieldConstraint.TYPE_RET_VALUE);
        model.getConditions().add(p1_);

        final ConditionCol52 c1__ = new ConditionCol52();
        c1__.setFactField("sex");
        p1_.getChildColumns().add(c1__);
        c1__.setConstraintValueType(BaseSingleFieldConstraint.TYPE_RET_VALUE);
        c1__.setValueList("Male,Female");
        model.getConditions().add(p1_);

        final ConditionCol52 c1___ = new ConditionCol52();
        final Pattern52 p1__ = new Pattern52();
        p1__.setBoundName("c1");
        p1__.setFactType("Driver");
        c1___.setFactField("name");
        c1___.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c1___.setValueList("one,two,three");
        p1__.getChildColumns().add(c1___);
        model.getConditions().add(p1__);

        final ConditionCol52 c2 = new ConditionCol52();
        final Pattern52 p2 = new Pattern52();
        p2.setBoundName("c2");
        p2.setFactType("Driver");
        c2.setFactField("nothing");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p2.getChildColumns().add(c2);
        model.getConditions().add(p2);

        final ActionSetFieldCol52 asf = new ActionSetFieldCol52();
        asf.setBoundName("c1");
        asf.setFactField("name");
        model.getActionCols().add(asf);

        final ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName("x");
        ins.setFactField("rating");
        ins.setFactType("Person");
        model.getActionCols().add(ins);

        final ActionInsertFactCol52 ins_ = new ActionInsertFactCol52();
        ins_.setBoundName("x");
        ins_.setFactField("rating");
        ins_.setFactType("Person");
        ins_.setValueList("one,two,three");
        model.getActionCols().add(ins_);

        final ActionSetFieldCol52 asf_ = new ActionSetFieldCol52();
        asf_.setBoundName("c1");
        asf_.setFactField("goo");
        model.getActionCols().add(asf_);

        final ActionSetFieldCol52 asf__ = new ActionSetFieldCol52();
        asf__.setBoundName("c1");
        asf__.setType("String");
        asf__.setFactField("goo");
        asf__.setValueList("one,two,three");
        model.getActionCols().add(asf__);

        assertTrue(oracle.hasEnums(p1.getFactType(),
                                   c1.getFactField()));
        assertFalse(utils.hasValueList(c1));
        String[] r = oracle.getEnums(p1.getFactType(),
                                     c1.getFactField(),
                                     currentValueMap).getFixedList();
        assertEquals(2,
                     r.length);
        assertEquals("bob",
                     r[0]);
        assertEquals("michael",
                     r[1]);

        assertTrue(oracle.hasEnums(p1_.getFactType(),
                                   c1_.getFactField()));
        assertFalse(utils.hasValueList(c1_));
        r = oracle.getEnums(p1_.getFactType(),
                            c1_.getFactField(),
                            currentValueMap).getFixedList();
        assertEquals(2,
                     r.length);
        assertEquals("bob",
                     r[0]);
        assertEquals("michael",
                     r[1]);

        assertFalse(oracle.hasEnums(p1_.getFactType(),
                                    c1__.getFactField()));
        assertTrue(utils.hasValueList(c1__));
        r = utils.getValueList(c1__);
        assertEquals(2,
                     r.length);
        assertEquals("Male",
                     r[0]);
        assertEquals("Female",
                     r[1]);

        assertTrue(oracle.hasEnums(p1__.getFactType(),
                                   c1___.getFactField()));
        assertTrue(utils.hasValueList(c1___));
        r = utils.getValueList(c1___);
        assertEquals(3,
                     r.length);
        assertEquals("one",
                     r[0]);
        assertEquals("two",
                     r[1]);
        assertEquals("three",
                     r[2]);

        assertEquals(0,
                     utils.getValueList(c2).length);

        assertTrue(oracle.hasEnums(p1.getFactType(),
                                   asf.getFactField()));
        assertFalse(utils.hasValueList(asf));
        r = oracle.getEnums(p1.getFactType(),
                            asf.getFactField(),
                            currentValueMap).getFixedList();
        assertEquals(2,
                     r.length);
        assertEquals("bob",
                     r[0]);
        assertEquals("michael",
                     r[1]);

        assertTrue(oracle.hasEnums(ins.getFactType(),
                                   ins.getFactField()));
        assertFalse(utils.hasValueList(ins));
        r = oracle.getEnums(ins.getFactType(),
                            ins.getFactField(),
                            currentValueMap).getFixedList();
        assertEquals(2,
                     r.length);
        assertEquals("1",
                     r[0]);
        assertEquals("2",
                     r[1]);

        assertTrue(oracle.hasEnums(ins_.getFactType(),
                                   ins_.getFactField()));
        assertTrue(utils.hasValueList(ins_));
        r = utils.getValueList(ins_);
        assertEquals(3,
                     r.length);
        assertEquals("one",
                     r[0]);
        assertEquals("two",
                     r[1]);
        assertEquals("three",
                     r[2]);

        assertEquals(0,
                     utils.getValueList(asf_).length);

        assertFalse(oracle.hasEnums(p1.getFactType(),
                                    asf__.getFactField()));
        assertTrue(utils.hasValueList(asf__));
        r = utils.getValueList(asf__);
        assertEquals(3,
                     r.length);
        assertEquals("one",
                     r[0]);
        assertEquals("two",
                     r[1]);
        assertEquals("three",
                     r[2]);

        AttributeCol52 at = new AttributeCol52();
        at.setAttribute(Attribute.NO_LOOP.getAttributeName());
        model.getAttributeCols().add(at);

        r = utils.getValueList(at);
        assertEquals(2,
                     r.length);
        assertEquals("true",
                     r[0]);
        assertEquals("false",
                     r[1]);

        at.setAttribute(Attribute.ENABLED.getAttributeName());
        assertEquals(2,
                     utils.getValueList(at).length);

        at.setAttribute(Attribute.SALIENCE.getAttributeName());
        assertEquals(0,
                     utils.getValueList(at).length);
    }

    @Test
    @SuppressWarnings("serial")
    public void testNumeric() {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final ModuleDataModelOracle loader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
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
        dataModel.setModelFields(loader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);
        final ColumnUtilities utils = new ColumnUtilities(model,
                                                          oracle);

        final AttributeCol52 at = new AttributeCol52();
        at.setAttribute(Attribute.SALIENCE.getAttributeName());
        final AttributeCol52 at_ = new AttributeCol52();
        at_.setAttribute(Attribute.ENABLED.getAttributeName());

        model.getAttributeCols().add(at);
        model.getAttributeCols().add(at_);

        final ConditionCol52 c1 = new ConditionCol52();
        final Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");
        c1.setFactField("name");
        c1.setOperator("==");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        final ConditionCol52 c1_ = new ConditionCol52();
        final Pattern52 p1_ = new Pattern52();
        p1_.setBoundName("c1");
        p1_.setFactType("Driver");
        c1_.setFactField("age");
        c1_.setOperator("==");
        c1_.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1_.getChildColumns().add(c1_);
        model.getConditions().add(p1_);

        final ConditionCol52 c2 = new ConditionCol52();
        final Pattern52 p2 = new Pattern52();
        p2.setBoundName("c1");
        p2.setFactType("Driver");
        c2.setFactField("age");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p2.getChildColumns().add(c2);
        model.getConditions().add(p2);

        final ActionSetFieldCol52 a = new ActionSetFieldCol52();
        a.setBoundName("c1");
        a.setFactField("name");
        model.getActionCols().add(a);

        final ActionSetFieldCol52 a2 = new ActionSetFieldCol52();
        a2.setBoundName("c1");
        a2.setFactField("age");
        model.getActionCols().add(a2);

        final ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName("x");
        ins.setFactType("Driver");
        ins.setFactField("name");
        model.getActionCols().add(ins);

        final ActionInsertFactCol52 ins_ = new ActionInsertFactCol52();
        ins_.setBoundName("x");
        ins_.setFactType("Driver");
        ins_.setFactField("age");
        model.getActionCols().add(ins_);

        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     utils.getType(at));
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     utils.getType(c1_));
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     utils.getType(a2));
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     utils.getType(ins_));

        assertEquals(DataType.TYPE_BOOLEAN,
                     utils.getType(at_));
        assertEquals(DataType.TYPE_STRING,
                     utils.getType(c1));
        assertEquals(DataType.TYPE_STRING,
                     utils.getType(a));
        assertEquals(DataType.TYPE_STRING,
                     utils.getType(ins));

        assertEquals(DataType.TYPE_STRING,
                     utils.getType(c2));
    }

    @Test
    @SuppressWarnings("serial")
    public void testGetType() {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final ModuleDataModelOracle loader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
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
                .addField(new ModelField("date",
                                         Date.class.getName(),
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
        dataModel.setModelFields(loader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);
        final ColumnUtilities utils = new ColumnUtilities(model,
                                                          oracle);

        final AttributeCol52 salienceAttribute = new AttributeCol52();
        salienceAttribute.setAttribute(Attribute.SALIENCE.getAttributeName());
        final AttributeCol52 enabledAttribute = new AttributeCol52();
        enabledAttribute.setAttribute(Attribute.ENABLED.getAttributeName());

        model.getAttributeCols().add(salienceAttribute);
        model.getAttributeCols().add(enabledAttribute);

        final Pattern52 p1 = new Pattern52();

        final ConditionCol52 conditionColName = new ConditionCol52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");
        conditionColName.setFactField("name");
        conditionColName.setOperator("==");
        conditionColName.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(conditionColName);

        final ConditionCol52 conditionColAge = new ConditionCol52();
        conditionColAge.setFactField("age");
        conditionColAge.setOperator("==");
        conditionColAge.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(conditionColAge);

        final ConditionCol52 conditionColDate = new ConditionCol52();
        conditionColDate.setFactField("date");
        conditionColDate.setOperator("==");
        conditionColDate.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(conditionColDate);

        final ConditionCol52 conditionColApproved = new ConditionCol52();
        conditionColApproved.setFactField("approved");
        conditionColApproved.setOperator("==");
        conditionColApproved.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(conditionColApproved);

        final ConditionCol52 conditionColAge2 = new ConditionCol52();
        conditionColAge2.setFactField("age");
        conditionColAge2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(conditionColAge2);

        model.getConditions().add(p1);

        final ActionSetFieldCol52 a = new ActionSetFieldCol52();
        a.setBoundName("c1");
        a.setFactField("name");
        model.getActionCols().add(a);

        final ActionSetFieldCol52 a2 = new ActionSetFieldCol52();
        a2.setBoundName("c1");
        a2.setFactField("age");
        model.getActionCols().add(a2);

        final ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName("x");
        ins.setFactType("Driver");
        ins.setFactField("name");
        model.getActionCols().add(ins);

        final ActionInsertFactCol52 ins_ = new ActionInsertFactCol52();
        ins_.setBoundName("x");
        ins_.setFactType("Driver");
        ins_.setFactField("age");
        model.getActionCols().add(ins_);

        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     utils.getType(salienceAttribute));
        assertEquals(DataType.TYPE_BOOLEAN,
                     utils.getType(enabledAttribute));
        assertEquals(DataType.TYPE_STRING,
                     utils.getType(conditionColName));
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     utils.getType(conditionColAge));
        assertEquals(DataType.TYPE_DATE,
                     utils.getType(conditionColDate));
        assertEquals(DataType.TYPE_BOOLEAN,
                     utils.getType(conditionColApproved));
        assertEquals(DataType.TYPE_STRING,
                     utils.getType(a));
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     utils.getType(a2));
        assertEquals(DataType.TYPE_STRING,
                     utils.getType(ins));
        assertEquals(DataType.TYPE_NUMERIC_INTEGER,
                     utils.getType(ins_));
        assertEquals(DataType.TYPE_STRING,
                     utils.getType(conditionColAge2));
    }

    @Test
    public void testNoConstraintLists() {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final PackageDataModelOracle loader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .addEnum("Driver",
                         "name",
                         new String[]{"bob", "michael"})
                .build();

        //Emulate server-to-client conversions
        final AsyncPackageDataModelOracle oracle = getOracle();
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields(loader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);
        final ColumnUtilities utils = new ColumnUtilities(model,
                                                          oracle);

        // add cols for LHS
        final ConditionCol52 c1 = new ConditionCol52();
        final Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        final ConditionCol52 c2 = new ConditionCol52();
        final Pattern52 p2 = new Pattern52();
        p2.setBoundName("c2");
        p2.setFactType("Driver");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        c2.setValueList("a,b,c");
        p2.getChildColumns().add(c2);
        model.getConditions().add(p1);

        assertEquals(0,
                     utils.getValueList(c1).length);
        assertEquals(3,
                     utils.getValueList(c2).length);
    }

    @SuppressWarnings("serial")
    @Test
    public void testNoConstraints() {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final ModuleDataModelOracle loader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
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
        dataModel.setModelFields(loader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);
        final ColumnUtilities utils = new ColumnUtilities(model,
                                                          oracle);

        // add cols for LHS
        final RowNumberCol52 rnc = new RowNumberCol52();
        final DescriptionCol52 dc = new DescriptionCol52();

        final MetadataCol52 mdc = new MetadataCol52();
        mdc.setMetadata("cheese");

        final AttributeCol52 ac = new AttributeCol52();
        ac.setAttribute(Attribute.SALIENCE.getAttributeName());

        final ActionSetFieldCol52 asfc = new ActionSetFieldCol52();
        asfc.setBoundName("d1");
        asfc.setFactField("age");

        final ActionInsertFactCol52 aifc = new ActionInsertFactCol52();
        aifc.setBoundName("d2");
        aifc.setFactType("Driver");
        aifc.setFactField("age");

        final ConditionCol52 c1 = new ConditionCol52();
        Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        final ConditionCol52 c2 = new ConditionCol52();
        Pattern52 p2 = new Pattern52();
        p2.setBoundName("c2");
        p2.setFactType("Driver");
        c2.setFactField("age");
        c2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p2.getChildColumns().add(c2);
        model.getConditions().add(p2);

        final ConditionCol52 c3 = new ConditionCol52();
        Pattern52 p3 = new Pattern52();
        p3.setBoundName("c3");
        p3.setFactType("Driver");
        c3.setOperator("==");
        c3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p3.getChildColumns().add(c3);
        model.getConditions().add(p3);

        final ConditionCol52 c4 = new ConditionCol52();
        Pattern52 p4 = new Pattern52();
        p4.setBoundName("c4");
        p4.setFactType("Driver");
        c4.setFactField("age");
        c4.setOperator("==");
        c4.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        p4.getChildColumns().add(c4);
        model.getConditions().add(p4);

        final ConditionCol52 c5 = new ConditionCol52();
        Pattern52 p5 = new Pattern52();
        p5.setBoundName("c5");
        p5.setFactType("Driver");
        c5.setConstraintValueType(BaseSingleFieldConstraint.TYPE_PREDICATE);
        p5.getChildColumns().add(c5);
        model.getConditions().add(p5);

        final ConditionCol52 c6 = new ConditionCol52();
        Pattern52 p6 = new Pattern52();
        p6.setBoundName("c6");
        p6.setFactType("Driver");
        c6.setConstraintValueType(BaseSingleFieldConstraint.TYPE_RET_VALUE);
        p6.getChildColumns().add(c6);
        model.getConditions().add(p6);

        assertTrue(utils.isConstraintValid(rnc));
        assertTrue(utils.isConstraintValid(dc));
        assertTrue(utils.isConstraintValid(mdc));
        assertTrue(utils.isConstraintValid(ac));
        assertTrue(utils.isConstraintValid(asfc));
        assertTrue(utils.isConstraintValid(aifc));

        assertFalse(utils.isConstraintValid(c1));
        assertFalse(utils.isConstraintValid(c2));
        assertFalse(utils.isConstraintValid(c3));
        assertTrue(utils.isConstraintValid(c4));
        assertTrue(utils.isConstraintValid(c5));
        assertTrue(utils.isConstraintValid(c6));
    }

    @SuppressWarnings("serial")
    @Test
    public void testConditionPredicateChoices() {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final ModuleDataModelOracle loader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
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
        dataModel.setModelFields(loader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);
        final ColumnUtilities utils = new ColumnUtilities(model,
                                                          oracle);

        final ConditionCol52 c1 = new ConditionCol52();
        final Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_PREDICATE);
        c1.setFieldType(DataType.TYPE_STRING);
        c1.setValueList("age>10,age>20,age>30");
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        assertTrue(utils.getValueList(c1).length > 0);
        assertTrue(utils.getValueList(c1).length == 3);
        assertEquals("age>10",
                     utils.getValueList(c1)[0]);
        assertEquals("age>20",
                     utils.getValueList(c1)[1]);
        assertEquals("age>30",
                     utils.getValueList(c1)[2]);
    }

    @SuppressWarnings("serial")
    @Test
    public void testConditionFormulaChoices() {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final ModuleDataModelOracle loader = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator())
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
        dataModel.setModelFields(loader.getModuleModelFields());
        populateDataModelOracle(mock(Path.class),
                                model,
                                oracle,
                                dataModel);
        final ColumnUtilities utils = new ColumnUtilities(model,
                                                          oracle);

        final ConditionCol52 c1 = new ConditionCol52();
        final Pattern52 p1 = new Pattern52();
        p1.setBoundName("c1");
        p1.setFactType("Driver");
        c1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_RET_VALUE);
        c1.setFieldType(DataType.TYPE_STRING);
        c1.setValueList("getAge()>10,getAge()>20,getAge()>30");
        p1.getChildColumns().add(c1);
        model.getConditions().add(p1);

        assertTrue(utils.getValueList(c1).length > 0);
        assertTrue(utils.getValueList(c1).length == 3);
        assertEquals("getAge()>10",
                     utils.getValueList(c1)[0]);
        assertEquals("getAge()>20",
                     utils.getValueList(c1)[1]);
        assertEquals("getAge()>30",
                     utils.getValueList(c1)[2]);
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
