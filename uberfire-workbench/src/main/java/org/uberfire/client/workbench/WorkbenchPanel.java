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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.events.WorkbenchPanelOnFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartBeforeCloseEvent;
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

        WorkbenchPanel getPresenter();

        void clear();

        void addPart(String title,
                     WorkbenchPart.View view);

        void addPanel(PanelDefinition panel,
                      WorkbenchPanel.View view,
                      Position position);

        void selectPart(int index);

        void removePart(int index);

        void removePanel();

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

    private PanelDefinition                      definition = new PanelDefinition();

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        view.init( this );
    }

    public PanelDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(final PanelDefinition definition) {
        this.definition = definition;
    }

    public void addPart(final String title,
                        final PartDefinition part,
                        final WorkbenchPart.View view) {
        if ( !definition.getParts().contains( part ) ) {
            definition.addPart( part );
        }
        getPanelView().addPart( title,
                                view );
    }

    public void addPanel(final PanelDefinition panel,
                         final WorkbenchPanel.View view,
                         final Position position) {
        final List<PanelDefinition> panels = definition.getChildren( position );
        if ( panels == null ) {
            throw new IllegalArgumentException( "Unhandled Position. Expect subsequent errors." );
        }
        if ( !panels.contains( panel ) ) {
            panels.add( panel );
        }
        getPanelView().addPanel( panel,
                                 view,
                                 position );
    }

    public void clear() {
        view.clear();
    }

    public void removePart(final PartDefinition part) {
        if ( !contains( part ) ) {
            return;
        }
        final int indexOfPartToRemove = definition.getParts().indexOf( part );
        definition.getParts().remove( indexOfPartToRemove );
        view.removePart( indexOfPartToRemove );
    }

    public void removePanel() {
        view.removePanel();
    }

    public void setFocus(final boolean hasFocus) {
        view.setFocus( hasFocus );
    }

    public void selectPart(final PartDefinition part) {
        if ( !contains( part ) ) {
            return;
        }
        final int indexOfPartToSelect = definition.getParts().indexOf( part );
        view.selectPart( indexOfPartToSelect );
    }

    private boolean contains(final PartDefinition part) {
        return definition.getParts().contains( part );
    }

    public void onPartFocus(final int index) {
        final PartDefinition definition = getDefinition().getParts().get( index );
        workbenchPartOnFocusEvent.fire( new WorkbenchPartOnFocusEvent( definition ) );
    }

    public void onPartLostFocus(final int index) {
        final PartDefinition definition = getDefinition().getParts().get( index );
        workbenchPartLostFocusEvent.fire( new WorkbenchPartLostFocusEvent( definition ) );
    }

    public void onPanelFocus() {
        workbenchPanelOnFocusEvent.fire( new WorkbenchPanelOnFocusEvent( getDefinition() ) );
    }

    public void onBeforePartClose(final int index) {
        final PartDefinition definition = getDefinition().getParts().get( index );
        workbenchPartBeforeCloseEvent.fire( new WorkbenchPartBeforeCloseEvent( definition ) );
    }

    public View getPanelView() {
        return view;
    }

}
