/*
 *
 *  * Copyright 2012 JBoss Inc
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.menu;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
import org.uberfire.mvp.Command;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class WorkbenchViewModeSwitcherPresenter implements IsWidget {

    public static final String SWITCH_TO_DEFAULT_VIEW = "Switch to Default View";
    public static final String SWITCH_TO_COMPACT_VIEW = "Switch to Compact View";

    private final WorkbenchMenuBar menubar;

    private Command collapseCommand;
    private Command expandCommand;

    public interface View extends UberView<WorkbenchViewModeSwitcherPresenter> {

        void setText( String text );

        void enable();

        void disable();

        void addClickHandler( Command command );
    }

    private final View view;

    @Inject
    public WorkbenchViewModeSwitcherPresenter( final View view, final WorkbenchMenuBar menubar ) {
        this.view = checkNotNull( "view", view );
        this.menubar = checkNotNull( "menubar", menubar );
        view.init( this );
        view.setText( SWITCH_TO_COMPACT_VIEW );
        view.addClickHandler( new Command() {
            @Override
            public void execute() {
                if( menubar.isExpanded() ){
                    menubar.collapse();
                    if( collapseCommand != null ) {
                        collapseCommand.execute();
                    }
                } else {
                    menubar.expand();
                    if( expandCommand != null ) {
                        expandCommand.execute();
                    }
                }
            }
        } );
        menubar.addCollapseHandler( new Command() {
            @Override
            public void execute() {
                view.setText( SWITCH_TO_DEFAULT_VIEW );
            }
        } );
        menubar.addExpandHandler( new Command() {
            @Override
            public void execute() {
                view.setText( SWITCH_TO_COMPACT_VIEW );
            }
        } );
    }

    public void setCollapseHandler( final Command command ){
        this.collapseCommand = command;
    }

    public void setExpandHandler( final Command command ){
        this.expandCommand = command;
    }

    protected void onPerspectiveChange( @Observes final PerspectiveChange perspectiveChange ) {
        view.enable();
    }

    protected void onPlaceMinimized( @Observes final PlaceMinimizedEvent event ) {
        view.enable();
    }

    protected void onPlaceMaximized( @Observes final PlaceMaximizedEvent event ) {
        view.disable();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

}
