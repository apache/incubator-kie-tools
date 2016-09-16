package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;

import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.resources.images.GuidedDecisionTableImageResources508;
import org.drools.workbench.screens.guided.dtable.client.widget.DefaultValueWidgetFactory;
import org.drools.workbench.screens.guided.rule.client.editor.RuleAttributeWidget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.widgets.client.ruleselector.RuleSelector;
import org.uberfire.ext.widgets.common.client.common.DecoratedDisclosurePanel;
import org.uberfire.ext.widgets.common.client.common.ImageButton;
import org.uberfire.ext.widgets.common.client.common.PrettyFormLayout;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;
import org.uberfire.mvp.ParameterizedCommand;

public class GuidedDecisionTableModellerViewImpl extends Composite implements GuidedDecisionTableModellerView {

    private static final double VP_SCALE = 1.0;

    interface GuidedDecisionTableModellerViewImplUiBinder extends UiBinder<Widget, GuidedDecisionTableModellerViewImpl> {

    }

    private static GuidedDecisionTableModellerViewImplUiBinder uiBinder = GWT.create( GuidedDecisionTableModellerViewImplUiBinder.class );

    private static String SECTION_SEPARATOR = "..................";

    private enum NewColumnTypes {
        METADATA_ATTRIBUTE,
        CONDITION_SIMPLE,
        CONDITION_BRL_FRAGMENT,
        ACTION_UPDATE_FACT_FIELD,
        ACTION_INSERT_FACT_FIELD,
        ACTION_RETRACT_FACT,
        ACTION_WORKITEM,
        ACTION_WORKITEM_UPDATE_FACT_FIELD,
        ACTION_WORKITEM_INSERT_FACT_FIELD,
        ACTION_BRL_FRAGMENT
    }

    private TransformMediator defaultTransformMediator;
    private GuidedDecisionTableModellerView.Presenter presenter;

    private final DefaultGridLayer gridLayer = new DefaultGridLayer() {
        @Override
        public void enterPinnedMode( final GridWidget gridWidget,
                                     final Command onStartCommand ) {
            super.enterPinnedMode( gridWidget,
                                   new Command() {
                                       @Override
                                       public void execute() {
                                           onStartCommand.execute();
                                           presenter.onViewPinned( true );
                                       }
                                   } );
        }

        @Override
        public void exitPinnedMode( final Command onCompleteCommand ) {
            super.exitPinnedMode( new Command() {
                @Override
                public void execute() {
                    onCompleteCommand.execute();
                    presenter.onViewPinned( false );
                }
            } );
        }

        @Override
        public TransformMediator getDefaultTransformMediator() {
            return defaultTransformMediator;
        }

    };

    private final RestrictedMousePanMediator mousePanMediator = new RestrictedMousePanMediator( gridLayer ) {
        @Override
        protected void onMouseMove( final NodeMouseMoveEvent event ) {
            super.onMouseMove( event );
            presenter.updateRadar();
        }
    };

    @UiField
    VerticalPanel configuration;

    @UiField(provided = true)
    GridLienzoPanel gridPanel = new GridLienzoPanel() {

        @Override
        public void onResize() {
            Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    final int width = getParent().getOffsetWidth();
                    final int height = getParent().getOffsetHeight();
                    if ( ( width != 0 ) && ( height != 0 ) ) {
                        domElementContainer.setPixelSize( width,
                                                          height );
                        lienzoPanel.setPixelSize( width,
                                                  height );
                    }

                    final TransformMediator restriction = mousePanMediator.getTransformMediator();
                    final Transform transform = restriction.adjust( gridLayer.getViewport().getTransform(),
                                                                    gridLayer.getVisibleBounds() );
                    gridLayer.getViewport().setTransform( transform );
                    gridLayer.draw();
                }
            } );

        }
    };

    private Button addButton = new Button() {{
        setIcon( IconType.PLUS_SQUARE );
        setText( GuidedDecisionTableConstants.INSTANCE.NewColumn() );
        setTitle( GuidedDecisionTableConstants.INSTANCE.AddNewColumn() );
        setEnabled( false );
    }};
    private VerticalPanel config = new VerticalPanel();

    private DecoratedDisclosurePanel disclosurePanelConditions;
    private DecoratedDisclosurePanel disclosurePanelActions;
    private DecoratedDisclosurePanel disclosurePanelAttributes;
    private DecoratedDisclosurePanel disclosurePanelMetaData;
    private PrettyFormLayout configureColumnsNote;
    private VerticalPanel attributeConfigWidget;
    private VerticalPanel metaDataConfigWidget;
    private VerticalPanel conditionsConfigWidget;
    private VerticalPanel actionsConfigWidget;

    private final RuleSelector ruleSelector = new RuleSelector();
    private final GuidedDecisionTableModellerBoundsHelper boundsHelper = new GuidedDecisionTableModellerBoundsHelper();

    public GuidedDecisionTableModellerViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final GuidedDecisionTableModellerView.Presenter presenter ) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void setup() {
        configureColumnsNote = new PrettyFormLayout();
        configureColumnsNote.startSection();
        configureColumnsNote.addRow( new HTML( AbstractImagePrototype.create( GuidedDecisionTableResources.INSTANCE.images().information() ).getHTML()
                                                       + "&nbsp;"
                                                       + GuidedDecisionTableConstants.INSTANCE.ConfigureColumnsNote() ) );
        configureColumnsNote.endSection();

        final DecoratedDisclosurePanel disclosurePanelContainer = new DecoratedDisclosurePanel( GuidedDecisionTableConstants.INSTANCE.DecisionTable() );
        disclosurePanelContainer.setTitle( GuidedDecisionTableConstants.INSTANCE.DecisionTable() );
        disclosurePanelContainer.setWidth( "100%" );

        config.setWidth( "100%" );
        disclosurePanelContainer.add( config );

        config.add( newColumn() );

        disclosurePanelMetaData = setupDisclosurePanel( GuidedDecisionTableConstants.INSTANCE.MetadataColumns(),
                                                        wrapDisclosurePanelContent( getMetaDataWidget() ) );
        disclosurePanelAttributes = setupDisclosurePanel( GuidedDecisionTableConstants.INSTANCE.AttributeColumns(),
                                                          wrapDisclosurePanelContent( getAttributesWidget() ) );
        disclosurePanelConditions = setupDisclosurePanel( GuidedDecisionTableConstants.INSTANCE.ConditionColumns(),
                                                          wrapDisclosurePanelContent( getConditionsWidget() ) );
        disclosurePanelActions = setupDisclosurePanel( GuidedDecisionTableConstants.INSTANCE.ActionColumns(),
                                                       wrapDisclosurePanelContent( getActionsWidget() ) );

        config.add( disclosurePanelMetaData );
        config.add( disclosurePanelAttributes );
        config.add( disclosurePanelConditions );
        config.add( disclosurePanelActions );

        configuration.add( disclosurePanelContainer );
        configuration.add( configureColumnsNote );
        configuration.add( getRuleInheritanceWidget() );

        //Lienzo stuff - Set default scale
        final Transform transform = new Transform().scale( VP_SCALE );
        gridPanel.getViewport().setTransform( transform );

        //Lienzo stuff - Add mouse pan support
        defaultTransformMediator = new BoundaryTransformMediator( GuidedDecisionTableModellerViewImpl.this );
        mousePanMediator.setTransformMediator( defaultTransformMediator );
        gridPanel.getViewport().getMediators().push( mousePanMediator );
        mousePanMediator.setBatchDraw( true );

        //Wire-up widgets
        gridPanel.add( gridLayer );
    }

    private DecoratedDisclosurePanel setupDisclosurePanel( final String title,
                                                           final Widget content ) {
        final DecoratedDisclosurePanel panel = new DecoratedDisclosurePanel( title );
        panel.setWidth( "75%" );
        panel.setOpen( false );
        panel.add( content );
        return panel;
    }

    @Override
    public void onResize() {
        gridPanel.onResize();
        presenter.updateRadar();
    }

    @Override
    public HandlerRegistration addKeyDownHandler( final KeyDownHandler handler ) {
        return gridPanel.addKeyDownHandler( handler );
    }

    @Override
    public HandlerRegistration addContextMenuHandler( final ContextMenuHandler handler ) {
        return gridPanel.addDomHandler( handler,
                                        ContextMenuEvent.getType() );
    }

    @Override
    public HandlerRegistration addMouseDownHandler( final MouseDownHandler handler ) {
        return gridPanel.addMouseDownHandler( handler );
    }

    private Widget getRuleInheritanceWidget() {
        final HorizontalPanel result = new HorizontalPanel();
        result.add( new Label( GuidedDecisionTableConstants.INSTANCE.AllTheRulesInherit() ) );
        ruleSelector.addValueChangeHandler( new ValueChangeHandler<String>() {
            @Override
            public void onValueChange( ValueChangeEvent<String> event ) {
                presenter.getActiveDecisionTable().setParentRuleName( event.getValue() );
            }
        } );
        result.add( ruleSelector );
        return result;
    }

    @Override
    public void clear() {
        gridLayer.removeAll();
    }

    @Override
    public void addDecisionTable( final GridWidget gridWidget ) {
        //Ensure the first Decision Table is visible
        if ( gridLayer.getGridWidgets().isEmpty() ) {
            final Point2D translation = getTranslation( gridWidget );
            final Transform t = gridLayer.getViewport().getTransform();
            t.translate( translation.getX(),
                         translation.getY() );
        }
        gridLayer.add( gridWidget );
        gridLayer.batch();
    }

    private Point2D getTranslation( final GridWidget gridWidget ) {
        final Transform t = gridLayer.getViewport().getTransform();
        final double requiredTranslateX = GuidedDecisionTableModellerBoundsHelper.BOUNDS_PADDING - gridWidget.getX();
        final double requiredTranslateY = GuidedDecisionTableModellerBoundsHelper.BOUNDS_PADDING - gridWidget.getY();
        final double actualTranslateX = t.getTranslateX();
        final double actualTranslateY = t.getTranslateY();
        final double dx = requiredTranslateX - actualTranslateX;
        final double dy = requiredTranslateY - actualTranslateY;
        return new Point2D( dx,
                            dy );
    }

    @Override
    public void removeDecisionTable( final GridWidget gridWidget,
                                     final Command afterRemovalCommand ) {
        if ( gridWidget == null ) {
            return;
        }
        final Command remove = () -> {
            gridLayer.remove( gridWidget );
            gridLayer.batch();
            afterRemovalCommand.execute();
        };
        if ( gridLayer.isGridPinned() ) {
            final GridPinnedModeManager.PinnedContext context = gridLayer.getPinnedContext();
            if ( gridWidget.equals( context.getGridWidget() ) ) {
                gridLayer.exitPinnedMode( remove );
            }
        } else {
            remove.execute();
        }
    }

    @Override
    public void setEnableColumnCreation( final boolean enabled ) {
        addButton.setEnabled( enabled );
    }

    private Widget newColumn() {
        addButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                doNewColumn();
            }
        } );

        return addButton;
    }

    private void doNewColumn() {
        final FormStylePopup pop = new FormStylePopup( GuidedDecisionTableConstants.INSTANCE.AddNewColumn() );

        //List of basic column types
        final ListBox choice = new ListBox();
        choice.setVisibleItemCount( NewColumnTypes.values().length );
        choice.setWidth( "100%" );

        choice.addItem( GuidedDecisionTableConstants.INSTANCE.AddNewMetadataOrAttributeColumn(),
                        NewColumnTypes.METADATA_ATTRIBUTE.name() );
        choice.addItem( SECTION_SEPARATOR );
        choice.addItem( GuidedDecisionTableConstants.INSTANCE.AddNewConditionSimpleColumn(),
                        NewColumnTypes.CONDITION_SIMPLE.name() );
        choice.addItem( SECTION_SEPARATOR );
        choice.addItem( GuidedDecisionTableConstants.INSTANCE.SetTheValueOfAField(),
                        NewColumnTypes.ACTION_UPDATE_FACT_FIELD.name() );
        choice.addItem( GuidedDecisionTableConstants.INSTANCE.SetTheValueOfAFieldOnANewFact(),
                        NewColumnTypes.ACTION_INSERT_FACT_FIELD.name() );
        choice.addItem( GuidedDecisionTableConstants.INSTANCE.DeleteAnExistingFact(),
                        NewColumnTypes.ACTION_RETRACT_FACT.name() );

        //Checkbox to include Advanced Action types
        final CheckBox chkIncludeAdvancedOptions = new CheckBox( SafeHtmlUtils.fromString( GuidedDecisionTableConstants.INSTANCE.IncludeAdvancedOptions() ) );
        chkIncludeAdvancedOptions.setValue( false );
        chkIncludeAdvancedOptions.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                if ( chkIncludeAdvancedOptions.getValue() ) {
                    addItem( 3,
                             GuidedDecisionTableConstants.INSTANCE.AddNewConditionBRLFragment(),
                             NewColumnTypes.CONDITION_BRL_FRAGMENT.name() );
                    addItem( GuidedDecisionTableConstants.INSTANCE.WorkItemAction(),
                             NewColumnTypes.ACTION_WORKITEM.name() );
                    addItem( GuidedDecisionTableConstants.INSTANCE.WorkItemActionSetField(),
                             NewColumnTypes.ACTION_WORKITEM_UPDATE_FACT_FIELD.name() );
                    addItem( GuidedDecisionTableConstants.INSTANCE.WorkItemActionInsertFact(),
                             NewColumnTypes.ACTION_WORKITEM_INSERT_FACT_FIELD.name() );
                    addItem( GuidedDecisionTableConstants.INSTANCE.AddNewActionBRLFragment(),
                             NewColumnTypes.ACTION_BRL_FRAGMENT.name() );
                } else {
                    removeItem( NewColumnTypes.CONDITION_BRL_FRAGMENT.name() );
                    removeItem( NewColumnTypes.ACTION_WORKITEM.name() );
                    removeItem( NewColumnTypes.ACTION_WORKITEM_UPDATE_FACT_FIELD.name() );
                    removeItem( NewColumnTypes.ACTION_WORKITEM_INSERT_FACT_FIELD.name() );
                    removeItem( NewColumnTypes.ACTION_BRL_FRAGMENT.name() );
                }
            }

            private void addItem( int index,
                                  String item,
                                  String value ) {
                for ( int itemIndex = 0; itemIndex < choice.getItemCount(); itemIndex++ ) {
                    if ( choice.getValue( itemIndex ).equals( value ) ) {
                        return;
                    }
                }
                choice.insertItem( item,
                                   value,
                                   index );
            }

            private void addItem( String item,
                                  String value ) {
                for ( int itemIndex = 0; itemIndex < choice.getItemCount(); itemIndex++ ) {
                    if ( choice.getValue( itemIndex ).equals( value ) ) {
                        return;
                    }
                }
                choice.addItem( item,
                                value );
            }

            private void removeItem( String value ) {
                for ( int itemIndex = 0; itemIndex < choice.getItemCount(); itemIndex++ ) {
                    if ( choice.getValue( itemIndex ).equals( value ) ) {
                        choice.removeItem( itemIndex );
                        break;
                    }
                }
            }

        } );

        //OK button to create column
        final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( new Command() {
            @Override
            public void execute() {
                String s = choice.getValue( choice.getSelectedIndex() );
                if ( s.equals( NewColumnTypes.METADATA_ATTRIBUTE.name() ) ) {
                    presenter.getActiveDecisionTable().newAttributeOrMetaDataColumn();

                } else if ( s.equals( NewColumnTypes.CONDITION_SIMPLE.name() ) ) {
                    presenter.getActiveDecisionTable().newConditionColumn();

                } else if ( s.equals( NewColumnTypes.CONDITION_BRL_FRAGMENT.name() ) ) {
                    presenter.getActiveDecisionTable().newConditionBRLFragment();

                } else if ( s.equals( NewColumnTypes.ACTION_INSERT_FACT_FIELD.name() ) ) {
                    presenter.getActiveDecisionTable().newActionInsertColumn();

                } else if ( s.equals( NewColumnTypes.ACTION_UPDATE_FACT_FIELD.name() ) ) {
                    presenter.getActiveDecisionTable().newActionSetColumn();

                } else if ( s.equals( NewColumnTypes.ACTION_RETRACT_FACT.name() ) ) {
                    presenter.getActiveDecisionTable().newActionRetractFact();

                } else if ( s.equals( NewColumnTypes.ACTION_WORKITEM.name() ) ) {
                    presenter.getActiveDecisionTable().newActionWorkItem();

                } else if ( s.equals( NewColumnTypes.ACTION_WORKITEM_UPDATE_FACT_FIELD.name() ) ) {
                    presenter.getActiveDecisionTable().newActionWorkItemSetField();

                } else if ( s.equals( NewColumnTypes.ACTION_WORKITEM_INSERT_FACT_FIELD.name() ) ) {
                    presenter.getActiveDecisionTable().newActionWorkItemInsertFact();

                } else if ( s.equals( NewColumnTypes.ACTION_BRL_FRAGMENT.name() ) ) {
                    presenter.getActiveDecisionTable().newActionBRLFragment();

                }
                pop.hide();
            }

        }, new Command() {
            @Override
            public void execute() {
                pop.hide();
            }
        } );

        //If a separator is clicked disable OK button
        choice.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                int itemIndex = choice.getSelectedIndex();
                if ( itemIndex < 0 ) {
                    return;
                }
                footer.enableOkButton( !choice.getValue( itemIndex ).equals( SECTION_SEPARATOR ) );
            }

        } );

        pop.setTitle( GuidedDecisionTableConstants.INSTANCE.AddNewColumn() );
        pop.addAttribute( GuidedDecisionTableConstants.INSTANCE.TypeOfColumn(),
                          choice );
        pop.addAttribute( "",
                          chkIncludeAdvancedOptions );
        pop.add( footer );
        pop.show();
    }

    @Override
    public void refreshRuleInheritance( final String selectedParentRuleName,
                                        final Collection<String> availableParentRuleNames ) {
        ruleSelector.setRuleName( selectedParentRuleName );
        ruleSelector.setRuleNames( availableParentRuleNames );
    }

    private Widget getAttributesWidget() {
        attributeConfigWidget = new VerticalPanel();
        return attributeConfigWidget;
    }

    private Widget wrapDisclosurePanelContent( final Widget content ) {
        final SimplePanel container = new SimplePanel();
        container.getElement().getStyle().setProperty( "maxHeight", "200px" );
        container.getElement().getStyle().setOverflowY( Style.Overflow.SCROLL );
        container.add( content );
        return container;
    }

    @Override
    public void refreshAttributeWidget( final List<AttributeCol52> attributeColumns ) {
        this.attributeConfigWidget.clear();

        if ( attributeColumns == null || attributeColumns.isEmpty() ) {
            disclosurePanelAttributes.setOpen( false );
            return;
        }

        final boolean isEditable = presenter.isActiveDecisionTableEditable();
        for ( AttributeCol52 attributeColumn : attributeColumns ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );

            hp.add( new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) );
            if ( isEditable ) {
                hp.add( removeAttribute( attributeColumn ) );
            }

            final SmallLabel label = makeColumnLabel( attributeColumn );
            hp.add( label );

            final AttributeCol52 originalColumn = attributeColumn;
            final Widget defaultValue = DefaultValueWidgetFactory.getDefaultValueWidget( attributeColumn,
                                                                                         !isEditable,
                                                                                         new DefaultValueWidgetFactory.DefaultValueChangedEventHandler() {
                                                                                             @Override
                                                                                             public void onDefaultValueChanged( DefaultValueWidgetFactory.DefaultValueChangedEvent event ) {
                                                                                                 final AttributeCol52 editedColumn = originalColumn.cloneColumn();
                                                                                                 editedColumn.setDefaultValue( event.getEditedDefaultValue() );
                                                                                                 presenter.getActiveDecisionTable().updateColumn( originalColumn,
                                                                                                                                                  editedColumn );
                                                                                             }
                                                                                         } );

            if ( attributeColumn.getAttribute().equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                hp.add( new HTML( "&nbsp;&nbsp;" ) );
                final CheckBox chkUseRowNumber = new CheckBox( GuidedDecisionTableConstants.INSTANCE.UseRowNumber() );
                chkUseRowNumber.setValue( attributeColumn.isUseRowNumber() );
                chkUseRowNumber.setEnabled( isEditable );
                hp.add( chkUseRowNumber );

                hp.add( new SmallLabel( "(" ) );
                final CheckBox chkReverseOrder = new CheckBox( GuidedDecisionTableConstants.INSTANCE.ReverseOrder() );
                chkReverseOrder.setValue( attributeColumn.isReverseOrder() );
                chkReverseOrder.setEnabled( attributeColumn.isUseRowNumber() && isEditable );

                chkUseRowNumber.addClickHandler( new ClickHandler() {

                    @Override
                    public void onClick( final ClickEvent event ) {
                        final AttributeCol52 editedColumn = originalColumn.cloneColumn();
                        editedColumn.setUseRowNumber( chkUseRowNumber.getValue() );
                        chkReverseOrder.setEnabled( chkUseRowNumber.getValue() );
                        presenter.getActiveDecisionTable().updateColumn( originalColumn,
                                                                         editedColumn );
                    }
                } );

                chkReverseOrder.addClickHandler( new ClickHandler() {

                    @Override
                    public void onClick( final ClickEvent event ) {
                        final AttributeCol52 editedColumn = originalColumn.cloneColumn();
                        editedColumn.setReverseOrder( chkReverseOrder.getValue() );
                        presenter.getActiveDecisionTable().updateColumn( originalColumn,
                                                                         editedColumn );
                    }
                } );
                hp.add( chkReverseOrder );
                hp.add( new SmallLabel( ")" ) );
            }
            hp.add( new HTML( "&nbsp;&nbsp;" ) );
            hp.add( new SmallLabel( new StringBuilder( GuidedDecisionTableConstants.INSTANCE.DefaultValue() ).append( GuidedDecisionTableConstants.COLON ).toString() ) );
            hp.add( defaultValue );

            final CheckBox chkHideColumn = new CheckBox( new StringBuilder( GuidedDecisionTableConstants.INSTANCE.HideThisColumn() ).append( GuidedDecisionTableConstants.COLON ).toString() );
            chkHideColumn.setValue( attributeColumn.isHideColumn() );
            chkHideColumn.addClickHandler( new ClickHandler() {

                @Override
                public void onClick( final ClickEvent event ) {
                    final AttributeCol52 editedColumn = originalColumn.cloneColumn();
                    editedColumn.setHideColumn( chkHideColumn.getValue() );
                    presenter.getActiveDecisionTable().updateColumn( originalColumn,
                                                                     editedColumn );
                }
            } );
            hp.add( new HTML( "&nbsp;&nbsp;" ) );
            hp.add( chkHideColumn );

            attributeConfigWidget.add( hp );
        }
    }

    private SmallLabel makeColumnLabel( final AttributeCol52 attributeColumn ) {
        SmallLabel label = new SmallLabel( attributeColumn.getAttribute() );
        setColumnLabelStyleWhenHidden( label,
                                       attributeColumn.isHideColumn() );
        return label;
    }

    private Widget removeAttribute( final AttributeCol52 at ) {
        Image image = GuidedDecisionTableImageResources508.INSTANCE.DeleteItemSmall();
        image.setAltText( GuidedDecisionTableConstants.INSTANCE.RemoveThisAttribute() );

        return new ImageButton( image,
                                GuidedDecisionTableConstants.INSTANCE.RemoveThisAttribute(),
                                new ClickHandler() {
                                    public void onClick( ClickEvent w ) {
                                        String ms = GuidedDecisionTableConstants.INSTANCE.DeleteActionColumnWarning( at.getAttribute() );
                                        if ( Window.confirm( ms ) ) {
                                            presenter.getActiveDecisionTable().deleteColumn( at );
                                        }
                                    }
                                } );
    }

    private Widget getMetaDataWidget() {
        metaDataConfigWidget = new VerticalPanel();
        return metaDataConfigWidget;
    }

    @Override
    public void refreshMetaDataWidget( final List<MetadataCol52> metaDataColumns ) {
        this.metaDataConfigWidget.clear();

        if ( metaDataColumns == null || metaDataColumns.isEmpty() ) {
            disclosurePanelMetaData.setOpen( false );
            return;
        }

        final boolean isEditable = presenter.isActiveDecisionTableEditable();
        for ( MetadataCol52 metaDataColumn : metaDataColumns ) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
            hp.add( new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) );

            if ( isEditable ) {
                hp.add( removeMetaData( metaDataColumn ) );
            }

            final SmallLabel label = makeColumnLabel( metaDataColumn );
            hp.add( label );

            final MetadataCol52 originalColumn = metaDataColumn;
            final CheckBox chkHideColumn = new CheckBox( new StringBuilder( GuidedDecisionTableConstants.INSTANCE.HideThisColumn() ).append( GuidedDecisionTableConstants.COLON ).toString() );
            chkHideColumn.setValue( metaDataColumn.isHideColumn() );
            chkHideColumn.addClickHandler( new ClickHandler() {

                @Override
                public void onClick( final ClickEvent event ) {
                    final MetadataCol52 editedColumn = originalColumn.cloneColumn();
                    editedColumn.setHideColumn( chkHideColumn.getValue() );
                    presenter.getActiveDecisionTable().updateColumn( originalColumn,
                                                                     editedColumn );
                }
            } );
            hp.add( new HTML( "&nbsp;&nbsp;" ) );
            hp.add( chkHideColumn );

            metaDataConfigWidget.add( hp );
        }
    }

    private SmallLabel makeColumnLabel( final MetadataCol52 metaDataColumn ) {
        SmallLabel label = new SmallLabel( metaDataColumn.getMetadata() );
        setColumnLabelStyleWhenHidden( label,
                                       metaDataColumn.isHideColumn() );
        return label;
    }

    private Widget removeMetaData( final MetadataCol52 md ) {
        Image image = GuidedDecisionTableImageResources508.INSTANCE.DeleteItemSmall();
        image.setAltText( GuidedDecisionTableConstants.INSTANCE.RemoveThisMetadata() );

        return new ImageButton( image,
                                GuidedDecisionTableConstants.INSTANCE.RemoveThisMetadata(),
                                new ClickHandler() {
                                    public void onClick( ClickEvent w ) {
                                        String ms = GuidedDecisionTableConstants.INSTANCE.DeleteActionColumnWarning( md.getMetadata() );
                                        if ( Window.confirm( ms ) ) {
                                            presenter.getActiveDecisionTable().deleteColumn( md );
                                        }
                                    }
                                } );
    }

    private Widget getConditionsWidget() {
        conditionsConfigWidget = new VerticalPanel();
        return conditionsConfigWidget;
    }

    @Override
    public void refreshConditionsWidget( final List<CompositeColumn<? extends BaseColumn>> conditionColumns ) {
        this.conditionsConfigWidget.clear();

        if ( conditionColumns == null || conditionColumns.isEmpty() ) {
            disclosurePanelConditions.setOpen( false );
            return;
        }

        //Each Pattern is a row in a vertical panel
        final VerticalPanel patternsPanel = new VerticalPanel();
        conditionsConfigWidget.add( patternsPanel );

        final boolean isEditable = presenter.isActiveDecisionTableEditable();
        for ( CompositeColumn<?> conditionColumn : conditionColumns ) {
            if ( conditionColumn instanceof Pattern52 ) {
                Pattern52 p = (Pattern52) conditionColumn;
                VerticalPanel patternPanel = new VerticalPanel();
                VerticalPanel conditionsPanel = new VerticalPanel();
                HorizontalPanel patternHeaderPanel = new HorizontalPanel();
                patternHeaderPanel.setStylePrimaryName( GuidedDecisionTableResources.INSTANCE.css().patternSectionHeader() );
                Label patternLabel = makePatternLabel( p );
                patternHeaderPanel.add( patternLabel );
                patternPanel.add( patternHeaderPanel );
                patternPanel.add( conditionsPanel );
                patternsPanel.add( patternPanel );

                List<ConditionCol52> conditions = p.getChildColumns();
                for ( ConditionCol52 c : conditions ) {
                    HorizontalPanel hp = new HorizontalPanel();
                    hp.setStylePrimaryName( GuidedDecisionTableResources.INSTANCE.css().patternConditionSectionHeader() );
                    if ( isEditable ) {
                        hp.add( removeCondition( c ) );
                    }
                    hp.add( editCondition( p,
                                           c ) );
                    SmallLabel conditionLabel = makeColumnLabel( c );
                    hp.add( conditionLabel );
                    conditionsPanel.add( hp );
                }

            } else if ( conditionColumn instanceof BRLConditionColumn ) {
                BRLConditionColumn brl = (BRLConditionColumn) conditionColumn;

                HorizontalPanel patternHeaderPanel = new HorizontalPanel();
                patternHeaderPanel.setStylePrimaryName( GuidedDecisionTableResources.INSTANCE.css().patternSectionHeader() );
                HorizontalPanel patternPanel = new HorizontalPanel();
                if ( isEditable ) {
                    patternPanel.add( removeCondition( brl ) );
                }
                patternPanel.add( editCondition( brl ) );
                Label patternLabel = makePatternLabel( brl );
                patternPanel.add( patternLabel );
                patternHeaderPanel.add( patternPanel );
                patternsPanel.add( patternHeaderPanel );
            }

        }
    }

    private Label makePatternLabel( final Pattern52 p ) {
        StringBuilder patternLabel = new StringBuilder();
        String factType = p.getFactType();
        String boundName = p.getBoundName();
        if ( factType != null && factType.length() > 0 ) {
            if ( p.isNegated() ) {
                patternLabel.append( GuidedDecisionTableConstants.INSTANCE.negatedPattern() ).append( " " ).append( factType );
            } else {
                patternLabel.append( factType ).append( " [" ).append( boundName ).append( "]" );
            }
        }
        return new Label( patternLabel.toString() );
    }

    private Label makePatternLabel( final BRLConditionColumn brl ) {
        StringBuilder sb = new StringBuilder();
        sb.append( brl.getHeader() );
        return new Label( sb.toString() );
    }

    private SmallLabel makeColumnLabel( final ConditionCol52 cc ) {
        StringBuilder sb = new StringBuilder();
        if ( cc.isBound() ) {
            sb.append( cc.getBinding() );
            sb.append( " : " );
        }
        sb.append( cc.getHeader() );
        SmallLabel label = new SmallLabel( sb.toString() );
        if ( cc.isHideColumn() ) {
            label.setStylePrimaryName( GuidedDecisionTableResources.INSTANCE.css().columnLabelHidden() );
        }
        return label;
    }

    private Widget editCondition( final Pattern52 origPattern,
                                  final ConditionCol52 origCol ) {
        return makeEditColumnWidget( GuidedDecisionTableConstants.INSTANCE.EditThisColumnsConfiguration(),
                                     () -> presenter.getActiveDecisionTable().editCondition( origPattern,
                                                                                             origCol ) );
    }

    private Widget editCondition( final BRLConditionColumn origCol ) {
        return makeEditColumnWidget( GuidedDecisionTableConstants.INSTANCE.EditThisColumnsConfiguration(),
                                     () -> presenter.getActiveDecisionTable().editCondition( origCol ) );
    }

    private Widget makeEditColumnWidget( final String caption,
                                         final Command command ) {
        final Image icon = GuidedDecisionTableImageResources508.INSTANCE.Edit();
        icon.setAltText( caption );
        return new ImageButton( icon,
                                caption,
                                ( ClickEvent e ) -> command.execute() );
    }

    private Widget removeCondition( final ConditionCol52 column ) {
        if ( column instanceof BRLConditionColumn ) {
            return makeRemoveConditionWidget( column,
                                              ( command ) -> {
                                                  if ( !presenter.getActiveDecisionTable().canConditionBeDeleted( (BRLConditionColumn) column ) ) {
                                                      Window.alert( GuidedDecisionTableConstants.INSTANCE.UnableToDeleteConditionColumn0( column.getHeader() ) );
                                                      return;
                                                  }
                                                  command.execute();
                                              } );
        }

        return makeRemoveConditionWidget( column,
                                          ( command ) -> {
                                              if ( !presenter.getActiveDecisionTable().canConditionBeDeleted( column ) ) {
                                                  Window.alert( GuidedDecisionTableConstants.INSTANCE.UnableToDeleteConditionColumn0( column.getHeader() ) );
                                                  return;
                                              }
                                              command.execute();
                                          } );
    }

    private Widget makeRemoveConditionWidget( final ConditionCol52 column,
                                              final ParameterizedCommand<Command> command ) {
        Image image = GuidedDecisionTableImageResources508.INSTANCE.DeleteItemSmall();
        image.setAltText( GuidedDecisionTableConstants.INSTANCE.RemoveThisConditionColumn() );
        return new ImageButton( image,
                                GuidedDecisionTableConstants.INSTANCE.RemoveThisConditionColumn(),
                                ( e ) -> command.execute( () -> {
                                    String cm = GuidedDecisionTableConstants.INSTANCE.DeleteConditionColumnWarning0( column.getHeader() );
                                    if ( Window.confirm( cm ) ) {
                                        presenter.getActiveDecisionTable().deleteColumn( column );
                                    }

                                } ) );
    }

    private Widget getActionsWidget() {
        actionsConfigWidget = new VerticalPanel();
        return actionsConfigWidget;
    }

    @Override
    public void refreshActionsWidget( final List<ActionCol52> actionColumns ) {
        this.actionsConfigWidget.clear();

        if ( actionColumns == null || actionColumns.isEmpty() ) {
            disclosurePanelActions.setOpen( false );
            return;
        }

        //Each Action is a row in a vertical panel
        final VerticalPanel actionsPanel = new VerticalPanel();
        this.actionsConfigWidget.add( actionsPanel );

        //Add Actions to panel
        final boolean isEditable = presenter.isActiveDecisionTableEditable();
        for ( ActionCol52 actionColumn : actionColumns ) {
            HorizontalPanel hp = new HorizontalPanel();
            if ( isEditable ) {
                hp.add( removeAction( actionColumn ) );
            }
            hp.add( editAction( actionColumn ) );
            Label actionLabel = makeColumnLabel( actionColumn );
            hp.add( actionLabel );
            actionsPanel.add( hp );
        }
    }

    private SmallLabel makeColumnLabel( final ActionCol52 actionColumn ) {
        SmallLabel label = new SmallLabel( actionColumn.getHeader() );
        if ( actionColumn.isHideColumn() ) {
            label.setStylePrimaryName( GuidedDecisionTableResources.INSTANCE.css().columnLabelHidden() );
        }
        return label;
    }

    private Widget editAction( final ActionCol52 actionColumn ) {
        return makeEditColumnWidget( GuidedDecisionTableConstants.INSTANCE.EditThisActionColumnConfiguration(),
                                     () -> presenter.getActiveDecisionTable().editAction( actionColumn ) );
    }

    private Widget removeAction( final ActionCol52 column ) {
        final Image image = GuidedDecisionTableImageResources508.INSTANCE.DeleteItemSmall();
        image.setAltText( GuidedDecisionTableConstants.INSTANCE.RemoveThisActionColumn() );
        return new ImageButton( image,
                                GuidedDecisionTableConstants.INSTANCE.RemoveThisActionColumn(),
                                ( e ) -> {
                                    String cm = GuidedDecisionTableConstants.INSTANCE.DeleteActionColumnWarning( column.getHeader() );
                                    if ( Window.confirm( cm ) ) {
                                        presenter.getActiveDecisionTable().deleteColumn( column );
                                    }

                                } );
    }

    private void setColumnLabelStyleWhenHidden( final SmallLabel label,
                                                final boolean isHidden ) {
        if ( isHidden ) {
            label.addStyleName( GuidedDecisionTableResources.INSTANCE.css().columnLabelHidden() );
        } else {
            label.removeStyleName( GuidedDecisionTableResources.INSTANCE.css().columnLabelHidden() );
        }
    }

    @Override
    public void refreshColumnsNote( final boolean hasColumnDefinitions ) {
        configureColumnsNote.setVisible( !hasColumnDefinitions );
    }

    @Override
    public void setZoom( final int zoom ) {
        //Set zoom preserving translation
        final Transform transform = new Transform();
        final double tx = gridPanel.getViewport().getTransform().getTranslateX();
        final double ty = gridPanel.getViewport().getTransform().getTranslateY();
        transform.translate( tx, ty );
        transform.scale( zoom / 100.0 );

        //Ensure the change in zoom keeps the view in bounds. IGridLayer's visibleBounds depends
        //on the Viewport Transformation; so set it to the "proposed" transformation before checking.
        gridPanel.getViewport().setTransform( transform );
        final TransformMediator restriction = mousePanMediator.getTransformMediator();
        final Transform newTransform = restriction.adjust( transform,
                                                           gridLayer.getVisibleBounds() );
        gridPanel.getViewport().setTransform( newTransform );
        gridPanel.getViewport().batch();
    }

    @Override
    public void onInsertColumn() {
        doNewColumn();
    }

    @Override
    public GridLayer getGridLayerView() {
        return gridLayer;
    }

    @Override
    public GridLienzoPanel getGridPanel() {
        return gridPanel;
    }

    @Override
    public Bounds getBounds() {
        if ( presenter == null ) {
            return boundsHelper.getBounds( Collections.emptySet() );
        } else {
            return boundsHelper.getBounds( presenter.getAvailableDecisionTables() );
        }
    }

    @Override
    public void select( final GridWidget selectedGridWidget ) {
        gridLayer.select( selectedGridWidget );
    }

    @Override
    public void selectLinkedColumn( final GridColumn<?> link ) {
        gridLayer.selectLinkedColumn( link );
    }

    @Override
    public Set<GridWidget> getGridWidgets() {
        return gridLayer.getGridWidgets();
    }
}
