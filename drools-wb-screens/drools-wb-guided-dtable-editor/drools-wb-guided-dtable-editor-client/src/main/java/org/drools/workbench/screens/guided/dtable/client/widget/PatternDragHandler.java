/*
 * Copyright 2011 JBoss Inc
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
package org.drools.workbench.screens.guided.dtable.client.widget;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.AbstractDecisionTableWidget;

/**
 * A Drag Handler for Patterns in the Configuration section of the Guided
 * Decision Table screen that acts as a Mediator between drag operations and the
 * Decision Table Widget
 */
public class PatternDragHandler
        implements
        DragHandler {

    //Index of pattern at the start of a drag operation
    private int startIndex = -1;

    //Index of pattern at the end of a drag operation
    private int endIndex = -1;

    private VerticalPanel patternsPanel;
    private GuidedDecisionTable52 dtableModel;
    private AbstractDecisionTableWidget dtableWidget;

    /**
     * Constructor to mediate drag operations between the Pattern configuration
     * section of the Guided Decision Table screen and the Decision Table Widget
     * @param patternsPanel
     * @param dtableModel
     * @param dtableWidget
     */
    public PatternDragHandler( VerticalPanel patternsPanel,
                               GuidedDecisionTable52 dtableModel,
                               AbstractDecisionTableWidget dtableWidget ) {
        this.patternsPanel = patternsPanel;
        this.dtableModel = dtableModel;
        this.dtableWidget = dtableWidget;
    }

    public void onDragStart( DragStartEvent event ) {
        startIndex = patternsPanel.getWidgetIndex( event.getContext().draggable );
    }

    public void onDragEnd( DragEndEvent event ) {
        endIndex = patternsPanel.getWidgetIndex( event.getContext().draggable );
        if ( endIndex == startIndex ) {
            return;
        }
        CompositeColumn<?> column = dtableModel.getConditions().get( startIndex );
        dtableWidget.movePattern( column,
                                  endIndex );
    }

    public void onPreviewDragEnd( DragEndEvent event ) throws VetoDragException {
        //Do nothing
    }

    public void onPreviewDragStart( DragStartEvent event ) throws VetoDragException {
        //Do nothing
    }

}
