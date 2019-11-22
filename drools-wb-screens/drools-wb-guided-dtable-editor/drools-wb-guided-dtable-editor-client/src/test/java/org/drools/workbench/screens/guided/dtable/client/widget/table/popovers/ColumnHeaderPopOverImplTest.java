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

package org.drools.workbench.screens.guided.dtable.client.widget.table.popovers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.Instance;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.definitions.ColumnDefinitionBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.definitions.ColumnDefinitionFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.definitions.ConditionCol52DefinitionBuilder;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ColumnHeaderPopOverImplTest {

    @Mock
    private PopOverView view;

    @Mock
    private GuidedDecisionTableEditorService service;
    private Caller<GuidedDecisionTableEditorService> serviceCaller;

    @Mock
    private GuidedDecisionTableView.Presenter dtPresenter;

    private GuidedDecisionTable52 model;

    @Mock
    private AsyncPackageDataModelOracle dmo;

    @Mock
    private GuidedDecisionTableModellerView modellerView;

    @Mock
    private GridLayer gridLayer;

    @Mock
    private AbsolutePanel domElementContainer;

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    @Mock
    private GuidedDecisionTableView gridWidget;

    @Mock
    private GridRenderer renderer;

    @Mock
    private GridColumnRenderer<String> columnRenderer;

    @Mock
    private BaseGridRendererHelper rendererHelper;

    @Captor
    private ArgumentCaptor<PopOverView.ContentProvider> contentProviderArgumentCaptor;

    private BaseGridData uiModel;
    private BaseGridColumn uiColumn1;
    private BaseGridColumn<String> uiColumn2;
    private Bounds bounds = new BaseBounds( -50, -50, 250, 250 );

    private ColumnHeaderPopOver popOver;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.model = new GuidedDecisionTable52();
        this.model.getExpandedColumns().get( 0 ).setHeader( "#" );
        this.model.getExpandedColumns().get( 1 ).setHeader( "description" );

        this.uiColumn1 = new RowNumberColumn();
        this.uiColumn2 = new BaseGridColumn<>( new BaseHeaderMetaData( "description" ),
                                               columnRenderer,
                                               100.0 );
        this.uiModel = new BaseGridData() {{
            setHeaderRowCount( 2 );
        }};
        uiModel.appendColumn( uiColumn1 );
        uiModel.appendColumn( uiColumn2 );

        serviceCaller = new CallerMock<>( service );
        when( service.toSource( any( Path.class ),
                                any( GuidedDecisionTable52.class ) ) ).thenReturn( "source" );

        final Instance<ColumnDefinitionBuilder> buildersInstance = makeBuildersInstance();
        final ColumnDefinitionFactory columnDefinitionFactory = new ColumnDefinitionFactory( buildersInstance );

        when( renderer.getHeaderHeight() ).thenReturn( 64.0 );
        when( renderer.getHeaderRowHeight() ).thenReturn( 32.0 );

        when( dtPresenter.getView() ).thenReturn( gridWidget );
        when( dtPresenter.getModel() ).thenReturn( model );
        when( dtPresenter.getDataModelOracle() ).thenReturn( dmo );

        when( modellerView.getGridLayerView() ).thenReturn( gridLayer );
        when( gridLayer.getDomElementContainer() ).thenReturn( domElementContainer );
        when( gridLayer.getVisibleBounds() ).thenReturn( bounds );
        when( gridLayer.getViewport() ).thenReturn( viewport );
        when( gridWidget.getModel() ).thenReturn( uiModel );
        when( gridWidget.getViewport() ).thenReturn( viewport );
        when( gridWidget.getRenderer() ).thenReturn( renderer );
        when( gridWidget.getRendererHelper() ).thenReturn( rendererHelper );
        when( gridWidget.getWidth() ).thenReturn( 150.0 );
        when( gridWidget.getHeight() ).thenReturn( 64.0 );
        when( gridWidget.getX() ).thenReturn( 50.0 );
        when( gridWidget.getY() ).thenReturn( 50.0 );

        when( domElementContainer.getAbsoluteLeft() ).thenReturn( 200 );
        when( viewport.getTransform() ).thenReturn( transform );
        when( rendererHelper.getColumnOffset( uiColumn1 ) ).thenReturn( 0.0 );
        when( rendererHelper.getColumnOffset( uiColumn2 ) ).thenReturn( uiColumn1.getWidth() );

        final BaseGridRendererHelper.RenderingInformation ri = new BaseGridRendererHelper.RenderingInformation( bounds,
                                                                                                                uiModel.getColumns(),
                                                                                                                new BaseGridRendererHelper.RenderingBlockInformation(
                                                                                                                        new ArrayList<GridColumn<?>>() {{
                                                                                                                            add( uiColumn2 );
                                                                                                                        }},
                                                                                                                        0.0,
                                                                                                                        0.0,
                                                                                                                        0.0,
                                                                                                                        100.0 ),
                                                                                                                new BaseGridRendererHelper.RenderingBlockInformation(
                                                                                                                        new ArrayList<GridColumn<?>>() {{
                                                                                                                            add( uiColumn1 );
                                                                                                                        }},
                                                                                                                        25.0,
                                                                                                                        0.0,
                                                                                                                        0.0,
                                                                                                                        50.0 ),
                                                                                                                0,
                                                                                                                0,
                                                                                                                Collections.emptyList(),
                                                                                                                Collections.emptyList(),
                                                                                                                false,
                                                                                                                false,
                                                                                                                2,
                                                                                                                0,
                                                                                                                0,
                                                                                                                0 );
        when( rendererHelper.getRenderingInformation() ).thenReturn( ri );

        final ColumnHeaderPopOver wrapped = new ColumnHeaderPopOverImpl( view,
                                                                         columnDefinitionFactory );
        this.popOver = spy( wrapped );
    }

    @Test
    public void hideView() {
        popOver.hide();

        verify( view,
                times( 1 ) ).hide();
    }

    @Test
    public void showColumnHeaderPositioningFloatingBlockColumns_Scale100pct() {
        when( transform.getScaleX() ).thenReturn( 1.0 );
        when( transform.getScaleY() ).thenReturn( 1.0 );

        popOver.show( modellerView,
                      dtPresenter,
                      0 );

        verify( view,
                times( 1 ) ).show( contentProviderArgumentCaptor.capture() );
        final PopOverView.ContentProvider contentProvider = contentProviderArgumentCaptor.getValue();
        contentProvider.getContent( ( PopOverView.Content content ) -> {
                                        assertEquals( 350,
                                                      content.getX() );
                                        assertEquals( 148,
                                                      content.getY() );
                                        assertEquals( "#",
                                                      content.getContent() );
                                    }
                                  );
    }

    @Test
    public void showColumnHeaderPositioningBodyBlockColumns_Scale100pct() {
        when( transform.getScaleX() ).thenReturn( 1.0 );
        when( transform.getScaleY() ).thenReturn( 1.0 );

        popOver.show( modellerView,
                      dtPresenter,
                      1 );

        verify( view,
                times( 1 ) ).show( contentProviderArgumentCaptor.capture() );
        final PopOverView.ContentProvider contentProvider = contentProviderArgumentCaptor.getValue();
        contentProvider.getContent( ( PopOverView.Content content ) -> {
                                        assertEquals( 400,
                                                      content.getX() );
                                        assertEquals( 148,
                                                      content.getY() );
                                        assertEquals( "description",
                                                      content.getContent() );
                                    }
                                  );
    }

    @Test
    public void showColumnHeaderPositioningFloatingBlockColumns_Scale75pct() {
        when( transform.getScaleX() ).thenReturn( 0.75 );
        when( transform.getScaleY() ).thenReturn( 0.75 );

        popOver.show( modellerView,
                      dtPresenter,
                      0 );

        verify( view,
                times( 1 ) ).show( contentProviderArgumentCaptor.capture() );
        final PopOverView.ContentProvider contentProvider = contentProviderArgumentCaptor.getValue();
        contentProvider.getContent( ( PopOverView.Content content ) -> {
                                        assertEquals( 312,
                                                      content.getX() );
                                        assertEquals( 111,
                                                      content.getY() );
                                        assertEquals( "#",
                                                      content.getContent() );
                                    }
                                  );
    }

    @Test
    public void showColumnHeaderPositioningBodyBlockColumns_Scale75pct() {
        when( transform.getScaleX() ).thenReturn( 0.75 );
        when( transform.getScaleY() ).thenReturn( 0.75 );

        popOver.show( modellerView,
                      dtPresenter,
                      1 );

        verify( view,
                times( 1 ) ).show( contentProviderArgumentCaptor.capture() );
        final PopOverView.ContentProvider contentProvider = contentProviderArgumentCaptor.getValue();
        contentProvider.getContent( ( PopOverView.Content content ) -> {
                                        assertEquals( 350,
                                                      content.getX() );
                                        assertEquals( 111,
                                                      content.getY() );
                                        assertEquals( "description",
                                                      content.getContent() );
                                    }
                                  );
    }

    @Test
    public void showColumnServiceInvocation() {
        final Pattern52 p = new Pattern52();
        p.getChildColumns().add( new ConditionCol52() );
        this.model.getConditions().add( p );

        final BaseGridColumn<String> uiColumn3 = new BaseGridColumn<>( new BaseHeaderMetaData( "condition" ),
                                                                       columnRenderer,
                                                                       100.0 );
        uiModel.appendColumn( uiColumn3 );

        when( transform.getScaleX() ).thenReturn( 1.0 );
        when( transform.getScaleY() ).thenReturn( 1.0 );

        popOver.show( modellerView,
                      dtPresenter,
                      2 );

        verify( view,
                times( 1 ) ).show( contentProviderArgumentCaptor.capture() );

        popOver.show( modellerView,
                      dtPresenter,
                      2 );

        verify( view,
                times( 2 ) ).show( contentProviderArgumentCaptor.capture() );

        //Emulate Timer execution
        final PopOverView.ContentProvider contentProvider = contentProviderArgumentCaptor.getValue();
        contentProvider.getContent( ( PopOverView.Content content ) -> {
                                        assertEquals( "source",
                                                      content.getContent() );
                                    }
                                  );
        verify( service,
                times( 1 ) ).toSource( any( Path.class ),
                                       any( GuidedDecisionTable52.class ) );
    }

    private Instance<ColumnDefinitionBuilder> makeBuildersInstance() {
        final List<ColumnDefinitionBuilder> builders = new ArrayList<>();
        builders.add( new ConditionCol52DefinitionBuilder( serviceCaller ) );
        builders.add( new ColumnDefinitionBuilder() {
            @Override
            public Class getSupportedColumnType() {
                return RowNumberCol52.class;
            }

            @Override
            public void generateDefinition( final GuidedDecisionTableView.Presenter dtPresenter,
                                            final BaseColumn column,
                                            final Callback<String> afterGenerationCallback ) {
                afterGenerationCallback.callback( column.getHeader() );
            }
        } );
        builders.add( new ColumnDefinitionBuilder() {
            @Override
            public Class getSupportedColumnType() {
                return DescriptionCol52.class;
            }

            @Override
            public void generateDefinition( final GuidedDecisionTableView.Presenter dtPresenter,
                                            final BaseColumn column,
                                            final Callback<String> afterGenerationCallback ) {
                afterGenerationCallback.callback( column.getHeader() );
            }
        } );

        return new MockInstanceImpl<>( builders );
    }

}
