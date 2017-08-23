/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.wizard.table.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.appformer.project.datamodel.oracle.DataType;
import org.appformer.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.appformer.project.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.drools.workbench.screens.guided.dtable.client.wizard.table.pages.events.ConditionsDefinedEvent;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FactPatternConstraintsPageTest {

    private List<ConditionCol52> conditions;

    private AsyncPackageDataModelOracleImpl oracle;

    @Mock
    private IncrementalDataModelService incrementalDataModelService;

    private Caller<IncrementalDataModelService> incrementalDataModelServiceCaller;

    @Mock
    private Pattern52 pattern;

    @Mock
    private Pattern52 secondPattern;

    @Mock
    private ConditionCol52 conditionCol;

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private FactPatternConstraintsPageView view;

    @Mock
    private Validator validator;

    private Event<ConditionsDefinedEvent> conditionsDefinedEvent = mock(EventSourceMock.class);

    @Captor
    private ArgumentCaptor<ConditionsDefinedEvent> conditionsDefinedEventCaptor;

    @Spy
    @InjectMocks
    private FactPatternConstraintsPage page;

    @Captor
    private ArgumentCaptor<List<AvailableField>> availableFieldsCaptor;

    private ModelField[] modelFields;

    @Before
    public void setUp() throws Exception {

        incrementalDataModelServiceCaller = new CallerMock<>(incrementalDataModelService);
        oracle = new AsyncPackageDataModelOracleImpl(incrementalDataModelServiceCaller,
                                                     null);

        page.oracle = oracle;
        page.model = model;
        page.validator = validator;

        when(pattern.getFactType()).thenReturn("org.Address");

        when(conditionCol.getFactField()).thenReturn("street");

        when(model.getPatterns()).thenReturn(Arrays.asList(pattern,
                                                           secondPattern));

        modelFields = new ModelField[]{
                new ModelField("this",
                               "org.Address",
                               ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                               ModelField.FIELD_ORIGIN.SELF,
                               FieldAccessorsAndMutators.ACCESSOR,
                               DataType.TYPE_THIS),
                new ModelField("street",
                               String.class.getName(),
                               ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                               ModelField.FIELD_ORIGIN.DECLARED,
                               FieldAccessorsAndMutators.BOTH,
                               DataType.TYPE_STRING),
                new ModelField("homeAddress",
                               Boolean.class.getName(),
                               ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                               ModelField.FIELD_ORIGIN.DECLARED,
                               FieldAccessorsAndMutators.BOTH,
                               DataType.TYPE_BOOLEAN),
                new ModelField("number",
                               Integer.class.getName(),
                               ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                               ModelField.FIELD_ORIGIN.DECLARED,
                               FieldAccessorsAndMutators.BOTH,
                               DataType.TYPE_NUMERIC_INTEGER)};

        Map<String, ModelField[]> fields = new HashMap<>();
        fields.put("org.Address",
                   modelFields);

        oracle.addModelFields(fields);

        conditions = new ArrayList<>();
        conditions.add(mock(ConditionCol52.class));
        when(pattern.getChildColumns()).thenReturn(conditions);
        when(secondPattern.getChildColumns()).thenReturn(conditions);
    }

    @Test
    public void testPrepareView() throws Exception {
        page.prepareView();

        verify(view).setAvailablePatterns(Arrays.asList(pattern,
                                                        secondPattern));
    }

    @Test
    public void testIsComplete() throws Exception {
        testConditionCompletion(true);
    }

    @Test
    public void testIsNotComplete() throws Exception {
        testConditionCompletion(false);
    }

    private void testConditionCompletion(boolean conditionsCompleted) {
        when(validator.isConditionValid(any(ConditionCol52.class))).thenReturn(conditionsCompleted);

        Callback<Boolean> callback = mock(Callback.class);

        page.isComplete(callback);

        verify(conditionsDefinedEvent).fire(conditionsDefinedEventCaptor.capture());
        assertEquals(conditionsCompleted,
                     conditionsDefinedEventCaptor.getValue().getAreConditionsDefined());

        verify(callback).callback(conditionsCompleted);
    }

    @Test
    public void testSelectPattern() throws Exception {
        page.selectPattern(pattern);

        verify(view).setAvailableFields(availableFieldsCaptor.capture());

        List<AvailableField> availableFields = availableFieldsCaptor.getValue();

        assertEquals(4,
                     availableFields.size());

        assertTrue(availableFields.stream()
                           .allMatch(field ->
                                             field.getCalculationType() == BaseSingleFieldConstraint.TYPE_LITERAL &&
                                                     ((field.getName().compareTo("this") == 0 &&
                                                             field.getType().compareTo(DataType.TYPE_THIS) == 0) ||
                                                             (field.getName().compareTo("street") == 0 &&
                                                                     field.getType().compareTo(String.class.getSimpleName()) == 0) ||
                                                             (field.getName().compareTo("homeAddress") == 0 &&
                                                                     field.getType().compareTo(Boolean.class.getSimpleName()) == 0) ||
                                                             (field.getName().compareTo("number") == 0 &&
                                                                     field.getType().compareTo(Integer.class.getSimpleName()) == 0))
                           )
        );

        verify(view).setChosenConditions(conditions);
    }

    @Test
    public void testGetOperatorCompletionsLiteral() throws Exception {
        when(conditionCol.getConstraintValueType()).thenReturn(BaseSingleFieldConstraint.TYPE_LITERAL);
        Callback<String[]> callback = mock(Callback.class);

        page.getOperatorCompletions(pattern,
                                    conditionCol,
                                    callback);

        verify(callback).callback(new String[]{"==", "!=", "<", ">", "<=", ">=", "matches", "soundslike", "== null", "!= null", "in", "not in"});
    }

    @Test
    public void testGetOperatorCompletionsPredicate() throws Exception {
        when(conditionCol.getConstraintValueType()).thenReturn(BaseSingleFieldConstraint.TYPE_PREDICATE);
        Callback<String[]> callback = mock(Callback.class);

        page.getOperatorCompletions(pattern,
                                    conditionCol,
                                    callback);

        verify(callback).callback(new String[]{"==", "!=", "<", ">", "<=", ">=", "matches", "soundslike", "== null", "!= null"});
    }

    @Test
    public void testSetChosenConditions() throws Exception {
        ConditionCol52 a = mock(ConditionCol52.class);
        ConditionCol52 b = mock(ConditionCol52.class);

        assertEquals(1,
                     conditions.size());

        page.setChosenConditions(pattern,
                                 Arrays.asList(a,
                                               b));

        assertEquals(2,
                     conditions.size());
        assertEquals(a,
                     conditions.get(0));
        assertEquals(b,
                     conditions.get(1));
    }
}
