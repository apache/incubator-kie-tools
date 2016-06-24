/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.wires.core.grids.client.demo.resources.i18n.WiresGridsDemoConstants;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.util.GridDataFactory;
import org.uberfire.ext.wires.core.grids.client.widget.dom.multiple.impl.CheckBoxDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.ListBoxSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.TextBoxSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.BooleanDOMElementColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.ListBoxDOMElementSingletonColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.StringDOMElementSingletonColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.StringPopupColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.StringColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.MultiColouredTheme;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.RedTheme;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

/**
 * A Workbench Screen to demonstrate Wires Grids.
 */
@Dependent
@WorkbenchScreen(identifier = "WiresGridsDemoPresenter")
public class WiresGridsDemoPresenter implements WiresGridsDemoView.Presenter {

    private static final int GRID1_ROWS = 100;
    private static final int GRID2_ROWS = 100;
    private static final int GRID3_ROWS = 2;
    private static final int GRID4_ROWS = 100;

    private Menus menus;
    private WiresGridsDemoView view;

    private GridWidget gridWidget1;
    private GridWidget gridWidget2;
    private GridWidget gridWidget3;
    private GridWidget gridWidget4;

    private TranslationService translationService;

    @Inject
    public WiresGridsDemoPresenter( final WiresGridsDemoView view,
                                    final TranslationService translationService ) {
        this.view = view;
        this.translationService = translationService;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.getTranslation( WiresGridsDemoConstants.Screen_Title );
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @PostConstruct
    public void setup() {
        setupMenus();
        setupKeyDownHandler();
        setupZoomChangeHandler();
        setupStyleChangeHandler();
        setupMergedStateValueChangeHandler();
        setupAppendRowClickHandler();

        this.gridWidget1 = makeGridWidget1();
        this.gridWidget2 = makeGridWidget2();
        this.gridWidget3 = makeGridWidget3();
        this.gridWidget4 = makeGridWidget4();

        //Associations need to be set before the Grids are added to the View.
        linkGrids( gridWidget1,
                   9,
                   gridWidget2,
                   1 );
        linkGrids( gridWidget2,
                   3,
                   gridWidget3,
                   1 );
        linkGrids( gridWidget3,
                   1,
                   gridWidget1,
                   1 );

        view.add( gridWidget1 );
        view.add( gridWidget2 );
        view.add( gridWidget3 );
        view.add( gridWidget4 );
    }

    private void linkGrids( final GridWidget sourceGridWidget,
                            final int sourceGridColumnIndex,
                            final GridWidget targetGridWidget,
                            final int targetGridColumnIndex ) {
        final GridColumn<?> sourceGridColumn = sourceGridWidget.getModel().getColumns().get( sourceGridColumnIndex );
        final GridColumn<?> targetGridColumn = targetGridWidget.getModel().getColumns().get( targetGridColumnIndex );
        sourceGridColumn.setLink( targetGridColumn );
    }

    private void setupMenus() {
        this.menus = MenuFactory
                .newTopLevelMenu( translationService.getTranslation( WiresGridsDemoConstants.Menu_ClearSelections ) )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        for ( GridWidget gridWidget : view.getGridWidgets() ) {
                            if ( gridWidget.isSelected() ) {
                                gridWidget.getModel().clearSelections();
                            }
                        }
                        view.refresh();
                        menus.getItems().get( 0 ).setEnabled( false );
                        menus.getItems().get( 1 ).setEnabled( false );
                    }
                } )
                .endMenu()
                .newTopLevelMenu( translationService.getTranslation( WiresGridsDemoConstants.Menu_ClearCells ) )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        clearCells();
                    }
                } )
                .endMenu()
                .build();
        menus.getItems().get( 0 ).setEnabled( false );
        menus.getItems().get( 1 ).setEnabled( false );
    }

    private void setupKeyDownHandler() {
        view.addKeyDownHandler( new KeyDownHandler() {
            @Override
            public void onKeyDown( final KeyDownEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_DELETE ) {
                    clearCells();
                }
            }
        } );
    }

    private GridWidget makeGridWidget1() {
        final GridData grid1 = new BaseGridData( false );
        grid1.setHeaderRowCount( 2 );
        final GridWidget gridWidget1 = new BaseGridWidget( grid1,
                                                           this,
                                                           view.getGridLayer(),
                                                           new BaseGridRenderer( new MultiColouredTheme() ) );

        //Add a floating column for row number
        final RowNumberColumn grid1ColumnRowNumber = new RowNumberColumn();
        grid1.appendColumn( grid1ColumnRowNumber );

        //Add a floating column
        final GridColumn.HeaderMetaData grid1ColumnFloatingHeaderMetaData = new BaseHeaderMetaData( "Floating" );
        final TextBoxSingletonDOMElementFactory grid1ColumnFloatingFactory = new TextBoxSingletonDOMElementFactory( view.getGridLayer(),
                                                                                                                    gridWidget1 );
        final BaseGridColumn<String> grid1ColumnFloating = new StringDOMElementSingletonColumn( grid1ColumnFloatingHeaderMetaData,
                                                                                                grid1ColumnFloatingFactory,
                                                                                                100 );
        grid1ColumnFloating.setMovable( false );
        grid1ColumnFloating.setResizable( true );
        grid1ColumnFloating.setFloatable( true );
        grid1.appendColumn( grid1ColumnFloating );

        for ( int idx = 0; idx < 8; idx++ ) {
            final int grid1ColumnGroupSuffix = ( idx < 3 ? 0 : ( idx < 6 ? 1 : 2 ) );
            final boolean isFloatable = ( idx == 0 );
            final GridColumn.HeaderMetaData grid1ColumnHeaderMetaData1 = new BaseHeaderMetaData( "G1-G" + grid1ColumnGroupSuffix + "-a-Long-Caption-1",
                                                                                                 "grid1ColumnGroup" );
            final GridColumn.HeaderMetaData grid1ColumnHeaderMetaData2 = new BaseHeaderMetaData( "G1-G" + grid1ColumnGroupSuffix + "-C" + idx + "-a-Long-Caption-2",
                                                                                                 "grid1ColumnGroup" + grid1ColumnGroupSuffix );
            final List<GridColumn.HeaderMetaData> grid1ColumnHeaderMetaData = new ArrayList<GridColumn.HeaderMetaData>();
            grid1ColumnHeaderMetaData.add( grid1ColumnHeaderMetaData1 );
            grid1ColumnHeaderMetaData.add( grid1ColumnHeaderMetaData2 );
            final BaseGridColumn<String> grid1Column = new StringPopupColumn( grid1ColumnHeaderMetaData,
                                                                              new StringColumnRenderer(),
                                                                              100 );
            grid1Column.setMinimumWidth( 50.0 );
            grid1Column.setFloatable( isFloatable );
            grid1.appendColumn( grid1Column );
        }
        GridDataFactory.populate( grid1,
                                  GRID1_ROWS );

        gridWidget1.setLocation( new Point2D( -1300,
                                              0 ) );

        return gridWidget1;
    }

    private GridWidget makeGridWidget2() {
        final GridData grid2 = new BaseGridData( false );
        final GridWidget gridWidget2 = new BaseGridWidget( grid2,
                                                           this,
                                                           view.getGridLayer(),
                                                           new BaseGridRenderer( new MultiColouredTheme() ) {
                                                               @Override
                                                               public double getHeaderHeight() {
                                                                   return 64.0;
                                                               }

                                                               @Override
                                                               public double getHeaderRowHeight() {
                                                                   return 64.0;
                                                               }
                                                           } );
        for ( int idx = 0; idx < 5; idx++ ) {
            final GridColumn.HeaderMetaData grid2ColumnHeaderMetaData = new BaseHeaderMetaData( "G2-G0-C" + idx );
            final BaseGridColumn<String> grid2Column = new StringPopupColumn( grid2ColumnHeaderMetaData,
                                                                              new StringColumnRenderer(),
                                                                              150 );
            grid2.appendColumn( grid2Column );
        }
        GridDataFactory.populate( grid2,
                                  GRID2_ROWS );

        gridWidget2.setLocation( new Point2D( 0,
                                              750 ) );

        return gridWidget2;
    }

    private GridWidget makeGridWidget3() {
        final GridData grid3 = new BaseGridData( false );
        final GridWidget gridWidget3 = new BaseGridWidget( grid3,
                                                           this,
                                                           view.getGridLayer(),
                                                           new BaseGridRenderer( new MultiColouredTheme() ) {
                                                               @Override
                                                               public double getHeaderHeight() {
                                                                   return 64.0;
                                                               }

                                                               @Override
                                                               public double getHeaderRowHeight() {
                                                                   return 64.0;
                                                               }
                                                           } );

        //RowNumber column supporting row drag-and-drop
        final RowNumberColumn grid3RowNumberColumn = new RowNumberColumn();
        grid3.appendColumn( grid3RowNumberColumn );

        for ( int idx = 0; idx < 2; idx++ ) {
            final boolean isResizeable = idx > 1;
            final boolean isMovable = idx > 1;
            final GridColumn.HeaderMetaData grid3ColumnHeaderMetaData = new BaseHeaderMetaData( "G3-G0-C" + idx );
            final BaseGridColumn<String> grid3Column = new StringPopupColumn( grid3ColumnHeaderMetaData,
                                                                              new StringColumnRenderer(),
                                                                              100 );
            grid3Column.setResizable( isResizeable );
            grid3Column.setMovable( isMovable );
            grid3.appendColumn( grid3Column );
        }
        GridDataFactory.populate( grid3,
                                  GRID3_ROWS );

        //Add DOM Column - TextBox (Lazy show)
        final String grid3ColumnGroup1 = "grid3ColumnGroup1";
        final GridColumn.HeaderMetaData grid3Column2HeaderMetaData = new BaseHeaderMetaData( "G3-G1-C2",
                                                                                             grid3ColumnGroup1 );
        final TextBoxSingletonDOMElementFactory grid3Column2Factory = new TextBoxSingletonDOMElementFactory( view.getGridLayer(),
                                                                                                             gridWidget3 );
        final BaseGridColumn<String> grid3Column2 = new StringDOMElementSingletonColumn( grid3Column2HeaderMetaData,
                                                                                         grid3Column2Factory,
                                                                                         100 );
        grid3.appendColumn( grid3Column2 );
        for ( int rowIndex = 0; rowIndex < GRID4_ROWS; rowIndex++ ) {
            grid3.setCell( rowIndex,
                           3,
                           new BaseGridCellValue<String>( "(" + 2 + ", " + rowIndex + ")" ) );
        }

        //Add DOM Column - CheckBox
        final GridColumn.HeaderMetaData grid3Column3HeaderMetaData = new BaseHeaderMetaData( "G3-G1-C3",
                                                                                             grid3ColumnGroup1 );
        final CheckBoxDOMElementFactory grid3Column3Factory = new CheckBoxDOMElementFactory( view.getGridLayer(),
                                                                                             gridWidget3 );
        final BaseGridColumn<Boolean> grid3Column3 = new BooleanDOMElementColumn( grid3Column3HeaderMetaData,
                                                                                  grid3Column3Factory,
                                                                                  100 );
        grid3Column3.setFloatable( true );
        grid3.appendColumn( grid3Column3 );
        for ( int rowIndex = 0; rowIndex < GRID4_ROWS; rowIndex++ ) {
            grid3.setCell( rowIndex,
                           4,
                           new BaseGridCellValue<Boolean>( Math.random() < GridDataFactory.FILL_FACTOR ) );
        }

        //Add DOM Column - ListBox
        final GridColumn.HeaderMetaData grid3Column4HeaderMetaData = new BaseHeaderMetaData( "G3-G1-C4",
                                                                                             grid3ColumnGroup1 );
        final ListBoxSingletonDOMElementFactory grid3Column4Factory = new ListBoxSingletonDOMElementFactory( view.getGridLayer(),
                                                                                                             gridWidget3 );
        final BaseGridColumn<String> grid3Column4 = new ListBoxDOMElementSingletonColumn( grid3Column4HeaderMetaData,
                                                                                          grid3Column4Factory,
                                                                                          100 );
        grid3.appendColumn( grid3Column4 );
        for ( int rowIndex = 0; rowIndex < GRID4_ROWS; rowIndex++ ) {
            grid3.setCell( rowIndex,
                           5,
                           new BaseGridCellValue<String>( rowIndex % 2 == 0 ? "one" : "two" ) );
        }

        gridWidget3.setLocation( new Point2D( 1050,
                                              0 ) );

        return gridWidget3;
    }

    private GridWidget makeGridWidget4() {
        final GridData grid4 = new BaseGridData( false );
        final GridWidget gridWidget4 = new BaseGridWidget( grid4,
                                                           this,
                                                           view.getGridLayer(),
                                                           new BaseGridRenderer( new RedTheme() ) {
                                                               @Override
                                                               public double getHeaderHeight() {
                                                                   return 64.0;
                                                               }

                                                               @Override
                                                               public double getHeaderRowHeight() {
                                                                   return 64.0;
                                                               }
                                                           } );

        //Add DOM Column - TextBox
        final GridColumn.HeaderMetaData grid4Column1HeaderMetaData = new BaseHeaderMetaData( "G4-G0-C0" );
        final BaseGridColumn<String> grid4Column1 = new StringPopupColumn( grid4Column1HeaderMetaData,
                                                                           new StringColumnRenderer(),
                                                                           100 );
        grid4.appendColumn( grid4Column1 );

        //Add DOM Column - CheckBox
        final GridColumn.HeaderMetaData grid4Column2HeaderMetaData = new BaseHeaderMetaData( "G4-G0-C1" );
        final CheckBoxDOMElementFactory grid4Column2Factory = new CheckBoxDOMElementFactory( view.getGridLayer(),
                                                                                             gridWidget4 );
        final BaseGridColumn<Boolean> grid4Column2 = new BooleanDOMElementColumn( grid4Column2HeaderMetaData,
                                                                                  grid4Column2Factory,
                                                                                  100 );
        grid4.appendColumn( grid4Column2 );

        for ( int rowIndex = 0; rowIndex < GRID4_ROWS; rowIndex++ ) {
            final GridRow row = new BaseGridRow();
            grid4.appendRow( row );
            for ( int columnIndex = 0; columnIndex < grid4.getColumnCount(); columnIndex++ ) {
                switch ( columnIndex ) {
                    case 0:
                        if ( Math.random() > 0.5 ) {
                            grid4.setCell( rowIndex,
                                           columnIndex,
                                           new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
                        }
                        break;
                    case 1:
                        grid4.setCell( rowIndex,
                                       columnIndex,
                                       new BaseGridCellValue<Boolean>( Math.random() < 0.5 ) );
                        break;
                }
            }
        }

        gridWidget4.setLocation( new Point2D( 1800,
                                              200 ) );

        return gridWidget4;
    }

    private void setupZoomChangeHandler() {
        view.addZoomChangeHandler( new ChangeHandler() {

            private int m_currentZoom = 100;

            @Override
            public void onChange( final ChangeEvent event ) {
                final int pct = view.getSelectedZoomLevel();
                if ( m_currentZoom == pct ) {
                    return;
                }
                m_currentZoom = pct;
                view.setZoom( pct );
            }

        } );
    }

    private void setupStyleChangeHandler() {
        view.addThemeChangeHandler( new ChangeHandler() {
            @Override
            @SuppressWarnings("unused")
            public void onChange( final ChangeEvent event ) {
                final GridRendererTheme theme = view.getSelectedTheme();
                gridWidget4.getRenderer().setTheme( theme );
                view.refresh();
            }
        } );
    }

    private void setupMergedStateValueChangeHandler() {
        view.setMergedState( false );
        view.addMergedStateValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            @SuppressWarnings("unused")
            public void onValueChange( final ValueChangeEvent<Boolean> event ) {
                final boolean isMerged = event.getValue();
                gridWidget1.getModel().setMerged( isMerged );
                gridWidget2.getModel().setMerged( isMerged );
                gridWidget3.getModel().setMerged( isMerged );
                view.refresh();
            }
        } );
    }

    private void setupAppendRowClickHandler() {
        view.addAppendRowClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                for ( GridWidget gridWidget : view.getGridWidgets() ) {
                    if ( gridWidget.isSelected() ) {
                        gridWidget.getModel().appendRow( new BaseGridRow() );
                    }
                }
                view.refresh();
            }
        } );
    }

    private void clearCells() {
        for ( GridWidget gridWidget : view.getGridWidgets() ) {
            if ( gridWidget.isSelected() ) {
                for ( GridData.SelectedCell cell : gridWidget.getModel().getSelectedCells() ) {
                    gridWidget.getModel().deleteCell( cell.getRowIndex(),
                                                      cell.getColumnIndex() );
                }
            }
        }
        view.refresh();
        menus.getItems().get( 0 ).setEnabled( false );
        menus.getItems().get( 1 ).setEnabled( false );
    }

    @Override
    public void select( final GridWidget selectedGridWidget ) {
        view.select( selectedGridWidget );
        final boolean hasSelections = selectedGridWidget.getModel().getSelectedCells().size() > 0;
        menus.getItems().get( 0 ).setEnabled( hasSelections );
        menus.getItems().get( 1 ).setEnabled( hasSelections );
    }

    @Override
    public void selectLinkedColumn( final GridColumn<?> selectedGridColumn ) {
        view.selectLinkedColumn( selectedGridColumn );
    }

    @Override
    public Set<GridWidget> getGridWidgets() {
        return view.getGridWidgets();
    }

}
