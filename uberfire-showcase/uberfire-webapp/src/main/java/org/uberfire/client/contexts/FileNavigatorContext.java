package org.uberfire.client.contexts;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.lifecycle.OnContextAttach;
import org.uberfire.client.annotations.WorkbenchContext;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.navigator.FileNavigator;
import org.uberfire.client.navigator.NavigatorOptions;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.PanelDefinition;

@Dependent
@WorkbenchContext(identifier = "fileNavContext")
public class FileNavigatorContext extends Composite {

    @Inject
    private PlaceManager placeManager;

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

    @OnContextAttach
    public void onAttach( final PanelDefinition panel ) {
        this.panel = panel;
        fileNavigator.setFileActionCommand( new ParameterizedCommand<Path>() {
            @Override
            public void execute( final Path path ) {
                placeManager.goTo( path, panel );
            }
        } );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "My Custom Context";
    }

}