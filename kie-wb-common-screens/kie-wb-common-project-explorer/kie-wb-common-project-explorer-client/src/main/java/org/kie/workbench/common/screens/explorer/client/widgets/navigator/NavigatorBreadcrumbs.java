/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.explorer.client.widgets.navigator;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Breadcrumbs;
import org.gwtbootstrap3.client.ui.ListItem;
import org.kie.workbench.common.screens.explorer.client.resources.ProjectExplorerResources;
import org.kie.workbench.common.screens.explorer.client.widgets.dropdown.CustomDropdown;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.uberfire.mvp.ParameterizedCommand;

public class NavigatorBreadcrumbs extends Composite {

    public enum Mode {
        REGULAR, HEADER, SECOND_LEVEL
    }

    private final Breadcrumbs breadcrumbs = new Breadcrumbs();

    public NavigatorBreadcrumbs() {
        this( Mode.REGULAR );
    }

    public NavigatorBreadcrumbs( final Mode mode ) {
        initWidget( breadcrumbs );
        breadcrumbs.getElement().getStyle().setProperty( "whiteSpace", "nowrap" );
        breadcrumbs.getElement().getStyle().setMarginBottom( 0, Style.Unit.PX );
    }

    public void build( final List<FolderItem> segments,
                       final FolderItem file,
                       final ParameterizedCommand<FolderItem> onPathClick,
                       final CustomDropdown... headers ) {

        build( headers );

        if ( segments != null ) {
            for ( final FolderItem activeItem : segments ) {
                breadcrumbs.add( new AnchorListItem( activeItem.getFileName() ) {{
                    setStyleName( ProjectExplorerResources.INSTANCE.CSS().directory() );
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            onPathClick.execute( activeItem );
                        }
                    } );
                }} );
            }
            if ( file != null ) {
                breadcrumbs.add( new ListItem() {{
                    add( new InlineLabel( file.getFileName() ) );
                    setStyleName( ProjectExplorerResources.INSTANCE.CSS().directory() );
                }} );
            }
        }
    }

    public void build( final CustomDropdown... headers ) {
        breadcrumbs.clear();

        for ( final CustomDropdown header : headers ) {
            header.addStyleName( ProjectExplorerResources.INSTANCE.CSS().breadcrumbHeader() );
            breadcrumbs.add( new ListItem() {{
                add( header );
            }} );
        }
    }

    @Override
    public void setVisible( boolean visible ) {
        breadcrumbs.setVisible( visible );
    }
}

