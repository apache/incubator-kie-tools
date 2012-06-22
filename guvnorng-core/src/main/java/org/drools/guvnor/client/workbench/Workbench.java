package org.drools.guvnor.client.workbench;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.workbench.annotations.DefaultPerspective;
import org.drools.guvnor.client.workbench.menu.GuvnorMenu;
import org.drools.guvnor.client.workbench.perspectives.IPerspectiveProvider;
import org.drools.guvnor.client.workbench.widgets.dnd.CompassDropController;
import org.drools.guvnor.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.drools.guvnor.client.workbench.widgets.dnd.WorkbenchPickupDragController;
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

@ApplicationScoped
public class Workbench extends Composite {

    public static final int               WIDTH     = Window.getClientWidth();

    public static final int               HEIGHT    = Window.getClientHeight();

    private final VerticalPanel           container = new VerticalPanel();

    private final SimplePanel             workbench = new SimplePanel();

    @Inject
    private PanelManager                  panelManager;

    @Inject
    private IOCBeanManager                iocManager;

    @Inject
    private WorkbenchDragAndDropManager   dndManager;

    @Inject
    private WorkbenchPickupDragController dragController;

    @Inject
    private BeanFactory                   factory;

    @PostConstruct
    public void setup() {

        //Menubar -> Spoof for now, would probably be a banner or something
        HorizontalPanel menubar = new HorizontalPanel();
        menubar.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
        menubar.getElement().getStyle().setBackgroundColor( "#c0c0c0" );
        menubar.getElement().getStyle().setHeight( 48.0,
                                                   Unit.PX );
        menubar.setWidth( WIDTH + "px" );

        final GuvnorMenu menu = iocManager.lookupBean( GuvnorMenu.class ).getInstance();
        menubar.add( menu );
        container.add( menubar );

        //Container panels for workbench
        final AbsolutePanel workbenchContainer = dragController.getBoundaryPanel();
        workbenchContainer.setPixelSize( WIDTH,
                                         HEIGHT - 48 );
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
        dndManager.unregisterDropControllers();

        //Add default workbench widget
        final WorkbenchPanel workbenchRootPanel = factory.newWorkbenchPanel();
        workbench.setWidget( workbenchRootPanel );
        panelManager.setRoot( workbenchRootPanel );
        panelManager.setFocus( workbenchRootPanel );

        //Wire-up DnD controller
        final CompassDropController workbenchDropController = factory.newDropController( workbenchRootPanel );
        dndManager.registerDropController( workbench,
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
