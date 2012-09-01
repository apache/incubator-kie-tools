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
package org.uberfire.client.workbench;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPanelOnFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartBeforeCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartDroppedEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartLostFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;

import com.google.gwt.user.client.ui.RequiresResize;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
public class WorkbenchPanel {

    public interface View
        extends
        UberView<WorkbenchPanel>,
        RequiresResize {

        void clear();

        void addPart(WorkbenchPart part);

        void addPanel(WorkbenchPanel part,
                      Position position);

        void selectPart(int index);

        void removePart(int index);

        void setFocus(boolean hasFocus);

    }

    @Inject
    private View                                 view;

    @Inject
    private Event<WorkbenchPartBeforeCloseEvent> workbenchPartBeforeCloseEvent;

    @Inject
    private Event<WorkbenchPartOnFocusEvent>     workbenchPartOnFocusEvent;

    @Inject
    private Event<WorkbenchPartLostFocusEvent>   workbenchPartLostFocusEvent;

    @Inject
    private Event<WorkbenchPanelOnFocusEvent>    workbenchPanelOnFocusEvent;

    private boolean                              isRoot                   = false;

    private PanelDefinition                      definition               = new PanelDefinition();

    private Map<PartDefinition, WorkbenchPart>   mapDefinitionToPresenter = new HashMap<PartDefinition, WorkbenchPart>();

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        view.init( this );
    }

    public boolean isRoot() {
        return this.isRoot;
    }

    public void setRoot(final boolean isRoot) {
        this.isRoot = isRoot;
    }

    public PanelDefinition getDefinition() {
        return definition;
    }

    public void addPart(final WorkbenchPart part) {
        definition.addPart( part.getDefinition() );
        mapDefinitionToPresenter.put( part.getDefinition(),
                                      part );
        view.addPart( part );
    }

    public void addPanel(final WorkbenchPanel panel,
                         final Position position) {
        final List<PanelDefinition> panels = definition.getChildren( position );
        if ( panels == null ) {
            throw new IllegalArgumentException( "Unhandled Position. Expect subsequent errors." );
        }
        panels.add( panel.getDefinition() );
        view.addPanel( panel,
                       position );
    }

    public void clear() {
        view.clear();
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPanelOnFocus(@Observes WorkbenchPanelOnFocusEvent event) {
        final WorkbenchPanel panel = event.getWorkbenchPanel();
        this.view.setFocus( panel == this );
    }

    @SuppressWarnings("unused")
    private void onSelectWorkbenchPartEvent(@Observes SelectWorkbenchPartEvent event) {
        final WorkbenchPart part = event.getWorkbenchPart();
        if ( contains( part ) ) {
            view.selectPart( definition.getParts().indexOf( part.getDefinition() ) );
            workbenchPanelOnFocusEvent.fire( new WorkbenchPanelOnFocusEvent( WorkbenchPanel.this ) );
        }
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartClosedEvent(@Observes WorkbenchPartCloseEvent event) {
        final WorkbenchPart part = event.getWorkbenchPart();
        remove( part );
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartDroppedEvent(@Observes WorkbenchPartDroppedEvent event) {
        final WorkbenchPart part = event.getWorkbenchPart();
        remove( part );
    }

    private void remove(final WorkbenchPart part) {
        if ( !contains( part ) ) {
            return;
        }
        final int indexOfPartToRemove = definition.getParts().indexOf( part.getDefinition() );
        mapDefinitionToPresenter.remove( definition.getParts().get( indexOfPartToRemove ) );
        definition.getParts().remove( indexOfPartToRemove );
        view.removePart( indexOfPartToRemove );
    }

    private boolean contains(WorkbenchPart part) {
        return definition.getParts().contains( part.getDefinition() );
    }

    public void onPartFocus(final int index) {
        final WorkbenchPart part = mapDefinitionToPresenter.get( definition.getParts().get( index ) );
        workbenchPartOnFocusEvent.fire( new WorkbenchPartOnFocusEvent( part ) );
    }

    public void onPartLostFocus(final int index) {
        final WorkbenchPart part = mapDefinitionToPresenter.get( definition.getParts().get( index ) );
        workbenchPartLostFocusEvent.fire( new WorkbenchPartLostFocusEvent( part ) );
    }

    public void onPanelFocus() {
        workbenchPanelOnFocusEvent.fire( new WorkbenchPanelOnFocusEvent( WorkbenchPanel.this ) );
    }

    public void onBeforePartClose(final int index) {
        final WorkbenchPart part = mapDefinitionToPresenter.get( definition.getParts().get( index ) );
        workbenchPartBeforeCloseEvent.fire( new WorkbenchPartBeforeCloseEvent( part ) );
    }

    public View getPanelView() {
        return view;
    }

}
