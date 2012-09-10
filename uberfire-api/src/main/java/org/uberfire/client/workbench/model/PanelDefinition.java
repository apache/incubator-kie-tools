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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.client.workbench.Position;

/**
 * 
 */
@Portable
public class PanelDefinition {

    private Integer                        height    = null;
    private Integer                        width     = null;

    private Integer                        minHeight = null;
    private Integer                        minWidth  = null;

    private boolean                        isRoot    = false;
    private Set<PartDefinition>            parts     = new HashSet<PartDefinition>();
    private Map<Position, PanelDefinition> children  = new HashMap<Position, PanelDefinition>();

    public PanelDefinition() {
        this( false );
    }

    public PanelDefinition(final boolean isRoot) {
        this.isRoot = isRoot;
    }

    public void addPart(final PartDefinition part) {
        part.setParentPanel( this );
        this.parts.add( part );
    }

    public Set<PartDefinition> getParts() {
        return parts;
    }

    public Map<Position, PanelDefinition> getChildren() {
        return children;
    }

    public PanelDefinition getChild(final Position position) {
        checkPosition( position );
        return children.get( position );
    }

    public void setChild(final Position position,
                         final PanelDefinition panel) {
        checkPosition( position );
        children.put( position,
                      panel );
    }

    public boolean isRoot() {
        return this.isRoot;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public final Integer getMinHeight() {
        return minHeight;
    }

    public final void setMinHeight(Integer minHeight) {
        this.minHeight = minHeight;
    }

    public final Integer getMinWidth() {
        return minWidth;
    }

    public final void setMinWidth(Integer minWidth) {
        this.minWidth = minWidth;
    }

    public void removePanel(final PanelDefinition panel) {
        if ( children.get( Position.NORTH ) != null ) {
            if ( children.get( Position.NORTH ).equals( panel ) ) {
                children.remove( Position.NORTH );
            } else {
                children.get( Position.NORTH ).removePanel( panel );
            }
        }
        if ( children.get( Position.SOUTH ) != null ) {
            if ( children.get( Position.SOUTH ).equals( panel ) ) {
                children.remove( Position.SOUTH );
            } else {
                children.get( Position.SOUTH ).removePanel( panel );
            }
        }
        if ( children.get( Position.EAST ) != null ) {
            if ( children.get( Position.EAST ).equals( panel ) ) {
                children.remove( Position.EAST );
            } else {
                children.get( Position.EAST ).removePanel( panel );
            }
        }
        if ( children.get( Position.WEST ) != null ) {
            if ( children.get( Position.WEST ).equals( panel ) ) {
                children.remove( Position.WEST );
            } else {
                children.get( Position.WEST ).removePanel( panel );
            }
        }
    }

    private void checkPosition(final Position position) {
        if ( position == Position.ROOT || position == Position.SELF || position == Position.NONE ) {
            throw new IllegalArgumentException( "Position must be NORTH, SOUTH, EAST or WEST" );
        }
    }

}
