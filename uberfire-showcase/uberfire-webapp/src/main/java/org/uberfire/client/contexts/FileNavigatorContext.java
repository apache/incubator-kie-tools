package org.uberfire.client.contexts;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.OnContextAttach;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchContext;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.navigator.FileNavigator;
import org.uberfire.client.navigator.NavigatorOptions;
import org.uberfire.workbench.events.ContextUpdateEvent;
import org.uberfire.workbench.model.PanelDefinition;

@Dependent
@WorkbenchContext(identifier = "fileNavContext")
public class FileNavigatorContext extends Composite {

    @Inject
    private Caller<VFSService> vfsServices;

    @Inject
    private FileNavigator fileNavigator;

    @Inject
    private org.uberfire.client.workbench.context.WorkbenchContext context;

    @UiField
    SimplePanel container;

    private PanelDefinition panel;

    interface ViewBinder
            extends
            UiBinder<SimplePanel, FileNavigatorContext> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        container.add( fileNavigator );
        fileNavigator.setWidth( 300 + "px" );
        fileNavigator.setOptions( new NavigatorOptions() {
            @Override
            public boolean showFiles() {
                return true;
            }

            @Override
            public boolean showDirectories() {
                return true;
            }

            @Override
            public boolean listRepositories() {
                return true;
            }

            @Override
            public boolean allowUpLink() {
                return true;
            }

            @Override
            public boolean showBreadcrumb() {
                return false;
            }

            @Override
            public boolean breadcrumbWithLink() {
                return false;
            }

            @Override
            public boolean allowAddIconOnBreadcrumb() {
                return false;
            }

            @Override
            public boolean showItemAge() {
                return false;
            }

            @Override
            public boolean showItemMessage() {
                return false;
            }

            @Override
            public boolean showItemLastUpdater() {
                return false;
            }
        } );
        fileNavigator.loadContent( null );
    }

    //    public void onContextUpdateEvent( @Observes final ContextUpdateEvent event ) {
//        if ( event.getPanel().equals( panel ) && event.getData().containsKey( "path" ) ) {
//            container.clear();
//            container.add( fileNavigator );
//            fileNavigator.loadContent( (Path) event.getData().get( "path" ) );
//        }
//    }
//
    @OnContextAttach
    public void onAttach( final PanelDefinition panel ) {
        this.panel = panel;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "My Custom Context";
    }

}