package org.kie.uberfire.apps.client.home;

import java.util.List;
import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Breadcrumbs;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.uberfire.apps.api.Directory;
import org.kie.uberfire.apps.api.DirectoryBreadCrumb;
import org.kie.uberfire.apps.client.home.components.ThumbnailApp;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class AppsHomeView extends Composite implements AppsHomePresenter.View {

    private AppsHomePresenter presenter;

    @UiField
    FlowPanel mainPanel;

    @UiField
    Breadcrumbs dirs;

    @UiField
    FlowPanel dirContent;

    interface AppsHomeViewBinder
            extends
            UiBinder<Widget, AppsHomeView> {

    }

    private static AppsHomeViewBinder uiBinder = GWT.create( AppsHomeViewBinder.class );

    @AfterInitialization
    public void initialize() {
        initWidget( uiBinder.createAndBindUi( this ) );
        configBreadCrumbs();
    }

    private void configBreadCrumbs() {
        dirs.setDivider( "/" );
    }

    @Override
    public void init( final AppsHomePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupBreadCrumbs( List<DirectoryBreadCrumb> breadCrumbs,
                                  final ParameterizedCommand<String> breadCrumbAction ) {
        dirs.clear();
        for ( final DirectoryBreadCrumb breadCrumb : breadCrumbs ) {
            final NavLink bread = new NavLink( breadCrumb.getName() );
            bread.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    breadCrumbAction.execute(  breadCrumb.getUri() );
                }
            } );
            dirs.add( bread );
        }
    }

    @Override
    public void setupAddDir( ParameterizedCommand<String> command ) {
        generateCreateDirThumbNail( command );
    }

    @Override
    public void setupChildsDirectories( List<Directory> childsDirectories,
                                        ParameterizedCommand<String> clickCommand ) {
        for ( Directory childsDirectory : childsDirectories ) {
            final ThumbnailApp link = new ThumbnailApp( childsDirectory.getName(), childsDirectory.getURI(), ThumbnailApp.TYPE.DIR, clickCommand );
            dirContent.add( link );
        }
    }

    @Override
    public void clear() {
        dirContent.clear();
    }

    @Override
    public void setupChildComponents( List<String> childComponents,
                                      ParameterizedCommand<String> clickCommand ) {
        for ( String childComponent : childComponents ) {
            final ThumbnailApp link = new ThumbnailApp( childComponent, ThumbnailApp.TYPE.COMPONENT, clickCommand );
            dirContent.add( link );
        }

    }

    private void generateCreateDirThumbNail( ParameterizedCommand<String> clickCommand ) {
        final ThumbnailApp link = new ThumbnailApp( ThumbnailApp.TYPE.ADD, clickCommand );
        dirContent.add( link );
    }

}
