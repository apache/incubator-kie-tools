/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.workbench.model.impl;

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.workbench.model.ContextDisplayMode.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.debug.Debug;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.ContextDefinition;
import org.uberfire.workbench.model.ContextDisplayMode;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

/**
 * Default implementation of PanelDefinition
 */
@Portable
public class PanelDefinitionImpl implements PanelDefinition {

    private Integer height = null;
    private Integer width = null;

    private Integer minHeight = null;
    private Integer minWidth = null;

    private final Set<PartDefinition> parts = new LinkedHashSet<PartDefinition>();

    //Ideally this should be a Set but the order of insertion is important
    private final List<PanelDefinition> children = new ArrayList<PanelDefinition>();

    private String elementId;
    private Position position;
    private String panelType;
    private boolean isRoot;
    private ContextDefinition contextDefinition;
    private ContextDisplayMode contextDisplayMode = SHOW;
    private PanelDefinition parent = null;

    public PanelDefinitionImpl() {
        this( "org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter" );
    }

    public PanelDefinitionImpl( final String type ) {
        this.panelType = type;
    }

    public void setParent( PanelDefinition parent ) {
        if ( this.parent != null && parent != null ) {
            throw new IllegalStateException( "Can't change parent: this panel still belongs to " + this.parent );
        }
        this.parent = parent;
    }

    @Override
    public PanelDefinition getParent() {
        return parent;
    }

    @Override
    public void setElementId( String id ) {
        elementId = id;
    }

    @Override
    public String getElementId() {
        return elementId;
    }

    @Override
    public void addPart( final PartDefinition part ) {
        if ( part.getParentPanel() != null ) {
            part.getParentPanel().removePart( part );
        }
        this.parts.add( part );
        part.setParentPanel( this );
    }

    @Override
    public PartDefinition addPart( String partSpec ) {
        PartDefinition pd = new PartDefinitionImpl( DefaultPlaceRequest.parse( partSpec ) );
        addPart( pd );
        return pd;
    }

    @Override
    public boolean removePart( PartDefinition part ) {
        if ( this.parts.remove( part ) ) {
            part.setParentPanel( null );
            return true;
        }
        return false;
    }

    @Override
    public Set<PartDefinition> getParts() {
        return parts;
    }

    @Override
    public List<PanelDefinition> getChildren() {
        return Collections.unmodifiableList( new ArrayList<PanelDefinition>( children ) );
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

        // parent wiring
        ((PanelDefinitionImpl)panel).setParent(this);

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

            // parent wiring
            ((PanelDefinitionImpl)panel).setParent(this);

            children.add( panel );
        } else {
            existingChild.appendChild( position,
                                       panel );
        }
    }

    @Override
    public void appendChild( final PanelDefinition panel ) {

        if ( panel == null ) {
            return;
        }
        if ( children.contains( panel ) ) {
            return;
        }

        // parent wiring
        ((PanelDefinitionImpl)panel).setParent(this);

        children.add( panel );
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
                // parent wiring
                ((PanelDefinitionImpl)child).setParent(null);

                itr.remove();
            }
        }
    }

    @Override
    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot( boolean isRoot ) {
        this.isRoot = isRoot;
    }

    @Override
    public String getPanelType() {
        return panelType;
    }

    @Override
    public void setPanelType( String fqcn ) {
        this.panelType = checkNotNull( "fqcn", fqcn );
    }

    @Override
    public Integer getHeight() {
        return height;
    }

    @Override
    public void setHeight( Integer height ) {
        if ( height != null ) {
            this.height = height;
        }
    }

    @Override
    public Integer getWidth() {
        return width;
    }

    @Override
    public void setWidth( Integer width ) {
        if ( width != null ) {
            this.width = width;
        }
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
    public boolean isMaximized() {
        return false;
    }

    @Override
    public void setContextDefinition( final ContextDefinition contextDefinition ) {
        this.contextDefinition = contextDefinition;
    }

    @Override
    public ContextDefinition getContextDefinition() {
        return contextDefinition;
    }

    @Override
    public ContextDisplayMode getContextDisplayMode() {
        return contextDisplayMode;
    }

    @Override
    public void setContextDisplayMode( final ContextDisplayMode contextDisplayMode ) {
        this.contextDisplayMode = contextDisplayMode;
    }

    private void checkPosition( final Position position ) {
        if ( position == CompassPosition.ROOT || position == CompassPosition.SELF || position == CompassPosition.NONE ) {
            throw new IllegalArgumentException( "Position must be NORTH, SOUTH, EAST or WEST" );
        }
    }

    @Override
    public String toString() {
        String fullName = getClass().getName();
        String simpleName = fullName.substring( fullName.lastIndexOf( '.' ) + 1 );
        return simpleName + " [id=" + elementId + ", parts=" + parts + ", children=" + children + ", panelType=" + panelType
                + ", contextDefinition=" + contextDefinition + ", contextDisplayMode=" + contextDisplayMode + "]";
    }

}
