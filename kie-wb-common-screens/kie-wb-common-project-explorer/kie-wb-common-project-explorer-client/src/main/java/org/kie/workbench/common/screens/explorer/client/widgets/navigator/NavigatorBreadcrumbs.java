package org.kie.workbench.common.screens.explorer.client.widgets.navigator;

import java.util.List;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.base.InlineLabel;
import com.github.gwtbootstrap.client.ui.base.ListItem;
import com.github.gwtbootstrap.client.ui.constants.Constants;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import org.guvnor.structure.client.resources.NavigatorResources;
import org.kie.workbench.common.screens.explorer.client.widgets.dropdown.CustomDropdown;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.uberfire.ext.widgets.common.client.common.UberBreadcrumbs;
import org.uberfire.mvp.ParameterizedCommand;

public class NavigatorBreadcrumbs extends Composite {

    public static enum Mode {
        REGULAR, HEADER, SECOND_LEVEL
    }

    private final UberBreadcrumbs breadcrumbs = new UberBreadcrumbs();

    public NavigatorBreadcrumbs() {
        this( Mode.REGULAR );
    }

    public NavigatorBreadcrumbs( final Mode mode ) {
        initWidget( breadcrumbs );
        breadcrumbs.getElement().getStyle().setProperty( "whiteSpace", "nowrap" );
        if ( mode != null ) {
            switch ( mode ) {
                case HEADER:
                    breadcrumbs.removeStyleName( Constants.BREADCRUMB );
                    breadcrumbs.setStyleName( NavigatorResources.INSTANCE.css().breadcrumb() );
                    break;
                case SECOND_LEVEL:
                    breadcrumbs.addStyleName( NavigatorResources.INSTANCE.css().breadcrumb2ndLevel() );
                    break;
            }
        }
    }

    public void build( final List<FolderItem> segments,
                       final FolderItem file,
                       final ParameterizedCommand<FolderItem> onPathClick,
                       final CustomDropdown... headers ) {

        build( headers );

        if ( segments != null ) {
            for ( final FolderItem activeItem : segments ) {
                breadcrumbs.add( new NavLink( activeItem.getFileName() ) {{
                    setStyleName( NavigatorResources.INSTANCE.css().directory() );
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            onPathClick.execute( activeItem );
                        }
                    } );
                }} );
            }
            if ( file != null ) {
                breadcrumbs.add( new ListItem( new InlineLabel( file.getFileName() ) ) {{
                    setStyleName( NavigatorResources.INSTANCE.css().directory() );
                }} );
            }
        }
    }

    public void build( final CustomDropdown... headers ) {
        breadcrumbs.clear();

        for ( int i = 0; i < headers.length; i++ ) {
            final CustomDropdown header = headers[ i ];
            header.addStyleName( NavigatorResources.INSTANCE.css().breadcrumbHeader() );
            if ( i + 1 == headers.length ) {
                header.setRightDropdown( true );
            }
            breadcrumbs.add( header );
        }
    }
}

