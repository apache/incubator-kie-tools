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

import java.util.List;
import java.util.Set;

/**
 * Describes a physical region within a Workbench Perspective. Panels have a set physical size that they occupy, which
 * is divided up between any panel decorations (a tab bar or dropdown list is common), one or more Parts (generally
 * Editors or Screens), one of which can be visible at a time, and also child Panel Definitions, all of which are
 * visible simultaneously.
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
     * Returns this panel's immediate child panels.
     * 
     * @return a snapshot of the current child list. The list is not modifiable, and will not change as panels are added
     *         and removed from this panel. The returned list is never null.
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
     * Append a Panel at the first empty child position. If this Panel already
     * has a child at the specified position the descendants are followed until
     * an empty position is found.
     * @param panel The child Panel
     */
    public void appendChild(
                            final PanelDefinition panel );

    /**
     * Return the parent panel, or null if {@link #isRoot()}} returns true.
     *
     * @return  a parent panel definition or null if at the top of the hierarchy
     */
    public PanelDefinition getParent();

    /**
     * Get the Panel's immediate child Panel at the given Position
     * @param position The child Panel's Position
     * @return The child Panel or null, if a child does not exist at the given
     * Position
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
     * Get the type of the Panel
     * @return The panel type
     */
    public PanelType getPanelType();

    public PanelType getDefaultChildPanelType();

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
     * Has the Panel been expanded to a maximum size
     * @return true If maximized
     */
    public boolean isMaximized();

    void setContextDefinition( final ContextDefinition contextDefinition );

    ContextDefinition getContextDefinition();

    ContextDisplayMode getContextDisplayMode();

    void setContextDisplayMode( final ContextDisplayMode contextDisplayMode );

}
