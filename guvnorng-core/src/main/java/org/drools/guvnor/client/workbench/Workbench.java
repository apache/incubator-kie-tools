package org.drools.guvnor.client.workbench;

import org.drools.guvnor.client.workbench.PositionSelectorPopup.Position;
import org.drools.guvnor.client.workbench.events.AddWorkbenchPanelEvent;
import org.drools.guvnor.client.workbench.events.AddWorkbenchPanelEvent.AddWorkbenchPanelEventHandler;
import org.drools.guvnor.client.workbench.widgets.dnd.CompassDropController;
import org.drools.guvnor.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.drools.guvnor.client.workbench.widgets.panels.PanelHelper;
import org.drools.guvnor.client.workbench.widgets.panels.PanelHelperEast;
import org.drools.guvnor.client.workbench.widgets.panels.PanelHelperNorth;
import org.drools.guvnor.client.workbench.widgets.panels.PanelHelperSelf;
import org.drools.guvnor.client.workbench.widgets.panels.PanelHelperSouth;
import org.drools.guvnor.client.workbench.widgets.panels.PanelHelperWest;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Workbench extends Composite
    implements
    AddWorkbenchPanelEventHandler {

    public static final int     WIDTH              = Window.getClientWidth();

    public static final int     HEIGHT             = Window.getClientHeight();

    private final EventBus      eventBus           = new SimpleEventBus();

    private final VerticalPanel container          = new VerticalPanel();

    private final SimplePanel   workbench          = new SimplePanel();

    private final AbsolutePanel workbenchContainer = new AbsolutePanel();

    private final PanelHelper   helperNorth        = new PanelHelperNorth( eventBus );
    private final PanelHelper   helperSouth        = new PanelHelperSouth( eventBus );
    private final PanelHelper   helperEast         = new PanelHelperEast( eventBus );
    private final PanelHelper   helperWest         = new PanelHelperWest( eventBus );
    private final PanelHelper   helperSelf         = new PanelHelperSelf( eventBus );

    public Workbench() {

        //Menubar -> Spoof for now, would probably be a banner or something
        HorizontalPanel menubar = new HorizontalPanel();
        menubar.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
        menubar.getElement().getStyle().setBackgroundColor( "#c0c0c0" );
        menubar.getElement().getStyle().setHeight( 48.0,
                                                   Unit.PX );
        menubar.setWidth( WIDTH + "px" );
        menubar.add( makeAddWindowButton() );
        menubar.add( makeBootstrapButton() );
        container.add( menubar );

        //Container panels for workbench
        workbenchContainer.setPixelSize( WIDTH,
                                         HEIGHT - 48 );
        WorkbenchDragAndDropManager.getInstance().init( workbenchContainer );
        workbench.setPixelSize( WIDTH,
                                HEIGHT - 48 );
        workbenchContainer.add( workbench );
        container.add( workbenchContainer );

        //Wire-up events
        eventBus.addHandler( AddWorkbenchPanelEvent.TYPE,
                             this );

        initWidget( container );

        //Schedule creation of the default perspective
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                //We need to defer execution until the browser has completed initial layout
                final Timer t = new Timer() {
                    public void run() {
                        bootstrap();
                    }
                };
                t.schedule( 250 );
            }

        } );
    }

    private Widget makeAddWindowButton() {
        final Button addWidgetButton = new Button( "Add" );
        final PositionSelectorPopup popup = new PositionSelectorPopup( eventBus );
        popup.addAutoHidePartner( addWidgetButton.getElement() );
        popup.setAutoHideEnabled( true );

        addWidgetButton.addClickHandler( new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                popup.showRelativeTo( addWidgetButton );
            }

        } );
        return addWidgetButton;
    }

    private Widget makeBootstrapButton() {
        final Button bootstrapButton = new Button( "Bootstrap" );

        bootstrapButton.addClickHandler( new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                bootstrap();
            }

        } );
        return bootstrapButton;
    }

    private void bootstrap() {
        workbench.clear();
        WorkbenchDragAndDropManager.getInstance().unregisterDropControllers();

        //Add default workbench widget
        //TODO {manstis} You know, I don't really like this, but it works for now.
        final WorkbenchPanel workbenchRootPanel = new WorkbenchPanel( eventBus,
                                                                      new Label( "root" ),
                                                                      "root" );
        workbench.setWidget( workbenchRootPanel );

        //Wire-up DnD controller
        final CompassDropController workbenchDropController = new CompassDropController( workbenchRootPanel,
                                                                                         eventBus );
        WorkbenchDragAndDropManager.getInstance().registerDropController( workbench,
                                                                          workbenchDropController );

        //TODO {manstis} This needs to add the applicable Widgets for the Perspective
        addWorkbenchPanel( "p1",
                           workbenchRootPanel,
                           Position.NORTH,
                           new Label( "p1" ) );
        addWorkbenchPanel( "p2",
                           workbenchRootPanel,
                           Position.WEST,
                           new Label( "p2" ) );

        //Set focus to root panel
        workbenchRootPanel.setFocus( true );
    }

    @Override
    public void onAddWorkbenchPanel(AddWorkbenchPanelEvent event) {

        final String title = event.getTitle();
        final WorkbenchPanel target = event.getTarget();
        final Position position = event.getPosition();
        final Widget widget = event.getWidget();

        addWorkbenchPanel( title,
                           target,
                           position,
                           widget );
    }

    private void addWorkbenchPanel(final String title,
                                   final WorkbenchPanel target,
                                   final Position position,
                                   final Widget widget) {
        switch ( position ) {
            case NORTH :
                helperNorth.add( title,
                                 target,
                                 widget );
                break;

            case SOUTH :
                helperSouth.add( title,
                                 target,
                                 widget );
                break;

            case EAST :
                helperEast.add( title,
                                target,
                                widget );
                break;

            case WEST :
                helperWest.add( title,
                                target,
                                widget );
                break;

            case SELF :
                helperSelf.add( title,
                                target,
                                widget );
                break;
        }
    }

}
