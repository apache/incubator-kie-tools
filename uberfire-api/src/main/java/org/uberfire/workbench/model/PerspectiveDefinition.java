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

/**
 * Meta-data defining a Perspective. A Perspective is a set of Panels and Parts
 * arranged within the Workbench. One Workbench contains multiple Panels, each
 * Panel contains multiple Parts. Each Part contains one PlaceRequest.
 */
public interface PerspectiveDefinition {

    /**
     * Get whether the Perspective is transient, i.e. will not be persisted.
     * @return True if the Perspective is transient and is not to be persisted.
     */
    public boolean isTransient();

    /**
     * Set whether the Perspective is transient, i.e. will not be persisted.
     * @param isTransient True if the Perspective is not to be persisted.
     */
    public void setTransient( final boolean isTransient );

    /**
     * Get the name of the Perspective.
     * @return The name of the Perspective.
     */
    public String getName();

    /**
     * Set the name of the Perspective.
     * @param name The name of the Perspective.
     */
    public void setName( final String name );

    /**
     * Get the root Panel for this Perspective. The root Panel contains all
     * child Panels. A Perspective is based on a single root Panel.
     * @return The root Panel.
     */
    public PanelDefinition getRoot();

    void setContextDefinition( final ContextDefinition contextDefinition );

    ContextDefinition getContextDefinition();

    ContextDisplayMode getContextDisplayMode();

    void setContextDisplayMode( final ContextDisplayMode contextDisplayMode );
}
