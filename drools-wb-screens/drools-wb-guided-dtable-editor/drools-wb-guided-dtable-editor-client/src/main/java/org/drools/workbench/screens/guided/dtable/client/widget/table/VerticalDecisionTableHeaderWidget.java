/*
 * Copyright 2011 JBoss Inc
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
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AnalysisCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.InsertInternalDecisionTableColumnEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.SetInternalDecisionTableModelEvent;
import org.guvnor.common.services.shared.config.ApplicationPreferences;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.AbstractDecoratedGridHeaderWidget;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.DynamicColumn;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.ResourcesProvider;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.SortConfiguration;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.ColumnResizeEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SetInternalModelEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SortDataEvent;

/**
 * Header for a Vertical Decision Table
 */
public class VerticalDecisionTableHeaderWidget extends AbstractDecoratedGridHeaderWidget<GuidedDecisionTable52, BaseColumn> {

    private static final String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();

    private static final DateTimeFormat format = DateTimeFormat.getFormat( DATE_FORMAT );

    // UI Components
    private HeaderWidget widget;

    //Offsets from the left most column
    private int multiRowColumnOffset = -1;
    private int multiRowColumnActionsOffset = -1;

    /**
     * This is the guts of the widget.
     */
    private class HeaderWidget extends CellPanel {

        /**
         * A Widget to display sort order
         */
        private class HeaderSorter extends FocusPanel {

            private final HorizontalPanel hp = new HorizontalPanel();
            private final DynamicColumn<BaseColumn> col;

            private HeaderSorter( final DynamicColumn<BaseColumn> col ) {
                this.col = col;
                hp.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
                hp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
                hp.setHeight( resources.rowHeaderSorterHeight() + "px" );
                hp.setWidth( "100%" );
                setIconImage();
                add( hp );

                // Ensure our icon is updated when the SortDirection changes
                col.addValueChangeHandler( new ValueChangeHandler<SortConfiguration>() {

                    public void onValueChange( ValueChangeEvent<SortConfiguration> event ) {
                        setIconImage();
                    }

                } );
            }

            // Set icon's resource accordingly
            private void setIconImage() {
                hp.clear();
                switch ( col.getSortDirection() ) {
                    case ASCENDING:
                        switch ( col.getSortIndex() ) {
                            case 0:
                                hp.add( new Image( resources.upArrowIcon() ) );
                                break;
                            default:
                                hp.add( new Image( resources.smallUpArrowIcon() ) );
                        }
                        break;
                    case DESCENDING:
                        switch ( col.getSortIndex() ) {
                            case 0:
                                hp.add( new Image( resources.downArrowIcon() ) );
                                break;
                            default:
                                hp.add( new Image( resources.smallDownArrowIcon() ) );
                        }
                        break;
                    default:
                        hp.add( new Image( resources.arrowSpacerIcon() ) );
                }
            }

        }

        /**
         * A Widget to split Conditions section
         */
        private class HeaderSplitter extends FocusPanel {

            /**
             * Animation to change the height of a row
             */
            private class HeaderRowAnimation extends Animation {

                private TableRowElement tre;
                private int startHeight;
                private int endHeight;

                private HeaderRowAnimation( TableRowElement tre,
                                            int startHeight,
                                            int endHeight ) {
                    this.tre = tre;
                    this.startHeight = startHeight;
                    this.endHeight = endHeight;
                }

                // Set row height by setting height of children
                private void setHeight( int height ) {
                    for ( int i = 0; i < tre.getChildCount(); i++ ) {
                        tre.getChild( i ).getFirstChild().<DivElement>cast().getStyle().setHeight( height,
                                                                                                   Unit.PX );
                    }
                    fireResizeEvent();
                }

                @Override
                protected void onComplete() {
                    super.onComplete();
                    setHeight( endHeight );
                }

                @Override
                protected void onUpdate( double progress ) {
                    int height = (int) ( startHeight + ( progress * ( endHeight - startHeight ) ) );
                    setHeight( height );
                }

            }

            private Element[] rowHeaders;
            private final HorizontalPanel hp = new HorizontalPanel();
            private final Image icon = new Image();
            private boolean isCollapsed = true;

            private HeaderSplitter() {
                hp.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
                hp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
                hp.getElement().getStyle().setHeight( resources.rowHeaderSplitterHeight(),
                                                      Unit.PX );
                hp.setWidth( "100%" );
                setIconImage();
                hp.add( icon );
                add( hp );

                // Handle action
                addClickHandler( new ClickHandler() {

                    public void onClick( ClickEvent event ) {
                        if ( isCollapsed ) {
                            showRow( 2 );
                            showRow( 3 );
                        } else {
                            hideRow( 2 );
                            hideRow( 3 );
                        }
                        isCollapsed = !isCollapsed;
                        setIconImage();
                    }

                } );
            }

            // Hide a row using our animation
            private void hideRow( int iRow ) {
                if ( rowHeaders == null
                        || ( rowHeaders.length - 1 ) < iRow ) {
                    return;
                }
                TableRowElement tre = rowHeaders[ iRow ].<TableRowElement>cast();
                HeaderRowAnimation anim = new HeaderRowAnimation( tre,
                                                                  resources.rowHeaderHeight(),
                                                                  0 );
                anim.run( 250 );
            }

            // Set icon's resource accordingly
            private void setIconImage() {
                if ( isCollapsed ) {
                    icon.setResource( resources.smallDownArrowIcon() );
                } else {
                    icon.setResource( resources.smallUpArrowIcon() );
                }
            }

            // Set rows to animate
            private void setRowHeaders( Element[] rowHeaders ) {
                this.rowHeaders = rowHeaders;
            }

            // Show a row using our animation
            private void showRow( int iRow ) {
                if ( rowHeaders == null || ( rowHeaders.length - 1 ) < iRow ) {
                    return;
                }
                TableRowElement tre = rowHeaders[ iRow ].<TableRowElement>cast();
                HeaderRowAnimation anim = new HeaderRowAnimation( tre,
                                                                  0,
                                                                  resources.rowHeaderHeight() );
                anim.run( 250 );
            }

        }

        // Child Widgets used in this Widget
        private List<HeaderSorter> sorters = new ArrayList<HeaderSorter>();
        private HeaderSplitter splitter = new HeaderSplitter();

        // UI Components
        private Element[] rowHeaders = new Element[ 5 ];

        private List<DynamicColumn<BaseColumn>> visibleCols = new ArrayList<DynamicColumn<BaseColumn>>();
        private List<DynamicColumn<BaseColumn>> visibleConditionCols = new ArrayList<DynamicColumn<BaseColumn>>();
        private List<DynamicColumn<BaseColumn>> visibleActionCols = new ArrayList<DynamicColumn<BaseColumn>>();

        // Constructor
        private HeaderWidget() {
            for ( int iRow = 0; iRow < rowHeaders.length; iRow++ ) {
                rowHeaders[ iRow ] = DOM.createTR();
                getBody().appendChild( rowHeaders[ iRow ] );
            }
            getBody().getParentElement().<TableElement>cast().setCellSpacing( 0 );
            getBody().getParentElement().<TableElement>cast().setCellPadding( 0 );
        }

        // Make default header label
        private Element makeLabel( String text,
                                   int width,
                                   int height ) {
            Element div = DOM.createDiv();
            div.getStyle().setWidth( width,
                                     Unit.PX );
            div.getStyle().setHeight( height,
                                      Unit.PX );
            div.getStyle().setOverflow( Overflow.HIDDEN );
            div.setInnerText( text );
            return div;
        }

        // Populate a default header element
        private void populateTableCellElement( BaseColumn modelCol,
                                               int width,
                                               Element tce ) {

            if ( modelCol instanceof RowNumberCol52 ) {
                tce.appendChild( makeLabel( "#",
                                            width,
                                            resources.rowHeaderHeight() ) );
                tce.<TableCellElement>cast().setRowSpan( 4 );
                tce.addClassName( resources.headerRowIntermediate() );
                tce.addClassName( resources.cellTableColumn( modelCol ) );
            } else if ( modelCol instanceof DescriptionCol52 ) {
                tce.appendChild( makeLabel( GuidedDecisionTableConstants.INSTANCE.Description(),
                                            width,
                                            resources.rowHeaderHeight() ) );
                tce.<TableCellElement>cast().setRowSpan( 4 );
                tce.addClassName( resources.headerRowIntermediate() );
                tce.addClassName( resources.cellTableColumn( modelCol ) );
            } else if ( modelCol instanceof MetadataCol52 ) {
                tce.appendChild( makeLabel( ( (MetadataCol52) modelCol ).getMetadata(),
                                            width,
                                            resources.rowHeaderHeight() ) );
                tce.<TableCellElement>cast().setRowSpan( 4 );
                tce.addClassName( resources.headerRowIntermediate() );
                tce.addClassName( resources.cellTableColumn( modelCol ) );
            } else if ( modelCol instanceof AttributeCol52 ) {
                tce.appendChild( makeLabel( ( (AttributeCol52) modelCol ).getAttribute(),
                                            width,
                                            resources.rowHeaderHeight() ) );
                tce.<TableCellElement>cast().setRowSpan( 4 );
                tce.addClassName( resources.headerRowIntermediate() );
                tce.addClassName( resources.cellTableColumn( modelCol ) );
            } else if ( modelCol instanceof ConditionCol52 ) {
                ConditionCol52 cc = (ConditionCol52) modelCol;
                StringBuilder header = new StringBuilder();
                if ( cc.isBound() ) {
                    header.append( cc.getBinding() );
                    header.append( " : " );
                }
                header.append( cc.getHeader() );
                tce.appendChild( makeLabel( header.toString(),
                                            width,
                                            resources.rowHeaderHeight() ) );
                tce.addClassName( resources.cellTableColumn( modelCol ) );
            } else if ( modelCol instanceof ActionCol52 ) {
                tce.appendChild( makeLabel( ( (ActionCol52) modelCol ).getHeader(),
                                            width,
                                            resources.rowHeaderHeight() ) );
                tce.addClassName( resources.cellTableColumn( modelCol ) );
            } else if ( modelCol instanceof AnalysisCol52 ) {
                tce.appendChild( makeLabel( GuidedDecisionTableConstants.INSTANCE.Analysis(),
                                            width,
                                            resources.rowHeaderHeight() ) );
                tce.<TableCellElement>cast().setRowSpan( 4 );
                tce.addClassName( resources.headerRowIntermediate() );
                tce.addClassName( resources.cellTableColumn( modelCol ) );
            }

        }

        // Redraw entire header
        private void redraw() {

            // Remove existing widgets from the DOM hierarchy
            if ( splitter != null ) {
                remove( splitter );
            }
            for ( HeaderSorter sorter : sorters ) {
                remove( sorter );
            }
            sorters.clear();

            // Extracting visible columns makes life easier
            visibleCols.clear();
            visibleConditionCols.clear();
            visibleActionCols.clear();
            multiRowColumnOffset = -1;
            multiRowColumnActionsOffset = -1;
            int iColumnCount = 0;
            for ( int iCol = 0; iCol < sortableColumns.size(); iCol++ ) {
                DynamicColumn<BaseColumn> col = sortableColumns.get( iCol );
                if ( col.isVisible() ) {
                    visibleCols.add( col );
                    BaseColumn modelCol = col.getModelColumn();
                    if ( modelCol instanceof ConditionCol52 ) {
                        if ( multiRowColumnOffset == -1 ) {
                            multiRowColumnOffset = iColumnCount;
                        }
                        visibleConditionCols.add( col );
                    }
                    if ( modelCol instanceof ActionCol52 ) {
                        if ( multiRowColumnOffset == -1 ) {
                            multiRowColumnOffset = iColumnCount;
                        }
                        if ( multiRowColumnActionsOffset == -1 ) {
                            multiRowColumnActionsOffset = iColumnCount;
                        }
                        visibleActionCols.add( col );
                    }
                    iColumnCount++;
                }
            }

            // Draw rows
            for ( int iRow = 0; iRow < rowHeaders.length; iRow++ ) {
                redrawHeaderRow( iRow );
            }

            fireResizeEvent();
        }

        // Redraw a single row obviously
        private void redrawHeaderRow( int iRow ) {
            Element tce = null;
            Element tre = DOM.createTR();
            switch ( iRow ) {
                case 0:
                    // General row, all visible cells included
                    for ( int iCol = 0; iCol < visibleCols.size(); iCol++ ) {
                        DynamicColumn<BaseColumn> col = visibleCols.get( iCol );
                        BaseColumn modelCol = col.getModelColumn();
                        tce = DOM.createTD();
                        tce.addClassName( resources.headerText() );
                        tre.appendChild( tce );

                        // Merging
                        int colSpan = 1;
                        int width = col.getWidth();
                        if ( modelCol instanceof BRLVariableColumn ) {
                            BRLVariableColumn brlColumn = (BRLVariableColumn) col.getModelColumn();
                            BRLColumn<?, ?> brlColumnParent = model.getBRLColumn( brlColumn );

                            while ( iCol + colSpan < visibleCols.size() ) {
                                DynamicColumn<BaseColumn> mergeCol = visibleCols.get( iCol + colSpan );
                                BaseColumn mergeModelCol = mergeCol.getModelColumn();
                                if ( !( mergeModelCol instanceof BRLVariableColumn ) ) {
                                    break;
                                }
                                BRLVariableColumn mergeBRLColumn = (BRLVariableColumn) mergeModelCol;
                                BRLColumn<?, ?> mergeBRLColumnParent = model.getBRLColumn( mergeBRLColumn );
                                if ( mergeBRLColumnParent != brlColumnParent ) {
                                    break;
                                }
                                width = width + mergeCol.getWidth();
                                colSpan++;
                            }
                            iCol = iCol + colSpan - 1;
                        }

                        populateTableCellElement( modelCol,
                                                  width,
                                                  tce );
                        tce.<TableCellElement>cast().setColSpan( colSpan );
                    }
                    break;

                case 1:
                    // Splitter between "general" and "technical" condition details
                    if ( visibleConditionCols.size() > 0 || visibleActionCols.size() > 0 ) {
                        splitter.setRowHeaders( rowHeaders );
                        tce = DOM.createTD();
                        tce.<TableCellElement>cast().setColSpan( visibleConditionCols.size() + visibleActionCols.size() );
                        tce.addClassName( resources.headerSplitter() );
                        tre.appendChild( tce );
                        add( splitter,
                             tce );
                    }
                    break;

                case 2:
                    // Condition FactType, merged between identical
                    for ( int iCol = 0; iCol < visibleConditionCols.size(); iCol++ ) {
                        DynamicColumn<BaseColumn> col = visibleConditionCols.get( iCol );
                        ConditionCol52 cc = (ConditionCol52) col.getModelColumn();
                        Pattern52 ccPattern = model.getPattern( cc );

                        tce = DOM.createTD();
                        tce.addClassName( resources.headerText() );
                        tce.addClassName( resources.cellTableColumn( col.getModelColumn() ) );
                        tce.addClassName( resources.headerRowIntermediate() );
                        tre.appendChild( tce );

                        // Merging
                        int colSpan = 1;
                        int width = col.getWidth();
                        while ( iCol + colSpan < visibleConditionCols.size() ) {
                            DynamicColumn<BaseColumn> mergeCol = visibleConditionCols.get( iCol + colSpan );
                            ConditionCol52 mergeCondCol = (ConditionCol52) mergeCol.getModelColumn();
                            Pattern52 mergeCondColPattern = model.getPattern( mergeCondCol );
                            if ( mergeCondColPattern != ccPattern ) {
                                break;
                            }
                            width = width + mergeCol.getWidth();
                            colSpan++;
                        }
                        iCol = iCol + colSpan - 1;

                        //Make applicable label (TODO move to Factory method)
                        StringBuilder label = new StringBuilder();
                        if ( cc instanceof LimitedEntryBRLConditionColumn ) {
                            //Nothing needed
                        } else if ( cc instanceof BRLConditionVariableColumn ) {
                            BRLConditionVariableColumn brl = (BRLConditionVariableColumn) cc;
                            label.append( brl.getVarName() );
                        } else if ( cc instanceof ConditionCol52 ) {
                            String factType = ccPattern.getFactType();
                            String boundName = ccPattern.getBoundName();
                            if ( factType != null && factType.length() > 0 ) {
                                if ( ccPattern.isNegated() ) {
                                    label.append( GuidedDecisionTableConstants.INSTANCE.negatedPattern() ).append( " " ).append( factType );
                                } else {
                                    label.append( factType ).append( " [" ).append( boundName ).append( "]" );
                                }
                            }
                        }

                        tce.appendChild( makeLabel( label.toString(),
                                                    width,
                                                    ( splitter.isCollapsed ? 0 : resources.rowHeaderHeight() ) ) );
                        tce.<TableCellElement>cast().setColSpan( colSpan );

                    }

                    //Action FactType
                    for ( int iCol = 0; iCol < visibleActionCols.size(); iCol++ ) {
                        DynamicColumn<BaseColumn> col = visibleActionCols.get( iCol );
                        ActionCol52 ac = (ActionCol52) col.getModelColumn();

                        tce = DOM.createTD();
                        tce.addClassName( resources.headerText() );
                        tce.addClassName( resources.cellTableColumn( col.getModelColumn() ) );
                        tce.addClassName( resources.headerRowIntermediate() );
                        tre.appendChild( tce );

                        //Make applicable label (TODO move to Factory method)
                        StringBuilder label = new StringBuilder();
                        if ( ac instanceof ActionInsertFactCol52 ) {
                            ActionInsertFactCol52 aifc = (ActionInsertFactCol52) ac;
                            String factType = aifc.getFactType();
                            String binding = aifc.getBoundName();
                            if ( factType != null && factType.length() > 0 ) {
                                label.append( factType );
                                if ( binding != null ) {
                                    label.append( " [" + binding + "]" );
                                }
                            }
                        } else if ( ac instanceof ActionSetFieldCol52 ) {
                            String factType = ( (ActionSetFieldCol52) ac ).getBoundName();
                            if ( factType != null && factType.length() > 0 ) {
                                label.append( factType );
                            }
                        } else if ( ac instanceof LimitedEntryActionRetractFactCol52 ) {
                            String factType = ( (LimitedEntryActionRetractFactCol52) ac ).getValue().getStringValue();
                            if ( factType != null && factType.length() > 0 ) {
                                label.append( factType );
                            }
                        } else if ( ac instanceof ActionWorkItemCol52 ) {
                            String factType = ( (ActionWorkItemCol52) ac ).getWorkItemDefinition().getDisplayName();
                            if ( factType != null && factType.length() > 0 ) {
                                label.append( factType );
                            }
                        } else if ( ac instanceof BRLActionVariableColumn ) {
                            String factType = ( (BRLActionVariableColumn) ac ).getVarName();
                            if ( factType != null && factType.length() > 0 ) {
                                label.append( factType );
                            }
                        }

                        tce.appendChild( makeLabel( label.toString(),
                                                    col.getWidth(),
                                                    ( splitter.isCollapsed ? 0 : resources.rowHeaderHeight() ) ) );
                    }
                    break;

                case 3:
                    // Condition Fact Fields
                    for ( DynamicColumn<BaseColumn> col : visibleConditionCols ) {
                        tce = DOM.createTD();
                        tce.addClassName( resources.headerText() );
                        tce.addClassName( resources.headerRowIntermediate() );
                        tce.addClassName( resources.cellTableColumn( col.getModelColumn() ) );
                        tre.appendChild( tce );
                        ConditionCol52 cc = (ConditionCol52) col.getModelColumn();

                        //Make applicable label (TODO move to Factory method)
                        StringBuilder label = new StringBuilder();
                        if ( cc instanceof LimitedEntryBRLConditionColumn ) {
                            //Nothing needed
                        } else if ( cc instanceof BRLConditionVariableColumn ) {
                            BRLConditionVariableColumn brl = (BRLConditionVariableColumn) cc;
                            String field = brl.getFactField();
                            label.append( field == null ? "" : field );
                        } else {
                            String factField = cc.getFactField();
                            if ( factField != null && factField.length() > 0 ) {
                                label.append( factField );
                            }
                            if ( cc.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_PREDICATE ) {
                                label.append( " [" );
                                label.append( cc.getOperator() );
                                String lev = getLimitedEntryValue( cc );
                                if ( lev != null ) {
                                    label.append( lev );
                                }
                                label.append( "]" );
                            }
                        }

                        tce.appendChild( makeLabel( label.toString(),
                                                    col.getWidth(),
                                                    ( splitter.isCollapsed ? 0 : resources.rowHeaderHeight() ) ) );
                    }

                    // Action Fact Fields
                    for ( DynamicColumn<BaseColumn> col : visibleActionCols ) {
                        tce = DOM.createTD();
                        tce.addClassName( resources.headerText() );
                        tce.addClassName( resources.headerRowIntermediate() );
                        tce.addClassName( resources.cellTableColumn( col.getModelColumn() ) );
                        tre.appendChild( tce );
                        ActionCol52 ac = (ActionCol52) col.getModelColumn();

                        //Make applicable label (TODO move to Factory method)
                        StringBuilder label = new StringBuilder();
                        if ( ac instanceof ActionInsertFactCol52 ) {
                            ActionInsertFactCol52 aifc = (ActionInsertFactCol52) ac;
                            String factField = aifc.getFactField();
                            if ( factField != null && factField.length() > 0 ) {
                                label.append( factField );
                            }
                            String lev = getLimitedEntryValue( aifc );
                            if ( lev != null ) {
                                label.append( " [" );
                                label.append( lev );
                                label.append( "]" );
                            }
                        } else if ( ac instanceof ActionSetFieldCol52 ) {
                            ActionSetFieldCol52 asf = (ActionSetFieldCol52) ac;
                            String factField = asf.getFactField();
                            if ( factField != null && factField.length() > 0 ) {
                                label.append( factField );
                            }
                            String lev = getLimitedEntryValue( asf );
                            if ( lev != null ) {
                                label.append( " [" );
                                label.append( lev );
                                label.append( "]" );
                            }
                        } else if ( ac instanceof ActionRetractFactCol52 ) {
                            label.append( "[" + GuidedDecisionTableConstants.INSTANCE.Delete() + "]" );
                        } else if ( ac instanceof ActionWorkItemCol52 ) {
                            label.append( "[" + GuidedDecisionTableConstants.INSTANCE.WorkItemAction() + "]" );
                        } else if ( ac instanceof BRLActionVariableColumn ) {
                            BRLActionVariableColumn brl = (BRLActionVariableColumn) ac;
                            String field = brl.getFactField();
                            label.append( field == null ? "" : field );
                        }

                        tce.appendChild( makeLabel( label.toString(),
                                                    col.getWidth(),
                                                    ( splitter.isCollapsed ? 0 : resources.rowHeaderHeight() ) ) );
                    }
                    break;

                case 4:
                    // Sorters
                    for ( DynamicColumn<BaseColumn> col : sortableColumns ) {
                        if ( col.isVisible() ) {
                            final HeaderSorter shp = new HeaderSorter( col );
                            final DynamicColumn<BaseColumn> sortableColumn = col;
                            if ( !isReadOnly ) {
                                shp.addClickHandler( new ClickHandler() {

                                    public void onClick( ClickEvent event ) {
                                        if ( sortableColumn.isSortable() ) {
                                            updateSortOrder( sortableColumn );

                                            SortDataEvent sde = new SortDataEvent( getSortConfiguration() );
                                            eventBus.fireEvent( sde );
                                        }
                                    }

                                } );
                            }
                            sorters.add( shp );

                            tce = DOM.createTD();
                            tce.addClassName( resources.headerRowBottom() );
                            tce.addClassName( resources.cellTableColumn( col.getModelColumn() ) );
                            tre.appendChild( tce );
                            add( shp,
                                 tce );
                        }
                    }
                    break;
            }

            getBody().replaceChild( tre,
                                    rowHeaders[ iRow ] );
            rowHeaders[ iRow ] = tre;
        }

        private String getLimitedEntryValue( DTColumnConfig52 c ) {
            if ( !( c instanceof LimitedEntryCol ) ) {
                return null;
            }
            LimitedEntryCol lec = (LimitedEntryCol) c;
            DTCellValue52 cv = lec.getValue();
            if ( cv == null ) {
                return null;
            }
            DataType.DataTypes type = cv.getDataType();
            switch ( type ) {
                case BOOLEAN:
                    return cv.getBooleanValue().toString();
                case NUMERIC:
                    final BigDecimal numeric = (BigDecimal) cv.getNumericValue();
                    return numeric.toPlainString();
                case NUMERIC_BIGDECIMAL:
                    final BigDecimal numericBigDecimal = (BigDecimal) cv.getNumericValue();
                    return numericBigDecimal.toPlainString();
                case NUMERIC_BIGINTEGER:
                    final BigInteger numericBigInteger = (BigInteger) cv.getNumericValue();
                    return numericBigInteger.toString();
                case NUMERIC_BYTE:
                    final Byte numericByte = (Byte) cv.getNumericValue();
                    return numericByte.toString();
                case NUMERIC_DOUBLE:
                    final Double numericDouble = (Double) cv.getNumericValue();
                    return numericDouble.toString();
                case NUMERIC_FLOAT:
                    final Float numericFloat = (Float) cv.getNumericValue();
                    return numericFloat.toString();
                case NUMERIC_INTEGER:
                    final Integer numericInteger = (Integer) cv.getNumericValue();
                    return numericInteger.toString();
                case NUMERIC_LONG:
                    final Long numericLong = (Long) cv.getNumericValue();
                    return numericLong.toString();
                case NUMERIC_SHORT:
                    final Short numericShort = (Short) cv.getNumericValue();
                    return numericShort.toString();
                case DATE:
                    return format.format( cv.getDateValue() );
                default:
                    return cv.getStringValue();
            }
        }

    }

    /**
     * Construct a "Header" for the provided DecisionTable
     * @param resources
     * @param eventBus
     */
    public VerticalDecisionTableHeaderWidget( final ResourcesProvider<BaseColumn> resources,
                                              final boolean isReadOnly,
                                              final EventBus eventBus ) {
        super( resources,
               isReadOnly,
               eventBus );

        //Wire-up event handlers
        eventBus.addHandler( SetInternalDecisionTableModelEvent.TYPE,
                             this );
        eventBus.addHandler( InsertInternalDecisionTableColumnEvent.TYPE,
                             this );
        addResizeHandler( new ResizeHandler() {
            @Override
            public void onResize( final ResizeEvent event ) {
                final int width = event.getWidth();
                panel.setWidth( width + "px" );
            }
        } );
    }

    @Override
    public void redraw() {
        widget.redraw();
    }

    @Override
    public void setScrollPosition( int position ) {
        if ( position < 0 ) {
            throw new IllegalArgumentException( "position cannot be null" );
        }

        ( (ScrollPanel) this.panel ).setHorizontalScrollPosition( position );
    }

    // Schedule resize event after header has been drawn or resized
    private void fireResizeEvent() {
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {
            public void execute() {
                ResizeEvent.fire( VerticalDecisionTableHeaderWidget.this,
                                  widget.getOffsetWidth(),
                                  widget.getOffsetHeight() );
            }
        } );

    }

    // Set the cursor type for all cells on the table as
    // we only use rowHeader[4] to check which column
    // needs resizing however the mouse could be over any
    // row
    private void setCursorType( Cursor cursor ) {
        for ( int iRow = 0; iRow < widget.rowHeaders.length; iRow++ ) {
            TableRowElement tre = widget.rowHeaders[ iRow ].<TableRowElement>cast();
            for ( int iCol = 0; iCol < tre.getCells().getLength(); iCol++ ) {
                TableCellElement tce = tre.getCells().getItem( iCol );
                tce.getStyle().setCursor( cursor );
            }
        }

    }

    @Override
    protected Widget getHeaderWidget() {
        if ( this.widget == null ) {
            this.widget = new HeaderWidget();
        }
        return widget;
    }

    @Override
    protected ResizerInformation getResizerInformation( int mx ) {
        boolean isPrimed = false;
        ResizerInformation resizerInfo = new ResizerInformation();
        for ( int iCol = 0; iCol < widget.rowHeaders[ 4 ].getChildCount(); iCol++ ) {
            TableCellElement tce = widget.rowHeaders[ 4 ].getChild( iCol ).<TableCellElement>cast();
            int cx = tce.getAbsoluteRight();
            if ( Math.abs( mx - cx ) <= 5 ) {
                isPrimed = true;
                resizerInfo.setResizePrimed( isPrimed );
                resizerInfo.setResizeColumn( widget.visibleCols.get( iCol ) );
                resizerInfo.setResizeColumnLeft( tce.getAbsoluteLeft() );
                break;
            }
        }
        if ( isPrimed ) {
            setCursorType( Cursor.COL_RESIZE );
        } else {
            setCursorType( Cursor.DEFAULT );
        }

        return resizerInfo;
    }

    // Resize the inner DIV in each table cell
    protected void resizeColumn( DynamicColumn<BaseColumn> resizeColumn,
                                 int resizeColumnWidth ) {

        DivElement div;
        TableCellElement tce;
        int colOffsetIndex;

        // This is also set in the ColumnResizeEvent handler, however it makes
        // resizing columns in the header more simple too
        resizeColumn.setWidth( resizeColumnWidth );
        resizeColumn.getModelColumn().setWidth( resizeColumnWidth );
        int resizeColumnIndex = widget.visibleCols.indexOf( resizeColumn );

        // Row 0 (General\Fact Type)
        // General row, all visible cells included
        int iRow0ColColumn = 0;
        for ( int iCol = 0; iCol < widget.visibleCols.size(); iCol++ ) {
            DynamicColumn<BaseColumn> col = widget.visibleCols.get( iCol );
            BaseColumn modelCol = col.getModelColumn();

            // Merging
            int colSpan = 1;
            int width = col.getWidth();
            if ( modelCol instanceof BRLVariableColumn ) {
                BRLVariableColumn brlColumn = (BRLVariableColumn) col.getModelColumn();
                BRLColumn<?, ?> brlColumnParent = model.getBRLColumn( brlColumn );

                while ( iCol + colSpan < widget.visibleCols.size() ) {
                    DynamicColumn<BaseColumn> mergeCol = widget.visibleCols.get( iCol + colSpan );
                    BaseColumn mergeModelCol = mergeCol.getModelColumn();
                    if ( !( mergeModelCol instanceof BRLVariableColumn ) ) {
                        break;
                    }
                    BRLVariableColumn mergeBRLColumn = (BRLVariableColumn) mergeModelCol;
                    BRLColumn<?, ?> mergeBRLColumnParent = model.getBRLColumn( mergeBRLColumn );
                    if ( mergeBRLColumnParent != brlColumnParent ) {
                        break;
                    }
                    width = width + mergeCol.getWidth();
                    colSpan++;
                }
                iCol = iCol + colSpan - 1;
            }

            // Resize cell
            tce = widget.rowHeaders[ 0 ].getChild( iRow0ColColumn ).<TableCellElement>cast();
            div = tce.getFirstChild().<DivElement>cast();
            div.getStyle().setWidth( width,
                                     Unit.PX );
            iRow0ColColumn++;
        }

        // Row 4 (Sorters)
        tce = widget.rowHeaders[ 4 ].getChild( resizeColumnIndex ).<TableCellElement>cast();
        div = tce.getFirstChild().<DivElement>cast();
        div.getStyle().setWidth( resizeColumnWidth,
                                 Unit.PX );

        // Row 3 (Fact Fields)
        if ( multiRowColumnOffset != -1 ) {
            colOffsetIndex = resizeColumnIndex - multiRowColumnOffset;
            if ( colOffsetIndex >= 0 && !( resizeColumn.getModelColumn() instanceof AnalysisCol52 ) ) {
                DynamicColumn<BaseColumn> col = widget.visibleCols.get( resizeColumnIndex );
                tce = widget.rowHeaders[ 3 ].getChild( colOffsetIndex ).<TableCellElement>cast();
                div = tce.getFirstChild().<DivElement>cast();
                div.getStyle().setWidth( col.getWidth(),
                                         Unit.PX );
            }
        }

        // Row 2 (Fact Types) - Condition Columns
        int iRow2ColColumn = 0;
        for ( int iCol = 0; iCol < widget.visibleConditionCols.size(); iCol++ ) {
            DynamicColumn<BaseColumn> col = widget.visibleConditionCols.get( iCol );
            ConditionCol52 cc = (ConditionCol52) col.getModelColumn();
            Pattern52 ccPattern = model.getPattern( cc );

            // Merging
            int colSpan = 1;
            int width = col.getWidth();
            while ( iCol + colSpan < widget.visibleConditionCols.size() ) {
                DynamicColumn<BaseColumn> mergeCol = widget.visibleConditionCols.get( iCol + colSpan );
                ConditionCol52 mergeCondCol = (ConditionCol52) mergeCol.getModelColumn();
                Pattern52 mergeCondColPattern = model.getPattern( mergeCondCol );
                if ( mergeCondColPattern != ccPattern ) {
                    break;
                }
                width = width + mergeCol.getWidth();
                colSpan++;
            }

            // Resize cell
            iCol = iCol + colSpan - 1;
            tce = widget.rowHeaders[ 2 ].getChild( iRow2ColColumn ).<TableCellElement>cast();
            div = tce.getFirstChild().<DivElement>cast();
            div.getStyle().setWidth( width,
                                     Unit.PX );
            iRow2ColColumn++;
        }

        // Row 2 (Fact Types) - Action Columns
        if ( multiRowColumnActionsOffset != -1 ) {
            colOffsetIndex = resizeColumnIndex - multiRowColumnActionsOffset;
            if ( colOffsetIndex >= 0 && !( resizeColumn.getModelColumn() instanceof AnalysisCol52 ) ) {
                colOffsetIndex = colOffsetIndex + iRow2ColColumn;
                DynamicColumn<BaseColumn> col = widget.visibleCols.get( resizeColumnIndex );
                tce = widget.rowHeaders[ 2 ].getChild( colOffsetIndex ).<TableCellElement>cast();
                div = tce.getFirstChild().<DivElement>cast();
                div.getStyle().setWidth( col.getWidth(),
                                         Unit.PX );
            }
        }

        // Fire event to any interested consumers
        ColumnResizeEvent cre = new ColumnResizeEvent( widget.visibleCols.get( resizeColumnIndex ),
                                                       resizeColumnWidth );
        eventBus.fireEvent( cre );
    }

    public void onSetInternalModel( SetInternalModelEvent<GuidedDecisionTable52, BaseColumn> event ) {
        this.sortableColumns.clear();
        this.model = event.getModel();
        List<DynamicColumn<BaseColumn>> columns = event.getColumns();
        for ( DynamicColumn<BaseColumn> column : columns ) {
            sortableColumns.add( column );
        }
        redraw();
    }

}
