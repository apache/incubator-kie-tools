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
package org.uberfire.client.workbench.model;

import java.util.List;
import java.util.Set;

import org.uberfire.client.workbench.Position;

/**
 * A Panel in the Workbench.
 */
public interface PanelDefinition {

    /**
     * Add a Part to the Panel
     * @param part The Part to add
     */
    public void addPart( final PartDefinition part );

    /**
     * Get the Parts contained in the Panel
     * @return The parts
     */
    public Set<PartDefinition> getParts();

    /**
     * Get all of this Panel's immediate child Panels (i.e. not recursive).
     * @return All children
     */
    public List<PanelDefinition> getChildren();

    /**
     * Insert a Panel as an immediate child at the given Position. If this
     * Panel already has a child at the specified position the existing Panel at
     * that position becomes a child of the Panel being added at the same
     * Position.
     * @param position The Position to add the child
     * @param panel The child Panel
     */
    public void insertChild( final Position position,
                             final PanelDefinition panel );

    /**
     * Append a Panel at the first empty child position. If this Panel already
     * has a child at the specified position the descendants are followed until
     * an empty position is found.
     * @param position The Position to add the child
     * @param panel The child Panel
     */
    public void appendChild( final Position position,
                             final PanelDefinition panel );

    /**
     * Get the Panel's immediate child Panel at the given Position
     * @param position The child Panel's Position
     * @return The child Panel or null, if a child does not exist at the given
     *         Position
     */
    public PanelDefinition getChild( final Position position );

    /**
     * Remove a child from the Panel
     * @param position
     */
    public void removeChild( final Position position );

    /**
     * Is this Panel the root of the Perspective definition
     * @return True if the Panel is the root
     */
    public boolean isRoot();

    /**
     * Get the height of the Panel in pixels
     * @return The height, or null if not set
     */
    public Integer getHeight();

    /**
     * Set the height of the Panel in pixels
     * @param height The height, or null if not set
     */
    public void setHeight( Integer height );

    /**
     * Get the width of the Panel in pixels
     * @return The width, or null if not set
     */
    public Integer getWidth();

    /**
     * Set the width of the Panel in pixels
     * @param width The width, or null if not set
     */
    public void setWidth( Integer width );

    /**
     * Get the minimum height of the Panel in pixels
     * @return The minimum height, or null if not set
     */
    public Integer getMinHeight();

    /**
     * Set the minimum height of the Panel in pixels
     * @param minHeight The minimum height, or null if not set
     */
    public void setMinHeight( Integer minHeight );

    /**
     * Get the minimum width of the Panel in pixels
     * @return The minimum width, or null if not set
     */
    public Integer getMinWidth();

    /**
     * Set the minimum width of the Panel in pixels
     * @param minWidth The width, or null if not set
     */
    public void setMinWidth( Integer minWidth );

    /**
     * Get the Position of the Panel relate to it's Parent
     * @return The Position of the Panel
     */
    public Position getPosition();

    /**
     * Set the Position of the Panel relative to it's parent.
     * @param position The Position of the Panel relative to it's parent
     */
    public void setPosition( Position position );

    /**
     * Has the Panel been collapsed to a minimal size
     * @return true If minimized
     */
    public boolean isMinimized();

    /**
     * Has the Panel been expanded to a maximum size
     * @return true If maximized
     */
    public boolean isMaximized();

}
