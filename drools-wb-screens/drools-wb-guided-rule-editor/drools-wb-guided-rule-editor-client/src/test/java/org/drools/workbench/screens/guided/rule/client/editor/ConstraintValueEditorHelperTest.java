/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.inject.Instance;

import org.assertj.core.api.Assertions;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.screens.guided.rule.client.editor.util.ConstraintValueEditorHelper;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.OperatorsOracle;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mocks.CallerMock;

import static org.drools.workbench.screens.guided.rule.client.util.ModelFieldUtil.modelField;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConstraintValueEditorHelperTest {

    private RuleModel model;

    @Mock
    private IncrementalDataModelService service;
    private Caller<IncrementalDataModelService> serviceCaller;

    @Mock
    private Instance<DynamicValidator> validatorInstance;

    @Before
    public void setUp() throws Exception {
        model = new RuleModel();
        serviceCaller = new CallerMock<>(service);
    }

    @Test
    public void testSimplePattern() throws Exception {
        AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);

        FactPattern pattern = new FactPattern();
        pattern.setBoundName("pp");
        pattern.setFactType("House");
        model.addLhsItem(pattern);

        FactPattern pattern2 = new FactPattern();
        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setFactType("House");
        constraint.setFieldName("this");
        constraint.setFieldType("org.mortgages.House");
        pattern2.addConstraint(constraint);
        model.addLhsItem(pattern);

        when(
                oracle.getFieldClassName("House",
                                         "this")
        ).thenReturn(
                "org.mortgages.House"
        );

        ConstraintValueEditorHelper helper = new ConstraintValueEditorHelper(model,
                                                                             oracle,
                                                                             "House",
                                                                             "this",
                                                                             constraint,
                                                                             "House",
                                                                             new DropDownData());

        helper.isApplicableBindingsInScope("pp",
                                           new Callback<Boolean>() {
                                               @Override
                                               public void callback(Boolean result) {
                                                   assertTrue(result);
                                               }
                                           });
    }

    @Test
    public void testSimpleField() throws Exception {
        AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);

        FactPattern pattern = new FactPattern();
        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setFieldBinding("pp");
        constraint.setFactType("House");
        constraint.setFieldName("parent");
        constraint.setFieldType("org.mortgages.Parent");
        pattern.addConstraint(constraint);
        model.addLhsItem(pattern);

        when(
                oracle.getFieldClassName("House",
                                         "parent")
        ).thenReturn(
                "org.mortgages.Parent"
        );

        ConstraintValueEditorHelper helper = new ConstraintValueEditorHelper(model,
                                                                             oracle,
                                                                             "House",
                                                                             "parent",
                                                                             constraint,
                                                                             "Parent",
                                                                             new DropDownData());

        helper.isApplicableBindingsInScope("pp",
                                           new Callback<Boolean>() {
                                               @Override
                                               public void callback(Boolean result) {
                                                   assertTrue(result);
                                               }
                                           });
    }

    @Test
    public void testBoundFieldOfDifferentType() throws Exception {
        final AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);

        final FactPattern pattern = new FactPattern();
        final SingleFieldConstraint isFinished = new SingleFieldConstraint();
        isFinished.setFieldBinding("$finished");
        isFinished.setFactType("House");
        isFinished.setFieldName("finished");
        isFinished.setFieldType("java.time.LocalDate");
        isFinished.setOperator("!= null");
        pattern.addConstraint(isFinished);
        model.addLhsItem(pattern);

        final SingleFieldConstraint isFinishedAfter = new SingleFieldConstraint();
        isFinishedAfter.setFactType("House");
        isFinishedAfter.setFieldName("cost");
        isFinishedAfter.setFieldType("java.util.BigDecimal");
        isFinishedAfter.setOperator("==");
        pattern.addConstraint(isFinishedAfter);
        model.addLhsItem(pattern);

        when(oracle.getFieldClassName("House",
                                      "finished")).thenReturn("java.time.LocalDate");
        when(oracle.getFieldClassName("House",
                                      "cost")).thenReturn("java.util.BigDecimal");

        ConstraintValueEditorHelper helper = new ConstraintValueEditorHelper(model,
                                                                             oracle,
                                                                             "House",
                                                                             "cost",
                                                                             isFinished,
                                                                             "LocalDate",
                                                                             new DropDownData());

        helper.isApplicableBindingsInScope("$finished",
                                           result -> assertFalse(result));
    }

    @Test
    public void testBoundFieldOfSameType() throws Exception {
        final AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);

        final FactPattern pattern = new FactPattern();
        final SingleFieldConstraint isFinished = new SingleFieldConstraint();
        isFinished.setFieldBinding("$finished");
        isFinished.setFactType("House");
        isFinished.setFieldName("finished");
        isFinished.setFieldType("java.time.LocalDate");
        isFinished.setOperator("!= null");
        pattern.addConstraint(isFinished);
        model.addLhsItem(pattern);

        final SingleFieldConstraint isFinishedAfter = new SingleFieldConstraint();
        isFinishedAfter.setFactType("House");
        isFinishedAfter.setFieldName("bought");
        isFinishedAfter.setFieldType("java.time.LocalDate");
        isFinishedAfter.setOperator("==");
        pattern.addConstraint(isFinishedAfter);
        model.addLhsItem(pattern);

        when(oracle.getFieldClassName("House",
                                      "finished")).thenReturn("java.time.LocalDate");
        when(oracle.getFieldClassName("House",
                                      "bought")).thenReturn("java.time.LocalDate");

        ConstraintValueEditorHelper helper = new ConstraintValueEditorHelper(model,
                                                                             oracle,
                                                                             "House",
                                                                             "bought",
                                                                             isFinished,
                                                                             "LocalDate",
                                                                             new DropDownData());

        helper.isApplicableBindingsInScope("$finished",
                                           result -> assertTrue(result));
    }

    @Test
    public void testEvents_BothTypesAreEvents() throws Exception {
        AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(serviceCaller,
                                                                                 validatorInstance);
        oracle.setPackageName("org.test");

        oracle.addModelFields(new HashMap<String, ModelField[]>() {{
            put("org.test.Event1",
                new ModelField[]{modelField("this",
                                            "org.test.Event1")});
            put("org.test.Event2",
                new ModelField[]{modelField("this",
                                            "org.test.Event2")});
        }});

        oracle.addEventTypes(new HashMap<String, Boolean>() {{
            put("org.test.Event1",
                true);
            put("org.test.Event2",
                true);
        }});
        oracle.filter();

        FactPattern pattern1 = new FactPattern();
        pattern1.setFactType("Event1");
        pattern1.setBoundName("$e");

        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setFactType("Event1");
        constraint.setFieldName("this");
        constraint.setFieldType("Event1");
        constraint.setOperator(OperatorsOracle.SIMPLE_CEP_OPERATORS[0]);
        pattern1.addConstraint(constraint);

        model.addLhsItem(pattern1);

        ConstraintValueEditorHelper helper = new ConstraintValueEditorHelper(model,
                                                                             oracle,
                                                                             "Event2",
                                                                             "this",
                                                                             constraint,
                                                                             "Event2",
                                                                             new DropDownData());

        helper.isApplicableBindingsInScope("$e",
                                           new Callback<Boolean>() {
                                               @Override
                                               public void callback(Boolean result) {
                                                   assertTrue(result);
                                               }
                                           });
    }

    @Test
    public void testEvents_BoundTypeIsEvent() throws Exception {
        AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(serviceCaller,
                                                                                 validatorInstance);
        oracle.setPackageName("org.test");

        oracle.addModelFields(new HashMap<String, ModelField[]>() {{
            put("org.test.Event1",
                new ModelField[]{modelField("this",
                                            "org.test.Event1")});
            put("org.test.Event2",
                new ModelField[]{modelField("this",
                                            "org.test.Event2")});
        }});

        oracle.addEventTypes(new HashMap<String, Boolean>() {{
            put("org.test.Event1",
                true);
            put("org.test.Event2",
                false);
        }});
        oracle.addSuperTypes(new HashMap<String, List<String>>() {{
            put("org.test.Event1",
                Collections.EMPTY_LIST);
            put("org.test.Event2",
                Collections.EMPTY_LIST);
        }});
        oracle.filter();

        FactPattern pattern1 = new FactPattern();
        pattern1.setFactType("Event1");
        pattern1.setBoundName("$e");

        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setFactType("Event1");
        constraint.setFieldName("this");
        constraint.setFieldType("Event1");
        constraint.setOperator(OperatorsOracle.SIMPLE_CEP_OPERATORS[0]);
        pattern1.addConstraint(constraint);

        model.addLhsItem(pattern1);

        ConstraintValueEditorHelper helper = new ConstraintValueEditorHelper(model,
                                                                             oracle,
                                                                             "Event2",
                                                                             "this",
                                                                             constraint,
                                                                             "Event2",
                                                                             new DropDownData());

        helper.isApplicableBindingsInScope("$e",
                                           new Callback<Boolean>() {
                                               @Override
                                               public void callback(Boolean result) {
                                                   assertFalse(result);
                                               }
                                           });
    }

    @Test
    public void testEvents_BoundTypeIsNotEvent() throws Exception {
        AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(serviceCaller,
                                                                                 validatorInstance);
        oracle.setPackageName("org.test");

        oracle.addModelFields(new HashMap<String, ModelField[]>() {{
            put("org.test.Event1",
                new ModelField[]{modelField("this",
                                            "org.test.Event1")});
            put("org.test.Event2",
                new ModelField[]{modelField("this",
                                            "org.test.Event2")});
        }});

        oracle.addEventTypes(new HashMap<String, Boolean>() {{
            put("org.test.Event1",
                false);
            put("org.test.Event2",
                true);
        }});
        oracle.addSuperTypes(new HashMap<String, List<String>>() {{
            put("org.test.Event1",
                Collections.EMPTY_LIST);
            put("org.test.Event2",
                Collections.EMPTY_LIST);
        }});
        oracle.filter();

        FactPattern pattern1 = new FactPattern();
        pattern1.setFactType("Event1");
        pattern1.setBoundName("$e");

        SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setFactType("Event1");
        constraint.setFieldName("this");
        constraint.setFieldType("Event1");
        constraint.setOperator(OperatorsOracle.SIMPLE_CEP_OPERATORS[0]);
        pattern1.addConstraint(constraint);

        model.addLhsItem(pattern1);

        ConstraintValueEditorHelper helper = new ConstraintValueEditorHelper(model,
                                                                             oracle,
                                                                             "Event2",
                                                                             "this",
                                                                             constraint,
                                                                             "Event2",
                                                                             new DropDownData());

        helper.isApplicableBindingsInScope("$e",
                                           new Callback<Boolean>() {
                                               @Override
                                               public void callback(Boolean result) {
                                                   assertFalse(result);
                                               }
                                           });
    }

    @Test
    public void isEnumEquivalentBothNull() throws Exception {
        final DropDownData dropDownData = mock(DropDownData.class);
        doReturn(null).when(dropDownData).getFixedList();
        Assertions.assertThat(ConstraintValueEditorHelper.isEnumEquivalent(null, dropDownData)).isTrue();
    }

    @Test
    public void isEnumEquivalentFirstNull() throws Exception {
        final DropDownData dropDownData = mock(DropDownData.class);
        doReturn(new String[0]).when(dropDownData).getFixedList();
        Assertions.assertThat(ConstraintValueEditorHelper.isEnumEquivalent(null, dropDownData)).isFalse();
    }

    @Test
    public void isEnumEquivalentSecondNull() throws Exception {
        final DropDownData dropDownData = mock(DropDownData.class);
        doReturn(null).when(dropDownData).getFixedList();
        Assertions.assertThat(ConstraintValueEditorHelper.isEnumEquivalent(new String[0], dropDownData)).isFalse();
    }

    @Test
    public void isEnumEquivalentDifferentLength() throws Exception {
        final DropDownData dropDownData = mock(DropDownData.class);
        doReturn(new String[0]).when(dropDownData).getFixedList();
        Assertions.assertThat(ConstraintValueEditorHelper.isEnumEquivalent(new String[1], dropDownData)).isFalse();
    }

    @Test
    public void isEnumEquivalentDifferentContent() throws Exception {
        final DropDownData dropDownData = mock(DropDownData.class);
        doReturn(new String[]{"a"}).when(dropDownData).getFixedList();
        Assertions.assertThat(ConstraintValueEditorHelper.isEnumEquivalent(new String[]{"b"}, dropDownData)).isFalse();
    }

    @Test
    public void isEnumEquivalentSameContent() throws Exception {
        final DropDownData dropDownData = mock(DropDownData.class);
        doReturn(new String[]{"a", "b", "c"}).when(dropDownData).getFixedList();
        Assertions.assertThat(ConstraintValueEditorHelper.isEnumEquivalent(new String[]{"a", "b", "c"}, dropDownData)).isTrue();
    }

    @Test
    public void isEnumEquivalentNullDropDownData() throws Exception {
        final DropDownData dropDownData = null;
        Assertions.assertThat(ConstraintValueEditorHelper.isEnumEquivalent(new String[]{"a", "b", "c"}, dropDownData)).isFalse();
    }
}
