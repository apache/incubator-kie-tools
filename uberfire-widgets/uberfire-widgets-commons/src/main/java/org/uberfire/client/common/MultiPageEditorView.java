/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.common;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.common.tab.CustomTabPanel;

@Dependent
public class MultiPageEditorView
        extends Composite
        implements RequiresResize {

    final CustomTabPanel tabPanel = new CustomTabPanel();

    public MultiPageEditorView() {
        tabPanel.setWidth( "100%" );

        //Selecting a tab causes the previously selected tab to receive a Lost Focus event
        tabPanel.addBeforeSelectionHandler( new BeforeSelectionHandler<Integer>() {

            @Override
            public void onBeforeSelection( BeforeSelectionEvent<Integer> event ) {
                final Widget widget = tabPanel.getWidget( event.getItem() );
                ( (Page.PageView) widget ).onLostFocus();
            }
        } );

        //When a tab is selected ensure content is resized and set focus
        tabPanel.addSelectionHandler( new SelectionHandler<Integer>() {

            @Override
            public void onSelection( SelectionEvent<Integer> event ) {
                final Widget widget = tabPanel.getWidget( event.getSelectedItem() );
                scheduleResize( widget );
                ( (Page.PageView) widget ).onFocus();
            }
        } );

        initWidget( tabPanel );
    }

    protected void scheduleResize( final Widget widget ) {
        if ( widget instanceof RequiresResize ) {
            final RequiresResize requiresResize = (RequiresResize) widget;
            Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {

                @Override
                public void execute() {
                    requiresResize.onResize();
                }

            } );
        }
    }

    public void addPage( final Page page ) {
        tabPanel.add( page.getView(),
                      page.getLabel() );
        tabPanel.selectTab( 0 );
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if ( parent != null ) {
            final int width = parent.getOffsetWidth();
            final int height = parent.getOffsetHeight();
            setPixelSize( width,
                          height );
        }
        final Widget widget = getWidget();
        if ( widget instanceof RequiresResize ) {
            ( (RequiresResize) widget ).onResize();
        }
    }

}
