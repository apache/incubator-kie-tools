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
package org.uberfire.client.workbench;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.panels.WorkbenchTabLayoutPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
public class WorkbenchPanelView extends ResizeComposite
    implements
    WorkbenchPanel.View {

    public static final int               TAB_BAR_HEIGHT   = 32;

    private static final int              FOCUS_BAR_HEIGHT = 3;

    @Inject
    private WorkbenchDragAndDropManager   dndManager;

    private final WorkbenchTabLayoutPanel tabPanel;

    private WorkbenchPanel                presenter;

    public WorkbenchPanelView() {
        this.tabPanel = makeTabPanel();
        initWidget( this.tabPanel );
    }

    @Override
    public void init(final WorkbenchPanel presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        tabPanel.clear();
    }

    @Override
    public void addPart(WorkbenchPart part) {
        final IsWidget view = part.getPartView();
        tabPanel.add( view,
                      makeTabWidget( part ) );
        tabPanel.selectTab( view );
    }

    @Override
    public void selectPart(int index) {
        tabPanel.selectTab( index );
    }

    @Override
    public void removePart(int indexOfPartToRemove) {

        final int indexOfSelectedPart = tabPanel.getSelectedIndex();

        tabPanel.remove( indexOfPartToRemove );
        if ( tabPanel.getWidgetCount() > 0 ) {
            if ( indexOfSelectedPart == indexOfPartToRemove ) {
                tabPanel.selectTab( indexOfPartToRemove > 0 ? indexOfPartToRemove - 1 : 0 );
            }
        }
    }

    @Override
    public void setFocus(boolean hasFocus) {
        this.tabPanel.setFocus( hasFocus );
    }

    private WorkbenchTabLayoutPanel makeTabPanel() {
        final WorkbenchTabLayoutPanel tabPanel = new WorkbenchTabLayoutPanel( TAB_BAR_HEIGHT,
                                                                              FOCUS_BAR_HEIGHT,
                                                                              Unit.PX );

        //Selecting a tab causes the previously selected tab to receive a Lost Focus event
        tabPanel.addBeforeSelectionHandler( new BeforeSelectionHandler<Integer>() {

            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                final int index = tabPanel.getSelectedIndex();
                if ( index < 0 ) {
                    return;
                }
                presenter.onPartLostFocus( index );
            }
        } );

        //When a tab is selected ensure content is resized and set focus
        tabPanel.addSelectionHandler( new SelectionHandler<Integer>() {

            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                final int index = tabPanel.getSelectedIndex();
                final IsWidget w = tabPanel.getWidget( event.getSelectedItem() );
                if ( w instanceof RequiresResize ) {
                    scheduleResize( (RequiresResize) w );
                }
                presenter.onPartFocus( index );
            }

        } );
        return tabPanel;
    }

    private Widget makeTabWidget(final WorkbenchPart part) {
        final FlowPanel fp = new FlowPanel();
        final InlineLabel tabLabel = new InlineLabel( part.getDefinition().getTitle() );
        fp.add( tabLabel );

        //Clicking on the Tab takes focus
        fp.addDomHandler( new ClickHandler() {

                              @Override
                              public void onClick(ClickEvent event) {
                                  presenter.onPanelFocus();
                              }

                          },
                          ClickEvent.getType() );

        dndManager.makeDraggable( part.getPartView().asWidget(),
                                  tabLabel );

        final FocusPanel image = new FocusPanel();
        image.getElement().getStyle().setFloat( Style.Float.RIGHT );
        image.setStyleName( WorkbenchResources.INSTANCE.CSS().closeTabImage() );
        image.addClickHandler( new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                final int index = tabPanel.getWidgetIndex( part.getPartView() );
                presenter.onBeforePartClose( index );
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

    @Override
    public void onResize() {
        final Widget parent = getParent();
        setPixelSize( parent.getOffsetWidth(),
                      parent.getOffsetHeight() );
        super.onResize();
    }

}
