/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.workbench;

import com.google.gwt.event.logical.shared.*;
import org.drools.guvnor.client.resources.GuvnorResources;
import org.drools.guvnor.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPartHideEvent;
import org.drools.guvnor.client.workbench.widgets.panels.PanelManager;
import org.drools.guvnor.client.workbench.widgets.panels.WorkbenchTabLayoutPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;


/**
 *
 */
public class WorkbenchPanel extends ResizeComposite {

    private static final int              TAB_BAR_HEIGHT = 32;

    private final WorkbenchTabLayoutPanel tabPanel;

    public WorkbenchPanel() {
        this.tabPanel = makeTabPanel();
        initWidget( this.tabPanel );
    }

    public WorkbenchPanel(final WorkbenchPart part) {
        this();
        addTab( part );
    }

    public void addTab(final WorkbenchPart part) {
        tabPanel.add( part,
                      makeTabWidget( part ) );
        tabPanel.selectTab( part );
    }

    private WorkbenchTabLayoutPanel makeTabPanel() {
        final WorkbenchTabLayoutPanel tabPanel = new WorkbenchTabLayoutPanel( TAB_BAR_HEIGHT,
                                                                              Unit.PX );

        //Clicking on the TabPanel takes focus
        tabPanel.addDomHandler( new ClickHandler() {

                                    @Override
                                    public void onClick(ClickEvent event) {
                                        PanelManager.getInstance().setFocus( WorkbenchPanel.this );
                                    }

                                },
                                ClickEvent.getType() );

        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {

                int previousTabIndex = tabPanel.getSelectedIndex();
                if (previousTabIndex >= 0) {
                    final Widget widget = tabPanel.getWidget(previousTabIndex);
                    if (widget instanceof WorkbenchPart) {
                        WorkbenchPartHideEvent.fire(
                                (WorkbenchPart) widget,
                                (WorkbenchPart) widget);
                    }
                }
            }
        });
        //When tab is selected ensure content is resized
        tabPanel.addSelectionHandler( new SelectionHandler<Integer>() {

            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                final Widget w = tabPanel.getWidget( event.getSelectedItem() );
                if ( w instanceof RequiresResize ) {
                    scheduleResize( (RequiresResize) w );
                }
                if( w instanceof WorkbenchPart){
                    SelectionEvent.fire(
                            (WorkbenchPart) w,
                            (WorkbenchPart) w);
                }

            }

        } );
        return tabPanel;
    }

    private Widget makeTabWidget(final WorkbenchPart part) {
        final FlowPanel fp = new FlowPanel();
        final InlineLabel tabLabel = new InlineLabel( part.getPartTitle() );
        fp.add( tabLabel );

        WorkbenchDragAndDropManager.getInstance().makeDraggable( part,
                                                                 tabLabel );

        final FocusPanel image = new FocusPanel();
        image.getElement().getStyle().setFloat( Style.Float.RIGHT );

        image.setStyleName( GuvnorResources.INSTANCE.guvnorCss().closeTabImage() );
        image.addClickHandler( new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                CloseEvent.fire( part,
                                 part );
            }

        } );
        fp.add( image );
        return fp;
    }

    private void scheduleResize(final RequiresResize widget) {
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                widget.onResize();
            }

        } );
    }

    public void setFocus(boolean hasFocus) {
        this.tabPanel.setFocus( hasFocus );
    }

    public boolean contains(WorkbenchPart workbenchPart) {
        return tabPanel.getWidgetIndex( workbenchPart ) >= 0;
    }

    public boolean remove(WorkbenchPart part) {
        final int indexOfTabToRemove = tabPanel.getWidgetIndex( part );
        final int indexOfSelectedTab = tabPanel.getSelectedIndex();
        final boolean removed = tabPanel.remove( part );

        if ( removed ) {

            if ( tabPanel.getWidgetCount() == 0 ) {
                PanelManager.getInstance().removeWorkbenchPanel( this );
            } else {
                if ( indexOfSelectedTab == indexOfTabToRemove ) {
                    tabPanel.selectTab( indexOfTabToRemove > 0 ? indexOfTabToRemove - 1 : 0 );
                }
            }
        }
        return removed;
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        setPixelSize( parent.getOffsetWidth(),
                      parent.getOffsetHeight() );
        super.onResize();
    }

}
