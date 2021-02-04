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

import org.drools.workbench.models.guided.dtable.backend.GuidedDTDRLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class BaseColumnDefinitionBuilderTest {

    @Mock
    protected GuidedDecisionTableEditorService service;
    protected Caller<GuidedDecisionTableEditorService> serviceCaller;

    @Mock
    protected GuidedDecisionTableView.Presenter dtPresenter;
    protected GuidedDecisionTable52 model;

    @Mock
    protected AsyncPackageDataModelOracle dmo;

    protected ColumnDefinitionBuilder builder;

    @Before
    public void setup() {
        this.model = new GuidedDecisionTable52();
        this.serviceCaller = new CallerMock<>(service);

        final ColumnDefinitionBuilder wrapped = getBuilder();
        this.builder = spy(wrapped);

        when(service.toSource(any(),
                              any(GuidedDecisionTable52.class))).thenAnswer((InvocationOnMock invocation) -> {
            final GuidedDecisionTable52 model = (GuidedDecisionTable52) invocation.getArguments()[1];
            return GuidedDTDRLPersistence.getInstance().marshal(model);
        });
        when(dtPresenter.getModel()).thenReturn(model);
        when(dtPresenter.getDataModelOracle()).thenReturn(dmo);
    }

    protected abstract ColumnDefinitionBuilder getBuilder();

    protected void setupPatternAndCondition() {
        final Pattern52 p = new Pattern52();
        p.setFactType("Person");
        p.setBoundName("$p");
        final ConditionCol52 cc = new ConditionCol52();
        cc.setFactField("name");
        cc.setOperator("==");
        cc.setFieldType(DataType.TYPE_STRING);
        p.getChildColumns().add(cc);
        model.getConditions().add(p);
    }

    protected void setupLimitedEntryPatternAndCondition() {
        final Pattern52 p = new Pattern52();
        p.setFactType("Person");
        p.setBoundName("$p");
        final LimitedEntryConditionCol52 cc = new LimitedEntryConditionCol52();
        cc.setFactField("name");
        cc.setOperator("==");
        cc.setFieldType(DataType.TYPE_STRING);
        cc.setValue(new DTCellValue52("Michael"));
        p.getChildColumns().add(cc);
        model.getConditions().add(p);
    }
}
