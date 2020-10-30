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

package org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.definitions;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;

import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.MockInstanceImpl;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ColumnDefinitionFactoryTest {

    @Mock
    private GuidedDecisionTableEditorService service;
    private Caller<GuidedDecisionTableEditorService> serviceCaller;

    @Mock
    private GuidedDecisionTableView.Presenter dtPresenter;
    private GuidedDecisionTable52 model;

    @Mock
    private AsyncPackageDataModelOracle dmo;

    private ColumnDefinitionBuilder conditionCol52DefinitionBuilder;
    private ColumnDefinitionBuilder actionInsertFactCol52DefinitionBuilder;
    private ColumnDefinitionBuilder actionSetFieldCol52DefinitionBuilder;
    private ColumnDefinitionBuilder actionRetractFactCol52DefinitionBuilder;

    private ColumnDefinitionFactory columnDefinitionFactory;

    @Before
    public void setup() {
        this.model = new GuidedDecisionTable52();
        this.serviceCaller = new CallerMock<>(service);

        this.conditionCol52DefinitionBuilder = spy(new ConditionCol52DefinitionBuilder(serviceCaller));
        this.actionInsertFactCol52DefinitionBuilder = spy(new ActionInsertFactCol52DefinitionBuilder(serviceCaller));
        this.actionSetFieldCol52DefinitionBuilder = spy(new ActionSetFieldCol52DefinitionBuilder(serviceCaller));
        this.actionRetractFactCol52DefinitionBuilder = spy(new ActionRetractFactCol52DefinitionBuilder(serviceCaller));

        when(dtPresenter.getModel()).thenReturn(model);
        when(dtPresenter.getDataModelOracle()).thenReturn(dmo);
        when(service.toSource(any(),
                              any(GuidedDecisionTable52.class))).thenReturn("source");

        final Instance<ColumnDefinitionBuilder> buildersInstance = makeBuildersInstance();
        this.columnDefinitionFactory = new ColumnDefinitionFactory(buildersInstance);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void unknownColumnTypeDoesNotTriggerBuilder() {
        final Callback<String> callback = mock(Callback.class);
        columnDefinitionFactory.generateColumnDefinition(dtPresenter,
                                                         new RowNumberCol52(),
                                                         callback);
        verify(callback,
               never()).callback(any(String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void knownColumnType_ConditionCol52() {
        final Pattern52 p = new Pattern52();
        final ConditionCol52 column = new ConditionCol52();
        p.getChildColumns().add(column);
        model.getConditions().add(p);

        final Callback<String> callback = mock(Callback.class);
        columnDefinitionFactory.generateColumnDefinition(dtPresenter,
                                                         column,
                                                         callback);
        verify(conditionCol52DefinitionBuilder,
               times(1)).generateDefinition(eq(dtPresenter),
                                            eq(column),
                                            any(Callback.class));
        verify(callback,
               times(1)).callback(any(String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void knownColumnType_ActionInsertFactCol52() {
        final BaseColumn column = new ActionInsertFactCol52();
        final Callback<String> callback = mock(Callback.class);
        columnDefinitionFactory.generateColumnDefinition(dtPresenter,
                                                         column,
                                                         callback);
        verify(actionInsertFactCol52DefinitionBuilder,
               times(1)).generateDefinition(eq(dtPresenter),
                                            eq(column),
                                            any(Callback.class));
        verify(callback,
               times(1)).callback(any(String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void knownColumnType_ActionSetFieldCol52() {
        final BaseColumn column = new ActionSetFieldCol52();
        final Callback<String> callback = mock(Callback.class);
        columnDefinitionFactory.generateColumnDefinition(dtPresenter,
                                                         column,
                                                         callback);
        verify(actionSetFieldCol52DefinitionBuilder,
               times(1)).generateDefinition(eq(dtPresenter),
                                            eq(column),
                                            any(Callback.class));
        verify(callback,
               times(1)).callback(any(String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void knownColumnType_ActionRetractFactCol52() {
        final BaseColumn column = new ActionRetractFactCol52();
        final Callback<String> callback = mock(Callback.class);
        columnDefinitionFactory.generateColumnDefinition(dtPresenter,
                                                         column,
                                                         callback);
        verify(actionRetractFactCol52DefinitionBuilder,
               times(1)).generateDefinition(eq(dtPresenter),
                                            eq(column),
                                            any(Callback.class));
        verify(callback,
               times(1)).callback(any(String.class));
    }

    private Instance<ColumnDefinitionBuilder> makeBuildersInstance() {
        final List<ColumnDefinitionBuilder> builders = new ArrayList<>();
        builders.add(conditionCol52DefinitionBuilder);
        builders.add(actionInsertFactCol52DefinitionBuilder);
        builders.add(actionSetFieldCol52DefinitionBuilder);
        builders.add(actionRetractFactCol52DefinitionBuilder);

        return new MockInstanceImpl<>(builders);
    }
}
