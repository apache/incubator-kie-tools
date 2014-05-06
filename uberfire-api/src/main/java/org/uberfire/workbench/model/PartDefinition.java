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
package org.uberfire.workbench.model;

import org.uberfire.mvp.PlaceRequest;

/**
 * A Part in the Workbench. Parts are added to Panels. See the documentation of WorkbenchPanelPresenter for details.
 */
public interface PartDefinition {

    /**
     * Get the PlaceRequest that this Part will contain.
     * @return the place
     */
    PlaceRequest getPlace();

    /**
     * Set the PlaceRequest that this Part will contain.
     * @param place the place to set
     */
    void setPlace( final PlaceRequest place );

    /**
     * Get the parent Panel containing this Part.
     * @return the parentPanel
     */
    PanelDefinition getParentPanel();

    /**
     * Set the parent Panel containing this Part.
     * @param parentPanel the parentPanel to set
     */
    void setParentPanel( final PanelDefinition parentPanel );

    /**
     * Set whether this Part is minimized or not.
     * @param isMinimized true if Part is minimized
     */
    void setMinimized( final boolean isMinimized );

    /**
     * Has the Part been collapsed to a minimal size.
     * @return true If minimized
     */
    boolean isMinimized();

    /**
     * Has the Part been expanded to a maximum size.
     * @return true If maximized
     */
    boolean isMaximized();

    void setContextDefinition( final ContextDefinition contextDefinition );

    ContextDefinition getContextDefinition();

    ContextDisplayMode getContextDisplayMode();

    void setContextDisplayMode( final ContextDisplayMode contextDisplayMode );
}
