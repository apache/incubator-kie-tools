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
package org.uberfire.client.workbench.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;

/**
 * Default implementation of PanelDefinition
 */
@Portable
public class PanelDefinitionImpl
        implements
        PanelDefinition {

    private Integer height = null;
    private Integer width = null;

    private Integer minHeight = null;
    private Integer minWidth = null;

    private boolean isRoot = false;
    private Set<PartDefinition> parts = new HashSet<PartDefinition>();

    //Ideally this should be a Set but the order of insertion is important
    private List<PanelDefinition> children = new ArrayList<PanelDefinition>();

    private Position position;

    public PanelDefinitionImpl() {
    }

    public PanelDefinitionImpl( final boolean isRoot ) {
        this.isRoot = isRoot;
        this.position = Position.ROOT;
    }

    public void addPart( final PartDefinition part ) {
        part.setParentPanel( this );
        this.parts.add( part );
    }

    @Override
    public Set<PartDefinition> getParts() {
        return parts;
    }

    @Override
    public List<PanelDefinition> getChildren() {
        return Collections.unmodifiableList( children );
    }

    @Override
    public void insertChild( final Position position,
                             final PanelDefinition panel ) {
        if ( panel == null ) {
            return;
        }
        if ( children.contains( panel ) ) {
            return;
        }
        checkPosition( position );
        panel.setPosition( position );
        final PanelDefinition existingChild = getChild( position );
        if ( existingChild == null ) {
            children.add( panel );
        } else {
            removeChild( position );
            children.add( panel );
            panel.insertChild( position,
                               existingChild );
        }
    }

    @Override
    public void appendChild( final Position position,
                             final PanelDefinition panel ) {
        if ( panel == null ) {
            return;
        }
        if ( children.contains( panel ) ) {
            return;
        }
        checkPosition( position );
        panel.setPosition( position );
        final PanelDefinition existingChild = getChild( position );
        if ( existingChild == null ) {
            children.add( panel );
        } else {
            existingChild.appendChild( position,
                                       panel );
        }
    }

    @Override
    public PanelDefinition getChild( final Position position ) {
        for ( PanelDefinition child : children ) {
            if ( child.getPosition() == position ) {
                return child;
            }
        }
        return null;
    }

    @Override
    public void removeChild( final Position position ) {
        Iterator<PanelDefinition> itr = children.iterator();
        while ( itr.hasNext() ) {
            final PanelDefinition child = itr.next();
            if ( child.getPosition() == position ) {
                itr.remove();
            }
        }
    }

    @Override
    public boolean isRoot() {
        return this.isRoot;
    }

    @Override
    public Integer getHeight() {
        return height;
    }

    @Override
    public void setHeight( Integer height ) {
        this.height = height;
    }

    @Override
    public Integer getWidth() {
        return width;
    }

    @Override
    public void setWidth( Integer width ) {
        this.width = width;
    }

    @Override
    public final Integer getMinHeight() {
        return minHeight;
    }

    @Override
    public final void setMinHeight( Integer minHeight ) {
        this.minHeight = minHeight;
    }

    @Override
    public final Integer getMinWidth() {
        return minWidth;
    }

    @Override
    public final void setMinWidth( Integer minWidth ) {
        this.minWidth = minWidth;
    }

    @Override
    public final Position getPosition() {
        return position;
    }

    @Override
    public void setPosition( final Position position ) {
        this.position = position;
    }

    @Override
    public boolean isMinimized() {
        for(PartDefinition part : getParts()) {
            if(!part.isMinimized()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

    private void checkPosition( final Position position ) {
        if ( position == Position.ROOT || position == Position.SELF || position == Position.NONE ) {
            throw new IllegalArgumentException( "Position must be NORTH, SOUTH, EAST or WEST" );
        }
    }

}
