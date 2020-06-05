/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.inject.Instance;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RuleNameColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.GridWidgetCellFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.impl.GridWidgetCellFactoryImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.BaseColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.GridWidgetColumnFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ActionInsertFactColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ActionRetractFactColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ActionSetFieldColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ActionWorkItemExecuteColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ActionWorkItemInsertFactColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ActionWorkItemSetFieldColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.AttributeColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.BRLActionVariableColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.BRLConditionVariableColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ConditionColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.DescriptionColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.GridWidgetColumnFactoryImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.LimitedEntryColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.MetaDataColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.RowNumberColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.RuleNameColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.DependentEnumsUtilities;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.mocks.CallerMock;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer.MetaData;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public abstract class BaseSynchronizerTest {

    protected GuidedDecisionTable52 model;
    protected GuidedDecisionTableUiModel uiModel;
    protected ModelSynchronizerImpl modelSynchronizer = new ModelSynchronizerImpl();
    protected GridWidgetColumnFactory gridWidgetColumnFactory = new GridWidgetColumnFactoryImpl();
    protected AsyncPackageDataModelOracle oracle = getOracle();

    @Mock
    protected IncrementalDataModelService incrementalDataModelService;
    protected Caller<IncrementalDataModelService> incrementalDataModelServiceCaller;

    protected Instance<DynamicValidator> validatorInstance = new MockDynamicValidatorInstance();

    protected final GuidedDecisionTableView view = mock(GuidedDecisionTableView.class);

    private GuidedDecisionTablePresenter.Access editable = new GuidedDecisionTablePresenter.Access();

    protected GuidedDecisionTablePresenter.Access readOnly = new GuidedDecisionTablePresenter.Access() {{
        setReadOnly(true);
    }};

    @Before
    public void setup() {
        //Setup model related classes
        model = new GuidedDecisionTable52();
        uiModel = new GuidedDecisionTableUiModel(modelSynchronizer);
        incrementalDataModelServiceCaller = new CallerMock<>(incrementalDataModelService);

        final BRLRuleModel rm = new BRLRuleModel(model);
        final CellUtilities cellUtilities = new CellUtilities();
        final ColumnUtilities columnUtilities = new ColumnUtilities(model,
                                                                    oracle);
        final DependentEnumsUtilities enumsUtilities = new DependentEnumsUtilities(model,
                                                                                   oracle);
        final GridWidgetCellFactory gridWidgetCellFactory = new GridWidgetCellFactoryImpl();

        //Setup mocks
        final GuidedDecisionTableModellerView.Presenter modellerPresenter = mock(GuidedDecisionTableModellerView.Presenter.class);
        final GuidedDecisionTableModellerView modellerView = mock(GuidedDecisionTableModellerView.class);
        final GridLayer gridLayer = mock(GridLayer.class);
        final AbsolutePanel domElementContainer = mock(AbsolutePanel.class);
        final GuidedDecisionTableView.Presenter dtablePresenter = mock(GuidedDecisionTableView.Presenter.class);
        final EventBus eventBus = mock(EventBus.class);

        when(dtablePresenter.getModellerPresenter()).thenReturn(modellerPresenter);
        when(modellerPresenter.getView()).thenReturn(modellerView);
        when(modellerView.getGridLayerView()).thenReturn(gridLayer);
        when(gridLayer.getDomElementContainer()).thenReturn(domElementContainer);
        when(domElementContainer.iterator()).thenReturn(mock(Iterator.class));

        //Setup column converters
        final List<BaseColumnConverter> converters = getConverters();
        gridWidgetColumnFactory.setConverters(converters);
        gridWidgetColumnFactory.initialise(model,
                                           oracle,
                                           columnUtilities,
                                           dtablePresenter);

        //Setup synchronizers
        final List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> synchronizers = getSynchronizers();
        modelSynchronizer.setSynchronizers(synchronizers);
        modelSynchronizer.initialise(model,
                                     uiModel,
                                     cellUtilities,
                                     columnUtilities,
                                     enumsUtilities,
                                     gridWidgetCellFactory,
                                     gridWidgetColumnFactory,
                                     view,
                                     rm,
                                     eventBus,
                                     editable);

        //Dummy columns for Row number, rule name and Description
        uiModel.appendColumn(gridWidgetColumnFactory.convertColumn(new RowNumberCol52(),
                                                                   readOnly,
                                                                   view));
        uiModel.appendColumn(gridWidgetColumnFactory.convertColumn(new RuleNameColumn(),
                                                                   readOnly,
                                                                   view));
        uiModel.appendColumn(gridWidgetColumnFactory.convertColumn(new DescriptionCol52(),
                                                                   readOnly,
                                                                   view));

        ApplicationPreferences.setUp(new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd-MM-yyyy");
        }});
    }

    protected AsyncPackageDataModelOracle getOracle() {
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(incrementalDataModelServiceCaller,
                                                                                       validatorInstance);
        return oracle;
    }

    protected List<BaseColumnConverter> getConverters() {
        final List<BaseColumnConverter> converters = new ArrayList<BaseColumnConverter>();
        converters.add(new ActionInsertFactColumnConverter());
        converters.add(new ActionRetractFactColumnConverter());
        converters.add(new ActionSetFieldColumnConverter());
        converters.add(new ActionWorkItemExecuteColumnConverter());
        converters.add(new ActionWorkItemInsertFactColumnConverter());
        converters.add(new ActionWorkItemSetFieldColumnConverter());
        converters.add(new AttributeColumnConverter());
        converters.add(new BRLActionVariableColumnConverter());
        converters.add(new BRLConditionVariableColumnConverter());
        converters.add(new ConditionColumnConverter());
        converters.add(new DescriptionColumnConverter());
        converters.add(new LimitedEntryColumnConverter());
        converters.add(new MetaDataColumnConverter());
        converters.add(new RuleNameColumnConverter());
        converters.add(new RowNumberColumnConverter());
        return converters;
    }

    protected List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> getSynchronizers() {
        final List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> synchronizers = new ArrayList<>();
        synchronizers.add(new ActionColumnSynchronizer());
        synchronizers.add(new ActionInsertFactColumnSynchronizer());
        synchronizers.add(new ActionRetractFactColumnSynchronizer());
        synchronizers.add(new ActionSetFieldColumnSynchronizer());
        synchronizers.add(new ActionWorkItemExecuteColumnSynchronizer());
        synchronizers.add(new ActionWorkItemInsertFactColumnSynchronizer());
        synchronizers.add(new ActionWorkItemSetFieldColumnSynchronizer());
        synchronizers.add(new AttributeColumnSynchronizer());
        synchronizers.add(new BRLActionColumnSynchronizer());
        synchronizers.add(new BRLConditionColumnSynchronizer());
        synchronizers.add(new ConditionColumnSynchronizer());
        synchronizers.add(new LimitedEntryBRLActionColumnSynchronizer());
        synchronizers.add(new LimitedEntryBRLConditionColumnSynchronizer());
        synchronizers.add(new MetaDataColumnSynchronizer());
        synchronizers.add(new RowSynchronizer());
        return synchronizers;
    }

    //It was not possible to mock Instance<DynamicValidator> with GwtMockitoTestRunner so we have a mock implementation
    private static class MockDynamicValidatorInstance implements Instance<DynamicValidator> {

        @Override
        public Instance<DynamicValidator> select(final Annotation... annotations) {
            return null;
        }

        @Override
        public <U extends DynamicValidator> Instance<U> select(final Class<U> aClass,
                                                               final Annotation... annotations) {
            return null;
        }

        @Override
        public boolean isUnsatisfied() {
            return true;
        }

        @Override
        public boolean isAmbiguous() {
            return false;
        }

        @Override
        public void destroy(final DynamicValidator dynamicValidator) {

        }

        @Override
        public Iterator<DynamicValidator> iterator() {
            return null;
        }

        @Override
        public DynamicValidator get() {
            return null;
        }
    }
}
