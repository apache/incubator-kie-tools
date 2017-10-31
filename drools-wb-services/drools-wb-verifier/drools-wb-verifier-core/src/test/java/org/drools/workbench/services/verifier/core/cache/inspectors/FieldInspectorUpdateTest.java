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

package org.drools.workbench.services.verifier.core.cache.inspectors;

import java.util.ArrayList;

import org.drools.workbench.services.verifier.api.client.index.Action;
import org.drools.workbench.services.verifier.api.client.index.Actions;
import org.drools.workbench.services.verifier.api.client.index.Column;
import org.drools.workbench.services.verifier.api.client.index.Condition;
import org.drools.workbench.services.verifier.api.client.index.Conditions;
import org.drools.workbench.services.verifier.api.client.index.DataType;
import org.drools.workbench.services.verifier.api.client.index.Field;
import org.drools.workbench.services.verifier.api.client.index.FieldAction;
import org.drools.workbench.services.verifier.api.client.index.FieldCondition;
import org.drools.workbench.services.verifier.api.client.index.ObjectField;
import org.drools.workbench.services.verifier.api.client.index.keys.Values;
import org.drools.workbench.services.verifier.core.checks.AnalyzerConfigurationMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FieldInspectorUpdateTest {


    @Mock
    ObjectField objectField;

    @Mock
    RuleInspectorUpdater ruleInspectorUpdater;
    private FieldCondition fieldCondition;
    private FieldAction fieldAction;


    private AnalyzerConfigurationMock configurationMock;

    @Before
    public void setUp() throws
                        Exception {

        configurationMock = new AnalyzerConfigurationMock();

        final Field field = spy( new Field( objectField,
                                            "org.Person",
                                            "String",
                                            "name",
                                            configurationMock ) );

        fieldCondition = makeCondition( field );
        fieldAction = makeAction( field );

        new FieldInspector( field,
                            ruleInspectorUpdater,
                            new AnalyzerConfigurationMock() );

    }

    private FieldCondition makeCondition( final Field field ) {
        final FieldCondition fieldAction = new FieldCondition( field,
                                                               mock( Column.class ),
                                                               "==",
                                                               new Values( 11 ),
                                                               configurationMock );
        final ArrayList<Condition> actionsList = new ArrayList<>();
        actionsList.add( fieldAction );
        final Conditions conditions = new Conditions( actionsList );
        when( field.getConditions() ).thenReturn( conditions );

        return fieldAction;
    }

    private FieldAction makeAction( final Field field ) {
        final FieldAction fieldAction = new FieldAction( field,
                                                         mock( Column.class ),
                                                         DataType.DataTypes.NUMERIC,
                                                         new Values( 11 ),
                                                         configurationMock );
        final ArrayList<Action> actionsList = new ArrayList<>();
        actionsList.add( fieldAction );
        final Actions actions = new Actions( actionsList );
        when( field.getActions() ).thenReturn( actions );

        return fieldAction;
    }

    @Test
    public void updateAction() throws
                               Exception {
        fieldAction.setValue( new Values( 20 ) );

        verify( ruleInspectorUpdater ).resetActionsInspectors();
    }

    @Test
    public void updateCondition() throws
                                  Exception {
        fieldCondition.setValue( new Values( 20 ) );

        verify( ruleInspectorUpdater ).resetConditionsInspectors();
    }
}