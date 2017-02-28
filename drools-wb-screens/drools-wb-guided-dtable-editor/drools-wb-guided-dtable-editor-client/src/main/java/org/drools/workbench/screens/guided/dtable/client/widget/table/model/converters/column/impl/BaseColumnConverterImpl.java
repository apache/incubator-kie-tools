/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.ListBox;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BigDecimalUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BigIntegerUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BooleanUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.ByteUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.DateUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.DoubleUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.EnumMultiSelectUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.EnumSingleSelectBigDecimalUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.EnumSingleSelectBigIntegerUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.EnumSingleSelectByteUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.EnumSingleSelectDateUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.EnumSingleSelectDoubleUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.EnumSingleSelectFloatUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.EnumSingleSelectIntegerUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.EnumSingleSelectLongUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.EnumSingleSelectNumericUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.EnumSingleSelectShortUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.EnumSingleSelectStringUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.FloatUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.IntegerUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.LongUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.PriorityListUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.SalienceUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.ShortUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.StringUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.ValueListUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.datepicker.DatePickerSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxBigDecimalSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxBigIntegerSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxByteSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxDOMElement;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxDateSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxDoubleSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxFloatSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxIntegerSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxLongSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxNumericSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxShortSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxStringSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxBigDecimalSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxBigIntegerSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxByteSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxDoubleSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxFloatSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxIntegerSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxLongSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxNumericSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxShortSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxStringSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.BaseColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.multiple.impl.CheckBoxDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

/**
 * Generic Handler for different BaseUiColumn types
 */
public abstract class BaseColumnConverterImpl implements BaseColumnConverter {

    protected static final int DEFAULT_COLUMN_WIDTH = 100;

    protected GuidedDecisionTable52 model;
    protected AsyncPackageDataModelOracle oracle;
    protected ColumnUtilities columnUtilities;
    protected GuidedDecisionTableView.Presenter presenter;

    protected GridLienzoPanel gridPanel;
    protected GridLayer gridLayer;

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public void initialise( final GuidedDecisionTable52 model,
                            final AsyncPackageDataModelOracle oracle,
                            final ColumnUtilities columnUtilities,
                            final GuidedDecisionTableView.Presenter presenter ) {
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
        this.oracle = PortablePreconditions.checkNotNull( "oracle",
                                                          oracle );
        this.columnUtilities = PortablePreconditions.checkNotNull( "columnUtilities",
                                                                   columnUtilities );
        this.presenter = PortablePreconditions.checkNotNull( "presenter",
                                                             presenter );
        this.gridLayer = presenter.getModellerPresenter().getView().getGridLayerView();
        this.gridPanel = presenter.getModellerPresenter().getView().getGridPanel();
    }

    protected GridColumn<?> newColumn( final BaseColumn column,
                                       final GuidedDecisionTablePresenter.Access access,
                                       final GuidedDecisionTableView gridWidget ) {
        //Get a column based upon the data-type
        final String type = columnUtilities.getType( column );

        if ( DataType.TYPE_NUMERIC.equals( type ) ) {
            return newNumericColumn( makeHeaderMetaData( column ),
                                     Math.max( column.getWidth(),
                                               DEFAULT_COLUMN_WIDTH ),
                                     true,
                                     !column.isHideColumn(),
                                     access,
                                     gridWidget );

        } else if ( DataType.TYPE_NUMERIC_BIGDECIMAL.equals( type ) ) {
            return newBigDecimalColumn( makeHeaderMetaData( column ),
                                        Math.max( column.getWidth(),
                                                  DEFAULT_COLUMN_WIDTH ),
                                        true,
                                        !column.isHideColumn(),
                                        access,
                                        gridWidget );

        } else if ( DataType.TYPE_NUMERIC_BIGINTEGER.equals( type ) ) {
            return newBigIntegerColumn( makeHeaderMetaData( column ),
                                        Math.max( column.getWidth(),
                                                  DEFAULT_COLUMN_WIDTH ),
                                        true,
                                        !column.isHideColumn(),
                                        access,
                                        gridWidget );

        } else if ( DataType.TYPE_NUMERIC_BYTE.equals( type ) ) {
            return newByteColumn( makeHeaderMetaData( column ),
                                  Math.max( column.getWidth(),
                                            DEFAULT_COLUMN_WIDTH ),
                                  true,
                                  !column.isHideColumn(),
                                  access,
                                  gridWidget );

        } else if ( DataType.TYPE_NUMERIC_DOUBLE.equals( type ) ) {
            return newDoubleColumn( makeHeaderMetaData( column ),
                                    Math.max( column.getWidth(),
                                              DEFAULT_COLUMN_WIDTH ),
                                    true,
                                    !column.isHideColumn(),
                                    access,
                                    gridWidget );

        } else if ( DataType.TYPE_NUMERIC_FLOAT.equals( type ) ) {
            return newFloatColumn( makeHeaderMetaData( column ),
                                   Math.max( column.getWidth(),
                                             DEFAULT_COLUMN_WIDTH ),
                                   true,
                                   !column.isHideColumn(),
                                   access,
                                   gridWidget );

        } else if ( DataType.TYPE_NUMERIC_INTEGER.equals( type ) ) {
            return newIntegerColumn( makeHeaderMetaData( column ),
                                     Math.max( column.getWidth(),
                                               DEFAULT_COLUMN_WIDTH ),
                                     true,
                                     !column.isHideColumn(),
                                     access,
                                     gridWidget );

        } else if ( DataType.TYPE_NUMERIC_LONG.equals( type ) ) {
            return newLongColumn( makeHeaderMetaData( column ),
                                  Math.max( column.getWidth(),
                                            DEFAULT_COLUMN_WIDTH ),
                                  true,
                                  !column.isHideColumn(),
                                  access,
                                  gridWidget );

        } else if ( DataType.TYPE_NUMERIC_SHORT.equals( type ) ) {
            return newShortColumn( makeHeaderMetaData( column ),
                                   Math.max( column.getWidth(),
                                             DEFAULT_COLUMN_WIDTH ),
                                   true,
                                   !column.isHideColumn(),
                                   access,
                                   gridWidget );

        } else if ( DataType.TYPE_BOOLEAN.equals( type ) ) {
            return newBooleanColumn( makeHeaderMetaData( column ),
                                     Math.max( column.getWidth(),
                                               DEFAULT_COLUMN_WIDTH ),
                                     true,
                                     !column.isHideColumn(),
                                     access,
                                     gridWidget );

        } else if ( DataType.TYPE_DATE.equals( type ) ) {
            return newDateColumn( makeHeaderMetaData( column ),
                                  Math.max( column.getWidth(),
                                            DEFAULT_COLUMN_WIDTH ),
                                  true,
                                  !column.isHideColumn(),
                                  access,
                                  gridWidget );

        } else {
            return newStringColumn( makeHeaderMetaData( column ),
                                    Math.max( column.getWidth(),
                                              DEFAULT_COLUMN_WIDTH ),
                                    true,
                                    !column.isHideColumn(),
                                    access,
                                    gridWidget );
        }
    }

    protected GridColumn<?> newValueListColumn( final ConditionCol52 column,
                                                final GuidedDecisionTablePresenter.Access access,
                                                final GuidedDecisionTableView gridWidget ) {
        final boolean isMultipleSelect = OperatorsOracle.operatorRequiresList( column.getOperator() );
        return new ValueListUiColumn( makeHeaderMetaData( column ),
                                      Math.max( column.getWidth(),
                                                DEFAULT_COLUMN_WIDTH ),
                                      true,
                                      !column.isHideColumn(),
                                      access,
                                      new ListBoxSingletonDOMElementFactory<String, ListBox>( gridPanel,
                                                                                              gridLayer,
                                                                                              gridWidget ) {

                                          @Override
                                          public ListBox createWidget() {
                                              final ListBox listBox = new ListBox();
                                              listBox.setMultipleSelect( isMultipleSelect );
                                              return listBox;
                                          }

                                          @Override
                                          public ListBoxDOMElement<String, ListBox> createDomElement( final GridLayer gridLayer,
                                                                                                      final GridWidget gridWidget,
                                                                                                      final GridBodyCellRenderContext context ) {
                                              this.widget = createWidget();
                                              this.widget.addKeyDownHandler( ( e ) -> e.stopPropagation() );
                                              this.widget.addMouseDownHandler( ( e ) -> e.stopPropagation() );

                                              widget.addBlurHandler( new BlurHandler() {
                                                  @Override
                                                  public void onBlur( final BlurEvent event ) {
                                                      destroyResources();
                                                      gridLayer.batch();
                                                      gridPanel.setFocus( true );
                                                  }
                                              } );
                                              this.e = new ListBoxDOMElement<String, ListBox>( widget,
                                                                                               gridLayer,
                                                                                               gridWidget );

                                              return e;
                                          }

                                          @Override
                                          public void toWidget( final GridCell<String> cell,
                                                                final ListBox widget ) {
                                              if ( cell == null || cell.getValue() == null || cell.getValue().getValue() == null ) {
                                                  if ( widget.getItemCount() > 0 ) {
                                                      widget.setSelectedIndex( 0 );
                                                  }

                                              } else {
                                                  final String value = cell.getValue().getValue();
                                                  if ( isMultipleSelect ) {
                                                      final List<String> values = Arrays.asList( value.split( "," ) );
                                                      for ( int i = 0; i < widget.getItemCount(); i++ ) {
                                                          widget.setItemSelected( i,
                                                                                  values.contains( widget.getValue( i ) ) );
                                                      }

                                                  } else {
                                                      for ( int i = 0; i < widget.getItemCount(); i++ ) {
                                                          if ( widget.getValue( i ).equals( value ) ) {
                                                              widget.setSelectedIndex( i );
                                                              break;
                                                          }
                                                      }
                                                  }
                                              }
                                          }

                                          @Override
                                          public String fromWidget( final ListBox widget ) {
                                              final StringBuilder sb = new StringBuilder();
                                              if ( isMultipleSelect ) {
                                                  for ( int i = 0; i < widget.getItemCount(); i++ ) {
                                                      if ( widget.isItemSelected( i ) ) {
                                                          if ( i == 0 ) {
                                                              sb.append( widget.getValue( i ) );
                                                          } else {
                                                              sb.append( "," ).append( widget.getValue( i ) );
                                                          }
                                                      }
                                                  }

                                              } else {
                                                  int selectedIndex = widget.getSelectedIndex();
                                                  if ( selectedIndex >= 0 ) {
                                                      sb.append( widget.getValue( selectedIndex ) );
                                                  }
                                              }

                                              return sb.toString();
                                          }

                                          @Override
                                          public String convert( final String value ) {
                                              return value;
                                          }

                                      },
                                      presenter.getValueListLookups( column ),
                                      isMultipleSelect );
    }

    protected GridColumn<?> newValueListColumn( final ActionCol52 column,
                                                final GuidedDecisionTablePresenter.Access access,
                                                final GuidedDecisionTableView gridWidget ) {
        return new ValueListUiColumn( makeHeaderMetaData( column ),
                                      Math.max( column.getWidth(),
                                                DEFAULT_COLUMN_WIDTH ),
                                      true,
                                      !column.isHideColumn(),
                                      access,
                                      new ListBoxStringSingletonDOMElementFactory( gridPanel,
                                                                                   gridLayer,
                                                                                   gridWidget ),
                                      presenter.getValueListLookups( column ) );
    }

    protected GridColumn<?> newMultipleSelectEnumColumn( final String factType,
                                                         final String factField,
                                                         final BaseColumn column,
                                                         final GuidedDecisionTablePresenter.Access access,
                                                         final GuidedDecisionTableView gridWidget ) {
        return new EnumMultiSelectUiColumn( makeHeaderMetaData( column ),
                                            Math.max( column.getWidth(),
                                                      DEFAULT_COLUMN_WIDTH ),
                                            true,
                                            !column.isHideColumn(),
                                            access,
                                            new ListBoxSingletonDOMElementFactory<String, ListBox>( gridPanel,
                                                                                                    gridLayer,
                                                                                                    gridWidget ) {

                                                @Override
                                                public ListBox createWidget() {
                                                    final ListBox listBox = new ListBox();
                                                    listBox.setMultipleSelect( true );
                                                    return listBox;
                                                }

                                                @Override
                                                public ListBoxDOMElement<String, ListBox> createDomElement( final GridLayer gridLayer,
                                                                                                            final GridWidget gridWidget,
                                                                                                            final GridBodyCellRenderContext context ) {
                                                    this.widget = createWidget();
                                                    this.widget.addKeyDownHandler( ( e ) -> e.stopPropagation() );
                                                    this.widget.addMouseDownHandler( ( e ) -> e.stopPropagation() );

                                                    widget.addBlurHandler( new BlurHandler() {
                                                        @Override
                                                        public void onBlur( final BlurEvent event ) {
                                                            destroyResources();
                                                            gridLayer.batch();
                                                            gridPanel.setFocus( true );
                                                        }
                                                    } );

                                                    this.e = new ListBoxDOMElement<String, ListBox>( widget,
                                                                                                     gridLayer,
                                                                                                     gridWidget );

                                                    return e;
                                                }

                                                @Override
                                                public String convert( final String value ) {
                                                    return value;
                                                }

                                                @Override
                                                public void toWidget( final GridCell<String> cell,
                                                                      final ListBox widget ) {
                                                    final String value = cell.getValue().getValue();
                                                    if ( value == null ) {
                                                        return;
                                                    }
                                                    final List<String> values = Arrays.asList( value.split( "," ) );
                                                    for ( int i = 0; i < widget.getItemCount(); i++ ) {
                                                        widget.setItemSelected( i,
                                                                                values.contains( widget.getValue( i ) ) );
                                                    }
                                                }

                                                @Override
                                                public String fromWidget( final ListBox widget ) {
                                                    final StringBuilder sb = new StringBuilder();
                                                    for ( int i = 0; i < widget.getItemCount(); i++ ) {
                                                        if ( widget.isItemSelected( i ) ) {
                                                            if ( i == 0 ) {
                                                                sb.append( widget.getValue( i ) );
                                                            } else {
                                                                sb.append( "," ).append( widget.getValue( i ) );
                                                            }
                                                        }
                                                    }
                                                    return sb.toString();
                                                }
                                            },
                                            presenter,
                                            factType,
                                            factField );
    }

    protected GridColumn<?> newSingleSelectionEnumColumn( final String factType,
                                                          final String factField,
                                                          final DataType.DataTypes dataType,
                                                          final BaseColumn column,
                                                          final GuidedDecisionTablePresenter.Access access,
                                                          final GuidedDecisionTableView gridWidget ) {
        if ( dataType.equals( DataType.DataTypes.NUMERIC ) ) {
            return new EnumSingleSelectNumericUiColumn( makeHeaderMetaData( column ),
                                                        Math.max( column.getWidth(),
                                                                  DEFAULT_COLUMN_WIDTH ),
                                                        true,
                                                        !column.isHideColumn(),
                                                        access,
                                                        new ListBoxNumericSingletonDOMElementFactory( gridPanel,
                                                                                                      gridLayer,
                                                                                                      gridWidget ),
                                                        new TextBoxNumericSingletonDOMElementFactory( gridPanel,
                                                                                                      gridLayer,
                                                                                                      gridWidget ),
                                                        presenter,
                                                        factType,
                                                        factField );

        } else if ( dataType.equals( DataType.DataTypes.NUMERIC_BIGDECIMAL ) ) {
            return new EnumSingleSelectBigDecimalUiColumn( makeHeaderMetaData( column ),
                                                           Math.max( column.getWidth(),
                                                                     DEFAULT_COLUMN_WIDTH ),
                                                           true,
                                                           !column.isHideColumn(),
                                                           access,
                                                           new ListBoxBigDecimalSingletonDOMElementFactory( gridPanel,
                                                                                                            gridLayer,
                                                                                                            gridWidget ),
                                                           new TextBoxBigDecimalSingletonDOMElementFactory( gridPanel,
                                                                                                            gridLayer,
                                                                                                            gridWidget ),

                                                           presenter,
                                                           factType,
                                                           factField );

        } else if ( dataType.equals( DataType.DataTypes.NUMERIC_BIGINTEGER ) ) {
            return new EnumSingleSelectBigIntegerUiColumn( makeHeaderMetaData( column ),
                                                           Math.max( column.getWidth(),
                                                                     DEFAULT_COLUMN_WIDTH ),
                                                           true,
                                                           !column.isHideColumn(),
                                                           access,
                                                           new ListBoxBigIntegerSingletonDOMElementFactory( gridPanel,
                                                                                                            gridLayer,
                                                                                                            gridWidget ),
                                                           new TextBoxBigIntegerSingletonDOMElementFactory( gridPanel,
                                                                                                            gridLayer,
                                                                                                            gridWidget ),
                                                           presenter,
                                                           factType,
                                                           factField );

        } else if ( dataType.equals( DataType.DataTypes.NUMERIC_BYTE ) ) {
            return new EnumSingleSelectByteUiColumn( makeHeaderMetaData( column ),
                                                     Math.max( column.getWidth(),
                                                               DEFAULT_COLUMN_WIDTH ),
                                                     true,
                                                     !column.isHideColumn(),
                                                     access,
                                                     new ListBoxByteSingletonDOMElementFactory( gridPanel,
                                                                                                gridLayer,
                                                                                                gridWidget ),
                                                     new TextBoxByteSingletonDOMElementFactory( gridPanel,
                                                                                                gridLayer,
                                                                                                gridWidget ),
                                                     presenter,
                                                     factType,
                                                     factField );

        } else if ( dataType.equals( DataType.DataTypes.NUMERIC_DOUBLE ) ) {
            return new EnumSingleSelectDoubleUiColumn( makeHeaderMetaData( column ),
                                                       Math.max( column.getWidth(),
                                                                 DEFAULT_COLUMN_WIDTH ),
                                                       true,
                                                       !column.isHideColumn(),
                                                       access,
                                                       new ListBoxDoubleSingletonDOMElementFactory( gridPanel,
                                                                                                    gridLayer,
                                                                                                    gridWidget ),
                                                       new TextBoxDoubleSingletonDOMElementFactory( gridPanel,
                                                                                                    gridLayer,
                                                                                                    gridWidget ),
                                                       presenter,
                                                       factType,
                                                       factField );

        } else if ( dataType.equals( DataType.DataTypes.NUMERIC_FLOAT ) ) {
            return new EnumSingleSelectFloatUiColumn( makeHeaderMetaData( column ),
                                                      Math.max( column.getWidth(),
                                                                DEFAULT_COLUMN_WIDTH ),
                                                      true,
                                                      !column.isHideColumn(),
                                                      access,
                                                      new ListBoxFloatSingletonDOMElementFactory( gridPanel,
                                                                                                  gridLayer,
                                                                                                  gridWidget ),
                                                      new TextBoxFloatSingletonDOMElementFactory( gridPanel,
                                                                                                  gridLayer,
                                                                                                  gridWidget ),
                                                      presenter,
                                                      factType,
                                                      factField );

        } else if ( dataType.equals( DataType.DataTypes.NUMERIC_INTEGER ) ) {
            return new EnumSingleSelectIntegerUiColumn( makeHeaderMetaData( column ),
                                                        Math.max( column.getWidth(),
                                                                  DEFAULT_COLUMN_WIDTH ),
                                                        true,
                                                        !column.isHideColumn(),
                                                        access,
                                                        new ListBoxIntegerSingletonDOMElementFactory( gridPanel,
                                                                                                      gridLayer,
                                                                                                      gridWidget ),
                                                        new TextBoxIntegerSingletonDOMElementFactory( gridPanel,
                                                                                                      gridLayer,
                                                                                                      gridWidget ),
                                                        presenter,
                                                        factType,
                                                        factField );

        } else if ( dataType.equals( DataType.DataTypes.NUMERIC_LONG ) ) {
            return new EnumSingleSelectLongUiColumn( makeHeaderMetaData( column ),
                                                     Math.max( column.getWidth(),
                                                               DEFAULT_COLUMN_WIDTH ),
                                                     true,
                                                     !column.isHideColumn(),
                                                     access,
                                                     new ListBoxLongSingletonDOMElementFactory( gridPanel,
                                                                                                gridLayer,
                                                                                                gridWidget ),
                                                     new TextBoxLongSingletonDOMElementFactory( gridPanel,
                                                                                                gridLayer,
                                                                                                gridWidget ),
                                                     presenter,
                                                     factType,
                                                     factField );

        } else if ( dataType.equals( DataType.DataTypes.NUMERIC_SHORT ) ) {
            return new EnumSingleSelectShortUiColumn( makeHeaderMetaData( column ),
                                                      Math.max( column.getWidth(),
                                                                DEFAULT_COLUMN_WIDTH ),
                                                      true,
                                                      !column.isHideColumn(),
                                                      access,
                                                      new ListBoxShortSingletonDOMElementFactory( gridPanel,
                                                                                                  gridLayer,
                                                                                                  gridWidget ),
                                                      new TextBoxShortSingletonDOMElementFactory( gridPanel,
                                                                                                  gridLayer,
                                                                                                  gridWidget ),
                                                      presenter,
                                                      factType,
                                                      factField );

        } else if ( dataType.equals( DataType.DataTypes.BOOLEAN ) ) {
            return newBooleanColumn( makeHeaderMetaData( column ),
                                     Math.max( column.getWidth(),
                                               DEFAULT_COLUMN_WIDTH ),
                                     true,
                                     !column.isHideColumn(),
                                     access,
                                     gridWidget );

        } else if ( dataType.equals( DataType.DataTypes.DATE ) ) {
            return new EnumSingleSelectDateUiColumn( makeHeaderMetaData( column ),
                                                     Math.max( column.getWidth(),
                                                               DEFAULT_COLUMN_WIDTH ),
                                                     true,
                                                     !column.isHideColumn(),
                                                     access,
                                                     new ListBoxDateSingletonDOMElementFactory( gridPanel,
                                                                                                gridLayer,
                                                                                                gridWidget ),
                                                     new DatePickerSingletonDOMElementFactory( gridPanel,
                                                                                               gridLayer,
                                                                                               gridWidget ),
                                                     presenter,
                                                     factType,
                                                     factField );

        } else {
            return new EnumSingleSelectStringUiColumn( makeHeaderMetaData( column ),
                                                       Math.max( column.getWidth(),
                                                                 DEFAULT_COLUMN_WIDTH ),
                                                       true,
                                                       !column.isHideColumn(),
                                                       access,
                                                       new ListBoxStringSingletonDOMElementFactory( gridPanel,
                                                                                                    gridLayer,
                                                                                                    gridWidget ),
                                                       new TextBoxStringSingletonDOMElementFactory( gridPanel,
                                                                                                    gridLayer,
                                                                                                    gridWidget ),
                                                       presenter,
                                                       factType,
                                                       factField );
        }
    }

    protected GridColumn<BigDecimal> newNumericColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                                       final double width,
                                                       final boolean isResizable,
                                                       final boolean isVisible,
                                                       final GuidedDecisionTablePresenter.Access access,
                                                       final GuidedDecisionTableView gridWidget ) {
        return new BigDecimalUiColumn( headerMetaData,
                                       width,
                                       isResizable,
                                       isVisible,
                                       access,
                                       new TextBoxBigDecimalSingletonDOMElementFactory( gridPanel,
                                                                                        gridLayer,
                                                                                        gridWidget ) );
    }

    protected GridColumn<BigDecimal> newBigDecimalColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                                          final double width,
                                                          final boolean isResizable,
                                                          final boolean isVisible,
                                                          final GuidedDecisionTablePresenter.Access access,
                                                          final GuidedDecisionTableView gridWidget ) {
        return new BigDecimalUiColumn( headerMetaData,
                                       width,
                                       isResizable,
                                       isVisible,
                                       access,
                                       new TextBoxBigDecimalSingletonDOMElementFactory( gridPanel,
                                                                                        gridLayer,
                                                                                        gridWidget ) );
    }

    protected GridColumn<BigInteger> newBigIntegerColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                                          final double width,
                                                          final boolean isResizable,
                                                          final boolean isVisible,
                                                          final GuidedDecisionTablePresenter.Access access,
                                                          final GuidedDecisionTableView gridWidget ) {
        return new BigIntegerUiColumn( headerMetaData,
                                       width,
                                       isResizable,
                                       isVisible,
                                       access,
                                       new TextBoxBigIntegerSingletonDOMElementFactory( gridPanel,
                                                                                        gridLayer,
                                                                                        gridWidget ) );
    }

    protected GridColumn<Byte> newByteColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                              final double width,
                                              final boolean isResizable,
                                              final boolean isVisible,
                                              final GuidedDecisionTablePresenter.Access access,
                                              final GuidedDecisionTableView gridWidget ) {
        return new ByteUiColumn( headerMetaData,
                                 width,
                                 isResizable,
                                 isVisible,
                                 access,
                                 new TextBoxByteSingletonDOMElementFactory( gridPanel,
                                                                            gridLayer,
                                                                            gridWidget ) );
    }

    protected GridColumn<Double> newDoubleColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                                  final double width,
                                                  final boolean isResizable,
                                                  final boolean isVisible,
                                                  final GuidedDecisionTablePresenter.Access access,
                                                  final GuidedDecisionTableView gridWidget ) {
        return new DoubleUiColumn( headerMetaData,
                                   width,
                                   isResizable,
                                   isVisible,
                                   access,
                                   new TextBoxDoubleSingletonDOMElementFactory( gridPanel,
                                                                                gridLayer,
                                                                                gridWidget ) );
    }

    protected GridColumn<Float> newFloatColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                                final double width,
                                                final boolean isResizable,
                                                final boolean isVisible,
                                                final GuidedDecisionTablePresenter.Access access,
                                                final GuidedDecisionTableView gridWidget ) {
        return new FloatUiColumn( headerMetaData,
                                  width,
                                  isResizable,
                                  isVisible,
                                  access,
                                  new TextBoxFloatSingletonDOMElementFactory( gridPanel,
                                                                              gridLayer,
                                                                              gridWidget ) {

                                  } );
    }

    protected GridColumn<Integer> newIntegerColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                                    final double width,
                                                    final boolean isResizable,
                                                    final boolean isVisible,
                                                    final GuidedDecisionTablePresenter.Access access,
                                                    final GuidedDecisionTableView gridWidget ) {
        return new IntegerUiColumn( headerMetaData,
                                    width,
                                    isResizable,
                                    isVisible,
                                    access,
                                    new TextBoxIntegerSingletonDOMElementFactory( gridPanel,
                                                                                  gridLayer,
                                                                                  gridWidget ) );
    }

    protected GridColumn<Long> newLongColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                              final double width,
                                              final boolean isResizable,
                                              final boolean isVisible,
                                              final GuidedDecisionTablePresenter.Access access,
                                              final GuidedDecisionTableView gridWidget ) {
        return new LongUiColumn( headerMetaData,
                                 width,
                                 isResizable,
                                 isVisible,
                                 access,
                                 new TextBoxLongSingletonDOMElementFactory( gridPanel,
                                                                            gridLayer,
                                                                            gridWidget ) );
    }

    protected GridColumn<Short> newShortColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                                final double width,
                                                final boolean isResizable,
                                                final boolean isVisible,
                                                final GuidedDecisionTablePresenter.Access access,
                                                final GuidedDecisionTableView gridWidget ) {
        return new ShortUiColumn( headerMetaData,
                                  width,
                                  isResizable,
                                  isVisible,
                                  access,
                                  new TextBoxShortSingletonDOMElementFactory( gridPanel,
                                                                              gridLayer,
                                                                              gridWidget ) );
    }

    protected GridColumn<Date> newDateColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                              final double width,
                                              final boolean isResizable,
                                              final boolean isVisible,
                                              final GuidedDecisionTablePresenter.Access access,
                                              final GuidedDecisionTableView gridWidget ) {
        return new DateUiColumn( headerMetaData,
                                 width,
                                 isResizable,
                                 isVisible,
                                 access,
                                 new DatePickerSingletonDOMElementFactory( gridPanel,
                                                                           gridLayer,
                                                                           gridWidget ) );
    }

    protected GridColumn<Boolean> newBooleanColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                                    final double width,
                                                    final boolean isResizable,
                                                    final boolean isVisible,
                                                    final GuidedDecisionTablePresenter.Access access,
                                                    final GuidedDecisionTableView gridWidget ) {
        return new BooleanUiColumn( headerMetaData,
                                    width,
                                    isResizable,
                                    isVisible,
                                    access,
                                    new CheckBoxDOMElementFactory( gridLayer,
                                                                   gridWidget ) {
                                        @Override
                                        public CheckBox createWidget() {
                                            final CheckBox checkBox = super.createWidget();
                                            checkBox.setEnabled( access.isEditable() );
                                            checkBox.addKeyDownHandler( ( e ) -> e.stopPropagation() );
                                            checkBox.addMouseDownHandler( ( e ) -> e.stopPropagation() );
                                            return checkBox;
                                        }
                                    } );
    }

    protected GridColumn<String> newStringColumn( final List<GridColumn.HeaderMetaData> headerMetaData,
                                                  final double width,
                                                  final boolean isResizable,
                                                  final boolean isVisible,
                                                  final GuidedDecisionTablePresenter.Access access,
                                                  final GuidedDecisionTableView gridWidget ) {
        return new StringUiColumn( headerMetaData,
                                   width,
                                   isResizable,
                                   isVisible,
                                   access,
                                   new TextBoxStringSingletonDOMElementFactory( gridPanel,
                                                                                gridLayer,
                                                                                gridWidget ) );
    }

}
