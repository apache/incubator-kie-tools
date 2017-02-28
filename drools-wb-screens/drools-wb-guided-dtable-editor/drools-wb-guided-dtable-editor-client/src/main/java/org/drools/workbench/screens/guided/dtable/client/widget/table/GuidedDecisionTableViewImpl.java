/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.Set;
import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.ActionColumnCommand;
import org.drools.workbench.screens.guided.dtable.client.widget.ActionInsertFactPopup;
import org.drools.workbench.screens.guided.dtable.client.widget.ActionRetractFactPopup;
import org.drools.workbench.screens.guided.dtable.client.widget.ActionSetFieldPopup;
import org.drools.workbench.screens.guided.dtable.client.widget.ActionWorkItemInsertFactPopup;
import org.drools.workbench.screens.guided.dtable.client.widget.ActionWorkItemPopup;
import org.drools.workbench.screens.guided.dtable.client.widget.ActionWorkItemSetFieldPopup;
import org.drools.workbench.screens.guided.dtable.client.widget.BRLActionColumnViewImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.BRLConditionColumnViewImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.ConditionColumnCommand;
import org.drools.workbench.screens.guided.dtable.client.widget.ConditionPopup;
import org.drools.workbench.screens.guided.dtable.client.widget.LimitedEntryBRLActionColumnViewImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.LimitedEntryBRLConditionColumnViewImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.themes.GuidedDecisionTableTheme;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.workbench.events.NotificationEvent;

public class GuidedDecisionTableViewImpl extends BaseGridWidget implements GuidedDecisionTableView {

    public static final int HEADER_CAPTION_WIDTH = 200;

    public static final int HEADER_CAPTION_HEIGHT = 32;

    private final GuidedDecisionTableView.Presenter presenter;
    private final GuidedDecisionTable52 model;
    private final AsyncPackageDataModelOracle oracle;
    private final Set<PortableWorkDefinition> workItemDefinitions;
    private final Event<NotificationEvent> notificationEvent;
    private final EventBus eventBus;
    private final GuidedDecisionTablePresenter.Access access;

    private final Group headerCaption;

    public GuidedDecisionTableViewImpl( final GridData uiModel,
                                        final GridRenderer renderer,
                                        final GuidedDecisionTableView.Presenter presenter,
                                        final GuidedDecisionTable52 model,
                                        final AsyncPackageDataModelOracle oracle,
                                        final Set<PortableWorkDefinition> workItemDefinitions,
                                        final Event<NotificationEvent> notificationEvent,
                                        final EventBus eventBus,
                                        final GuidedDecisionTablePresenter.Access access ) {
        super( uiModel,
               presenter,
               presenter,
               renderer );
        this.presenter = presenter;
        this.model = model;
        this.oracle = oracle;
        this.workItemDefinitions = workItemDefinitions;
        this.notificationEvent = notificationEvent;
        this.eventBus = eventBus;
        this.access = access;
        this.headerCaption = makeHeaderCaption();

        addNodeDragMoveHandler( ( event ) -> presenter.getModellerPresenter().updateRadar() );
    }

    private Group makeHeaderCaption() {
        final Group g = new Group();
        final GuidedDecisionTableTheme theme = (GuidedDecisionTableTheme) renderer.getTheme();
        final Rectangle r = theme.getBaseRectangle( GuidedDecisionTableTheme.ModelColumnType.CAPTION )
                .setWidth( HEADER_CAPTION_WIDTH )
                .setHeight( HEADER_CAPTION_HEIGHT );

        final MultiPath border = theme.getBodyGridLine();
        border.M( 0.5,
                  HEADER_CAPTION_HEIGHT + 0.5 )
                .L( 0.5,
                    0.5 )
                .L( HEADER_CAPTION_WIDTH + 0.5,
                    0.5 )
                .L( HEADER_CAPTION_WIDTH + 0.5,
                    HEADER_CAPTION_HEIGHT + 0.5 )
                .L( 0.5,
                    HEADER_CAPTION_HEIGHT + 0.5 );

        final Text caption = theme.getHeaderText()
                .setText( model.getTableName() )
                .setX( HEADER_CAPTION_WIDTH / 2 )
                .setY( HEADER_CAPTION_HEIGHT / 2 );

        g.add( r );
        g.add( caption );
        g.add( border );

        //Add handler to enter/exit "pinned" mode
        addNodeMouseDoubleClickHandler( ( event ) -> {
            if ( isNodeMouseEventOverCaption( event ) ) {
                if ( presenter.isGridPinned() ) {
                    presenter.exitPinnedMode( () -> {/*Nothing*/} );

                } else {
                    presenter.enterPinnedMode( GuidedDecisionTableViewImpl.this,
                                               () -> {/*Nothing*/} );
                }
            }
        } );

        return g;
    }

    @Override
    public boolean onDragHandle( final INodeXYEvent event ) {
        return isNodeMouseEventOverCaption( event );
    }

    private boolean isNodeMouseEventOverCaption( final INodeXYEvent event ) {
        final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate( this,
                                                                           new Point2D( event.getX(),
                                                                                        event.getY() ) );
        final double cx = ap.getX();
        final double cy = ap.getY();

        if ( cx > headerCaption.getX() && cx < headerCaption.getX() + HEADER_CAPTION_WIDTH ) {
            if ( cy > headerCaption.getY() && cy < headerCaption.getY() + HEADER_CAPTION_HEIGHT ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setLocation( final double x,
                             final double y ) {
        setLocation( new Point2D( x,
                                  y ) );
    }

    @Override
    protected void drawHeader( final BaseGridRendererHelper.RenderingInformation renderingInformation,
                               final boolean isSelectionLayer ) {
        super.drawHeader( renderingInformation,
                          isSelectionLayer );

        headerCaption.setY( header == null ? 0.0 : header.getY() );

        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        if ( !floatingBlockInformation.getColumns().isEmpty() ) {
            headerCaption.setX( floatingBlockInformation.getX() );

        } else {
            headerCaption.setX( 0.0 );
        }

        add( headerCaption );
    }

    @Override
    public void newAttributeOrMetaDataColumn( final Set<String> reservedAttributeNames ) {
        new GuidedDecisionTableAttributeSelectorPopup( reservedAttributeNames.toArray( new String[0] ),
                                                       presenter ).show();
    }

    @Override
    public void newExtendedEntryConditionColumn() {
        final ConditionCol52 column = new ConditionCol52();
        doNewConditionColumn( column );
    }

    @Override
    public void newLimitedEntryConditionColumn() {
        final ConditionCol52 column = new LimitedEntryConditionCol52();
        doNewConditionColumn( column );
    }

    private void doNewConditionColumn( final ConditionCol52 column ) {
        final ConditionPopup popup = new ConditionPopup( model,
                                                         oracle,
                                                         presenter,
                                                         new ConditionColumnCommand() {
                                                             public void execute( final Pattern52 pattern,
                                                                                  final ConditionCol52 column ) {
                                                                 presenter.appendColumn( pattern,
                                                                                         column );
                                                             }
                                                         },
                                                         column,
                                                         true,
                                                         !access.isEditable() );
        popup.show();
    }

    @Override
    public void newExtendedEntryConditionBRLFragment() {
        final BRLConditionColumn column = new BRLConditionColumn();
        final BRLConditionColumnViewImpl popup = new BRLConditionColumnViewImpl( model,
                                                                                 oracle,
                                                                                 presenter,
                                                                                 eventBus,
                                                                                 column,
                                                                                 true,
                                                                                 !access.isEditable() );
        popup.show();
    }

    @Override
    public void newLimitedEntryConditionBRLFragment() {
        final LimitedEntryBRLConditionColumn column = new LimitedEntryBRLConditionColumn();
        final LimitedEntryBRLConditionColumnViewImpl popup = new LimitedEntryBRLConditionColumnViewImpl( model,
                                                                                                         oracle,
                                                                                                         presenter,
                                                                                                         eventBus,
                                                                                                         column,
                                                                                                         true,
                                                                                                         !access.isEditable() );
        popup.show();
    }

    @Override
    public void newExtendedEntryActionInsertColumn() {
        final ActionInsertFactCol52 column = new ActionInsertFactCol52();
        doNewActionInsertColumn( column );
    }

    @Override
    public void newLimitedEntryActionInsertColumn() {
        final LimitedEntryActionInsertFactCol52 column = new LimitedEntryActionInsertFactCol52();
        doNewActionInsertColumn( column );
    }

    private void doNewActionInsertColumn( final ActionInsertFactCol52 column ) {
        final ActionInsertFactPopup popup = new ActionInsertFactPopup( model,
                                                                       oracle,
                                                                       presenter,
                                                                       new ActionColumnCommand() {
                                                                           public void execute( final ActionCol52 column ) {
                                                                               presenter.appendColumn( column );
                                                                           }
                                                                       },
                                                                       column,
                                                                       true,
                                                                       !access.isEditable() );
        popup.show();
    }

    @Override
    public void newExtendedEntryActionSetColumn() {
        final ActionSetFieldCol52 column = new ActionSetFieldCol52();
        doNewActionSetColumn( column );
    }

    @Override
    public void newLimitedEntryActionSetColumn() {
        final LimitedEntryActionSetFieldCol52 column = new LimitedEntryActionSetFieldCol52();
        doNewActionSetColumn( column );
    }

    private void doNewActionSetColumn( final ActionSetFieldCol52 column ) {
        final ActionSetFieldPopup popup = new ActionSetFieldPopup( model,
                                                                   oracle,
                                                                   presenter,
                                                                   new ActionColumnCommand() {
                                                                       public void execute( final ActionCol52 column ) {
                                                                           presenter.appendColumn( column );
                                                                       }
                                                                   },
                                                                   column,
                                                                   true,
                                                                   !access.isEditable() );
        popup.show();
    }

    @Override
    public void newExtendedEntryActionRetractFact() {
        final ActionRetractFactCol52 column = new ActionRetractFactCol52();
        doNewActionRetractFactColumn( column );
    }

    @Override
    public void newLimitedEntryActionRetractFact() {
        final LimitedEntryActionRetractFactCol52 column = new LimitedEntryActionRetractFactCol52();
        column.setValue( new DTCellValue52( "" ) );
        doNewActionRetractFactColumn( column );
    }

    private void doNewActionRetractFactColumn( final ActionRetractFactCol52 column ) {
        final ActionRetractFactPopup popup = new ActionRetractFactPopup( model,
                                                                         presenter,
                                                                         new ActionColumnCommand() {
                                                                             public void execute( final ActionCol52 column ) {
                                                                                 presenter.appendColumn( column );
                                                                             }
                                                                         },
                                                                         column,
                                                                         true,
                                                                         !access.isEditable() );
        popup.show();
    }

    @Override
    public void newActionWorkItem() {
        //WorkItems are defined within the column and always boolean (i.e. Limited Entry) in the table
        final ActionWorkItemCol52 column = new ActionWorkItemCol52();
        final ActionWorkItemPopup popup = new ActionWorkItemPopup( model,
                                                                   presenter,
                                                                   new ActionColumnCommand() {
                                                                       public void execute( final ActionCol52 column ) {
                                                                           presenter.appendColumn( column );
                                                                       }
                                                                   },
                                                                   column,
                                                                   workItemDefinitions,
                                                                   true,
                                                                   !access.isEditable() );
        popup.show();
    }

    @Override
    public void newActionWorkItemSetField() {
        //Actions setting Field Values from Work Item Result Parameters are always boolean (i.e. Limited Entry) in the table
        final ActionWorkItemSetFieldCol52 column = new ActionWorkItemSetFieldCol52();
        final ActionWorkItemSetFieldPopup popup = new ActionWorkItemSetFieldPopup( model,
                                                                                   oracle,
                                                                                   presenter,
                                                                                   new ActionColumnCommand() {
                                                                                       public void execute( final ActionCol52 column ) {
                                                                                           presenter.appendColumn( column );
                                                                                       }
                                                                                   },
                                                                                   column,
                                                                                   true,
                                                                                   !access.isEditable() );
        popup.show();
    }

    @Override
    public void newActionWorkItemInsertFact() {
        //Actions setting Field Values from Work Item Result Parameters are always boolean (i.e. Limited Entry) in the table
        final ActionWorkItemInsertFactCol52 column = new ActionWorkItemInsertFactCol52();
        final ActionWorkItemInsertFactPopup popup = new ActionWorkItemInsertFactPopup( model,
                                                                                       oracle,
                                                                                       presenter,
                                                                                       new ActionColumnCommand() {
                                                                                           public void execute( final ActionCol52 column ) {
                                                                                               presenter.appendColumn( column );
                                                                                           }
                                                                                       },
                                                                                       column,
                                                                                       true,
                                                                                       !access.isEditable() );
        popup.show();
    }

    @Override
    public void newExtendedEntryActionBRLFragment() {
        final BRLActionColumn column = new BRLActionColumn();
        final BRLActionColumnViewImpl popup = new BRLActionColumnViewImpl( model,
                                                                           oracle,
                                                                           presenter,
                                                                           eventBus,
                                                                           column,
                                                                           true,
                                                                           !access.isEditable() );
        popup.show();
    }

    @Override
    public void newLimitedEntryActionBRLFragment() {
        final LimitedEntryBRLActionColumn column = new LimitedEntryBRLActionColumn();
        final LimitedEntryBRLActionColumnViewImpl popup = new LimitedEntryBRLActionColumnViewImpl( model,
                                                                                                   oracle,
                                                                                                   presenter,
                                                                                                   eventBus,
                                                                                                   column,
                                                                                                   true,
                                                                                                   !access.isEditable() );
        popup.show();
    }

    public void editCondition( final Pattern52 pattern,
                               final ConditionCol52 column ) {
        final ConditionPopup popup = new ConditionPopup( model,
                                                         oracle,
                                                         presenter,
                                                         new ConditionColumnCommand() {
                                                             public void execute( final Pattern52 newPattern,
                                                                                  final ConditionCol52 newColumn ) {
                                                                 if ( !access.isEditable() ) {
                                                                     return;
                                                                 }
                                                                 presenter.updateColumn( pattern,
                                                                                         column,
                                                                                         newPattern,
                                                                                         newColumn );
                                                             }
                                                         },
                                                         pattern,
                                                         column,
                                                         false,
                                                         !access.isEditable() );
        popup.show();
    }

    @Override
    public void editExtendedEntryConditionBRLFragment( final BRLConditionColumn column ) {
        final BRLConditionColumnViewImpl popup = new BRLConditionColumnViewImpl( model,
                                                                                 oracle,
                                                                                 presenter,
                                                                                 eventBus,
                                                                                 column,
                                                                                 false,
                                                                                 !access.isEditable() );
        popup.show();
    }

    @Override
    public void editLimitedEntryConditionBRLFragment( final LimitedEntryBRLConditionColumn column ) {
        final LimitedEntryBRLConditionColumnViewImpl popup = new LimitedEntryBRLConditionColumnViewImpl( model,
                                                                                                         oracle,
                                                                                                         presenter,
                                                                                                         eventBus,
                                                                                                         column,
                                                                                                         false,
                                                                                                         !access.isEditable() );
        popup.show();
    }

    @Override
    public void editActionInsertFact( final ActionInsertFactCol52 column ) {
        final ActionInsertFactPopup ed = new ActionInsertFactPopup( model,
                                                                    oracle,
                                                                    presenter,
                                                                    new ActionColumnCommand() {
                                                                        public void execute( final ActionCol52 newColumn ) {
                                                                            if ( !access.isEditable() ) {
                                                                                return;
                                                                            }
                                                                            presenter.updateColumn( column,
                                                                                                    (ActionInsertFactCol52) newColumn );
                                                                        }
                                                                    },
                                                                    column,
                                                                    false,
                                                                    !access.isEditable() );
        ed.show();
    }

    @Override
    public void editActionSetField( final ActionSetFieldCol52 column ) {
        final ActionSetFieldPopup ed = new ActionSetFieldPopup( model,
                                                                oracle,
                                                                presenter,
                                                                new ActionColumnCommand() {
                                                                    public void execute( final ActionCol52 newColumn ) {
                                                                        if ( !access.isEditable() ) {
                                                                            return;
                                                                        }
                                                                        presenter.updateColumn( column,
                                                                                                (ActionSetFieldCol52) newColumn );
                                                                    }
                                                                },
                                                                column,
                                                                false,
                                                                !access.isEditable() );
        ed.show();
    }

    @Override
    public void editActionRetractFact( final ActionRetractFactCol52 column ) {
        final ActionRetractFactPopup ed = new ActionRetractFactPopup( model,
                                                                      presenter,
                                                                      new ActionColumnCommand() {
                                                                          public void execute( final ActionCol52 newColumn ) {
                                                                              if ( !access.isEditable() ) {
                                                                                  return;
                                                                              }
                                                                              presenter.updateColumn( column,
                                                                                                      (ActionRetractFactCol52) newColumn );
                                                                          }
                                                                      },
                                                                      column,
                                                                      false,
                                                                      !access.isEditable() );
        ed.show();
    }

    @Override
    public void editActionWorkItemInsertFact( final ActionWorkItemInsertFactCol52 column ) {
        final ActionWorkItemInsertFactPopup ed = new ActionWorkItemInsertFactPopup( model,
                                                                                    oracle,
                                                                                    presenter,
                                                                                    new ActionColumnCommand() {
                                                                                        public void execute( final ActionCol52 newColumn ) {
                                                                                            if ( !access.isEditable() ) {
                                                                                                return;
                                                                                            }
                                                                                            presenter.updateColumn( column,
                                                                                                                    (ActionWorkItemInsertFactCol52) newColumn );
                                                                                        }
                                                                                    },
                                                                                    column,
                                                                                    false,
                                                                                    !access.isEditable() );
        ed.show();
    }

    @Override
    public void editActionWorkItemSetField( final ActionWorkItemSetFieldCol52 column ) {
        final ActionWorkItemSetFieldPopup ed = new ActionWorkItemSetFieldPopup( model,
                                                                                oracle,
                                                                                presenter,
                                                                                new ActionColumnCommand() {
                                                                                    public void execute( final ActionCol52 newColumn ) {
                                                                                        if ( !access.isEditable() ) {
                                                                                            return;
                                                                                        }
                                                                                        presenter.updateColumn( column,
                                                                                                                (ActionWorkItemSetFieldCol52) newColumn );
                                                                                    }
                                                                                },
                                                                                column,
                                                                                false,
                                                                                !access.isEditable() );
        ed.show();
    }

    @Override
    public void editActionWorkItem( final ActionWorkItemCol52 column ) {
        final ActionWorkItemPopup popup = new ActionWorkItemPopup( model,
                                                                   presenter,
                                                                   new ActionColumnCommand() {
                                                                       public void execute( final ActionCol52 newColumn ) {
                                                                           if ( !access.isEditable() ) {
                                                                               return;
                                                                           }
                                                                           presenter.updateColumn( column,
                                                                                                   (ActionWorkItemCol52) newColumn );
                                                                       }
                                                                   },
                                                                   column,
                                                                   workItemDefinitions,
                                                                   false,
                                                                   !access.isEditable() );
        popup.show();
    }

    @Override
    public void editExtendedEntryActionBRLFragment( final BRLActionColumn column ) {
        final BRLActionColumnViewImpl popup = new BRLActionColumnViewImpl( model,
                                                                           oracle,
                                                                           presenter,
                                                                           eventBus,
                                                                           column,
                                                                           false,
                                                                           !access.isEditable() );
        popup.show();
    }

    @Override
    public void editLimitedEntryActionBRLFragment( final LimitedEntryBRLActionColumn column ) {
        final LimitedEntryBRLActionColumnViewImpl popup = new LimitedEntryBRLActionColumnViewImpl( model,
                                                                                                   oracle,
                                                                                                   presenter,
                                                                                                   eventBus,
                                                                                                   column,
                                                                                                   false,
                                                                                                   !access.isEditable() );
        popup.show();
    }

    @Override
    public void showDataCutNotificationEvent() {
        notificationEvent.fire( new NotificationEvent( GuidedDecisionTableConstants.INSTANCE.DataCutToClipboardMessage() ) );
    }

    @Override
    public void showDataCopiedNotificationEvent() {
        notificationEvent.fire( new NotificationEvent( GuidedDecisionTableConstants.INSTANCE.DataCopiedToClipboardMessage() ) );
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}
