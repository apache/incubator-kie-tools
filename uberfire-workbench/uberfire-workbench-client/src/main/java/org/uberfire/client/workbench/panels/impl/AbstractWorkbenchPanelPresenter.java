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
package org.uberfire.client.workbench.panels.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;

import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Basic implementation of common functionality in a panel presenter.
 * <p>
 * At the least, concrete subclasses have to implement {@link #asPresenterType()} to return {@code this}. Subclasses may
 * override other methods as necessary to customize behaviour for use with specialized layouts and view implementations.
 */
public abstract class AbstractWorkbenchPanelPresenter<P extends AbstractWorkbenchPanelPresenter<P>> implements WorkbenchPanelPresenter {

    private WorkbenchPanelPresenter parent;
    private final WorkbenchPanelView<P> view;
    protected final PerspectiveManager perspectiveManager;
    private PanelDefinition definition;
    private final Event<MaximizePlaceEvent> maximizePanelEvent;
    private final Map<Position, WorkbenchPanelPresenter> childPanels = new HashMap<Position, WorkbenchPanelPresenter>();

    public AbstractWorkbenchPanelPresenter( final WorkbenchPanelView<P> view,
                                            final PerspectiveManager perspectiveManager,
                                            final Event<MaximizePlaceEvent> maximizePanelEvent ) {
        this.view = view;
        this.perspectiveManager = perspectiveManager;
        this.maximizePanelEvent = maximizePanelEvent;
    }

    /**
     * Returns a reference to {@code this}. Helps superclass code work around generic type checking problems.
     */
    protected abstract P asPresenterType();

    @PostConstruct
    void init() {
        getPanelView().init( this.asPresenterType() );
    }

    @Override
    public WorkbenchPanelPresenter getParent() {
        return parent;
    }

    @Override
    public void setParent( WorkbenchPanelPresenter parent ) {
        this.parent = parent;
    }

    @Override
    public PanelDefinition getDefinition() {
        return definition;
    }

    /**
     * Most panel types are their own default child type, so this implementation returns {@code getClass().getName()}.
     * Subclasses can override if they prefer a different child type.
     */
    @Override
    public String getDefaultChildType() {
        return getClass().getName();
    }

    @Override
    public void setDefinition( final PanelDefinition definition ) {
        this.definition = definition;
        view.setElementId( definition.getElementId() );
    }

    /**
     * Calls {@link #addPart(org.uberfire.client.workbench.part.WorkbenchPartPresenter.View, String)}. Subclasses can
     * take advantage of this by only overriding the 2-arg version.
     */
    @Override
    public void addPart( final WorkbenchPartPresenter part ) {
        addPart( part, null );
    }

    /**
     * Adds the given part to the view returned by {@link #getPanelView()}, ignoring the given {@code contextId}.
     * Subclasses that care about context id's will override this method.
     */
    @Override
    public void addPart( final WorkbenchPartPresenter part,
                         final String contextId ) {
        definition.addPart( part.getDefinition() );
        getPanelView().addPart( part.getPartView() );
    }

    @Override
    public boolean removePart( final PartDefinition part ) {
        view.removePart( part );
        return definition.removePart( part );
    }

    @Override
    public void addPanel( final WorkbenchPanelPresenter child,
                          final Position position ) {
        getPanelView().addPanel( child.getDefinition(), child.getPanelView(), position );
        definition.insertChild( position, child.getDefinition() );
        childPanels.put( position, child );
        child.setParent( this );
    }

    @Override
    public boolean removePanel( WorkbenchPanelPresenter child ) {
        Position position = positionOf( child );
        if ( position == null ) {
            return false;
        }
        getPanelView().removePanel( child.getPanelView() );
        definition.removeChild( position );
        childPanels.remove( position );
        child.setParent( null );
        return true;
    }

    @Override
    public Map<Position, WorkbenchPanelPresenter> getPanels() {
        return Collections.unmodifiableMap( childPanels );
    }

    private Position positionOf( WorkbenchPanelPresenter child ) {
        for ( Map.Entry<Position, WorkbenchPanelPresenter> entry : childPanels.entrySet() ) {
            if ( child == entry.getValue() ) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecorator ) {
        getPanelView().changeTitle( part, title, titleDecorator );
    }

    @Override
    public void setFocus( final boolean hasFocus ) {
        view.setFocus( hasFocus );
    }

    @Override
    public boolean selectPart( final PartDefinition part ) {
        if ( !contains( part ) ) {
            return false;
        }
        view.selectPart( part );
        return true;
    }

    private boolean contains( final PartDefinition part ) {
        return definition.getParts().contains( part );
    }

    @Override
    public void maximize() {
        if ( !getDefinition().isRoot() ) {
            for ( final PartDefinition part : getDefinition().getParts() ) {
                maximizePanelEvent.fire( new MaximizePlaceEvent( part.getPlace() ) );
            }
        }
    }

    @Override
    public WorkbenchPanelView<P> getPanelView() {
        return view;
    }

    @Override
    public void onResize( final int width,
                          final int height ) {
        getDefinition().setWidth( width == 0 ? null : width );
        getDefinition().setHeight( height == 0 ? null : height );
    }

}
