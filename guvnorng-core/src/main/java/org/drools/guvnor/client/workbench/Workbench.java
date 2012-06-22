package org.drools.guvnor.client.workbench;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.drools.guvnor.client.mvp.PlaceManager;
import org.drools.guvnor.client.workbench.menu.GuvnorMenu;
import org.drools.guvnor.client.workbench.perspectives.DefaultPerspective;
import org.drools.guvnor.client.workbench.perspectives.IPerspectiveProvider;
import org.drools.guvnor.client.workbench.widgets.dnd.CompassDropController;
import org.drools.guvnor.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.drools.guvnor.client.workbench.widgets.panels.PanelManager;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

@Singleton
@ApplicationScoped
public class Workbench extends Composite {

    public static final int     WIDTH              = Window.getClientWidth();

    public static final int     HEIGHT             = Window.getClientHeight();

    private final VerticalPanel container          = new VerticalPanel();

    private final SimplePanel   workbench          = new SimplePanel();

    private final AbsolutePanel workbenchContainer = new AbsolutePanel();

    @Inject
    private PanelManager        panelManager;

    @Inject
    private PlaceManager        placeManager;

    @Inject
    private IOCBeanManager      iocManager;

    @Inject
    private GuvnorMenu          menu;

    public Workbench() {
    }

    @PostConstruct
    public void setup() {

        //Menubar -> Spoof for now, would probably be a banner or something
        HorizontalPanel menubar = new HorizontalPanel();
        menubar.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
        menubar.getElement().getStyle().setBackgroundColor( "#c0c0c0" );
        menubar.getElement().getStyle().setHeight( 48.0,
                                                   Unit.PX );
        menubar.setWidth( WIDTH + "px" );
        menubar.add( menu );
        container.add( menubar );

        //Container panels for workbench
        workbenchContainer.setPixelSize( WIDTH,
                                         HEIGHT - 48 );
        WorkbenchDragAndDropManager.getInstance().init( workbenchContainer );
        workbench.setPixelSize( WIDTH,
                                HEIGHT - 48 );
        workbenchContainer.add( workbench );
        container.add( workbenchContainer );

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
                t.schedule( 500 );
            }

        } );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void bootstrap() {
        workbench.clear();
        WorkbenchDragAndDropManager.getInstance().unregisterDropControllers();

        //Add default workbench widget
        final WorkbenchPanel workbenchRootPanel = new WorkbenchPanel();
        workbench.setWidget( workbenchRootPanel );
        panelManager.setRoot( workbenchRootPanel );
        panelManager.setFocus( workbenchRootPanel );

        //Wire-up DnD controller
        final CompassDropController workbenchDropController = new CompassDropController( workbenchRootPanel );
        WorkbenchDragAndDropManager.getInstance().registerDropController( workbench,
                                                                          workbenchDropController );

        //Lookup PerspectiveProviders and if present launch it to set-up the Workbench
        boolean foundDefaultPerspective = false;
        IPerspectiveProvider defaultPerspective = null;

        Collection<IOCBeanDef> perspectives = iocManager.lookupBeans( IPerspectiveProvider.class );
        Iterator<IOCBeanDef> perspectivesIterator = perspectives.iterator();
        while ( !foundDefaultPerspective && perspectivesIterator.hasNext() ) {
            IOCBeanDef perspective = perspectivesIterator.next();
            Set<Annotation> annotations = perspective.getQualifiers();
            for ( Annotation a : annotations ) {
                if ( a instanceof DefaultPerspective ) {
                    if ( defaultPerspective == null ) {
                        defaultPerspective = (IPerspectiveProvider) perspective.getInstance();
                        foundDefaultPerspective = true;
                        break;
                    }
                }
            }
        }

        //If a default perspective was found load it up!
        if ( foundDefaultPerspective ) {
            defaultPerspective.buildWorkbench( panelManager,
                                               workbenchRootPanel );
        }
    }

}
