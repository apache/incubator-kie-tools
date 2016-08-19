/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Action;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Actions;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Column;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Condition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Conditions;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.FieldAction;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.FieldCondition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.ObjectField;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Values;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class FieldInspectorUpdateTest {

    @GwtMock
    AnalysisConstants analysisConstants;

    @GwtMock
    DateTimeFormat dateTimeFormat;

    @Mock
    ObjectField objectField;

    @Mock
    RuleInspectorUpdater ruleInspectorUpdater;
    private FieldCondition fieldCondition;
    private FieldAction    fieldAction;

    @Before
    public void setUp() throws Exception {

        final Map<String, String> preferences = new HashMap<String, String>();
        preferences.put( ApplicationPreferences.DATE_FORMAT, "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );

        final Field field = spy( new Field( objectField,
                                            "org.Person",
                                            "String",
                                            "name" ) );

        fieldCondition = makeCondition( field );
        fieldAction = makeAction( field );

        new FieldInspector( field,
                            ruleInspectorUpdater );

    }

    private FieldCondition makeCondition( final Field field ) {
        final FieldCondition fieldAction = new FieldCondition( field,
                                                               mock( Column.class ),
                                                               "==",
                                                               new Values( 11 ) );
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
                                                         new Values( 11 ) );
        final ArrayList<Action> actionsList = new ArrayList<>();
        actionsList.add( fieldAction );
        final Actions actions = new Actions( actionsList );
        when( field.getActions() ).thenReturn( actions );

        return fieldAction;
    }

    @Test
    public void updateAction() throws Exception {
        fieldAction.setValue( new Values( 20 ) );

        verify( ruleInspectorUpdater ).resetActionsInspectors();
    }

    @Test
    public void updateCondition() throws Exception {
        fieldCondition.setValue( new Values( 20 ) );

        verify( ruleInspectorUpdater ).resetConditionsInspectors();
    }
}