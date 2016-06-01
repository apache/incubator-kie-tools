/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.GridWidgetCellFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.impl.GridWidgetCellFactoryImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.BaseColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.GridWidgetColumnFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.DescriptionColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.GridWidgetColumnFactoryImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.RowNumberColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.DependentEnumsUtilities;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public abstract class BaseSynchronizerTest {

    protected GuidedDecisionTable52 model;
    protected GuidedDecisionTableUiModel uiModel;
    protected ModelSynchronizerImpl modelSynchronizer = new ModelSynchronizerImpl();
    protected GridWidgetColumnFactory gridWidgetColumnFactory = new GridWidgetColumnFactoryImpl();
    protected AsyncPackageDataModelOracle oracle = getOracle();

    private GuidedDecisionTablePresenter.Access editable = new GuidedDecisionTablePresenter.Access();

    private GuidedDecisionTablePresenter.Access readOnly = new GuidedDecisionTablePresenter.Access() {{
        setReadOnly( true );
    }};

    @Before
    public void setup() {
        //Setup model related classes
        model = new GuidedDecisionTable52();
        uiModel = new GuidedDecisionTableUiModel( modelSynchronizer );

        final BRLRuleModel rm = new BRLRuleModel( model );
        final CellUtilities cellUtilities = new CellUtilities();
        final ColumnUtilities columnUtilities = new ColumnUtilities( model,
                                                                     oracle );
        final DependentEnumsUtilities enumsUtilities = new DependentEnumsUtilities( model,
                                                                                    oracle );
        final GridWidgetCellFactory gridWidgetCellFactory = new GridWidgetCellFactoryImpl();

        //Setup mocks
        final GuidedDecisionTableModellerView.Presenter modellerPresenter = mock( GuidedDecisionTableModellerView.Presenter.class );
        final GuidedDecisionTableModellerView modellerView = mock( GuidedDecisionTableModellerView.class );
        final GridLayer gridLayer = mock( GridLayer.class );
        final AbsolutePanel domElementContainer = mock( AbsolutePanel.class );
        final GuidedDecisionTableView.Presenter dtablePresenter = mock( GuidedDecisionTableView.Presenter.class );
        final GuidedDecisionTableView view = mock( GuidedDecisionTableView.class );
        final EventBus eventBus = mock( EventBus.class );

        when( dtablePresenter.getModellerPresenter() ).thenReturn( modellerPresenter );
        when( modellerPresenter.getView() ).thenReturn( modellerView );
        when( modellerView.getGridLayerView() ).thenReturn( gridLayer );
        when( gridLayer.getDomElementContainer() ).thenReturn( domElementContainer );
        when( domElementContainer.iterator() ).thenReturn( mock( Iterator.class ) );

        //Setup column converters
        final List<BaseColumnConverter> converters = getConverters();
        converters.add( new RowNumberColumnConverter() );
        converters.add( new DescriptionColumnConverter() );
        gridWidgetColumnFactory.setConverters( converters );
        gridWidgetColumnFactory.initialise( model,
                                            oracle,
                                            columnUtilities,
                                            dtablePresenter );

        //Setup synchronizers
        final List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> synchronizers = getSynchronizers();
        modelSynchronizer.setSynchronizers( synchronizers );
        modelSynchronizer.initialise( model,
                                      uiModel,
                                      cellUtilities,
                                      columnUtilities,
                                      enumsUtilities,
                                      gridWidgetCellFactory,
                                      gridWidgetColumnFactory,
                                      view,
                                      rm,
                                      eventBus,
                                      editable );

        //Dummy columns for Row number and Description
        uiModel.appendColumn( gridWidgetColumnFactory.convertColumn( new RowNumberCol52(),
                                                                     readOnly,
                                                                     view ) );
        uiModel.appendColumn( gridWidgetColumnFactory.convertColumn( new DescriptionCol52(),
                                                                     readOnly,
                                                                     view ) );

        ApplicationPreferences.setUp( new HashMap<String, String>() {{
            put( ApplicationPreferences.DATE_FORMAT,
                 "dd-MM-yyyy" );
        }} );
    }

    protected abstract AsyncPackageDataModelOracle getOracle();

    protected abstract List<BaseColumnConverter> getConverters();

    protected abstract List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> getSynchronizers();

}
