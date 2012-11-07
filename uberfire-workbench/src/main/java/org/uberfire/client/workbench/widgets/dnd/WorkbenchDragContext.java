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
package org.uberfire.client.workbench.widgets.dnd;

import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * The context of a drag and drop operation within the Workbench.
 */
public class WorkbenchDragContext {

    private final PlaceRequest place;
    private final PartDefinition sourcePart;
    private final PanelDefinition sourcePanel;
    private final IsWidget titleWidget;
    private final IsWidget widget;

    private Integer height;
    private Integer width;
    private Integer minHeight;
    private Integer minWidth;

    public WorkbenchDragContext( final PlaceRequest place,
                                 final PartDefinition sourcePart,
                                 final PanelDefinition sourcePanel,
                                 final IsWidget titleWidget,
                                 final IsWidget widget,
                                 final Integer height,
                                 final Integer width,
                                 final Integer minHeight,
                                 final Integer minWidth ) {
        this.place = place;
        this.sourcePart = sourcePart;
        this.sourcePanel = sourcePanel;
        this.titleWidget = titleWidget;
        this.widget = widget;
        this.height = height;
        this.width = width;
        this.minHeight = minHeight;
        this.minWidth = minWidth;
    }

    /**
     * @return the place
     */
    public PlaceRequest getPlace() {
        return this.place;
    }

    /**
     * @return the sourcePart
     */
    public PartDefinition getSourcePart() {
        return sourcePart;
    }

    /**
     * @return the sourcePanel
     */
    public PanelDefinition getSourcePanel() {
        return sourcePanel;
    }

    /**
     * @return the title widget
     */
    public IsWidget getTitleWidget() {
        return titleWidget;
    }

    public IsWidget getWidget() {
        return widget;
    }

    public final Integer getHeight() {
        return height;
    }

    public final Integer getWidth() {
        return width;
    }

    public final Integer getMinHeight() {
        return minHeight;
    }

    public final Integer getMinWidth() {
        return minWidth;
    }

}