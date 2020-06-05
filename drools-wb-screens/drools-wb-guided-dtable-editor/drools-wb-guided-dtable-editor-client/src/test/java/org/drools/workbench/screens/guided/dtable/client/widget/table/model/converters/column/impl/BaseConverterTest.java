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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.BaseColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.junit.Before;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;

public abstract class BaseConverterTest {

    @Mock
    protected GuidedDecisionTableView gridWidget;
    protected GuidedDecisionTableView.Presenter presenter;

    protected GuidedDecisionTable52 model;
    protected AsyncPackageDataModelOracle oracle;
    protected ColumnUtilities columnUtilities;

    private final List<BaseColumnConverter> converters = new ArrayList<BaseColumnConverter>();

    @Before
    public void setup() {
        this.model = getModel();
        this.oracle = getOracle();
        this.presenter = getPresenter();
        this.columnUtilities = new ColumnUtilities( model,
                                                    oracle );
        for ( BaseColumnConverter bcc : getConverters() ) {
            bcc.initialise( model,
                            oracle,
                            columnUtilities,
                            presenter );
        }
    }

    protected abstract GuidedDecisionTable52 getModel();

    protected abstract AsyncPackageDataModelOracle getOracle();

    protected abstract GuidedDecisionTableView.Presenter getPresenter();

    protected List<BaseColumnConverter> getConverters() {
        if ( !converters.isEmpty() ) {
            return converters;
        }
        converters.add( new ActionInsertFactColumnConverter() );
        converters.add( new ActionRetractFactColumnConverter() );
        converters.add( new ActionSetFieldColumnConverter() );
        converters.add( new ActionWorkItemExecuteColumnConverter() );
        converters.add( new ActionWorkItemInsertFactColumnConverter() );
        converters.add( new ActionWorkItemSetFieldColumnConverter() );
        converters.add( new AttributeColumnConverter() );
        converters.add( new BRLActionVariableColumnConverter() );
        converters.add( new BRLConditionVariableColumnConverter() );
        converters.add( new ConditionColumnConverter() );
        converters.add( new DescriptionColumnConverter() );
        converters.add( new LimitedEntryColumnConverter() );
        converters.add( new MetaDataColumnConverter() );
        converters.add( new RuleNameColumnConverter() );
        converters.add( new RowNumberColumnConverter() );
        return converters;
    }

}
