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
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.AbstractDecisionTableWidget;

/**
 * A Drag Handler for Conditions in the Configuration section of the Guided
 * Decision Table screen that acts as a Mediator between drag operations and the
 * Decision Table Widget
 */
public class ConditionDragHandler
        implements
        DragHandler {

    //Index of Action at the start of a drag operation
    private int startIndex = -1;

    //Index of Action at the end of a drag operation
    private int endIndex = -1;

    private VerticalPanel conditionsPanel;
    private Pattern52 pattern;
    private AbstractDecisionTableWidget dtableWidget;

    /**
     * Constructor to mediate drag operations between the Conditions
     * configuration section of the Guided Decision Table screen and the
     * Decision Table Widget
     * @param conditionsPanel
     * @param pattern
     * @param dtableWidget
     */
    public ConditionDragHandler( VerticalPanel conditionsPanel,
                                 Pattern52 pattern,
                                 AbstractDecisionTableWidget dtableWidget ) {
        this.conditionsPanel = conditionsPanel;
        this.pattern = pattern;
        this.dtableWidget = dtableWidget;
    }

    public void onDragStart( DragStartEvent event ) {
        startIndex = conditionsPanel.getWidgetIndex( event.getContext().draggable );
    }

    public void onDragEnd( DragEndEvent event ) {
        endIndex = conditionsPanel.getWidgetIndex( event.getContext().draggable );
        if ( endIndex == startIndex ) {
            return;
        }
        ConditionCol52 conditionBeingMoved = pattern.getChildColumns().get( startIndex );
        dtableWidget.moveCondition( pattern,
                                    conditionBeingMoved,
                                    endIndex );
    }

    public void onPreviewDragEnd( DragEndEvent event ) throws VetoDragException {
        //Do nothing
    }

    public void onPreviewDragStart( DragStartEvent event ) throws VetoDragException {
        //Do nothing
    }

}
